package EventsAndCommands;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import EventsAndCommands.FunEvents.GoodBotEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * CommandHandler
 */
public class ChatEventDistributor extends ListenerAdapter{

    private static ChatEventDistributor instance = null;

    private Set<ChatEventHandler> eventHandlers;

    public static ChatEventDistributor getInstance(){
        if(instance == null){
            instance = new ChatEventDistributor();
        }
        return instance;
    }

    private ChatEventDistributor(){
        eventHandlers = new HashSet<>();
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