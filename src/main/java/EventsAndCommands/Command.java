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

    public abstract void executeCommand(T event);

    public abstract String getFullCommand();

}
