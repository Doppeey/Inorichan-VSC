package EventsAndCommands;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * The CommandDispatcher class manages Commands that extend {@link Command}.
 * It uses a HashMap to store them all and uses their full command as key.
 *
 * This class itself is abstract to block any way of using it as an object, as it would nothing on its own.
 *
 * Usage is as follows:
 * Extend {@link CommandDispatcher} and Override one of the event calls of {@link ListenerAdapter}.
 * Then, use the hashMap like in {@link EventsAndCommands.GuildMessageEventCommands.GuildMessageEventCommandDispatcher#onGuildMessageReceived(GuildMessageReceivedEvent)}.
 * Or use the following example:
 *
 * <pre>
 * {@code
 * @Override
 *     public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
 *         String rawContent = event.getMessage().getContentRaw();
 *         getRegisteredCommands().forEach((k, v) ->{
 *             if(rawContent.startsWith(k)){
 *                 v.executeCommand(event);
 *                 return;
 *             }
 *         });
 *     }
 * }
 * </pre>
 *
 * As you can see, you can use {@link #getRegisteredCommands()} to search trough the map, and use <pre>forEach( (k, v) ->{ }</pre>
 * to look for the right {@link Command} before calling {@link Command#executeCommand(net.dv8tion.jda.core.events.Event)} on it.
 *
 * @param <T> has to extend {@link Command}
 */
public abstract class CommandDispatcher<T extends Command> extends ListenerAdapter {

    private Map<String, T> registeredCommands;

    public CommandDispatcher(){
        registeredCommands = new HashMap<>();
    }

    protected Map<String, T> getRegisteredCommands(){
        return registeredCommands;
    }

    public void addCommand(T commandToAdd){
        registeredCommands.put(commandToAdd.getFullCommand(), commandToAdd);
    }

    public void addCommands(T ...commandsToAdd){
        Arrays.stream(commandsToAdd).forEach(e -> registeredCommands.put(e.getCommandWithoutInfix(), e));
    }

    public void removeCommand(T commandToRemove){
        registeredCommands.remove(commandToRemove);
    }

    public void removeCommandByString(T commandToRemove){
        registeredCommands.remove(commandToRemove.getFullCommand());
    }
}
