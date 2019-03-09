package EventsAndCommands.FunCommands;

import EventsAndCommands.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.EmbedBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Properties;


public class CatCommand extends Command {

    private HttpResponse<String> response = null;


    JSONObject json = null;
    private String url = null;
    private Properties config;
    private String apiKey;


    public CatCommand(Properties config) {

        this.config=config;
        this.name = "cat";
        this.help = "Gets cat pictures, >cat help for a list of categories";
        this.category = Categories.AnimalPictures;
        this.apiKey = config.getProperty("CAT_API_KEY");

    }


    @Override
    protected void execute(CommandEvent commandEvent) {

        System.out.println("Cat command fired");
        int category = 0;
        String requestedCategory = "";

        Runnable fetchCat = () -> {
            int category1 = 0;
            String requestedCategory1 = "";

            switch (commandEvent.getArgs()) {

                case "hats":
                    category1 = 1;
                    requestedCategory1 = commandEvent.getArgs();
                    break;
                case "space":
                    category1 = 2;
                    requestedCategory1 = commandEvent.getArgs();
                    break;
                case "clothes":
                    category1 = 15;
                    requestedCategory1 = commandEvent.getArgs();
                    break;
                case "boxes":
                    category1 = 5;
                    requestedCategory1 = commandEvent.getArgs();
                    break;
                case "sunglasses":
                    category1 = 4;
                    requestedCategory1 = commandEvent.getArgs();
                    break;
                case "sinks":
                    category1 = 14;
                    requestedCategory1 = commandEvent.getArgs();
                    break;
                case "ties":
                    requestedCategory1 = commandEvent.getArgs();
                    category1 = 7;
                    break;


            }

            if (!commandEvent.getArgs().equalsIgnoreCase("categories") && !commandEvent.getArgs().equalsIgnoreCase("help")) {
                try {
                    if (category1 != 0) {
                        response = com.mashape.unirest.http.Unirest.get("https://api.thecatapi.com/v1/images/search?")
                                .header("x-api-key", apiKey)
                                .queryString("category_ids", category1)
                                .asString();
                    } else {
                        response = com.mashape.unirest.http.Unirest.get("https://api.thecatapi.com/v1/images/search?")
                                .header("x-api-key", apiKey)
                                .asString();
                    }
                } catch (
                        UnirestException e) {
                    System.out.println("Connection timeout at Cat command");
                    commandEvent.reply("Connection timeout, cat api does not react, try again later :(");
                    return;

                }

                if (response.getBody() != null) {
                    JSONArray jsonArray = new JSONArray(response.getBody());
                    JSONObject json = jsonArray.getJSONObject(0);

                    url = json.getString("url");


                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setImage(url);
                    if (!requestedCategory1.isEmpty()) {
                        embed.setTitle("You requested one of our cats in " + requestedCategory1 + ". Here you go! :cat:");
                    } else {
                        embed.setTitle("A random kitty, it's all yours! :cat:");
                    }
                    commandEvent.reply(embed.build());
                } else {
                    commandEvent.reply("Couldn't fetch a kitty, try again later :(");
                }
            } else {
                commandEvent.reply(new EmbedBuilder().setTitle("Cat Command").setDescription("Use **>cat [category]** to get a cat of that category or just **>cat** to get a random cat.")
                        .addField("Categories", "Hats\nSpace\nSinks\nTies\nBoxes\nClothes\nSunglasses", false).build());
            }
        };
        Thread thread = new Thread(fetchCat);
        thread.run();

    }


}

