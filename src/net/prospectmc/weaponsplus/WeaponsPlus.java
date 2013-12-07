package net.prospectmc.weaponsplus;

import java.io.File;
import java.util.List;

import net.prospectmc.weaponsplus.commands.CommandHandler;
import net.prospectmc.weaponsplus.commands.WeaponsPlusCommands;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 
 * @author Zach Abney
 *
 */
public class WeaponsPlus extends JavaPlugin {
	
	public static final String TYPE = "type";
	private static YamlConfiguration config;
	
	@Override
	public void onEnable() {
		getDataFolder().mkdirs();
		File file = new File(getDataFolder() + "/Config.yml");
		if(!file.exists()) saveResource("Config.yml", false);
		config = new YamlConfiguration();
		try {
			config.load(file);
		} catch (Exception e) {
			getLogger().warning("An error occued while trying to load " + getDataFolder() + "/Config.yml");
			e.printStackTrace();
		}
		new CommandHandler(this).registerCommands(WeaponsPlusCommands.class);
		PluginDescriptionFile pdfFile = this.getDescription();
		getLogger().info(pdfFile.getName() + " v" + pdfFile.getVersion() + " by Zach Abney enabled!");
	}
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		getLogger().info(pdfFile.getName() + " by Zach Abney disabled.");
	}
	
	/**
	 * Returns the display name for the specified item
	 * 
	 * @param item The name of the item
	 * @return the display name for the item
	 */
	public static String getDisplayName(String item) {
		return ChatColor.translateAlternateColorCodes('&', config.getString("Weapons." + item + ".Display Name"));
	}
	
	/**
	 * Returns the lore for the specified item
	 * 
	 * @param item The name of the item
	 * @return the lore for the item
	 */
	public static List<String> getLore(String item) {
		@SuppressWarnings("unchecked")
		List<String> lore = (List<String>) config.getList("Weapons." + item + ".Lore");
		for(int i = 0; i < lore.size(); i++) {
			lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
		}
		return lore;
	}
	
}
