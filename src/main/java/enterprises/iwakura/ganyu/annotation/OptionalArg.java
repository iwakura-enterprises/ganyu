package enterprises.iwakura.ganyu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a parameter as optional. Be aware that it is not possible to mark primitive type as optional.
 * <p>
 *     Optional parameters will be assigned to null if no argument is provided.
 * </p>
 * In case of a method not marked with {@link NamedArgumentHandler}, you must mark parameters as optional from right to left. This means you cannot have a required parameter
 * after an optional parameter.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface OptionalArg {

}
