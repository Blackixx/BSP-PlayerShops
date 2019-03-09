package org.black_ixx.bossshop.addon.playershops.objects;

import java.util.UUID;

import org.black_ixx.bossshop.managers.ClassManager;
import org.black_ixx.bossshop.managers.misc.InputReader;
import org.black_ixx.bossshop.misc.userinput.BSUserInput;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class PlayerShopsUserInputPrice extends BSUserInput {


    private PlayerShop shop;
    private UUID uuid;
    private ItemStack item;
    private int slot;


    public PlayerShopsUserInputPrice(PlayerShop shop, Player p, ItemStack item, int slot) {
        this.shop = shop;
        this.uuid = p.getUniqueId();
        this.item = item;
        this.slot = slot;
    }


    public void receivedInput(Player p, String text) {
        if (p.getUniqueId() == uuid) { //probably it is not even possible this event will trigger with an other player

            if (p.getInventory().getItem(slot) == null) {
                ClassManager.manager.getBugFinder().warn("Unable to set price of PlayerShops item: The given slot somehow is empty.");
                return;
            }

            if (!p.getInventory().getItem(slot).equals(item)) {
                ClassManager.manager.getBugFinder().warn("Unable to set price of PlayerShops item: The given slot somehow contains a different item than it did before the price was entered.");
                return;
            }

            text = ChatColor.stripColor(text).trim();
            double worth = InputReader.getDouble(text, -1);
            if (worth == -1) {
                ClassManager.manager.getMessageHandler().sendMessageDirect(ClassManager.manager.getStringManager().transform(shop.getPlugin().getMessages().get("Message.InvalidNumber").replace("%input%", text), p), p);
                shop.getShopEdit().openInventory(p);
                return;
            }

            if (worth > shop.getPlugin().getSettings().getPriceMax()) {
                ClassManager.manager.getMessageHandler().sendMessageDirect(ClassManager.manager.getStringManager().transform(shop.getPlugin().getMessages().get("Message.InvalidNumberHigh").replace("%input%", text), p), p);
                shop.getShopEdit().openInventory(p);
                return;
            }

            if (worth < shop.getPlugin().getSettings().getPriceMin()) {
                ClassManager.manager.getMessageHandler().sendMessageDirect(ClassManager.manager.getStringManager().transform(shop.getPlugin().getMessages().get("Message.InvalidNumberLow").replace("%input%", text), p), p);
                shop.getShopEdit().openInventory(p);
                return;
            }

            p.getInventory().setItem(slot, null);

            shop.addItem(new PlayerShopItem(item, item.getAmount(), worth));
            shop.getShopEdit().openInventory(p);
        }
    }


}
