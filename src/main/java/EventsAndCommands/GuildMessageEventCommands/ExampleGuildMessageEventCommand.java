package EventsAndCommands.GuildMessageEventCommands;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

/**
 * This is an example implementation for @{@link GuildMessageEventCommand}.
 * Give it to a {@link GuildMessageEventCommandDispatcher} so it can be activated!
 */
public class ExampleGuildMessageEventCommand extends GuildMessageEventCommand{

    private String messageToSend = "Hello! :)";

    public ExampleGuildMessageEventCommand(){
        super("hello", "A friendly hello!");
    }
    @Override
    protected void parseCommand(String message, GuildMessageReceivedEvent event) {
        event.getChannel().sendMessage(messageToSend).queue();
    }
}
