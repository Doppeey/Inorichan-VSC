package EventsAndCommands.TestCommandsAndEvents;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

public class PmReportTest extends ListenerAdapter {

    EventWaiter waiter;


    public PmReportTest(EventWaiter waiter) {
        this.waiter = waiter;

    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        Runnable timeOut = new Runnable() {
            @Override
            public void run() {
                event.getChannel().sendMessage("Your report has timed out because you haven't replied in 5 minutes").queue();

            }
        };

        final String[] reportedUser = new String[1];
        final String[] reportReason = new String[1];


        if(event.getMessage().getContentRaw().equalsIgnoreCase("report")){
            event.getAuthor().openPrivateChannel().complete().sendMessage("Please supply the ID of the person you'd like to report").queue();


          waiter.waitForEvent(event.getClass(), who -> event.getAuthor().equals(who.getAuthor()), message -> {
              System.out.println("yes");

              reportedUser[0] = message.getMessage().getContentRaw();
              event.getChannel().sendMessage("What did they do ?").queue();

              waiter.waitForEvent(event.getClass(), why -> event.getAuthor().equals(why.getAuthor()), why -> {

                  reportReason[0] = why.getMessage().getContentRaw();
                  why.getChannel().sendMessage("Your report has been submitted and will be reviewed by one of our moderators as soon as possible, thank you!").queue();
                  event.getJDA().getGuildById("459848257867350017").getTextChannelById("459848258299232277").sendMessage("User "+event.getAuthor().getName()+" has reported the user with id "+reportedUser[0]+"" +
                          "\n**Reason:** "+reportReason[0]).queue();

              },5,TimeUnit.MINUTES,timeOut);


          }, 5, TimeUnit.MINUTES, timeOut);










        }




    }
}
