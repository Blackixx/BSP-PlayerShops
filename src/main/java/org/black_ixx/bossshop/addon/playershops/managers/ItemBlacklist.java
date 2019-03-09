package org.black_ixx.bossshop.addon.playershops.managers;

import java.util.List;

import org.black_ixx.bossshop.managers.ClassManager;
import org.black_ixx.bossshop.managers.item.ItemStackChecker;
import org.black_ixx.bossshop.managers.misc.InputReader;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class ItemBlacklist {

    private List<ItemStack> items;


    public ItemBlacklist(FileConfiguration config) {
        items = InputReader.readItemList(config.get("ForbiddenItems"), true);
        for (ItemStack item : items) {
            item.setAmount(1);
        }
    }


    public boolean isBlocked(ItemStack check) {
        ItemStackChecker c = ClassManager.manager.getItemStackChecker();
        for (ItemStack item : items) {
            if (c.isEqualShopItemAdvanced(item, check, true, false, false, null)) {
                return true;
            }
        }
        return false;
    }

}
