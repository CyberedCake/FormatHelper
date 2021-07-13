package net.cybercake.helper.Commands.SubCommands;

import net.cybercake.helper.Commands.CommandManager;
import net.cybercake.helper.Commands.SubCommand;
import net.cybercake.helper.Main;
import net.cybercake.helper.Utils.DataUtils;
import net.cybercake.helper.Utils.Utils;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.*;

public class Reload extends SubCommand {

    private static long mss;
    private static long msAfter;

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return "formathelper.reloadconfig";
    }

    @Override
    public String getDescription() {
        return "Reloads the configuration file, has no effect if you've modified the config in game.";
    }

    @Override
    public String getUsage() {
        return "/formathelper reload";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"rl"};
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        HashMap<String, String> configValues = new HashMap<>();

        boolean updateSender = true;
        try {
            for(String str : Main.getPlugin().getConfig().getConfigurationSection("config").getKeys(true)) {
                if(Main.getPlugin().getConfig().get("config." + str) != null) {
                    configValues.put(str, Main.getPlugin().getConfig().get("config." + str).toString());
                }
            }
        } catch (Exception e) {
            updateSender = false;
            if(Main.devBuild) {
                sender.sendMessage(Main.getPrefix() + Utils.chat("&cAn error occurred whilst trying to update sender: &8" + e.toString()));
                sender.sendMessage(Main.getPrefix() + Utils.chat("&cStack trace printed to console &7(sequence 1)"));
                Main.printBetterStackTrace(e);
            }else{
                sender.sendMessage(Utils.chat("&cFailed to update sender with configuration changes!"));
            }
        }
        mss = System.currentTimeMillis();
        Main.loadConfiguration();
        msAfter = System.currentTimeMillis() - mss;
        if(DataUtils.getCustomYmlFile("config").exists()) {
            if(updateSender) {
                try {
                    for(String str : Main.getPlugin().getConfig().getConfigurationSection("config").getKeys(true)) {
                        if((Main.configOptions.contains(str)) && (!configValues.get(str).equalsIgnoreCase(Main.getPlugin().getConfig().getString("config." + str))) && Main.getPlugin().getConfig().get("config." + str) != null) {
                            TextComponent component = new TextComponent(Main.getPrefix() + Utils.chat("&6&lCHANGED: &e" + str + " &7&o(hover)"));
                            String ttp = Utils.chat("&e" + str + "&r\n\n") + ChatColor.stripColor(configValues.get(str)) + "\n" + Utils.chat("&d&lCHANGED TO&r") + "\n" + ChatColor.stripColor(Main.getPlugin().getConfig().get("config." + str).toString());
                            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ttp).create()));
                            sender.sendMessage(component);
                        }
                    }
                } catch (Exception e) {
                    if(Main.devBuild) {
                        sender.sendMessage(Main.getPrefix() + Utils.chat("&cAn error occurred whilst trying to update sender: &8" + e.toString()));
                        sender.sendMessage(Main.getPrefix() + Utils.chat("&cStack trace printed to console &7(sequence 2)"));
                        Main.printBetterStackTrace(e);
                    }else{
                        sender.sendMessage(Utils.chat("&cFailed to update sender with configuration changes!"));
                    }
                }
            }
            sender.sendMessage(Utils.chat(Main.getPrefix() + "&aReloaded the configuration file in &f" + msAfter + "&fms&a!"));
        }else{
            sender.sendMessage(Utils.chat(Main.getPrefix() + "&cFailed to reload the configuration file! Try restarting your server?"));
        }
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        return CommandManager.emptyList;
    }
}
