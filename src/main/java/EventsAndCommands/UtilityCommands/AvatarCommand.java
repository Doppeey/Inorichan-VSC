package EventsAndCommands.UtilityCommands;

import EventsAndCommands.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;

public class AvatarCommand extends Command {


    public AvatarCommand() {
        this.name = "getavatar";
        this.help = "Tag a user and get their avatar";
        this.category = Categories.Utility;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {

        Member memberToGetAvatarFrom = null;

        memberToGetAvatarFrom = getMemberFromArgs(commandEvent);


        if (memberToGetAvatarFrom != null) {

            final User user = memberToGetAvatarFrom.getUser();
            final String avatarUrl = user.getAvatarUrl();
            final Color userColor = memberToGetAvatarFrom.getColor();

            EmbedBuilder embedBuilder = new EmbedBuilder();


            embedBuilder.setImage(avatarUrl);
            embedBuilder.setColor(userColor);
            embedBuilder.setTitle("Here you go");
            embedBuilder.setDescription("[Click here to reverse image search this picture](http://images.google.com/searchbyimage?image_url=" + avatarUrl + ")");

            commandEvent.getChannel().sendMessage(embedBuilder.build()).queue();

        } else {
            commandEvent.reply("Could not find user");
        }


    }

    private Member getMemberFromArgs(CommandEvent commandEvent) {
        Member memberToGetAvatarFrom = null;
        String args = commandEvent.getArgs();
        final Guild guild = commandEvent.getGuild();
        final Message message = commandEvent.getMessage();


        if (message.getMentionedMembers().isEmpty()) {

            if (guild.getMembersByEffectiveName(args, true).isEmpty()) {

                if (guild.getMemberById(args) != null) {
                    memberToGetAvatarFrom = guild.getMemberById(args);
                } else {
                    System.out.println("Get avatar command was not able to find user in " + commandEvent.getChannel().getName() + " in " + guild.getName());

                }


            } else {
                memberToGetAvatarFrom = guild.getMembersByEffectiveName(args, true).get(0);
            }


        } else {
            memberToGetAvatarFrom = message.getMentionedMembers().get(0);
        }


        return memberToGetAvatarFrom;
    }
}
