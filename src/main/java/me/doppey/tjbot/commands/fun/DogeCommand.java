package me.doppey.tjbot.commands.fun;

import me.doppey.tjbot.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import me.doppey.tjbot.InoriChan;
import net.dv8tion.jda.core.EmbedBuilder;
import org.json.JSONArray;

import java.awt.*;

public class DogeCommand extends Command {

    public DogeCommand() {

        this.name = "doge";
        this.help = "Gets a random Doge (Shiba Inu) picture.";
        this.category = Categories.AnimalPictures;


    }


    @Override
    protected void execute(CommandEvent commandEvent) {



        HttpResponse<String> response = null;


        try {
            response = Unirest.get("http://shibe.online/api/shibes?count=1&urls=true").asString();
        } catch (Exception e) {
          InoriChan.LOGGER.error(e.getMessage(), e);


        }





        JSONArray json = new JSONArray(response.getBody());
        String url = json.get(0).toString();

        commandEvent.reply(new EmbedBuilder()
                .setImage(url).setColor(Color.ORANGE).build());


    }
}
