package EventsAndCommands;


import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;


/**
 * Command
 */
public abstract class ChatEventHandler {

    public ChatEventHandler(){
        register();
    }
    public abstract void receiveCommand(GuildMessageReceivedEvent event);
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