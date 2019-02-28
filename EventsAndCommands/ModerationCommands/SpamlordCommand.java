package EventsAndCommands.ModerationCommands;

import EventsAndCommands.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
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


        final Role spamlord = commandEvent.getGuild().getRolesByName("spamlord", true).get(0);


        final Member spammer = commandEvent.getMessage().getMentionedMembers().get(0);

        if (commandEvent.getMessage().getContentRaw().contains("-")) {

            final int time = Integer.parseInt(commandEvent.getArgs().split("-")[1]);

            commandEvent.getMessage().addReaction("✅").queue( reacted -> spammer.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("You have been a bit too spammy and are locked to the spam Channel for an hour").queue(x -> privateChannel.close().queue())));
            gc.addSingleRoleToMember(spammer, spamlord).queue(muted -> gc.removeSingleRoleFromMember(spammer, spamlord).queueAfter(time, TimeUnit.HOURS));


        } else {


            gc.addSingleRoleToMember(spammer, spamlord).queue(muted -> gc.removeSingleRoleFromMember(spammer, spamlord).queueAfter(1, TimeUnit.HOURS));


        }
    }
}











