package EventsAndCommands.FunCommands;

import EventsAndCommands.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;
import java.util.Properties;


public class YoutubeToMp3Command extends Command {



    private Properties config;

    public YoutubeToMp3Command(Properties config) {
        this.config = config;
        this.name = "youtubetomp3";
        this.help = "Will post a link to download your mp3";
        this.category = Categories.Utility;


    }


    @Override
    protected void execute(CommandEvent commandEvent) {
        final String baseURL = "https://www.download-mp3-youtube.com/api/?api_key="+config.getProperty("YOUTUBE_TO_MP3_API_KEY")+"&format=mp3&video_id=";

        String args = commandEvent.getArgs();
        String videoId = args.split("v=")[1];
        EmbedBuilder embed = new EmbedBuilder();
        embed.addField(commandEvent.getMember().getEffectiveName() + " your MP3 is ready!", "[This link](" + baseURL + videoId + ") will redirect you to the download page!", false)
                .addField("The requested video was", args, false)
                .addField("Disclaimer", "Provided by download-mp3-youtube.com, please adhere to their ToS which you can find [here](https://www.download-mp3-youtube.com/terms.php)", false)
                .setColor(Color.red);


        commandEvent.reply(embed.build(), x -> commandEvent.getMessage().delete().queue());


    }
}
