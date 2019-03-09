package EventsAndCommands.FunCommands;

import EventsAndCommands.Categories;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.core.entities.TextChannel;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toList;

public class OoflmaoCommand extends Command {

    /*
    This command will retrieve a random message from the Oofs and lmaos channel
    by a specified user, or if no user is specified, then from a random user


    THIS COMMAND IS ON HOLD, NEED TO DO IT WITH THE DB INSTEAD
     */


    MongoCollection oofLmaoCollection;
    public OoflmaoCommand(MongoDatabase database) {

        this.oofLmaoCollection = database.getCollection("oofsAndLmaos");
        this.name = "ooflmao";
        this.help = "retrieves a random oof or lmao message";
        this.cooldown = 5;
        this.category = Categories.Fun;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {


        List<Document> messageDocumentList = new ArrayList<>();
        oofLmaoCollection.find().into(messageDocumentList);
        List<String> messageIdStringList = messageDocumentList.stream().map(doc -> doc.getString("id")).collect(toList());


        getRandomOofOrLmao(commandEvent, messageIdStringList,true);


    }

    private void getRandomOofOrLmao(CommandEvent commandEvent, List<String> history, boolean repeat) {

        Runnable runnable = () -> {
            if(commandEvent.getGuild().getId().equalsIgnoreCase("272761734820003841")) {

                try {

                    String randomId = history.get(new Random().nextInt(history.size()));


                    TextChannel ooflmaoChannel = commandEvent.getGuild().getTextChannelById("521647171871703040");

                    final TextChannel[] originChannel = new TextChannel[1];
                    final boolean[] foundTheChannel = {false};


                    commandEvent.getGuild().getTextChannels().forEach(channel -> {
                        if (!foundTheChannel[0]) {
                            try {
                                channel.getMessageById(randomId).complete();
                                originChannel[0] = channel;
                                foundTheChannel[0] = true;



                            } catch (Exception | Error e) {
                                // go to next
                            }
                        }

                    });
                    originChannel[0].getMessageById(randomId).queue(gotem -> {

                        try {
                            commandEvent.getChannel().sendMessage(gotem).queue();
                        } catch (Exception | Error f) {

                        }
                    });


                } catch (Exception | Error e) {
                    if(repeat){
                        getRandomOofOrLmao(commandEvent,history,false);
                    } else {
                        commandEvent.reply("Could not get message, try again in a few seconds");
                    }


                }
            } else {
                commandEvent.reply("This command only works on the Together Java server");
            }
        };
        Thread oofThread = new Thread(runnable);
        oofThread.start();


    }
}
