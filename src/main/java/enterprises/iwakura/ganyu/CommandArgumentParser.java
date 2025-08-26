package enterprises.iwakura.ganyu;

import enterprises.iwakura.ganyu.exception.CommandParseException;

/**
 * Interface for parsing command arguments. Implementations should provide methods for parsing
 * both simple and named arguments. All the unprocessed arguments are stored in the {@link CommandInvocationContext}.
 */
public interface CommandArgumentParser {

    /**
     * Parses simple (positional) arguments from the command invocation context.
     *
     * @param ctx The command invocation context containing the arguments to be parsed.
     *
     * @return The updated command invocation context with parsed arguments.
     * @throws CommandParseException if parsing fails.
     */
    CommandInvocationContext parseSimple(CommandInvocationContext ctx) throws CommandParseException;

    /**
     * Parses named arguments from the command invocation context.
     *
     * @param ctx The command invocation context containing the arguments to be parsed.
     *
     * @return The updated command invocation context with parsed arguments.
     * @throws CommandParseException if parsing fails.
     */
    CommandInvocationContext parseNamed(CommandInvocationContext ctx) throws CommandParseException;

    /**
     * Parses a single argument based on its definition and the provided string value. Handles primitive
     * types by converting them to their corresponding wrapper classes. Calls current
     * {@link Ganyu}'s {@link ArgumentParser} for specific type parsing.
     *
     * @param ctx The command invocation context.
     * @param argumentDefinition The definition of the argument to be parsed.
     * @param argument The string representation of the argument to be parsed.
     *
     * @return The parsed argument object.
     * @throws CommandParseException if parsing fails.
     */
    default Object parseSingleArgument(CommandInvocationContext ctx, CommandArgumentDefinition argumentDefinition, String argument) throws CommandParseException {
        if (argument == null) {
            return null;
        }

        Class<?> type = argumentDefinition.getType();

        // Handles primitive types by converting them to their corresponding wrapper classes
        if (type.isPrimitive()) {
            if (type == boolean.class) {
                type = Boolean.class;
            } else if (type == int.class) {
                type = Integer.class;
            } else if (type == long.class) {
                type = Long.class;
            } else if (type == double.class) {
                type = Double.class;
            } else if (type == float.class) {
                type = Float.class;
            } else if (type == short.class) {
                type = Short.class;
            } else if (type == byte.class) {
                type = Byte.class;
            } else if (type == char.class) {
                type = Character.class;
            }
        }

        return ctx.getGanyu().getArgumentParser(type).parse(argument);
    }
}
