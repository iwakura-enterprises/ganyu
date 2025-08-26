package enterprises.iwakura.ganyu.exception;

import enterprises.iwakura.ganyu.CommandInvocationContext;
import lombok.Getter;

/**
 * Exception thrown when command arguments are invalid or missing.
 */
@Getter
public class InvalidCommandArgumentsException extends CommandParseException {

    protected final CommandInvocationContext ctx;
    protected final Class<?> parameterType;
    protected final int index;

    public InvalidCommandArgumentsException(CommandInvocationContext ctx, Class<?> parameterType, int index) {
        super(String.format("No argument specified for parameter of type %s at index %d in command %s",
                parameterType.getName(), index, ctx.getRegisteredCommand().getName()));
        this.ctx = ctx;
        this.parameterType = parameterType;
        this.index = index;
    }
}
