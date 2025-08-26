package enterprises.iwakura.ganyu;

import java.util.NoSuchElementException;

/**
 * Interface for reading user input. Its method should block until input is available.
 */
public interface Input {

    /**
     * Reads the next line of input from the user. This method should block until input is available.
     *
     * If {@link NoSuchElementException} is thrown, the command reading thread will silently terminate.
     *
     * @return Non-null string containing the user input.
     */
    String readNextInput();

}
