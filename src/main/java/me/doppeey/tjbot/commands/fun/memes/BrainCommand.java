package me.doppeey.tjbot.commands.fun.memes;

import me.doppeey.tjbot.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.util.Properties;

public class BrainCommand extends Command {

    String memeId = "93895088";
    Properties config;

    public BrainCommand(Properties config){
        this.name = "brain";
        this.help = "usage: >brain a,b,c,d";
        this.category = Categories.Memes;
        this.config = config;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {

        String[] parameters = commandEvent.getArgs().split(";");

        if(parameters.length < 4){
            commandEvent.reply("Couldn't get the parameters, make sure you use four sentences split by commas.");
            return;
        }



        String apiUsername = this.config.getProperty("IMGFLIP_USERNAME");
        String apiPassword = this.config.getProperty("IMGFLIP_PASSWORD");

        HttpResponse<String> response = null;

        try {
            response = com.mashape.unirest.http.Unirest.get("https://api.imgflip.com/caption_image")
                    .queryString("username", apiUsername)
                    .queryString("template_id", memeId)
                    .queryString("password", apiPassword)
                    .queryString("boxes[0][type]","text")
                    .queryString("boxes[0][text]", parameters[0])
                    .queryString("boxes[0][x]",10)
                    .queryString("boxes[0][y]",0)
                    .queryString("boxes[0][height]",350)
                    .queryString("boxes[0][width]",400)

                    .queryString("boxes[1][type]","text")
                    .queryString("boxes[1][text]", parameters[1])
                    .queryString("boxes[1][x]",10)
                    .queryString("boxes[1][y]",275)
                    .queryString("boxes[1][height]",350)
                    .queryString("boxes[1][width]",400)

                    .queryString("boxes[2][type]","text")
                    .queryString("boxes[2][text]", parameters[2])
                    .queryString("boxes[2][x]",10)
                    .queryString("boxes[2][y]",550)
                    .queryString("boxes[2][height]",350)
                    .queryString("boxes[2][width]",400)

                    .queryString("boxes[3][type]","text")
                    .queryString("boxes[3][text]", parameters[3])
                    .queryString("boxes[3][x]",10)
                    .queryString("boxes[3][y]",855)
                    .queryString("boxes[3][height]",350)
                    .queryString("boxes[3][width]",400)


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



        commandEvent.reply(str);





    }
}
