package EventsAndCommands.GuildMessageEventCommands;

import EventsAndCommands.Command;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;


/**
 * This class inherits from {@link Command} and sets the type to {@link GuildMessageReceivedEvent}.
 * It offers the user the ability to add a required Role and/or a required channel for the command to trigger.
 *
 * The setup of the command itself follows trough its name, for example "spamfilter".
 * You can then use the function {@link #setCommandPrefix(String)} to set the Prefix, for example ">".
 * Using the function {@link #setCommandInfix(String)} you can set what is expected after the command. For example,
 * if you want there to be a space after the command, you can set " " as an Infix. The command returned by {@link #getFullCommand()}
 * would then be ">spamfilter ".
 *
 * After an Event has been recognized to be destined for this Command, it will first call {@link #executeCommand(GuildMessageReceivedEvent)}
 * where the Channel and user is checked. Afzerwards, it calls the function {@link #parseCommand(String, GuildMessageReceivedEvent)}
 * with the raw Message <b>without</b> the command <b>and</b> the event itself.
 *
 * This class also supports descriptions, altho nothing is done with them as of now.
 */
public abstract class GuildMessageEventCommand extends Command<GuildMessageReceivedEvent> {

    private final String command;
    private final String commandDescription;
    private final String roleNeeded;
    private final String requiredChannel;

    private static String COMMAND_PREFIX = "";
    private static String COMMAND_INFIX = " ";

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
        this.command = command;
        this.commandDescription = commandDescription;
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

    /**
     * Returns the full Command. The composition of it is:
     * <b></b>COMMAND_PREFIX + command + COMMAND_INFIX</b>
     * @return
     */
    @Override
    public String getFullCommand(){
        return COMMAND_PREFIX + command + COMMAND_INFIX;
    }

    public String getCommandDescription(){
        return commandDescription;
    }

    /**
     * Contrary to {@link #getFullCommand()}, this returns only the name of the command.
     * Example: FullCommand is ">spamfilter ", {@link #getCommand()} return "spamfilter"
     * @return
     */
    public String getCommand(){
        return command;
    }

    public static void setCommandPrefix(String commandPrefix){
        COMMAND_PREFIX = commandPrefix;
    }

    public static void setCommandInfix(String commandInfix){
        COMMAND_INFIX = commandInfix;
    }

}
