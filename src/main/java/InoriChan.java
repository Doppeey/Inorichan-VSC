import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import EventsAndCommands.*;
import EventsAndCommands.EventUtility.AboutCommand;

class InoriChan {


    public static void main(String[] args) throws Exception {
        
        // Loading config file
        final String configFileName = "tjbot.config";
        Properties config = loadConfig(configFileName);
        System.out.println(configFileName.substring(0, configFileName.length() - 7) + " config loaded.");
      


        // DATABASE
        MongoClientURI uri = new MongoClientURI(config.getProperty("MONGO_URI"));

        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase("TogetherJava");
        // DATABASE END

        String[] desc = { "Generates dank memes", "more to come..." };
        Permission[] perms = { Permission.MESSAGE_READ, Permission.MESSAGE_WRITE };

        EventWaiter waiter = new EventWaiter();
        CommandClientBuilder inoriChan = new CommandClientBuilder();

        JDA jda = new JDABuilder(config.getProperty("BOT_TOKEN")).build();

        jda.addEventListener(waiter);
      
        inoriChan.setOwnerId(config.getProperty("OWNER_ID"));
        inoriChan.setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26");
        inoriChan.setPrefix(config.getProperty("PREFIX")); // prefix for testbot < , prefix for InoriChan >
        inoriChan.addCommand(new AboutCommand("\nInformation about the bot: \n", desc, perms));


        CommandLoader<Command> commands = new CommandLoader<>(Command.class, database, config, waiter);
        commands.loadClasses();

        for (var cmd : commands.getLoadedClasses()) {
            inoriChan.addCommand(cmd);
        }
        CommandLoader<EventListener> listeners = new CommandLoader<>(EventListener.class, database, config, waiter);
        jda.addEventListener(inoriChan.build());
        listeners.loadClasses();
        for (var listener : listeners.getLoadedClasses()){
            jda.addEventListener(listener);
        }


    }

    private static Properties loadConfig(String fileName) throws Exception {

        // CONFIG
        Properties prop = new Properties();
        InputStream is = null;
        try {
            is = new FileInputStream(fileName);

        } catch (FileNotFoundException ex) {
            System.out.println("Could not find config file");
            throw new FileNotFoundException();
        }
        try {
            prop.load(is);
        } catch (IOException ex) {
            System.out.println("Could not load config file");
            throw new IOException();
        }
        //

        return prop;
    }

}
