package org.black_ixx.bossshop.addon.playershops.types;

import org.black_ixx.bossshop.addon.playershops.PlayerShops;
import org.black_ixx.bossshop.addon.playershops.objects.PlayerShop;
import org.black_ixx.bossshop.addon.playershops.objects.PlayerShopItem;
import org.black_ixx.bossshop.core.BSBuy;
import org.black_ixx.bossshop.core.BSShopHolder;
import org.black_ixx.bossshop.core.conditions.BSConditionType;
import org.black_ixx.bossshop.managers.ClassManager;
import org.black_ixx.bossshop.managers.misc.InputReader;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BSConditionTypePlayerShopItem extends BSConditionType {


    private PlayerShops plugin;

    public BSConditionTypePlayerShopItem(PlayerShops plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean dependsOnPlayer() {
        return true;
    }

    @Override
    public String[] createNames() {
        return new String[]{"playershopitem"};
    }


    @Override
    public void enableType() {
    }


    @Override
    public boolean meetsCondition(BSShopHolder holder, BSBuy shopitem, Player p, String conditiontype, String condition) {
        if (conditiontype.equalsIgnoreCase("instock")) {
            PlayerShop playershop = plugin.getShopsManager().getPlayerShop(shopitem.getShop(), false);
            if (playershop == null) {
                ClassManager.manager.getBugFinder().severe("[PlayerShops] (Condition PlayerShopItem) Unable to detect PlayerShop via Shopitem that is connected to " + shopitem.getShop());
                return false;
            }
            ItemStack itemstack = (ItemStack) shopitem.getReward(null);
            PlayerShopItem item = playershop.getShopItem((itemstack));
            if (item == null) {
                ClassManager.manager.getBugFinder().severe("[PlayerShops] (Condition PlayerShopItem) Unable to detect PlayerShopItem via Shopitem.");
                return false;
            }
            return InputReader.getBoolean(condition, true) == item.getAmount() >= itemstack.getAmount();
        }

        return false;
    }


    @Override
    public String[] showStructure() {
        return new String[]{"instock:[boolean]"};
    }


}
