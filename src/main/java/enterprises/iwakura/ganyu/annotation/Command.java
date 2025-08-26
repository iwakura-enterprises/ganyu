package enterprises.iwakura.ganyu.annotation;

import enterprises.iwakura.ganyu.CommandResult;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.CompletableFuture;

/**
 * Annotation to define a command or sub-command.
 * <p>
 * This annotation can be applied to classes or methods.
 * When applied to a class or method, it defines level-one command.
 * </p>
 * A command may return nothing (void), {@link CommandResult}
 * or a {@link CompletableFuture} of either.
 * Any other return type will be ignored and considered as {@link CommandResult#success()}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Command {

    /**
     * The command's name. Used when invoking the command.
     *
     * @return the command's name
     */
    String value();

}
