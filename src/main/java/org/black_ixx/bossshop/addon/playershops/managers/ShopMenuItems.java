package org.black_ixx.bossshop.addon.playershops.managers;

import org.black_ixx.bossshop.addon.playershops.objects.ShopMenuItem;
import org.bukkit.configuration.file.FileConfiguration;

public class ShopMenuItems {

    private ShopMenuItem
            arrow_left,
            arrow_right,
            back,
            close,
            createshop,
            ownshop,
            renting,
            renting_increase,
            renting_limit_reached,
            editshop,
            buyslot,
            selecticon,
            selectinventoryitem_allow,
            selectinventoryitem_deny,
            shoprename,
            saveshop,
            shopinfo;


    public ShopMenuItems(FileConfiguration config) {
        arrow_left = new ShopMenuItem(config, "ArrowLeft");
        arrow_right = new ShopMenuItem(config, "ArrowRight");
        back = new ShopMenuItem(config, "Back");
        close = new ShopMenuItem(config, "Close");
        createshop = new ShopMenuItem(config, "CreateShop");
        ownshop = new ShopMenuItem(config, "OwnShop");
        renting = new ShopMenuItem(config, "Renting");
        renting_increase = new ShopMenuItem(config, "RentingIncrease");
        renting_limit_reached = new ShopMenuItem(config, "RentingLimitReached");
        editshop = new ShopMenuItem(config, "EditShop");
        buyslot = new ShopMenuItem(config, "BuySlot");
        selecticon = new ShopMenuItem(config, "SelectIcon");
        selectinventoryitem_allow = new ShopMenuItem(config, "SelectInventoryItemAllow");
        selectinventoryitem_deny = new ShopMenuItem(config, "SelectInventoryItemDeny");
        shopinfo = new ShopMenuItem(config, "ShopInfo");
        shoprename = new ShopMenuItem(config, "ShopRename");
        saveshop = new ShopMenuItem(config, "SaveShop");
    }


    public ShopMenuItem getArrowLeft() {
        return arrow_left;
    }

    public ShopMenuItem getArrowRight() {
        return arrow_right;
    }

    public ShopMenuItem getBack() {
        return back;
    }

    public ShopMenuItem getClose() {
        return close;
    }

    public ShopMenuItem getCreateShop() {
        return createshop;
    }

    public ShopMenuItem getOwnShop() {
        return ownshop;
    }

    public ShopMenuItem getRenting() {
        return renting;
    }

    public ShopMenuItem getRentingIncrease() {
        return renting_increase;
    }

    public ShopMenuItem getRentingLimitReached() {
        return renting_limit_reached;
    }

    public ShopMenuItem getEditShop() {
        return editshop;
    }

    public ShopMenuItem getBuySlot() {
        return buyslot;
    }

    public ShopMenuItem getSelectIcon() {
        return selecticon;
    }

    public ShopMenuItem getSelectInventoryItemAllow() {
        return selectinventoryitem_allow;
    }

    public ShopMenuItem getSelectInventoryItemDeny() {
        return selectinventoryitem_deny;
    }

    public ShopMenuItem getSaveShop() {
        return saveshop;
    }

    public ShopMenuItem getShopInfo() {
        return shopinfo;
    }

    public ShopMenuItem getShopRename() {
        return shoprename;
    }


}
