import EventsAndCommands.ChatEventDistributor;
import EventsAndCommands.EventUtility.AboutCommand;
import EventsAndCommands.FunCommands.*;
import EventsAndCommands.FunCommands.DeAndEncryptor.DecryptCommand;
import EventsAndCommands.FunCommands.DeAndEncryptor.EncryptCommand;
import EventsAndCommands.FunCommands.MemeCommands.*;
import EventsAndCommands.FunCommands.MemeCommands.CrabRaveMeme.GifSequenceWriter;
import EventsAndCommands.FunEvents.*;
import EventsAndCommands.GameCommands.HangmanCommand;
import EventsAndCommands.GameCommands.HighOrLowCommand;
import EventsAndCommands.GameCommands.RockPaperScissorsCommand;
import EventsAndCommands.Hiddencommands.SummonCommand;
import EventsAndCommands.ModerationCommands.PurgeCommand;
import EventsAndCommands.ModerationCommands.ReportCommand;
import EventsAndCommands.ModerationCommands.SpamlordCommand;
import EventsAndCommands.ModerationCommands.WhoIsCommand;
import EventsAndCommands.UtilityEvents.ReportByPmEvent;
import EventsAndCommands.TestCommandsAndEvents.VCJoinByID;
import EventsAndCommands.UtilityCommands.*;
import EventsAndCommands.UtilityEvents.BotCatchingEvent;
import EventsAndCommands.UtilityEvents.HelpMessageCountingEvent;
import EventsAndCommands.UtilityEvents.PollReactionListener;
import EventsAndCommands.UtilityEvents.StagingAreaEvent;
import EventsAndCommands.UtilityEvents.UnspoilEvent;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.hooks.ListenerAdapter;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import EventsAndCommands.*;

class InoriChan extends ListenerAdapter {


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


        ChatEventDistributor.getInstance().load(ChatEventHandler.class);





        // TESTING

        // jda.addEventListener(new ReportByPmEvent(waiter));

        // ANIMAL COMMANDS
        inoriChan.addCommand(new CatCommand(config));
        inoriChan.addCommand(new FoxCommand());
        inoriChan.addCommand(new DogeCommand());
        inoriChan.addCommand(new DogCommand(config));
        // MODERATION COMMANDS
        inoriChan.addCommand(new ReportCommand());
        inoriChan.addCommand(new SpamlordCommand());
        inoriChan.addCommand(new PurgeCommand());
        inoriChan.addCommand(new WhoIsCommand());
        // MEME COMMANDS
        inoriChan.addCommand(new ScrollOfTruthCommand(config));
        inoriChan.addCommand(new HurensohnCommand(config));
        inoriChan.addCommand(new DrakeCommand(config));
        inoriChan.addCommand(new DistractedBoyfriendCommand(config));
        inoriChan.addCommand(new TwoButtonsCommand(config));
        inoriChan.addCommand(new PillsCommand(config));
        inoriChan.addCommand(new BrainCommand(config));
        inoriChan.addCommand(new SpongebobCommand(config));
        inoriChan.addCommand(new GoodNoodleCommand(config));
        inoriChan.addCommand(new ChangeMyMindCommand(config));
        inoriChan.addCommand(new NpcCommand(config));
        // GAME COMMANDS
        inoriChan.addCommand(new RockPaperScissorsCommand());
        inoriChan.addCommand(new HangmanCommand(waiter));
        inoriChan.addCommand(new HighOrLowCommand(waiter));
        // UTILITY COMMANDS
        inoriChan.addCommand(new unsplashCommand(config,waiter));
        inoriChan.addCommand(new TranslateCommand());
        inoriChan.addCommand(new DefinitionCommand());
        inoriChan.addCommand(new GoogleCommand());
        inoriChan.addCommand(new TopHelpMessages(database));
        inoriChan.addCommand(new QuoteWtfCommand());
        inoriChan.addCommand(new PollCommand(database));
        inoriChan.addCommand(new TimerCommand());
        inoriChan.addCommand(new YoutubeToMp3Command(config));
        inoriChan.addCommand(new CodeCommand());
        inoriChan.addCommand(new EmoteIdCommand());
        inoriChan.addCommand(new AvatarCommand());
        inoriChan.addCommand(new QuoteCommand());
        // FUN COMMANDS
        inoriChan.addCommand(new BubbleSortCommand());
        inoriChan.addCommand(new OoflmaoCommand(database));
        inoriChan.addCommand(new DecryptCommand());
        inoriChan.addCommand(new EncryptCommand());
        inoriChan.addCommand(new DabCommand());
        inoriChan.addCommand(new DeletThisCommand());
        inoriChan.addCommand(new GifSequenceWriter());

        // HIDDEN COMMANDS
        inoriChan.addCommand(new VCJoinByID());
        inoriChan.addCommand(new SummonCommand());
        inoriChan.addCommand(new DebugCommand());

        jda.addEventListener(new StagingAreaEvent());
        jda.addEventListener(new BotCatchingEvent(database));
        jda.addEventListener(inoriChan.build());
        jda.addEventListener(new AnimationEvent());
        jda.addEventListener(new imgToAsciiEvent());
        jda.addEventListener(new LemonSqueezyEvent());
        jda.addEventListener(new AiTalkEvent(config));
        jda.addEventListener(new VoiceChannelJoinNotifyEvent());
        jda.addEventListener(new OofiesAndLmaosEvent(database));
        jda.addEventListener(new AntiScholzEvent());
        jda.addEventListener(new HelpMessageCountingEvent(database));
        jda.addEventListener(new PollReactionListener(database));
        jda.addEventListener(new UnspoilEvent(waiter));
        jda.addEventListener(ChatEventDistributor.getInstance());

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
