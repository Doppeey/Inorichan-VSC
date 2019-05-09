package EventsAndCommands.GuildMessageEventCommands;

import EventsAndCommands.CommandDispatcher;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

/**
 * This implementation takes care of Dispatching Commands that listen to {@link GuildMessageReceivedEvent}
 */
public class GuildMessageEventCommandDispatcher extends CommandDispatcher<GuildMessageEventCommand> {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String rawContent = event.getMessage().getContentRaw();
        getRegisteredCommands().forEach((k, v) ->{
            if(rawContent.startsWith(k)){
                v.executeCommand(event);
                return;
            }
        });
    }

}

