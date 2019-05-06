package EventsAndCommands.FunEvents;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class UserFilteringAdapter extends ListenerAdapter {

    private HashMap<String, Boolean> userIDs;
    private HashSet<String> bannedWords;

    private final String FILTER_ON = ">spamfilter on";
    private final String FILTER_OFF = ">spamfilter on";

    private final String FILTER_MESSAGE = "Anti spam measurements are now: ";

    private boolean filterIsOn = false;

    public UserFilteringAdapter(){
        //setup of basic Filters needed
        userIDs = new HashMap<String, Boolean>();
        bannedWords = new HashSet<String>();

        userIDs.put("431542303346262039", false); //scholfz
        userIDs.put("301485597925703683", false); //Ayresia

        bannedWords.addAll(Arrays.asList("opop", "xd", "lmao", "lol", "rofl", "eksd", "dx", "oga"));
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        try {
            if (!event.getGuild().getRolesByName("Moderator", true).isEmpty()) {
                if (event.getMessage().getContentRaw().equalsIgnoreCase(FILTER_ON)) {
                    filterIsOn = true;
                } else if (event.getMessage().getContentRaw().equalsIgnoreCase(FILTER_OFF)) {
                    filterIsOn = false;
                }
                event.getChannel().sendMessage(FILTER_MESSAGE + isSpamFilterActiveAsString()).queue();
            }
        } catch (NullPointerException e) {
            // user has no roles
        }

        if (userIDs.containsKey(event.getAuthor().getId()) && filterIsOn) {
            for (String s : bannedWords) {
                if (event.getMessage().getContentRaw().toLowerCase().contains(s)) {
                    event.getMessage().delete().queue();
                    break;
                }
            }
        }
    }

    public String isSpamFilterActiveAsString(){
        return filterIsOn ? "activated" : "deactivate";
    }
}
