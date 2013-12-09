package net.prospectmc.weaponsplus.weapons;

import net.prospectmc.nmsapi.NMSApi;
import net.prospectmc.weaponsplus.Weapon;
import net.prospectmc.weaponsplus.WeaponsPlus;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

/**
 * 
 * @author Zach Abney
 *
 */
public class IceWand extends ProjectileWeapon {
	
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
	
	@Override
	public void launchProjectile(Location loc, Vector vec) {
		Entity entity = loc.getWorld().spawnEntity(loc, EntityType.SNOWBALL);
		entity.setVelocity(vec);
		loc.getWorld().playSound(loc, Sound.FIZZ, 0.35F, 0.75F);
	}
	
	@EventHandler
	public void onPlayerLeftClick(PlayerInteractEvent event) {
		if(event.getAction() == Action.LEFT_CLICK_AIR && Weapon.getType(event.getPlayer().getItemInHand()) == getType()) {
			launchProjectile(event.getPlayer());
		}
	}
	
}
