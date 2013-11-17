package com.codisimus.plugins.regionapi;

import com.codisimus.plugins.regiontools.Region;
import com.codisimus.plugins.regiontools.RegionTools;
import java.io.File;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;
import net.prospectmc.Faction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Loads Plugin and manages Data/Permissions
 *
 * @author Codisimus
 */
public class RegionAPI extends JavaPlugin {
    static Logger logger;
    static File dataFolder;
    static Plugin plugin;

    /**
     * Calls methods to load this Plugin when it is enabled
     */
    @Override
    public void onEnable() {
        logger = getLogger();
        plugin = this;

        /* Create data folders */
        dataFolder = getDataFolder();
        if (!dataFolder.isDirectory()) {
            dataFolder.mkdir();
        }

        /* Register Events */
        Bukkit.getPluginManager().registerEvents(new InhabitorListener(), this);

        RegionTools.getRegionCommand().registerCommands(RegionAPICommands.class);

        Properties version = new Properties();
        try {
            version.load(getResource("version.properties"));
        } catch (Exception ex) {
            logger.warning("version.properties file not found within jar");
        }
        logger.info("RegionAPI " + getDescription().getVersion()
                + " (Build " + version.getProperty("Build") + ") is enabled!");

        new DynmapHook().activate();
    }

    /**
     * Adds the Region to the saved data
     *
     * @param region The Region to add
     */
    public static boolean addRegion(Region region) {
        return RegionTools.addRegion(region);
    }

    /**
     * Removes the Region from the saved data
     *
     * @param region The Region to remove
     */
    public static void removeRegion(Region region) {
        RegionTools.removeRegion(region);
    }

    public static Region findRegion(Location location, boolean deep) {
        return RegionTools.findRegion(location, deep);
    }

    public static Region findRegion(String name, boolean deep) {
        return RegionTools.findRegion(name, deep);
    }

    /**
     * Returns a List of all Regions
     *
     * @return A List of all Regions
     */
    public static ArrayList<Region> getRegions() {
        return RegionTools.getRegions();
    }

    public static Faction getFaction(Region region) {
        String string = (String) region.getData("faction");
        if (string == null) {
            return Faction.UNDECIDED;
        }
        return Faction.valueOf(string);
    }

    public static void setFaction(Region region, Faction faction) {
        region.setData("faction", faction.name());
        RegionTools.saveRegion(region);
    }

    public static int getTier(Region region) {
        Integer tier = (Integer) region.getData("tier");
        if (tier == null) {
            tier = -1;
        }
        return tier;
    }

    public static void setTier(Region region, int tier) {
        region.setData("tier", tier);
        RegionTools.saveRegion(region);
    }
}
