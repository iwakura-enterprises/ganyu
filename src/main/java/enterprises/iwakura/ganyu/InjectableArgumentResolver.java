package enterprises.iwakura.ganyu;

import java.util.function.BiFunction;

/**
 * Interface for resolving injectable arguments in command methods.
 */
public interface InjectableArgumentResolver {

    /**
     * Registers a resolver function for a specific class type.
     *
     * @param clazz the class type to register the resolver for
     * @param resolverFunction the function that resolves an instance of the class type
     * @param <T> the type of the class
     */
    <T> void register(Class<T> clazz, BiFunction<CommandArgumentDefinition, CommandInvocationContext, T> resolverFunction);

    /**
     * Resolves an instance of the specified class type for the given command argument definition and context.
     *
     * @param commandArgumentDefinition the definition of the command argument
     * @param ctx the command invocation context
     *
     * @return an instance of the specified class type, or null if not resolvable
     */
    Object resolve(CommandArgumentDefinition commandArgumentDefinition, CommandInvocationContext ctx);

}
