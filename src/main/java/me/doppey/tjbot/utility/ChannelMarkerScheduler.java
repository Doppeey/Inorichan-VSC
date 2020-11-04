package me.doppey.tjbot.utility;

import me.doppey.tjbot.InoriChan;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.utils.MiscUtil;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;



public class ChannelMarkerScheduler {

    private Duration waitTime;
    private Guild guild;

    public ChannelMarkerScheduler(Guild guild) {
        this.guild = guild;
        waitTime = Duration.ofHours(Long.valueOf(InoriChan.getConfig().getProperty("WAIT_TIME", "6")));
    }

    /*
    public void start() {
        List<TextChannel> helpChannels = guild.getTextChannels().stream()
                .filter(c -> c.getName().contains("help"))
                .collect(Collectors.toList());
        Runnable runnable = () -> {

            for (TextChannel channel : helpChannels) {

                if (channel.getName().contains("\uD83C\uDD93")) {
                    continue;
                }


                if (channel.hasLatestMessage()) {
                    markChannel(channel, channel.getLatestMessageId());
                } else {
                    channel.getHistoryBefore(MiscUtil.getDiscordTimestamp(System.currentTimeMillis()), 1)
                            .queue(history -> markChannel(channel, history.getRetrievedHistory().get(0).getId()));
                }
            }
        };
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(runnable, 3, TimeUnit.MINUTES.toSeconds(5) /* 5 minutes */, TimeUnit.SECONDS);
    */
        
    }

    private void markChannel(TextChannel channel, String messageId) {
        channel.getMessageById(messageId).queue(message -> execute(channel, message),
                failed -> channel.getHistoryBefore(MiscUtil.getDiscordTimestamp(System.currentTimeMillis()), 1).queue(
                        messageHistory -> execute(channel, messageHistory.getRetrievedHistory().get(0)),
                        failedAgain -> InoriChan.LOGGER.error("Failed to get last msg from %s", channel.getName())
                ));
    }

    private void execute(TextChannel channel, Message message) {
        Duration between = Duration.between(message.getCreationTime().toLocalTime(),
                ZonedDateTime.now(ZoneOffset.UTC).toLocalTime());

        if (waitTime.minus(between).toMinutes() <= 0) {
            channel.getManager().setName(channel.getName() + "\uD83C\uDD93").queue();
        }
    }
}

