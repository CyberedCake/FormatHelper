package net.cybercake.helper.Commands.SubCommands;

import net.cybercake.helper.Commands.CommandManager;
import net.cybercake.helper.Commands.SubCommand;
import net.cybercake.helper.Main;
import net.cybercake.helper.Utils.Utils;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Help extends SubCommand {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Prints this help message";
    }

    @Override
    public String getUsage() {
        return "/formathelper help";
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"?", "info"};
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        // Moved to the CommandManager class
        sender.sendMessage(Main.getPrefix() + Utils.chat("&cUh-oh! Something went wrong while trying to send you the help message. Try again later or report this on the github."));
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        return CommandManager.emptyList;
    }
}
