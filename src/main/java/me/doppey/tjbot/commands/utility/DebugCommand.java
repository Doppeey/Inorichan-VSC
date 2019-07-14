package me.doppey.tjbot.commands.utility;

import me.doppey.tjbot.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.doppey.tjbot.InoriChan;

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
        this.cooldownScope = CooldownScope.GUILD;
        this.cooldown = 10;

    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        Runnable debug = () -> {
            Robot robot = null;
            try {
                robot = new Robot();
            } catch (AWTException e) {
                InoriChan.LOGGER.error(e.getMessage(), e);
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
                InoriChan.LOGGER.error(e.getMessage(), e);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                InoriChan.LOGGER.error(e.getMessage(), e);
            }
            commandEvent.getChannel().sendFile(new File("resources/images/Screenshot.jpg")).queue();
        };

        Thread thread = new Thread(debug);
        thread.run();
    }
}
