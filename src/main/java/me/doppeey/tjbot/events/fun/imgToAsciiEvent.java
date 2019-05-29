package me.doppeey.tjbot.events.fun;

import me.doppeey.tjbot.InoriChan;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class imgToAsciiEvent extends ListenerAdapter {


    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        try {
            if (event.getMessage().getAttachments().get(0) != null && event.getMessage().getAttachments().get(0).isImage()) {

                if (event.getMessage().getContentRaw().toLowerCase().contains("imgtoascii")) {


                    BufferedImage image = null;

                    try {
                        image = ImageIO.read(event.getMessage().getAttachments().get(0).getInputStream());
                    } catch (Exception e) {
                        InoriChan.LOGGER.error(e.getMessage(), e);

;
                        return;
                    }
                    try {
                        event.getChannel().sendMessage("```\n" + run(image) + "\n```").queue();
                    } catch (Exception e) {
                        InoriChan.LOGGER.error(e.getMessage(), e);

;
                        event.getChannel().sendMessage("Couldn't convert image").queue();
                    }


                }


            }
        } catch (Exception e) {
            //nothing
        }

    }

    boolean negative;

    public String convert(BufferedImage image) {
        StringBuilder sb = new StringBuilder((image.getWidth() + 1) * image.getHeight());
        for (int y = 0; y < image.getHeight(); y++) {
            if (sb.length() != 0) sb.append("\n");
            for (int x = 0; x < image.getWidth(); x++) {
                Color pixelColor = new Color(image.getRGB(x, y));
                double gValue = (double) pixelColor.getRed() * 0.2989 + (double) pixelColor.getBlue() * 0.5870 + (double) pixelColor.getGreen() * 0.1140;
                final char s = negative ? returnStrNeg(gValue) : returnStrPos(gValue);
                sb.append(s);
            }
        }
        return sb.toString();
    }

    /**
     * Create a new string and assign to it a string based on the grayscale value.
     * If the grayscale value is very high, the pixel is very bright and assign characters
     * such as . and , that do not appear very dark. If the grayscale value is very lowm the pixel is very dark,
     * assign characters such as # and @ which appear very dark.
     *
     * @param g grayscale
     * @return char
     */
    private char returnStrPos(double g)//takes the grayscale value as parameter
    {
        final char str;

        if (g >= 230.0) {
            str = ' ';
        } else if (g >= 200.0) {
            str = '.';
        } else if (g >= 180.0) {
            str = '*';
        } else if (g >= 160.0) {
            str = ':';
        } else if (g >= 130.0) {
            str = 'o';
        } else if (g >= 100.0) {
            str = '&';
        } else if (g >= 70.0) {
            str = '8';
        } else if (g >= 50.0) {
            str = '#';
        } else {
            str = '@';
        }
        return str; // return the character

    }

    /**
     * Same method as above, except it reverses the darkness of the pixel. A dark pixel is given a light character and vice versa.
     *
     * @param g grayscale
     * @return char
     */
    private char returnStrNeg(double g) {
        final char str;

        if (g >= 230.0) {
            str = '@';
        } else if (g >= 200.0) {
            str = '#';
        } else if (g >= 180.0) {
            str = '8';
        } else if (g >= 160.0) {
            str = '&';
        } else if (g >= 130.0) {
            str = 'o';
        } else if (g >= 100.0) {
            str = ':';
        } else if (g >= 70.0) {
            str = '*';
        } else if (g >= 50.0) {
            str = '.';
        } else {
            str = ' ';
        }
        return str;

    }


    public String run(BufferedImage image) throws IOException {


        Image tmp = image.getScaledInstance(40, 35, BufferedImage.SCALE_FAST);
        BufferedImage buffered = new BufferedImage(40, 35, BufferedImage.TYPE_INT_RGB);
        buffered.getGraphics().drawImage(tmp, 0, 0, null);


        return convert(buffered);

    }
}
