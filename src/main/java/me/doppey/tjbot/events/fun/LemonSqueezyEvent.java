package me.doppey.tjbot.events.fun;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class LemonSqueezyEvent extends ListenerAdapter {


    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        final String contentRaw = event.getMessage().getContentRaw();
        final boolean isBot = event.getAuthor().isBot();
        final TextChannel eventChannel = event.getChannel();


        if (isBot) {

            if (contentRaw.equalsIgnoreCase("lemon squeezy")) {
                eventChannel.sendMessage("dip my head in something cheesy").queue();
            }
        }
    }
}
