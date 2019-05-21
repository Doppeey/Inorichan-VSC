package me.doppeey.tjbot.events.fun;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class AntiScholzEvent extends ListenerAdapter {

    private String scholzID = "431542303346262039";
    private String[] bannedWords = { "opop", "xd", "lmao", "lol", "rofl", "eksd", "dx" };
    private boolean filterIsOn = false;

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        try {
            if (event.getMember().getRoles().contains(event.getGuild().getRolesByName("Moderator", true).get(0))) {

                if (event.getMessage().getContentRaw().equalsIgnoreCase(">scholzfilter on")) {
                    filterIsOn = true;
                    event.getChannel().sendMessage("Anti scholz measures activated").queue();
                } else if (event.getMessage().getContentRaw().equalsIgnoreCase(">scholzfilter off")) {
                    filterIsOn = false;
                    event.getChannel().sendMessage("Anti scholz measures deactivated").queue();
                }

            }
        } catch (NullPointerException e) {
            // user has no roles
        }

        if (event.getAuthor().getId().equalsIgnoreCase(scholzID) && filterIsOn) {

            for (String s : bannedWords) {
                if (event.getMessage().getContentRaw().toLowerCase().contains(s)) {
                    event.getMessage().delete().queue();
                    break;
                }

            }

        }

    }
}
