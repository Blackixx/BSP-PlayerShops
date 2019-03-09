package org.black_ixx.bossshop.addon.playershops.objects;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class LimitedNode {

    private int slots;
    private String perm;

    public LimitedNode(ConfigurationSection s) {
        slots = s.getInt("SlotsNeeded");
        perm = s.getString("Permission");
    }


    public boolean meetsRequirements(PlayerShop shop, Player p) {
        if (perm != null) {
            if (!perm.isEmpty()) {
                if (p == null) {
                    return false;
                }
                if (!p.hasPermission(perm)) {
                    return false;
                }
            }
        }
        if (shop.getSlotsAmount(p, true) < slots) {
            return false;
        }
        return true;
    }

}
