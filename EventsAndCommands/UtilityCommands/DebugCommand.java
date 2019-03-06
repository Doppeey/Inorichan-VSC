package EventsAndCommands.UtilityCommands;

import EventsAndCommands.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class DebugCommand extends Command {

    public DebugCommand() {
        this.name = "debug";
        this.help = "visits a website for you and previews the link";
        this.category = Categories.Utility;
        this.hidden = true;
        this.ownerCommand = true;

    }


    @Override
    protected void execute(CommandEvent commandEvent) {


        Runnable debug = new Runnable() {
            @Override
            public void run() {
                Robot robot = null;
                try {
                    robot = new Robot();
                } catch (AWTException e) {
                    e.event.getJDA().getGuildById("272761734820003841").getTextChannelById("552931145579495424").sendMessage(event.getMessage()).queue();
;
                }
                String fileName = "resources/images/Screenshot.jpg";

                Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit()
                        .getScreenSize());
                BufferedImage screenFullImage = null;
                if (robot != null) {
                    screenFullImage = robot.createScreenCapture(screenRect);
                }

                try {
                    ImageIO.write(screenFullImage, "jpg", new File(fileName));
                } catch (IOException e) {
                    e.event.getJDA().getGuildById("272761734820003841").getTextChannelById("552931145579495424").sendMessage(event.getMessage()).queue();
;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.event.getJDA().getGuildById("272761734820003841").getTextChannelById("552931145579495424").sendMessage(event.getMessage()).queue();
;
                }
                commandEvent.getChannel().sendFile(new File("resources/images/Screenshot.jpg")).queue();
            }
        };
        Thread thread = new Thread(debug);

        thread.run();



    }
}
