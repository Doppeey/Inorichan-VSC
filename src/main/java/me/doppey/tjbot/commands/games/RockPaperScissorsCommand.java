package me.doppey.tjbot.commands.games;

import me.doppey.tjbot.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.util.Random;


public class RockPaperScissorsCommand extends Command {
    public RockPaperScissorsCommand() {
        this.name = "rps";
        this.help = "Rock Paper Scissors, usage: >rps [rock/paper/scissors]";
        this.category = Categories.Games;

    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        String[] message = commandEvent.getMessage().getContentRaw().split(" ");

        if (message.length < 2) {
            commandEvent.reply("Invalid Argument");
        }

        String playerGuess = message[1];

        commandEvent.reply(gameLogic(playerGuess));
    }

    private String gameLogic(String playerGuess) {
        String aiGuess = rockPaperScissorsGuess();

        final boolean playerPaper = playerGuess.equalsIgnoreCase("paper");
        final boolean playerRock = playerGuess.equalsIgnoreCase("rock");
        final boolean playerScissors = playerGuess.equalsIgnoreCase("scissors");
        final boolean aiScissors = aiGuess.equalsIgnoreCase("scissors");
        final boolean aiRock = aiGuess.equalsIgnoreCase("rock");
        final boolean aiPaper = aiGuess.equalsIgnoreCase("paper");

        //rock
        if (playerRock) {
            if (aiScissors) {
                return "I picked scissors, you win!";
            } else if (aiRock) {
                return "I picked rock, it's a draw!";
            } else if (aiPaper) {
                return "I picked paper, you lose!";
            }
        }
        //rock end

        //paper
        if (playerPaper) {
            if (aiScissors) {
                return "I picked scissors, you lose!";
            } else if (aiRock) {
                return "I picked rock, you win!";
            } else if (aiPaper) {
                return "I picked paper, it's a draw!";
            }
        }
        //paper end

        //scissors
        if (playerScissors) {
            if (aiScissors) {
                return "I picked scissors, it's a draw!";
            } else if (aiRock) {
                return "I picked rock, you lose!";
            } else if (aiPaper) {
                return "I picked paper, you win!";
            }
        }

        return "Error";
    }

    private String rockPaperScissorsGuess() {
        int guessID = new Random().nextInt(3);

        switch (guessID) {
            case 0:
                return "scissors";
            case 1:
                return "rock";
            case 2:
                return "paper";
        }

        return "Error";
    }
}
