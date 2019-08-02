package me.doppey.tjbot.commands.hidden;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.doppey.tjbot.InoriChan;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;

import java.util.List;
import java.util.stream.Collectors;

public class SummonServerCommand extends Command implements Hiddencommand {

    public SummonServerCommand() {
        this.name = "summonserver";
        this.hidden = true;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        if (commandEvent.getMember().getUser().getId().equalsIgnoreCase("272158112318750720")) {
            if (!commandEvent.getGuild().getId().equalsIgnoreCase("272761734820003841")) {
                if (!Hiddencommand.isTogetherJavaServer(commandEvent.getGuild().getId())) {
                    Member summoningMember = commandEvent.getMember();
                    final String summonerName = summoningMember.getEffectiveName();
                    final String serverName = commandEvent.getGuild().getName();

                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle("SUMMONING EVERYONE");
                    embed.setImage("https://i.imgur.com/9796NI2.gif");

                    final List<Member> memberList = commandEvent.getGuild().getMembers(); // List of all members
                    final List<Member> filteredList =
                            memberList.stream().filter(member -> !member.getUser().isBot()).collect(Collectors.toList()); // List of all members without bots

                    Runnable summonAll = () -> {
                        int howManyMembersHadPmOff = 0;
                        commandEvent.getChannel().sendMessage("Summoning " + filteredList.size() + " members").queue();

                        for (Member member : filteredList) {
                            try {
                                member.getUser().openPrivateChannel().queue(x -> {
                                    for (int i = 0; i < 5; i++) {
                                        x.sendMessage("DEAR MAN OR WOMAN, YOU ARE BEING SUMMONED BY " + summonerName + "\n IN THE SERVER " + serverName).queue();
                                    }

                                    x.close().queue();
                                });
                            } catch (Exception e) {
                                InoriChan.LOGGER.error("Couldn't contact {}", member.getEffectiveName(), e);
                                howManyMembersHadPmOff++;
                            }
                        }

                        commandEvent.reply(embed.build());

                        for (int i = 0; i < 5; i++) {
                            commandEvent.getChannel().sendMessage("@everyone").queue(x -> x.delete().queue());
                        }
                        commandEvent.reply("I contacted everyone, " + howManyMembersHadPmOff + " members had their " +
                                "PM's off");

                    };
                    Thread summonThread = new Thread(summonAll);
                    summonThread.start();
                }
            }
        }
    }
}
