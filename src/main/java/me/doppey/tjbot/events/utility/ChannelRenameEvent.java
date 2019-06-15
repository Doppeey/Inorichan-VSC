package me.doppey.tjbot.events.utility;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.ChannelManager;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

public class ChannelRenameEvent extends ListenerAdapter {


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        String content = event.getMessage().getContentDisplay();
        TextChannel eventChannel = event.getTextChannel();
        boolean hasCalledFreeCommand = content.toLowerCase().startsWith(">tag free") || content.toLowerCase().startsWith("?tag free");
        boolean isHelpChannel = eventChannel.getName().toLowerCase().contains("help");
        boolean isMarkedFree = eventChannel.getName().toLowerCase().contains("\uD83C\uDD93");
        ChannelManager cm = eventChannel.getManager();


        if(isMarkedFree && isHelpChannel && !event.getAuthor().isBot()){

            String newName = eventChannel.getName().replaceAll("\uD83C\uDD93","");
            cm.setName(newName).queue();
            return;
        }


        //Checks if the message marked the channel to be free and if its even a help channel and if it might already be marked free
        if(!hasCalledFreeCommand || !isHelpChannel ||isMarkedFree){
            return;
        }

        cm.setName(cm.getChannel().getName()+"\uD83C\uDD93").queue();








    }
}
