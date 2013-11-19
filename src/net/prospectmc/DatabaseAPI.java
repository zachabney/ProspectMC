package net.prospectmc;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Handles queries between the Bukkit server and the Database
 *
 * @author Cody
 */
public class DatabaseAPI extends JavaPlugin {
    static Plugin plugin;
    static Logger logger;
    private static String url;
    private static String username;
    private static String password;
    private static Connection con = null;

    @Override
    public void onEnable() {
        plugin = this;
        logger = getLogger();
        saveDefaultConfig();

        FileConfiguration config = getConfig();
        url = "jdbc:mysql://" + config.getString("host") + ':' + config.getString("port")  + '/' + config.getString("database");
        username = config.getString("username");
        password = config.getString("password");

        Bukkit.getPluginManager().registerEvents(new AutoUpdater(), this);

        Properties version = new Properties();
        try {
            version.load(this.getResource("version.properties"));
        } catch (Exception ex) {
            logger.warning("version.properties file not found within jar");
        }
        logger.info("DatabaseAPI " + getDescription().getVersion() + " (Build "
                    + version.getProperty("Build") + ") is enabled!");

        for (Player player : Bukkit.getOnlinePlayers()) {
            AutoUpdater.playerLoggedOn(player.getName());
        }
    }

    /**
     * Executes the given query asynchronously
     * This should be used whenever you do not care about the return value
     *
     * @param query The query to execute
     */
    static void asyncQuery(final String query) {
        new BukkitRunnable() {
            @Override
            public void run() {
                update(query);
            }
        }.runTaskAsynchronously(plugin);
    }

    /**
     * Executes the given query
     *
     * @param query The UPDATE/INSERT/DELETE query to execute
     * @return The number of rows affected
     */
    static int update(String query) {
        refreshConnection();
        try {
            Statement stat = con.createStatement();
            return stat.executeUpdate(query);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException", e);
            return -1;
        }
    }

    /**
     * Executes the given query
     *
     * @param query The query to execute
     * @return The result of the query
     */
    static ResultSet select(String query) {
        refreshConnection();
        try {
            Statement stat = con.createStatement();
            return stat.executeQuery(query);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException", e);
            return null;
        }
    }

    /**
     * Refreshes the connection with the database
     */
    private static void refreshConnection() {
        try {
            if (con == null || !con.isValid(0)) {
                try {
                    con = DriverManager.getConnection(url, username, password);
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, "Error connecting to database", ex);
                }
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Something that should never happen has happened!", ex);
        }
    }
}
