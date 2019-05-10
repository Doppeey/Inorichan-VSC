package EventsAndCommands.GuildMessageEventCommands;

import EventsAndCommands.Command;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;


/**
 * This class inherits from {@link Command} and sets the type to {@link GuildMessageReceivedEvent}.
 * It offers the user the ability to add a required Role and/or a required channel for the command to trigger.
 *
 * After an Event has been recognized to be destined for this Command, it will first call {@link #executeCommand(GuildMessageReceivedEvent)}
 * where the Channel and user is checked. Afzerwards, it calls the function {@link #parseCommand(String, GuildMessageReceivedEvent)}
 * with the raw Message <b>without</b> the command <b>and</b> the event itself.
 *
 * This class also supports descriptions, altho nothing is done with them as of now.
 */
public abstract class GuildMessageEventCommand extends Command<GuildMessageReceivedEvent> {

    private final String roleNeeded;
    private final String requiredChannel;

    /**
     * Setup the command
     * @param command the command itself.
     * @param commandDescription the description of the command
     */
    public GuildMessageEventCommand(String command, String commandDescription){
        this(command, commandDescription, "", "");
    }

    /**
     * Setup the command
     * @param command the command itself.
     * @param commandDescription the description of the command
     * @param roleNeeded the role needed. You can leave this as "" if you want no role to be required
     * @param requiredChannel the channel required. You can leave this as "" if you want no channel to be required
     */
    public GuildMessageEventCommand(String command, String commandDescription, String roleNeeded, String requiredChannel){
        super(command, commandDescription);
        this.roleNeeded = roleNeeded;
        this.requiredChannel = requiredChannel;
    }

    @Override
    public void executeCommand(GuildMessageReceivedEvent event){
        if(checkIfChannelIsCorrect(event) && checkIfRoleIsCorrect(event)){
            parseCommand(event.getMessage().getContentRaw().replace(getFullCommand(), ""), event);
        }
    }

    /**
     * Use this to parse the command and do your desired actions.
     * @param message the raw message <b>without</b> the <b>full</b> command. See {@link #getFullCommand()} for it.
     * @param event the event itself
     */
    protected abstract void parseCommand(String message, GuildMessageReceivedEvent event);

    private boolean checkIfChannelIsCorrect(GuildMessageReceivedEvent event){
        return requiredChannel.isEmpty() || event.getChannel().getId().equalsIgnoreCase(requiredChannel);
    }

    private boolean checkIfRoleIsCorrect(GuildMessageReceivedEvent event){
        return roleNeeded.isEmpty() || !event.getGuild().getRolesByName(roleNeeded, true).isEmpty();
    }

}
