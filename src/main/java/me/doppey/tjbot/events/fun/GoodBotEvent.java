package me.doppey.tjbot.events.fun;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Random;

public class GoodBotEvent extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        final TextChannel eventChannel = event.getChannel();
        String msg = event.getMessage().getContentRaw().toLowerCase();
        if(msg.contains("good bot") || msg.contains("bad bot")){
            eventChannel.getHistoryBefore(event.getMessage(), 1).queue(messageHistory -> {

                final String id = messageHistory.getRetrievedHistory().get(0).getMember().getUser().getId();
                final String contentRaw = event.getMessage().getContentRaw();
                final String effectiveName = messageHistory.getRetrievedHistory().get(0).getMember().getEffectiveName();
                final boolean isBot = messageHistory.getRetrievedHistory().get(0).getMember().getUser().isBot();
        
                if (contentRaw.equalsIgnoreCase("good bot")) {
        
                    if (id.equalsIgnoreCase("503181069705805844")) {
        
                        eventChannel.sendMessage("Thanks! :blush: :blush:  ").queue();
        
                    } else if (!isBot) {
        
                        eventChannel.sendMessage(
                                "I'm " + new Random().nextInt(101) + "% sure that " + effectiveName + " is not a bot")
                                .queue();
        
                    }
        
                } else if (id.equalsIgnoreCase("503181069705805844") && contentRaw.equalsIgnoreCase("bad bot")) {
                        eventChannel.sendMessage("No u").queue();
                } else if (!isBot && contentRaw.equalsIgnoreCase("bad bot")) {
                    eventChannel.sendMessage(
                            "I'm " + new Random().nextInt(101) + "% sure that " + effectiveName + " is not a bot")
                            .queue();
                }
        
            });
        }
    }

}
