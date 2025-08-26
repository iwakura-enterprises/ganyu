package enterprises.iwakura.ganyu;

import java.util.List;

/**
 * Interface for processing command registration. Should register commands based on their annotations in the <code>enterprises.iwakura.annotation</code> package.
 */
public interface CommandRegisterProcessor {

    /**
     * Processes the given command and registers it with the provided Ganyu instance.
     *
     * @param ganyu the Ganyu instance to register commands with
     * @param command the command to process
     *
     * @return a list of registered commands, each of them being level-one commands
     */
    List<RegisteredCommand> process(Ganyu ganyu, GanyuCommand command);

}
