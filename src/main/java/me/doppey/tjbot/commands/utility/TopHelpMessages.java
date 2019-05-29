package me.doppey.tjbot.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import org.bson.Document;

import java.awt.*;
import java.util.ArrayList;

import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Sorts.orderBy;

public class TopHelpMessages extends Command {

    MongoCollection helpMessages;

    public TopHelpMessages(MongoDatabase mongodb) {

        this.helpMessages = mongodb.getCollection("helpMessages");
        this.name = "tophelpmessages";
        this.help = "Shows the top active people in help channels. Up to 15";

    }

    @Override
    protected void execute(CommandEvent commandEvent) {

        int amountOfHelpers = 10;

        boolean hasRequestedOwnCount = commandEvent.getArgs().toLowerCase().contains("me");

        if (hasRequestedOwnCount == false) {
            try {
                amountOfHelpers = Integer.parseInt(commandEvent.getArgs());
            } catch (Exception e) {
                commandEvent.getChannel().sendMessage("Showing the top 10..").queue();
            }
            if (amountOfHelpers > 15) {
                amountOfHelpers = 15;
                commandEvent.getChannel().sendMessage("Max amount is 15, setting parameter to 15...").queue();
            }
        } else {
            Document doc = new Document();
            doc.put("_id", commandEvent.getMember().getUser().getId());
            Document userDoc = (Document) helpMessages.find(doc).first();
            commandEvent.reply("Your message count in help channels is: " + userDoc.get("messageCount"));
            return;
        }

        ArrayList<Document> topHelpMessages = new ArrayList<>();

        helpMessages.find().sort(orderBy(descending("messageCount"))).into(topHelpMessages);

        StringBuilder topList = new StringBuilder();

        for (int i = 0; i < amountOfHelpers; i++) {

            final Member member = commandEvent.getGuild().getMemberById(topHelpMessages.get(i).getString("_id"));

            if (member != null) {

                topList.append(member.getUser().getName()).append("#").append(member.getUser().getDiscriminator())
                        .append(" - ").append(topHelpMessages.get(i).get("messageCount")).append("\n");

            }

        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.blue);
        embedBuilder.addField("Top messagecount in help channels", topList.toString(), false);

        commandEvent.getChannel().sendMessage(embedBuilder.build()).queue();

    }
}
