package me.doppey.tjbot.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import me.doppey.tjbot.Categories;
import me.doppey.tjbot.InoriChan;
import net.dv8tion.jda.core.EmbedBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class DefinitionCommand extends Command {


    public DefinitionCommand() {
        this.name = "define";
        this.help = "Finds the definition of a word";
        this.category = Categories.Utility;
    }

    @Override
    protected void execute(CommandEvent event) {

        String wordToDefine = URLEncoder.encode(event.getArgs(), StandardCharsets.UTF_8);

        HttpResponse<JsonNode> response;

        try {
            response = Unirest.get("https://mashape-community-urban-dictionary.p.rapidapi.com/define")
                    .header("X-RapidAPI-Key", "f280f864c0mshf1d2aa90f4e5b26p16b045jsn5444bbfa5484")
                    .queryString("term", wordToDefine)
                    .asJson();

        } catch (UnirestException e) {
            event.reply("Oopsie doopsie I did an ouchie wouchie uwu, something went wrong!", x -> x.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();


        JSONObject responseJson = new JSONObject(response.getBody());
        JSONArray resultArray = responseJson.getJSONArray("array");

        try {

            //DEFINITION
            String definition = resultArray.getJSONObject(0).getJSONArray("list").getJSONObject(0).getString("definition");
            embedBuilder.addField("Definition", definition, true);
        } catch (org.json.JSONException e) {
            event.reply("Word not found :( ");
            return;
        }


        //EXAMPLES
        try {
            String example = resultArray.getJSONObject(0).getJSONArray("list").getJSONObject(0).getString("example");
            embedBuilder.addField("Example", example, false);


        } catch (Exception e) {
            // no examples
        }


        embedBuilder.setColor(Color.blue);
        event.reply(embedBuilder.build());
        InoriChan.LOGGER.info("User "+event.getMember().getEffectiveName()+" has used the DEFINE command to look up the word "+wordToDefine);

    }
}
