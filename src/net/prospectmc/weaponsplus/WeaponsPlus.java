package net.prospectmc.weaponsplus;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import net.prospectmc.attributeapi.Attribute;
import net.prospectmc.attributeapi.AttributeAPI;
import net.prospectmc.weaponsplus.attributes.FireDamage;
import net.prospectmc.weaponsplus.attributes.PotionAttribute;

import net.prospectmc.weaponsplus.commands.CommandHandler;
import net.prospectmc.weaponsplus.commands.WeaponsPlusCommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Zach Abney
 *
 */
public class WeaponsPlus extends JavaPlugin {

	public static final String TYPE = "type";
	public static final int COOLDOWN_ID = 120;
	public static WeaponsPlus plugin;
	private static YamlConfiguration config;

	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		plugin = this;
		try {
			Field f = Enchantment.class.getDeclaredField("acceptingNew");
			f.setAccessible(true);
			f.set(null, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(Enchantment.getById(COOLDOWN_ID) == null) Enchantment.registerEnchantment(new Cooldown(120));
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
		registerWeaponListeners();
                registerAttributes();
		PluginDescriptionFile pdfFile = this.getDescription();
		getLogger().info(pdfFile.getName() + " v" + pdfFile.getVersion() + " by Zach Abney enabled!");
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		getLogger().info(pdfFile.getName() + " by Zach Abney disabled.");
	}

	/**
	 * Registers each Weapon's listener with Bukkit
	 */
	private void registerWeaponListeners() {
		for(Weapon weapon : Weapon.values()) {
			Bukkit.getPluginManager().registerEvents(weapon.getWPWeapon(), this);
		}
	}

	/**
	 * Registers Attributes with AttributeAPI
	 */
	private void registerAttributes() {
                /* Potion Attributes (and their resistance) */
                Attribute att = new Attribute("Poison Resistance", "antipoison");
                AttributeAPI.registerAttribute(att);
                AttributeAPI.registerAttribute(new PotionAttribute("Poison Damage", "poison", att, PotionEffectType.POISON, 1));
                att = new Attribute("Ice Resistance", "antiice");
                AttributeAPI.registerAttribute(att);
                AttributeAPI.registerAttribute(new PotionAttribute("Ice Damage", "ice", att, PotionEffectType.SLOW, 1));
                att = new Attribute("Saturation", "saturation");
                AttributeAPI.registerAttribute(att);
                AttributeAPI.registerAttribute(new PotionAttribute("Exhaustion", "exhaustion", att, PotionEffectType.HUNGER, 1));
                att = new Attribute("Adaptation", "adaptation");
                AttributeAPI.registerAttribute(att);
                AttributeAPI.registerAttribute(new PotionAttribute("Sickness", "sickness", att, PotionEffectType.WITHER, 1));
                att = new Attribute("Sanity", "sanity");
                AttributeAPI.registerAttribute(att);
                AttributeAPI.registerAttribute(new PotionAttribute("Confusion", "confusion", att, PotionEffectType.CONFUSION, 1));
                att = new Attribute("Accommodation", "accommodation");
                AttributeAPI.registerAttribute(att);
                AttributeAPI.registerAttribute(new PotionAttribute("Blindness", "blind", att, PotionEffectType.BLINDNESS, 1));
                /* Fire Damage Attribute */
                att = new Attribute("Fire Resistance", "antifire");
                AttributeAPI.registerAttribute(att);
                AttributeAPI.registerAttribute(new FireDamage("Fire Damage", "fire", att));
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

	/**
	 * Returns the velocity multiplier for the specified item
	 *
	 * @param item The name of the item
	 * @return the velocity multiplier for the item
	 */
	public static float getVelocityMultiplier(String item) {
		return (float) config.getDouble("Weapons." + item + ".Velocity Multiplier");
	}

	/**
	 * Returns the cooldown, in ticks, for the specified item
	 *
	 * @param item The name of the item
	 * @return the cooldown in ticks
	 */
	public static int getCooldownTicks(String item) {
		return config.getInt("Weapons." + item + ".Cooldown (Ticks)");
	}

	/**
	 * Returns the knockback for the specified item
	 *
	 * @param item The name of the item
	 * @return the knockback of the item
	 */
	public static float getKnockback(String item) {
		return (float) config.getDouble("Weapons." + item + ".Knockback");
	}

}
