package EventsAndCommands.TestCommandsAndEvents;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

public class VCJoinByID extends Command {



    public VCJoinByID(){

        this.name = "joinvc";
        this.hidden = true;

    }


    @Override
    protected void execute(CommandEvent commandEvent) {
        AudioManager audioManager = commandEvent.getGuild().getAudioManager();

        if(audioManager.isConnected()){
            audioManager.closeAudioConnection();
        }


        VoiceChannel vc = commandEvent.getMember().getVoiceState().getChannel();
        audioManager.openAudioConnection(vc);

    }
}
