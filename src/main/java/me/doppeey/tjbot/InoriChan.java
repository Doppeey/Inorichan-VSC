package me.doppeey.tjbot;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import me.doppeey.tjbot.commands.fun.*;
import me.doppeey.tjbot.commands.fun.encryption.DecryptCommand;
import me.doppeey.tjbot.commands.fun.encryption.EncryptCommand;
import me.doppeey.tjbot.commands.fun.memes.*;
import me.doppeey.tjbot.commands.fun.memes.CrabRaveMeme.GifSequenceWriter;
import me.doppeey.tjbot.commands.games.HangmanCommand;
import me.doppeey.tjbot.commands.games.HighOrLowCommand;
import me.doppeey.tjbot.commands.games.RockPaperScissorsCommand;
import me.doppeey.tjbot.commands.hidden.SummonCommand;
import me.doppeey.tjbot.commands.moderation.PurgeCommand;
import me.doppeey.tjbot.commands.moderation.ReportCommand;
import me.doppeey.tjbot.commands.moderation.SpamlordCommand;
import me.doppeey.tjbot.commands.moderation.WhoIsCommand;
import me.doppeey.tjbot.commands.utility.*;
import me.doppeey.tjbot.events.fun.*;
import me.doppeey.tjbot.events.utility.*;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class InoriChan extends ListenerAdapter {

    public static final String CONFIG_FILENAME = "tjbot.config";
    public static final Properties CONFIG = loadConfig(CONFIG_FILENAME);
    public static final MongoDatabase DATABASE;
    public static final Logger LOGGER = LoggerFactory.getLogger(InoriChan.class);

    static {
        if (CONFIG == null) {
            System.exit(-1);
        }

        LOGGER.info("{} config loaded.", CONFIG_FILENAME.substring(0, CONFIG_FILENAME.length() - 7));

        //DATABASES
        MongoClientURI uri = new MongoClientURI(CONFIG.getProperty("MONGO_URI"));
        DATABASE = new MongoClient(uri).getDatabase("TogetherJava");
    }

    public static void main(String[] args) throws Exception {

        String[] desc = {"Generates dank memes", "more to come..."};
        Permission[] perms = {Permission.MESSAGE_READ, Permission.MESSAGE_WRITE};

        EventWaiter waiter = new EventWaiter();
        CommandClientBuilder InoriChan = new CommandClientBuilder();

        JDA jda = new JDABuilder(CONFIG.getProperty("BOT_TOKEN")).build();

        jda.addEventListener(waiter);
        InoriChan.setOwnerId(CONFIG.getProperty("OWNER_ID"));
        InoriChan.setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26");
        InoriChan.setPrefix(CONFIG.getProperty("PREFIX")); // prefix for testbot < , prefix for InoriChan >
        InoriChan.addCommand(new AboutCommand("\nInformation about the bot: \n", desc, perms));


        // TESTING

        jda.addEventListener(new ReportByPmEvent(waiter));

        // ANIMAL COMMANDS
        InoriChan.addCommand(new CatCommand(CONFIG));
        InoriChan.addCommand(new FoxCommand());
        InoriChan.addCommand(new DogeCommand());
        InoriChan.addCommand(new DogCommand(CONFIG));
        // MODERATION COMMANDS
        InoriChan.addCommand(new ReportCommand());
        InoriChan.addCommand(new SpamlordCommand());
        InoriChan.addCommand(new PurgeCommand());
        InoriChan.addCommand(new WhoIsCommand());
        // MEME COMMANDS
        InoriChan.addCommand(new ScrollOfTruthCommand(CONFIG));
        InoriChan.addCommand(new HurensohnCommand(CONFIG));
        InoriChan.addCommand(new DrakeCommand(CONFIG));
        InoriChan.addCommand(new DistractedBoyfriendCommand(CONFIG));
        InoriChan.addCommand(new TwoButtonsCommand(CONFIG));
        InoriChan.addCommand(new PillsCommand(CONFIG));
        InoriChan.addCommand(new BrainCommand(CONFIG));
        InoriChan.addCommand(new SpongebobCommand(CONFIG));
        InoriChan.addCommand(new GoodNoodleCommand(CONFIG));
        InoriChan.addCommand(new ChangeMyMindCommand(CONFIG));
        InoriChan.addCommand(new NpcCommand(CONFIG));
        // GAME COMMANDS
        InoriChan.addCommand(new RockPaperScissorsCommand());
        InoriChan.addCommand(new HangmanCommand(waiter));
        InoriChan.addCommand(new HighOrLowCommand(waiter));
        // UTILITY COMMANDS
        InoriChan.addCommand(new BigCommand());
        InoriChan.addCommand(new unsplashCommand(CONFIG, waiter));
        InoriChan.addCommand(new TranslateCommand());
        InoriChan.addCommand(new DefinitionCommand());
        InoriChan.addCommand(new GoogleCommand());
        InoriChan.addCommand(new TopHelpMessages(DATABASE));
        InoriChan.addCommand(new QuoteWtfCommand());
        InoriChan.addCommand(new PollCommand(DATABASE));
        InoriChan.addCommand(new TimerCommand());
        InoriChan.addCommand(new YoutubeToMp3Command(CONFIG));
        InoriChan.addCommand(new CodeCommand());
        InoriChan.addCommand(new EmoteIdCommand());
        InoriChan.addCommand(new AvatarCommand());
        InoriChan.addCommand(new QuoteCommand());
        // FUN COMMANDS
        InoriChan.addCommand(new BubbleSortCommand());
        InoriChan.addCommand(new OoflmaoCommand(DATABASE));
        InoriChan.addCommand(new DecryptCommand());
        InoriChan.addCommand(new EncryptCommand());
        InoriChan.addCommand(new DabCommand());
        InoriChan.addCommand(new DeletThisCommand());
        InoriChan.addCommand(new GifSequenceWriter());

        // HIDDEN COMMANDS
        InoriChan.addCommand(new SummonCommand());
        InoriChan.addCommand(new DebugCommand());

        jda.addEventListener(new StagingAreaEvent());
        jda.addEventListener(new BotCatchingEvent(DATABASE));
        jda.addEventListener(InoriChan.build());
        jda.addEventListener(new AnimationEvent());
        jda.addEventListener(new imgToAsciiEvent());
        jda.addEventListener(new LemonSqueezyEvent());
        jda.addEventListener(new GoodBotEvent());
        jda.addEventListener(new SafetyFeature());
        jda.addEventListener(new AiTalkEvent(CONFIG));
        jda.addEventListener(new VoiceChannelJoinNotifyEvent());
        jda.addEventListener(new OofiesAndLmaosEvent(DATABASE));
        jda.addEventListener(new AntiScholzEvent());
        jda.addEventListener(new HelpMessageCountingEvent(DATABASE));
        jda.addEventListener(new PollReactionListener(DATABASE));
        jda.addEventListener(new UnspoilEvent(waiter));

    }

    private static Properties loadConfig(String fileName) {
        // CONFIG
        Properties prop = new Properties();
        InputStream is = null;
        try {
            is = new FileInputStream(fileName);

        } catch (FileNotFoundException ex) {
            LOGGER.info("Could not find config file");
            return null;
        }
        try {
            prop.load(is);
        } catch (IOException ex) {
            LOGGER.info("Could not load config file");
            return null;
        }
        //

        return prop;
    }

}
