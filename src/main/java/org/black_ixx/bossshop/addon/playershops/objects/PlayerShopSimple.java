package org.black_ixx.bossshop.addon.playershops.objects;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.black_ixx.bossshop.addon.playershops.PlayerShops;
import org.black_ixx.bossshop.api.BSAddonConfig;
import org.black_ixx.bossshop.managers.ClassManager;
import org.black_ixx.bossshop.misc.CurrencyTools;
import org.black_ixx.bossshop.misc.CurrencyTools.BSCurrency;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.io.Files;


public class PlayerShopSimple extends BSAddonConfig {

    private PlayerShops plugin;
    private UUID owner;
    private String owner_name;
    private String shop_name;
    private double reward;
    private double worth;

    private int priority;
    private ItemStack icon;

    private int slots_amount;
    private List<PlayerShopItem> items;

    private long rent_start; //After rent time is reached one rent will be removed from paid amount
    private double rent_paid; //Total amount of rent


    public PlayerShopSimple(PlayerShops plugin, Player owner) {
        super(plugin, "shops" + File.separator + owner.getUniqueId().toString().substring(0, 1) + File.separator + owner.getUniqueId());
        this.plugin = plugin;
        this.owner = owner.getUniqueId();
        slots_amount = plugin.getSettings().getShopCreationSlots();
        rent_start = 0;
        rent_paid = 0;
        owner_name = owner.getName();
        items = new ArrayList<PlayerShopItem>();
        setIcon(plugin.getIconManager().getHighestShopIconItem(owner, this, false), true, false, false);
        updatePriority(owner);
    }

    public PlayerShopSimple(PlayerShops plugin, UUID owner) {
        super(plugin, "shops" + File.separator + owner.toString().substring(0, 1) + File.separator + owner);
        this.plugin = plugin;
        this.owner = owner;
        load();
    }

    public PlayerShopSimple(PlayerShops plugin, File f) {
        super(plugin, f);
        this.plugin = plugin;
        this.owner = UUID.fromString(getString("Owner", "UNKNOWN"));
        load();
    }

    public void load() {
        owner_name = getString("OwnerName", "unknown");
        slots_amount = getInt("Slots", plugin.getSettings().getSlotsAmount());
        rent_start = getConfig().getLong("Rent.Start", 0);
        rent_paid = getDouble("Rent.Paid", 0);
        reward = getDouble("Reward", 0);
        worth = getDouble("Worth", 0);
        priority = getInt("Priority", 0);
        shop_name = getString("ShopName", null);
        this.icon = getConfig().getItemStack("Icon");
        items = new ArrayList<PlayerShopItem>();
        ConfigurationSection s = getConfig().getConfigurationSection("item");
        if (s != null) {
            for (String key : s.getKeys(false)) {
                PlayerShopItem item = new PlayerShopItem(s.getConfigurationSection(key));
                items.add(item);
            }
        }
        updateWorth();
    }

    public void delete(File backupfolder) {
        if (backupfolder != null) {
            save();
            try {
                File dest = new File(backupfolder, getFile().getName());
                if (!backupfolder.exists()) {
                    backupfolder.mkdir();
                }
                dest.createNewFile();
                Files.copy(getFile(), dest);
            } catch (IOException e) {
                plugin.getLogger().warning("Unable to create backup of deleted shop of player " + getOwnerName() + ".");
            }
        }
        getFile().delete();
        plugin.getShopsManager().removePlayerShop(owner);
        plugin.getShopsManager().updateShopListing();
    }

    public void ownerJoin(Player p) {
        updateInfo(p);
    }

    public void ownerLeave(Player p) {
    }

    public void updateInfo(Player p) {
        if (p != null) {
            this.owner_name = p.getName();
            updatePriority(p);
            updateIcon(p);
        }
        updateRent();
    }

    public void updateIcon(Player p) {
        if (plugin.getIconManager().isShopIconFix()) {
            setIcon(plugin.getIconManager().getHighestShopIconItem(p, this, true), true, false, false);
        } else {
            setIcon(icon, true, false, false);
        }
    }


    public PlayerShops getPlugin() {
        return plugin;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getOwnerName() {
        return owner_name;
    }

    public int getSlotsAmount(Player p, boolean including_permissions_slots) {
        return slots_amount + (including_permissions_slots ? plugin.getSettings().getAdditionalSlots(p) : 0);
    }

    public boolean isEmptySlotLeft(Player p) {
        return getSlotsAmount(p, true) > items.size();
    }

    public List<PlayerShopItem> getItems() {
        return items;
    }

    public ItemStack getIcon() {
        if (icon == null) {
            setIcon(plugin.getIconManager().getHighestShopIconItem(null, this, true), true, false, false);
        }
        return icon;
    }

    public void increaseReward(double d, Player reason, int amount_bought) {
        increaseRewardIncludingTax(d - (d * plugin.getSettings().getTax()), reason, amount_bought);
    }

    public void increaseRewardIncludingTax(double d, Player reason, int amount_bought) {
        reward += d;
    }

    public double getReward() {
        return reward;
    }

    public double getWorth() {
        return worth;
    }

    public boolean containsVisibleItems() {
        synchronized (items) {
            for (PlayerShopItem item : items) {
                if (item.getAmount() >= item.getItemStack().getAmount()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setIcon(ItemStack i, boolean force_transform, boolean use_default_text, boolean inventoryitem) {
        if (force_transform) {
            icon = plugin.getIconManager().transformName(plugin, this, i, use_default_text, inventoryitem);
        } else {
            icon = i;
        }
    }

    public void setShopDisplayName(String s) {
        this.shop_name = s;
    }

    public String getShopDisplayName() {
        if (shop_name != null) {
            return shop_name;
        }
        return plugin.getMessages().get("ShopIcon.DefaultPlayerShopName").replace("%player%", getOwnerName());
    }

    public void giveReward(Player p) {
        BSCurrency c = BSCurrency.detectCurrency(plugin.getSettings().getPriceType().name());
        CurrencyTools.giveReward(p, c, reward);
        ClassManager.manager.getMessageHandler().sendMessageDirect(ClassManager.manager.getStringManager().transform(plugin.getMessages().get("Message.ReceivedReward").replace("%reward%", CurrencyTools.getDisplayPrice(c, reward)), p), p);
        reward = 0;
        save();
    }

    public void increaseSlots(Player p) {
        this.slots_amount += plugin.getSettings().getSlotsAmount();
        updateIcon(p);
    }

    public void updatePriority(Player p) {
        priority = plugin.getSettings().getShopPriority(this, p);
    }

    public boolean hasHigherPriority(PlayerShopSimple current) {
        if (priority > current.priority) {
            return true;
        }
        if (priority == current.priority) {

            if (plugin.getSettings().getRentSortAfterAmount()) {
                if (rent_paid > current.rent_paid) {
                    return true;
                } else if (current.rent_paid > rent_paid) {
                    return false;
                }
            }

            //There is one other possible case: None of the shops paid rent (or rent does not matter at all)
            if (this.slots_amount > current.slots_amount) {
                return true;
            }


        }
        return false;
    }

    public long getRentTimeLeft(boolean complete, boolean update) {
        if (update) {
            updateRent();
        }
        double period_decrease = plugin.getSettings().getRentPeriodDecrease();
        if (period_decrease != 0) {
            long period = plugin.getSettings().getRentPeriod() * 1000;
            long time_so_far = System.currentTimeMillis() - rent_start;

            if (complete) {
                period *= ((rent_paid / period_decrease) + (rent_paid % period_decrease == 0 ? 0 : 1));
            }

            long time_left = period - time_so_far;
            return time_left;
        }

        return -1; //Infinite time left
    }

    public String getShopName() {
        return "playershops_" + getOwner().toString();
    }

    public String getShopEditName() {
        return getShopName() + "_edit";
    }

    public double getRentPaid() {
        return rent_paid;
    }

    public void updateRent() {
        if (rent_paid > 0 && getRentTimeLeft(false, false) <= 0) {
            rent_paid -= plugin.getSettings().getRentPeriodDecrease();
            if (rent_paid > 0) {
                rent_start = System.currentTimeMillis();
            } else {
                rent_start = 0;
            }
        }
    }

    public void payRent() {
        updateRent();
        rent_paid += plugin.getSettings().getRentPrice();
        if (getRentTimeLeft(false, false) <= 0) {
            rent_start = System.currentTimeMillis();
        }
    }

    public void updateWorth() {
        double worth = 0;
        for (PlayerShopItem item : items) {
            worth += item.getWorth();
        }
        this.worth = worth;
    }

    public void addItem(PlayerShopItem item) {
        items.add(item);
        updateWorth();
    }

    public void removeItem(PlayerShopItem item) {
        items.remove(item);
        updateWorth();
    }

    public boolean increaseItemAmount(ItemStack item, int amount) {
        PlayerShopItem si = getShopItem(item);
        if (si != null) {
            si.increase(amount);
            updateWorth();
            return true;
        }
        return false;
    }

    public boolean containsItem(ItemStack item) {
        return getShopItem(item) != null;
    }

    public PlayerShopItem getShopItem(ItemStack item) {
        for (PlayerShopItem si : items) {
            if (si.isSimilar(item)) {
                return si;
            }
        }
        return null;
    }


    public boolean save() {
        set("Owner", owner.toString());
        set("OwnerName", owner_name);
        set("Reward", reward);
        set("Worth", worth);
        set("Slots", slots_amount);
        set("Rent.Start", rent_start);
        set("Rent.Paid", rent_paid);
        set("Priority", priority);
        set("Icon", icon);
        set("ShopName", shop_name);
        deleteAll("item");
        int i = 0;
        for (PlayerShopItem item : items) {
            ConfigurationSection s = getConfig().getConfigurationSection("item");
            if (s == null) {
                s = getConfig().createSection("item");
            }
            item.store(s.createSection(String.valueOf(i)));
            i++;
        }

        return super.save();
    }

}
