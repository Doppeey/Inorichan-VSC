package EventsAndCommands.FunCommands;

import EventsAndCommands.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.io.File;
import java.io.InputStream;
import java.util.Random;

public class DeletThisCommand extends Command {

    private InputStream is;
    private ClassLoader classloader;

    public DeletThisCommand(){
        this.name="delet";
        this.category = Categories.Fun;
        this.help = "Sends random delet this picture";
    }


    private File file;

    @Override
    protected void execute(CommandEvent commandEvent) {


        try {
            String fileName = "delet/"+ (new Random().nextInt(20)+1)+".jpg";

           file = new File(fileName);

        } catch (Exception e) {
            e.printStackTrace();

;
            return;
        }

        commandEvent.getChannel().sendFile(file).queue();

    }
}
