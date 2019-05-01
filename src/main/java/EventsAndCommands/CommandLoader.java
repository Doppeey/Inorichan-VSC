package EventsAndCommands;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import com.jagrosh.jdautilities.command.Command;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import org.reflections.*;
import com.mongodb.client.MongoDatabase;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

/**
 * CommandHandler
 */
public class CommandLoader {

    private List<Command> loadedCommands;
    private List<ListenerAdapter> loadedListeners;
    private MongoDatabase db;
    private Properties config;
    private EventWaiter waiter;

    public CommandLoader(MongoDatabase db, Properties config, EventWaiter waiter) {
        loadedCommands = new ArrayList<>();
        loadedListeners = new ArrayList<>();
        setDb(db);
        setConfig(config);
        setWaiter(waiter);
    }

    public void loadCommands() {
        Reflections reflections = new Reflections(this.getClass().getPackageName());

        Set<Class<? extends Command>> cmds = reflections.getSubTypesOf(Command.class);
        List<Command> instances = new ArrayList<>();

        for (var cmd : cmds) {
            List<Constructor<?>> ctors = Arrays.asList(cmd.getConstructors());
            for (var ctor : ctors) {
                if (hasAcceptableParams(ctor)) {
                    try {
                        instances.add(injectCommand(ctor));
                    } catch (Exception e) {
                        // TODO: Log
                        System.err.println("Could not load " + cmd.getName());
                    }
                } else {
                    System.err.println("No suitable constructor for " + cmd.getName());
                }
            }
        }

        setLoadedCommands(instances);
    }

    public void loadListenerAdaptor() {
        Reflections reflections = new Reflections(this.getClass().getPackageName());

        Set<Class<? extends ListenerAdapter>> cmds = reflections.getSubTypesOf(ListenerAdapter.class);
        List<ListenerAdapter> instances = new ArrayList<>();

        for (var cmd : cmds) {
            List<Constructor<?>> ctors = Arrays.asList(cmd.getConstructors());
            for (var ctor : ctors) {
                if (hasAcceptableParams(ctor)) {
                    try {
                        instances.add(injectListenerAdaptor(ctor));
                    } catch (Exception e) {
                        // TODO: Log
                        System.err.println("Could not load " + cmd.getName());
                    }
                } else {
                    System.err.println("No suitable constructor for " + cmd.getName());
                }
            }
        }

        setLoadedListeners(instances);
    }

    /**
     * 
     * @param ctors
     * @return
     */
    public ListenerAdapter injectListenerAdaptor(Constructor<?> ctor) throws Exception{
        Class<?>[] paramTypes = ctor.getParameterTypes();
        Object[] parameters = new Object[paramTypes.length];

        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> type = paramTypes[i];
            if (type.equals(db.getClass())) {
                parameters[i] = db;
            } else if (type.equals(config.getClass())) {
                parameters[i] = config;
            } else if (type.equals(waiter.getClass())) {
                parameters[i] = waiter;
            }
        }

        return (ListenerAdapter) ctor.newInstance(parameters);
    }   
    
    public Command injectCommand(Constructor<?> ctor) throws Exception{
        Class<?>[] paramTypes = ctor.getParameterTypes();
        Object[] parameters = new Object[paramTypes.length];

        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> type = paramTypes[i];
            if (type.equals(db.getClass())) {
                parameters[i] = db;
            } else if (type.equals(config.getClass())) {
                parameters[i] = config;
            } else if (type.equals(waiter.getClass())) {
                parameters[i] = waiter;
            }
        }

        return (Command) ctor.newInstance(parameters);
    }

    public boolean hasAcceptableParams(Constructor<?> ctor){
        final Set<Class<?>> acceptableParams = new HashSet<>();
        acceptableParams.add(MongoDatabase.class);
        acceptableParams.add(Properties.class);
        acceptableParams.add(EventWaiter.class);

        return acceptableParams.containsAll(Arrays.asList(ctor.getParameterTypes()));
    }

    /**
     * @return the eventHandlers
     */
    public List<Command> getLoadedCommands() {
        return loadedCommands;
    }

    /**
     * @param eventHandlers the eventHandlers to set
     */
    public void setLoadedCommands(List<Command> loadedCommands) {
        this.loadedCommands = loadedCommands;
    }

    /**
     * @return the db
     */
    public MongoDatabase getDb() {
        return db;
    }

    /**
     * @param db the db to set
     */
    public void setDb(MongoDatabase db) {
        this.db = db;
    }

    /**
     * @return the config
     */
    public Properties getConfig() {
        return config;
    }

    /**
     * @param config the config to set
     */
    public void setConfig(Properties config) {
        this.config = config;
    }

    /**
     * @return the waiter
     */
    public EventWaiter getWaiter() {
        return waiter;
    }

    /**
     * @param waiter the waiter to set
     */
    public void setWaiter(EventWaiter waiter) {
        this.waiter = waiter;
    }

    /**
     * @return the loadedListeners
     */
    public List<ListenerAdapter> getLoadedListeners() {
        return loadedListeners;
    }

    /**
     * @param loadedListeners the loadedListeners to set
     */
    public void setLoadedListeners(List<ListenerAdapter> loadedListeners) {
        this.loadedListeners = loadedListeners;
    }


    
}