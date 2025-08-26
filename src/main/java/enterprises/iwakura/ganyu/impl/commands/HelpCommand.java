package enterprises.iwakura.ganyu.impl.commands;

import enterprises.iwakura.ganyu.*;
import enterprises.iwakura.ganyu.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * In-built help command to list available commands and their details.
 */
@Command("help")
@Description("Integrated help command to list available commands.")
@Syntax("[command]")
public class HelpCommand implements GanyuCommand {

    @DefaultCommand
    @Description("Lists all registered commands or shows help for a specific command.")
    public CommandResult listCommands(
        CommandInvocationContext ctx,
        @OptionalArg @Description("Command name to see help for") String commandName
    ) {
        Ganyu ganyu = ctx.getGanyu();
        Output output = ganyu.getOutput();
        List<RegisteredCommand> commands = ganyu.getRegisteredCommands();

        if (commandName != null) {
            RegisteredCommand command = ganyu.getRegisteredCommandLookup().get(commandName);

            if (command == null) {
                return CommandResult.error("Unknown command: " + commandName);
            }

            showCommandHelp(command, output, "");
        } else {
            output.info("There is a total of " + commands.size() + " registered commands:");

            commands.forEach(command -> {
                StringBuilder line = new StringBuilder();
                line.append("- ").append(command.getName());
                if (command.getSyntax() != null && !command.getSyntax().isEmpty()) {
                    line.append(" ").append(command.getSyntax());
                }
                if (command.getDescription() != null && !command.getDescription().isEmpty()) {
                    line.append(": ").append(command.getDescription());
                }
                if (!command.getSubCommands().isEmpty()) {
                    line.append(" (and ").append(command.getSubCommands().size()).append(" subcommands)");
                }

                output.info(line.toString());
            });
        }

        return CommandResult.success();
    }

    private void showCommandHelp(RegisteredCommand command, Output output, String outputPrefix) {
        output.info(outputPrefix + "Command: " + command.getName());
        if (command.getDescription() != null && !command.getDescription().isEmpty()) {
            output.info(outputPrefix + "  Description: " + command.getDescription());
        }
        if (command.getSyntax() != null && !command.getSyntax().isEmpty()) {
            output.info(outputPrefix + "  Syntax: " + command.getName() + " " + command.getSyntax());
        }
        if (!command.getArgumentDefinitions().isEmpty()) {
            output.info(outputPrefix + "  Arguments:");
            command.getArgumentDefinitions().forEach(argDef -> {
                StringBuilder argLine = new StringBuilder();
                String argumentName = (argDef.getName() != null ? "-" + argDef.getName() : "") + (argDef.getLongName() != null ? " --" + argDef.getLongName() : "");

                argLine.append(outputPrefix).append("   - ");

                if (argDef.isInjectable()) {
                    argLine.append("[Injected] ");
                } else if (argDef.isMandatory()) {
                    argLine.append("[Mandatory] ");
                } else {
                    argLine.append("[Optional] ");
                }

                if (argumentName.isEmpty()) {
                    argLine.append(argDef.getType().getSimpleName());
                } else {
                    argLine.append(argumentName).append(" (").append(argDef.getType().getSimpleName()).append(")");
                }
                if (argDef.getDescription() != null && !argDef.getDescription().isEmpty()) {
                    argLine.append(": ").append(argDef.getDescription());
                }
                output.info(argLine.toString());
            });
        }
        if (!command.getSubCommands().isEmpty()) {
            output.info(outputPrefix + "  Sub-commands:");
            command.getSubCommands().forEach(subCommand -> {
                showCommandHelp(subCommand, output, outputPrefix + "\t");
            });
        }
    }

    @SubCommand("lookups")
    @Description("Lists the command lookups used by Ganyu.")
    public CommandResult listLookups(CommandInvocationContext ctx) {
        Ganyu ganyu = ctx.getGanyu();
        Output output = ganyu.getOutput();
        Map<String, RegisteredCommand> commandLookupMap = ganyu.getRegisteredCommandLookup();

        output.info("There is a total of " + commandLookupMap.size() + " command lookups:");

        commandLookupMap.forEach((commandLookup, command) -> {
            output.info("- " + commandLookup + " -> " + command.getGanyuCommand().getClass().getName() + "#" + command.getMethod().getName() + "()");
        });

        return CommandResult.success();
    }
}
