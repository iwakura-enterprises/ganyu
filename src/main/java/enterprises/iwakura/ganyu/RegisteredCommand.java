package enterprises.iwakura.ganyu;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a registered command with its metadata, argument definitions, and sub-commands.
 */
@Getter
@Setter
public class RegisteredCommand {

    private final GanyuCommand ganyuCommand;

    private final List<CommandArgumentDefinition> argumentDefinitions = new ArrayList<>();
    private final List<RegisteredCommand> subCommands = new ArrayList<>();

    private String fullyQualifiedName;
    private String name;
    private String description;
    private String syntax;
    private boolean namedArgumentHandler;

    private Method method;
    private Method preCommandMethod;
    private Method postCommandMethod;
    private Method exceptionHandlerMethod;

    /**
     * Creates a new RegisteredCommand instance.
     * @param ganyuCommand The command instance associated with this registered command.
     */
    public RegisteredCommand(GanyuCommand ganyuCommand) {
        this.ganyuCommand = ganyuCommand;
    }

    /**
     * Creates a new RegisteredCommand instance with an associated method.
     *
     * @param ganyuCommand The command instance associated with this registered command.
     * @param method The method to be invoked for this command.
     */
    public RegisteredCommand(GanyuCommand ganyuCommand, Method method) {
        this.ganyuCommand = ganyuCommand;
        this.method = method;
    }

    /**
     * Checks if the command has an associated method.
     *
     * @return true if the method is not null, false otherwise.
     */
    public boolean hasMethod() {
        return method != null;
    }

    /**
     * Adds a command argument definition to the list of argument definitions.
     *
     * @param definition The command argument definition to add.
     */
    public void addArgumentDefinition(CommandArgumentDefinition definition) {
        argumentDefinitions.add(definition);
    }

    /**
     * Adds a sub-command to the list of sub-commands.
     *
     * @param subCommand The sub-command to add.
     */
    public void addSubCommand(RegisteredCommand subCommand) {
        subCommands.add(subCommand);
    }

    /**
     * Retrieves the last command argument definition in the list.
     *
     * @return The last CommandArgumentDefinition, or null if the list is empty.
     */
    public CommandArgumentDefinition getLastArgumentDefinition() {
        if (argumentDefinitions.isEmpty()) {
            return null;
        }
        return argumentDefinitions.get(argumentDefinitions.size() - 1);
    }
}
