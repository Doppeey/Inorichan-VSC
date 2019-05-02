package EventsAndCommands;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.mongodb.client.MongoDatabase;

import org.reflections.Reflections;

/**
 * CommandHandler
 */
public class CommandLoader<T> {

    private List<T> loadedClasses = new ArrayList<>();
    private Class<T> token;
    private MongoDatabase db;
    private Properties config;
    private EventWaiter waiter;

    public CommandLoader(Class<T> token, MongoDatabase db, Properties config, EventWaiter waiter) {
        setDb(db);
        setConfig(config);
        setWaiter(waiter);
        setToken(token);
    }

    /**
     * loads all classes via reflection and instanciates them once. Injects needed
     * dependencies in the constructor
     */
    public void loadClasses() {
        Reflections reflections = new Reflections(this.getClass().getPackageName());

        Set<Class<? extends T>> cmds = reflections.getSubTypesOf(token).stream()
                .filter(x -> !x.isAnnotationPresent(IgnoreCommand.class))
                .collect(Collectors.toSet());

        List<T> instances = new ArrayList<>();

        for (var cmd : cmds) {
            List<Constructor<?>> ctors = Arrays.asList(cmd.getConstructors());
            for (var ctor : ctors) {
                if (hasAcceptableParams(ctor)) {
                    try {
                        instances.add(inject(ctor));
                    } catch (InvocationTargetException e) {
                        e.getCause().printStackTrace();
                    } catch (Exception e) {
                        // TODO: Log
                        System.err.println(e.getMessage());
                        e.printStackTrace();
                        System.err.println("Could not load " + cmd.getName());
                    }
                } else {
                    System.err.println("No suitable constructor for " + cmd.getName());
                }
            }
        }

        setLoadedClasses(instances);
    }

    public T inject(Constructor<?> ctor) throws Exception {
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

        return (T) ctor.newInstance(parameters);
    }

    public boolean hasAcceptableParams(Constructor<?> ctor) {
        final Set<Class<?>> acceptableParams = new HashSet<>();
        acceptableParams.add(MongoDatabase.class);
        acceptableParams.add(Properties.class);
        acceptableParams.add(EventWaiter.class);

        return acceptableParams.containsAll(Arrays.asList(ctor.getParameterTypes()));
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
     * @return the token
     */
    public Class<T> getToken() {
        return token;
    }

    /**
     * @param token the token to set
     */
    public void setToken(Class<T> token) {
        this.token = token;
    }

    /**
     * @return the loadedClasses
     */
    public List<T> getLoadedClasses() {
        return loadedClasses;
    }

    /**
     * @param loadedClasses the loadedClasses to set
     */
    public void setLoadedClasses(List<T> loadedClasses) {
        this.loadedClasses = loadedClasses;
    }

}