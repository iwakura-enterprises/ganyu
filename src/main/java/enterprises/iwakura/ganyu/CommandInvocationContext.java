package enterprises.iwakura.ganyu;

import enterprises.iwakura.ganyu.annotation.InjectableArgument;
import enterprises.iwakura.ganyu.annotation.NamedArg;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Context for a command invocation, holding information about the command, its arguments, and the result.
 */
@Getter
@Setter
@RequiredArgsConstructor
@InjectableArgument
public class CommandInvocationContext {

    private final Ganyu ganyu;
    private final RegisteredCommand registeredCommand;

    private String unprocessedArguments = "";
    private final Map<CommandArgumentDefinition, Object> argumentValues = new HashMap<>();

    private CommandResult commandResult;
    private Throwable commandException;

    /**
     * Adds a value for a specific command argument definition.
     *
     * @param argumentDefinition The definition of the command argument.
     * @param value              The value to associate with the command argument.
     */
    public void addArgumentValue(CommandArgumentDefinition argumentDefinition, Object value) {
        argumentValues.put(argumentDefinition, value);
    }

    /**
     * Retrieves a list of command argument definitions that match the specified parameter type and optional named argument.
     *
     * @param parameterType The type of the parameter to match.
     * @param namedArg      An optional NamedArg annotation to further filter by name.
     * @return A list of matching CommandArgumentDefinition instances.
     */
    public List<CommandArgumentDefinition> getArgumentsByTypeOrName(Class<?> parameterType, NamedArg namedArg) {
        List<CommandArgumentDefinition> matchingArguments = new ArrayList<>();
        if (namedArg == null) {
            for (CommandArgumentDefinition def : registeredCommand.getArgumentDefinitions()) {
                if (def.getType().equals(parameterType)) {
                    matchingArguments.add(def);
                }
            }
        } else {
            for (CommandArgumentDefinition def : registeredCommand.getArgumentDefinitions()) {
                if (def.getType().equals(parameterType) && def.getName().equals(namedArg.value())) {
                    matchingArguments.add(def);
                }
            }
        }
        return matchingArguments;
    }

    /**
     * Retrieves the value of a specific command argument.
     *
     * @param commandArgumentDefinition The definition of the command argument.
     * @return The value associated with the specified command argument definition, or null if not found.
     */
    public Object getArgumentValue(CommandArgumentDefinition commandArgumentDefinition) {
        return argumentValues.get(commandArgumentDefinition);
    }
}
