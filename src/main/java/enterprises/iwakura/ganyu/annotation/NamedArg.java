package enterprises.iwakura.ganyu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a named parameter in a command method. A command that holds named parameters
 * should be annotated with {@link NamedArgumentHandler} if intended to be processed as "-arg value" or "--arg value".
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface NamedArg {

    /**
     * Short form of the named argument, e.g. "-f"
     * @return the short form
     */
    String value();

    /**
     * Long form of the named argument, e.g. "--force"
     * @return the long form
     */
    String longForm() default "";

}
