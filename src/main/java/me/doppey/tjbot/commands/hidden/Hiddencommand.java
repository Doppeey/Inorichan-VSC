package me.doppey.tjbot.commands.hidden;

import me.doppey.tjbot.Constants;

interface Hiddencommand {


    static boolean isTogetherJavaServer(String serverId) {
        return serverId.equalsIgnoreCase(Constants.TJ_GUILD.getId());
    }


}
