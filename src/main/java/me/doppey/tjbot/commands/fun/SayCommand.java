package me.doppey.tjbot.commands.fun;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.util.concurrent.TimeUnit;

public class SayCommand extends Command {



    @Override
    protected void execute(CommandEvent commandEvent) {

        if (!commandEvent.getGuild().getId().equals("272761734820003841")) {
            commandEvent.getMessage().delete().queueAfter(300, TimeUnit.MILLISECONDS);

            String[] message = commandEvent.getMessage().getContentRaw().split(">say ");
            commandEvent.reply(message[1]);
        } else {
            commandEvent.reply("Say command disabled on this Server");
        }

    }
}
