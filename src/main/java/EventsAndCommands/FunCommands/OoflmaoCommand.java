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
        Document randomDocument = messageDocumentList.get(new Random().nextInt(messageDocumentList.size()));

        
        sendRandomOofOrLmao(commandEvent, randomDocument);

    }




    private void sendRandomOofOrLmao(CommandEvent commandEvent, Document randomDocument) {
        commandEvent.getGuild().getTextChannelById(randomDocument.getString("channel"))
                .getMessageById(randomDocument.getString("id")).queue(message -> commandEvent.reply(message));
    }

    
}
