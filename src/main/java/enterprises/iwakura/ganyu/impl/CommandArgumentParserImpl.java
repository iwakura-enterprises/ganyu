package enterprises.iwakura.ganyu.impl;

import enterprises.iwakura.ganyu.*;
import enterprises.iwakura.ganyu.exception.CommandParseException;
import enterprises.iwakura.ganyu.exception.InvalidCommandArgumentsException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Default implementation of {@link CommandArgumentParser}.
 */
public class CommandArgumentParserImpl implements CommandArgumentParser {

    @Override
    public CommandInvocationContext parseSimple(CommandInvocationContext ctx) throws CommandParseException {
        Ganyu ganyu = ctx.getGanyu();
        RegisteredCommand command = ctx.getRegisteredCommand();
        List<CommandArgumentDefinition> argumentDefinitions = command.getArgumentDefinitions();
        CommandArgumentDefinition lastArgumentDefinition = command.getLastArgumentDefinition();
        String unprocessedArgsWithoutDoubleSpaces = ctx.getUnprocessedArguments().replaceAll("\\s+", " ").trim();
        String[] args = unprocessedArgsWithoutDoubleSpaces.isEmpty() ? new String[0] : unprocessedArgsWithoutDoubleSpaces.split(" ");

        if (lastArgumentDefinition == null) {
            return ctx; // No arguments defined
        }

        int argumentIndex = 0;
        for (CommandArgumentDefinition argumentDefinition : argumentDefinitions) {
            // Handle injectable arguments
            if (argumentDefinition.isInjectable()) {
                ctx.addArgumentValue(argumentDefinition, ganyu.getInjectableArgumentResolver().resolve(argumentDefinition, ctx));
                continue;
            }

            // Handle greedy arguments
            if (lastArgumentDefinition == argumentDefinition && argumentDefinition.isGreedy() && args.length > 0) {
                final StringBuilder greedyArgs = new StringBuilder();
                for (int i = argumentIndex; i < args.length; i++) {
                    greedyArgs.append(args[i]).append(" ");
                }
                ctx.addArgumentValue(argumentDefinition, parseSingleArgument(ctx, argumentDefinition, greedyArgs.toString().trim()));
                argumentIndex = args.length; // Move index to the end
                continue;
            }

            // Handle normal arguments
            if (argumentIndex >= args.length) {
                if (argumentDefinition.isMandatory()) {
                    throw new InvalidCommandArgumentsException(ctx, argumentDefinition.getType(), argumentIndex);
                } else {
                    ctx.addArgumentValue(argumentDefinition, parseSingleArgument(ctx, argumentDefinition, null));
                    continue;
                }
            }

            ctx.addArgumentValue(argumentDefinition, parseSingleArgument(ctx, argumentDefinition, args[argumentIndex]));
            argumentIndex++;
        }

        return ctx;
    }

    @Override
    public CommandInvocationContext parseNamed(CommandInvocationContext ctx) throws CommandParseException {
        final Ganyu ganyu = ctx.getGanyu();
        final RegisteredCommand command = ctx.getRegisteredCommand();
        final List<CommandArgumentDefinition> argumentDefinitions = command.getArgumentDefinitions();
        final String rawArgs = ctx.getUnprocessedArguments();

        // Parse the raw input into a list of tokens
        List<String> tokens = tokenizeArguments(rawArgs);

        int i = 0;
        while (i < tokens.size()) {
            final String token = tokens.get(i);
            i++;

            // Skip if not a flag
            if (!token.startsWith("-")) {
                continue;
            }

            CommandArgumentDefinition argDef = getCommandArgumentDefinition(token, argumentDefinitions);

            // Ignore injectable arguments
            if (argDef.isInjectable()) {
                continue;
            }

            // Collect value tokens until we hit another flag or end of input
            final StringBuilder valueBuilder = new StringBuilder();

            while (i < tokens.size() && !tokens.get(i).startsWith("-")) {
                if (valueBuilder.length() > 0) {
                    valueBuilder.append(" ");
                }
                valueBuilder.append(tokens.get(i));
                i++;
            }

            String value = valueBuilder.toString();

            if (value.isEmpty() && argDef.isMandatory()) {
                if (argDef.isMandatory()) {
                    throw new CommandParseException("Missing value for argument: " + token);
                } else {
                    value = null;
                }
            }

            ctx.addArgumentValue(argDef, parseSingleArgument(ctx, argDef, value));
        }

        // Handle injectable arguments
        for (CommandArgumentDefinition argumentDefinition : argumentDefinitions) {
            if (argumentDefinition.isInjectable()) {
                ctx.addArgumentValue(argumentDefinition, ganyu.getInjectableArgumentResolver().resolve(argumentDefinition, ctx));
            }
        }

        return ctx;
    }

    protected static CommandArgumentDefinition getCommandArgumentDefinition(String token, List<CommandArgumentDefinition> argumentDefinitions) {
        final boolean isLongFlag = token.startsWith("--");
        final String flagName = isLongFlag ? token.substring(2) : token.substring(1);

        // Find matching argument definition
        CommandArgumentDefinition argDef = null;
        for (CommandArgumentDefinition def : argumentDefinitions) {
            if ((isLongFlag && def.getLongName() != null && !def.getLongName().isEmpty() && flagName.equals(def.getLongName())) ||
                    (!isLongFlag && flagName.equals(def.getName()))) {
                argDef = def;
                break;
            }
        }

        if (argDef == null) {
            throw new CommandParseException("Unknown argument: " + token);
        }
        return argDef;
    }

    protected List<String> tokenizeArguments(String input) {
        if (input == null || input.isEmpty()) {
            return Collections.emptyList();
        }

        final List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == '"' && (i == 0 || input.charAt(i-1) != '\\')) {
                inQuotes = !inQuotes;
                continue;
            }

            if (c == ' ' && !inQuotes) {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken = new StringBuilder();
                }
            } else {
                currentToken.append(c);
            }
        }

        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        return tokens;
    }
}
