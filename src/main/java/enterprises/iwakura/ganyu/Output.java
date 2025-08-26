package enterprises.iwakura.ganyu;

/**
 * Interface for outputting messages and errors.
 */
public interface Output {

    /**
     * Outputs an informational message.
     *
     * @param message The message to output.
     */
    void info(String message);

    /**
     * Outputs an error message.
     *
     * @param message The message to output.
     * @param throwable The throwable associated with the error (can be null).
     */
    void error(String message, Throwable throwable);

}
