package enterprises.iwakura.ganyu;

import enterprises.iwakura.ganyu.exception.CommandParseException;
import lombok.Getter;

/**
 * Abstract class for parsing command arguments of type T.
 * @param <T> the type of the argument to be parsed
 */
@Getter
public abstract class ArgumentParser<T> {

    /**
     * The class type of the argument to be parsed.
     */
    private final Class<T> type;

    /**
     * Constructor for ArgumentParser.
     *
     * @param type the class type of the argument to be parsed
     */
    public ArgumentParser(Class<T> type) {
        this.type = type;
    }

    /**
     * Parses the given argument string into an object of type T.
     *
     * @param argument the argument string to parse
     *
     * @return the parsed object of type T
     * @throws CommandParseException if parsing fails
     */
    public abstract T parse(String argument) throws CommandParseException;

}
