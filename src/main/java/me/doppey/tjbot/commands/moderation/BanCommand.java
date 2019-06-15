package me.doppey.tjbot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.doppey.tjbot.Categories;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.util.Optional;

public class BanCommand extends Command {

    public BanCommand() {
        this.name = "ban";
        this.category = Categories.Moderation;
        this.help = "Bans a user, works by supplying their ID";
        this.userPermissions = new Permission[]{Permission.BAN_MEMBERS};
    }

    @Override
    protected void execute(CommandEvent event) {



        try {

            event.getGuild().getController().ban(event.getArgs(),5).queue( banned -> {
                 event.getGuild().getBanList().queue( banList -> {

                     User bannedUser = null;

                     for(Guild.Ban ban : banList){

                         if(ban.getUser().getId().equals(event.getArgs())){
                             bannedUser = ban.getUser();
                             break;
                         }

                     }

                     if(bannedUser == null){
                         throw new IllegalArgumentException();
                     }



                     EmbedBuilder successEmbed = new EmbedBuilder();
                     successEmbed.setColor(Color.GREEN);
                     successEmbed.setTitle("Member ban || Success");
                     successEmbed.setDescription("Successfully banned the user "+bannedUser.getName()+"#"+bannedUser.getDiscriminator());
                     successEmbed.setThumbnail(bannedUser.getAvatarUrl());
                     event.reply(successEmbed.build());
                 });

            });






        } catch (Exception e) {
            // Member not found
            EmbedBuilder failEmbed = new EmbedBuilder();
            failEmbed.setColor(Color.red);
            failEmbed.setTitle("Member ban || Failed");
            failEmbed.setDescription("No user was found under the specified ID");

            event.reply(failEmbed.build());
        }


    }
}
