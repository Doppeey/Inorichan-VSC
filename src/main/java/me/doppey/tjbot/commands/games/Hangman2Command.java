package me.doppey.tjbot.commands.games;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.doppey.tjbot.InoriChan;
import net.dv8tion.jda.core.EmbedBuilder;
import org.bson.Document;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Random;

public class Hangman2Command extends Command {
    MongoCollection hangmanDb;

    public Hangman2Command(MongoDatabase db) {
        this.name = "hangman2";
        this.hangmanDb = db.getCollection("hangman");
    }

    @Override
    protected void execute(CommandEvent event) {

        /*
            lives:
            gameId:
            playerId:
            word:
            hiddenword:

         */
        try {
            int lineNumber = new Random().nextInt(852);
            String word = Files.readAllLines(Paths.get("words.txt")).get(lineNumber);
            String hiddenword = "";
            for (char c : word.toCharArray()) {
                hiddenword += "_ ";
            }

            HashSet<Character> guessedLetters = new HashSet<>();

            hiddenword = hiddenword.stripTrailing();

            EmbedBuilder hangmanEmbed = new EmbedBuilder();
            hangmanEmbed.setColor(Color.blue);
            hangmanEmbed.setTitle(event.getMember().getEffectiveName() + "'s Game");
            hangmanEmbed.addField("Word:", "`" + hiddenword + "`", true);
            hangmanEmbed.addField("Lives left: ", "5", true);
            hangmanEmbed.setFooter("React with an emoji letter to make a guess", null);

            event.getChannel().sendMessage(hangmanEmbed.build()).queue(
                    sent -> {
                        String playerId = event.getAuthor().getId();

                        if (!event.getMessage().getMentionedMembers().isEmpty()) {
                            playerId = event.getMessage().getMentionedMembers().get(0).getUser().getId();
                            if (event.getMessage().getContentRaw().split(" ").length > 2) {

                                String wordDirty = event.getMessage().getContentRaw().split(" ")[1];
                                StringBuilder wordClean = new StringBuilder();

                                for (char c : wordDirty.toCharArray()) {
                                    if (c != '|') {
                                        wordClean.append(c);
                                    }
                                }

                                StringBuilder hiddenCustom = new StringBuilder();
                                for (char c : wordClean.toString().toCharArray()) {
                                    hiddenCustom.append("_ ");
                                }

                                EmbedBuilder hangmanEmbedCustom = new EmbedBuilder();
                                hangmanEmbedCustom.setColor(Color.blue);
                                hangmanEmbedCustom.setTitle(event.getMessage().getMentionedMembers().get(0).getEffectiveName() + "'s Game");
                                hangmanEmbedCustom.addField("Word:", "`" + hiddenCustom.toString().stripTrailing() + "`", true);
                                hangmanEmbedCustom.addField("Lives left: ", "5", true);
                                hangmanEmbedCustom.setFooter("React with an emoji letter to make a guess", null);

                                sent.editMessage(hangmanEmbedCustom.build()).queue();
                                event.getMessage().delete().queue();

                                Document hangmanDoc = new Document()
                                        .append("lives", 5)
                                        .append("gameId", sent.getId())
                                        .append("playerId", playerId)
                                        .append("word", wordClean.toString())
                                        .append("guessedLetters", guessedLetters);

                                hangmanDb.insertOne(hangmanDoc);

                                InoriChan.LOGGER.info(event.getMember().getEffectiveName() + " has started a hangman game!");
                                return;
                            }
                        }

                        Document hangmanDoc = new Document()
                                .append("lives", 5)
                                .append("gameId", sent.getId())
                                .append("playerId", playerId)
                                .append("word", word)
                                .append("guessedLetters", guessedLetters);

                        hangmanDb.insertOne(hangmanDoc);

                        InoriChan.LOGGER.info(event.getMember().getEffectiveName() + " has started a hangman game!");
                    }
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
