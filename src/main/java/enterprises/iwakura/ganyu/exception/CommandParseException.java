package enterprises.iwakura.ganyu.exception;

/**
 * Exception thrown when a command cannot be parsed or executed correctly.
 */
public class CommandParseException extends RuntimeException {

    public CommandParseException(String message) {
        super(message);
    }
}
