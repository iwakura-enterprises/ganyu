package enterprises.iwakura.ganyu.exception;

import enterprises.iwakura.ganyu.GanyuCommand;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Exception thrown when a command or method is not annotated with a required annotation.
 */
@Getter
public class CommandNotAnnotatedException extends CommandParseException {

    protected final GanyuCommand command;
    protected final Method method;
    protected final Class<? extends Annotation> annotation;

    public CommandNotAnnotatedException(GanyuCommand command, Class<? extends Annotation> annotation) {
        super(String.format("Command %s does not inherit or have valid annotation %s or has empty value", command.getClass(), annotation.getName()));
        this.command = command;
        this.method = null;
        this.annotation = annotation;
    }

    public CommandNotAnnotatedException(GanyuCommand command, Method method, Class<? extends Annotation> annotation) {
        super(String.format("Method %s in command %s does not inherit or have valid annotation %s or has empty value", method, command.getClass(), annotation.getName()));
        this.command = command;
        this.method = method;
        this.annotation = annotation;
    }
}
