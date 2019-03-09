package org.black_ixx.bossshop.addon.playershops.types;


import org.black_ixx.bossshop.addon.playershops.PlayerShops;
import org.black_ixx.bossshop.addon.playershops.objects.PlayerShop;
import org.black_ixx.bossshop.core.BSBuy;
import org.black_ixx.bossshop.core.rewards.BSRewardTypeItem;
import org.black_ixx.bossshop.managers.ClassManager;
import org.black_ixx.bossshop.managers.misc.InputReader;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class BSRewardTypePlayerShopIcon extends BSRewardTypeItem {

    private PlayerShops plugin;

    public BSRewardTypePlayerShopIcon(PlayerShops plugin) {
        this.plugin = plugin;
    }


    public Object createObject(Object o, boolean force_final_state) {
        if (force_final_state) {
            return InputReader.readItem(o, false);
        } else {
            return InputReader.readStringList(o);
        }
    }

    public boolean validityCheck(String item_name, Object o) {
        if (o != null) {
            return true;
        }
        ClassManager.manager.getBugFinder().severe("Was not able to create ShopItem " + item_name + "! PlayerShops did something wrong.");
        return false;
    }

    @Override
    public void enableType() {
    }

    @Override
    public boolean canBuy(Player p, BSBuy buy, boolean message_if_no_success, Object reward, ClickType clickType) {
        PlayerShop playershop = plugin.getShopsManager().getPlayerShop(p.getUniqueId());
        if (playershop == null) {
            ClassManager.manager.getBugFinder().severe("[PlayerShops] (RewardType ShopIcon) Unable to detect PlayerShop via Shopitem that is connected to " + buy.getShop());
            return false;
        }

        return true;
    }

    @Override
    public void giveReward(Player p, BSBuy buy, Object reward, ClickType clickType) {
        ItemStack i = (ItemStack) buy.getReward(clickType);

        //Decrease shop item stock
        PlayerShop playershop = plugin.getShopsManager().getPlayerShop(p.getUniqueId());
        playershop.setIcon(i.clone(), true, false, false);
    }

    @Override
    public String getDisplayReward(Player p, BSBuy buy, Object reward, ClickType clickType) {
        ItemStack i = (ItemStack) buy.getReward(clickType);
        String items_formatted = ClassManager.manager.getItemStackTranslator().readItemStack(i);
        return ClassManager.manager.getMessageHandler().get("Display.Item").replace("%items%", items_formatted);
    }

    @Override
    public String[] createNames() {
        return new String[]{"playershopicon"};
    }

    @Override
    public boolean mightNeedShopUpdate() {
        return false;
    }


}
