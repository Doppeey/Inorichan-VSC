package EventsAndCommands.UtilityCommands;

import EventsAndCommands.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.EmbedBuilder;
import okio.Utf8;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.File;
import java.net.URL;
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

        HttpResponse<String> response = null;

        try {
            response = Unirest.get("https://od-api.oxforddictionaries.com/api/v1/entries/en/" + wordToDefine + "/regions=us")
                    .header("app_id", "cd5250e8")
                    .header("app_key", "b504b23d94231e2cb8758db372c6c0cb")
                    .header("cache-control", "no-cache")
                    .asString();

        } catch (UnirestException e) {
            event.reply("Oopsie doopsie I did an ouchie wouchie uwu, something went wrong!", x -> x.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();


        assert response != null;
        try {


            JSONObject responseJson = new JSONObject(response.getBody());
            JSONArray resultArray = responseJson.getJSONArray("results");


            //DEFINITION
            String definition = resultArray.getJSONObject(0)
                    .getJSONArray("lexicalEntries")
                    .getJSONObject(0)
                    .getJSONArray("entries")
                    .getJSONObject(0)
                    .getJSONArray("senses")
                    .getJSONObject(0)
                    .getJSONArray("definitions")
                    .getString(0);
            embedBuilder.addField("Definition", definition, true);


            //EXAMPLES
            try{
                StringBuilder examples = new StringBuilder();
                JSONArray examplesArray = resultArray.getJSONObject(0)
                        .getJSONArray("lexicalEntries")
                        .getJSONObject(0)
                        .getJSONArray("entries")
                        .getJSONObject(0)
                        .getJSONArray("senses")
                        .getJSONObject(0)
                        .getJSONArray("examples");


                for (int i = 0; i < examplesArray.length(); i++) {
                    examples.append(examplesArray.getJSONObject(i).getString("text")).append(".\n");
                }

                embedBuilder.addField("Examples",examples.toString(),false);



            } catch ( Exception e){
                // no examples
            }



            // ORIGIN
            try {
                String origin =  resultArray.getJSONObject(0)
                        .getJSONArray("lexicalEntries")
                        .getJSONObject(0)
                        .getJSONArray("entries")
                        .getJSONObject(0)
                        .getJSONArray("etymologi" +
                                "es")
                        .getString(0);
                if (origin != null) {
                    embedBuilder.addField("Origin", origin, true);
                }
            } catch (Exception e) {
                //
            }






            StringBuilder derivatives = new StringBuilder();
            try{
                for (int i = 0; i < resultArray.getJSONObject(0)
                        .getJSONArray("lexicalEntries")
                        .getJSONObject(0)
                        .getJSONArray("derivatives").length(); i++) {

                    try {
                        derivatives.append(resultArray.getJSONObject(0)
                                .getJSONArray("lexicalEntries")
                                .getJSONObject(0)
                                .getJSONArray("derivatives")
                                .getJSONObject(i)
                                .getString("text") + "\n");



                    } catch (Exception f){
                        //
                    }

                }

                if(!derivatives.toString().isEmpty()){
                    embedBuilder.addField("Derivatives",derivatives.toString(),false);
                }



            } catch (Exception e){
                //
            }
            embedBuilder.setColor(Color.green);

            event.reply(embedBuilder.build());
            try {
                String URL = null;

                for (int i = 0; i < resultArray.getJSONObject(0)
                        .getJSONArray("lexicalEntries")
                        .getJSONObject(0)
                        .getJSONArray("pronunciations").length(); i++) {
                    try {

                        URL = resultArray.getJSONObject(0)
                                .getJSONArray("lexicalEntries")
                                .getJSONObject(0)
                                .getJSONArray("pronunciations")
                                .getJSONObject(i)
                                .getString("audioFile");


                    } catch (Exception e) {
                        //
                    }


                }

                if (URL != null) {
                    FileUtils.copyURLToFile(
                            new URL(URL),
                            new File("pronunciation.mp3"),
                            100000,
                            100000);
                }

                File file = new File("pronunciation.mp3");
                event.getChannel().sendFile(file).queue();


            } catch (Exception e) {
              e.event.getJDA().getGuildById("272761734820003841").getTextChannelById("552931145579495424").sendMessage(event.getMessage()).queue();
;
            }
        } catch (Exception | Error e) {

            e.event.getJDA().getGuildById("272761734820003841").getTextChannelById("552931145579495424").sendMessage(event.getMessage()).queue();
;
            event.reply("Oopsie doopise owie wowie the word was not fownd uwu", x -> x.delete().queueAfter(5, TimeUnit.SECONDS));
            event.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);

        }


    }
}
