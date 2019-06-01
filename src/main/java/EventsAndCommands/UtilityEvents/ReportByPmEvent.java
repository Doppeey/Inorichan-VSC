package EventsAndCommands.UtilityEvents;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class ReportByPmEvent extends ListenerAdapter {

    private EventWaiter waiter;

    public ReportByPmEvent(EventWaiter waiter) {
        this.waiter = waiter;

    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {

        if (!event.getAuthor().isBot()) {

            Runnable timeOut = () -> event.getChannel()
                    .sendMessage("Your report has timed out because you haven't replied in 5 minutes").queue();

            Runnable reportRunnable = new Runnable() {

                private final Member[] reportedMember = new Member[1];

                @Override
                public void run() {
                    final String[] reportReason = new String[1];

                    if (event.getMessage().getContentRaw().toLowerCase().contains("report")) {

                        final User eventAuthor = event.getAuthor();
                        eventAuthor.openPrivateChannel().queue(channel -> channel.sendMessage(
                                "Please supply the name and discriminator of the person you'd like to report \nExample: \"JungleMan#1325\"")
                                .queue());

                        waiter.waitForEvent(event.getClass(), who -> eventAuthor.equals(who.getAuthor()), message -> {

                            String messageContent = message.getMessage().getContentRaw();
                            final Guild togetherJavaGuild = event.getJDA().getGuildById("272761734820003841");
                            try {
                                togetherJavaGuild.getMembersByName(messageContent.split("#")[0], true).stream()
                                        .filter(member -> member.getUser().getDiscriminator()
                                                .equalsIgnoreCase(messageContent.split("#")[1]))
                                        .forEach(correctMember -> reportedMember[0] = correctMember);
                            } catch (Exception | Error e) {
                                event.getChannel().sendMessage(
                                        "Sorry, can't find that user, please contact a moderator for further assistance.")
                                        .queue();
                                return;
                            }

                            if (reportedMember[0] == null) {
                                event.getChannel().sendMessage(
                                        "Sorry, can't find that user, please contact a moderator for further assistance.")
                                        .queue();
                                return;
                            }

                            event.getChannel().sendMessage("What did they do ?").queue();

                            waiter.waitForEvent(event.getClass(), why -> eventAuthor.equals(why.getAuthor()), why -> {

                                reportReason[0] = why.getMessage().getContentRaw();
                                event.getChannel().sendMessage(
                                        "Please upload evidence in the form of a screenshot, or type anything to report without evidence")
                                        .queue();

                                waiter.waitForEvent(event.getClass(),
                                        evidence -> eventAuthor.equals(evidence.getAuthor()), evidenceMessage -> {

                                            EmbedBuilder embedBuilder = new EmbedBuilder();
                                            embedBuilder.setTitle("Report reason");
                                            embedBuilder.setDescription(reportReason[0]);
                                            embedBuilder.addField("Reported by", eventAuthor.getAsMention() + "\n"
                                                    + eventAuthor.getName() + "#" + eventAuthor.getDiscriminator(),
                                                    true);
                                            embedBuilder.addField("Reported user",
                                                    togetherJavaGuild.getMemberById(reportedMember[0].getUser().getId())
                                                            .getAsMention() + "\n"
                                                            + reportedMember[0].getUser().getName() + "#"
                                                            + reportedMember[0].getUser().getDiscriminator(),
                                                    true);
                                            embedBuilder.setThumbnail(reportedMember[0].getUser().getAvatarUrl());
                                            embedBuilder.setColor(Color.red);

                                            if (!evidenceMessage.getMessage().getAttachments().isEmpty()) {
                                                if (evidenceMessage.getMessage().getAttachments().get(0).isImage()) {
                                                    embedBuilder.setImage(evidenceMessage.getMessage().getAttachments()
                                                            .get(0).getUrl());
                                                }
                                            }

                                            togetherJavaGuild.getTextChannelById("544565081724289024")
                                                    .sendMessage(embedBuilder.build()).queue();
                                            why.getChannel().sendMessage(
                                                    "Your report has been submitted and will be reviewed by one of our moderators as soon as possible, thank you!")
                                                    .queue();

                                        }, 5, TimeUnit.MINUTES, timeOut);

                            }, 5, TimeUnit.MINUTES, timeOut);

                        }, 5, TimeUnit.MINUTES, timeOut);

                    }
                }
            };
            Thread reportThread = new Thread(reportRunnable);
            reportThread.start();

        }

    }
}
