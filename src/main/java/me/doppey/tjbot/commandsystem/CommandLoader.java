package me.doppey.tjbot.commandsystem;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.mongodb.client.MongoDatabase;

import org.reflections.Reflections;

/**
 * Autodetects commands that implement a generic type T. Can be used to
 * dynamically load up all commands without explicitly instantiating them.
 * Injects needed dependencies into the instances individually.
 * 
 * @param <T> The superclass whose subclasses should be loaded
 */
public class CommandLoader<T> {

    private List<T> loadedClasses = new ArrayList<>();
    private Class<T> token;
    private MongoDatabase db;
    private Properties config;
    private EventWaiter waiter;

    /**
     * Ctor that takes in the surounding dependencies and a token that represents
     * the loaded class.
     * 
     * @param token  A reflection Class that represents the class that will be
     *               loaded.
     * @param db     A connection to a MongoDB.
     * @param config The read in properties from some cfg file.
     * @param waiter A waiter that is needed by some commands.
     */
    public CommandLoader(Class<T> token, MongoDatabase db, Properties config, EventWaiter waiter) {
        setDb(db);
        setConfig(config);
        setWaiter(waiter);
        setToken(token);
    }

    /**
     * Dynamically instanciates all Classes that inherit from type T. But ignores classes that are annotated with {@link IgnoreCommand}
     */
    public void loadClasses() {
        Reflections reflections = new Reflections(this.getClass().getPackageName());

        Set<Class<? extends T>> cmds = reflections.getSubTypesOf(token).stream()
                .filter(x -> !x.isAnnotationPresent(IgnoreCommand.class)).collect(Collectors.toSet());

        List<T> instances = new ArrayList<>();

        for (var cmd : cmds) {
            try {
                T instance = inject(cmd);
                if (instance != null) {
                    instances.add(instance);
                } else {
                    System.err.println("No suitable constructor found for " + cmd.getName());
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
                System.err.println("Could not load " + cmd.getName());
            }
        }

        setLoadedClasses(instances);
    }

    /**
     * Innstances the class cl with dependencies injected.
     * 
     * @param cl A reflective Class that needs to be instanced.
     * @return A concrete object derived from cl. Or null if no suitable constructor
     *         was found.
     * @throws ReflectiveOperationException gets thrown if something goes wrong with reflective
     *                   operations.
     */
    private T inject(Class<? extends T> cl) throws ReflectiveOperationException {
        Constructor<? extends T> ctor = findFittingConstructor(cl);

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

    /**
     * Finds the ctor with the most args that fits the Dependency Injection
     * criteria. The constructor needs to be empty or have any combination of the
     * following parametertypes: {{@see MongoDatabase}, {@see Properties},
     * {@see EventWaiter}}.
     * 
     * @param cl The class that has constructors.
     * @return The best fitting constructor.
     */
    private Constructor<? extends T> findFittingConstructor(Class<? extends T> cl) {
        final Set<Class<?>> acceptableParams = new HashSet<>();
        acceptableParams.add(MongoDatabase.class);
        acceptableParams.add(Properties.class);
        acceptableParams.add(EventWaiter.class);

        List<Constructor<?>> ctors = Arrays.asList(cl.getConstructors());
        // Might need reverse order, but no Command there to test yet
        ctors.sort(Comparator.comparingInt(Constructor::getParameterCount));
        Constructor<? extends T> fitting = null;

        for (var ctor : ctors) {
            Class<?>[] params = ctor.getParameterTypes();
            if (acceptableParams.containsAll(Arrays.asList(ctor.getParameterTypes()))) {
                try {
                    return cl.getConstructor(params);
                } catch (NoSuchMethodException ignore) {
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        }

        return fitting;
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