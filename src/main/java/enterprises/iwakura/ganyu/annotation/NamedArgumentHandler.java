package enterprises.iwakura.ganyu.annotation;

import enterprises.iwakura.ganyu.CommandArgumentParser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a command method that uses named parameters. This wil enable the named parameter
 * parsing in {@link CommandArgumentParser}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface NamedArgumentHandler {

}
