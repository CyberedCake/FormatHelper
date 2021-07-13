package net.cybercake.helper.Listeners;

import net.cybercake.helper.Commands.CommandManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

public class BlockCMD implements Listener {

    @EventHandler
    public void onCommandSender(PlayerCommandSendEvent e) {
        Player p = e.getPlayer();

        CommandManager manager = new CommandManager();

        e.getCommands().remove("formathelper:formathelper");
        e.getCommands().remove("formathelper:fh");
        if(manager.getSubCommandsOnlyWithPerms(p).size() <= 1) {
            e.getCommands().remove("formathelper");
            e.getCommands().remove("fh");
        }
    }

}
