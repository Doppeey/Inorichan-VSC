package me.doppey.tjbot.events.utility;

import me.doppey.tjbot.Constants;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * SafetyFeature
 */
public class SafetyFeature extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getMember().getRoles().contains(Constants.ILLUMINATI_ROLE)) {

            event.getChannel().getMessageById(event.getMessageId()).queue(success -> {

                
                if (success.getContentRaw().toLowerCase().contains(Constants.ILLUMINATI_ROLE.getName().toLowerCase())) {
                    if (event.getReactionEmote().getId().equals("312735215829385216")) {

                        success.delete().queue();
                    }
                }

            });

        }
    }

}