package me.doppey.tjbot.commands.moderation;

import me.doppey.tjbot.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.time.OffsetDateTime;
import java.util.List;

public class WhoIsCommand extends Command {
    public WhoIsCommand() {
        this.name = "whois";
        this.category = Categories.Moderation;
        this.help = "Gets info about a specified user by ID";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        String ID;
        Member member;
        final List<Member> mentionedMembers = commandEvent.getMessage().getMentionedMembers();
        final Guild guild = commandEvent.getGuild();

        try {
            ID = commandEvent.getMessage().getContentRaw().split(">whois ")[1];
        } catch (NullPointerException n) {
            commandEvent.reply("Invalid Argument");
            return;
        }

        final List<Member> membersByEffectiveName = guild.getMembersByEffectiveName(ID, true);

        if (ID.matches("[0-9]+")) {
            member = guild.getMemberById(ID);
        } else {
            if (!mentionedMembers.isEmpty()) {
                member = mentionedMembers.get(0);
            } else {
                try {
                    member = membersByEffectiveName.get(0);
                } catch (IndexOutOfBoundsException f) {
                    commandEvent.reply("Can't find user " + ID);
                    return;
                }
            }
        }

        final OffsetDateTime joinDate = member.getJoinDate();
        final OffsetDateTime creationTime = member.getUser().getCreationTime();

        EmbedBuilder whois = new EmbedBuilder();
        whois.setColor(member.getColor());
        whois.setTitle("Information about " + member.getEffectiveName());
        whois.setThumbnail(member.getUser().getAvatarUrl());
        whois.setDescription("Member as mention: " + member.getAsMention() + "\n" + "Currently *" + member.getOnlineStatus().toString() + "*\n" + "\u200B");
        whois.addField("Join Date", joinDate.getMonth().toString() + " " + joinDate.getDayOfMonth() + " (" + joinDate.getYear() + ")", true);
        whois.addField("Register Date", creationTime.getMonth() + " " + creationTime.getDayOfMonth() + " (" + creationTime.getYear() + ")", true);

        List<Role> roleList = member.getRoles();
        StringBuilder roles = new StringBuilder();
        int amountOfRoles = 0;

        for (Role r : roleList) {
            if (!r.getName().equalsIgnoreCase("illuminati")) {
                roles.append(" ").append(r.getAsMention());
                amountOfRoles++;
            }
        }

        whois.setFooter("ID: " + member.getUser().getId(), null);
        whois.addField("Roles [" + amountOfRoles + "]", roles.toString(), false);
        commandEvent.reply(whois.build());
    }
}
