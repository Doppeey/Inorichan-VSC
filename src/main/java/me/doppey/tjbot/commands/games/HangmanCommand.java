package me.doppey.tjbot.commands.games;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import me.doppey.tjbot.Categories;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class HangmanCommand extends Command {

    private final EventWaiter waiter;
    private final int startingLives = 5;
    boolean hasWon = false;
    String word = "doppey is the best";
    String hiddenword = "------------------";
    String hiddenwordPermanent = "------------------";
    private char currentGuess;
    private char[] tempWord;
    private boolean hasGuessed = false;
    private Member member;
    private int livesLeft = 5;

    public HangmanCommand(EventWaiter waiter) {

        this.name = "hangman";
        this.help = "under construction";
        this.category = Categories.Games;
        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        int lineNumber = new Random().nextInt(852);

        try {
            word = Files.readAllLines(Paths.get("resources/words.txt")).get(lineNumber);
        } catch (IOException e) {
            e.printStackTrace();
            commandEvent.reply("Error reading the hangman words file");
            return;
        }

        hiddenword = "";
        hiddenwordPermanent = "";
        for (int i = 0; i < word.toCharArray().length; i++) {
            hiddenword += "-";
            hiddenwordPermanent += "-";
        }

        member = commandEvent.getMember();

        final MessageChannel channel = commandEvent.getChannel();

        channel.sendMessage("Good luck, here we go:").queue();
        channel.sendMessage(hiddenword).queue();

        tempWord = hiddenword.toCharArray();

        waiter.waitForEvent(MessageReceivedEvent.class, e -> e.getMember().equals(member),
                e -> guess(channel, tempWord, e), 30, TimeUnit.SECONDS, new Runnable() {

                    @Override
                    public void run() {
                        commandEvent.reply("No reply in 30 seconds, game has timed out.");
                    }
                });
    }

    private void guess(MessageChannel channel, char[] tempWord, MessageReceivedEvent e) {
        int correctGuesses = 0;
        final String contentRaw = e.getMessage().getContentRaw();
        final int messageLength = contentRaw.length();

        if (messageLength == 1) {
            final char currentGuess = contentRaw.charAt(0);
            setCurrentGuess(currentGuess);

            for (int i = 0; i < word.length(); i++) {

                if (word.charAt(i) == this.currentGuess) {
                    this.tempWord[i] = this.currentGuess;
                    correctGuesses++;
                }
            }
        } else {
            if (foundWord(e)) {
                channel.sendMessage("You guessed it! Gratz!").queue();
                this.setHiddenword(hiddenwordPermanent);
                this.livesLeft = startingLives;
                return;
            }
        }

        setHiddenword(new String(tempWord));
        if (correctGuesses == 0) {
            this.livesLeft--;
            if (livesLeft > 0) {
                channel.sendMessage("No correct guesses, you lost a life, lives left: " + livesLeft).queue();
            }
        }

        if (livesLeft == 0) {
            channel.sendMessage("No lives left, you lost!").queue();
            channel.sendMessage("The word was " + word).queue();
            this.setHiddenword(hiddenwordPermanent);
            this.livesLeft = startingLives;
            return;
        }

        channel.sendMessage(hiddenword).queue();

        if (!this.word.equalsIgnoreCase(new String(this.tempWord))) {
            waiter.waitForEvent(MessageReceivedEvent.class, x -> x.getMember().equals(member),
                    x -> guess(channel, this.tempWord, x));
        } else {
            channel.sendMessage("You guessed it! Gratz!").queue();
            this.setHiddenword(hiddenwordPermanent);
            this.livesLeft = startingLives;
        }
    }

    private boolean foundWord(MessageReceivedEvent e) {
        final String contentRaw = e.getMessage().getContentRaw();
        return contentRaw.equalsIgnoreCase(this.word);
    }

    public String getHiddenword() {
        return hiddenword;
    }

    private void setHiddenword(String hiddenword) {
        this.hiddenword = hiddenword;
    }

    public char getCurrentGuess() {
        return currentGuess;
    }

    private void setCurrentGuess(char currentGuess) {
        this.currentGuess = currentGuess;
    }

    public boolean getHasGuessed() {
        return hasGuessed;
    }

    public void setHasGuessed(boolean hasGuessed) {
        this.hasGuessed = hasGuessed;
    }
}
