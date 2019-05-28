package EventsAndCommands.UtilityEvents;

import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
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

        final String contentRaw = event.getMessage().getContentRaw();
        final String replyMessage = "I have detected a message with more than 3 spoilers, react with the magnifying glass to reveal it";
        boolean hasABunchOfSpoilers;

        // Only check the message if it contains at least one spoiler symbol.
        if (contentRaw.contains("|")) {

            int howManySpoilerTags = 0;

            /*
             * Add one to the spoiler counter, since each spoiler consists out of 4
             * "spoiler symbols", divide the resulting integer by 4. If the result of that
             * division is at least 4 we can assume that the message has 4 or more spoiler
             * and offer to un-spoil it
             */
            for (char c : contentRaw.toCharArray()) {

                if (c == '|') {
                    howManySpoilerTags++;
                }

            }
            hasABunchOfSpoilers = (howManySpoilerTags / 4) > 3;

            if (hasABunchOfSpoilers) {

                event.getChannel().sendMessage(replyMessage).queue(reply -> {

                    reply.addReaction("\uD83D\uDD0D").queue(reacted -> {

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