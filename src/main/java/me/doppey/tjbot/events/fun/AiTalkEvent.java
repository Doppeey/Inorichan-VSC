package me.doppey.tjbot.events.fun;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import me.doppey.tjbot.Config;
import me.doppey.tjbot.InoriChan;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AiTalkEvent extends ListenerAdapter {
    String apiKey;
    String sessionID;
    String chatbotId;
    /**
     * Takes the input as a parameters and sends it
     * to an API to get smart responses based on
     * what has been written already
     */

    LocalDateTime lastRan;

    public AiTalkEvent(Config config) {
        this.apiKey = config.getProperty("CHATBOT_API_KEY");
        this.chatbotId = config.getProperty("CHATBOT_ID");
        this.sessionID = config.getProperty("CHATBOT_SESSION_ID");
        lastRan = LocalDateTime.now();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        final List<Member> mentionedMembers = event.getMessage().getMentionedMembers();
        final TextChannel eventChannel = event.getChannel();

        //Check if bot has been mentioned
        if (!mentionedMembers.isEmpty()) {
            if (mentionedMembers.get(0).getUser() == event.getJDA().getSelfUser()) {

                //Check if it has been at least 5 seconds since the last call
                if (!hasCooldown() || event.getAuthor().getId().equals("332989789936549889")) {
                    final HttpResponse<String>[] response = new HttpResponse[]{null};
                    String message = "";

                    //Make sure there is at leat one word supplied
                    try {
                        final String contentRaw = event.getMessage().getContentRaw();

                        message = contentRaw.substring(contentRaw.split(" ")[0].length());
                        String[] messages = message.split(" ");

                        if (messages.length < 2) {
                            eventChannel.sendMessage("I can't hear you").queue();
                            return;
                        }
                    } catch (Exception e) {
                        eventChannel.sendMessage("Error, no message supplied").queue();
                    }

                    //Send GET request to Botmakr
                    event.getChannel().sendTyping().queueAfter(1, TimeUnit.SECONDS);
                    String finalMessage = message;
                    Runnable getResponseRunnable = () -> {
                        try {
                            response[0] = Unirest.post("https://botmakr.net/api/")
                                    .header("cache-control", "no-cache")
                                    .queryString("key", apiKey)
                                    .queryString("id", chatbotId)
                                    .queryString("sessionid", sessionID)
                                    .queryString("q", finalMessage)
                                    .asString();
                        } catch (Exception e) {
                            //
                        }
                    };

                    Thread newThread = new Thread(getResponseRunnable);
                    newThread.start();

                    for (int i = 0; i < 10; i++) {
                        if (response[0] != null) {
                            break;
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) {
                        }
                    }

                    if (response[0] == null) {
                        InoriChan.LOGGER.error("Error chatbot ai");
                        eventChannel.sendMessage("Couldn't get a response from the AI Server, please try again in a bit").queue();
                        return;
                    }

                    JSONObject json = new JSONObject(response[0].getBody());

                    try {
                        String botResponse = json.getString("response");

                        eventChannel.sendMessage(botResponse).queue();
                        lastRan = LocalDateTime.now();
                    } catch (Exception e) {
                        eventChannel.sendMessage("Couldn't process your message, sorry.").queue(x -> x.delete().queueAfter(5, TimeUnit.SECONDS));
                    }
                } else {
                    lastRan = LocalDateTime.now();   // Set lastran to now, because there was a bug where it got stuck in the cooldown system

                    eventChannel.sendMessage("You're too fast, wait 5 seconds between messages").queue(x -> x.delete().queueAfter(10, TimeUnit.SECONDS));
                }
            }
        }
    }

    private boolean hasCooldown() {
        return LocalDateTime.now().minusSeconds(5).isBefore(lastRan);
    }
}
