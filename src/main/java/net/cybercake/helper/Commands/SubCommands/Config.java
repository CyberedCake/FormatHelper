package net.cybercake.helper.Commands.SubCommands;

import net.cybercake.helper.Commands.SubCommand;
import net.cybercake.helper.Main;
import net.cybercake.helper.Utils.DataUtils;
import net.cybercake.helper.Utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Config extends SubCommand {

    @Override
    public String getName() {
        return "config";
    }

    @Override
    public String getPermission() {
        return "formathelper.modifyconfig";
    }

    @Override
    public String getDescription() {
        return "Lets you modify the configuration within the game instead of in the file manager.";
    }

    @Override
    public String getUsage() {
        return "/formathelper config <setting> [value]";
    }

    @Override
    public String[] getAliases() {
        return new String[]{""};
    }

    @Override
    public void perform(CommandSender sender, String[] args) {

        if(args.length < 2) {
            sender.sendMessage(Utils.getSeperator(ChatColor.BLUE));
            Utils.sendCenteredMessage(sender, "&d&lFORMAT HELPER CONFIG OPTIONS:");
            for(String str : Main.configOptions) {
                TextComponent component = new TextComponent(Utils.chat("&b" + str));
                component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/formathelper config " + str + " "));
                String detect = str.replace(":", ".");
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Utils.chat("&6Option: &f" + str + "\n&6Description: &f" + Main.configDescription.get(str) + "\n&6Type: &f" + Main.configType.get(str) + "\n&6Default Value: &f" + Main.configDefaultValue.get(str) + "\n&6Accepted Values: &f" + Main.configAcceptedValues.get(str) + "\n&6Current Value: &f" + Main.getPlugin().getConfig().get("config." + detect).toString())).create()));
                sender.sendMessage(component);
            }
            sender.sendMessage(Utils.getSeperator(ChatColor.BLUE));
        }else{
            boolean use = false;
            for(String str : Main.configOptions) {
                if(args[1].equals(str)) {
                    use = true;
                }
            }
            if(use) {
                if(args.length < 3) {
                    sender.sendMessage(Main.getPrefix());
                    TextComponent component = new TextComponent(Utils.chat("&6\"&f" + Main.getPlugin().getConfig().get("config." + args[1]).toString() + "&6\""));
                    TextComponent prefix = new TextComponent(Utils.chat(Main.getPrefix()));
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.stripColor(Main.getPlugin().getConfig().get("config." + args[1]).toString())).create()));
                    component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ChatColor.stripColor(Main.getPlugin().getConfig().get("config." + args[1]).toString())));
                    sender.sendMessage(Utils.chat(Main.getPrefix() + "&6The setting &e" + args[1] + " &6is currently:"));
                    BaseComponent mainSetting = prefix;
                    mainSetting.addExtra(component);
                    sender.sendMessage(mainSetting);
                    sender.sendMessage(Main.getPrefix());
                    sender.sendMessage(Main.getPrefix() + Utils.chat("&6Description: &f" + Main.configDescription.get(args[1])));
                    sender.sendMessage(Main.getPrefix() + Utils.chat("&6Type: &f" + Main.configType.get(args[1])));
                    sender.sendMessage(Main.getPrefix() + Utils.chat("&6Accepted Values: &f" + Main.configAcceptedValues.get(args[1])));
                    sender.sendMessage(Main.getPrefix());
                }else if(args.length > 2) {

                    String sm = "";
                    for(int i = 2; i < args.length; i++) {
                        String arg = "";
                        if(i == args.length-1) {
                            arg = (args[i]);
                        }else{
                            arg = (args[i] + " ");
                        }
                        sm = (sm + arg);
                    }

                    ArrayList<String> acceptedValues = new ArrayList<>();
                    for(Object str : Main.configAcceptedValues.get(args[1])) {
                        acceptedValues.add(str.toString());
                    }

                    if(ChatColor.stripColor(Objects.requireNonNull(DataUtils.getCustomYmlObject("config", "config." + args[1])).toString()).equalsIgnoreCase(ChatColor.stripColor(sm))) {
                        sender.sendMessage(Main.getPrefix() + Utils.chat("&cThat configuration value is already set to what you've entered!"));
                        if(sender instanceof Player) {
                            Player p = (Player) sender;
                            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 2F, 2F);
                        }
                    }else if((!Main.configType.get(args[1]).equalsIgnoreCase("String")) && (!acceptedValues.contains(sm))) {
                        TextComponent mainBef = new TextComponent(Main.getPrefix() + Utils.chat("&cExpected "));
                        TextComponent mainAfter = new TextComponent(Utils.chat("&c, but found &8" + sm));
                        TextComponent type = new TextComponent(Utils.chat("&c&n" + Main.configType.get(args[1])));
                        type.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Utils.chat("&cExpected one of the following values:\n&f" + acceptedValues)).create()));
                        mainBef.addExtra(type);
                        mainBef.addExtra(mainAfter);
                        sender.sendMessage(mainBef);
                        if(sender instanceof Player) {
                            Player p = (Player) sender;
                            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 2F, 2F);
                        }
                    }else{
                        try {
                            DataUtils.setCustomYml("config", "config." + args[1], ChatColor.stripColor(sm));
                            Main.getPlugin().reloadConfig();
                            TextComponent main = new TextComponent(Main.getPrefix() + Utils.chat("&aYou set " + args[1] + " &ato &f"));
                            TextComponent toWhat = new TextComponent(sm);
                            toWhat.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Utils.chat(sm)).create()));
                            main.addExtra(toWhat);
                            sender.sendMessage(main);
                            if(sender instanceof Player) {
                                Player p = (Player) sender;
                                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2F, 2F);
                            }
                        } catch (Exception e) {
                            sender.sendMessage(Utils.chat("&cFailed to change that configuration value. &c&oView console for more information!"));
                            ArrayList<String> detectedPlugins = new ArrayList<>();
                            if(Bukkit.getPluginManager().getPlugins() != null) {
                                for(Plugin pl : Bukkit.getPluginManager().getPlugins()) {
                                    detectedPlugins.add(pl.getDescription().getName());
                                }
                            }
                            Bukkit.getLogger().severe(" ");
                            Bukkit.getLogger().severe("Something went wrong whilst trying to modify configuration in plugin 'FormatHelper'");
                            Bukkit.getLogger().severe("This usually is not your fault, but before reporting this please try deleting your config.yml and then restarting your server! If that does not fix the issue, follow the steps below.");
                            Bukkit.getLogger().severe(" ");
                            Bukkit.getLogger().severe("Failed to change config values, report this on github: " + ChatColor.GOLD + e.toString());
                            Bukkit.getLogger().severe("The GitHub issue tracked can be found here: https://github.com/CyberedCake/FormatHelper/issues");
                            Bukkit.getLogger().severe(ChatColor.UNDERLINE + ">>> Be sure to include the log file, your config.yml, and jar file in your issue reports! <<<");
                            Bukkit.getLogger().severe(" ");
                            Main.printBetterStackTrace(e);
                            Bukkit.getLogger().severe(" ");
                            Bukkit.getLogger().severe("Since this is not a fatal exception, the stack trace has not been logged to errors.txt!");
                            Bukkit.getLogger().severe(" ");
                            Bukkit.getLogger().severe("FormatHelper version (per Jar File): " + Main.version);
                            Bukkit.getLogger().severe("FormatHelper version (per plugin): " + Main.getPlugin().getDescription().getVersion());
                            Bukkit.getLogger().severe("FormatHelper config version (per Jar File): " + Main.configVersion);
                            if(DataUtils.getCustomYmlString("config","serverInfo.configVersion") != null) {
                                Bukkit.getLogger().severe("FormatHelper config version (per config): " + DataUtils.getCustomYmlString("config","serverInfo.configVersion"));
                            }else{
                                Bukkit.getLogger().severe("FormatHelper config version (per config): " + null);
                            }
                            Bukkit.getLogger().severe("FormatHelper Error Date: " + Utils.getFormattedDate("MMM-dd-yyyy HH:mm:ss z"));
                            Bukkit.getLogger().severe("Java Version: Java " + System.getProperty("java.version") + " (" + System.getProperty("java.vm.name") + ")");
                            Bukkit.getLogger().severe("Operating System: " + System.getProperty("os.name"));
                            Bukkit.getLogger().severe("Detected Plugins (" + detectedPlugins.size() + "): " + detectedPlugins);
                            Bukkit.getLogger().severe(" ");
                        }
                    }
                }
            }else{
                sender.sendMessage(Utils.chat("&cUnknown configuration option: &8" + args[1]));
            }
        }
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        ArrayList<String> returnValue = new ArrayList<>();
        if (args.length <= 2) {
            if (!args[1].contains(".")) {
                returnValue.addAll(Main.getPlugin().getConfig().getConfigurationSection("config.").getKeys(false));
            } else {
                returnValue.addAll(Main.configOptions);
            }
        }else if(args.length == 3){
            if(!Main.configAcceptedValues.get(args[1]).contains("Any String")) {
                for(Object accepted : Main.configAcceptedValues.get(args[1])) {
                    returnValue.add(accepted.toString());
                    // Bukkit.broadcastMessage(returnValue + "");
                }
                return returnValue;
            }
        }
        return returnValue;
    }
}
