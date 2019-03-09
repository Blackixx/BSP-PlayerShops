package org.black_ixx.bossshop.addon.playershops.objects;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.black_ixx.bossshop.addon.playershops.PlayerShops;
import org.black_ixx.bossshop.addon.playershops.managers.SaveManager.REASON_SAVE;
import org.black_ixx.bossshop.core.BSBuy;
import org.black_ixx.bossshop.core.BSShop;
import org.black_ixx.bossshop.core.prices.BSPriceType;
import org.black_ixx.bossshop.core.rewards.BSRewardType;
import org.black_ixx.bossshop.managers.ClassManager;
import org.black_ixx.bossshop.misc.CurrencyTools;
import org.black_ixx.bossshop.misc.Misc;
import org.black_ixx.bossshop.misc.CurrencyTools.BSCurrency;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class PlayerShop extends PlayerShopSimple {

    private BSShop shop, shopedit;
    private boolean editing;
    private long last_edit;
    private HashMap<UUID, Double> money_spent;
    private HashMap<UUID, Integer> items_bought;
    private int rank = -1;


    public PlayerShop(PlayerShops plugin, Player owner) {
        super(plugin, owner);
    }

    public PlayerShop(PlayerShops plugin, UUID owner) {
        super(plugin, owner);
    }

    @Override
    public void delete(File backupfolder) {
        super.delete(backupfolder);
        if (shop != null) {
            ClassManager.manager.getShops().unloadShop(shop);
            shop = null;
        }
        if (shopedit != null) {
            ClassManager.manager.getShops().unloadShop(shopedit);
        }
        if (shopedit != null) {
            ClassManager.manager.getShops().unloadShop(shopedit);
            shopedit = null;
        }
    }

    @Override
    public void ownerJoin(Player p) {
        super.ownerJoin(p);
        money_spent = new HashMap<UUID, Double>();
        items_bought = new HashMap<UUID, Integer>();
    }

    @Override
    public void ownerLeave(Player p) {
        super.ownerLeave(p);
        if (isBeingEdited()) {
            getPlugin().getSaveManager().saveShop(this, p, REASON_SAVE.EDITMODE_OWNER_QUIT);
        } else {
            getPlugin().getSaveManager().saveShop(this, p, REASON_SAVE.OWNER_QUIT);
        }
        money_spent = null;
        items_bought = null;
    }

    public void playerLeave(Player visitor) {
        updateWorth();
        double money = getMoneySpentSoFar(visitor);
        int amount = getItemsBoughtSoFar(visitor);
        if (money != 0) {
            money_spent.remove(visitor.getUniqueId());
            Player owner = Bukkit.getPlayer(getOwner());
            if (owner != null) {
                String reward = CurrencyTools.getDisplayPrice(BSCurrency.detectCurrency(getPlugin().getSettings().getPriceType().name()), money);
                ClassManager.manager.getMessageHandler().sendMessageDirect(ClassManager.manager.getStringManager().transform(getPlugin().getMessages().get("Message.PlayerPurchasedFromYou").replace("%other%", visitor.getDisplayName()), owner).replace("%reward%", reward).replace("%amount%", String.valueOf(amount)), owner);
                Misc.playSound(owner, getPlugin().getSettings().getSoundPlayerPurchasedFromYou());
            }
        }
    }


    public BSShop getShop() {
        return shop;
    }

    public BSShop getShopEdit() {
        return shopedit;
    }

    public BSShop getCurrentShop() {
        return shop == null ? shopedit : shop;
    }

    public boolean canEdit(Player p, boolean fail_message) {
        if (shop != null) {
            if (shop.isBeingAccessed(p)) {
                if (fail_message) {
                    getPlugin().getMessages().sendMessage("Message.ShopBeingUsed", p, null, p, shop, null, null);
                }
                return false;
            }
            if (getEditDelayRemaining() > 0) {
                if (fail_message) {
                    getPlugin().getMessages().sendMessage("Message.ShopEditDelay", p, null, p, shop, null, null);
                }
                return false;
            }
        }
        return true;
    }

    public boolean isBeingEdited() {
        return editing;
    }

    public long getEditDelayRemaining() {
        return last_edit + getPlugin().getSettings().getEditDelay() * 1000 - System.currentTimeMillis();
    }

    public double getMoneySpentSoFar(Player p) {
        if (money_spent != null) {
            if (money_spent.containsKey(p.getUniqueId())) {
                return money_spent.get(p.getUniqueId());
            }
        }
        return 0;
    }

    public int getItemsBoughtSoFar(Player p) {
        if (items_bought != null) {
            if (items_bought.containsKey(p.getUniqueId())) {
                return items_bought.get(p.getUniqueId());
            }
        }
        return 0;
    }

    public int getRank() {
        return rank;
    }


    public void setShop(BSShop s) {
        this.shop = s;
    }

    public void setShopEdit(BSShop s) {
        this.shopedit = s;
    }

    public void setRank(int i) {
        this.rank = i;
    }


    public BSShop createShop() {
        if (shop == null) {
            shop = getPlugin().getShopCreator().createShop(this, true);
        }
        return shop;
    }

    public BSShop createShopEdit(Player p) {
        if (shopedit == null) {
            shopedit = getPlugin().getShopCreator().createShopEdit(this, true);
        }
        return shopedit;
    }

    public void unload() {
        if (isBeingEdited()) {
            finishEdit(false);
        }
        if (shopedit != null) {
            ClassManager.manager.getShops().unloadShop(shopedit);
            shopedit = null;
        }
        if (shop != null) {
            ClassManager.manager.getShops().unloadShop(shop);
            shop = null;
        }
        getPlugin().getShopsManager().removePlayerShop(getOwner());
    }


    public BSBuy createShoplink() {
        BSBuy buy = new BSBuy(BSRewardType.Shop, BSPriceType.Nothing, getShopName(), null, null, -1, null, getShopName());
        buy.setItem(getIcon(), true);
        return buy;
    }


    public void close() {
        if (shop != null) {
            shop.close();
        }
        if (shopedit != null) {
            shopedit.close();
        }
    }


    @Override
    public void addItem(PlayerShopItem item) {
        super.addItem(item);
        if (isBeingEdited()) {
            BSBuy shopitem = item.createShopItemEdit(getPlugin(), createNextItemName(), shopedit);
            shopedit.addShopItem(shopitem, shopitem.getItem(), ClassManager.manager);
            shopedit.finishedAddingItems();
        }
    }

    @Override
    public void removeItem(PlayerShopItem item) {
        if (isBeingEdited()) {
            BSBuy to_remove = null;
            for (BSBuy buy : shopedit.getItems()) {
                ItemStack i = (ItemStack) buy.getReward(null);
                if (i != null) {
                    PlayerShopItem ps = getShopItem(i);
                    if (ps == item) {
                        to_remove = buy;
                        break;
                    }
                }
            }
            if (to_remove != null) {
                shopedit.removeShopItem(to_remove);
            }
            shopedit.finishedAddingItems();
        } else {
            ClassManager.manager.getBugFinder().warn("[PlayerShops] Shopitem removed although shop not being in edit mode.");
        }
        super.removeItem(item);
    }


    public boolean tryEdit(Player p, boolean fail_message) {
        if (canEdit(p, fail_message)) {
            startEdit(p);
            return true;
        }
        return false;
    }

    public void startEdit(Player p) {
        if (shop != null) {
            ClassManager.manager.getShops().unloadShop(shop);
            shop = null;
        }
        getPlugin().getShopsManager().updateShopListing();
        createShopEdit(p);
        editing = true;

        if (getPlugin().getSettings().getRemoveItemsOutOfStock()) {
            List<PlayerShopItem> to_remove = null;
            for (BSBuy buy : shopedit.getItems()) {
                ItemStack i = (ItemStack) buy.getReward(null);
                if (i != null) {
                    PlayerShopItem ps = getShopItem(i);
                    if (ps.getAmount() == 0) {
                        if (to_remove == null) {
                            to_remove = new ArrayList<PlayerShopItem>();
                        }
                        to_remove.add(ps);
                    }
                }
            }
            if (to_remove != null) {
                for (PlayerShopItem ps : to_remove) {
                    removeItem(ps);
                }
            }
        }

        shopedit.openInventory(p);
    }

    public boolean finishEdit(boolean create_real_shop) {
        if (isBeingEdited()) {
            if (shopedit != null) {
                ClassManager.manager.getShops().unloadShop(shopedit);
                shopedit = null;
            }
            save();
            if (create_real_shop) {
                createShop();
                getPlugin().getShopsManager().updateShopListing();
            }
            editing = false;
            last_edit = System.currentTimeMillis();
            return true;
        }
        return false;
    }


    @Override
    public void increaseRewardIncludingTax(double d, Player reason, int amount_bought) {
        super.increaseRewardIncludingTax(d, reason, amount_bought);
        Player owner = Bukkit.getPlayer(getOwner());
        if (owner != null) {
            if (money_spent != null) {
                double spent_so_far = getMoneySpentSoFar(reason);
                money_spent.put(reason.getUniqueId(), spent_so_far + d);
            }
            if (items_bought != null) {
                int bought_so_far = getItemsBoughtSoFar(reason);
                items_bought.put(reason.getUniqueId(), bought_so_far + amount_bought);
            }
        }
    }


    private String createNextItemName() {
        if (isBeingEdited()) {
            return createNextItemName(shopedit);
        } else {
            return createNextItemName(shop);
        }
    }

    private String createNextItemName(BSShop shop) {
        int i = 0;
        String name = String.valueOf(i);
        while (shop.getItem(name) != null) {
            i++;
            name = String.valueOf(i);
        }

        return name;
    }

}
