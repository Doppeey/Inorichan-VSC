package me.doppeey.tjbot.commands.utility;

import me.doppeey.tjbot.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GoogleCommand extends Command {

    public GoogleCommand() {
        this.name = "google";
        this.help = "Generates a google search query";
        this.category = Categories.Utility;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {

        String base = "https://www.google.com/search?q=";
        String searchQuery = commandEvent.getArgs();

        StringBuilder builder = new StringBuilder();

        builder.append(commandEvent.getMember().getAsMention())
                .append(" thinks that your problem could be solved with the following google search: \n")
                .append("<")
                .append(base)
                .append(URLEncoder.encode(searchQuery, StandardCharsets.UTF_8))
                .append(">");

        commandEvent.reply(builder.toString(), success -> commandEvent.getMessage().delete().queue());

    }
}
