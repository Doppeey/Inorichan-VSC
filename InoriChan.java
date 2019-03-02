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
import EventsAndCommands.TestCommandsAndEvents.PmReportTest;
import EventsAndCommands.TestCommandsAndEvents.VCJoinByID;
import EventsAndCommands.UtilityCommands.*;
import EventsAndCommands.UtilityEvents.BotCatchingEvent;
import EventsAndCommands.UtilityEvents.HelpMessageCountingEvent;
import EventsAndCommands.UtilityEvents.PollReactionListener;
import EventsAndCommands.UtilityEvents.StagingAreaEvent;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.GuildController;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


class InoriChan extends ListenerAdapter {


    public static void main(String[] args) throws Exception {


        //Loading config file
        final String configFileName = "testbot.config";
        Properties config = loadConfig(configFileName);
        System.out.println(configFileName.substring(0,configFileName.length()-7)+" config loaded.");



        //DATABASE
        MongoClientURI uri = new MongoClientURI(
                config.getProperty("MONGO_URI"));

        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase("TogetherJava");
        //DATABASE END

        String[] desc = {"Generates dank memes", "more to come..."};
        Permission[] perms = {Permission.MESSAGE_READ, Permission.MESSAGE_WRITE};

        EventWaiter waiter = new EventWaiter();
        CommandClientBuilder InoriChan = new CommandClientBuilder();



        JDA jda = new JDABuilder(config.getProperty("BOT_TOKEN")).build();


        jda.addEventListener(waiter);
        InoriChan.setOwnerId(config.getProperty("OWNER_ID"));
        InoriChan.setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26");
        InoriChan.setPrefix(config.getProperty("PREFIX")); // prefix for testbot < , prefix for InoriChan >
        InoriChan.addCommand(new AboutCommand("\nInformation about the bot: \n", desc, perms));
        GuildController gc = new GuildController(jda.getGuildById("272761734820003841"));


        //TESTING

        jda.addEventListener(new PmReportTest(waiter));

        //ANIMAL COMMANDS
        InoriChan.addCommand(new CatCommand(config));
        InoriChan.addCommand(new FoxCommand());
        InoriChan.addCommand(new DogeCommand());
        InoriChan.addCommand(new DogCommand(config));
        //MODERATION COMMANDS
        //InoriChan.addCommand(new RestartCommand());
        InoriChan.addCommand(new ReportCommand());
        InoriChan.addCommand(new SpamlordCommand());
        InoriChan.addCommand(new PurgeCommand());
        InoriChan.addCommand(new WhoIsCommand());
        //MEME COMMANDS
        InoriChan.addCommand(new HurensohnCommand(config));
        InoriChan.addCommand(new DrakeCommand(config));
        InoriChan.addCommand(new DistractedBoyfriendCommand(config));
        InoriChan.addCommand(new TwoButtonsCommand(config));
        InoriChan.addCommand(new PillsCommand(config));
        InoriChan.addCommand(new BrainCommand(config));
        InoriChan.addCommand(new SpongebobCommand(config));
        InoriChan.addCommand(new GoodNoodleCommand(config));
        InoriChan.addCommand(new ChangeMyMindCommand(config));
        InoriChan.addCommand(new NpcCommand(config));
        //GAME COMMANDS
        InoriChan.addCommand(new RockPaperScissorsCommand());
        InoriChan.addCommand(new HangmanCommand(waiter));
        InoriChan.addCommand(new HighOrLowCommand(waiter));
        //UTILITY COMMANDS

        InoriChan.addCommand(new DefinitionCommand());
        InoriChan.addCommand(new GoogleCommand());
        InoriChan.addCommand(new TopHelpMessages(database));
        InoriChan.addCommand(new QuoteWtfCommand());
        InoriChan.addCommand(new PollCommand(database));
        InoriChan.addCommand(new TimerCommand());
        InoriChan.addCommand(new YoutubeToMp3Command(config));
        InoriChan.addCommand(new CodeCommand());
        InoriChan.addCommand(new EmoteIdCommand());
        InoriChan.addCommand(new AvatarCommand());
        InoriChan.addCommand(new QuoteCommand());
        //FUN COMMANDS

        InoriChan.addCommand(new OoflmaoCommand(database));
        InoriChan.addCommand(new DecryptCommand());
        InoriChan.addCommand(new EncryptCommand());
        InoriChan.addCommand(new DabCommand());
        InoriChan.addCommand(new DeletThisCommand());
        InoriChan.addCommand(new GifSequenceWriter());



        //HIDDEN COMMANDS
        InoriChan.addCommand(new VCJoinByID());
        InoriChan.addCommand(new SummonCommand());
        InoriChan.addCommand(new DebugCommand());

        jda.addEventListener(new StagingAreaEvent());
        jda.addEventListener(new BotCatchingEvent(database));
        jda.addEventListener(InoriChan.build());
        jda.addEventListener(new AnimationEvent());
        jda.addEventListener(new imgToAsciiEvent());
        jda.addEventListener(new LemonSqueezyEvent());
        jda.addEventListener(new GoodBotEvent());
        jda.addEventListener(new AiTalkEvent(config));
        jda.addEventListener(new VoiceChannelJoinNotifyEvent());
        jda.addEventListener(new OofiesAndLmaosEvent(database));
        jda.addEventListener(new AntiScholzEvent());
        jda.addEventListener(new HelpMessageCountingEvent(database));
        jda.addEventListener(new PollReactionListener(database));


    }

    private static Properties loadConfig(String fileName) throws Exception {

        //CONFIG
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
