package me.doppey.tjbot;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import me.doppey.tjbot.commands.utility.AboutCommand;
import me.doppey.tjbot.commandsystem.CommandLoader;
import me.doppey.tjbot.utility.ChannelMarkerScheduler;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.hooks.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class InoriChan {
    public static final String CONFIG_FILENAME = "tjbot.config";
    private static final Config CONFIG = loadConfig(CONFIG_FILENAME);
    public static final MongoDatabase DATABASE;
    public static final Logger LOGGER = LoggerFactory.getLogger(InoriChan.class);

    static {
        if (getConfig() == null) {
            System.exit(-1);
        }

        LOGGER.info("{} loaded.", CONFIG_FILENAME.substring(0, CONFIG_FILENAME.length() - 7));

        //DATABASES
        MongoClientURI uri = new MongoClientURI(CONFIG.getProperty("MONGO_URI"));
        DATABASE = new MongoClient(uri).getDatabase("TogetherJava");
    }

    public static void main(String[] args) {
        try {
            String[] desc = {"Generates dank memes", "more to come..."};
            Permission[] perms = {Permission.MESSAGE_READ, Permission.MESSAGE_WRITE};

            EventWaiter waiter = new EventWaiter();
            CommandClientBuilder builder = new CommandClientBuilder();

            JDA jda = new JDABuilder(getConfig().getProperty("BOT_TOKEN")).build();

            jda.addEventListener(waiter);

            builder.setOwnerId(getConfig().getProperty("OWNER_ID"));
            builder.setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26");
            builder.setPrefix(getConfig().getProperty("PREFIX")); // prefix for testbot < , prefix for InoriChan >
            builder.addCommand(new AboutCommand("\nInformation about the bot: \n", desc, perms));

            CommandLoader<Command> commands = new CommandLoader<>(Command.class, DATABASE, CONFIG, waiter);
            commands.loadClasses().forEach(builder::addCommand);

            jda.addEventListener(builder.build());

            CommandLoader<EventListener> listeners = new CommandLoader<>(EventListener.class, DATABASE, CONFIG, waiter);
            listeners.loadClasses().forEach(jda::addEventListener);

            // Scheduler that checks help channels and adds the free icon

            Thread.sleep(5000);
            ChannelMarkerScheduler cm = new ChannelMarkerScheduler(jda.getGuildById("272761734820003841"));
            cm.checkHelpChannels();

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static Config getConfig() {
        return CONFIG;
    }

    private static Config loadConfig(String filename) {
        // CONFIG
        Properties properties = new Properties();
        try {
            InputStream is = new FileInputStream(filename);
            properties.load(is);
        } catch (FileNotFoundException ex) {
            //logger doesn't work until after config is loaded
            System.out.println("Could not find config file");
        } catch (IOException ex) {
            System.out.println("Could not load config file");
        }
        return new Config(properties);
    }
}