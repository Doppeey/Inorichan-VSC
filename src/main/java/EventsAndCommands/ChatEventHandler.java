package EventsAndCommands;


import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;


/**
 * Command
 */
public abstract class ChatEventHandler {

    public ChatEventHandler(){
        register();
    }
    /**
     * This method is executed when the command got triggered trough {@see ChatEventHandler#trigger(GuildMessageReceivedEvent)}.
     * This method should only handle what happens after the command was triggered, i.e. responses or whatever,
     * @param event the message that triggered the event.
     */
    public abstract void receiveCommand(GuildMessageReceivedEvent event);

    /**
     * Determines if an event would trigger the command. If the given event evaluates to true, {@see ChatEventHandler#receiveCommand(GuildMessageReceivedEvent)}
     * gets called.
     * @param event The event that might trigger the command.
     * @return true if the the command is triggered, false if not.
     */
    public abstract boolean trigger(GuildMessageReceivedEvent event);

    protected void register(){
        ChatEventDistributor.getInstance().registerChatEventHandler(this);
    }

    protected void unregister(){
        ChatEventDistributor.getInstance().unregisterChatEventHandler(this);
    }

    @Override
    protected void finalize() throws Throwable {
        unregister();
    }


}