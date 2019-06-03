package me.doppey.tjbot.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.doppey.tjbot.InoriChan;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.Color;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FreeCommand extends Command {

    public FreeCommand() {
        this.name = "free";
        this.help = "Shows the times any given help channel has been idle for";
    }

    @Override
    protected void execute(CommandEvent event) {
        StringBuilder description = new StringBuilder();
        LocalDateTime now = ZonedDateTime.now(Clock.systemUTC()).toLocalDateTime();

        List<TextChannel> helpChannels = InoriChan.getHelpChannels();
        Map<String, Message> latestMessage = new TreeMap<>();
        AtomicInteger countChannels = new AtomicInteger(0);

        helpChannels.forEach(c -> c.getHistory().retrievePast(1).queue(retrieved -> {
            latestMessage.put(c.getName(), retrieved.get(0));
            countChannels.getAndIncrement();
        }));


        while (true) {
            if (countChannels.get() == helpChannels.size()) {
                break;
            }
        }

        countChannels.set(0);



        latestMessage.forEach((c, m) -> {
            appendNameAndTime(description, m, c, now);
            countChannels.getAndIncrement();
        });

        while (true) {
            if (countChannels.get() == helpChannels.size()) {
                break;
            }
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Minutes since last message in each help channel")
                .setColor(Color.GREEN)
                .setDescription(description.toString());
        event.reply(embed.build());
    }


    private void appendNameAndTime(StringBuilder description, Message message, String name, LocalDateTime now) {
        Duration between = Duration.between(message.getCreationTime().toLocalDateTime(), now);
        description
                .append("**")
                .append(name)
                .append("**")
                .append(" ----> ")
                .append(between.getSeconds() / 60)
                .append("\n");
    }
}

