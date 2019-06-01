package me.doppey.tjbot.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class EmoteIdCommand extends Command {

    public EmoteIdCommand(){
        this.name = "emote";
        this.category = new Category("Utility");
        this.help = "Gives info about emotes for developping purposes";
    }



    @Override
    protected void execute(CommandEvent commandEvent) {

        commandEvent.reply("`"+commandEvent.getArgs()+"`");
        String asMention = commandEvent.getGuild().getEmoteById(commandEvent.getMessage().getEmotes().get(0).getId()).getAsMention();
        commandEvent.reply(asMention);


    }
}
