package EventsAndCommands;

import net.dv8tion.jda.core.events.Event;

/**
 * This class is the top level superclass for all commands.
 * It is intended to have a value assigned to it that <b>extends</b> {@link Event}.
 *
 * The function {@link #executeCommand(Event)} is used for the Dispatcher - its the entry point for any event.
 * The functionality and build of the dispatcher requires {@link #getFullCommand()} to check if a command was called.
 *
 * The setup of the command itself follows trough its name, for example "spamfilter".
 * You can then use the function {@link #setCommandPrefix(String)} to set the Prefix, for example ">".
 * Using the function {@link #setCommandInfix(String)} you can set what is expected after the command. For example,
 * if you want there to be a space after the command, you can set " " as an Infix. The command returned by {@link #getFullCommand()}
 * would then be ">spamfilter ".
 *
 * The standard Prefix is {@value COMMAND_PREFIX} and the standard Infix is {@value COMMAND_INFIX}
 *
 * @param <T> has to extend {@link Event}
 */
public abstract class Command<T extends Event>{
    private static String COMMAND_PREFIX = "";
    private static String COMMAND_INFIX = " ";
    private final String command;
    private final String commandDescription;

    public Command(String command, String commandDescription){
        this.command = command;
        this.commandDescription = commandDescription;
    }

    public abstract void executeCommand(T event);

    /**
     * Use this to parse the command and do your desired actions.
     * @param message the raw message <b>without</b> the <b>full</b> command. See {@link #getFullCommand()} for it.
     * @param event the event itself
     */
    protected abstract void parseCommand(String message, T event);

    /**
     * Returns the full Command. The composition of it is:
     * <b></b>COMMAND_PREFIX + command + COMMAND_INFIX</b>
     * @return
     */
    public String getFullCommand(){
        return COMMAND_PREFIX + command + COMMAND_INFIX;
    }

    /**
     * Returns the partial Command. This can be used to check for commands that require no parameters, for
     * example ">help"
     * @return
     */
    public String getCommandWithoutInfix(){
        return COMMAND_PREFIX + command;
    }
    /**
     * Contrary to {@link #getFullCommand()}, this returns only the name of the command.
     * Example: FullCommand is ">spamfilter ", {@link #getCommand()} return "spamfilter"
     * @return
     */
    public String getCommand(){
        return command;
    }

    public String getCommandDescription(){
        return commandDescription;
    }

    public static void setCommandPrefix(String commandPrefix){
        COMMAND_PREFIX = commandPrefix;
    }

    public static void setCommandInfix(String commandInfix){
        COMMAND_INFIX = commandInfix;
    }

    public static String getCommandPrefix(){
        return COMMAND_PREFIX;
    }

    public static String getCommandInfix(){
        return COMMAND_INFIX;
    }



}
