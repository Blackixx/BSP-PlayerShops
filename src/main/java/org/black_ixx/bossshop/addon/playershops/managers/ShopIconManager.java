package org.black_ixx.bossshop.addon.playershops.managers;

import java.util.ArrayList;
import java.util.List;

import org.black_ixx.bossshop.addon.playershops.PlayerShops;
import org.black_ixx.bossshop.addon.playershops.objects.LimitedNode;
import org.black_ixx.bossshop.addon.playershops.objects.PlayerShop;
import org.black_ixx.bossshop.addon.playershops.objects.PlayerShopIcon;
import org.black_ixx.bossshop.addon.playershops.objects.PlayerShopSimple;
import org.black_ixx.bossshop.core.BSBuy;
import org.black_ixx.bossshop.core.BSShop;
import org.black_ixx.bossshop.managers.ClassManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;


public class ShopIconManager {

    private final List<PlayerShopIcon> icons;
    private final boolean use_playerheads;
    private final boolean allow_icon_selection;
    private BSShop iconselection;

    private final LimitedNode inventoryitem;
    private final LimitedNode rename;
    private final LimitedNode renamecolor;


    public ShopIconManager(FileConfiguration config, FileConfiguration iconslist) {
        use_playerheads = config.getBoolean("ShopIcon.UsePlayerHeads");
        allow_icon_selection = config.getBoolean("ShopIcon.AllowIconSelection");
        inventoryitem = new LimitedNode(config.getConfigurationSection("ShopIcon.AllowInventoryItem"));
        rename = new LimitedNode(config.getConfigurationSection("ShopIcon.AllowShopRename"));
        renamecolor = new LimitedNode(config.getConfigurationSection("ShopIcon.ShopRenameAllowColors"));

        icons = new ArrayList<>();

        for (String key : iconslist.getConfigurationSection("List").getKeys(false)) {
            PlayerShopIcon icon = new PlayerShopIcon(iconslist.getConfigurationSection("List." + key));
            icons.add(icon);

        }

    }

    public void setupIconShop(PlayerShops plugin) {
        if (getAllowIconSelection()) {
            iconselection = plugin.getShopCreator().createShopIconSelector(plugin, this, true);

            for (int i = icons.size() - 1; i >= 0; i--) {
                PlayerShopIcon icon = icons.get(i);
                BSBuy buy_a = icon.createShopitemAllow(plugin, icon.getPath());
                iconselection.addShopItem(buy_a, buy_a.getItem(), ClassManager.manager);

                BSBuy buy_d = icon.createShopitemDeny(plugin, icon.getPath());
                iconselection.addShopItem(buy_d, buy_d.getItem(), ClassManager.manager);
            }

            iconselection.finishedAddingItems();
        }
    }


    public boolean isShopIconFix() {
        if (use_playerheads) {
            return true;
        }
        return !allow_icon_selection;
    }

    public BSShop getIconSelectionShop() {
        return iconselection;
    }


    public ItemStack createPlayerheadItem(PlayerShopSimple shop) {
        ItemStack i = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) i.getItemMeta();
        assert meta != null;
        meta.setOwningPlayer(Bukkit.getPlayer(shop.getOwnerName()));
        i.setItemMeta(meta);
        return i;
    }

    public PlayerShopIcon getHighestShopIcon(Player p, PlayerShopSimple shop) {
        for (PlayerShopIcon icon : icons) {
            if (icon.canUse(p, shop)) {
                return icon;
            }
        }
        return null;
    }

    public ItemStack transformName(PlayerShops plugin, PlayerShopSimple shop, ItemStack i, boolean use_default_text, boolean inventoryitem) {
        if (i == null) {
            return null;
        }
        ItemMeta meta = i.getItemMeta();


        assert meta != null;
        String title = meta.getDisplayName();
        if (title == null || use_default_text) {
            title = inventoryitem ? plugin.getMessages().get("ShopIcon.InventoryItemTitle") : plugin.getMessages().get("ShopIcon.DefaultTitle");
        }
        if (shop != null) {
            title = title.replace("%playershopname%", shop.getShopDisplayName());
        }
        meta.setDisplayName(ClassManager.manager.getStringManager().transform(title));

        if (!meta.hasLore() || use_default_text) {
            List<String> lore = new ArrayList<>();
            String desc = inventoryitem ? plugin.getMessages().get("ShopIcon.InventoryItemDescription") : plugin.getMessages().get("ShopIcon.DefaultDescription");
            if (shop != null) {
                desc = desc.replace("%playershopname%", shop.getShopDisplayName());
                desc = desc.replace("%player%", shop.getOwnerName());
            }
            lore.add(ClassManager.manager.getStringManager().transform(desc));
            meta.setLore(lore);
        }

        i.setItemMeta(meta);
        return i;
    }

    public ItemStack getHighestShopIconItem(Player p, PlayerShopSimple shop, boolean can_return_null) {
        if (use_playerheads) {
            return createPlayerheadItem(shop);
        }
        if (!allow_icon_selection) {
            PlayerShopIcon icon = getHighestShopIcon(p, shop);
            if (icon == null) {
                return new ItemStack(Material.DIRT, 1);
            } else {
                return icon.getItem();
            }
        }
        if (can_return_null) {
            return null;
        } else {
            return new ItemStack(Material.DIRT, 1);
        }
    }

    public List<PlayerShopIcon> getIcons() {
        return icons;
    }


    public boolean getUsePlayerHeads() {
        return use_playerheads;
    }


    public boolean getAllowIconSelection() {
        if (use_playerheads) {
            return false;
        }
        return allow_icon_selection;
    }

    public boolean getAllowInventoryItem(Player p, PlayerShop shop) {
        return inventoryitem.meetsRequirements(shop, p);
    }

    public boolean getAllowShopRename(Player p, PlayerShop shop) {
        return rename.meetsRequirements(shop, p);
    }

    public boolean getAllowShopRenameColors(Player p, PlayerShop shop) {
        return renamecolor.meetsRequirements(shop, p);
    }


}
