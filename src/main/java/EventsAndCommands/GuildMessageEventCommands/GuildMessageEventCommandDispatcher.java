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
     * @param event
     */
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        //Todo Create a check to maybe call different functions if the command has no infix/parameters.
        //Example: ">help" and ">help roles"

        String command = event.getMessage().getContentRaw().split(Command.getCommandInfix())[0];
        if(!command.isEmpty() && getRegisteredCommands().containsKey(command)){
            getRegisteredCommands().get(command).executeCommand(event);
        }
    }

    private String addInfixToCommand(String command){
        return command + Command.getCommandInfix();
    }

}

