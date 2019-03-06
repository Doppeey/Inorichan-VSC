package EventsAndCommands.FunCommands;

import EventsAndCommands.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.EmbedBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;


public class DogCommand extends Command {

    private HttpResponse<String> response = null;
    private final HashMap<String, Integer> dogIds;
    private String apiKey;

    public DogCommand(Properties config) {
        this.apiKey = config.getProperty("DOG_API_KEY");
        this.name = "dog";
        this.help = "Gets dog pictures with extra info about breed etc.";
        this.category = Categories.AnimalPictures;


        HttpResponse<String> response = null;

        try {
            response = Unirest.get("https://api.thedogapi.com/v1/breeds?limit=500")
                    .header("Content-Type", "application/json")
                    .header("cache-control", "no-cache")
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();

;
        }


        JSONArray dogbreeds = null;
        if (response != null) {
            dogbreeds = new JSONArray(response.getBody());
        }
        HashSet<JSONObject> dogJsonObjects = new HashSet<>();
        HashMap<String, Integer> dogIdMap = new HashMap<>();

        if (dogbreeds != null) {
            for (int i = 0; i < dogbreeds.length() - 1; i++) {
                dogJsonObjects.add(dogbreeds.getJSONObject(i));
            }
        }

        for (JSONObject dog : dogJsonObjects) {

            dogIdMap.put(dog.getString("name").toLowerCase(), dog.getInt("id"));

        }

        this.dogIds = dogIdMap;

    }

    @Override
    protected void execute(CommandEvent commandEvent) {

        if (commandEvent.getArgs().isEmpty()) {
            postRandomDog(commandEvent);
        } else if (commandEvent.getArgs().toLowerCase().contains("breed")) {
            try {
                String breedInfoSearchQuery = commandEvent.getArgs().toLowerCase().split("breed")[1].trim();
                int dogID = 0;
                for (String d : dogIds.keySet()) {
                    if (d.contains(breedInfoSearchQuery)) {
                        dogID = dogIds.get(d);
                        break;
                    }
                }
                HttpResponse<String> response = Unirest.get("https://api.thedogapi.com/v1/breeds/" + dogID)
                        .header("Content-Type", "application/json")
                        .header("cache-control", "no-cache")
                        .asString();

                JSONObject breedInfo = new JSONObject(response.getBody());
                EmbedBuilder embed = new EmbedBuilder();
                for (String key : breedInfo.keySet()) {
                    final String breedValue = breedInfo.get(key).toString();
                    switch (key) {

                        case "life_span":
                            embed.addField("Lifespan", breedValue, false);
                            break;
                        case "breed_group":
                            embed.addField("Breed Group", breedValue, false);
                            break;
                        case "temperament":
                            embed.addField("Temperament", breedValue, false);
                            break;
                        case "name":
                            embed.addField("Name", breedValue, false);
                            break;
                        case "bred_for":
                            embed.addField("Bred for", breedValue, false);
                            break;
                        case "weight":
                            final JSONObject breedWeight = breedInfo.getJSONObject("weight");
                            embed.addField("Weight", "Imperial:   " + breedWeight.getString("imperial") + "\n" + "Metric:      " + breedWeight.getString("metric"), false);
                            break;
                        case "height":
                            final JSONObject breedHeight = breedInfo.getJSONObject("height");
                            embed.addField("Height", "Imperial:   " + breedHeight.getString("imperial") + "\nMetric:      " + breedHeight.getString("metric"), false);
                            break;


                    }

                }
                commandEvent.reply(embed.build());
            } catch (Exception e) {
                commandEvent.reply("Couldn't find breed");
                e.printStackTrace();

;

            }


        }



    }


    private void postRandomDog(CommandEvent commandEvent) {
        try {

            response = Unirest.get("https://api.thedogapi.com/v1/images/search")
                    .header("Content-Type", "application/json")
                    .header("x-api-key", apiKey)
                    .header("cache-control", "no-cache")
                    .header("Postman-Token", "aa5a56f4-20b5-4145-a45f-82db8e9db253")
                    .queryString("size", "small")
                    .queryString("format", "json")
                    .queryString("order", "RANDOM")
                    .queryString("page", 0)
                    .queryString("limit", 1)
                    .queryString("has_breeds", "true")
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();

;
        }

        JSONArray jsonArray = new JSONArray(response.getBody());
        final JSONObject jsonObject = jsonArray.getJSONObject(0);
        JSONArray breedArray = jsonObject.getJSONArray("breeds");
        String breedName = breedArray.getJSONObject(0).getString("name");
        String url = jsonObject.getString("url");


        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Random woofer just for you :dog: The breed is: " + breedName);
        embed.setImage(url);
        commandEvent.reply(embed.build());
    }
}
