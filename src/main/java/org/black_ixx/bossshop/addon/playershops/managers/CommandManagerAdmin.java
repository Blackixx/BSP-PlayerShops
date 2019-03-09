package org.black_ixx.bossshop.addon.playershops.managers;


import java.io.File;

import org.black_ixx.bossshop.addon.playershops.CustomActions;
import org.black_ixx.bossshop.addon.playershops.PlayerShops;
import org.black_ixx.bossshop.addon.playershops.objects.PlayerShop;
import org.black_ixx.bossshop.managers.ClassManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandManagerAdmin implements CommandExecutor {

    private PlayerShops plugin;


    public CommandManagerAdmin(PlayerShops plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (plugin.getShopsManager() != null) {
            if (sender.hasPermission("PlayerShops.Admin")) {


                if (args.length != 0) {
                    String arg = args[0];

                    if (arg.equalsIgnoreCase("create")) {
                        if (args.length == 2) {
                            Player p = Bukkit.getPlayer(args[1]);

                            if (p == null) {
                                ClassManager.manager.getMessageHandler().sendMessage("Main.PlayerNotFound", sender, args[1]);
                                return false;
                            }

                            PlayerShop shop = plugin.getShopsManager().getPlayerShop(p.getUniqueId());
                            if (shop != null) {
                                plugin.getMessages().sendMessage("Admin.PlayerAlreadyOwnsShop", sender, p.getDisplayName());
                                return false;
                            }

                            plugin.getActions().customAction(p, CustomActions.ACTION_CREATE_SHOP);
                            plugin.getMessages().sendMessage("Admin.CreatedShop", sender, p.getDisplayName());
                            return true;

                        } else {
                            plugin.getMessages().sendMessage("Admin.CommandInfoCreateShop", sender);
                            return false;
                        }
                    }

                    if (arg.equalsIgnoreCase("delete")) {
                        if (args.length == 2) {
                            OfflinePlayer p = Bukkit.getPlayer(args[1]);
                            if (p == null) {
                                p = Bukkit.getOfflinePlayer(args[1]);
                            }

                            if (p == null) {
                                ClassManager.manager.getMessageHandler().sendMessage("Main.PlayerNotFound", sender, args[1]);
                                return false;
                            }

                            PlayerShop shop = plugin.getShopsManager().getPlayerShop(p.getUniqueId());
                            if (shop == null) {
                                plugin.getMessages().sendMessage("Admin.PlayerDoesNotOwnShop", sender, p.getName());
                                return false;
                            }

                            shop.delete(new File(plugin.getBossShop().getDataFolder().getPath() + "/addons/PlayerShops/deletedshops"));
                            plugin.getMessages().sendMessage("Admin.DeletedShop", sender, p.getName());
                            shop.close();
                            return true;

                        } else {
                            plugin.getMessages().sendMessage("Admin.CommandInfoDeleteShop", sender);
                            return false;
                        }
                    }


                    if (arg.equalsIgnoreCase("save")) {
                        if (args.length == 2) {
                            Player p = Bukkit.getPlayer(args[1]);
                            if (p == null) {
                                ClassManager.manager.getMessageHandler().sendMessage("Main.PlayerNotFound", sender, args[1]);
                                return false;
                            }

                            PlayerShop shop = plugin.getShopsManager().getPlayerShop(p.getUniqueId());

                            if (shop == null) {
                                plugin.getMessages().sendMessage("Admin.PlayerDoesNotOwnShop", sender, p.getDisplayName());
                                return false;
                            }

                            if (!shop.isBeingEdited()) {
                                plugin.getMessages().sendMessage("Admin.PlayerDoesNotEditShop", sender, p.getDisplayName());
                                return false;
                            }

                            plugin.getActions().customAction(p, CustomActions.ACTION_SAVE_SHOP);
                            plugin.getMessages().sendMessage("Admin.SavedShop", sender, p.getDisplayName());
                            return true;
                        } else {
                            plugin.getMessages().sendMessage("Admin.CommandInfoSaveShop", sender);
                            return false;
                        }
                    }

                }

                plugin.getMessages().sendMessage("Admin.MenuTitle", sender);
                plugin.getMessages().sendMessage("Admin.CommandInfoSaveShop", sender);
                plugin.getMessages().sendMessage("Admin.CommandInfoCreateShop", sender);
                plugin.getMessages().sendMessage("Admin.CommandInfoDeleteShop", sender);

                return false;
            } else {
                ClassManager.manager.getMessageHandler().sendMessage("Main.NoPermission", sender);
                return false;
            }


        }

        return false;
    }


}
