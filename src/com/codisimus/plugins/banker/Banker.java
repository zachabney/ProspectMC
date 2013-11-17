package com.codisimus.plugins.banker;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Codisimus
 */
public class Banker extends JavaPlugin {
    static JavaPlugin plugin;
    static Logger logger;
    static String dataFolder;
    static HashMap<String, Bank> banks = new HashMap<String, Bank>();

    @Override
    public void onEnable () {
        logger = this.getLogger();
        plugin = this;

        File dir = this.getDataFolder();
        if (!dir.isDirectory()) {
            dir.mkdir();
        }
        dataFolder = dir.getPath();

        dir = new File(dataFolder + File.separator + "Accounts");
        if (!dir.isDirectory()) {
            dir.mkdir();
        }

        try {
            File file = new File(dataFolder + File.separator + "BankLayout.yml");
            if (!file.exists()) {
                this.saveResource("BankLayout.yml", true);
            }
            this.getConfig().load(file);
            Bank.defaultItems = (ArrayList<ItemStack>) this.getConfig().get("DefaultInventory");
        } catch (Exception ex) {
            logger.severe("Failed to load Bank Accounts");
            ex.printStackTrace();
        }

        try {
            for (File file : dir.listFiles()) {
                Bank bank = new Bank(YamlConfiguration.loadConfiguration(file));
                banks.put(bank.getName(), bank);
            }
        } catch (Exception ex) {
            logger.severe("Failed to load Bank Accounts");
            ex.printStackTrace();
        }

        getCommand("bank").setExecutor(new BankCommand());

        Properties version = new Properties();
        try {
            version.load(this.getResource("version.properties"));
        } catch (Exception ex) {
        }
        logger.info("Banker " + this.getDescription().getVersion()
                + " (Build " + version.getProperty("Build") + ") is enabled!");
    }

    public static Bank getBank(String player) {
        if (!banks.containsKey(player)) {
            banks.put(player, new Bank(player));
        }
        return banks.get(player);
    }

    public static void openBank(String player) {
        if (!banks.containsKey(player)) {
            banks.put(player, new Bank(player));
        }
        banks.get(player).open();
    }
}
