package org.black_ixx.bossshop.addon.playershops.objects;

import java.util.UUID;

import org.black_ixx.bossshop.managers.ClassManager;
import org.black_ixx.bossshop.misc.userinput.BSUserInput;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerShopsUserInputRename extends BSUserInput {

    private PlayerShop shop;
    private UUID uuid;


    public PlayerShopsUserInputRename(PlayerShop shop, Player p) {
        this.shop = shop;
        this.uuid = p.getUniqueId();
    }


    public void receivedInput(Player p, String text) {
        text = text.replaceAll(String.valueOf(ChatColor.COLOR_CHAR), "&");
        if (p.getUniqueId() == uuid) { //probably it is not even possible this event will trigger with an other player
            if (!shop.getPlugin().getIconManager().getAllowShopRenameColors(p, shop)) {
                text = text.replaceAll("&", "");
            }


            text = ClassManager.manager.getStringManager().transform(text, null, shop.getCurrentShop(), null, p);
            ItemStack i = shop.getIcon();
            if (i != null && i.hasItemMeta()) {
                ItemMeta meta = i.getItemMeta();
                if (meta.hasDisplayName()) {
                    meta.setDisplayName(meta.getDisplayName().replace(shop.getShopDisplayName(), text));
                    i.setItemMeta(meta);
                }
            }
            shop.setShopDisplayName(text);
            shop.updateIcon(p);
            shop.getShopEdit().openInventory(p);

        }
    }
}
