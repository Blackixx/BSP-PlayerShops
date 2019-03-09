package org.black_ixx.bossshop.addon.playershops.listener;

import org.black_ixx.bossshop.addon.playershops.PlayerShops;
import org.black_ixx.bossshop.managers.ClassManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;


public class SignListener implements Listener {

    public SignListener(PlayerShops plugin) {
        this.plugin = plugin;
    }

    private PlayerShops plugin;


    @EventHandler
    public void createSign(SignChangeEvent e) {
        if (!plugin.getSettings().getSignsEnabled() || plugin.getShopsManager() == null) {
            return;
        }

        String text = e.getLine(0).toLowerCase();

        boolean playershop = text.endsWith(plugin.getSettings().getSignsTextPlayerShop().toLowerCase());
        boolean shoplisting = text.endsWith(plugin.getSettings().getSignsTextShopListing().toLowerCase());
        if (playershop || shoplisting) {
            if (plugin.getSettings().getPermissionsEnabled()) {
                String line2 = ChatColor.stripColor(e.getLine(1));
                Player p = e.getPlayer();
                if (!p.hasPermission("PlayerShops.createSign")
                        || (playershop & !line2.equalsIgnoreCase(p.getName()) & !p.hasPermission("PlayerShops.createSign.other"))) {
                    ClassManager.manager.getMessageHandler().sendMessage("Main.NoPermission", e.getPlayer());
                    e.setCancelled(true);
                    return;
                }
            }

            if (e.getLine(0) != "") {
                e.setLine(0, ClassManager.manager.getStringManager().transform(e.getLine(0)));
            }
            if (e.getLine(1) != "") {
                e.setLine(1, ClassManager.manager.getStringManager().transform(e.getLine(1)));
            }
            if (e.getLine(2) != "") {
                e.setLine(2, ClassManager.manager.getStringManager().transform(e.getLine(2)));
            }
            if (e.getLine(3) != "") {
                e.setLine(3, ClassManager.manager.getStringManager().transform(e.getLine(3)));
            }
        }

    }


    @EventHandler
    public void interactSign(PlayerInteractEvent e) {
        if (plugin.getSettings() == null || plugin.getShopsManager() == null) {
            return;
        }
        if (!plugin.getSettings().getSignsEnabled()) {
            return;
        }

        if (e.getClickedBlock() != null) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {

                Block b = e.getClickedBlock();
                if (b.getType() == Material.SIGN || b.getType() == Material.WALL_SIGN) {

                    if (b.getState() instanceof Sign) {
                        Sign s = (Sign) b.getState();

                        String text = s.getLine(0).toLowerCase();
                        if (text.endsWith(plugin.getSettings().getSignsTextPlayerShop().toLowerCase())) {
                            plugin.getCommandManager().tryOpenShop(e.getPlayer(), ChatColor.stripColor(s.getLine(1)), true);
                        } else if (text.endsWith(plugin.getSettings().getSignsTextShopListing().toLowerCase())) {
                            plugin.getShopsManager().openShoplist(e.getPlayer());
                        }

                    }
                }
            }
        }
    }


}
