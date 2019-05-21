package me.doppeey.tjbot.commands.fun.encryption;

import me.doppeey.tjbot.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;

public class EncryptCommand extends Command {

    public EncryptCommand() {

        this.name = "encrypt";
        this.help = "Takes a text and encrypts it";
        this.category = Categories.Fun;

    }


    @Override
    protected void execute(CommandEvent commandEvent) {

        char[] args = commandEvent.getArgs().toCharArray();
        StringBuilder stringBuilder = new StringBuilder();


        commandEvent.getMessage().delete().queue();


        for(Character character : args){

            stringBuilder.append((int)character);
            stringBuilder.append(" ");

        }


        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(commandEvent.getMember().getEffectiveName()+" has encrypted a message!",null,commandEvent.getAuthor().getEffectiveAvatarUrl());
        try {
            embedBuilder.setDescription(stringBuilder.toString());
            if(stringBuilder.toString().length() > 2000){
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e){
            commandEvent.reply("Couldn't encrypt your message, "+commandEvent.getMember().getAsMention()+".\nThe encrypted message exceeded the character limit of Discord (2000 characters)");
            return;
        }
        embedBuilder.setColor(Color.red);



        commandEvent.reply(embedBuilder.build());

    }
}
