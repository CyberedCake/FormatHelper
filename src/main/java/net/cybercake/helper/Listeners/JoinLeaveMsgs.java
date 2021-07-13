package net.cybercake.helper.Listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import net.cybercake.helper.Main;
import net.cybercake.helper.Utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeaveMsgs implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if(!Main.getPlugin().getConfig().getBoolean("config.useDefaultMessages")) { e.joinMessage(Component.text(Utils.chat(replacePlaceholders(Main.getPlugin().getConfig().getString("config.joinMsg"), e.getPlayer())))); }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if(!Main.getPlugin().getConfig().getBoolean("config.useDefaultMessages")) { e.quitMessage(Component.text(Utils.chat(replacePlaceholders(Main.getPlugin().getConfig().getString("config.quitMsg"), e.getPlayer())))); }
    }

    private static String replacePlaceholders(String str, Player player) {
        if(Main.placeholderAPI) { str = PlaceholderAPI.setPlaceholders(player, str); }
        return str.replace("%player%", player.getName());
    }



}
