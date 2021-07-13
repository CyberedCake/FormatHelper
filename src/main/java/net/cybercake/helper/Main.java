package net.cybercake.helper;

import net.cybercake.helper.Commands.CommandManager;
import net.cybercake.helper.Listeners.BlockCMD;
import net.cybercake.helper.Listeners.ChatMsg;
import net.cybercake.helper.Listeners.JoinLeaveMsgs;
import net.cybercake.helper.Utils.DataUtils;
import net.cybercake.helper.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

public final class Main extends JavaPlugin {

    private static Main plugin;
    public static String version = "1.0.0";
    public static String configVersion = "1.0.0.5";
    public static boolean devBuild = true;
    public static boolean errored = false;
    public static boolean placeholderAPI = false;
    public static HashMap<String, String> configType = new HashMap<>();
    public static HashMap<String, String> configDefaultValue = new HashMap<>();
    public static HashMap<String, List<Object>> configAcceptedValues = new HashMap<>();
    public static HashMap<String, String> configDescription = new HashMap<>();

    public static ArrayList<String> configOptions = new ArrayList<>();

    @Override
    public void onLoad() {
        plugin = this;

        try {
            reloadConfig();
            loadConfiguration();
        } catch (Exception e) {
            failedPlugin(e);
        }
    }

    @Override
    public void onEnable() {
        plugin = this;

        try {
            getCommand("formathelper").setExecutor(new CommandManager());

            Bukkit.getPluginManager().registerEvents(new BlockCMD(), this);
            Bukkit.getPluginManager().registerEvents(new JoinLeaveMsgs(), this);
            Bukkit.getPluginManager().registerEvents(new ChatMsg(), this);

            if(!DataUtils.getCustomYmlFile("serverinfo").exists()) {
                loadConfiguration();
                System.out.println(Utils.chat(getPrefix() + "&aGenerated a new configuration file because we couldn't find an already-generated one!"));
            }else if(DataUtils.getCustomYmlString("serverinfo", "serverInfo.configVersion") == null) {
                loadConfiguration();
                System.out.println(Utils.chat(getPrefix() + "&aGenerated a new configuration file because we couldn't detect the config version!"));
            }else{
                reloadConfig();
                System.out.println(Utils.chat(getPrefix() + "&aReloaded the configuration file!"));
            }

            setConfigurationOption("prefix", "String", "&9&l[&b&lFH&9&l] &r", Arrays.asList("Any String"), "The prefix before most all FormatHelper commands.");
            setConfigurationOption("noPermissionMsg", "String", "&cYou don't have permission to use this!", Arrays.asList("Any String"), "When a player enters a command they don't have permission for, this is what shows up.");
            setConfigurationOption("useDefaultMessages", "Boolean", "false", Arrays.asList("true", "false"), "Determines if anything in the config takes effect.");
            setConfigurationOption("joinMsg", "String", "&e%player% &ejoined the game", Arrays.asList("Any String"), "What the join message of the server will be.");
            setConfigurationOption("quitMsg", "String", "&e%player% &eleft the game", Arrays.asList("Any String"), "What the quit message of the server will be.");
            setConfigurationOption("chatFormat", "String", "<%player%> %message%", Arrays.asList("Any String"), "What the quit message of the server will be.");

            configOptions.clear();
            for(String configOption : DataUtils.getCustomYmlFileConfig("config").getConfigurationSection("config").getKeys(true)) {
                if(configType.get(configOption) != null) {
                    configOptions.add(configOption);
                }
            }

            if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                System.out.println(Utils.chat(getPrefix() + "&aSuccessfully hooked into &fPlaceholderAPI &aversion &e" + Bukkit.getPluginManager().getPlugin("PlaceholderAPI").getDescription().getVersion() + ""));
                placeholderAPI = true;
            }

            System.out.println(Utils.chat(getPrefix() + "&aSuccessfully loaded &fFormatHelper &aversion &e" + version + ""));
        } catch (Exception e) {
            failedPlugin(e);
        }

    }

    @Override
    public void onDisable() {
        System.out.println(Utils.chat(getPrefix() + "&aSuccessfully unloaded &fFormatHelper &aversion &e" + version + ""));
    }

    public static void loadConfiguration() {
        Main.getPlugin().saveDefaultConfig();
        Main.getPlugin().reloadConfig();
        DataUtils.deleteFile("serverinfo");

        //Main.getPlugin().getConfig().set("configInfo.pluginDescription", "FormatHelper is a plugin that helps server owners more openly configure chat messages, such as join/leave messages, through configuration files and commands!");
        //Main.getPlugin().getConfig().set("configInfo.doNotChange", "Please do not change anything under the heading 'serverInfo', as it can reset your config or cause console errors.");
        //Main.getPlugin().getConfig().set("configInfo.whenUpdating", "When you update the plugin, please be sure to save your configuration options, as it will reset all config options and you'll have to retype everything.");

        if(DataUtils.getCustomYmlString("serverinfo", "serverInfo.originallyLoadedVer") == null) {
            DataUtils.setCustomYml("serverinfo", "serverInfo.originallyLoadedVer", version);
        }

        DataUtils.setCustomYml("serverinfo", "serverInfo.version", version);
        DataUtils.setCustomYml("serverinfo", "serverInfo.configVersion", configVersion);
        DataUtils.setCustomYml("serverinfo", "serverInfo.serverVersion", Bukkit.getVersion());
        DataUtils.setCustomYml("serverinfo", "serverInfo.lastLoaded", Utils.getFormattedDate("MMM-dd-yyyy HH:mm:ss z"));
        DataUtils.setCustomYml("serverinfo", "serverInfo.lastLoadedUnix", Utils.getUnix());
    }

    public static void setConfigurationOption(String optionName, String type, Object defaultValue, List<Object> acceptedValues, String description) {
        if(DataUtils.getCustomYmlFile("config").exists()) {
            if(DataUtils.getCustomYmlString("config", "config." + optionName) == null) {
                DataUtils.setCustomYml("config", "config." + optionName, defaultValue);
            }
            configType.put(optionName, type);
            configDefaultValue.put(optionName, defaultValue.toString());
            configDescription.put(optionName, description);
            configAcceptedValues.put(optionName, acceptedValues);
        }

    }

    public static Main getPlugin() {
        return plugin;
    }

    public static String getPrefix() {
        if(Main.getPlugin().getConfig().getString("config.prefix") == null) {
            return Utils.chat("&9&l[&b&lFH&9&l] &r");
        }
        return Utils.chat(Main.getPlugin().getConfig().getString("config.prefix"));
    }

    public static List<String> getBetterStackTrace(StackTraceElement[] element) {
        ArrayList<String> newStackTrace = new ArrayList<>();
        for(StackTraceElement e : element) {
            newStackTrace.add(e.toString());
        }
        return newStackTrace;
    }

    public static void printBetterStackTrace(Exception e) {
        Bukkit.getLogger().severe("Stack Trace:");
        Bukkit.getLogger().severe("    " + e.toString());
        for(StackTraceElement element : e.getStackTrace()) {
            Bukkit.getLogger().severe("        " + element.toString());
        }
    }

    private void failedPlugin(Exception e) {
        errored = true;

        if(Bukkit.getOnlinePlayers().size() >= 1) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                if(p.hasPermission("formathelper.*") || p.hasPermission("formathelper.reloadconfig") || p.hasPermission("formathelper.modifyconfig")) {
                    p.sendMessage(Utils.chat("&9&l[&b&lFH&9&l] &4The plugin failed to load! &4&oView console or &7&o/formathelper&4&o for more information!"));
                    p.playSound(p.getLocation(), Sound.ENTITY_WITHER_BREAK_BLOCK, 2F, 1F);
                }
            }
        }

        Bukkit.getLogger().severe(" ");
        Bukkit.getLogger().severe("Something went wrong whilst trying to load the plugin 'FormatHelper'");
        Bukkit.getLogger().severe("This usually is not your fault, but before reporting this please try deleting your config.yml and then restarting your server! If that does not fix this issue, please follow the steps below.");
        Bukkit.getLogger().severe(" ");
        Bukkit.getLogger().severe(Utils.chat("Failed to load FormatHelper, report this on github: &6" + e.toString()));
        Bukkit.getLogger().severe("The GitHub issues tracker can be found here: https://github.com/CyberedCake/FormatHelper/issues");
        Bukkit.getLogger().severe(Utils.chat("&c&n>>> Be sure to include the log file, your config.yml, serverinfo.yml, errors.txt, and jar file in your issue reports! <<<&r"));
        Bukkit.getLogger().severe(" ");
        Bukkit.getLogger().severe("Stack Trace:");
        for(String str : getBetterStackTrace(e.getStackTrace())) {
            try {
                Bukkit.getLogger().severe("        " + str);
            } catch (Exception ex) {
                Bukkit.getLogger().severe("        An error occurred with the stack trace element");
            }
        }
        Bukkit.getLogger().severe(" ");
        boolean errorsCreated = false;
        File errors = new File("errors.txt");
        try {
            if (errors.createNewFile()) {
                errorsCreated = true;
            } else {
                if(errors.exists()) {
                    errors.delete();
                    errors.createNewFile();
                    errorsCreated = true;
                }
            }
            if(errorsCreated) {
                FileWriter writer = new FileWriter(Main.getPlugin().getDataFolder() + "/" + "errors.txt");
                writer.write("\nERROR (on " + Utils.getFormattedDate("MMM-dd-yyyy HH:mm:ss z") + ")");
                writer.write("\n    " + e.toString());
                for (StackTraceElement element : e.getStackTrace()) {
                    writer.write("\n        " + element.toString());
                }
                writer.close();
                Bukkit.getLogger().severe("Stack trace has also been successfully logged to errors.txt");
                Bukkit.getLogger().severe(" ");
            }
        } catch (IOException ignored) {
        }

        ArrayList<String> detectedPlugins = new ArrayList<>();
        if(Bukkit.getPluginManager().getPlugins() != null) {
            for(Plugin pl : Bukkit.getPluginManager().getPlugins()) {
                detectedPlugins.add(pl.getDescription().getName());
            }
        }

        Bukkit.getLogger().severe("FormatHelper version (per Jar File): " + version);
        Bukkit.getLogger().severe("FormatHelper version (per plugin): " + getDescription().getVersion());
        Bukkit.getLogger().severe("FormatHelper config version (per Jar File): " + configVersion);
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
