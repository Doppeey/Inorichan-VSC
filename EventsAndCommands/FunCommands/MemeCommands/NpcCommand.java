package EventsAndCommands.FunCommands.MemeCommands;

import EventsAndCommands.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.util.Properties;

public class NpcCommand extends Command {

    private final String memeId;
    private HttpResponse<String> response = null;
    private Properties config;


    public NpcCommand(Properties config){
        this.config = config;
        this.name = "npc";
        this.help = "npc, usage: >npc [text here]";
        this.category = Categories.Memes;
        this.memeId = "166384716";
    }



    @Override
    protected void execute(CommandEvent commandEvent) {

        String memeText = commandEvent.getArgs();

        try {
            response = com.mashape.unirest.http.Unirest.get("https://api.imgflip.com/caption_image")
                    .queryString("username", config.getProperty("IMGFLIP_USERNAME"))
                    .queryString("template_id", this.memeId)
                    .queryString("password", config.getProperty("IMGFLIP_PASSWORD"))
                    .queryString("boxes[0][type]", "text")
                    .queryString("boxes[0][text]",memeText)
                    .queryString("boxes[0][x]",155)
                    .queryString("boxes[0][y]",125)
                    .queryString("boxes[0][width]",850)
                    .queryString("boxes[0][height]",250)
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

        commandEvent.reply("By "+commandEvent.getMember().getAsMention()+"\n"+str);





    }
}
