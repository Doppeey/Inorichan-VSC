package EventsAndCommands;


import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;


/**
 * This class provides the base structure of a chatevent that might be handled. 
 * To implement a new ChatEventHandler it is sufficient to just inherit from this 
 * Class and override {@see ChatEventHandler#trigger(GuildMessageReceivedEvent)}
 * and {@see ChatEventHandler#receiveCommand(GuildMessageReceivedEvent)}.
 */
public abstract class ChatEventHandler {

    /**
     * This method is executed when the command got triggered trough {@see ChatEventHandler#trigger(GuildMessageReceivedEvent)}.
     * This method should only handle what happens after the command was triggered, i.e. responses or whatever,
     * @param event the message that triggered the event.
     */
    public abstract void execute(GuildMessageReceivedEvent event);

    /**
     * Determines if an event would trigger the command. If the given event evaluates to true, 
     * {@see ChatEventHandler#receiveCommand(GuildMessageReceivedEvent)} gets called.
     * @param event The event that might trigger the command.
     * @return true if the the command is triggered, false if not.
     */
    public abstract boolean isTriggered(GuildMessageReceivedEvent event);
}