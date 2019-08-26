package me.doppey.tjbot.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.doppey.tjbot.Categories;
import me.doppey.tjbot.InoriChan;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import org.bson.Document;

import java.awt.Color;


public class PollCommand extends Command {

    String[] numbers = {":zero:", ":one:", ":two:", ":three:", ":four:", ":five:", ":six:", ":seven:", ":eight:",
            ":nine:"};
    private Message message;
    private MongoCollection polls;
    private MessageChannel commandEventChannel;

    public PollCommand(MongoDatabase database) {

        this.polls = database.getCollection("polls");
        this.name = "poll";
        this.help = "Command for starting polls. [usage: question;option;option;option ...] without options a yes or " +
                "no poll will be created";
        this.category = Categories.Utility;

    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        commandEventChannel = commandEvent.getChannel();
        final Member member = commandEvent.getMember();

        String[] args = commandEvent.getArgs().split(";");

        if (args[0].equals("")) {
            commandEvent.reply("No parameters supplied");
            return;
        }

        String question = args[0];
        boolean isYesOrNoQuestion = args.length == 1;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.green);
        embedBuilder.setAuthor("Poll by " + commandEvent.getMember().getEffectiveName(), null,
                commandEvent.getAuthor().getAvatarUrl());

        if (isYesOrNoQuestion) {
            embedBuilder.setDescription(question);
            commandEventChannel.sendMessage(embedBuilder.build()).queue(x -> {

                x.addReaction("✅").queue();
                x.addReaction("❌").queue();
                message = x;
                addPollToDb();
                commandEvent.getMessage().delete().queue();
            });
        } else {
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 1; i < args.length; i++) {
                stringBuilder.append(numbers[i])
                        .append(" ")
                        .append(args[i])
                        .append("\n\n");
            }

            embedBuilder.setDescription("**" + question + "**" + "\n\n");
            embedBuilder.appendDescription(stringBuilder.toString());

            commandEventChannel.sendMessage(embedBuilder.build()).queue(
                    x -> {
                        for (int i = 1; i < args.length; i++) {
                            x.addReaction(i + "\u20E3").queue();
                        }
                        message = x;
                        addPollToDb();
                        commandEvent.getMessage().delete().queue();
                    }
            );
        }
    }

    private void addPollToDb() {
        Document document = new Document();
        document.put("_id", message.getId());
        polls.insertOne(document);
    }


    public void countdown(Message message, int time) {
        if (time > 0) {
            message.editMessage("" + time).queue();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                InoriChan.LOGGER.error(e.getMessage(), e);
            }

            countdown(message, time - 5);
        } else if (time == 0) {
            message.editMessage("0").queue();
        }
    }
}
