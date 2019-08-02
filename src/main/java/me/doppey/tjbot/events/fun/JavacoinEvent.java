package me.doppey.tjbot.events.fun;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.doppey.tjbot.InoriChan;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.bson.Document;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class JavacoinEvent extends ListenerAdapter {

    MongoCollection javacoinCollection;

    public JavacoinEvent(MongoDatabase db) {
        javacoinCollection = db.getCollection("javacoins");
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String channelName = event.getChannel().getName().toLowerCase();

        try {
            event.getMessage();
        } catch (NullPointerException e) {
            return;
        }

        //Don't count the >coins message
        if (event.getMessage().getContentRaw().strip().toLowerCase().equals(">coins")) {
            return;
        }

        //Don't count messages in testing servers
        if (!event.getGuild().getId().equalsIgnoreCase("272761734820003841")) {
            return;
        }

        //Don't count messages in these channels
        if (channelName.contains("spam")) {
            return;
        }

        //If the user does not yet exist in the database
        final String userId = event.getAuthor().getId();
        if (javacoinCollection.find(new Document("userId", userId)).first() == null) {
            Document doc = new Document()
                    .append("userId", userId)
                    .append("javacoins", 50)
                    .append("dailyTime", LocalDateTime.now().toInstant(ZoneOffset.UTC))
                    .append("lastMessageTime", LocalDateTime.now().toInstant(ZoneOffset.UTC));

            InoriChan.LOGGER.info("User " + event.getMember().getEffectiveName() + " has received their 25 daily " +
                    "javacoins!");
            javacoinCollection.insertOne(doc);
            return;
        }

        // If the name is found in the database, check if its been 24h since last message (if yes they get 25 coins
        // for first message of the day)
        Document searchDoc = new Document().append("userId", userId);
        Document userDoc = (Document) javacoinCollection.find(searchDoc).first();
        LocalDateTime userDateTime =
                LocalDateTime.from(((Date) userDoc.get("dailyTime")).toInstant().atZone(ZoneOffset.UTC));
        LocalDateTime userLastMessageTime =
                LocalDateTime.from(((Date) userDoc.get("lastMessageTime")).toInstant().atZone(ZoneOffset.UTC));

        //Check for daily
        if (ChronoUnit.DAYS.between(userDateTime, LocalDateTime.now().atZone(ZoneOffset.UTC)) >= 1) {
            javacoinCollection.updateOne(searchDoc, new Document("$set", new Document("dailyTime",
                    LocalDateTime.now().toInstant(ZoneOffset.UTC))));
            javacoinCollection.updateOne(searchDoc, new Document("$set", new Document("javacoins",
                    userDoc.getInteger("javacoins") + 25)));
            InoriChan.LOGGER.info("User " + event.getMember().getEffectiveName() + " has received their 25 daily " +
                    "javacoins!");
            return;
        }

        //Check for cooldown
        if (ChronoUnit.MINUTES.between(userLastMessageTime, LocalDateTime.now().atZone(ZoneOffset.UTC)) >= 1) {
            javacoinCollection.updateOne(searchDoc, new Document("$set", new Document("javacoins",
                    userDoc.getInteger("javacoins") + 1)));
            javacoinCollection.updateOne(searchDoc, new Document("$set", new Document("lastMessageTime",
                    LocalDateTime.now().toInstant(ZoneOffset.UTC))));
            return;
        }
    }
}
