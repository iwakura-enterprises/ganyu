package enterprises.iwakura.ganyu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method to be executed before the main command method is executed.
 * This can be used for setup tasks, validation, or any other pre-command processing.
 * If this method fails (throws an exception), the main command method and {@link PostCommand} method will not be executed.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface PreCommand {

}
