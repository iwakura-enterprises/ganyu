package enterprises.iwakura.ganyu;

import lombok.Data;

/**
 * Represents the result of a command execution.
 * Contains an error message if the command failed, and a success flag.
 */
@Data
public class CommandResult {

    private static final CommandResult SUCCESS = new CommandResult(null, true);

    protected final String errorMessage;
    protected final boolean success;

    /**
     * Creates errored command result.
     *
     * @param errorMessage the error message to be displayed
     *
     * @return CommandResult instance indicating failure
     */
    public static CommandResult error(String errorMessage) {
        return new CommandResult(errorMessage, false);
    }

    /**
     * Creates a successful command result.
     *
     * @return CommandResult instance indicating success
     */
    public static CommandResult success() {
        return SUCCESS;
    }
}
