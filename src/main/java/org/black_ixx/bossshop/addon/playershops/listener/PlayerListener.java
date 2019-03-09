package org.black_ixx.bossshop.addon.playershops.listener;


import java.io.File;

import org.black_ixx.bossshop.addon.playershops.PlayerShops;
import org.black_ixx.bossshop.addon.playershops.managers.SaveManager.REASON_LOAD;
import org.black_ixx.bossshop.addon.playershops.objects.PlayerShop;
import org.black_ixx.bossshop.addon.playershops.objects.PlayerShopsUserInputPrice;
import org.black_ixx.bossshop.core.BSBuy;
import org.black_ixx.bossshop.core.BSShopHolder;
import org.black_ixx.bossshop.managers.ClassManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class PlayerListener implements Listener {

    private PlayerShops plugin;

    public PlayerListener(PlayerShops plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void joinServer(PlayerJoinEvent event) {
        join(event.getPlayer());
    }

    @EventHandler
    public void quitServer(PlayerQuitEvent event) {
        leave(event.getPlayer());
    }

    @EventHandler
    public void kickedOffServer(PlayerKickEvent event) {
        leave(event.getPlayer());
    }


    public void join(Player p) {
        if (plugin.getShopsManager() != null) {
            if (plugin.getSettings().getListOnlinePlayersOnly() && plugin.getShopsManager().getPlayerShop(p.getUniqueId()) == null) {
                File file = new File(plugin.getBossShop().getDataFolder() + File.separator + "addons" +
                        File.separator + plugin.getAddonName() + File.separator + "shops" + File.separator +
                        p.getUniqueId().toString().substring(0, 1) + File.separator + p.getUniqueId().toString() +
                        ".yml");
                if (file.exists()) {
                    plugin.getSaveManager().loadShop(p.getUniqueId(), p, REASON_LOAD.OWNER_JOIN, false,
                            true);
                }
            }

            if (plugin.getShopsManager() != null) {
                PlayerShop shop = plugin.getShopsManager().getPlayerShop(p.getUniqueId());
                if (shop != null) {
                    shop.ownerJoin(p);
                }
            }
        }

    }

    public void leave(Player p) {
        if (plugin.getShopsManager() != null) {
            PlayerShop shop = plugin.getShopsManager().getPlayerShop(p.getUniqueId());
            if (shop != null) {
                shop.ownerLeave(p);
            }
        }
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void inventoryClick(InventoryClickEvent event) {
        if (event.isCancelled() && plugin.getSettings().getPreventSellingPluginsItems()) {
            return;
        }
        if (plugin.getShopsManager() != null) {
            if (event.getWhoClicked() instanceof Player) {
                Player p = (Player) event.getWhoClicked();
                if (plugin.getBossShop().getAPI().isValidShop(event.getInventory())) {

                    BSShopHolder holder = (BSShopHolder) event.getInventory().getHolder();
                    BSBuy buy = holder.getShopItem(event.getRawSlot());
                    if (buy != null) {
                        return;
                    }

                    if (event.getCurrentItem() == null) {
                        return;
                    }
                    if (event.getCurrentItem().getType() == Material.AIR) {
                        return;
                    }

                    PlayerShop shop = plugin.getShopsManager().getPlayerShop(p.getUniqueId());
                    if (shop != null) {
                        if (shop.isBeingEdited() && shop.getShopEdit() == holder.getShop()) { //Player is in edit mode and clicked item of own inventory
                            event.setCancelled(true);
                            event.setResult(Result.DENY);
                            shopEditItemClick(p, shop, event.getCurrentItem(), event.getInventory(), event.getSlot(),
                                    ((BSShopHolder) event.getInventory().getHolder()));
                            return;

                        } else if (shop.isBeingEdited() && plugin.getIconManager().getIconSelectionShop() == holder.getShop()) { //Player is in edit mode and item selection menu and clicked item of own inventory
                            event.setCancelled(true);
                            event.setResult(Result.DENY);


                            if (plugin.getIconManager().getAllowInventoryItem(p, shop)) {
                                shopIconSelectionItemClick(p, shop, event.getCurrentItem(), holder);
                                return;

                            } else {
                                ClassManager.manager.getMessageHandler().sendMessage("Main.NoPermission", p,
                                        null, p, plugin.getIconManager().getIconSelectionShop(), holder, null);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }


    public void shopEditItemClick(Player p, PlayerShop shop, ItemStack item, Inventory i, int slot, BSShopHolder holder) {
        if (p.getGameMode() == GameMode.CREATIVE && plugin.getSettings().getPreventCreativeAccess()) {
            plugin.getMessages().sendMessage("Message.PreventedCreativeAddItem", p, null, p,
                    shop.getShopEdit(), holder, null);
            return;
        }

        if (plugin.getBlacklist().isBlocked(item)) {
            plugin.getMessages().sendMessage("Message.InvalidItem", p, null, p, shop.getShopEdit(),
                    holder, null);
            return;
        }

        if (shop.containsItem(item)) {
            shop.increaseItemAmount(item, item.getAmount());
            p.getInventory().setItem(slot, null);
            plugin.getBossShop().getAPI().updateInventory(p);
        } else {

            if (!shop.isEmptySlotLeft(p)) {
                plugin.getMessages().sendMessage("Message.OutOfSlots", p, null, p, shop.getShopEdit(),
                        holder, null);
                return;
            }

            new PlayerShopsUserInputPrice(shop, p, item, slot).getUserInput(p, plugin.getMessages().get("Message.EnterPrice"),
                    item, plugin.getMessages().get("Message.EnterPrice"));
        }

    }

    public void shopIconSelectionItemClick(Player p, PlayerShop shop, ItemStack item, BSShopHolder holder) {
        shop.setIcon(item.clone(), true, true, true);
        plugin.getMessages().sendMessage("Message.IconSelected", p, null, p, holder.getShop(), holder, null);
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void inventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            if (plugin.getShopsManager() != null) {
                Player p = (Player) event.getPlayer();
                if (plugin.getBossShop().getAPI().isValidShop(event.getInventory())) {
                    if (plugin.getShopsManager() != null) {
                        BSShopHolder holder = (BSShopHolder) event.getInventory().getHolder();
                        PlayerShop shop = plugin.getShopsManager().getPlayerShop(holder.getShop(), false);
                        if (shop != null) {
                            shop.playerLeave(p);
                        }
                    }
                }
            }
        }
    }


}