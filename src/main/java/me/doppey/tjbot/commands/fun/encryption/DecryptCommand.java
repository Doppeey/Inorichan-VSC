package me.doppey.tjbot.commands.fun.encryption;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.doppey.tjbot.Categories;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.Color;
import java.util.Arrays;

public class DecryptCommand extends Command {

    public DecryptCommand() {
        this.name = "decrypt";
        this.help = "decrypts a message that has been encrypted with the >encrypt command";
        this.category = Categories.Fun;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        try {
            int[] charNumberRepresentations =
                    Arrays.stream(commandEvent.getArgs().split(" ")).mapToInt(Integer::parseInt).toArray();

            StringBuilder decryptedMessage = new StringBuilder();

            for (int charNumberRepresentation : charNumberRepresentations) {
                decryptedMessage.append((char) charNumberRepresentation);
            }

            EmbedBuilder embedBuilder = new EmbedBuilder();

            embedBuilder.setAuthor("Decrypted message below");
            embedBuilder.setColor(Color.green);
            embedBuilder.setDescription(decryptedMessage.toString());

            commandEvent.reply(embedBuilder.build());
        } catch (Exception e) {
            commandEvent.reply("Could not decrypt message");
        }
    }
}
