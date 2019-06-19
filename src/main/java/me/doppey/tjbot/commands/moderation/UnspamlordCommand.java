package me.doppey.tjbot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.doppey.tjbot.Categories;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.managers.GuildController;

public class UnspamlordCommand extends Command {
    public UnspamlordCommand() {
        this.name = "unspamlord";
        this.help = "removes someone from the spamlord role [MOD COMMAND]";
        this.requiredRole = "Moderator";
        this.category = Categories.Moderation;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (commandEvent.getArgs().isEmpty()) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Unspamlord command")
                    .setDescription("Removes the spamlord role from a user");
            commandEvent.reply(embed.build());
            return;
        }

        GuildController controller = new GuildController(commandEvent.getGuild());
        Role spamlordRole = commandEvent.getGuild().getRolesByName("spamlord", true).get(0);

        MessageChannel channel = commandEvent.getChannel();
        MessageChannel spamChannel = commandEvent.getGuild().getTextChannelsByName("spam", true).get(0);

        Member member = commandEvent.getMessage().getMentionedMembers().get(0);

        if (member.getRoles().contains(spamlordRole)) {
            controller.removeRolesFromMember(member, spamlordRole).queue();

            commandEvent.getMessage().addReaction("✅").queue();
            channel.sendMessage("The user is no longer locked in #spam.").queue();
            spamChannel.sendMessage(member.getAsMention() + ", you are no longer locked in #spam... behave!").queue();
        } else {
            channel.sendMessage("The user is not currently locked in #spam.").queue();
        }
    }
}
