package me.doppey.tjbot.commands.moderation;

import me.doppey.tjbot.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class ReportCommand extends Command {

    private String mention;
    private String argsTrimmed;
    MessageChannel quoteChannel;

    public ReportCommand() {
        this.name = "report";
        this.help = "used to report a message. Usage >report [messageID], you have to mention the channel if you report a message from a different channel";
        this.category = Categories.Moderation;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {

        String args = commandEvent.getArgs();
        boolean hasMessageAttached = args.split(";").length == 2;
        String messageId = args;
        final boolean hasAMentionedChannel = !commandEvent.getMessage().getMentionedChannels().isEmpty();
        MessageChannel reportChannel = commandEvent.getGuild().getTextChannelById("544565081724289024");

        if (hasAMentionedChannel) {
            quoteChannel = commandEvent.getMessage().getMentionedChannels().get(0);
            mention = commandEvent.getMessage().getMentionedChannels().get(0).getAsMention();
            if (hasMessageAttached) {
                messageId = args.split(";")[0].replaceAll(mention, "").strip();

            } else {
                messageId = args.replaceAll(mention, "").strip();
            }

        } else {
            if (hasMessageAttached) {
                messageId = args.split(";")[0];
            }
            quoteChannel = commandEvent.getChannel();
        }

        try {

            quoteChannel.getMessageById(messageId).queue(x -> {

                EmbedBuilder embed = new EmbedBuilder();
                embed.setDescription(x.getContentRaw() + "\n \n [[Jump to message]](" + x.getJumpUrl() + ")  ");
                embed.setColor(Color.RED);
                embed.setAuthor(x.getAuthor().getName() + "#" + x.getAuthor().getDiscriminator(), null,
                        x.getAuthor().getEffectiveAvatarUrl());

                if (hasMessageAttached) {
                    embed.addField("Report Reason", args.split(";")[1], true);
                } else {
                    embed.addField("Report reason", "None supplied", true);
                }

                embed.addField("Reported by", commandEvent.getMember().getAsMention(), true);

                commandEvent.getMessage().delete().queue();
                reportChannel.sendMessage(embed.build()).queue();
                commandEvent.getChannel().sendMessage("A report has successfully been submitted")
                        .queue(confirmation -> confirmation.delete().queueAfter(2, TimeUnit.SECONDS));

                try {
                    boolean hasImage = x.getAttachments().get(0).isImage();
                    if (hasImage) {
                        embed.setImage(x.getAttachments().get(0).getUrl());
                    }
                } catch (Exception e) {
                    // Nothing, is not a picture
                }

            });
        } catch (Exception | Error e) {
            commandEvent.getMessage().delete().queue();
            commandEvent.reply(
                    "Could not submit report, make sure you mention the channel of the reported message if this command isn't called in the same channel",
                    confirmation -> confirmation.delete().queueAfter(3, TimeUnit.SECONDS));
        }
    }
}
