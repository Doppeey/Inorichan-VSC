package EventsAndCommands;

import java.util.List;
import java.util.function.Predicate;

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


}