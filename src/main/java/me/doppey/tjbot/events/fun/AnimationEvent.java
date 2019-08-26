package me.doppey.tjbot.events.fun;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

public class AnimationEvent extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equalsIgnoreCase(">animation")) {
            String[] test = {" ┬─┬ノ( º _ ºノ)", "(ﾉಥ益ಥ）ﾉ ┻━┻", "(ノಠ益ಠ)ノ彡┻━┻", "(ノಠ益ಠ)ノ彡彡┻━┻", "(ノಠ益ಠ)ノ彡彡彡┻━┻", "(ノಠ益ಠ)" +
                    "ノ"};

            event.getChannel().sendMessage("Welcome").queueAfter(1, TimeUnit.SECONDS, x -> animate(event, 0, test, x));
        }
    }

    private void animate(GuildMessageReceivedEvent event, int counter, String[] array, Message message) {
        if (counter != array.length - 1) {
            message.editMessage(array[counter]).queueAfter(1, TimeUnit.SECONDS,
                    x -> animate(event, counter + 1, array, message)
            );
        }
    }
}
