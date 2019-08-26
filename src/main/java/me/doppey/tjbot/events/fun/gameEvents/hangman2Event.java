package me.doppey.tjbot.events.fun.gameEvents;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.bson.Document;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class hangman2Event extends ListenerAdapter {

    private static HashMap<String, Character> emoteMap = new HashMap<>();
    MongoCollection hangman;

    public hangman2Event(MongoDatabase db) {
        this.hangman = db.getCollection("hangman");
        emoteMap.put("\uD83C\uDDE6", 'a');
        emoteMap.put("\uD83C\uDDE7", 'b');
        emoteMap.put("\uD83C\uDDE8", 'c');
        emoteMap.put("\uD83C\uDDE9", 'd');
        emoteMap.put("\uD83C\uDDEA", 'e');
        emoteMap.put("\uD83C\uDDEB", 'f');
        emoteMap.put("\uD83C\uDDEC", 'g');
        emoteMap.put("\uD83C\uDDED", 'h');
        emoteMap.put("\uD83C\uDDEE", 'i');
        emoteMap.put("\uD83C\uDDEF", 'j');
        emoteMap.put("\uD83C\uDDF0", 'k');
        emoteMap.put("\uD83C\uDDF1", 'l');
        emoteMap.put("\uD83C\uDDF2", 'm');
        emoteMap.put("\uD83C\uDDF3", 'n');
        emoteMap.put("\uD83C\uDDF4", 'o');
        emoteMap.put("\uD83C\uDDF5", 'p');
        emoteMap.put("\uD83C\uDDF6", 'q');
        emoteMap.put("\uD83C\uDDF7", 'r');
        emoteMap.put("\uD83C\uDDF8", 's');
        emoteMap.put("\uD83C\uDDF9", 't');
        emoteMap.put("\uD83C\uDDFA", 'u');
        emoteMap.put("\uD83C\uDDFB", 'v');
        emoteMap.put("\uD83C\uDDFC", 'w');
        emoteMap.put("\uD83C\uDDFD", 'x');
        emoteMap.put("\uD83C\uDDFE", 'y');
        emoteMap.put("\uD83C\uDDFF", 'z');
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        event.getChannel().getMessageById(event.getMessageId()).queue(message -> {

            //First check if its even by Arch btw
            if (!message.getMember().equals(event.getGuild().getSelfMember())) {
                return;
            }

            //If the game exists
            if (hangman.find(new Document("gameId", message.getId())).first() != null) {
                String guessedLetter = "" + emoteMap.get(event.getReactionEmote().getName());

                Document hangmanDoc = (Document) hangman.find(new Document("gameId", message.getId())).first();

                if (!hangmanDoc.getString("playerId").equals(event.getUser().getId())) {
                    return;
                }

                String word = hangmanDoc.getString("word");

                //Putting all guessed letters in a list so we can draw the word
                ArrayList<String> guessedLettersArray = (ArrayList<String>) hangmanDoc.get("guessedLetters");
                HashSet<String> guessedLettersSet = new HashSet<>();
                for (String c : guessedLettersArray) {
                    guessedLettersSet.add(c);
                }

                //Do not decrease lives if the letter had already been guessed.
                if (!guessedLettersSet.add(guessedLetter)) {
                    event.getChannel().sendMessage("Letter already guessed").queue();
                    return;
                }

                //redraw the word and lives
                String hiddenword = "";

                for (char c : word.toCharArray()) {
                    if (guessedLettersSet.contains("" + c)) {
                        hiddenword += "" + c + " ";
                    } else {
                        hiddenword += "_ ";
                    }
                }

                int lives = hangmanDoc.getInteger("lives");
                hiddenword = hiddenword.stripTrailing();

                if (!word.contains("" + guessedLetter)) {
                    lives--;
                    if (lives < 1) {
                        MessageEmbed embed = message.getEmbeds().get(0);
                        EmbedBuilder newEmbed = new EmbedBuilder();
                        newEmbed.setTitle(embed.getTitle() + " || Lost");
                        newEmbed.setColor(Color.red);
                        newEmbed.addField("Word:", "`" + hiddenword + "`", true);
                        newEmbed.addField("Lives left:", "" + lives, true);
                        newEmbed.setFooter(embed.getFooter().getText(), null);
                        newEmbed.addField("Solution", word, true);

                        message.editMessage(newEmbed.build()).queue();

                        hangman.deleteOne(new Document("gameId", hangmanDoc.getString("gameId")));
                        return;
                    }
                }

                MessageEmbed embed = message.getEmbeds().get(0);
                EmbedBuilder newEmbed = new EmbedBuilder();
                newEmbed.setTitle(embed.getTitle());
                newEmbed.setColor(embed.getColor());
                newEmbed.addField("Word:", "`" + hiddenword + "`", true);
                newEmbed.addField("Lives left:", "" + lives, true);
                newEmbed.setFooter(embed.getFooter().getText(), null);

                message.editMessage(newEmbed.build()).queue();

                hangman.updateOne(new Document("gameId", hangmanDoc.getString("gameId")), new Document("$set",
                        new Document("guessedLetters", guessedLettersSet)));
                hangman.updateOne(new Document("gameId", hangmanDoc.getString("gameId")), new Document("$set",
                        new Document("lives", lives)));

                if (!hiddenword.contains("_")) {
                    EmbedBuilder newEmbedWin = new EmbedBuilder();
                    newEmbedWin.setTitle(embed.getTitle() + " || Win");
                    newEmbedWin.setColor(Color.green);
                    newEmbedWin.addField("Word:", "`" + hiddenword + "`", true);
                    newEmbedWin.addField("Lives left:", "" + lives, true);
                    newEmbedWin.setFooter(embed.getFooter().getText(), null);

                    message.editMessage(newEmbedWin.build()).queue();

                    hangman.deleteOne(new Document("gameId", hangmanDoc.getString("gameId")));
                }
            }
        });
    }
}
