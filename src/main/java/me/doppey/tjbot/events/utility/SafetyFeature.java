package me.doppey.tjbot.events.utility;

import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * SafetyFeature
 */
public class SafetyFeature extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {

        Role roleById = event.getGuild().getRoleById("359136657435394049");
        
        if (event.getMember().getRoles().contains(roleById)) {

            event.getChannel().getMessageById(event.getMessageId()).queue(success -> {

                
                if (success.getContentRaw().toLowerCase().contains(roleById.getName().toLowerCase())) {
                    if (event.getReactionEmote().getId().equals("312735215829385216")) {

                        success.delete().queue();
                    }
                }

            });

        }
    }

}