package org.black_ixx.bossshop.addon.playershops.managers;


import org.black_ixx.bossshop.addon.playershops.PlayerShops;
import org.black_ixx.bossshop.api.BSAddonConfig;
import org.black_ixx.bossshop.core.BSBuy;
import org.black_ixx.bossshop.core.BSShop;
import org.black_ixx.bossshop.core.BSShopHolder;
import org.black_ixx.bossshop.managers.ClassManager;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class MessageHandler {
    private FileConfiguration config;

    public MessageHandler(final PlayerShops plugin, BSAddonConfig file) {
        config = file.getConfig();
    }

    public FileConfiguration getConfig() {
        return config;
    }


    public void sendMessage(String node, CommandSender sender) {
        sendMessage(node, sender, null, null, null, null, null);
    }

    public void sendMessage(String node, CommandSender sender, String offline_target) {
        sendMessage(node, sender, offline_target, null, null, null, null);
    }

    public void sendMessage(String node, CommandSender sender, Player target) {
        sendMessage(node, sender, null, target, null, null, null);
    }

    public void sendMessage(String node, CommandSender sender, String offline_target, Player target, BSShop shop, BSShopHolder holder, BSBuy item) {
        if (sender != null) {

            if (node == null || node == "") {
                return;
            }

            String message = get(node, target, shop, holder, item);

            if (message == null || message.isEmpty() || message.length() < 2) {
                return;
            }

            if (offline_target != null) {
                message = message.replace("%player%", offline_target).replace("%name%", offline_target).replace("%target%", offline_target);
            }

            sendMessageDirect(message, sender);
        }
    }

    public void sendMessageDirect(String message, CommandSender sender) {
        if (sender != null) {

            if (message == null || message.isEmpty() || message.length() < 2) {
                return;
            }

            for (String line : message.split("\n"))
                sender.sendMessage(line);
        }
    }


    public String get(String node) {
        return get(node, null, null, null, null);
    }

    private String get(String node, Player target, BSShop shop, BSShopHolder holder, BSBuy item) {
        return replace(config.getString(node, node), target, shop, holder, item);
    }

    private String replace(String message, Player target, BSShop shop, BSShopHolder holder, BSBuy item) {
        return ClassManager.manager.getStringManager().transform(message, item, shop, holder, target);
    }


}