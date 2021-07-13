package net.cybercake.helper.Commands;

import net.cybercake.helper.Commands.SubCommands.Config;
import net.cybercake.helper.Commands.SubCommands.Help;
import net.cybercake.helper.Commands.SubCommands.Reload;
import net.cybercake.helper.Main;
import net.cybercake.helper.Utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// Command Manager from https://gitlab.com/kodysimpson/command-manager-spigot/

public class CommandManager implements CommandExecutor, TabCompleter {

    private ArrayList<SubCommand> subcommands = new ArrayList<>();
    public static ArrayList<String> emptyList = new ArrayList<>();

    public CommandManager(){
        subcommands.add(new Help());
        subcommands.add(new Reload());
        subcommands.add(new Config());
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(Main.errored) {
            TextComponent component = new TextComponent(Utils.chat("&9&l[&b&lFH&9&l] &c&nhttps://github.com/CyberedCake/FormatHelper/issues"));
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Utils.chat("&6Click here to open the issues tracker!")).create()));
            component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/CyberedCake/FormatHelper/issues"));

            sender.sendMessage(Utils.chat("&9&l[&b&lFH&9&l] &r&cThe plugin is disabled!"));
            sender.sendMessage(Utils.chat("&9&l[&b&lFH&9&l] &r&cThis happens when an error occurred during the loading process and forces the plugin to disable to avoid corruption."));
            sender.sendMessage(Utils.chat("&9&l[&b&lFH&9&l] &r&cRestart your server and check console, and please report that stack trace, your logs, your config.yml, serverinfo.yml, errors.txt, and the jar file to the issue tracker!"));
            sender.sendMessage(component);
        }else{
            if(getSubCommandsOnlyWithPerms(sender).size() <= 1) {
                sender.sendMessage(Utils.chat(Main.getPlugin().getConfig().getString("config.noPermissionMsg")));
            }else if(args.length == 0) {
                printHelpMsg(sender);
            }else if(args.length > 0) {
                boolean ran = false;
                if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("info")) {
                    printHelpMsg(sender);
                }else{
                    for (SubCommand cmd : getSubcommands()) {
                        boolean use = false;
                        if(args[0].equalsIgnoreCase(cmd.getName())) {
                            use = true;
                        }
                        if(!use) {
                            for(String alias : cmd.getAliases()) {
                                if(args[0].equalsIgnoreCase(alias)) {
                                    use = true;
                                }
                            }
                        }
                        if (use) {
                            if(sender.hasPermission("formathelper.*")) {
                                cmd.perform(sender, args);
                            }else if (cmd.getPermission().equalsIgnoreCase("")) {
                                cmd.perform(sender, args);
                            } else if (!cmd.getPermission().equalsIgnoreCase("") && sender.hasPermission(cmd.getPermission())) {
                                cmd.perform(sender, args);
                            } else {
                                sender.sendMessage(Utils.chat(Main.getPlugin().getConfig().getString("config.noPermissionMsg")));
                            }
                            ran = true;
                        }
                    }
                    if(!ran) {
                        sender.sendMessage(Utils.chat("&cUnknown sub-command: &8" + args[0]));
                    }
                }
            }
        }


        return true;
    }

    public void printHelpMsg(CommandSender sender) {
        if(sender instanceof Player) {
            sender.sendMessage(Utils.getSeperator(ChatColor.BLUE));
        }
        Utils.sendCenteredMessage(sender, "&d&lFORMAT HELPER COMMANDS:");
        for(SubCommand cmd : getSubcommands()) {
            if(sender.hasPermission("formathelper.*")) {
                printHelpMsgSpecific(sender, cmd.getDescription(), cmd.getUsage(), cmd.getPermission());
            }else if (cmd.getPermission().equalsIgnoreCase("")) {
                printHelpMsgSpecific(sender, cmd.getDescription(), cmd.getUsage(), cmd.getPermission());
            } else if (!cmd.getPermission().equalsIgnoreCase("") && sender.hasPermission(cmd.getPermission())) {
                printHelpMsgSpecific(sender, cmd.getDescription(), cmd.getUsage(), cmd.getPermission());
            }
        }
        if(sender instanceof Player) {
            sender.sendMessage(Utils.getSeperator(ChatColor.BLUE));
        }
    }

    private static void printHelpMsgSpecific(CommandSender sender, String description, String usage, String permission) {
        if(permission.equalsIgnoreCase("")) {
            permission = "Everyone";
        }
        TextComponent component = new TextComponent(Utils.chat("&b" + usage));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, usage));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Utils.chat("&6Command: &f" + usage + "\n&6Description: &f" + description + "\n&6Permission: &f" + permission)).create()));
        sender.sendMessage(component);
    }

    public ArrayList<String> getSubCommandsOnlyWithPerms(CommandSender sender) {
        ArrayList<String> cmdNames = new ArrayList<>();
        for(SubCommand cmd : getSubcommands()) {
            if(sender.hasPermission("formathelper.*")) {
                cmdNames.add(cmd.getName());
            }else if(cmd.getPermission().equalsIgnoreCase("")) {
                cmdNames.add(cmd.getName());
            }else if(!cmd.getPermission().equalsIgnoreCase("") && sender.hasPermission(cmd.getPermission())) {
                cmdNames.add(cmd.getName());
            }
        }
        return cmdNames;
    }

    public ArrayList<SubCommand> getSubcommands(){
        return subcommands;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(!Main.errored) {
            if(getSubCommandsOnlyWithPerms(sender).size() <= 1) {
                return emptyList;
            }else if(args.length <= 1) {
                return createReturnList(getSubCommandsOnlyWithPerms(sender), args[0]);
            }else{
                try {
                    for(SubCommand cmd : getSubcommands()) {
                        for(int i = 1; i < 100; i++) {
                            boolean use = false;
                            if(args[0].equalsIgnoreCase(cmd.getName())) {
                                use = true;
                            }
                            if(!use) {
                                for(String cmdAlias : cmd.getAliases()) {
                                    if(args[0].equalsIgnoreCase(cmdAlias)) {
                                        use = true;
                                    }
                                }
                            }
                            if(use) {
                                if(args.length - 1 == i) {
                                    if(sender.hasPermission("formathelper.*")) {
                                        return createReturnList(cmd.tab(sender, args), args[i]);
                                    }else if(cmd.getPermission().equalsIgnoreCase("")) {
                                        return createReturnList(cmd.tab(sender, args), args[i]);
                                    }else if(!cmd.getPermission().equalsIgnoreCase("") && sender.hasPermission(cmd.getPermission())) {
                                        return createReturnList(cmd.tab(sender, args), args[i]);
                                    }else{
                                        return emptyList;
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    return emptyList;
                }
            }
            return emptyList;
        }
        return emptyList;
    }

    @NonNull
    private List<String> createReturnList(@NonNull List<String> list, @NonNull String string) {
        if (string.length() == 0) {
            return list;
        }

        String input = string.toLowerCase(Locale.ROOT);
        List<String> returnList = new ArrayList<>();

        for (String item : list) {
            if (item.toLowerCase(Locale.ROOT).contains(input)) {
                returnList.add(item);
            } else if (item.equalsIgnoreCase(input)) {
                return emptyList;
            }
        }

        return returnList;
    }
}
