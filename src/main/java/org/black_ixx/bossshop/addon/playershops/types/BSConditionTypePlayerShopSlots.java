package org.black_ixx.bossshop.addon.playershops.types;

import org.black_ixx.bossshop.addon.playershops.PlayerShops;
import org.black_ixx.bossshop.addon.playershops.objects.PlayerShop;
import org.black_ixx.bossshop.core.BSBuy;
import org.black_ixx.bossshop.core.BSShopHolder;
import org.black_ixx.bossshop.core.conditions.BSConditionTypeNumber;
import org.bukkit.entity.Player;

public class BSConditionTypePlayerShopSlots extends BSConditionTypeNumber {


    private PlayerShops plugin;

    public BSConditionTypePlayerShopSlots(PlayerShops plugin) {
        this.plugin = plugin;
    }


    @Override
    public double getNumber(BSBuy shopitem, BSShopHolder holder, Player p) {
        PlayerShop shop = plugin.getShopsManager().getPlayerShop(p.getUniqueId());
        if (shop == null) {
            return 0;
        }
        return shop.getSlotsAmount(p, true); //Player needs to be owner!
    }

    @Override
    public boolean dependsOnPlayer() {
        return true;
    }

    @Override
    public String[] createNames() {
        return new String[]{"playershopslots"};
    }


    @Override
    public void enableType() {
    }


}
