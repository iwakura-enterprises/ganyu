package enterprises.iwakura.ganyu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import java.lang.annotation.Target;

/**
 * Marks a method as an exception handler for command execution exceptions.
 * Annotated method will be invoked if any exception is thrown during command execution and processing in its class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ExceptionHandler {

}
