package EventsAndCommands.UtilityEvents;

import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;


import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * UnspoilEvent
 */
public class UnspoilEvent extends ListenerAdapter {

    private final EventWaiter waiter;

    public UnspoilEvent(EventWaiter waiter) {

        this.waiter = waiter;

    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        final TextChannel testChannel = event.getGuild().getTextChannelById("503642609894424577");
        final String contentRaw = event.getMessage().getContentRaw();
        final String replyMessage = "I have detected a message with 3 or more spoilers, if you want me to reveal it, react with the magnifying glass";
        boolean hasABunchOfSpoilers; 

        // Only check the message if it contains at least one spoiler symbol.
        if (contentRaw.contains("|")) {

            int howManySpoilerTags = 0;

            /*
             * Add one to the spoiler counter, since each spoiler consists out of 4
             * "spoiler symbols", divide the resulting integer by 4. If the result of that
             * division is at least 3 we can assume that the message has 3 or more spoiler
             * and offer to un-spoil it
             */
            for (char c : contentRaw.toCharArray()) {

                if (c == '|') {
                    howManySpoilerTags++;
                }

            }
            hasABunchOfSpoilers = (howManySpoilerTags / 4) != 3;


            if (hasABunchOfSpoilers) {

                testChannel.sendMessage(replyMessage).queue(reply -> {

                    reply.addReaction("🔍").queue(reacted -> {

                        waiter.waitForEvent(MessageReactionAddEvent.class,
                                reaction -> reply.getId().equals(reply.getId())
                                        && !event.getJDA().getSelfUser().equals(reaction.getMember().getUser()),
                                success -> {

                                    final String unspoilered = contentRaw.replace("||", "");
                                    reply.editMessage("Here is the unspoilered message: \n" + unspoilered).queue();
                                    reply.clearReactions().queue();
                                }, 300, TimeUnit.SECONDS, null);
                    });
                });

            }

        }

    }
}