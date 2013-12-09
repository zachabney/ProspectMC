package net.prospectmc.weaponsplus.weapons;

import net.prospectmc.weaponsplus.Weapon;

import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 * 
 * @author Zach Abney
 *
 */
public abstract class WPWeapon implements Listener {
	
	/**
	 * Returns an item stack with the specified amount of weapons
	 * 
	 * @param amount the amount of weapons to return
	 * @return an item stack with the specified amount of weapons
	 */
	public abstract ItemStack getItem(int amount);
	
	/**
	 * Returns the enum value of the Weapon
	 * 
	 * @return the enum value of the Weapon
	 */
	public abstract Weapon getType();
	
	/**
	 * Returns the name of the weapon in the config file
	 * 
	 * @return the name of the weapon in the config file
	 */
	public abstract String getName();
	
	/**
	 * Returns an item stack with one weapon
	 * 
	 * @return an item stack with one weapon
	 */
	public ItemStack getItem() {
		return getItem(1);
	}
	
}
