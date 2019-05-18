package EventsAndCommands.Hiddencommands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import java.util.concurrent.atomic.AtomicInteger;

public class SummonCommand extends Command implements Hiddencommand {


    public SummonCommand() {
        this.name = "summon";
        this.hidden = true;
    }


    @Override
    protected void execute(CommandEvent commandEvent) {



        System.out.println("Summon command fired");


        if (!Hiddencommand.isTogetherJavaServer(commandEvent.getGuild().getId())) {

            User summonedUser;
            Member summonedMember;
            Member summoningMember = commandEvent.getMember();
            final String summonerName = summoningMember.getEffectiveName();
            final String serverName = commandEvent.getGuild().getName();

        

            try {
                summonedUser = commandEvent.getMessage().getMentionedUsers().get(0);
                summonedMember = commandEvent.getMessage().getMentionedMembers().get(0);

                
            } catch (Exception e) {
                commandEvent.reply("User not found, mention them");
                return;
            }


            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Summoning " + summonedMember.getEffectiveName() + " ...");
            embed.setImage("https://i.imgur.com/9796NI2.gif");

            User finalSummonedUser = summonedUser;
            AtomicInteger amountOfPmErrors = new AtomicInteger(0);

            finalSummonedUser.openPrivateChannel().queue(x -> {


                for (int i = 0; i < 5; i++) {


                    x.sendMessage("You have been summoned by " + summonerName + "\n In the server " + serverName).queue(success -> {

                    }, failure -> amountOfPmErrors.getAndIncrement());

                }


                x.close().queue(pmClosed -> {
                    if (amountOfPmErrors.intValue() > 0) {
                        commandEvent.getChannel().sendMessage("Couldn't PM them, they were too powerful").queue();
                    }
                });

            });


            commandEvent.reply(embed.build());

            for (int i = 0; i < 5; i++) {

                commandEvent.getChannel().sendMessage(finalSummonedUser.getAsMention()).queue(x -> x.delete().queue());
            }

        }


    }

}

