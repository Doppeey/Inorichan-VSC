package me.doppey.tjbot.events.utility;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.bson.Document;

public class HelpMessageCountingEvent extends ListenerAdapter {

    private MongoCollection<Document> helpMessages;

    public HelpMessageCountingEvent(MongoDatabase database) {

        this.helpMessages = database.getCollection("helpMessages");

    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        // Variables
        final String channelName = event.getChannel().getName().toLowerCase();
        final String authorID = event.getAuthor().getId();
        final String eventContentRaw = event.getMessage().getContentRaw();
        final boolean doppeyIsCallingResetCommand = authorID.equalsIgnoreCase("272158112318750720")
                && eventContentRaw.equalsIgnoreCase(">resethelpermessages");

        if (doppeyIsCallingResetCommand) {
            resetHelpMessageDatabase(event);
            return;
        }

        if (channelName.contains("help") || channelName.contains("review")) {
            if(eventContentRaw.contains(">tag free")||eventContentRaw.contains("?tag free")){
                return;
            }
            IncrementMessageCounter(event);
        }

    }

    private void resetHelpMessageDatabase(GuildMessageReceivedEvent event) {
        helpMessages.deleteMany(new Document());
        event.getMessage().addReaction("✅").queue();
    }

    private void IncrementMessageCounter(GuildMessageReceivedEvent event) {
        Document searchTarget = new Document();
        searchTarget.put("_id", event.getAuthor().getId());

        if (helpMessages.find(searchTarget).first() == null) {

            helpMessages.insertOne(new Document().append("_id", event.getAuthor().getId())
                    .append("username", event.getAuthor().getName()).append("messageCount", 1)

            );

        } else {

            Document searchCondition = new Document().append("_id", event.getAuthor().getId());

            Document updatedValue = new Document().append("$inc", new Document().append("messageCount", 1));

            helpMessages.updateOne(searchCondition, updatedValue);

        }
    }
}
