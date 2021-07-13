package net.cybercake.helper.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MenuUtils implements CommandExecutor, Listener, TabCompleter {

    public static void setInvSlot(Inventory inventory, int slot, Material material, int amount, String name, List<String> lore) {
        try {
            ItemStack item = new ItemStack(material, amount, (byte)0);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            List<String> Lore = new ArrayList<String>();
            for(String loreActual : lore) {
                Lore.add(ChatColor.translateAlternateColorCodes('&', loreActual));
            }
            meta.setLore(Lore);
            item.setItemMeta(meta);
            inventory.setItem(slot, item);
        } catch (Exception e) {
            System.out.println("An error occurred whilst trying to set the inventory item. Stack trace below:");
            e.printStackTrace();
        }
    }

    public static void setShinyInvSlot(Inventory inventory, int slot, Material material, int amount, String name, List<String> lore) {
        try {
            ItemStack item = new ItemStack(material, amount, (byte)1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            List<String> Lore = new ArrayList<String>();
            for(String loreActual : lore) {
                Lore.add(ChatColor.translateAlternateColorCodes('&', loreActual));
            }
            meta.setLore(Lore);
            item.setItemMeta(meta);
            inventory.setItem(slot, item);
        } catch (Exception e) {
            System.out.println("An error occurred whilst trying to set the inventory item. Stack trace below:");
            e.printStackTrace();
        }
    }

    public static void setInvSlotHead(Inventory inventory, int slot, String owner, int amount, String name, List<String> lore) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, amount, (byte)1);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        Player ownerFinish = Bukkit.getPlayerExact(owner);
        OfflinePlayer ownerOffline = Bukkit.getOfflinePlayer(ownerFinish.getUniqueId());
        meta.setOwningPlayer(ownerOffline);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        List<String> Lore = new ArrayList<String>();
        for(String loreActual : lore) {
            Lore.add(ChatColor.translateAlternateColorCodes('&', loreActual));
        }
        meta.setLore(Lore);
        item.setItemMeta(meta);
        inventory.setItem(slot, item);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(args.length == 0) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid usage! &7/guislots <number of " +
                        "rows>"));
            }else if(args.length > 1) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid usage! &7/guislots <number of " +
                        "rows>"));
            }else if(args.length == 1) {
                try {
                    if(Integer.parseInt(args[0]) > 6) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can only set the gui rows to &b1" +
                                " - 6"));
                    }else if(Integer.parseInt(args[0]) < 1) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can only set the gui rows to &b1" +
                                " - 6"));
                    }else {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aOpening a GUI with " + args[0] +
                                " &arows!"));
                        Inventory guislots = Bukkit.createInventory(p, 9 * Integer.parseInt(args[0]), "GUI Slots (" + Integer.parseInt(args[0]) + " Rows)");

                        for(int i = 0; i < Integer.parseInt(args[0])*9; ++i) {
                            ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                            ItemMeta meta = item.getItemMeta();
                            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&8Slot >> " + i));
                            item.setItemMeta(meta);
                            guislots.setItem(i, item);
                        }

                        p.openInventory(guislots);
                    };
                } catch (Exception e) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cAn error occurred whilst trying to load the GUI slots menu."));
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThe error could be caused by something you typed into the arguments!"));
                }
            }
        }else{
            sender.sendMessage("Only players can execute this command!");
        }



        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        ArrayList<String> emptyTabCompleter = new ArrayList<>();
        emptyTabCompleter.add("");
        if(args.length <= 1) {
            ArrayList<String> tabCompleters = new ArrayList<>();
            tabCompleters.add("1");
            tabCompleters.add("2");
            tabCompleters.add("3");
            tabCompleters.add("4");
            tabCompleters.add("5");
            tabCompleters.add("6");

            return tabCompleters;
        }

        return emptyTabCompleter;
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent e) {
        if(e.getView().getTitle().contains("GUI Slots")) { e.setCancelled(true); }
    }

}
