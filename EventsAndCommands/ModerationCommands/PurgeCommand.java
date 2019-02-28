package EventsAndCommands.ModerationCommands;

import EventsAndCommands.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.managers.GuildController;

import java.util.List;
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

        GuildController gc = new GuildController(commandEvent.getGuild());


        String[] message = commandEvent.getMessage().getContentRaw().toLowerCase().split(" ");
        int howManyMessagesToDelete = 0;

        if (message.length == 2) {

            try {
                howManyMessagesToDelete = Integer.parseInt(message[1]);
            } catch (Exception e) {
            }


            List<Message> messageList = commandEvent.getChannel().getHistory().retrievePast(howManyMessagesToDelete + 1).complete();
            commandEvent.getMessage().delete().queue((x) -> commandEvent.getChannel().purgeMessages(messageList));

            commandEvent.replySuccess(howManyMessagesToDelete + " Message(s) purged!", (x) -> x.delete().queueAfter(2, TimeUnit.SECONDS));
        } else if (message.length == 1) {
            commandEvent.getMessage().delete().queueAfter(3, TimeUnit.SECONDS);
            commandEvent.reply("You need to specify the amount of messages!", (x) -> x.delete().queueAfter(2, TimeUnit.SECONDS));
        }


    }


}
