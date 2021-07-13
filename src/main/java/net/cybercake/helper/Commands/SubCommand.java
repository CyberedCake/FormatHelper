package net.cybercake.helper.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

// Command Manager from https://gitlab.com/kodysimpson/command-manager-spigot/

public abstract class SubCommand {

    public abstract String getName();

    public abstract String getPermission();

    public abstract String getDescription();

    public abstract String getUsage();

    public abstract String[] getAliases();

    public abstract void perform(CommandSender sender, String args[]);

    public abstract List<String> tab(CommandSender sender, String args[]);

}