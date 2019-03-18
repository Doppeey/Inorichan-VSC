package EventsAndCommands.FunEvents;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.bson.Document;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class OofiesAndLmaosEvent extends ListenerAdapter {

    //Constructor
    public OofiesAndLmaosEvent(MongoDatabase database) {
        this.oofsAndLmaosCollection = database.getCollection("oofsAndLmaos");

    }

    //Variables
    private final MongoCollection<Document> oofsAndLmaosCollection;
    private final String lmaoID = "521646894431076353";
    private final String guildID = "272761734820003841";
    private final String oofID = "510948096184680463";
    private HashSet<String> reactedMessages = new HashSet<>();
    private boolean isOofOrLmaoEmote;


    //Event Method
    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {

        //METHOD VARIABLES
        final Guild guild = event.getGuild();
        final TextChannel eventChannel = event.getChannel();
        Message message = null;
        try {
            message = eventChannel.getMessageById(event.getMessageId()).submit().get();
        } catch (InterruptedException | ExecutionException e) {
            // nuffn
            return;
        }

        Emote oof = event.getGuild().getEmoteById(oofID);
        Emote lmao = event.getGuild().getEmoteById(lmaoID);
        Message.Attachment attachment = null;

        if(message.getAuthor().isBot()){
            return;
        }


        if (!eventChannel.getId().equalsIgnoreCase("521647171871703040")) { // Don't react to reactions in the broadcast channel


            try {
                if (event.getReactionEmote().getId().equalsIgnoreCase(lmaoID) || event.getReactionEmote().getId().equalsIgnoreCase(oofID)) {
                    isOofOrLmaoEmote = true;
                }


            } catch (Exception e) {
                isOofOrLmaoEmote = false;
            }


            try {
                attachment = message.getAttachments().get(0);
            } catch (Exception e) {
                //do nothing
            }


            final List<MessageReaction> messageReactionList;
            messageReactionList = message.getReactions();


            if (guild.getId().equalsIgnoreCase(guildID)) {
                if (!messageReactionList.contains(oof) && !messageReactionList.contains(lmao)) {

                    if (isOofOrLmaoEmote) {

                        int howManyOOfiesAndLmaos = 0;

                        for (MessageReaction m : messageReactionList) {
                            try {
                                if (m.getReactionEmote().getId().equals(oofID) || m.getReactionEmote().getId().equals(lmaoID)) {
                                    howManyOOfiesAndLmaos++;
                                }
                            } catch (NullPointerException n) {
                                /* It will throw nullpointer when it's not a custom emoji.
                                    However this does not matter and can be ignored because
                                    it does not affect the functionality
                                 */

                            }
                        }

                        if (howManyOOfiesAndLmaos == 1) {


                            if (oofsAndLmaosCollection.find(new Document("id", message.getId())).first() == null) {
                                EmbedBuilder embed = new EmbedBuilder();


                                if (attachment != null && attachment.isImage()) {
                                    embed.setImage(attachment.getUrl());
                                }
                                embed.setAuthor(message.getAuthor().getName(), message.getAuthor().getAvatarUrl(), message.getAuthor().getEffectiveAvatarUrl());
                                embed.setDescription(message.getContentRaw()).setColor(Color.ORANGE);
                                embed.appendDescription("   [Link](" + message.getJumpUrl() + ")");


                                String channelID = "521647171871703040";
                                Message finalMessage = message;
                                event.getGuild().getTextChannelById(channelID).sendMessage(embed.build()).queue(x -> {

                                    x.addReaction(event.getReactionEmote().getEmote()).queue();

                                    Document document = new Document();
                                    document.append("id",finalMessage.getId());
                                    document.append("channel",finalMessage.getTextChannel().getId());
                                    oofsAndLmaosCollection.insertOne(document);
                                });

                                System.out.println("Added a message [ID: " + message.getId() + "] to the offsAndLmao channel.");
                            }




                        }
                    }

                }
            }


        }
    }
}
