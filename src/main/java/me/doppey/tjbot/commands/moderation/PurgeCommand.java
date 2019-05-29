package me.doppey.tjbot.commands.moderation;

import me.doppey.tjbot.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.managers.GuildController;
import net.dv8tion.jda.core.requests.restaction.AuditableRestAction;

import java.util.concurrent.TimeUnit;

public class PurgeCommand extends Command {


    public PurgeCommand() {
        this.name = "purge";
        this.category = Categories.Moderation;
        this.help = "deletes messages";
        this.requiredRole = "Moderator";


    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        final MessageChannel commandEventChannel = commandEvent.getChannel();

    


        GuildController gc = new GuildController(commandEvent.getGuild());


        String[] message = commandEvent.getMessage().getContentRaw().toLowerCase().split(" ");
        int howManyMessagesToDelete = 0;
        final AuditableRestAction<Void> deleteMessage = commandEvent.getMessage().delete();



        if (message.length == 2) {

            try {
                howManyMessagesToDelete = Integer.parseInt(message[1]);
            } catch (Exception e) {
            }


           commandEventChannel.getHistory().retrievePast(howManyMessagesToDelete + 1).queue(messageList ->deleteMessage.queue((x) -> commandEventChannel.purgeMessages(messageList)));


            commandEvent.replySuccess(howManyMessagesToDelete + " Message(s) purged!", (x) -> x.delete().queueAfter(2, TimeUnit.SECONDS));
        } else if (message.length == 1) {
            deleteMessage.queueAfter(3, TimeUnit.SECONDS);
            commandEvent.reply("You need to specify the amount of messages!", (x) -> x.delete().queueAfter(2, TimeUnit.SECONDS));
        }


    }


}
