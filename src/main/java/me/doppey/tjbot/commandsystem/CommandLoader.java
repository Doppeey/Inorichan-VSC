package me.doppey.tjbot.commandsystem;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.mongodb.client.MongoDatabase;
import me.doppey.tjbot.Config;
import me.doppey.tjbot.InoriChan;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Auto-detects and loads commands that implement a generic type T. Can be used to dynamically load up all commands
 * without explicitly instantiating them. Injects needed dependencies into the instances individually.
 *
 * @param <T> The superclass whose subclasses should be loaded.
 */
public class CommandLoader<T> {
    private Set<T> loadedClasses = Set.of();
    private Class<T> token;
    private MongoDatabase database;
    private Config config;
    private EventWaiter waiter;

    /**
     * Creates a {@link CommandLoader} that takes in the surrounding dependencies and a token that represents the
     * classes to load.
     *
     * @param token A reflection {@link Class} that represents the superclass to load all subclasses from.
     * @param database A connection to a {@link MongoDatabase}.
     * @param config The configuration for the bot.
     * @param waiter An {@link EventWaiter} that is needed by some commands.
     */
    public CommandLoader(Class<T> token, MongoDatabase database, Config config, EventWaiter waiter) {
        this.token = token;
        this.database = database;
        this.config = config;
        this.waiter = waiter;
    }

    /**
     * Dynamically instantiates all classes that inherit from type T,
     * ignoring classes that are annotated with {@link IgnoreCommand}.
     *
     * @return An unmodifiable set of instantiated objects derived from the superclass {@code T}.
     */
    public Set<T> loadClasses() {
        Reflections reflections = new Reflections(InoriChan.class.getPackageName());
        String masterPack = InoriChan.class.getPackageName();

        Set<Class<? extends T>> cmds = reflections.getSubTypesOf(token).stream()
                .filter(clazz -> !clazz.isAnnotationPresent(IgnoreCommand.class) && clazz.getPackageName().startsWith(masterPack))
                .collect(Collectors.toSet());

        Set<T> instances = new HashSet<>();

        for (var cmd : cmds) {
            try {
                T instance = inject(cmd);
                if (instance != null) {
                    instances.add(instance);
                } else {
                    InoriChan.LOGGER.error("No suitable constructor found for {}", cmd.getName());
                }
            } catch (Exception e) {
                InoriChan.LOGGER.error("Could not load {}", cmd.getName(), e);
            }
        }

        this.loadedClasses = Collections.unmodifiableSet(instances);
        return loadedClasses;
    }

    /**
     * Instantiates the class provided with dependencies injected.
     *
     * @param clazz A reflective {@link Class} that needs to be instanced.
     * @return An instance of the class provided if a suitable constructor was found, null otherwise.
     * @throws ReflectiveOperationException Thrown if something goes wrong with reflective operations.
     */
    private T inject(Class<? extends T> clazz) throws ReflectiveOperationException {
        Constructor<? extends T> ctor = findFittingConstructor(clazz);
        if (ctor == null) {
            return null;
        }

        if (ctor.getParameterCount() == 0) {
            return ctor.newInstance();
        }

        Class<?>[] paramTypes = ctor.getParameterTypes();
        Object[] parameters = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> type = paramTypes[i];
            if (type.isAssignableFrom(database.getClass())) {
                parameters[i] = database;
            } else if (type.isAssignableFrom(config.getClass())) {
                parameters[i] = config;
            } else if (type.isAssignableFrom(waiter.getClass())) {
                parameters[i] = waiter;
            }
        }

        return ctor.newInstance(parameters);
    }

    /**
     * Finds the constructor with the most arguments that fits the Dependency Injection criteria. The constructor
     * needs to be empty or have any combination of the following parameter types: {{@link MongoDatabase},
     * {@link Config}, {@link EventWaiter}}.
     *
     * @param clazz The class to find a suitable constructor for.
     * @return The best fitting constructor, or null if none if found.
     */
    private Constructor<? extends T> findFittingConstructor(Class<? extends T> clazz) {
        final Set<Class<?>> acceptableParams = Set.of(MongoDatabase.class, Config.class, EventWaiter.class);

        List<Constructor<? extends T>> ctors = Arrays.asList((Constructor<? extends T>[]) clazz.getConstructors());
        // Might need reverse order, but no Command there to test yet
        ctors.sort(Comparator.comparingInt(Constructor::getParameterCount));

        for (var ctor : ctors) {
            if (acceptableParams.containsAll(Arrays.asList(ctor.getParameterTypes()))) {
                try {
                    return ctor;
                } catch (SecurityException e) {
                    InoriChan.LOGGER.error(e.getMessage(), e);
                }
            }
        }

        try {
            return clazz.getDeclaredConstructor(); //return empty constructor
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * Returns an unmodifiable set of instantiated objects derived from the superclass {@code T}. This set is empty if
     * {@link #loadClasses()} has not been called yet.
     *
     * @return An unmodifiable set of instantiated objects derived from the superclass {@code T}.
     */
    public Set<T> getLoadedClasses() {
        return loadedClasses;
    }
}