package org.black_ixx.bossshop.addon.playershops.types;


import org.black_ixx.bossshop.addon.playershops.PlayerShops;
import org.black_ixx.bossshop.core.BSBuy;
import org.black_ixx.bossshop.core.prices.BSPriceTypeNumber;
import org.black_ixx.bossshop.managers.ClassManager;
import org.black_ixx.bossshop.managers.misc.InputReader;
import org.black_ixx.bossshop.misc.CurrencyTools;
import org.black_ixx.bossshop.misc.CurrencyTools.BSCurrency;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class BSPriceTypePlayerShopCurrency extends BSPriceTypeNumber {


    private PlayerShops plugin;

    public BSPriceTypePlayerShopCurrency(PlayerShops plugin) {
        this.plugin = plugin;
    }


    public Object createObject(Object o, boolean force_final_state) {
        return InputReader.getDouble(o, -1);
    }

    public boolean validityCheck(String item_name, Object o) {
        if ((Double) o != -1) {
            return true;
        }
        ClassManager.manager.getBugFinder().severe("Was not able to create ShopItem " + item_name + "! PlayerShops did something wrong.");
        return false;
    }

    public void enableType() {

        //Money
        if (plugin.getSettings().getPriceType() == Money) {
            ClassManager.manager.getSettings().setMoneyEnabled(true);
            ClassManager.manager.getSettings().setVaultEnabled(true);

            //Points
        } else if (plugin.getSettings().getPriceType() == Points) {
            ClassManager.manager.getSettings().setPointsEnabled(true);
        }
    }


    @Override
    public boolean hasPrice(Player p, BSBuy buy, Object price, ClickType clickType, int multiplier, boolean messageOnFailure) {
        double cost = (Double) price * multiplier;
        return CurrencyTools.hasValue(p, BSCurrency.detectCurrency(plugin.getSettings().getPriceType().name()), cost, messageOnFailure);
    }

    @Override
    public String takePrice(Player p, BSBuy buy, Object price, ClickType clickType, int multiplier) {
        double cost = (Double) price * multiplier;
        return CurrencyTools.takePrice(p, BSCurrency.detectCurrency(plugin.getSettings().getPriceType().name()), cost);
    }

    @Override
    public String getDisplayBalance(Player p, BSBuy buy, Object price, ClickType clickType) {
        return null;
    }

    @Override
    public String getDisplayPrice(Player p, BSBuy buy, Object price, ClickType clickType) {
        return CurrencyTools.getDisplayPrice(BSCurrency.detectCurrency(plugin.getSettings().getPriceType().name()), (Double) price);
    }

    @Override
    public String[] createNames() {
        return new String[]{"playerpointscurrency"};
    }

    public boolean supportsMultipliers() {
        return false;
    }

    @Override
    public boolean mightNeedShopUpdate() {
        return true;
    }

    @Override
    public boolean isIntegerValue() {
        return false;
    }


}
