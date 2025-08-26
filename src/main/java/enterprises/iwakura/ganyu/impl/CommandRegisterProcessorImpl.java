package enterprises.iwakura.ganyu.impl;

import enterprises.iwakura.ganyu.*;
import enterprises.iwakura.ganyu.annotation.*;
import enterprises.iwakura.ganyu.exception.CommandNotAnnotatedException;
import enterprises.iwakura.ganyu.exception.InvalidCommandMethodException;
import enterprises.iwakura.ganyu.exception.MultipleDefaultCommandMethodsException;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Default implementation of {@link CommandRegisterProcessor}.
 */
public class CommandRegisterProcessorImpl implements CommandRegisterProcessor {

    @Override
    public List<RegisteredCommand> process(Ganyu ganyu, GanyuCommand command) {
        RegisteredCommand mainCommand = new RegisteredCommand(command);
        List<RegisteredCommand> commands = new ArrayList<>(Collections.singletonList(mainCommand));

        List<Method> defaultCommandMethods = listMethodsWithAnnotations(command, DefaultCommand.class);
        List<Method> standaloneCommandMethods = listMethodsWithAnnotations(command, Command.class);
        List<Method> subCommandMethods = listMethodsWithAnnotations(command, SubCommand.class);
        Optional<Method> preCommandMethod = listMethodsWithAnnotations(command, PreCommand.class).stream().findFirst();
        Optional<Method> postCommandMethod = listMethodsWithAnnotations(command, PostCommand.class).stream().findFirst();
        Optional<Method> exceptionHandlerMethod = listMethodsWithAnnotations(command, ExceptionHandler.class).stream().findFirst();

        validateParameters(command, preCommandMethod, CommandInvocationContext.class);
        validateParameters(command, postCommandMethod, CommandInvocationContext.class);
        validateParameters(command, exceptionHandlerMethod, CommandInvocationContext.class, Throwable.class);

        Optional<String> classCommandName = readValue(command, Command.class);
        Optional<String> classDescription = readValue(command, Description.class);
        Optional<String> classSyntax = readValue(command, Syntax.class);

        populateAdditionalMethods(mainCommand, preCommandMethod, postCommandMethod, exceptionHandlerMethod);

        if (defaultCommandMethods.isEmpty()) {
            // Read class annotations
            mainCommand.setName(classCommandName.orElseThrow(() -> new CommandNotAnnotatedException(command, Command.class)));
            mainCommand.setDescription(classDescription.orElse(null));
            mainCommand.setSyntax(classSyntax.orElse(null));
        } else if (defaultCommandMethods.size() == 1) {
            // Read default command method annotations
            final Method defaultCommandMethod = defaultCommandMethods.get(0);
            parseMethod(mainCommand, defaultCommandMethod, classCommandName, classDescription, classSyntax);
        } else {
            throw new MultipleDefaultCommandMethodsException(command, defaultCommandMethods);
        }

        standaloneCommandMethods.forEach(method -> {
            final RegisteredCommand standaloneCommand = new RegisteredCommand(command, method);
            parseMethod(standaloneCommand, method, classCommandName, classDescription, classSyntax);
            populateAdditionalMethods(standaloneCommand, preCommandMethod, postCommandMethod, exceptionHandlerMethod);
            commands.add(standaloneCommand);
        });

        subCommandMethods.forEach(method -> {
            final RegisteredCommand subCommand = new RegisteredCommand(command, method);
            parseMethod(subCommand, method, Optional.empty(), Optional.empty(), Optional.empty());
            populateAdditionalMethods(subCommand, preCommandMethod, postCommandMethod, exceptionHandlerMethod);
            mainCommand.addSubCommand(subCommand);
        });

        for (RegisteredCommand registeredCommand : commands) {
            List<RegisteredCommand> subCommands = registeredCommand.getSubCommands();
            registeredCommand.setFullyQualifiedName(registeredCommand.getName());

            for (RegisteredCommand subCommand : subCommands) {
                subCommand.setFullyQualifiedName(String.format("%s %s", registeredCommand.getName(), subCommand.getName()));
            }
        }

        return commands;
    }

    private void populateAdditionalMethods(RegisteredCommand mainCommand, Optional<Method> preCommandMethod, Optional<Method> postCommandMethod, Optional<Method> exceptionHandlerMethod) {
        preCommandMethod.ifPresent(mainCommand::setPreCommandMethod);
        postCommandMethod.ifPresent(mainCommand::setPostCommandMethod);
        exceptionHandlerMethod.ifPresent(mainCommand::setExceptionHandlerMethod);
    }

    private void validateParameters(GanyuCommand command, Optional<Method> optionalMethod, @NonNull Class<?>... classParameters) {
        if (!optionalMethod.isPresent()) {
            return;
        }

        Method method = optionalMethod.get();
        Parameter[] parameters = method.getParameters();

        if (parameters.length != classParameters.length) {
            throw new InvalidCommandMethodException(command, method, classParameters);
        }

        for (int i = 0; i < parameters.length; i++) {
            if (!classParameters[i].isAssignableFrom(parameters[i].getType())) {
                throw new InvalidCommandMethodException(command, method, classParameters);
            }
        }
    }

    protected void parseMethod(RegisteredCommand registeredCommand, Method method, Optional<String> defaultCommandName, Optional<String> defaultDescription, Optional<String> defaultSyntax) {
        registeredCommand.setMethod(method);
        registeredCommand.setName(
            readValue(method, Command.class).orElseGet(() -> readValue(method, SubCommand.class).orElseGet(() -> defaultCommandName.orElseThrow(() -> new CommandNotAnnotatedException(registeredCommand.getGanyuCommand(), method, Command.class)))));
        registeredCommand.setDescription(readValue(method, Description.class).orElseGet(() -> defaultDescription.orElse(null)));
        registeredCommand.setSyntax(readValue(method, Syntax.class).orElseGet(() -> defaultSyntax.orElse(null)));
        registeredCommand.setNamedArgumentHandler(method.isAnnotationPresent(NamedArgumentHandler.class));

        final Parameter[] parameters = method.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];

            final CommandArgumentDefinition argumentDefinition = new CommandArgumentDefinition();

            if (registeredCommand.isNamedArgumentHandler()) {
                readNamedArg(parameter, argumentDefinition);
            }

            argumentDefinition.setParameterName(parameter.getName());
            argumentDefinition.setDescription(readValue(parameter, Description.class).orElse(null));
            argumentDefinition.setInjectable(parameter.isAnnotationPresent(InjectableArgument.class) || parameter.getType().isAnnotationPresent(InjectableArgument.class));
            argumentDefinition.setMandatory(!parameter.isAnnotationPresent(OptionalArg.class));
            argumentDefinition.setType(parameter.getType());
            argumentDefinition.setIndex(i);

            registeredCommand.addArgumentDefinition(argumentDefinition);
        }
    }

    protected Optional<Command> readCommandAnnotation(GanyuCommand command) {
        return Optional.ofNullable(command.getClass().getAnnotation(Command.class));
    }

    protected Optional<String> readValue(GanyuCommand command, Class<? extends Annotation> annotation) {
        return Optional.ofNullable(command.getClass().getAnnotation(annotation))
                .map(this::readAnnotationValue)
                .filter(value -> !value.isEmpty());
    }

    protected Optional<String> readValue(Method method, Class<? extends Annotation> annotation) {
        return Optional.ofNullable(method.getAnnotation(annotation))
                .map(this::readAnnotationValue)
                .filter(value -> !value.isEmpty());
    }

    protected Optional<String> readValue(Parameter parameter, Class<? extends Annotation> annotation) {
        return Optional.ofNullable(parameter.getAnnotation(annotation))
                .map(this::readAnnotationValue)
                .filter(value -> !value.isEmpty());
    }

    private void readNamedArg(Parameter parameter, CommandArgumentDefinition argumentDefinition) {
        Optional.ofNullable(parameter.getAnnotation(NamedArg.class))
                .ifPresent(namedArg -> {
                    argumentDefinition.setName(namedArg.value());
                    argumentDefinition.setLongName(namedArg.longForm());
                });
    }

    @SneakyThrows
    protected String readAnnotationValue(Annotation annotation) {
        Method valueMethod = annotation.annotationType().getMethod("value");
        Object value = valueMethod.invoke(annotation);
        if (value instanceof String) {
            return (String) value;
        }
        return null;
    }

    protected List<Method> listMethodsWithAnnotations(GanyuCommand command, Class<? extends Annotation> annotation) {
        List<Method> methodsWithAnnotations = new ArrayList<>();
        for (Method method : command.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotation)) {
                methodsWithAnnotations.add(method);
            }
        }
        return methodsWithAnnotations;
    }
}
