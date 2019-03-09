package org.black_ixx.bossshop.addon.playershops.objects;


import java.util.ArrayList;
import java.util.List;

import org.black_ixx.bossshop.core.BSBuy;
import org.black_ixx.bossshop.core.conditions.BSCondition;
import org.black_ixx.bossshop.core.prices.BSPriceType;
import org.black_ixx.bossshop.core.rewards.BSRewardType;
import org.black_ixx.bossshop.managers.ClassManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class ShopMenuItem {


    private String path;
    private String message;
    private ItemStack itemstack;
    private List<String> itemdata;


    public ShopMenuItem(FileConfiguration config, String path) {
        this(path, config.getStringList(path + ".MenuItem"), config.getString(path + ".Message"));
    }

    public ShopMenuItem(String path, List<String> itemdata, String message) {
        this.path = path;
        this.itemdata = itemdata;
        if (itemdata != null) {
            if (!itemdata.isEmpty()) {
                this.itemstack = ClassManager.manager.getItemStackCreator().createItemStack(itemdata, false);
            }
        }
        this.message = message;
    }


    public String getPath() {
        return path;
    }

    public List<String> getItsemData() {
        return itemdata;
    }

    public ItemStack getItesmStack() {
        return itemstack;
    }

    public String getMessage() {
        return message;
    }

    public List<ItemStack> getItemList(int amount) {
        if (itemstack != null) {
            ArrayList<ItemStack> list = new ArrayList<ItemStack>();
            ItemStack item = itemstack.clone();
            item.setAmount(amount);
            list.add(item);
            return list;
        }
        return null;
    }


    public BSBuy createShopItem(BSRewardType rewardtype, BSPriceType pricetype, Object reward, Object price, int inventorylocation, String permission) {
        return createShopItem(rewardtype, pricetype, reward, price, inventorylocation, permission, null);
    }

    public BSBuy createShopItem(BSRewardType rewardtype, BSPriceType pricetype, Object reward, Object price, int inventorylocation, String permission, BSCondition condition) {
        return createShopItem(rewardtype, pricetype, reward, price, message, inventorylocation, permission, condition);
    }

    public BSBuy createShopItem(BSRewardType rewardtype, BSPriceType pricetype, Object reward, Object price, String message, int inventorylocation, String permission, BSCondition condition) {
        if (itemstack != null) {
            BSBuy buy = new BSBuy(rewardtype, pricetype, reward, price, message, inventorylocation, permission, path, condition, null, null);
            buy.setItem(itemstack, false);
            return buy;
        } else {
            return null;
        }
    }


}
