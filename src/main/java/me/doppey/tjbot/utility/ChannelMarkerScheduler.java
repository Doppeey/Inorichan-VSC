package me.doppey.tjbot.utility;

import me.doppey.tjbot.InoriChan;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.managers.ChannelManager;
import net.dv8tion.jda.core.utils.MiscUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ChannelMarkerScheduler {

    Timer timer = new Timer();
    private Guild guild;

    public ChannelMarkerScheduler(Guild guild) {
        this.guild = guild;
    }

    public void checkHelpChannels() {


        TimerTask checker = new TimerTask() {
            @Override
            public void run() {

                List<TextChannel> channelList = guild.getTextChannels();
                ArrayList<TextChannel> filteredList = new ArrayList<>();
                for (TextChannel channel : channelList) {
                    if (channel.getName().toLowerCase().contains("help")) {
                        filteredList.add(channel);
                    }
                }

                for (TextChannel channel : filteredList) {

                    if (channel.getName().contains("\uD83C\uDD93")) {
                        continue;
                    }


                    if (channel.hasLatestMessage()) {
                        markChannel(channel, channel.getLatestMessageId());
                    } else {
                        channel.getHistoryBefore(MiscUtil.getDiscordTimestamp(System.currentTimeMillis()), 1).queue(history -> {

                            markChannel(channel, history.getRetrievedHistory().get(0).getId());
                        });
                    }


                }
            }
        };

        timer.schedule(checker, 3000, 300000);


    }

    private void markChannel(TextChannel channel, String messageId) {
        channel.getMessageById(messageId).queue(
                message -> {


                    Duration between = Duration.between(message.getCreationTime().toLocalDateTime(), LocalDateTime.now().minusHours(2));

                    if (between.toHours() >= 2) {

                        ChannelManager cm = channel.getManager();
                        cm.setName(cm.getChannel().getName() + "\uD83C\uDD93").queue();

                    }

                }
                , failed -> {
                    channel.getHistoryBefore(MiscUtil.getDiscordTimestamp(System.currentTimeMillis()), 1).queue(
                            messageHistory -> {

                                Message msg = messageHistory.getRetrievedHistory().get(0);
                                Duration between = Duration.between(msg.getCreationTime().toLocalDateTime(), LocalDateTime.now().minusHours(2));

                                if (between.toHours() >= 2) {

                                    System.out.println(between.toMinutes()+" Minutes for channel "+channel.getName());
                                    ChannelManager cm = channel.getManager();
                                    cm.setName(cm.getChannel().getName() + "\uD83C\uDD93").queue();

                                }


                            }, failedAgain -> {
                                InoriChan.LOGGER.error("Failed to get last msg from " + channel.getName());
                            }
                    );
                });
    }
}

