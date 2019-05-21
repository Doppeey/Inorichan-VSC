package me.doppeey.tjbot.commands.utility;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import org.json.JSONObject;

import me.doppeey.tjbot.Categories;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

/**
 * unsplashCommand
 */
public class unsplashCommand extends Command {

    Properties config;
    EventWaiter waiter;

    public unsplashCommand(Properties property, EventWaiter waiter) {
        this.config = property;
        this.waiter = waiter;
        this.name = "unsplash";
        this.help = "usage: >unsplash searchQuery";
        this.category = Categories.Utility;
        this.cooldownScope = CooldownScope.USER;
        this.cooldown = 30;
    }

    @Override
    protected void execute(CommandEvent event) {

        if(!event.getTextChannel().getName().toLowerCase().contains("bot")){
            event.reply("Due to spam, this command is only available in the bots channel", x -> x.delete().queueAfter(4,TimeUnit.SECONDS));
            event.getMessage().delete().queueAfter(4,TimeUnit.SECONDS);
            return;
        }

        final int[] counter = new int[1];

        try {
            String searchQuery = URLEncoder.encode(event.getArgs(), "UTF-8");

            HttpResponse<String> response = Unirest.get("https://api.unsplash.com/search/photos/")
                    .header("Accept-Version", "v1").header("cache-control", "no-cache")
                    .queryString("client_id", config.getProperty("UNSPLASH_ID")).queryString("query", searchQuery)
                    .asString();

            JSONObject json = new JSONObject(response.getBody());

            ArrayList<String> imageUrls = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                try {
                    imageUrls
                            .add(json.getJSONArray("results").getJSONObject(i).getJSONObject("urls").getString("full"));
                } catch (Exception e) {
                    // Less than 10 or zero pictures found
                }
            }

            
            counter[0] = 0;

            event.reply(imageUrls.get(0), sent -> {

                sent.addReaction("\u2B05").queue(added -> sent.addReaction("\u27A1").queue(reactionsAdded -> {

                    extracted(event, counter, imageUrls, sent);

                }));

            });

        } catch (Exception e) {
            event.reply("Error: Either the ratelimit is reached or no images were found using your query!", msg -> {
                msg.delete().queueAfter(3, TimeUnit.SECONDS);
                event.getMessage().delete().queueAfter(3, TimeUnit.SECONDS);
            });
        }
    }

    private void extracted(CommandEvent event, final int[] counter, ArrayList<String> imageUrls, Message sent) {
        waiter.waitForEvent(MessageReactionAddEvent.class, addEvent -> event.getAuthor().equals(addEvent.getUser()),
                correct -> {

                    if (correct.getMessageId().equals(sent.getId())) {
                        // IF THE REACTION IS A LEFT ARROW
                        if (correct.getReactionEmote().getName().equals("⬅")) {
                            if (counter[0] != 0) {
                                counter[0] = counter[0] - 1;
                            }

                        else {
                                counter[0] = imageUrls.size() - 1;
                            }

                            
                            sent.editMessage(imageUrls.get(counter[0])).queue();
                            correct.getReaction().removeReaction(event.getAuthor()).queue();

                            //WAIT FOR THE NEXT REACTION
                            extracted(event, counter, imageUrls, sent);
                        }

                        // IF THE REACTION IS A RIGHT ARROW
                    else if (correct.getReactionEmote().getName().equals("➡")) {
                            if (counter[0] != imageUrls.size() - 1) {
                                counter[0] = counter[0] + 1;
                            } else {

                                counter[0] = 0;
                            }

                            //WAIT FOR THE NEXT REACTION
                            sent.editMessage(imageUrls.get(counter[0])).queue();
                            correct.getReaction().removeReaction(event.getAuthor()).queue();
                            extracted(event, counter, imageUrls, sent);

                        }
                    }

                }, 30, TimeUnit.SECONDS, null);
    }

}