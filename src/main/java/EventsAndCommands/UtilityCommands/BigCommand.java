package EventsAndCommands.UtilityCommands;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import EventsAndCommands.Categories;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Webhook;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;
import net.dv8tion.jda.webhook.WebhookMessage;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;

/**
 * BigCommand
 */
public class BigCommand extends Command {

    public BigCommand() {
        this.name = "big";
        this.help = "posts the big version of an emoji";
        this.category = Categories.Utility;

    }

    @Override
    protected void execute(CommandEvent event) {

        if(event.getArgs().isEmpty()){
            event.reply("Usage: >big + emoji");
            return;
        }


        event.getTextChannel().getWebhooks().queue(hooks -> {

            if (hooks.size() > 0) {

                Message message = event.getMessage();
                List<Emote> emotes = message.getEmotes();

                Webhook webhook = hooks.get(0);
                WebhookClientBuilder builder = webhook.newClient();
                WebhookClient client = builder.build();
                WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder();

                if (!emotes.isEmpty()) {
                    MessageChannel channel = event.getChannel();

                    try {

                        String imageUrl = emotes.get(0).getImageUrl();
                        InputStream is = new URL(imageUrl).openStream();

                        if (imageUrl.contains("gif")) {
                            messageBuilder.addFile("bigemoji.gif", is);
                        } else {
                            messageBuilder.addFile("bigemoji.jpg", is);
                        }

                        messageBuilder.setAvatarUrl(event.getAuthor().getAvatarUrl());
                        messageBuilder.setUsername(event.getMember().getEffectiveName());

                        WebhookMessage webMsg = messageBuilder.build();
                        client.send(webMsg);
                        client.close();

                        message.delete().queue();

                    } catch (IOException e) {
                        channel.sendMessage("Oopsie doopsie owie wowie, something went fuckie wuckie uwu")
                                .queue(x -> x.delete().queueAfter(5, TimeUnit.SECONDS));

                    }

                } else {

                    event.reply("no emoji found");
                }
            } else {

                EmbedBuilder error = new EmbedBuilder();
                error.setColor(Color.red);
                error.setTitle("⚠️ Big Emoji || No Webhook Error!");
                error.setDescription("This channel does not have a webhook!");

                event.reply(error.build());


            }

        });

    }

}