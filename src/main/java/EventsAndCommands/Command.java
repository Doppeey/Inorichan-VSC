package EventsAndCommands;

import net.dv8tion.jda.core.events.Event;

/**
 * This class is the top level superclass for all commands.
 * It is intended to have a value assigned to it that <b>extends</b> {@link Event}.
 *
 * The function {@link #executeCommand(Event)} is used for the Dispatcher - its the entry point for any event.
 * The functionality and build of the dispatcher requires {@link #getFullCommand()} to check if a command was called.
 *
 * @param <T> has to extend {@link Event}
 */
public abstract class Command<T extends Event>{
    private static String COMMAND_PREFIX = "";
    private static String COMMAND_INFIX = " ";

    public abstract void executeCommand(T event);

    public abstract String getFullCommand();

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
