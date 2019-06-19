package me.doppey.tjbot.utility;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.managers.ChannelManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChannelMarkerScheduler {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Guild guild;

    public ChannelMarkerScheduler(Guild guild) {
        this.guild = guild;
    }

    public void checkHelpChannels() {
        List<TextChannel> channelList = guild.getTextChannels();
        ArrayList<TextChannel> filteredList = new ArrayList<>();
        for (TextChannel channel : channelList) {
            if (channel.getName().toLowerCase().contains("help")) {
                filteredList.add(channel);
            }
        }

        final Runnable checker = () -> {
            for (TextChannel channel : filteredList) {
                if (channel.getName().contains("\uD83C\uDD93")) {
                    continue;
                }

                channel.getMessageById(channel.getLatestMessageId()).queue(
                        message -> {
                            //If it has been at least an hour since the last message
                            Duration between = Duration.between(message.getCreationTime().toLocalDateTime(), LocalDateTime.now());
                            if (between.toHours() >= 1) {

                                ChannelManager cm = channel.getManager();
                                cm.setName(cm.getChannel().getName() + "\uD83C\uDD93").queue();
                                channel.sendMessage("This channel has been marked as `free` because it has been inactive for 1 hour\n" +
                                        "If nobody has answered your question, you can send a message," +
                                        " which will remove the `free` symbol, or ask again at a later time.").queue();
                            }
                        }
                );
            }
        };
        scheduler.schedule(checker, 1, TimeUnit.MINUTES);
    }
}
