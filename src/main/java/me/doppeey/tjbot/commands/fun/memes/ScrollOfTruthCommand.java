package me.doppeey.tjbot.commands.fun.memes;

import me.doppeey.tjbot.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import me.doppeey.tjbot.InoriChan;
import org.json.JSONObject;

import java.util.Properties;

public class ScrollOfTruthCommand extends Command {

    private final String memeId;
    private HttpResponse<String> response = null;
    private Properties config;


    public ScrollOfTruthCommand(Properties config){
        this.config = config;
        this.name = "sot";
        this.help = "scroll of truth meme, usage: >sot [text here]";
        this.category = Categories.Memes;
        this.memeId = "175383256";
    }



    @Override
    protected void execute(CommandEvent commandEvent) {

        String memeText = commandEvent.getArgs();

        try {
            response = com.mashape.unirest.http.Unirest.get("https://api.imgflip.com/caption_image")
                    .queryString("username", config.getProperty("IMGFLIP_USERNAME"))
                    .queryString("template_id", this.memeId)
                    .queryString("password", config.getProperty("IMGFLIP_PASSWORD"))
                    .queryString("boxes[0][text]",memeText)
                    .queryString("cache-control", "no-cache")
                    .asString();
        } catch (UnirestException e) {
            InoriChan.LOGGER.error(e.getMessage(), e);

;
        }

        JSONObject json = null;
        if (response != null) {
            json = new JSONObject(response.getBody());
        }
        String str = null;
        if (json != null) {
            str = json.getJSONObject("data").getString("url");
        }

        commandEvent.reply("By "+commandEvent.getMember().getAsMention()+"\n"+str);





    }
}
