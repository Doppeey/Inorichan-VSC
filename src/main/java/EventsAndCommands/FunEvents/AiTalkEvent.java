package EventsAndCommands.FunEvents;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class AiTalkEvent extends ListenerAdapter {

    String apiKey;
    String sessionID;
    String chatbotId;

    public AiTalkEvent(Properties config){
        this.apiKey = config.getProperty("CHATBOT_API_KEY");
        this.chatbotId = config.getProperty("CHATBOT_ID");
        this.sessionID = config.getProperty("CHATBOT_SESSION_ID");
        lastRan = LocalDateTime.now();
    }
    /**
     * Takes the input as a parameters and sends it
     * to an API to get smart responses based on
     * what has been written already
     */

    LocalDateTime lastRan;

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {


        final List<Member> mentionedMembers = event.getMessage().getMentionedMembers();
        final TextChannel eventChannel = event.getChannel();

        //Check if bot has been mentioned
        if (!mentionedMembers.isEmpty()) {
            if (mentionedMembers.get(0).getUser() == event.getJDA().getSelfUser()) {

                //Check if it has been at least 5 seconds since the last call
                if (!hasCooldown() || event.getAuthor().getId().equals("332989789936549889")) {



                    HttpResponse<String> response = null;
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
                    try {
                        response = Unirest.post("https://botmakr.net/api/")
                                .header("cache-control", "no-cache")
                                .queryString("key", apiKey)
                                .queryString("id", chatbotId)
                                .queryString("sessionid", sessionID)
                                .queryString("q", message)
                                .asString();
                    } catch (Exception e) {
                        System.out.println("Error chatbot ai");
                        eventChannel.sendMessage("Something went wrong, you might be too fast").queue();
                        return;
                    }


                    JSONObject json = new JSONObject(response.getBody());


                    try {
                        String botResponse = json.getString("response");

                        eventChannel.sendMessage(botResponse).queue();
                        lastRan = LocalDateTime.now();
                    } catch (Exception e) {

                        eventChannel.sendMessage("Couldn't process your message, sorry.").queue(x -> x.delete().queueAfter(5,TimeUnit.SECONDS));

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
