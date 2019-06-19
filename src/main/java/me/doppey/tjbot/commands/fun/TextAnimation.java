package me.doppey.tjbot.commands.fun;

import me.doppey.tjbot.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.entities.Message;

import java.util.concurrent.TimeUnit;


public class TextAnimation extends Command {
    public TextAnimation() {
        this.name = "textanimation";
        this.category = Categories.Fun;
        this.help = "A little animation by editing messages";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        String[] test = {" ┬─┬ノ( º _ ºノ)", "(ﾉಥ益ಥ）ﾉ ┻━┻", "(ノಠ益ಠ)ノ彡┻━┻", "(ノಠ益ಠ)ノ彡彡┻━┻", "(ノಠ益ಠ)ノ彡彡彡┻━┻", "(ノಠ益ಠ)ノ"};

        commandEvent.getChannel().sendMessage("Welcome").queueAfter(1, TimeUnit.SECONDS, x -> animate(commandEvent, 0, test, x));
    }

    private void animate(CommandEvent event, int counter, String[] array, Message message) {
        if (counter != array.length - 1) {

            message.editMessage(array[counter]).queueAfter(1, TimeUnit.SECONDS,
                    x -> animate(event, counter + 1, array, message));
        }
    }
}



