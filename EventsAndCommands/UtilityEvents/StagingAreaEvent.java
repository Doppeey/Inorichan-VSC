package EventsAndCommands.UtilityEvents;

import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.GuildController;

public class StagingAreaEvent extends ListenerAdapter {

    private GuildController gc = null;
    private Role stagingRole = null;

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (gc == null) {
            this.gc = new GuildController(event.getGuild());
        }
        if (stagingRole == null) {
            stagingRole = gc.getGuild().getRolesByName("stagingrole", true).get(0);
        }
        giveMemberStagingRole(event);
    }


    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getGuild().getId().equalsIgnoreCase("272761734820003841")) {
            //VARIABLES
            if (gc == null) {
                this.gc = new GuildController(event.getGuild());
            }
            if (stagingRole == null) {
                stagingRole = gc.getGuild().getRolesByName("stagingrole", true).get(0);
            }
            final boolean isWelcomeChannel = event.getChannel().getId().equalsIgnoreCase("513551097449807883");
            final boolean hasAcceptedRules = event.getMessage().getContentRaw().equalsIgnoreCase("accept");


            if (isWelcomeChannel) {
                if (hasAcceptedRules) {
                    removeStagingRoleFromUser(event);
                }
                event.getMessage().delete().queue();
            }
        }

    }


    private void giveMemberStagingRole(GuildMemberJoinEvent event) {
        gc.addSingleRoleToMember(event.getMember(), gc.getGuild().getRolesByName("stagingrole", true).get(0)).queue();
    }

    private void removeStagingRoleFromUser(GuildMessageReceivedEvent event) {
        gc.removeSingleRoleFromMember(event.getMember(), event.getMember().getRoles().get(0)).queue();
    }
}
