package me.doppeey.tjbot.commands.utility;

import me.doppeey.tjbot.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.util.concurrent.atomic.AtomicInteger;


public class CodeCommand extends Command {


    public CodeCommand(){
        this.name = "code";
        this.help = "Formats code with java syntax highlighting";
        this.category = Categories.Utility;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {


        AtomicInteger lineIndex = new AtomicInteger(1);



        String formatStep1 = "```java\n";


        String message = commandEvent.getArgs();
        if(!message.isEmpty()) {


            StringBuilder finalMessage = new StringBuilder();

            message.lines().forEach(x -> {

                if (lineIndex.get() < 10) {
                    finalMessage.append(" "+lineIndex+"    ");
                } else {
                    finalMessage.append(lineIndex+"    ");


                }
                lineIndex.getAndIncrement();
                finalMessage.append(x);
                finalMessage.append("\n");

            });

            final String effectiveName = commandEvent.getMember().getEffectiveName();
            commandEvent.reply("Posted by " + "**" + effectiveName + "**", x -> commandEvent.reply(formatStep1 + finalMessage + "```", y -> commandEvent.getMessage().delete().queue()));



        } else commandEvent.reply("Usage: >code [code here].");




    }
}
