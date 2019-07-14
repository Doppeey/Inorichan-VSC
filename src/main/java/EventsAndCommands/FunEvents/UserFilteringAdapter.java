package EventsAndCommands.FunEvents;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.*;

public class UserFilteringAdapter extends ListenerAdapter {

    private Map<String, Boolean> userIDs;
    private Set<String> bannedWords;

    private final static String FILTER_ON = ">spamfilter on";
    private final static String FILTER_OFF = ">spamfilter off";

    private final static String FILTER_MESSAGE = "Anti spam measures are now: ";

    private boolean filterIsOn = false;

    public UserFilteringAdapter() {
        userIDs = new HashMap();
        bannedWords = new HashSet();

        userIDs.put("431542303346262039", false); //scholfz
        userIDs.put("301485597925703683", false); //Ayresia

        bannedWords.addAll(Arrays.asList("opop", "xd", "lmao", "lol", "rofl", "eksd", "dx", "oga"));
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (!event.getGuild().getRolesByName("Moderator", true).isEmpty()
                && setFilterStatus(event.getMessage().getContentRaw())) {
            event.getChannel().sendMessage(FILTER_MESSAGE + isSpamFilterActiveAsString()).queue();
        } else {
            if (filterIsOn && userIDs.containsKey(event.getAuthor().getId())
                    && isMessageToDelete(event.getMessage().getContentRaw())) {
                event.getMessage().delete().queue();
            }
        }
    }

    /**
     * This function checks to see if there is any banend word in the message.
     * @param messageToCheck the message you want to check.
     * @return returns true if the message contains a banned word
     */
    private boolean isMessageToDelete(String messageToCheck) {
        messageToCheck = messageToCheck.toLowerCase();
        return !bannedWords.stream().anyMatch(messageToCheck::contains);
    }

    /**
     * Checks to see if the message is equal to one of the commands and sets the filter accordingly.
     * @param messageToCompare the message to check
     * @return returns true if the filter status was changed; false if it remains equal
     */
    private boolean setFilterStatus(String messageToCompare) {
        switch (messageToCompare.toLowerCase()) {
            case FILTER_ON:
                filterIsOn = true;
                return true;
            case FILTER_OFF:
                filterIsOn = false;
                return true;
            default:
                return false;
        }
    }

    public String isSpamFilterActiveAsString() {
        return filterIsOn ? "activated" : "deactivated";
    }
}
