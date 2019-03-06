package EventsAndCommands.ModerationCommands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

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
            e.event.getJDA().getGuildById("272761734820003841").getTextChannelById("552931145579495424").sendMessage(event.getMessage()).queue();
;
        }


        System.exit(0);


    }
}
