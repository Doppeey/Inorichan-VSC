package EventsAndCommands.FunCommands.MemeCommands;

import EventsAndCommands.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.util.Properties;

public class DrakeCommand extends Command {

    private final String memeId;
    private HttpResponse<String> response = null;
    private Properties config;



    public DrakeCommand(Properties config) {
        this.config = config;
        this.name = "drake";
        this.help = "Drake meme, usage: >drake [option a ; option b]";
        this.category = Categories.Memes;
        this.memeId = "172461322";
    }


    @Override
    protected void execute(CommandEvent commandEvent) {

        String text0 = null;
        String text1 = null;
        try {
            text0 = commandEvent.getArgs().split(";")[0];
            text1 = commandEvent.getArgs().split(";")[1];
        } catch (Exception | Error e) {
            commandEvent.getChannel().sendMessage("Usage: >buttons option a ; option b").queue();
            return;
        }


        try {
            response = com.mashape.unirest.http.Unirest.get("https://api.imgflip.com/caption_image")
                    .queryString("username", config.getProperty("IMGFLIP_USERNAME"))
                    .queryString("template_id", this.memeId)
                    .queryString("password", config.getProperty("IMGFLIP_PASSWORD"))
                    .queryString("text0", text0)
                    .queryString("text1", text1)
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

        commandEvent.reply("By " + commandEvent.getMember().getAsMention() + "\n" + str, sent -> commandEvent.getMessage().delete().queue());


    }
}
