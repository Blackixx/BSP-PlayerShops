package org.black_ixx.bossshop.addon.playershops.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.black_ixx.bossshop.addon.playershops.CustomActions;
import org.black_ixx.bossshop.addon.playershops.PlayerShops;
import org.black_ixx.bossshop.addon.playershops.managers.SaveManager.REASON_LOAD;
import org.black_ixx.bossshop.addon.playershops.managers.SaveManager.REASON_SAVE;
import org.black_ixx.bossshop.addon.playershops.objects.PlayerShop;
import org.black_ixx.bossshop.api.BSAddonConfig;
import org.black_ixx.bossshop.api.BSAddonStorage;
import org.black_ixx.bossshop.core.BSBuy;
import org.black_ixx.bossshop.core.BSCustomLink;
import org.black_ixx.bossshop.core.BSShop;
import org.black_ixx.bossshop.core.BSShops;
import org.black_ixx.bossshop.core.conditions.BSCondition;
import org.black_ixx.bossshop.core.conditions.BSConditionSet;
import org.black_ixx.bossshop.core.conditions.BSConditionType;
import org.black_ixx.bossshop.core.conditions.BSSingleCondition;
import org.black_ixx.bossshop.core.prices.BSPriceType;
import org.black_ixx.bossshop.core.rewards.BSRewardType;
import org.black_ixx.bossshop.managers.ClassManager;
import org.black_ixx.bossshop.managers.features.PageLayoutHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class PlayerShopsManager {


    private BSShop shoplist;
    private PageLayoutHandler shoplist_layout, shop_layout, shopedit_layout, iconselection_layout;

    private Map<UUID, PlayerShop> playershops;
    private List<String> renting;
    private BSAddonStorage storage;
    private int rank_max;


    public void init(PlayerShops plugin) {
        storage = new BSAddonConfig(plugin, "storage");
        renting = storage.getStringList("Renting");

        BSShops shophandler = ClassManager.manager.getShops();
        setupLayouts(plugin);

        //Shopslist
        shoplist = plugin.getShopCreator().createShopList(plugin, shophandler);

        //add player shops
        playershops = new HashMap<UUID, PlayerShop>(); //TODO: LOAD
        File shopfolder = new File(plugin.getBossShop().getDataFolder() + File.separator + "addons" + File.separator + plugin.getAddonName() + File.separator + "shops");
        if (shopfolder.exists()) {
            loadFile(plugin, shopfolder);
        }


        shoplist.finishedAddingItems();
        plugin.getShopsManager().updateShopListing();
    }

    private void loadFile(PlayerShops plugin, File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                loadFile(plugin, f);
            }
        } else {
            UUID uuid = UUID.fromString(file.getName().replace(".yml", ""));
            if (!plugin.getSettings().getListOnlinePlayersOnly()) {
                plugin.getSaveManager().loadShop(uuid, null, REASON_LOAD.SERVER_START, false, false);
            } else if (Bukkit.getPlayer(uuid) != null) {
                plugin.getSaveManager().loadShop(uuid, Bukkit.getPlayer(uuid), REASON_LOAD.SERVER_START_OWNER_ONLINE, false, false);
            } else if (renting.contains(uuid.toString())) {
                PlayerShop shop = plugin.getSaveManager().loadShop(uuid, null, REASON_LOAD.SERVER_START_RENTING, false, false);
                if (shop.getRentTimeLeft(false, true) <= 0) { //Renting time is out
                    shop.unload();
                    renting.remove(uuid.toString());
                }
            }
        }
    }

    public void save(PlayerShops plugin, REASON_SAVE reason) {
        if (playershops != null) {
            renting.clear();
            for (PlayerShop shop : playershops.values()) {
                plugin.getSaveManager().saveShop(shop, null, reason);
                if (shop.getRentTimeLeft(false, true) > 0) {
                    renting.add(shop.getOwner().toString());
                }
            }
            storage.set("Renting", renting);
            storage.save();
        }

        if (reason == REASON_SAVE.SERVER_RELOAD || reason == REASON_SAVE.SERVER_UNLOAD) {
            playershops.clear();
        }

    }


    public void addPlayerShop(PlayerShop shop) {
        playershops.put(shop.getOwner(), shop);
    }

    public void removePlayerShop(UUID uuid) {
        PlayerShop shop = getPlayerShop(uuid);
        if (shop != null) {
            shop.setRank(-1);
        }
        playershops.remove(uuid);
    }

    public PlayerShop getPlayerShop(UUID owner) {
        return playershops.get(owner);
    }

    public PlayerShop getPlayerShop(String owner_start) {
        synchronized (playershops) {
            for (PlayerShop s : playershops.values()) {
                if (s.getOwnerName().toLowerCase().startsWith(owner_start.toLowerCase())) {
                    return s;
                }
            }
        }
        return null;
    }

    public PlayerShop getPlayerShop(BSShop shop, boolean include_shopedits) {
        if (shop != null) {
            synchronized (playershops) {
                for (PlayerShop s : playershops.values()) {
                    if (s.getShop() == shop) {
                        return s;
                    } else if (s.getShopEdit() == shop && include_shopedits) {
                        return s;
                    }
                }
            }
        }
        return null;
    }

    public int getRankMax() {
        return rank_max;
    }

    public void openShoplist(Player p) {
        shoplist.openInventory(p);
    }

    public int getRentingPlayersAmount() {
        int count = 0;
        for (PlayerShop shop : playershops.values()) {
            if (shop.getRentTimeLeft(true, true) > 0) {
                count++;
            }
        }
        return count;
    }


    public void updateShopListing() {
        synchronized (shoplist.getItems()) {
            shoplist.getItems().clear();

            List<PlayerShop> to_add = new ArrayList<PlayerShop>();
            synchronized (playershops.values()) {
                for (PlayerShop shop : playershops.values()) {
                    if (shop.getShop() != null && shop.containsVisibleItems()) {
                        to_add.add(shop);
                    } else {
                        shop.setRank(-1);
                    }
                }
            }

            int rank = 0;
            while (!to_add.isEmpty()) {
                rank++;
                PlayerShop current = to_add.get(0);
                for (PlayerShop shop : to_add) {
                    shop.updateInfo(null);
                    if (shop.hasHigherPriority(current)) {
                        current = shop;
                    }
                }
                to_add.remove(current);
                addToShopListing(current);
                current.setRank(rank);
            }
            this.rank_max = rank;
            shoplist.finishedAddingItems();
        }
    }

    @Deprecated
    public void addToShopListing(PlayerShop shop) {
        BSBuy buy = shop.createShoplink();
        shoplist.addShopItem(buy, buy.getItem(), ClassManager.manager);
        //TODO
    }


    public PageLayoutHandler getLayout(BSShop shop, PlayerShops plugin) {
        if (shop == shoplist) {
            return shoplist_layout;
        }

        if (shop == plugin.getIconManager().getIconSelectionShop()) {
            return iconselection_layout;
        }

        PlayerShop playershop = getPlayerShop(shop, true);
        if (playershop != null) {
            if (playershop.isBeingEdited()) {
                return shopedit_layout;
            } else {
                return shop_layout;
            }
        }


        return null;
    }


    public void setupLayouts(PlayerShops plugin) {
        BSBuy arrowleft = plugin.getItems().getArrowLeft().createShopItem(BSRewardType.ShopPage, BSPriceType.Nothing, "previous", null, 45, null, new BSSingleCondition(BSConditionType.SHOPPAGE, "over", "1"));
        BSBuy arrowright = plugin.getItems().getArrowRight().createShopItem(BSRewardType.ShopPage, BSPriceType.Nothing, "next", null, 53, null, new BSSingleCondition(BSConditionType.SHOPPAGE, "under", "%maxpage%"));
        BSBuy close = plugin.getItems().getClose().createShopItem(BSRewardType.Close, BSPriceType.Nothing, null, null, 50, null);

        //Shopslist
        {
            List<BSBuy> items = new ArrayList<BSBuy>();
            addItem(items, arrowleft);
            addItem(items, arrowright);
            addItem(items, close);
            addItem(items, plugin.getItems().getOwnShop().createShopItem(BSRewardType.Custom, BSPriceType.Nothing, new BSCustomLink(CustomActions.ACTION_OPEN_SHOP, plugin.getActions()), null, 48, null, new BSSingleCondition(plugin.getBossShopListener().getPlayerShopCondition(), "ownany", "true")));
            addItem(items, plugin.getItems().getCreateShop().createShopItem(BSRewardType.Custom, plugin.getSettings().getPriceType(), new BSCustomLink(CustomActions.ACTION_CREATE_SHOP, plugin.getActions()), plugin.getSettings().getShopCreationPrice(), 48, plugin.getSettings().getPermission("PlayerShops.create"), new BSSingleCondition(plugin.getBossShopListener().getPlayerShopCondition(), "ownany", "false")));
            shoplist_layout = new PageLayoutHandler(items, 46, false);
        }

        //Shop
        {
            List<BSBuy> items = new ArrayList<BSBuy>();
            addItem(items, arrowleft);
            addItem(items, arrowright);
            addItem(items, plugin.getItems().getShopInfo().createShopItem(BSRewardType.Custom, BSPriceType.Nothing, new BSCustomLink(CustomActions.ACTION_SHOP_INFO, plugin.getActions()), null, 47, null, new BSSingleCondition(plugin.getBossShopListener().getPlayerShopCondition(), "own", "true")));

            if (plugin.getSettings().getRentAllowStacking()) {
                addItem(items, plugin.getItems().getRentingIncrease().createShopItem(BSRewardType.Custom, plugin.getSettings().getPriceType(), new BSCustomLink(CustomActions.ACTION_RENT_INCREASE, plugin.getActions()), plugin.getSettings().getRentPrice(), 48, plugin.getSettings().getPermission("PlayerShops.rent"), new BSSingleCondition(plugin.getBossShopListener().getPlayerShopCondition(), "renting", "true")));
            }

            List<BSCondition> conditions_rentlimit = new ArrayList<BSCondition>();
            conditions_rentlimit.add(new BSSingleCondition(plugin.getBossShopListener().getPlayerShopCondition(), "renting", "false"));
            conditions_rentlimit.add(new BSSingleCondition(plugin.getBossShopListener().getPlayerShopCondition(), "canrent", "false"));
            conditions_rentlimit.add(new BSSingleCondition(plugin.getBossShopListener().getPlayerShopCondition(), "own", "true"));
            addItem(items, plugin.getItems().getRentingLimitReached().createShopItem(BSRewardType.Nothing, BSPriceType.Nothing, null, null, 48, null, new BSConditionSet(conditions_rentlimit)));

            List<BSCondition> conditions_rentfirst = new ArrayList<BSCondition>();
            conditions_rentfirst.add(new BSSingleCondition(plugin.getBossShopListener().getPlayerShopCondition(), "renting", "false"));
            conditions_rentfirst.add(new BSSingleCondition(plugin.getBossShopListener().getPlayerShopCondition(), "own", "true"));
            addItem(items, plugin.getItems().getRenting().createShopItem(BSRewardType.Custom, plugin.getSettings().getPriceType(), new BSCustomLink(CustomActions.ACTION_RENT_FIRST, plugin.getActions()), plugin.getSettings().getRentPrice(), 48, plugin.getSettings().getPermission("PlayerShops.rent"), new BSConditionSet(conditions_rentfirst)));
            addItem(items, plugin.getItems().getEditShop().createShopItem(BSRewardType.Custom, BSPriceType.Nothing, new BSCustomLink(CustomActions.ACTION_EDIT_SHOP, plugin.getActions()), null, 49, null, new BSSingleCondition(plugin.getBossShopListener().getPlayerShopCondition(), "own", "true")));
            addItem(items, plugin.getItems().getBack().createShopItem(BSRewardType.Shop, BSPriceType.Nothing, "playershops_list", null, 50, null));
            shop_layout = new PageLayoutHandler(items, 46, false);
        }

        //Shopedit
        {
            List<BSBuy> items = new ArrayList<BSBuy>();
            addItem(items, arrowleft);
            addItem(items, arrowright);
            addItem(items, plugin.getItems().getShopInfo().createShopItem(BSRewardType.Custom, BSPriceType.Nothing, new BSCustomLink(CustomActions.ACTION_SHOP_INFO, plugin.getActions()), null, 47, null));
            if (plugin.getSettings().getSlotsEnabled()) {
                addItem(items, plugin.getItems().getBuySlot().createShopItem(BSRewardType.Custom, BSPriceType.Nothing, new BSCustomLink(CustomActions.ACTION_SLOT_BUY, plugin.getActions()), null, 48, plugin.getSettings().getPermission("PlayerShops.buyslot"), new BSSingleCondition(plugin.getBossShopListener().getPlayerShopSlotsCondition(), "under", String.valueOf((plugin.getSettings().getSlotsLimit() - plugin.getSettings().getSlotsAmount() + 1)))));
            }
            addItem(items, plugin.getItems().getSaveShop().createShopItem(BSRewardType.Custom, BSPriceType.Nothing, new BSCustomLink(CustomActions.ACTION_SAVE_SHOP, plugin.getActions()), null, 49, null));
            addItem(items, plugin.getItems().getBack().createShopItem(BSRewardType.Shop, BSPriceType.Nothing, "playershops_list", null, 50, null)); //Shops will stay in edit mode until either player saves or server restarts
            if (plugin.getIconManager().getAllowIconSelection()) {
                addItem(items, plugin.getItems().getSelectIcon().createShopItem(BSRewardType.Custom, BSPriceType.Nothing, new BSCustomLink(CustomActions.ACTION_SELECT_ICON, plugin.getActions()), plugin.getSettings().getPermission("PlayerShops.selecticon"), 51, null));
            }
            addItem(items, plugin.getItems().getShopRename().createShopItem(BSRewardType.Custom, BSPriceType.Nothing, new BSCustomLink(CustomActions.ACTION_RENAME_SHOP, plugin.getActions()), null, plugin.getIconManager().getAllowIconSelection() ? 52 : 51, null, new BSSingleCondition(plugin.getBossShopListener().getPlayerShopCondition(), "allowshoprename", "true")));
            shopedit_layout = new PageLayoutHandler(items, 46, false);
        }

        //Iconselection
        {
            List<BSBuy> items = new ArrayList<BSBuy>();
            addItem(items, arrowleft);
            addItem(items, arrowright);
            addItem(items, plugin.getItems().getSelectInventoryItemAllow().createShopItem(BSRewardType.Nothing, BSPriceType.Nothing, null, null, 48, null, new BSSingleCondition(plugin.getBossShopListener().getPlayerShopCondition(), "allowinventoryitem", "true")));
            addItem(items, plugin.getItems().getSelectInventoryItemDeny().createShopItem(BSRewardType.Nothing, BSPriceType.Nothing, null, null, 48, null, new BSSingleCondition(plugin.getBossShopListener().getPlayerShopCondition(), "allowinventoryitem", "false")));
            addItem(items, plugin.getItems().getBack().createShopItem(BSRewardType.Custom, BSPriceType.Nothing, new BSCustomLink(CustomActions.ACTION_OPEN_SHOP, plugin.getActions()), null, 50, null));
            iconselection_layout = new PageLayoutHandler(items, 46, false);
        }
    }


    private void addItem(List<BSBuy> items, BSBuy buy) {
        if (buy != null) {
            items.add(buy);
        }
    }


}
