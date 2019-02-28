package EventsAndCommands.FunCommands.MemeCommands;

import EventsAndCommands.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.EmbedBuilder;
import org.json.JSONObject;

import java.awt.*;
import java.util.Properties;

public class SpongebobCommand extends Command {

    private Properties config;

    public SpongebobCommand(Properties config){
        this.config = config;
        this.name = "spongebob";
        this.category = Categories.Memes;
        this.help = "creates scrambled text spongebob meme";

    }

    @Override
    protected void execute(CommandEvent commandEvent) {

        StringBuilder memeText = new StringBuilder();
        int counter = 0;

        for (char c : commandEvent.getArgs().toCharArray()) {

            counter++;
            if (counter == 2) {
                memeText.append(Character.toUpperCase(c));
                counter = 0;
            } else {
                memeText.append(Character.toLowerCase(c));
            }

        }

        String apiUsername = this.config.getProperty("IMGFLIP_USERNAME");
        String apiPassword = this.config.getProperty("IMGFLIP_PASSWORD");

        HttpResponse<String> response = null;

        try {
            response = com.mashape.unirest.http.Unirest.get("https://api.imgflip.com/caption_image")
                    .queryString("username", apiUsername)
                    .queryString("template_id", 102156234)
                    .queryString("password", apiPassword)
                    .queryString("boxes[0][text]", memeText.toString())
                    .queryString("cache-control", "no-cache")
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        JSONObject json = null;
        if (response != null) {
            json = new JSONObject(response.getBody());
        }
        String str = null;
        if (json != null) {
            str = json.getJSONObject("data").getString("url");
        }

        if(str != null) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setImage(str);
            embedBuilder.setColor(Color.yellow);
            commandEvent.getChannel().sendMessage(embedBuilder.build()).queue();
        } else System.out.println("Spongeboy exception");

    }
}
