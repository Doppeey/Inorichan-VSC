package me.doppey.tjbot.commands.fun;

import java.util.ArrayList;
import java.util.Arrays;

import me.doppey.tjbot.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

/**
 * BubbleSortCommand
 */
public class BubbleSortCommand extends Command {

    public BubbleSortCommand() {
        this.name = "bubblesort";
        this.help = "sorts numbers using the bubblesort algorithm, supplies numbers split by commas, a max of 10 numbers can be supplied";
        this.category = Categories.Fun;
    }

    @Override
    protected void execute(CommandEvent event) {
        ArrayList<Integer> unsorted = new ArrayList<Integer>();
        try {
            Arrays.stream(event.getArgs().trim().split(",")).mapToInt(Integer::parseInt).forEach(unsorted::add);
        } catch (Exception e){
            event.reply("Only use numbers and separate them with commas");
        }

        if (unsorted.size() > 15) {
            event.reply("A maximum of 15 numbers can be supplied, to prevent spam.");
            return;
        }

        int bubbleCounter = -1;
        int temp;

        StringBuilder result = new StringBuilder("```\n");
        result.append(Arrays.toString(unsorted.toArray()));
        result.append("\n");
        //As long as bubble counter is not zero, the list is not done sorting
        while (bubbleCounter != 0) {

            //We set bubbleCounter to zero so if the next
            //cycle wont add anything to it, we know we're done!
            bubbleCounter = 0;

            for (int i = 0; i < unsorted.size() - 1; i++) {

                //If the number we are at, is bigger than the next, you guessed it! We swap em!
                if (unsorted.get(i) > unsorted.get(i + 1)) {

                    //Store one of the values so we can swap them around
                    temp = unsorted.get(i + 1);

                    //Swap the numbers so that the bigger numbers is on the right
                    unsorted.set(i + 1, unsorted.get(i));
                    unsorted.set(i, temp);

                    //Add one to the bubbleCounter, because we changed something (meaning the list is not sorted yet)
                    bubbleCounter++;
                }
            }
            if (bubbleCounter == 0) {
                result.append("--------------------");
                result.append("\n");
                result.append(Arrays.toString(unsorted.toArray()));
            } else {
                result.append(Arrays.toString(unsorted.toArray()));
                result.append("\n");
            }
        }
        result.append("\n```");

        event.reply(result.toString());
    }
}