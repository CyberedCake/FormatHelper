package net.cybercake.helper.Listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import net.cybercake.helper.Main;
import net.cybercake.helper.Utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;

public class ChatMsg implements Listener {

    @EventHandler
    public void onChat(PlayerChatEvent e) {
        try {
            if(!Main.getPlugin().getConfig().getBoolean("config.useDefaultMessages")) {
                e.setFormat(replacePlaceholders(Main.getPlugin().getConfig().getString("config.chatFormat"), e.getMessage(), e.getPlayer()));
            }
        } catch (Exception ex) {
            Bukkit.getLogger().severe(" ");
            Bukkit.getLogger().severe("Failed to set the chat format, resorting back the normal format. Report the error to the github: " + ChatColor.GOLD + ex.toString());
            Bukkit.getLogger().severe(" ");
            Main.printBetterStackTrace(ex);
            Bukkit.getLogger().severe(" ");
            Bukkit.getLogger().severe("Chat Format Config Value: " + Main.getPlugin().getConfig().getString("config.chatFormat"));
            Bukkit.getLogger().severe("Chat Format Config Value (w/ Placeholders): " + replacePlaceholders(Main.getPlugin().getConfig().getString("config.chatFormat"), e.getMessage(), e.getPlayer()));
            Bukkit.getLogger().severe(" ");
            if(Main.devBuild) {

            }
        }

    }

    private static String replacePlaceholders(String str, String msg, Player player) {
        if(Main.placeholderAPI) { str = PlaceholderAPI.setPlaceholders(player, str); }
        return str.replace("%player%", player.getName()).replace("%message%", msg).replace("%colored message%", Utils.chat(msg));
    }

}
