package EventsAndCommands.UtilityCommands;

import EventsAndCommands.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.awt.*;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class QuoteCommand extends Command {
    String mention;
    MessageChannel quoteChannel;

    public QuoteCommand() {
        this.name = "quote";
        this.help = ">quote [message_id] [caption (optional)] to quote a message";
        this.category = Categories.Utility;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        String args = commandEvent.getArgs();
        String messageId = args.substring(0, 18);
        String caption = args.length() >= 19 ? args.substring(19) : "";
        final boolean hasAMentionedChannel = !commandEvent.getMessage().getMentionedChannels().isEmpty();

        if (hasAMentionedChannel) {
            quoteChannel = commandEvent.getMessage().getMentionedChannels().get(0);
            mention = commandEvent.getMessage().getMentionedChannels().get(0).getAsMention();
        } else {
            quoteChannel = commandEvent.getChannel();
        }

        try {

            quoteChannel.getMessageById(messageId).queue(x -> {

                final OffsetDateTime creationTime = x.getCreationTime();
                final Member member = x.getMember();
                EmbedBuilder embed = new EmbedBuilder();

                if(x.getContentRaw().isEmpty()){ // when the user quotes a quote or something else that makes no sense like those messages with no messages whatsoever
                    embed.setDescription("you can't do that!");
                    embed.setColor(Color.RED);
                    commandEvent.reply(embed.build(), success -> commandEvent.getMessage().delete().queue());
                    return;
                }

                embed.setDescription(x.getContentRaw() + "\n \n [[Jump to message]](" + x.getJumpUrl() + ")  ");
                if (caption.isEmpty()) {
                    embed.appendDescription("Creation time:  " + creationTime.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE) + "\n[Quoted by " + commandEvent.getMember().getAsMention() + "]");
                } else if (caption.length() > MessageEmbed.TEXT_MAX_LENGTH) { // discord won't allow more than 2000, but it's better to make sure just in case.
                    embed.appendDescription("Creation time:  " + creationTime.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE) + "\n[Quoted by " + commandEvent.getMember().getAsMention() + "]\n your caption is too long (longer than 2048 characters chill for a sec)");
                } else {
                    embed.appendDescription("Creation time:  " + creationTime.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE)
                            + "\nQuoted by " + commandEvent.getMember().getAsMention() + " with the caption:\n" + caption);
                }

                try {
                    boolean hasImage = x.getAttachments().get(0).isImage();
                    if (hasImage) {
                        embed.setImage(x.getAttachments().get(0).getUrl());
                    }
                } catch (Exception e) {
                    // Nothing, is not a picture
                }

                embed.setAuthor(member.getEffectiveName(), null, x.getAuthor().getEffectiveAvatarUrl());
                embed.setColor(Color.ORANGE);
                commandEvent.reply(embed.build(), success -> commandEvent.getMessage().delete().queue());

            });
        } catch (Exception e) {
            commandEvent.reply("Couldn't quote your message, if it's from another channel make sure to mention that channel");
        }

    }
}