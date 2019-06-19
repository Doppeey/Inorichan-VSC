package me.doppey.tjbot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.doppey.tjbot.InoriChan;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class RestartCommand extends Command {
    public RestartCommand() {
        this.name = "Restart";
        this.ownerCommand = true;

    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        Desktop desktop = Desktop.getDesktop();
        File file = new File("C:\\Users\\Administrator\\Desktop\\Start.bat");
        try {
            desktop.open(file);
            commandEvent.reply("New Instance opened successfully, shutting down this instance now!");
        } catch (IOException e) {
            InoriChan.LOGGER.error(e.getMessage(), e);
        }

        System.exit(0);
    }
}
