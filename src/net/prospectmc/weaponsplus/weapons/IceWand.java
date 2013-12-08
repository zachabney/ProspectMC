package net.prospectmc.weaponsplus.weapons;

import java.util.Random;

import net.prospectmc.nmsapi.NMSApi;
import net.prospectmc.weaponsplus.Weapon;
import net.prospectmc.weaponsplus.WeaponsPlus;
import net.prospectmc.weaponsplus.events.WeaponTriggerEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
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
	
	@Override
	public void playerTrigger(Player player) {
		WeaponTriggerEvent event = new WeaponTriggerEvent(this, player);
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled()) return;
		Location loc = player.getEyeLocation().subtract((double)(Math.cos(player.getEyeLocation().getYaw() / 180F * (float)Math.PI) * 0.16F), (player.getEyeLocation().getPitch() >= 66 ? 0.6 : 0), (double)(Math.sin(player.getEyeLocation().getYaw() / 180F * (float)Math.PI) * 0.16F)).add(player.getVelocity().clone().setY(0));
		double motionX = player.getLocation().getDirection().getX();
		double motionZ = player.getLocation().getDirection().getZ();
		double motionY = player.getLocation().getDirection().getY();
		Entity entity = loc.getWorld().spawnEntity(loc.add(getThrowableHeading(motionX, motionY, motionZ, 1.5F, 1F)), EntityType.SNOWBALL);
		entity.setVelocity(new Vector(motionX, motionY, motionZ).multiply(WeaponsPlus.getVelocityMultiplier(getName())));
		player.getWorld().playSound(player.getLocation(), Sound.FIZZ, 0.35F, 0.75F);
	}
	
	public Vector getThrowableHeading(double par1, double par3, double par5, float par7, float par8) {
		float var9 = (float)Math.sqrt(par1 * par1 + par3 * par3 + par5 * par5);
		par1 /= (double)var9;
		par3 /= (double)var9;
		par5 /= (double)var9;
		Random rand = new Random();
		par1 += rand.nextGaussian() * 0.007499999832361937D * (double)par8;
		par3 += rand.nextGaussian() * 0.007499999832361937D * (double)par8;
		par5 += rand.nextGaussian() * 0.007499999832361937D * (double)par8;
		par1 *= (double)par7;
		par3 *= (double)par7;
		par5 *= (double)par7;
		return new Vector(par1, par3, par5);
	}
	
	@EventHandler
	public void onPlayerLeftClick(PlayerInteractEvent event) {
		if(event.getAction() == Action.LEFT_CLICK_AIR && Weapon.getType(event.getPlayer().getItemInHand()) == getType()) {
			playerTrigger(event.getPlayer());
		}
	}
	
}
