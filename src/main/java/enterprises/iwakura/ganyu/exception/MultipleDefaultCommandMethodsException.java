package enterprises.iwakura.ganyu.exception;

import enterprises.iwakura.ganyu.GanyuCommand;
import enterprises.iwakura.ganyu.annotation.DefaultCommand;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Exception thrown when a command class has more than one method annotated with @DefaultCommand.
 */
@Getter
public class MultipleDefaultCommandMethodsException extends CommandParseException {

    protected final GanyuCommand command;
    protected final List<Method> methods;

    public MultipleDefaultCommandMethodsException(GanyuCommand command, List<Method> methods) {
        super(String.format("There is more than one method annotated with %s annotation in command class %s: %s",
                DefaultCommand.class.getSimpleName(), command.getClass().getName(), methods)
        );
        this.command = command;
        this.methods = methods;
    }
}
