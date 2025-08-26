package enterprises.iwakura.ganyu.exception;

import enterprises.iwakura.ganyu.GanyuCommand;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Exception thrown when a command method has invalid parameters.
 */
@Getter
public class InvalidCommandMethodException extends CommandParseException {

    protected final GanyuCommand command;
    protected final Method method;
    protected final Class<?>[] requiredParameters;

    public InvalidCommandMethodException(GanyuCommand command, Method method, Class<?>... requiredParameters) {
        super(String.format("Method %s in command %s has invalid parameters, expected: %s", method, command, Arrays.toString(requiredParameters)));
        this.command = command;
        this.method = method;
        this.requiredParameters = requiredParameters;
    }
}
