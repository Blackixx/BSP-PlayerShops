package org.black_ixx.bossshop.addon.playershops;

import java.util.HashMap;
import java.util.List;

import org.black_ixx.bossshop.addon.playershops.objects.PlayerShop;
import org.black_ixx.bossshop.addon.playershops.objects.PlayerShopSimple;
import org.black_ixx.bossshop.core.prices.BSPriceType;
import org.black_ixx.bossshop.managers.ClassManager;
import org.black_ixx.bossshop.managers.misc.InputReader;
import org.black_ixx.bossshop.misc.CurrencyTools.BSCurrency;
import org.black_ixx.bossshop.misc.MathTools;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;


public class Settings {


    private BSPriceType pricetype;
    private String pricetypeentry;
    private boolean permissions;

    private double creation_price;
    private int creation_slots;

    private boolean slots_enabled;
    private String slots_price;
    private int slots_amount;
    private int slots_limit;
    private HashMap<String, Integer> slot_permissions;
    private boolean slot_permissions_enabled;
    private int editdelay;

    private List<String> ranking;

    private boolean rent_enabled, rent_sort_after_amount, rent_allow_stacking;
    private double rent_price, rent_period_decrease;
    private long rent_period;
    private int rent_player_limit;

    private boolean listonlineplayersonly;
    private double tax;
    private double price_min, price_max;

    private String sound_player_purchased_from_you;
    private String signs_shop_text, signs_listing_text;
    private boolean signs_enabled;

    private boolean prevent_selling_pluginitems;
    private boolean prevent_creative_access;
    private boolean remove_items_out_of_stock;

    public Settings(FileConfiguration config) {
        this.pricetypeentry = config.getString("PriceType");
        this.creation_price = InputReader.getDouble(config.get("ShopCreation.Price"), 1000);
        this.creation_slots = InputReader.getInt(config.get("ShopCreation.Slots"), 18);
        this.permissions = config.getBoolean("EnablePermissions");

        this.slots_enabled = config.getBoolean("SlotPurchase.Enabled");
        this.slots_price = config.getString("SlotPurchase.Price");
        this.slots_amount = InputReader.getInt(config.get("SlotPurchase.Amount"), 3);
        this.slots_limit = InputReader.getInt(config.get("SlotPurchase.TotalLimit"), 54);
        this.slot_permissions_enabled = config.getBoolean("SlotPermissions.Enabled");
        this.slot_permissions = new HashMap<String, Integer>();
        for (String line : config.getStringList("SlotPermissions.List")) {
            String parts[] = line.split(":");
            slot_permissions.put(parts[0], InputReader.getInt(parts[1], 0));
        }
        this.editdelay = InputReader.getInt(config.get("ShopEditDelay"), 60);

        this.ranking = config.getStringList("ShopRanking.List");
        this.listonlineplayersonly = config.getBoolean("ShopRanking.ListOnlinePlayerShopsOnly");


        this.rent_enabled = config.getBoolean("Renting.Enabled");
        this.rent_sort_after_amount = config.getBoolean("Renting.SortAfterRentAmount");
        this.rent_allow_stacking = config.getBoolean("Renting.AllowStacking");
        this.rent_price = InputReader.getInt(config.get("Renting.Price"), 750);
        this.rent_period_decrease = InputReader.getDouble(config.get("Renting.PeriodDecrease"), 750);
        this.rent_period = InputReader.getInt(config.get("Renting.Period"), 60 * 60 * 24 * 30);
        this.rent_player_limit = InputReader.getInt(config.get("Renting.PlayerLimit"), 18);

        this.tax = InputReader.getDouble(config.get("Tax"), 0);
        this.price_min = InputReader.getDouble(config.get("Price.Minimum"), 0);
        this.price_max = InputReader.getDouble(config.get("Price.Maximum"), 75000);
        this.sound_player_purchased_from_you = config.getString("Sound.PlayerPurchasedFromYou");

        this.signs_shop_text = config.getString("Signs.PlayerShopText");
        this.signs_listing_text = config.getString("Signs.ShopListingText");
        this.signs_enabled = config.getBoolean("Signs.Enabled");

        this.prevent_selling_pluginitems = config.getBoolean("PreventSellingPluginItems");
        this.prevent_creative_access = config.getBoolean("PreventCreativeAccess");
        this.remove_items_out_of_stock = config.getBoolean("RemoveItemsOutOfStock");
    }

    private void updatePriceType() {
        if (pricetype == null) {
            pricetype = BSPriceType.detectType(pricetypeentry);
            if (BSCurrency.detectCurrency(pricetype.name()) != null) {
                //SUCCESS
            } else {
                ClassManager.manager.getBugFinder().severe("[PlayerShops] Unable to work with given PriceType. Automatically picking Exp in order to make the PlayerShops addon work. If you want something else please configure one of following supported PriceTypes: 'money', 'points' or 'exp'.");
                pricetype = BSPriceType.Exp;
            }
        }
    }


    public BSPriceType getPriceType() {
        updatePriceType();
        return pricetype;
    }

    public boolean getPermissionsEnabled() {
        return permissions;
    }

    public String getPermission(String node) {
        return permissions ? node : null;
    }


    public double getShopCreationPrice() {
        return creation_price;
    }

    public int getShopCreationSlots() {
        return creation_slots;
    }


    public boolean getSlotsEnabled() {
        return slots_enabled;
    }

    public double getSlotsPriceReal(Player p, PlayerShops plugin) {
        PlayerShop playershop = plugin.getShopsManager().getPlayerShop(p.getUniqueId());
        if (playershop == null) {
            return 0;
        }
        String price = ClassManager.manager.getStringManager().transform(slots_price, null, playershop.getShopEdit(), null, p);
        double d = MathTools.calculate(price, 0);
        return ClassManager.manager.getMultiplierHandler().calculatePriceWithMultiplier(p, pricetype, d);
    }

    public int getSlotsAmount() {
        return slots_amount;
    }

    public int getSlotsLimit() {
        return slots_limit;
    }

    public int getAdditionalSlots(Player p) {
        int add = 0;
        if (slot_permissions_enabled) {
            for (String permission : slot_permissions.keySet()) {
                if (p.hasPermission(permission)) {
                    add += slot_permissions.get(permission);
                }
            }
        }
        return add;
    }

    public int getEditDelay() {
        return editdelay;
    }


    public boolean getRentEnabled() {
        return rent_enabled;
    }

    public boolean getRentSortAfterAmount() {
        return rent_sort_after_amount;
    }

    public boolean getRentAllowStacking() {
        return rent_allow_stacking;
    }

    public double getRentPrice() {
        return rent_price;
    }

    public double getRentPeriodDecrease() {
        return rent_period_decrease;
    }

    public long getRentPeriod() {
        return rent_period;
    }

    public int getRentPlayerLimit() {
        return rent_player_limit;
    }

    public boolean getListOnlinePlayersOnly() {
        return listonlineplayersonly;
    }

    public double getTax() {
        return tax;
    }

    public double getPriceMin() {
        return price_min;
    }

    public double getPriceMax() {
        return price_max;
    }

    public String getSoundPlayerPurchasedFromYou() {
        return sound_player_purchased_from_you;
    }

    public String getSignsTextPlayerShop() {
        return signs_shop_text;
    }

    public String getSignsTextShopListing() {
        return signs_listing_text;
    }

    public boolean getSignsEnabled() {
        return signs_enabled;
    }

    public boolean getPreventSellingPluginsItems() {
        return prevent_selling_pluginitems;
    }

    public boolean getPreventCreativeAccess() {
        return prevent_creative_access;
    }

    public boolean getRemoveItemsOutOfStock() {
        return remove_items_out_of_stock;
    }


    public int getShopPriority(PlayerShopSimple shop, Player p) {
        if (ranking == null) {
            return 0;
        }
        for (int i = 0; i < ranking.size(); i++) {
            String s = ranking.get(i);

            if (s.equalsIgnoreCase("renting")) {
                if (shop.getRentTimeLeft(false, true) > 0) {
                    return ranking.size() - i;
                }
            }
            if (p.hasPermission(s)) {
                return ranking.size() - i;
            }
        }
        return 0;
    }

    public int getRentingPriority() {
        for (int i = 0; i < ranking.size(); i++) {
            String s = ranking.get(i);

            if (s.equalsIgnoreCase("renting")) {
                return ranking.size() - i;
            }

        }
        return 0;
    }


}
