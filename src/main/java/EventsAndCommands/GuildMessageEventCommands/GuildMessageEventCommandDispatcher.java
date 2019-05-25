package EventsAndCommands.GuildMessageEventCommands;

import EventsAndCommands.Command;
import EventsAndCommands.CommandDispatcher;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

/**
 * This implementation takes care of Dispatching Commands that listen to {@link GuildMessageReceivedEvent}
 */
public class GuildMessageEventCommandDispatcher extends CommandDispatcher<GuildMessageEventCommand> {

    /**
     * This function checks to see if an existing command matches to the keyword.
     *
     * @param event
     */
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        String contentRaw = event.getMessage().getContentRaw();
        if (contentRaw.split(Command.getCommandInfix()).length != 0) {
            getRegisteredCommands().get(contentRaw.split(Command.getCommandInfix())[0]).executeCommand(event);
        } else if (getRegisteredCommands().containsKey(contentRaw)) {
            getRegisteredCommands().get(contentRaw).executeCommand(event);
        }

    }

}

