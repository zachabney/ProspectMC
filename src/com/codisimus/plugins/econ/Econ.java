package com.codisimus.plugins.econ;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Codisimus
 */
public class Econ extends JavaPlugin {
    private static HashMap<String, Account> accounts = new HashMap<String, Account>();
    private static Logger logger;
    private static String dataFolder;
    private static Server server;
    private Properties p;

    @Override
    public void onEnable() {
        logger = this.getLogger();
        dataFolder = this.getDataFolder().getPath();
        server = getServer();

        getCommand("money").setExecutor(new MoneyCommand());
        getCommand("grant").setExecutor(new GrantCommand());
        Bukkit.getPluginManager().registerEvents(new Wallet(), this);
        registerEconomy();

        Properties version = new Properties();
        try {
            version.load(this.getResource("version.properties"));
        } catch (Exception ex) {
        }
        logger.info("Econ " + this.getDescription().getVersion()
                + " (Build " + version.getProperty("Build") + ") is enabled!");
    }

    public static Account getAccount(String player) {
        if (!accounts.containsKey(player)) {
            accounts.put(player, new Account(player));
        }
        return accounts.get(player);
    }

    public static ArrayList<String> getAccountNames() {
        return new ArrayList(accounts.keySet());
    }

    /**
     * Register Econ as economy provider for vault.
     */
    private void registerEconomy() {
        if (server.getPluginManager().isPluginEnabled("Vault")) {
            final ServicesManager sm = server.getServicesManager();
            sm.register(Economy.class, new VaultConnector(), this, ServicePriority.Highest);
            logger.info("Registered Vault interface.");
        } else {
            logger.info("Vault not found. Other plugins may not be able to access Econ accounts.");
        }
    }
}
