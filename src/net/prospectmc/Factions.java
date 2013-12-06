package net.prospectmc;

import java.util.Properties;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Places Players in factions and controls some of their faction based actions
 *
 * @author Cody
 */
public class Factions extends JavaPlugin {
    static Plugin plugin;
    static Logger logger;

    @Override
    public void onEnable() {
        plugin = this;
        logger = getLogger();

        Bukkit.getPluginManager().registerEvents(new FactionsListener(), this);

        Properties version = new Properties();
        try {
            version.load(this.getResource("version.properties"));
        } catch (Exception ex) {
            logger.warning("version.properties file not found within jar");
        }
        logger.info("Factions " + getDescription().getVersion() + " (Build "
                    + version.getProperty("Build") + ") is enabled!");

        for (Player player : Bukkit.getOnlinePlayers()) {
            FactionsListener.playerLoggedOn(player.getName());
        }
    }
}
