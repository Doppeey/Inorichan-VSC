package me.doppey.tjbot;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import me.doppey.tjbot.commands.fun.*;
import me.doppey.tjbot.commands.fun.encryption.DecryptCommand;
import me.doppey.tjbot.commands.fun.encryption.EncryptCommand;
import me.doppey.tjbot.commands.fun.memes.*;
import me.doppey.tjbot.commands.fun.memes.CrabRaveMeme.GifSequenceWriter;
import me.doppey.tjbot.commands.games.HangmanCommand;
import me.doppey.tjbot.commands.games.HighOrLowCommand;
import me.doppey.tjbot.commands.games.RockPaperScissorsCommand;
import me.doppey.tjbot.commands.hidden.SummonCommand;
import me.doppey.tjbot.commands.moderation.PurgeCommand;
import me.doppey.tjbot.commands.moderation.ReportCommand;
import me.doppey.tjbot.commands.moderation.SpamlordCommand;
import me.doppey.tjbot.commands.moderation.WhoIsCommand;
import me.doppey.tjbot.commands.utility.*;
import me.doppey.tjbot.events.fun.*;
import me.doppey.tjbot.events.utility.*;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class InoriChan extends ListenerAdapter {

    public static final String CONFIG_FILENAME = "tjbot.config";
    private static final Properties CONFIG = loadConfig(CONFIG_FILENAME);
    public static final MongoDatabase DATABASE;
    public static JDA jda;
    private static List<TextChannel> helpChannels;
    public static final Logger LOGGER = LoggerFactory.getLogger(InoriChan.class);

    static {
        if (getConfig() == null) {
            System.exit(-1);
        }

        LOGGER.info("{} loaded.", CONFIG_FILENAME.replace('.', ' '));

        //DATABASES
        MongoClientURI uri = new MongoClientURI(CONFIG.getProperty("MONGO_URI"));
        DATABASE = new MongoClient(uri).getDatabase("TogetherJava");
    }

    public static void main(String[] args) throws Exception {

        String[] desc = {"Generates dank memes", "more to come..."};
        Permission[] perms = {Permission.MESSAGE_READ, Permission.MESSAGE_WRITE};

        EventWaiter waiter = new EventWaiter();
        CommandClientBuilder builder = new CommandClientBuilder();

        jda = new JDABuilder(getConfig().getProperty("BOT_TOKEN")).build();

        jda.addEventListener(waiter);
        builder.setOwnerId(getConfig().getProperty("OWNER_ID"));
        builder.setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26");
        builder.setPrefix(getConfig().getProperty("PREFIX")); // prefix for testbot < , prefix for InoriChan >
        builder.addCommand(new AboutCommand("\nInformation about the bot: \n", desc, perms));


        // TESTING

        jda.addEventListener(new ReportByPmEvent(waiter));

        // ANIMAL COMMANDS
        builder.addCommand(new CatCommand(getConfig()));
        builder.addCommand(new FoxCommand());
        builder.addCommand(new DogeCommand());
        builder.addCommand(new DogCommand(getConfig()));
        // MODERATION COMMANDS
        builder.addCommand(new ReportCommand());
        builder.addCommand(new SpamlordCommand());
        builder.addCommand(new PurgeCommand());
        builder.addCommand(new WhoIsCommand());
        // MEME COMMANDS
        builder.addCommand(new ScrollOfTruthCommand(getConfig()));
        builder.addCommand(new HurensohnCommand(getConfig()));
        builder.addCommand(new DrakeCommand(getConfig()));
        builder.addCommand(new DistractedBoyfriendCommand(getConfig()));
        builder.addCommand(new TwoButtonsCommand(getConfig()));
        builder.addCommand(new PillsCommand(getConfig()));
        builder.addCommand(new BrainCommand(getConfig()));
        builder.addCommand(new SpongebobCommand(getConfig()));
        builder.addCommand(new GoodNoodleCommand(getConfig()));
        builder.addCommand(new ChangeMyMindCommand(getConfig()));
        builder.addCommand(new NpcCommand(getConfig()));
        // GAME COMMANDS
        builder.addCommand(new RockPaperScissorsCommand());
        builder.addCommand(new HangmanCommand(waiter));
        builder.addCommand(new HighOrLowCommand(waiter));
        // UTILITY COMMANDS
        builder.addCommand(new TagCommand(DATABASE, waiter));
        builder.addCommand(new FreeCommand());
        builder.addCommand(new BigCommand());
        builder.addCommand(new unsplashCommand(getConfig(), waiter));
        builder.addCommand(new TranslateCommand());
        builder.addCommand(new DefinitionCommand());
        builder.addCommand(new GoogleCommand());
        builder.addCommand(new TopHelpMessages(DATABASE));
        builder.addCommand(new QuoteWtfCommand());
        builder.addCommand(new PollCommand(DATABASE));
        builder.addCommand(new TimerCommand());
        builder.addCommand(new YoutubeToMp3Command(getConfig()));
        builder.addCommand(new CodeCommand());
        builder.addCommand(new EmoteIdCommand());
        builder.addCommand(new AvatarCommand());
        builder.addCommand(new QuoteCommand());
        // FUN COMMANDS
        builder.addCommand(new CoinsCommand(DATABASE));
        builder.addCommand(new BubbleSortCommand());
        builder.addCommand(new OoflmaoCommand(DATABASE));
        builder.addCommand(new DecryptCommand());
        builder.addCommand(new EncryptCommand());
        builder.addCommand(new DabCommand());
        builder.addCommand(new DeletThisCommand());
        builder.addCommand(new GifSequenceWriter());

        // HIDDEN COMMANDS
        builder.addCommand(new SummonCommand());
        builder.addCommand(new DebugCommand());

        jda.addEventListener(new JavacoinEvent(DATABASE));
        jda.addEventListener(new StagingAreaEvent());
        jda.addEventListener(new BotCatchingEvent(DATABASE));
        jda.addEventListener(builder.build());
        jda.addEventListener(new AnimationEvent());
        jda.addEventListener(new imgToAsciiEvent());
        jda.addEventListener(new LemonSqueezyEvent());
        jda.addEventListener(new GoodBotEvent());
        jda.addEventListener(new SafetyFeature());
        jda.addEventListener(new AiTalkEvent(getConfig()));
        jda.addEventListener(new VoiceChannelJoinNotifyEvent());
        jda.addEventListener(new OofiesAndLmaosEvent(DATABASE));
        jda.addEventListener(new AntiScholzEvent());
        jda.addEventListener(new HelpMessageCountingEvent(DATABASE));
        jda.addEventListener(new PollReactionListener(DATABASE));
        jda.addEventListener(new UnspoilEvent(waiter));
    }

    public static Properties getConfig() {
        return CONFIG;
    }

    private static Properties loadConfig(String fileName) {
        // CONFIG
        Properties prop = new Properties();
        try {
            InputStream is = new FileInputStream(fileName);
            prop.load(is);
        } catch (FileNotFoundException ex) {
            LOGGER.info("Could not find config file");
        } catch (IOException ex) {
            LOGGER.info("Could not load config file");
        }
        return prop;
    }

    public static List<TextChannel> getHelpChannels() {
        if (helpChannels == null) {
            helpChannels = jda.getTextChannels().stream()
                    .filter(c -> c.getName().toLowerCase().contains("help"))
                    .collect(Collectors.toUnmodifiableList());
        }
        return helpChannels;
    }
}
