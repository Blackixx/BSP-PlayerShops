package org.black_ixx.bossshop.addon.playershops;

import org.black_ixx.bossshop.addon.playershops.objects.PlayerShop;
import org.black_ixx.bossshop.addon.playershops.objects.PlayerShopsUserInputRename;
import org.black_ixx.bossshop.core.BSCustomActions;
import org.black_ixx.bossshop.core.BSShopHolder;
import org.black_ixx.bossshop.managers.ClassManager;
import org.black_ixx.bossshop.misc.CurrencyTools;
import org.black_ixx.bossshop.misc.CurrencyTools.BSCurrency;
import org.bukkit.entity.Player;

public class CustomActions implements BSCustomActions {

    public final static int ACTION_CREATE_SHOP = 0;
    public final static int ACTION_OPEN_SHOP = 1;
    public final static int ACTION_EDIT_SHOP = 2;
    public final static int ACTION_SAVE_SHOP = 3;
    public final static int ACTION_SHOP_INFO = 4;
    public final static int ACTION_RENT_FIRST = 5;
    public final static int ACTION_RENT_INCREASE = 6;
    public final static int ACTION_SLOT_BUY = 7;
    public final static int ACTION_SELECT_ICON = 8;
    public final static int ACTION_RENAME_SHOP = 9;


    private PlayerShops plugin;

    public CustomActions(PlayerShops plugin) {
        this.plugin = plugin;
    }

    @Override
    public void customAction(Player p, int id) {
        PlayerShop shop;


        switch (id) {//TODO: info messages
            case ACTION_CREATE_SHOP:
                shop = new PlayerShop(plugin, p);
                plugin.getShopsManager().addPlayerShop(shop);
                shop.createShop();
                plugin.getShopsManager().updateShopListing();
                break;

            case ACTION_OPEN_SHOP:
                shop = plugin.getShopsManager().getPlayerShop(p.getUniqueId());
                if (shop != null) {
                    if (shop.getShop() != null) {
                        shop.getShop().openInventory(p);
                    } else if (shop.getShopEdit() != null) {
                        shop.getShopEdit().openInventory(p);
                    }
                }
                break;

            case ACTION_EDIT_SHOP:
                shop = plugin.getShopsManager().getPlayerShop(p.getUniqueId());
                if (shop != null) {
                    shop.tryEdit(p, true);
                }
                break;

            case ACTION_SAVE_SHOP:
                shop = plugin.getShopsManager().getPlayerShop(p.getUniqueId());
                if (shop != null) {
                    boolean shop_opened = false;
                    if (ClassManager.manager.getPlugin().getAPI().isValidShop(p.getOpenInventory())) {
                        BSShopHolder holder = ((BSShopHolder) p.getOpenInventory().getTopInventory().getHolder());
                        if (holder.getShop().equals(shop.getShopEdit())) {
                            shop_opened = true;
                        }
                    }
                    shop.finishEdit(true);
                    if (shop_opened) { //if shop was opened re-open new shop
                        shop.getShop().openInventory(p);
                    }
                }
                break;

            case ACTION_RENT_FIRST:
                shop = plugin.getShopsManager().getPlayerShop(p.getUniqueId());
                if (shop != null) {
                    if (plugin.getSettings().getRentPlayerLimit() != -1 && plugin.getSettings().getRentPlayerLimit() <= plugin.getShopsManager().getRentingPlayersAmount()) {
                        plugin.getMessages().sendMessage("Message.RentingLimitReached", p, p);
                        break;
                    }
                    shop.payRent();
                    plugin.getMessages().sendMessage("Message.IncreasedRent", p, p);
                }
                break;

            case ACTION_RENT_INCREASE:
                shop = plugin.getShopsManager().getPlayerShop(p.getUniqueId());
                if (shop != null) {
                    shop.payRent();
                    plugin.getMessages().sendMessage("Message.IncreasedRent", p, p);
                }
                break;

            case ACTION_SHOP_INFO:
                shop = plugin.getShopsManager().getPlayerShop(p.getUniqueId());
                if (shop != null) {
                    shop.giveReward(p);
                }
                break;

            case ACTION_RENAME_SHOP:
                shop = plugin.getShopsManager().getPlayerShop(p.getUniqueId());
                if (shop != null) {
                    String text = plugin.getMessages().get("Message.EnterShopName");
                    new PlayerShopsUserInputRename(shop, p).getUserInput(p, text, null, text);
                }
                break;

            case ACTION_SLOT_BUY:
                shop = plugin.getShopsManager().getPlayerShop(p.getUniqueId());
                BSCurrency currency = BSCurrency.detectCurrency(plugin.getSettings().getPriceType().name());
                double price = plugin.getSettings().getSlotsPriceReal(p, plugin);
                if (shop != null) {
                    if (CurrencyTools.hasValue(p, currency, price, true)) {
                        CurrencyTools.takePrice(p, currency, price);
                        shop.increaseSlots(p);
                        plugin.getMessages().sendMessage("Message.IncreasedSlots", p, p);
                    }
                }
                break;

            case ACTION_SELECT_ICON:
                if (plugin.getIconManager().getIconSelectionShop() != null) {
                    plugin.getIconManager().getIconSelectionShop().openInventory(p);
                }
                break;

        }

    }

}
