package me.doppey.tjbot.commands.utility;

import me.doppey.tjbot.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.doppey.tjbot.InoriChan;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class TimerCommand extends Command {
    public TimerCommand() {
        this.name = "timer";
        this.help = "Start a timer, use s for seconds and m for minutes";
        this.category = Categories.Utility;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        String args = commandEvent.getArgs().stripLeading().stripTrailing();
        String[] argsArray = commandEvent.getArgs().split(" ");

        int time;
        String secondsOrMinutes;
        StringBuilder timerContent = new StringBuilder();

        try {
            commandEvent.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            InoriChan.LOGGER.error("Missing permission error, can't delete timer on {}", commandEvent.getGuild().getName(), e);
        }

        if (argsArray[0].toLowerCase().contains("s")) {
            try {
                time = Integer.parseInt(args.split("s")[0].trim());
                if (argsArray.length >= 2) {
                    for (int i = 1; i < argsArray.length; i++) {
                        timerContent.append(argsArray[i]);
                        timerContent.append(" ");
                    }
                }
            } catch (Exception e) {
                commandEvent.reply("Wrong format");
                return;
            }
            secondsOrMinutes = "s";
        } else if (argsArray[0].toLowerCase().contains("m")) {
            try {
                time = Integer.parseInt(args.split("m")[0].trim());
                if (argsArray.length >= 2) {
                    for (int i = 1; i < argsArray.length; i++) {
                        timerContent.append(argsArray[i]);
                        timerContent.append(" ");
                    }
                }
            } catch (Exception e) {
                commandEvent.reply("Wrong format");
                return;
            }
            secondsOrMinutes = "m";
        } else {
            commandEvent.reply("Use \"m\" or \"s\" as time unit");
            return;
        }

        String finalSecondsOrMinutes = secondsOrMinutes;
        int finalTime = time;

        Runnable timer = () -> {
            try {
                commandEvent.getChannel().sendMessage("I've started a " + finalTime + finalSecondsOrMinutes + " timer and will PM you when it's done!").queue(x ->

                        x.delete().queueAfter(5, TimeUnit.SECONDS));
                if (finalSecondsOrMinutes.equalsIgnoreCase("s")) {
                    try {
                        Thread.sleep(finalTime * 1000);
                    } catch (InterruptedException e) {
                        InoriChan.LOGGER.error(e.getMessage(), e);
                    }
                } else if (finalSecondsOrMinutes.equalsIgnoreCase("m")) {
                    try {
                        Thread.sleep(1000 * 60 * finalTime);
                    } catch (InterruptedException e) {
                        InoriChan.LOGGER.error(e.getMessage(), e);
                    }
                }
            } catch (Exception e) {
                InoriChan.LOGGER.error("Missing permission, can't delete the timer", e);
                commandEvent.getChannel().sendMessage("I've started a " + finalTime + finalSecondsOrMinutes + " timer and will PM you when it's done!").queue();
                if (finalSecondsOrMinutes.equalsIgnoreCase("s")) {
                    try {
                        Thread.sleep(finalTime * 1000);
                    } catch (InterruptedException f) {
                        InoriChan.LOGGER.error(e.getMessage(), e);
                    }
                } else if (finalSecondsOrMinutes.equalsIgnoreCase("m")) {
                    try {
                        Thread.sleep(1000 * 60 * finalTime);
                    } catch (InterruptedException g) {
                        InoriChan.LOGGER.error(e.getMessage(), e);
                    }
                }
            }

            commandEvent.getMember().getUser().openPrivateChannel().queue(x -> {
                        x.sendMessage("I'm here to notify you that your **" + finalTime + finalSecondsOrMinutes + "** timer has ended!").queue();

                        if (!timerContent.toString().strip().isEmpty()) {
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setDescription(timerContent);
                            embed.setAuthor(commandEvent.getMember().getEffectiveName(), commandEvent.getMember().getUser().getEffectiveAvatarUrl(), commandEvent.getMember().getUser().getEffectiveAvatarUrl());
                            embed.setColor(Color.red);
                            x.sendMessage(embed.build()).queue();
                        }

                        x.close().queue();
                    }
            );
        };

        Thread thread = new Thread(timer);
        thread.start();
    }
}
