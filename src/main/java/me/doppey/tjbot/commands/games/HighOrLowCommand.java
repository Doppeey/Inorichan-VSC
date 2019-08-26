package me.doppey.tjbot.commands.games;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import me.doppey.tjbot.Categories;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class HighOrLowCommand extends Command {

    private final EventWaiter waiter;

    public HighOrLowCommand(EventWaiter waiter) {
        this.waiter = waiter;
        this.name = "highorlow";
        this.category = Categories.Games;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        TextChannel eventChannel = commandEvent.getTextChannel();
        Member member = commandEvent.getMember();
        AtomicInteger points = new AtomicInteger();
        points.set(0);

        eventChannel.sendMessage("A random percentage from 0 to 100% will be generated, you can type \"high\" to " +
                "guess 51-100 or \"low\" for 0-49. 50% is always a loss." +
                " Each correct guess gets you one point. One wrong guess and all points are lost. You can enter " +
                "\"end\" at any time to quit.").queue(x -> {

            eventChannel.sendMessage("Type a command: ").queue(command -> {
                waiter.waitForEvent(MessageReceivedEvent.class, y -> y.getMember().equals(member), y -> {
                    playGame(y, new AtomicInteger(0));
                });
            });
        });
    }

    private void playGame(MessageReceivedEvent y, AtomicInteger pointso) {
        String guess = y.getMessage().getContentRaw();

        AtomicInteger percentage = new AtomicInteger();
        percentage.set(new Random().nextInt(101));
        String result;
        boolean hasLost = true;

        if (percentage.intValue() < 50) {
            result = "low";
        } else if (percentage.intValue() > 50) {
            result = "high";
        } else {
            result = "middle";
        }

        switch (result) {
            case "low":
                if (guess.equalsIgnoreCase("low")) {
                    pointso.getAndIncrement();
                    y.getChannel().sendMessage("Percentage was " + percentage + "%, you get a point!").queue();
                    hasLost = false;
                    break;
                } else if (y.getMessage().getContentRaw().equalsIgnoreCase("end")) {
                    y.getChannel().sendMessage("You ended the game with " + pointso + " points!").queue();
                    break;
                } else {
                    y.getChannel().sendMessage("Percentage was " + percentage + "%, you lose").queue();
                    hasLost = true;
                    break;
                }

            case "middle":
                y.getChannel().sendMessage("50%, you lose, 0 points.").queue();
                hasLost = true;
                break;
            case "high":
                if (guess.equalsIgnoreCase("high")) {
                    hasLost = false;
                    pointso.getAndIncrement();
                    y.getChannel().sendMessage("Percentage was " + percentage + "%, you get a point!").queue();
                    break;
                } else if (y.getMessage().getContentRaw().equalsIgnoreCase("end")) {
                    y.getChannel().sendMessage("You ended the game with " + pointso + " points!").queue();
                    break;
                } else {
                    y.getChannel().sendMessage("Percentage was " + percentage + "%, you lose").queue();
                    hasLost = true;
                    break;
                }
        }

        if (!hasLost) {
            y.getChannel().sendMessage("Next Command: ").queue();
            waiter.waitForEvent(MessageReceivedEvent.class, x -> y.getMember().equals(x.getMember()), x -> {
                playGame(x, pointso);
            });
        }
    }
}
