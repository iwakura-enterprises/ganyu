package enterprises.iwakura.ganyu.annotation;

import enterprises.iwakura.ganyu.ArgumentParser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a greedy argument type. The only default greedy type is a String. Will consume
 * all remaining input arguments when parsing with {@link ArgumentParser}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface GreedyArgument {

}
