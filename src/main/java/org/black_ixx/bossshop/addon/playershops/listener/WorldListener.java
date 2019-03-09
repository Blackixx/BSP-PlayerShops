package org.black_ixx.bossshop.addon.playershops.listener;

import org.black_ixx.bossshop.addon.playershops.PlayerShops;
import org.black_ixx.bossshop.addon.playershops.managers.SaveManager.REASON_SAVE;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldSaveEvent;

public class WorldListener {


    private PlayerShops plugin;
    private long latest;

    public WorldListener(PlayerShops plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void register(WorldSaveEvent event) {
        if (latest + 5000 < System.currentTimeMillis()) {
            plugin.getShopsManager().save(plugin, REASON_SAVE.WORLD_SAVE);
            latest = System.currentTimeMillis();
        }
    }


}
