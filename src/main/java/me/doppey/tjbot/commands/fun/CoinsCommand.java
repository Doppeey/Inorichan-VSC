package me.doppey.tjbot.commands.fun;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.doppey.tjbot.Categories;
import org.bson.Document;

public class CoinsCommand extends Command {

    MongoCollection javacoinsDb;

    public CoinsCommand(MongoDatabase db) {
        this.name = "coins";
        this.help = "Together Javas \"Javacoins\". Use `>Coins help`";
        this.category = Categories.Fun;
        this.javacoinsDb = db.getCollection("javacoins");
    }

    @Override
    protected void execute(CommandEvent event) {
        String userId = event.getAuthor().getId();

        //Only works on TJ
        if (!event.getGuild().getId().equalsIgnoreCase("272761734820003841")) {
            event.reply("This only works on together java");
            return;
        }

        if (event.getArgs().isEmpty()) {
            final FindIterable userIterable = javacoinsDb.find(new Document("userId", userId));

            if (userIterable.first() != null) {

                if (!event.getChannel().getName().toLowerCase().contains("bots")) {
                    event.getMessage().delete().queue();
                    event.getGuild().getTextChannelsByName("bots", true).get(0).sendMessage(event.getMember().getAsMention() + " You have " + "`" + ((Document) userIterable.first()).getInteger("javacoins") + "` javacoins.").queue();
                    return;
                }

                event.reply("You have " + "`" + ((Document) userIterable.first()).getInteger("javacoins") + "` " +
                        "javacoins.");
            }
        }
    }
}
