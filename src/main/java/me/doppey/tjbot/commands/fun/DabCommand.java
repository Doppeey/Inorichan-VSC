package me.doppey.tjbot.commands.fun;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.doppey.tjbot.Categories;

import java.io.File;

public class DabCommand extends Command {

    public DabCommand() {
        this.name = "dab";
        this.help = "dab on dem haters";
        this.category = Categories.Fun;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        File dab = new File("resources/images/dab.png");
        commandEvent.getChannel().sendFile(dab).queue();
    }
}
