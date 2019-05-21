package me.doppeey.tjbot.commands.fun;

import me.doppeey.tjbot.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.io.File;
import java.util.Random;

public class DeletThisCommand extends Command {

    public DeletThisCommand() {
        this.name = "delet";
        this.category = Categories.Fun;
        this.help = "Sends random delet this picture";
    }

    private File file;

    

    @Override
    protected void execute(CommandEvent commandEvent) {

        try {
            String fileName = "delet/" + (new Random().nextInt(21) + 1) + ".jpg";
            file = new File(fileName);
                        
        } catch (Exception e) {
            InoriChan.LOGGER.error(e.getMessage(), e);
            return;
        }

        commandEvent.getChannel().sendFile(file).queue();

    }
}
