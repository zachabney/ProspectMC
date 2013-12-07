package net.prospectmc.weaponsplus.weapons;

import net.prospectmc.weaponsplus.Weapon;

import org.bukkit.inventory.ItemStack;

/**
 * 
 * @author Zach Abney
 *
 */
public abstract class WPWeapon {
	
	/**
	 * Returns an item stack with the specified amount of weapons
	 * 
	 * @param amount the amount of weapons to return
	 * @return an item stack with the specified amount of weapons
	 */
	public abstract ItemStack getItem(int amount);
	public abstract Weapon getType();
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
