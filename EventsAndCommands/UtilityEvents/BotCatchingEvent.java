package EventsAndCommands.UtilityEvents;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.GuildController;
import org.bson.Document;

import java.awt.*;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;


public class BotCatchingEvent extends ListenerAdapter {

    private final MongoCollection<Document> botSuspicionCollection;
    private String botSuspicionChannelId = "546416238922956845";


    public BotCatchingEvent(MongoDatabase database) {
        this.botSuspicionCollection = database.getCollection("botSuspicion");
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if(event.getMessage().getContentRaw().equalsIgnoreCase("loadzebotz")){

            event.getGuild().getMembers().forEach( member -> {

                if(member.getUser().getName().matches("^(?=(?:\\D*\\d){2})[a-zA-Z0-9]*$")) {

                    final OffsetDateTime creationTime = member.getUser().getCreationTime();

                    long days = creationTime.until(OffsetDateTime.now(), ChronoUnit.DAYS);
                    long hours = creationTime.until(OffsetDateTime.now(), ChronoUnit.HOURS);
                    long minutes = creationTime.until(OffsetDateTime.now(),ChronoUnit.MINUTES);

                    String dayDescriptor = days == 1 ? "Day":"Days";



                    if (days <= 30) {
                        EmbedBuilder embedBuilder = new EmbedBuilder();
                        embedBuilder.setAuthor(member.getUser().getName());
                        embedBuilder.addField("Account created", days + " "+dayDescriptor+", "+hours+" Hours, "+minutes+" Minutes ago", true);
                        embedBuilder.addField("User as Mention", member.getUser().getAsMention(), true);
                        embedBuilder.setFooter("ID: " + member.getUser().getId(), null);
                        embedBuilder.setColor(Color.red);

                        event.getGuild().getTextChannelById(botSuspicionChannelId).sendMessage(embedBuilder.build()).queue(messageSent -> {

                            messageSent.addReaction("\uD83D\uDD25").queue(reactionAdded -> {

                                Document report = new Document();
                                report.put("messageId", messageSent.getId());
                                report.put("userId", member.getUser().getId());

                                botSuspicionCollection.insertOne(report);

                            });

                        });


                    }
                }

            });
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (!event.getUser().isBot()) {
            if (event.getTextChannel().getId().equalsIgnoreCase(botSuspicionChannelId))
                if (botSuspicionCollection.find(new Document("messageId", event.getMessageId())).first() != null) {

                    Document userDoc = botSuspicionCollection.find(new Document("messageId", event.getMessageId())).first();
                    GuildController gc = new GuildController(event.getGuild());
                    gc.ban(userDoc.getString("userId"), 0).queue();
                    event.getTextChannel().getMessageById(event.getMessageId()).queue(received -> received.delete().queue());
                }
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {


        User user = event.getUser();
        String username = user.getName();

        boolean lessThanAWeekOld = OffsetDateTime.now().minus(7, ChronoUnit.DAYS).isBefore(user.getCreationTime());
        boolean matchesNameCriteria = user.getName().matches("^(?=(?:\\D*\\d){2})[a-zA-Z0-9]*$");
        boolean noProfilePicture = user.getAvatarUrl() == null;
        final OffsetDateTime creationTime = user.getCreationTime();

        creationTime.until(OffsetDateTime.now(),ChronoUnit.MINUTES);

        long days = creationTime.until(OffsetDateTime.now(), ChronoUnit.DAYS);
        long hours = creationTime.until(OffsetDateTime.now(), ChronoUnit.HOURS) - (days * 24);


        String dayDescriptor = days == 1 ? "Day":"Days";
        String hourDescriptor = hours == 1 ? "Hour":"Hours";

        if (matchesNameCriteria && noProfilePicture) {

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setAuthor(username);
            embedBuilder.addField("Account created", days + " "+dayDescriptor+", "+hours+" "+hourDescriptor+" ago", true);
            embedBuilder.setFooter("ID: " + user.getId(), null);
            embedBuilder.setColor(Color.red);


            event.getGuild().getTextChannelById(botSuspicionChannelId).sendMessage(embedBuilder.build()).queue(messageSent -> {

                messageSent.addReaction("\uD83D\uDD25").queue(reactionAdded -> {

                    Document report = new Document();
                    report.put("messageId", messageSent.getId());
                    report.put("userId", user.getId());

                    botSuspicionCollection.insertOne(report);

                });

            });


        }


    }
}
