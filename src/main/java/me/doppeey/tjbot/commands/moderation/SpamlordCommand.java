package me.doppeey.tjbot.commands.moderation;

import me.doppeey.tjbot.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.managers.GuildController;

import java.util.concurrent.TimeUnit;

public class SpamlordCommand extends Command {

    public SpamlordCommand() {
        this.name = "spamlord";
        this.help = "gives someone the spamlord role, limiting them to #spam [MOD COMMAND]";
        this.requiredRole = "Moderator";
        this.category = Categories.Moderation;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {

        GuildController gc = new GuildController(commandEvent.getGuild());

        final TextChannel spamChannel = gc.getGuild().getTextChannelsByName("spam", true).get(0);
        final Role spamlord = commandEvent.getGuild().getRolesByName("spamlord", true).get(0);

        final Member spammer = commandEvent.getMessage().getMentionedMembers().get(0);

        // IF NO ARGUMENTS ARE GIVEN
        if (commandEvent.getArgs().isEmpty()) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Spamlord command");
            embed.setDescription("Gives the spamlord role to a user, limiting them to the spam channel");
            embed.addField("Parameters",
                    "**-t**: t stands for the amount of hours the user will be limited to the spam channel", true);
            commandEvent.reply(embed.build());
            return;
        }

        // IF TIME ARGUMENTS ARE GIVEN (A user and optionally a time)
        if (commandEvent.getMessage().getContentRaw().contains("-")) {

            final int time = Integer.parseInt(commandEvent.getArgs().split("-")[1].strip());

            commandEvent.getMessage().addReaction("✅").queue();
            gc.addSingleRoleToMember(spammer, spamlord)
                    .queue(muted -> gc.removeSingleRoleFromMember(spammer, spamlord).queueAfter(time, TimeUnit.HOURS));
            spamChannel.sendMessage(spammer.getAsMention() + " you are locked here for " + time
                    + " hour(s), please refrain from spammy behaviour in the future!").queue();
            return;
        }

        //DEFAULT
        gc.addSingleRoleToMember(spammer, spamlord)
                .queue(muted -> gc.removeSingleRoleFromMember(spammer, spamlord).queueAfter(1, TimeUnit.HOURS));
        commandEvent.getChannel().sendMessage("The user has been locked to the spam channel for an hour").queue();
        commandEvent.getMessage().addReaction("✅").queue();
        spamChannel
                .sendMessage(spammer.getAsMention()
                        + " you are locked here for an hour, please refrain from spammy behaviour in the future!")
                .queue();

    }
}
