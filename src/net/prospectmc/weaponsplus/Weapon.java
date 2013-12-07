package net.prospectmc.weaponsplus;

import net.prospectmc.nmsapi.NMSApi;
import net.prospectmc.weaponsplus.weapons.IceWand;
import net.prospectmc.weaponsplus.weapons.WPWeapon;

import org.bukkit.inventory.ItemStack;

public enum Weapon {
	
	ICE_WAND(new IceWand());
	
	private WPWeapon wpWeapon;
	
	Weapon(WPWeapon wpWeapon) {
		this.wpWeapon = wpWeapon;
	}
	
	/**
	 * Returns the WeaponsPlus weapon
	 * 
	 * @return the WeaponsPlus weapon
	 */
	public WPWeapon getWPWeapon() {
		return wpWeapon;
	}
	
	/**
	 * Returns an item stack of the weapon
	 * 
	 * @param amount the amount of weapons in the stack
	 * @return an item stack of the weapon
	 */
	public ItemStack getItem(int amount) {
		return wpWeapon.getItem(amount);
	}
	
	/**
	 * Returns an item stack with one of the weapon
	 * 
	 * @return an item stack with one of the weapon
	 */
	public ItemStack getItem() {
		return getItem(1);
	}
	
	/**
	 * Returns the weapon type from the specified name
	 * 
	 * @param weaponName the name of the weapon
	 * @return the weapon type from the specified name
	 */
	public static Weapon getWeapon(String weaponName) {
		if(weaponName == null) return null;
		try {
			return valueOf(weaponName.toUpperCase().replace(" ", "_"));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	/**
	 * Returns the type of weapon from the specified item stack
	 * 
	 * @param weapon The item stack to determine the type
	 * @return the type of weapon the item stack is
	 */
	public static Weapon getType(ItemStack weapon) {
		return getWeapon(NMSApi.getNMS("WeaponsPlus", weapon, WeaponsPlus.TYPE, String.class));
	}
	
}
