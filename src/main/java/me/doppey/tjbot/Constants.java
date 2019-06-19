package me.doppey.tjbot;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

public class Constants {
    public static final Guild TJ_GUILD = InoriChan.getJda().getGuildById(272761734820003841L);

    public static final MessageChannel SPAM_CHANNEL = TJ_GUILD.getTextChannelById(331534900777844738L);
    public static final MessageChannel REPORTS_CHANNEL = TJ_GUILD.getTextChannelById(544565081724289024L);
    public static final MessageChannel WELCOME_CHANNEL = TJ_GUILD.getTextChannelById(513551097449807883L);
    public static final MessageChannel BOT_SUSPICION_CHANNEL = TJ_GUILD.getTextChannelById(546416238922956845L);
    public static final MessageChannel OOFS_AND_LMAOS_CHANNEL = TJ_GUILD.getTextChannelById(590767396009279510L);

    public static final User DOPPEY_USER = InoriChan.getJda().getUserById(272158112318750720L);

    public static final Role ILLUMINATI_ROLE = TJ_GUILD.getRoleById(359136657435394049L);
}
