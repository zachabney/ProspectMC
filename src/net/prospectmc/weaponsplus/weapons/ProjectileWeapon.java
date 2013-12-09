package net.prospectmc.weaponsplus.weapons;

import java.util.Random;

import net.prospectmc.weaponsplus.WeaponsPlus;
import net.prospectmc.weaponsplus.events.WeaponTriggerEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * 
 * @author Zach Abney
 *
 */
public abstract class ProjectileWeapon extends WPWeapon {
	
	abstract Entity projectileLaunch(Location from, Vector velocity);
	
	/**
	 * Launches a projectile from the given location with the given velocity
	 * 
	 * @param from The location to spawn the projectile
	 * @param velocity The velocity applied to the projectile
	 * @return the launched projectile
	 */
	public Entity launchProjectile(Location from, Vector velocity) {
		WeaponTriggerEvent event = new WeaponTriggerEvent(this);
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled()) return null;
		return projectileLaunch(from, velocity);
	}
	
	Vector getThrowableHeading(double par1, double par3, double par5, float par7, float par8) {
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
	
	/**
	 * Launches the weapons projectile for the given player
	 * 
	 * @param player The player launching the projectile
	 * @return the launched projectile
	 */
	public Entity launchProjectile(Player player) {
		Location loc = player.getEyeLocation();
		loc = loc.subtract((double)(Math.cos(player.getLocation().getYaw() / 180F * (float)Math.PI) * 0.16F), player.getLocation().getPitch() >= 66 ? 0.6F : 0, (double)(Math.sin(player.getLocation().getYaw() / 180F * (float)Math.PI) * 0.16F));
		loc = loc.add(player.getVelocity());
		Vector dir = player.getLocation().getDirection();
		loc = loc.add(getThrowableHeading(dir.getX(), dir.getY(), dir.getZ(), 1.5F, 1F));
		player.setVelocity(player.getVelocity().add(player.getLocation().getDirection().multiply(-WeaponsPlus.getKnockback(getName()))));
		return launchProjectile(loc, player.getLocation().getDirection().multiply(WeaponsPlus.getVelocityMultiplier(getName())));
	}
	
}
