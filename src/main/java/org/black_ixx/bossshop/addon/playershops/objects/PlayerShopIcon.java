package org.black_ixx.bossshop.addon.playershops.objects;

import java.util.List;

import org.black_ixx.bossshop.addon.playershops.PlayerShops;
import org.black_ixx.bossshop.core.BSBuy;
import org.black_ixx.bossshop.core.conditions.BSSingleCondition;
import org.black_ixx.bossshop.core.prices.BSPriceType;
import org.black_ixx.bossshop.core.rewards.BSRewardType;
import org.black_ixx.bossshop.managers.ClassManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerShopIcon {

    private String path;
    private ItemStack item;
    private List<String> itemdata;
    private String permission;
    private int slot_needed;


    public PlayerShopIcon(ConfigurationSection section) {
        this.path = section.getName();
        itemdata = section.getStringList("Icon");
        item = ClassManager.manager.getItemStackCreator().createItemStack(itemdata, true);
        permission = section.getString("Permission");
        slot_needed = section.getInt("SlotsNeeded");
    }


    public boolean canUse(Player p, PlayerShopSimple shop) {
        if (permission != null) {
            if (!permission.isEmpty()) {
                if (p == null) {
                    return false;
                }
                if (!p.hasPermission(permission)) {
                    return false;
                }
            }
        }
        if (shop.getSlotsAmount(p, true) < slot_needed) {
            return false;
        }

        return true;
    }

    public ItemStack getItem() {
        return item.clone();
    }

    public List<String> getItemData() {
        return itemdata;
    }

    public String getPath() {
        return path;
    }


    public BSBuy createShopitemAllow(PlayerShops plugin, String name) {
        ItemStack item = plugin.getIconManager().transformName(plugin, null, this.item.clone(), false, false);
        BSBuy buy = new BSBuy(plugin.getBossShopListener().getRewardTypeShopIcon(), BSPriceType.Nothing, item, null, plugin.getMessages().get("Message.IconSelected"), -1, permission, name, new BSSingleCondition(plugin.getBossShopListener().getPlayerShopSlotsCondition(), "over", String.valueOf(slot_needed - 1)), null, null);
        buy.setItem(item, false);
        return buy;
    }

    public BSBuy createShopitemDeny(PlayerShops plugin, String name) {
        ItemStack item = plugin.getIconManager().transformName(plugin, null, this.item.clone(), false, false);

        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        lore.add(plugin.getMessages().get("ShopIcon.RequiresSlots").replace("%slots%", String.valueOf(slot_needed)));
        meta.setLore(lore);
        item.setItemMeta(meta);

        BSBuy buy = new BSBuy(BSRewardType.Nothing, BSPriceType.Nothing, null, null, plugin.getMessages().get("Message.RequiresMoreSlots"), -1, null, name, new BSSingleCondition(plugin.getBossShopListener().getPlayerShopSlotsCondition(), "under", String.valueOf(slot_needed)), null, null);
        buy.setItem(item, false);
        return buy;
    }

}
