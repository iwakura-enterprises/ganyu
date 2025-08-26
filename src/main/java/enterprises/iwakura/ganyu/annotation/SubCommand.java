package enterprises.iwakura.ganyu.annotation;

import enterprises.iwakura.ganyu.CommandResult;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.CompletableFuture;

/**
 * Marks a method as a sub-command for a command.
 * <p>
 * A command may return nothing (void), {@link CommandResult}
 * or a {@link CompletableFuture} of either.
 * Any other return type will be ignored and considered as {@link CommandResult#success()}
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SubCommand {

    /**
     * The name of the sub-command.
     *
     * @return The name of the sub-command.
     */
    String value();

}
