package EventsAndCommands.UtilityCommands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;


import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FreeCommand extends Command {


    public FreeCommand() {
        this.name = "free";
        this.help = "Shows the times any given help channel has been idle for";

    }

    @Override
    protected void execute(CommandEvent event) {

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Minutes since last message in each help channel");
        embed.setColor(Color.green);
        StringBuilder description = new StringBuilder();
        final Guild guild = event.getGuild();
        LocalDateTime now = LocalDateTime.now().minusHours(2);

        TreeMap<TextChannel, List<Message>> channels = new TreeMap<>();
        AtomicInteger countChannels = new AtomicInteger(0);
        ArrayList<TextChannel> channelList = new ArrayList<>();

        guild.getTextChannels().forEach(channel -> {
            if (channel.getName().toLowerCase().contains("help")) {
                channelList.add(channel);
            }
        });


        channelList.forEach(channel -> {


            channel.getHistory().retrievePast(1).queue(retrieved -> {
                channels.put(channel, retrieved);
                countChannels.getAndIncrement();
            });

        });


        while (true) {
            if (countChannels.get() == channelList.size()) {
                break;
            }
        }

        countChannels.set(0);

        channels.keySet().forEach(channel -> {
            appendNameAndTime(description, guild, channels.get(channel), channel.getId(),now);
            countChannels.getAndIncrement();
        });

        while (true) {
            if (countChannels.get() == channelList.size()) {
                break;
            }
        }

        embed.setDescription(description.toString());
        event.reply(embed.build());


    }



    private void appendNameAndTime(StringBuilder description, Guild guild, List<Message> retrieved, String channelID,LocalDateTime now) {
        description.append("**").append(guild.getTextChannelById(channelID).getName()).append("**").append(" ----> ");
        Duration between = Duration.between(retrieved.get(0).getCreationTime().toLocalDateTime(), now);
        description.append(between.getSeconds() / 60).append("\n");
    }
}
