package org.black_ixx.bossshop.addon.playershops.types;

import org.black_ixx.bossshop.addon.playershops.PlayerShops;
import org.black_ixx.bossshop.addon.playershops.objects.PlayerShop;
import org.black_ixx.bossshop.core.BSBuy;
import org.black_ixx.bossshop.core.BSShopHolder;
import org.black_ixx.bossshop.core.conditions.BSConditionType;
import org.black_ixx.bossshop.managers.misc.InputReader;
import org.bukkit.entity.Player;

public class BSConditionTypePlayerShop extends BSConditionType {


    private PlayerShops plugin;

    public BSConditionTypePlayerShop(PlayerShops plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean dependsOnPlayer() {
        return true;
    }

    @Override
    public String[] createNames() {
        return new String[]{"playershop"};
    }


    @Override
    public void enableType() {
    }


    @Override
    public boolean meetsCondition(BSShopHolder holder, BSBuy shopitem, Player p, String conditiontype, String condition) {
        if (conditiontype.equalsIgnoreCase("own")) {
            PlayerShop shop = plugin.getShopsManager().getPlayerShop(p.getUniqueId());
            if (shop == null) {
                return InputReader.getBoolean(condition, true) == false;
            }
            return InputReader.getBoolean(condition, true) == (shop.getShop() == holder.getShop());
        }
        if (conditiontype.equalsIgnoreCase("ownany")) {
            return InputReader.getBoolean(condition, true) == (plugin.getShopsManager().getPlayerShop(p.getUniqueId()) != null);
        }

        if (conditiontype.equalsIgnoreCase("renting")) {
            PlayerShop shop = plugin.getShopsManager().getPlayerShop(p.getUniqueId());
            if (shop == null) {
                return InputReader.getBoolean(condition, true) == false;
            }
            if (shop.getShop() != holder.getShop()) {
                return InputReader.getBoolean(condition, true) == false;
            }
            return InputReader.getBoolean(condition, true) == shop.getRentTimeLeft(false, true) > 0;
        }
        if (conditiontype.equalsIgnoreCase("rentingany")) {
            PlayerShop shop = plugin.getShopsManager().getPlayerShop(p.getUniqueId());
            if (shop == null) {
                return InputReader.getBoolean(condition, true) == false;
            }
            return InputReader.getBoolean(condition, true) == shop.getRentTimeLeft(false, true) > 0;
        }
        if (conditiontype.equalsIgnoreCase("canrent")) {
            int limit = plugin.getSettings().getRentPlayerLimit();
            if (limit == -1) {
                return InputReader.getBoolean(condition, true) == true;
            }
            return InputReader.getBoolean(condition, true) == limit > plugin.getShopsManager().getRentingPlayersAmount();
        }
        if (conditiontype.equalsIgnoreCase("allowinventoryitem")) {
            PlayerShop shop = plugin.getShopsManager().getPlayerShop(p.getUniqueId());
            if (shop == null) {
                return InputReader.getBoolean(condition, true) == false;
            }
            return InputReader.getBoolean(condition, true) == plugin.getIconManager().getAllowInventoryItem(p, shop);
        }
        if (conditiontype.equalsIgnoreCase("allowshoprename")) {
            PlayerShop shop = plugin.getShopsManager().getPlayerShop(p.getUniqueId());
            if (shop == null) {
                return InputReader.getBoolean(condition, true) == false;
            }
            return InputReader.getBoolean(condition, true) == plugin.getIconManager().getAllowShopRename(p, shop) && shop.getCurrentShop() == holder.getShop();
        }

        return false;
    }


    @Override
    public String[] showStructure() {
        return new String[]{"own:[boolean]", "renting:[boolean]"};
    }


}
