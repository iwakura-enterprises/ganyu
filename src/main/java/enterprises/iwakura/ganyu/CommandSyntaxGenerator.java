package enterprises.iwakura.ganyu;

/**
 * Command syntax generator used to create command syntax strings for registered commands that do not specify custom syntax.
 */
public class CommandSyntaxGenerator {

    /**
     * Generates a command syntax string for the given registered command.
     *
     * @param command the command
     *
     * @return the command syntax string
     */
    public String generate(RegisteredCommand command) {
        StringBuilder syntax = new StringBuilder();
        syntax.append(command.getName());

        for (CommandArgumentDefinition argument : command.getArgumentDefinitions()) {
            if (argument.isInjectable()) {
                continue;
            }

            syntax.append(" ");

            if (argument.isMandatory()) {
                syntax.append("<");
            } else {
                syntax.append("[");
            }

            syntax.append(getArgumentName(argument));

            if (argument.isMandatory()) {
                syntax.append(">");
            } else {
                syntax.append("]");
            }
        }

        return syntax.toString();
    }

    private String getArgumentName(CommandArgumentDefinition argument) {
        if (argument.getLongName() != null && !argument.getLongName().isEmpty()) {
            return "--" + argument.getLongName();
        } else if (argument.getName() != null && !argument.getName().isEmpty()) {
            return argument.getName();
        } else {
            // May return something like "arg0", "arg1", etc. if @NamedArg is not used
            return argument.getParameterName();
        }
    }
}
