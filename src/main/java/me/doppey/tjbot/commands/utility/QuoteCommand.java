package me.doppey.tjbot.commands.utility;

import me.doppey.tjbot.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.awt.*;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class QuoteCommand extends Command {
    private String mention;
    private String argsTrimmed;
    MessageChannel quoteChannel;

    public QuoteCommand() {
        this.name = "quote";
        this.help = ">quote [message_id] to quote a message";
        this.category = Categories.Utility;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        String args = commandEvent.getArgs();
        String messageId = args;
        boolean isFromDifferentChannel = false;
        final boolean hasAMentionedChannel = !commandEvent.getMessage().getMentionedChannels().isEmpty();

        if (hasAMentionedChannel) {
            quoteChannel = commandEvent.getMessage().getMentionedChannels().get(0);
            mention = commandEvent.getMessage().getMentionedChannels().get(0).getAsMention();
            messageId = args.replaceAll(mention, "").strip();

        } else {
            quoteChannel = commandEvent.getChannel();
        }

        try {


            quoteChannel.getMessageById(messageId).queue(x -> {

                final OffsetDateTime creationTime = x.getCreationTime();
                final Member member = x.getMember();

                EmbedBuilder embed = new EmbedBuilder();


                embed.setDescription(x.getContentRaw() + "\n \n [[Jump to message]](" + x.getJumpUrl() + ")  ");
                embed.appendDescription("Creation time:  " + creationTime.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE) + "\n[Quoted by " + commandEvent.getMember().getAsMention() + "]");
                embed.setColor(Color.ORANGE);


                try {
                    boolean hasImage = x.getAttachments().get(0).isImage();
                    if (hasImage) {
                        embed.setImage(x.getAttachments().get(0).getUrl());
                    }
                } catch (Exception e) {
                    // Nothing, is not a picture
                }
                embed.setAuthor(member.getEffectiveName(), null, x.getAuthor().getEffectiveAvatarUrl());

                commandEvent.reply(embed.build(), success -> commandEvent.getMessage().delete().queue());


            });
        } catch (Exception e) {
            commandEvent.reply("Couldn't quote your message, if it's from another channel make sure to mention that channel");
        }


    }
}
