package enterprises.iwakura.ganyu.annotation;

import enterprises.iwakura.ganyu.Ganyu;
import enterprises.iwakura.ganyu.InjectableArgumentResolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class that can be injected as an argument into a command method. You may
 * register this class with {@link InjectableArgumentResolver} in {@link Ganyu}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface InjectableArgument {

}
