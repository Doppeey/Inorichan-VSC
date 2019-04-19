package EventsAndCommands;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

/**
 * MemberParser
 */
public abstract class MemberParser {

    public static Member getMemberFromArgs(CommandEvent commandEvent) {

        String args = commandEvent.getArgs();
        final Guild guild = commandEvent.getGuild();
        final Message message = commandEvent.getMessage();
        String name = null;
        String identifier = null;

        try {
            name = args.split("#")[0];
            identifier = args.split("#")[1];
        } catch (Exception e) {
            // nothing to do
        }

        // Try by mention
        if (!message.getMentionedMembers().isEmpty()) {
            return message.getMentionedMembers().get(0);
        }

        // If they supplied a name + identifier
        if (name != null && identifier != null) {

            // Incase multiple members have the same name we look for the correct
            // discriminator
            if (guild.getMembersByEffectiveName(name, true).size() > 1) {
                for (Member member : guild.getMembersByEffectiveName(name, true)) {
                    if (member.getUser().getDiscriminator().equals(identifier)) {
                        return member;
                    }
                }
            } else {
                return guild.getMembersByEffectiveName(name, true).get(0);
            }
        }

        // Try by name
        if (!guild.getMembersByEffectiveName(args, true).isEmpty()) {
            return guild.getMembersByEffectiveName(args, true).get(0);
        }

        // try by ID
        if (guild.getMemberById(args) != null) {
            return guild.getMemberById(args);
        }

        // No user found, so we return null
        return null;

    }

}