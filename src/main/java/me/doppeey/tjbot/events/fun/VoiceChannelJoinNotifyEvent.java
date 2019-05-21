package me.doppeey.tjbot.events.fun;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

public class VoiceChannelJoinNotifyEvent extends ListenerAdapter {


    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {


        final String id = event.getMember().getUser().getId();
        final TextChannel lobby = event.getGuild().getTextChannelById("150599648019611648");
        final String joinedUser = event.getMember().getEffectiveName();

        final Member doppey = event.getGuild().getMemberById("272158112318750720");
        final Member kurisu = event.getGuild().getMemberById("171680368926261248");
        final Member fox = event.getGuild().getMemberById("150587512786518016");

        final boolean isFoxServer = event.getGuild().getId().equalsIgnoreCase("150599648019611648");
        final boolean isFox = id.equalsIgnoreCase("150587512786518016");
        final boolean isDoppey = id.equalsIgnoreCase("272158112318750720");
        final boolean isKurisu = id.equalsIgnoreCase("171680368926261248");


        if (isFoxServer) {

            if (isFox) {
                notifyKelseyLukas(lobby, joinedUser, doppey, kurisu);
            } else if (isDoppey) {
                notifyFox(lobby, joinedUser, fox);
            } else if (isKurisu) {
                notifyFox(lobby, joinedUser, fox);
            }
        }
    }


    private void notifyKelseyLukas(TextChannel lobby, String joinedUser, Member doppey, Member kelsey) {
        lobby.sendMessage(joinedUser
                + " has joined a voice channel! " + doppey.getAsMention()
                + " " + kelsey.getAsMention()).queue(x -> x.delete().queueAfter(10, TimeUnit.SECONDS));

        doppey.getUser().openPrivateChannel().queue(x -> x.sendMessage("Fox has joined a voicechannel!").queue(y -> y.getPrivateChannel().close().queue()));
        kelsey.getUser().openPrivateChannel().queue(x -> x.sendMessage("Fox has joined a voicechannel!").queue(y -> y.getPrivateChannel().close().queue()));
    }

    private void notifyFox(TextChannel lobby, String joinedUser, Member fox) {
        fox.getUser().openPrivateChannel().queue(x -> x.sendMessage(joinedUser + " has joined a voicechannel!").queue(y -> y.getPrivateChannel().close().queue()));

    }
}



