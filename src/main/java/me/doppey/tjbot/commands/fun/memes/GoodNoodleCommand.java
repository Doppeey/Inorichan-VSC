package me.doppey.tjbot.commands.fun.memes;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import me.doppey.tjbot.Categories;
import me.doppey.tjbot.Config;
import me.doppey.tjbot.InoriChan;
import org.json.JSONObject;

public class GoodNoodleCommand extends Command {
    private final String memeId;
    private HttpResponse<String> response = null;
    private Config config;
    private String apiPassword;
    private String apiUsername;


    public GoodNoodleCommand(Config config){
        this.config = config;
        this.name = "gn";
        this.help = "Good noodle board, usage: >gn [text here]";
        this.category = Categories.Memes;
        this.memeId = "164365625";
        this.apiPassword = config.getProperty("IMGFLIP_PASSWORD");
        this.apiUsername = config.getProperty("IMGFLIP_USERNAME");
    }



    @Override
    protected void execute(CommandEvent commandEvent) {

        String memeText = commandEvent.getArgs();

        try {

            response = com.mashape.unirest.http.Unirest.get("https://api.imgflip.com/caption_image")
                    .queryString("username", apiUsername)
                    .queryString("template_id", memeId)
                    .queryString("password", apiPassword)
                    .queryString("boxes[0][type]", "text")
                    .queryString("boxes[0][text]",memeText)
                    .queryString("boxes[0][x]",95)
                    .queryString("boxes[0][y]",580)
                    .queryString("boxes[0][width]",160)
                    .queryString("boxes[0][height]",45)
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
