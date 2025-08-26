package enterprises.iwakura.ganyu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method to be executed after the main command method has been executed.
 * This can be used for cleanup tasks, logging, or any other post-command processing.
 * The marked method will not be executed if the {@link PreCommand} method fails or the command method itself fails.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface PostCommand {

}
