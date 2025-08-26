package enterprises.iwakura.ganyu;

import enterprises.iwakura.ganyu.annotation.NamedArg;
import enterprises.iwakura.ganyu.exception.CommandParseException;
import enterprises.iwakura.ganyu.exception.InvalidCommandArgumentsException;
import enterprises.iwakura.ganyu.impl.*;
import enterprises.iwakura.ganyu.impl.argumentParsers.PrimitiveArgumentParsers;
import enterprises.iwakura.ganyu.impl.commands.HelpCommand;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

/**
 * Main class for the Ganyu CLI command library.
 * <p>
 *     For easiest usage, use the {@link #console()} method to create a Ganyu instance with console input and output.
 *     Furthermore, you can use the {@link #standard(Input, Output)} or
 *     {@link #standardWithExecutor(Input, Output, Executor)} methods to create a Ganyu instance with custom input and output
 *     implementations, as well as an optional executor for command execution.
 * </p>
 * You may customize the {@link Ganyu} to your heart's content by providing your own implementations of various
 * interfaces.
 */
@Getter
@Setter
public class Ganyu {

    protected final Input input;
    protected final Output output;
    protected final CommandArgumentParser commandArgumentParser;
    protected final CommandRegisterProcessor commandRegisterProcessor;
    protected final InjectableArgumentResolver injectableArgumentResolver;
    protected final Executor executor;

    // Lookup map for registered commands, sorted by key length and then lexicographically
    protected final Map<String, RegisteredCommand> registeredCommandLookup = new TreeMap<>(
            (a, b) -> {
                int cmp = Integer.compare(b.length(), a.length());
                return cmp != 0 ? cmp : a.compareTo(b);
            }
    );

    protected ThreadFactory threadFactory = runnable -> new Thread(runnable, "Ganyu-Command-Reader");
    protected Map<Class<?>, ArgumentParser<?>> argumentParsers = new HashMap<>();
    protected List<RegisteredCommand> registeredCommands = new ArrayList<>();

    protected boolean run;

    protected final Thread commandReaderThread = createCommandReaderThread();

    /**
     * Creates a new Ganyu instance with the provided components.
     *
     * @param input                     The input source to read commands from.
     * @param output                    The output destination to write info messages and errors to.
     * @param commandArgumentParser     The parser to parse command arguments.
     * @param commandRegisterProcessor  The processor to register commands.
     * @param injectableArgumentResolver The resolver for injectable arguments.
     * @param executor                  The executor to run commands asynchronously.
     */
    public Ganyu(Input input, Output output, CommandArgumentParser commandArgumentParser, CommandRegisterProcessor commandRegisterProcessor, InjectableArgumentResolver injectableArgumentResolver, Executor executor) {
        this.input = input;
        this.output = output;
        this.commandArgumentParser = commandArgumentParser;
        this.commandRegisterProcessor = commandRegisterProcessor;
        this.injectableArgumentResolver = injectableArgumentResolver;
        this.executor = executor;

        PrimitiveArgumentParsers.register(this);
        this.registerCommands(new HelpCommand());
    }

    /**
     * Creates a standard Ganyu instance with {@link ConsoleInput} and {@link ConsoleOutput}.
     *
     * @return A Ganyu instance configured for console input and output.
     */
    public static Ganyu console() {
        return standard(new ConsoleInput(), new ConsoleOutput());
    }

    /**
     * Creates a standard Ganyu instance with the provided input and output implementations. This will
     * run the commands in the command reader thread, blocking it until the command completes.
     *
     * @param input  The input source to read commands from.
     * @param output The output destination to write info messages and errors to.
     * @return A Ganyu instance configured with the provided input and output.
     */
    public static Ganyu standard(Input input, Output output) {
        return new Ganyu(input, output, new CommandArgumentParserImpl(), new CommandRegisterProcessorImpl(), new ClassInjectableArgumentResolver(), Runnable::run);
    }

    /**
     * Creates a standard Ganyu instance with the provided input, output implementations, and executor.
     * This will run the commands in the provided executor, allowing for asynchronous command execution.
     *
     * @param input    The input source to read commands from.
     * @param output   The output destination to write info messages and errors to.
     * @param executor The executor to run commands asynchronously.
     * @return A Ganyu instance configured with the provided input, output, and executor.
     */
    public static Ganyu standardWithExecutor(Input input, Output output, Executor executor) {
        return new Ganyu(input, output, new CommandArgumentParserImpl(), new CommandRegisterProcessorImpl(), new ClassInjectableArgumentResolver(), executor);
    }

    /**
     * Starts the Ganyu command reader thread, which will read commands from the input source
     * and execute them using the provided executor.
     *
     * @throws IllegalStateException if Ganyu is already running.
     */
    public void run() {
        if (run) {
            throw new IllegalStateException("Ganyu is already running!");
        }

        run = true;
        commandReaderThread.start();
    }

    /**
     * Stops the Ganyu command reader thread, which will stop reading commands from the input source.
     *
     * @throws IllegalStateException if Ganyu is not running.
     */
    public void stop() {
        if (!run) {
            throw new IllegalStateException("Ganyu is not running!");
        }

        run = false;
        commandReaderThread.interrupt();
    }

    /**
     * Creates the command reader thread, which continuously reads commands from the input source
     * @return The created command reader thread.
     */
    protected Thread createCommandReaderThread() {
        return threadFactory.newThread(() -> {
            while (run) {
                final String inputString;

                try {
                    inputString = this.input.readNextInput();
                } catch (Exception exception) {
                    if (exception instanceof NoSuchElementException) {
                        return; // End of input stream (usually thrown by Scanner)
                    }

                    output.error("Failed to read input!", exception);
                    continue;
                }

                if (inputString == null || inputString.isEmpty()) {
                    continue;
                }

                executor.execute(() -> {
                    final RegisteredCommand registeredCommand = lookupCommand(inputString);

                    if (registeredCommand == null) {
                        output.error("Unknown command: " + inputString, null);
                        return;
                    }

                    final CommandInvocationContext context = new CommandInvocationContext(this, registeredCommand);

                    try {
                        // Removes the command name from the input string to obtain the arguments
                        final String arguments = inputString.replaceFirst(registeredCommand.getFullyQualifiedName(), "").trim();
                        context.setUnprocessedArguments(arguments);

                        if (registeredCommand.isNamedArgumentHandler()) {
                            commandArgumentParser.parseNamed(context);
                        } else {
                            commandArgumentParser.parseSimple(context);
                        }
                    } catch (CommandParseException parseException) {
                        output.error(parseException.getMessage(), null);
                        handleException(context, parseException);
                        return;
                    } catch (Exception exception) {
                        output.error("An unexpected error occurred while parsing command arguments!", exception);
                        handleException(context, exception);
                        return;
                    }

                    executeCommand(context);
                });
            }
        });
    }

    /**
     * Registers a custom argument parser for a specific type.
     *
     * @param parser The argument parser to register.
     * @param <T>    The type that the parser can parse.
     * @throws IllegalArgumentException if the parser is null.
     */
    public <T> void registerArgumentParser(ArgumentParser<T> parser) {
        if (parser == null) {
            throw new IllegalArgumentException("Type and parser cannot be null!");
        }

        argumentParsers.put(parser.getType(), parser);
    }

    /**
     * Registers one or more commands with Ganyu.
     *
     * @param commands The commands to register.
     * @return A list of registered commands.
     */
    public List<RegisteredCommand> registerCommands(GanyuCommand... commands) {
        if (commands == null || commands.length == 0) {
            return null;
        }

        List<RegisteredCommand> registeredCommands = null;

        for (GanyuCommand command : commands) {
            if (command == null) {
                continue;
            }

            registeredCommands = commandRegisterProcessor.process(this, command);

            // Add to registered commands
            this.registeredCommands.addAll(registeredCommands);

            // Add to lookup map
            registeredCommands.forEach(registeredCommand -> {
                // Default command
                if (registeredCommand.hasMethod()) {
                    registeredCommandLookup.put(registeredCommand.getFullyQualifiedName(), registeredCommand);
                }

                // Subcommands
                registeredCommand.getSubCommands().forEach(subCommand -> {
                    registeredCommandLookup.put(subCommand.getFullyQualifiedName(), subCommand);
                });

                // NOTE: There are no two-level deep subcommands currently
            });
        }

        return registeredCommands;
    }

    /**
     * Looks up a registered command by its name from the input string.
     * This method attempts to match the longest possible command name first.
     *
     * @param inputString The full input string containing the command and its arguments.
     * @return The matched RegisteredCommand, or null if no command is found.
     */
    protected RegisteredCommand lookupCommand(String inputString) {
        final String[] splitInput = inputString.split(" ");

        for (int i = splitInput.length - 1; i >= 0; i--) {
            String constructedCommand = String.join(" ", Arrays.copyOfRange(splitInput, 0, i + 1));
            if (registeredCommandLookup.containsKey(constructedCommand)) {
                // If the command is found, return it
                return registeredCommandLookup.get(constructedCommand);
            } else if (i == 0) {
                // If we reach the first word and it's not found, return null
                return null;
            }
        }

        return null;
    }

    /**
     * Executes a command based on the provided CommandInvocationContext.
     * This method handles pre-command, command execution, post-command, and exception handling.
     *
     * @param ctx The CommandInvocationContext containing information about the command to execute.
     */
    protected void executeCommand(CommandInvocationContext ctx) {
        RegisteredCommand command = ctx.getRegisteredCommand();

        if (command.getPreCommandMethod() != null) {
            try {
                command.getPreCommandMethod().invoke(command.getGanyuCommand(), ctx);
            } catch (Exception exception) {
                output.error("An unexpected error occurred while invoking pre-command method!", exception);
                handleException(ctx, exception);
                return;
            }
        }

        CompletableFuture<CommandResult> futureCommandResult;
        Object commandReturnValue;

        try {
            commandReturnValue = command.getMethod().invoke(command.getGanyuCommand(), getArgumentValues(command.getMethod(), ctx));
        } catch (Exception exception) {
            output.error("An unexpected error occurred while invoking/executing command method!", exception);
            handleException(ctx, exception);
            return;
        }

        if (commandReturnValue instanceof CommandResult) {
            futureCommandResult = CompletableFuture.completedFuture((CommandResult) commandReturnValue);
        } else if (commandReturnValue instanceof CompletableFuture) {
            try {
                futureCommandResult = (CompletableFuture<CommandResult>) commandReturnValue;
            } catch (Exception exception) {
                output.error("An unexpected error occurred while casting command return value to CompletableFuture<CommandResult>!", exception);
                handleException(ctx, exception);
                return;
            }
        } else {
            // Invalid return type, treat as success
            futureCommandResult = CompletableFuture.completedFuture(CommandResult.success());
        }

        futureCommandResult.whenCompleteAsync((result, commandException) -> {
            ctx.setCommandException(commandException);
            ctx.setCommandResult(result);

            if (commandException != null) {
                output.error("An unexpected error occurred while executing command!", commandException);
                handleException(ctx, (Exception) (commandException instanceof InvocationTargetException ? commandException.getCause() : commandException));
                return;
            }

            if (!result.isSuccess()) {
                if (result.getErrorMessage() != null) {
                    output.error(result.getErrorMessage(), null);
                } else {
                    output.error("Command execution failed (however, no message was given.)", null);
                }
            }

            if (command.getPostCommandMethod() != null) {
                try {
                    command.getPostCommandMethod().invoke(command.getGanyuCommand(), ctx);
                } catch (Exception exception) {
                    output.error("An unexpected error occurred while invoking post-command method!", exception);
                    handleException(ctx, exception);
                }
            }
        });
    }

    /**
     * Handles exceptions that occur during command execution by invoking the registered exception handler method, if any.
     *
     * @param ctx                The CommandInvocationContext containing information about the command execution.
     * @param exceptionToHandle The exception that occurred during command execution.
     */
    protected void handleException(CommandInvocationContext ctx, Exception exceptionToHandle) {
        RegisteredCommand command = ctx.getRegisteredCommand();

        if (command.getExceptionHandlerMethod() != null) {
            try {
                command.getExceptionHandlerMethod().invoke(command.getGanyuCommand(), ctx, exceptionToHandle);
            } catch (Exception exception) {
                output.error("An unexpected error occurred while invoking exception handler method!", exception);
            }
        }
    }

    /**
     * Retrieves the argument values for a command method based on the provided CommandInvocationContext.
     *
     * @param method The command method for which to retrieve argument values.
     * @param ctx    The CommandInvocationContext containing information about the command invocation.
     * @return An array of argument values to be passed to the command method.
     * @throws InvalidCommandArgumentsException if required arguments are missing or invalid.
     */
    protected Object[] getArgumentValues(Method method, CommandInvocationContext ctx) {
        Parameter[] parameters = method.getParameters();
        Object[] arguments = new Object[method.getParameterCount()];

        for (int i = 0; i < arguments.length; i++) {
            final int argumentIndex = i;
            Parameter parameter = parameters[argumentIndex];
            Class<?> parameterType = parameter.getType();
            List<CommandArgumentDefinition> argumentDefinitions = ctx.getArgumentsByTypeOrName(parameterType, parameter.getAnnotation(NamedArg.class));

            if (argumentDefinitions.isEmpty()) {
                throw new InvalidCommandArgumentsException(ctx, parameterType, argumentIndex);
            } else if (argumentDefinitions.size() == 1) {
                arguments[argumentIndex] = ctx.getArgumentValue(argumentDefinitions.get(0));
            } else {
                // Multiple arguments of the same type, match by index hint
                Optional<CommandArgumentDefinition> argumentDefinitionByIndex = argumentDefinitions.stream()
                        .filter(def -> def.getIndex() == argumentIndex)
                        .findFirst();

                if (!argumentDefinitionByIndex.isPresent()) {
                    throw new InvalidCommandArgumentsException(ctx, parameterType, argumentIndex);
                }

                arguments[argumentIndex] = ctx.getArgumentValue(argumentDefinitionByIndex.get());
            }
        }

        // Check for primitive types that are null
        for (int i = 0; i < parameters.length; i++) {
            if (arguments[i] == null && parameters[i].getType().isPrimitive()) {
                throw new InvalidCommandArgumentsException(ctx, parameters[i].getType(), i);
            }
        }

        return arguments;
    }

    /**
     * Retrieves an unmodifiable list of all registered commands.
     *
     * @return An unmodifiable list of registered commands.
     */
    public List<RegisteredCommand> getRegisteredCommands() {
        return Collections.unmodifiableList(registeredCommands);
    }

    /**
     * Retrieves the argument parser registered for the specified type.
     *
     * @param type The type for which to retrieve the argument parser.
     * @return The ArgumentParser registered for the specified type.
     * @throws IllegalArgumentException if no parser is registered for the specified type or if the type is null.
     */
    public ArgumentParser<?> getArgumentParser(Class<?> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null!");
        }

        ArgumentParser<?> parser = argumentParsers.get(type);

        if (parser == null) {
            throw new IllegalArgumentException("No argument parser registered for type: " + type.getName());
        }

        return parser;
    }
}
