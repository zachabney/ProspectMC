package com.codisimus.plugins.banker;

import com.codisimus.plugins.econ.Account;
import com.codisimus.plugins.econ.Econ;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Cody
 */
public class Bank implements Listener {
    static ArrayList<ItemStack> defaultItems = new ArrayList<ItemStack>(18);

    private String name;
    private int money;
    private ArrayList<Inventory> pages = new ArrayList<Inventory>();
    private int currentPage = 1;
    private boolean openingPage;

    public Bank (String player) {
        name = player;
        newPage();
    }

    public String getName() {
        return name;
    }

    public Bank(FileConfiguration config) {
        name = config.getString("Name");
        ConfigurationSection section = config.getConfigurationSection("Pages");

        for (String key : section.getKeys(false)) {
            Inventory page = Bukkit.createInventory(null, 54, "§6Bank §0- §2PAGE: §e" + currentPage);
            ArrayList<ItemStack> contents = (ArrayList<ItemStack>) section.get(key);
            int i = 0;
            for (ItemStack item : contents) {
                if (item != null) {
                    page.setItem(i, item);
                }
                i++;
            }

            pages.add(page);
            currentPage++;
        }
        currentPage = 1;
    }

    public int balance() {
        return money;
    }

    public void giveMoney(int amount) {
        money += amount;
    }

    public boolean takeMoney(int amount) {
        if (money >= amount) {
            money -= amount;
            return true;
        } else {
            return false;
        }
    }

    public boolean deposit(int amount) {
        if (Econ.getAccount(name).take(amount)) {
            giveMoney(amount);
            return true;
        } else {
            return false;
        }
    }

    public boolean withdraw(int amount) {
        if (takeMoney(amount)) {
            Econ.getAccount(name).give(amount);
            return true;
        } else {
            return false;
        }
    }

    private void nextPage() {
        if (pages.size() > currentPage) {
            currentPage++;
            openPage();
        } else {
            currentPage++;
            if (newPage()) {
                openPage();
            } else {
                currentPage--;
            }
        }
    }

    private void prevPage() {
        if (currentPage > 1) {
            currentPage--;
            openPage();
        }
    }

    public boolean newPage() {
        if (!getPlayer().hasPermission("bankpages." + currentPage)) {
            return false;
        }

        Inventory page = Bukkit.createInventory(null, 54, "§6Bank §0- §2PAGE: §e" + currentPage);
        int i = 36;
        for (ItemStack item : defaultItems) {
            page.setItem(i, item);
            i++;
        }

        pages.add(page);
        return true;
    }

    public void open() {
        Bukkit.getPluginManager().registerEvents(this, Banker.plugin);
        openPage();
    }

    private void openPage() {
        updateBalances();
        final Player player = getPlayer();
        openingPage = true;
        player.closeInventory();
        openingPage = false;
        Bukkit.getScheduler().runTaskLater(Banker.plugin, new Runnable() {
                @Override
                public void run() {
                    player.openInventory(getCurrentPage());
                }
            }, 2);
    }

    private void updateBalances() {
        Inventory page = getCurrentPage();
        Account account = Econ.getAccount(name);

        ItemStack pocket = page.getItem(45);
        ItemMeta meta = pocket.getItemMeta();
        List<String> lore = meta.getLore();
        lore.remove(3);
        lore.add("§e" + account.getBalance() + "gp");
        meta.setLore(lore);
        pocket.setItemMeta(meta);

        ItemStack bank = page.getItem(36);
        meta = bank.getItemMeta();
        lore = meta.getLore();
        lore.remove(2);
        lore.add("§e" + money + "gp");
        meta.setLore(lore);
        bank.setItemMeta(meta);

        update();
    }

    public Player getPlayer() {
        return Bukkit.getPlayerExact(name);
    }

    private Inventory getCurrentPage() {
        return pages.get(currentPage - 1);
    }

    public void update() {
        getPlayer().updateInventory();
    }

    public void close() {
        HandlerList.unregisterAll(this);
        save();
    }

/*** LISTENERS ***/

    /**
     * Triggers trade actions
     *
     * @param event The InventoryClickEvent that occurred
     */
    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerInvClick(InventoryClickEvent event) {
        HumanEntity entity = event.getWhoClicked();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        if (!player.getName().equals(name)) {
            return;
        }

        Inventory inv = event.getInventory();
        if (inv.getType() != InventoryType.CHEST) {
            return;
        }

        int slot = event.getSlot();
        if (slot < 36) {
            return;
        }

        event.setResult(Event.Result.DENY);
        int amount;

        switch (slot) {
        /* Previous Page */
        case 43:
            prevPage();
            return;
        /* Next Page */
        case 44:
            nextPage();
            return;

        /* 1gp */
        case 47:
            amount = 1;
            break;
        /* 10gp */
        case 48:
            amount = 10;
            break;
        /* 100gp */
        case 49:
            amount = 100;
            break;
        /* 1,000gp */
        case 50:
            amount = 1000;
            break;
        /* 10,000gp */
        case 51:
            amount = 10000;
            break;
        /* 100,000gp */
        case 52:
            amount = 100000;
            break;
        /* 1,000,000gp */
        case 53:
            amount = 1000000;
            break;

        /* Unclickable Area */
        default:
            player.updateInventory();
            return;
        }

        Account account = Econ.getAccount(player.getName());
        if (event.isRightClick()) {
            if (event.isShiftClick() || money < amount)  {
                amount = money;
            }
            withdraw(amount);
        } else {
            if (event.isShiftClick() || account.getBalance() < amount)  {
                amount = account.getBalance();
            }
            deposit(amount);
        }

        updateBalances();
    }

    /**
     * Initiates the ending of a Bank session
     *
     * @param event The InventoryCloseEvent that occurred
     */
    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerInvClose(InventoryCloseEvent event) {
        if (openingPage) {
            return;
        }

        HumanEntity entity = event.getPlayer();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        if (!player.getName().equals(name)) {
            return;
        }

        close();
    }

    public void save() {
        try {
            FileConfiguration config = new YamlConfiguration();
            config.set("Name", name);
            ConfigurationSection section = config.createSection("Pages");
            int i = 1;
            for (Inventory inv : pages) {
                section.set(String.valueOf(i), inv.getContents());
                i++;
            }
            config.save(Banker.dataFolder + File.separator + "Accounts" + File.separator + name + ".yml");
        } catch (IOException ex) {
            Banker.logger.severe("Failed to save Bank for " + name);
        }
    }
}
