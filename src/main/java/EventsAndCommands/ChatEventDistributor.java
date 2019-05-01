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

import org.reflections.*;

/**
 * CommandHandler
 */
public class ChatEventDistributor extends ListenerAdapter{

    private static ChatEventDistributor instance;

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
        for (var x : eventHandlers) {
            if(x.isTriggered(event)){
                x.execute(event);
            }
        }
    }

    public void registerChatEventHandler(ChatEventHandler handler){
        eventHandlers.add(handler);
    }

    public void unregisterChatEventHandler(ChatEventHandler handler){
        eventHandlers.remove(handler);
    }

    public void load(Class<?> c){
        Reflections reflections = new Reflections(this.getClass().getPackageName());

        var handlers = reflections.getSubTypesOf(c);

        for (var handler : handlers) {
            try{
                Constructor<?> ctor = handler.getConstructor();
                ctor.newInstance();
            } catch (Exception e){
                // TODO: Handle
            }
        }
    }

}