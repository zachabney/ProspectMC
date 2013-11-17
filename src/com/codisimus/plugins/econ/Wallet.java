package com.codisimus.plugins.econ;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author Codisimus
 */
public class Wallet implements Listener {
    static int slot = 8;

    public static void updateWallet(Player player) {
        int amount = Econ.getAccount(player.getName()).getBalance();

        Material mat = Material.GLOWSTONE_DUST;
        if (amount > 0) {
            mat = Material.GLOWSTONE_DUST;
        }
        if (amount >= 100) {
            mat = Material.GOLD_NUGGET;
        }
        if (amount >= 10000) {
            mat = Material.GOLD_INGOT;
        }
        if (amount >= 1000000) {
            mat = Material.GOLD_INGOT;
        }

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(mat);
        meta.setDisplayName("§e" + amount + "gp");
        item.setItemMeta(meta);

        player.getInventory().setItem(slot, item);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getPlayer().getInventory().getHeldItemSlot() == slot) {
            event.getItemDrop().remove();
            updateWallet(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        switch (event.getAction()) {
        case RIGHT_CLICK_AIR:
        case RIGHT_CLICK_BLOCK:
            if (event.getPlayer().getInventory().getHeldItemSlot() == slot
                    && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getSlotType() == SlotType.QUICKBAR && event.getSlot() == slot) {
            event.setResult(Event.Result.DENY);
            HumanEntity human = event.getWhoClicked();
            if (human instanceof Player) {
                ((Player) human).updateInventory();
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        updateWallet(event.getPlayer());
    }
}
