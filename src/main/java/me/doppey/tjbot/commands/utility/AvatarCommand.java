package me.doppey.tjbot.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.doppey.tjbot.Categories;
import me.doppey.tjbot.MemberParser;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

public class AvatarCommand extends Command {

    public AvatarCommand() {
        this.name = "getavatar";
        this.help = "Tag a user and get their avatar";
        this.category = Categories.Utility;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        Member memberToGetAvatarFrom = MemberParser.getMemberFromArgs(commandEvent);

        if (memberToGetAvatarFrom != null) {

            final User user = memberToGetAvatarFrom.getUser();
            final String avatarUrl = user.getAvatarUrl();
            if (avatarUrl.isEmpty()) {
                commandEvent.reply("This user does not have an avatar");
            }

            commandEvent.reply(avatarUrl + "?size=512");
        } else {
            commandEvent.reply("Could not find user");
        }
    }
}
