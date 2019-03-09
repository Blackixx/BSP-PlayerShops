package org.black_ixx.bossshop.addon.playershops.managers;


import org.black_ixx.bossshop.addon.playershops.PlayerShops;
import org.black_ixx.bossshop.addon.playershops.objects.PlayerShop;
import org.black_ixx.bossshop.managers.ClassManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandManager implements CommandExecutor {

    private PlayerShops plugin;


    public CommandManager(PlayerShops plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;


            if (plugin.getShopsManager() != null) {
                if (p.hasPermission("PlayerShops.open") | !plugin.getSettings().getPermissionsEnabled()) {


                    if (args.length == 0) {
                        plugin.getShopsManager().openShoplist(p);
                    } else {
                        String target = args[0];
                        return tryOpenShop(p, target, true);
                    }


                    return true;
                } else {
                    ClassManager.manager.getMessageHandler().sendMessage("Main.NoPermission", p);
                    return false;
                }
            }


            return false;
        }

        sender.sendMessage(ChatColor.RED + "This command can not be used by the console.");
        return false;
    }


    public boolean tryOpenShop(Player p, String target, boolean fail_message) {
        if (plugin.getShopsManager() != null) {
            Player t = Bukkit.getPlayer(target);
            PlayerShop shop;

            if (t != null) {
                shop = plugin.getShopsManager().getPlayerShop(t.getUniqueId());
            } else {
                shop = plugin.getShopsManager().getPlayerShop(target);
            }

            if (shop == null) {
                plugin.getMessages().sendMessage("Message.ShopNotFound", p, target);
                return false;
            } else {
                if (shop.getShop() != null && (!shop.isBeingEdited() || shop.getOwner().equals(p.getUniqueId()))) {
                    shop.getShop().openInventory(p);
                    return true;
                } else {
                    plugin.getMessages().sendMessage("Message.ShopBeingEdited", p, target, t, shop.getShop(), null, null);
                    return false;
                }
            }
        }
        return false;
    }

}
