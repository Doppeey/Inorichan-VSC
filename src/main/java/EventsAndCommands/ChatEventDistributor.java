package EventsAndCommands;

import java.util.*;
import java.util.stream.Collectors;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * CommandHandler
 */
public class ChatEventDistributor extends ListenerAdapter{

    private static ChatEventDistributor instance = null;

    private List<ChatEventHandler> eventHandlers;

    public static ChatEventDistributor getInstance(){
        if(instance == null){
            instance = new ChatEventDistributor();
        }
        return instance;
    }

    private ChatEventDistributor(){
        eventHandlers = new ArrayList<>();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        eventHandlers.stream()
            .filter(x -> x.trigger(event))
            .forEach(x -> x.receiveCommand(event));
    }

    public void registerChatEventHandler(ChatEventHandler handler){
        eventHandlers.add(handler);
    }

    public void unregisterChatEventHandler(ChatEventHandler handler){
        eventHandlers.remove(handler);
    }

}