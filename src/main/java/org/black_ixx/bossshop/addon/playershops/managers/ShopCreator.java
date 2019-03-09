package org.black_ixx.bossshop.addon.playershops.managers;

import org.black_ixx.bossshop.addon.playershops.PlayerShops;
import org.black_ixx.bossshop.addon.playershops.objects.PlayerShop;
import org.black_ixx.bossshop.addon.playershops.objects.PlayerShopItem;
import org.black_ixx.bossshop.core.BSBuy;
import org.black_ixx.bossshop.core.BSShop;
import org.black_ixx.bossshop.core.BSShops;
import org.black_ixx.bossshop.managers.ClassManager;

public class ShopCreator {


    public BSShop createShopIconSelector(PlayerShops plugin, ShopIconManager im, boolean register) {
        BSShop shop = new BSShop(ClassManager.manager.getShops().createId(), "playershops_icons", null, true, plugin.getBossShop(), plugin.getMessages().get("InventoryName.Icons"), 0, null) {
            @Override
            public void reloadShop() {
            }
        };
        shop.setCustomizable(true);

        if (register) {
            ClassManager.manager.getShops().addShop(shop);
        }
        return shop;
    }

    public BSShop createShopList(PlayerShops plugin, BSShops shophandler) {
        BSShop shoplist = new BSShop(shophandler.createId(), "playershops_list", null, true, plugin.getBossShop(), plugin.getMessages().get("InventoryName.ShopList"), 0, null) {
            @Override
            public void reloadShop() {
            }
        };
        shoplist.setCustomizable(true);
        shophandler.addShop(shoplist);
        return shoplist;
    }

    public BSShop createShopEdit(PlayerShop s, boolean register) {
        BSShop shopedit = new BSShop(ClassManager.manager.getShops().createId(), s.getShopEditName(), null, true, ClassManager.manager.getPlugin(), s.getPlugin().getMessages().get("InventoryName.EditShop").replace("%owner%", s.getOwnerName()), 0, null) {
            @Override
            public void reloadShop() {
            }
        };

        int i = 0;
        for (PlayerShopItem item : s.getItems()) {
            BSBuy shopitem = item.createShopItemEdit(s.getPlugin(), String.valueOf(i), shopedit);
            shopedit.addShopItem(shopitem, shopitem.getItem(), ClassManager.manager);
            i++;
        }
        shopedit.setCustomizable(true);
        s.setShopEdit(shopedit);
        shopedit.finishedAddingItems();

        if (register) {
            ClassManager.manager.getShops().addShop(shopedit);
        }

        return shopedit;
    }


    public BSShop createShop(PlayerShop s, boolean register) {
        BSShop shop = new BSShop(ClassManager.manager.getShops().createId(), s.getShopName(), null, true, ClassManager.manager.getPlugin(), s.getPlugin().getMessages().get("InventoryName.PlayerShop").replace("%owner%", s.getOwnerName()), 0, null) {
            @Override
            public void reloadShop() {
            }
        };

        int i = 0;
        for (PlayerShopItem item : s.getItems()) {
            BSBuy shopitem = item.createShopItemNormal(s.getPlugin(), String.valueOf(i), shop);
            shop.addShopItem(shopitem, shopitem.getItem(), ClassManager.manager);
            i++;
        }
        shop.setCustomizable(true);
        s.setShop(shop);
        shop.finishedAddingItems();

        if (register) {
            ClassManager.manager.getShops().addShop(shop);
        }

        return shop;
    }
}
