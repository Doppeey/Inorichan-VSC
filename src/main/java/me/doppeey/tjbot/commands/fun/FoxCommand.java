package me.doppeey.tjbot.commands.fun;

import me.doppeey.tjbot.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import me.doppeey.tjbot.InoriChan;
import net.dv8tion.jda.core.EmbedBuilder;
import org.json.JSONObject;


public class FoxCommand extends Command {


    public FoxCommand() {
        this.name = "fox";
        this.help = "Gets a random fox picture";
        this.category = Categories.AnimalPictures;
        this.cooldownScope = CooldownScope.USER;
        this.cooldown = 10;
    }


    @Override
    protected void execute(CommandEvent commandEvent) {

        HttpResponse<String> response = null;


        try {
            response = Unirest.get("https://randomfox.ca/floof/").asString();
        } catch (Exception e) {

            InoriChan.LOGGER.error(e.getMessage(), e);


        }

        JSONObject json = new JSONObject(response.getBody());

        String imageUrl = json.getString("image");

        commandEvent.reply(new EmbedBuilder()
                .setImage(imageUrl)
                .build()
        );

    }


}


