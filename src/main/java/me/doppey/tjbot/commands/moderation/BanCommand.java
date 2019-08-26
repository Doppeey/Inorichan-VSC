package me.doppey.tjbot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.doppey.tjbot.Categories;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.awt.Color;

public class BanCommand extends Command {

    public BanCommand() {
        this.name = "ban";
        this.category = Categories.Moderation;
        this.help = "Bans a user, works by supplying their ID";
        this.userPermissions = new Permission[]{Permission.BAN_MEMBERS};
    }

    @Override
    protected void execute(CommandEvent event) {
        String args = event.getArgs();

        if (!args.contains(",")) {
            banId(event.getGuild(), event.getChannel(), args);
        } else {
            for (String id : args.split(",")) {
                banId(event.getGuild(), event.getChannel(), id);
            }
        }
    }

    private void banId(Guild guild, MessageChannel channel, String id) {
        try {
            guild.getController().ban(id, 5).queue(banned -> {
                guild.getBanList().queue(banList -> {
                    User bannedUser = null;
                    for (Guild.Ban ban : banList) {

                        if (ban.getUser().getId().equals(id)) {
                            bannedUser = ban.getUser();
                            break;
                        }
                    }

                    if (bannedUser == null) {
                        throw new IllegalArgumentException();
                    }

                    EmbedBuilder successEmbed = new EmbedBuilder();
                    successEmbed.setColor(Color.GREEN);
                    successEmbed.setTitle("Member ban || Success");
                    successEmbed.setDescription("Successfully banned the user " + bannedUser.getName() + "#" + bannedUser.getDiscriminator());
                    successEmbed.setThumbnail(bannedUser.getAvatarUrl());
                    channel.sendMessage(successEmbed.build()).queue();
                });
            });
        } catch (Exception e) {
            // Member not found
            EmbedBuilder failEmbed = new EmbedBuilder();
            failEmbed.setColor(Color.red);
            failEmbed.setTitle("Member ban || Failed");
            failEmbed.setDescription("No user was found under the specified ID");

            channel.sendMessage(failEmbed.build()).queue();
        }
    }
}
