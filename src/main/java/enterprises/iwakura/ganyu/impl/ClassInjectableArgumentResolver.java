package enterprises.iwakura.ganyu.impl;

import enterprises.iwakura.ganyu.CommandArgumentDefinition;
import enterprises.iwakura.ganyu.CommandInvocationContext;
import enterprises.iwakura.ganyu.InjectableArgumentResolver;
import enterprises.iwakura.ganyu.annotation.InjectableArgument;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * An implementation of {@link InjectableArgumentResolver} that allows registering
 * resolver functions for classes annotated with {@link InjectableArgument}.
 */
public class ClassInjectableArgumentResolver implements InjectableArgumentResolver {

    protected Map<Class<?>, BiFunction<CommandArgumentDefinition, CommandInvocationContext, Object>> resolvers;

    @Override
    public <T> void register(Class<T> clazz, BiFunction<CommandArgumentDefinition, CommandInvocationContext, T> resolverFunction) {
        if (!clazz.isAnnotationPresent(InjectableArgument.class)) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " is not annotated with @InjectableArgument");
        }

        //noinspection unchecked
        resolvers.put(clazz, (BiFunction<CommandArgumentDefinition, CommandInvocationContext, Object>)resolverFunction);
    }

    @Override
    public Object resolve(CommandArgumentDefinition commandArgumentDefinition, CommandInvocationContext ctx) {
        if (commandArgumentDefinition.getType() == CommandInvocationContext.class) {
            return ctx; // Directly return the context if the type is CommandInvocationContext
        }

        return resolvers.getOrDefault(commandArgumentDefinition.getType(), (def, ctx2) -> null);
    }
}
