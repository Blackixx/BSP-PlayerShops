package org.black_ixx.bossshop.addon.playershops;

import org.black_ixx.bossshop.addon.playershops.listener.BSListener;
import org.black_ixx.bossshop.addon.playershops.listener.PlayerListener;
import org.black_ixx.bossshop.addon.playershops.listener.SignListener;
import org.black_ixx.bossshop.addon.playershops.managers.CommandManager;
import org.black_ixx.bossshop.addon.playershops.managers.CommandManagerAdmin;
import org.black_ixx.bossshop.addon.playershops.managers.ItemBlacklist;
import org.black_ixx.bossshop.addon.playershops.managers.MessageHandler;
import org.black_ixx.bossshop.addon.playershops.managers.PlayerShopsManager;
import org.black_ixx.bossshop.addon.playershops.managers.SaveManager;
import org.black_ixx.bossshop.addon.playershops.managers.ShopCreator;
import org.black_ixx.bossshop.addon.playershops.managers.ShopIconManager;
import org.black_ixx.bossshop.addon.playershops.managers.ShopMenuItems;
import org.black_ixx.bossshop.addon.playershops.managers.SaveManager.REASON_SAVE;
import org.black_ixx.bossshop.api.BSAddonConfig;
import org.black_ixx.bossshop.api.BossShopAddonConfigurable;
import org.black_ixx.bossshop.managers.config.FileHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;


public class PlayerShops extends BossShopAddonConfigurable {


    private PlayerShopsManager manager;
    private Settings settings;
    private ShopMenuItems items;
    private BSListener bslistener;
    private PlayerListener playerlistener;
    private SignListener signlistener;
    private CustomActions actions;
    private CommandManager commandmanager;
    private SaveManager savemanager;
    private ShopCreator shopcreator;
    private ShopIconManager iconmanager;
    private MessageHandler messages;
    private ItemBlacklist blacklist;


    @Override
    public void enableAddon() {
        shopcreator = new ShopCreator();
        bslistener = new BSListener(this);
        Bukkit.getPluginManager().registerEvents(bslistener, this);
        playerlistener = new PlayerListener(this);
        Bukkit.getPluginManager().registerEvents(playerlistener, this);
        signlistener = new SignListener(this);
        Bukkit.getPluginManager().registerEvents(signlistener, this);
        commandmanager = new CommandManager(this);
        getCommand("ps").setExecutor(commandmanager);
        getCommand("psadmin").setExecutor(new CommandManagerAdmin(this));
        savemanager = new SaveManager(this);
    }

    @Override
    public void bossShopFinishedLoading() { //Executed on reload too
        load();
    }

    @Override
    public void disableAddon() {
        manager.save(this, REASON_SAVE.SERVER_UNLOAD);
        manager = null;
    }

    @Override
    public void bossShopReloaded(CommandSender s) {
        manager.save(this, REASON_SAVE.SERVER_RELOAD);
    }

    public void load() {
        new FileHandler().copyDefaultsFromJar(this, "config.yml");
        new FileHandler().copyDefaultsFromJar(this, "messages.yml");
        BSAddonConfig iconsconfig = new BSAddonConfig(this, "icons");
        if (!iconsconfig.getFile().exists()) {
            new FileHandler().copyDefaultsFromJar(this, "look.yml"); //create once only because buttons can be removed from file
            new FileHandler().copyDefaultsFromJar(this, "icons.yml");
            iconsconfig.reload();
        }
        new FileHandler().copyDefaultsFromJar(this, "itemblacklist.yml");
        reloadConfig();
        messages = new MessageHandler(this, new BSAddonConfig(this, "messages"));
        blacklist = new ItemBlacklist(new BSAddonConfig(this, "itemblacklist").getConfig());
        settings = new Settings(getConfig());
        items = new ShopMenuItems(new BSAddonConfig(this, "look").getConfig());
        iconmanager = new ShopIconManager(getConfig(), iconsconfig.getConfig());
        actions = new CustomActions(this);
        manager = new PlayerShopsManager();
        manager.init(this);
        iconmanager.setupIconShop(this);
    }

    @Override
    public String getAddonName() {
        return "PlayerShops";
    }

    @Override
    public String getRequiredBossShopVersion() {
        return "1.9.5";
    }

    @Override
    public boolean saveConfigOnDisable() {
        return false;
    }


    public Settings getSettings() {
        return settings;
    }

    public MessageHandler getMessages() {
        return messages;
    }

    public ShopMenuItems getItems() {
        return items;
    }

    public PlayerShopsManager getShopsManager() {
        return manager;
    }

    public BSListener getBossShopListener() {
        return bslistener;
    }

    public SignListener getSignListener() {
        return signlistener;
    }

    public CustomActions getActions() {
        return actions;
    }

    public SaveManager getSaveManager() {
        return savemanager;
    }

    public ShopCreator getShopCreator() {
        return shopcreator;
    }

    public ShopIconManager getIconManager() {
        return iconmanager;
    }

    public CommandManager getCommandManager() {
        return commandmanager;
    }

    public ItemBlacklist getBlacklist() {
        return blacklist;
    }
}
