package net.prospectmc.weaponsplus.weapons;

import net.prospectmc.weaponsplus.Weapon;
import net.prospectmc.weaponsplus.WeaponsPlus;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 
 * @author Zach Abney
 *
 */
public abstract class WPWeapon implements Listener {
	
	/**
	 * Returns the cooldown, in ticks, for the weapon
	 * 
	 * @return the cooldown in ticks
	 */
	public int getCooldown() {
		return WeaponsPlus.getCooldownTicks(getName());
	}
	
	/**
	 * Begins the cooldown timer for the specified item
	 * 
	 * @param stack the item to cooldown
	 */
	@SuppressWarnings("deprecation")
	public void beginCooldown(final ItemStack stack) {
		stack.removeEnchantment(Enchantment.getById(WeaponsPlus.COOLDOWN_ID));
		new BukkitRunnable() {
			@Override
			public void run() {
				stack.addEnchantment(Enchantment.getById(WeaponsPlus.COOLDOWN_ID), 1);
			}
		}.runTaskLater(WeaponsPlus.plugin, WeaponsPlus.getCooldownTicks(getName()));
	}
	
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
	
	/**
	 * Returns whether or not the item has cooled down
	 * 
	 * @param stack the item to check
	 * @return true if the item has cooled down
	 */
	@SuppressWarnings("deprecation")
	public static boolean isCooledDown(ItemStack stack) {
		return stack.containsEnchantment(Enchantment.getById(WeaponsPlus.COOLDOWN_ID));
	}
	
}
