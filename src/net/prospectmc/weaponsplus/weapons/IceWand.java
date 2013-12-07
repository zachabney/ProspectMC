package net.prospectmc.weaponsplus.weapons;

import net.prospectmc.nmsapi.NMSApi;
import net.prospectmc.weaponsplus.Weapon;
import net.prospectmc.weaponsplus.WeaponsPlus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * 
 * @author Zach Abney
 *
 */
public class IceWand extends WPWeapon {
	
	@Override
	public ItemStack getItem(int amount) {
		ItemStack stack = new ItemStack(Material.IRON_HOE);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(WeaponsPlus.getDisplayName(getName()));
		meta.setLore(WeaponsPlus.getLore(getName()));
		stack.setItemMeta(meta);
		return NMSApi.addNMS("WeaponsPlus", stack, WeaponsPlus.TYPE, getType().name());
	}
	
	@Override
	public String getName() {
		return "Ice Wand";
	}

	@Override
	public Weapon getType() {
		return Weapon.ICE_WAND;
	}
	
}
