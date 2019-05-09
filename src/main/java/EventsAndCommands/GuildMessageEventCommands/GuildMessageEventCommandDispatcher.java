package EventsAndCommands.GuildMessageEventCommands;

import EventsAndCommands.Command;
import EventsAndCommands.CommandDispatcher;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

/**
 * This implementation takes care of Dispatching Commands that listen to {@link GuildMessageReceivedEvent}
 */
public class GuildMessageEventCommandDispatcher extends CommandDispatcher<GuildMessageEventCommand> {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String command = event.getMessage().getContentRaw().split(Command.getCommandInfix())[0];
        if(!command.isEmpty() && getRegisteredCommands().containsKey(command)){
            getRegisteredCommands().get(command).executeCommand(event);
        }
    }

}

