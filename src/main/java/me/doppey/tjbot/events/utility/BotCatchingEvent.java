package me.doppey.tjbot.events.utility;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ISnowflake;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.GuildController;
import org.bson.Document;

import java.awt.Color;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;


public class BotCatchingEvent extends ListenerAdapter {

    private static final String FIRE_EMOJI = "\uD83D\uDD25";
    private static final String TRASHCAN_EMOJI = "\uD83D\uDDD1";
    private static final String BOT_SUSPICION_CHANNEL_ID = "546416238922956845";
    private final MongoCollection<Document> botSuspicionCollection;

    public BotCatchingEvent(MongoDatabase database) {
        this.botSuspicionCollection = database.getCollection("botSuspicion");
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (!event.getUser().isBot() && event.getTextChannel().getId().equalsIgnoreCase(BOT_SUSPICION_CHANNEL_ID)) {
            if (event.getReactionEmote().getName().equals(FIRE_EMOJI)) {
                runBotSuspicionReact(event, true);
            } else if (event.getReactionEmote().getName().equals(TRASHCAN_EMOJI)) {
                runBotSuspicionReact(event, false);
            }
        }
    }

    private void runBotSuspicionReact(MessageReactionAddEvent event, boolean ban) {
        event.getTextChannel().getMessageById(event.getMessageId()).queue(received -> received.delete().queue());
        Document userDoc = botSuspicionCollection.find(new Document("messageId", event.getMessageId())).first();
        if (userDoc != null) {
            if (ban) {
                GuildController gc = new GuildController(event.getGuild());
                gc.ban(userDoc.getString("userId"), 0).queue();
            }
            botSuspicionCollection.deleteOne(userDoc);
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        User user = event.getUser();

        boolean lessThanAWeekOld =
                OffsetDateTime.now(ZoneOffset.UTC).minus(1, ChronoUnit.WEEKS).isBefore(user.getCreationTime());
        boolean matchesNameCriteria = user.getName().matches("^(?=(?:\\D*\\d){2})[a-zA-Z0-9]*$");

        if (matchesNameCriteria && lessThanAWeekOld) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setAuthor(user.getName());
            embedBuilder.addField("Account created", getPrettyCreationTime(user), true);
            embedBuilder.setFooter("ID: " + user.getId(), null);
            embedBuilder.setColor(Color.RED);

            event.getGuild().getTextChannelById(BOT_SUSPICION_CHANNEL_ID).sendMessage(embedBuilder.build()).queue(messageSent -> {
                messageSent.addReaction(FIRE_EMOJI).queue();
                messageSent.addReaction(TRASHCAN_EMOJI).queue();

                Document report = new Document();
                report.put("messageId", messageSent.getId());
                report.put("userId", user.getId());

                botSuspicionCollection.insertOne(report);
            });
        }
    }

    private String getPrettyCreationTime(ISnowflake id) {
        OffsetDateTime creationTime = id.getCreationTime();
        Duration accountAge = Duration.between(creationTime, OffsetDateTime.now(ZoneOffset.UTC));

        StringBuilder result = new StringBuilder();

        if (accountAge.toDays() > 0) {
            long days = accountAge.toDays();
            result.append(days).append(days == 1 ? " Day, " : " Days, ");
            accountAge = accountAge.minusDays(days);
        }

        long hours = accountAge.toHours();
        result.append(hours)
                .append(hours == 1 ? " Hour" : " Hours")
                .append(" ago");

        return result.toString();
    }
}
