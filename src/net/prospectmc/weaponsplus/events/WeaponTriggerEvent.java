package net.prospectmc.weaponsplus.events;

import net.prospectmc.weaponsplus.weapons.WPWeapon;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 
 * @author Zach Abney
 *
 */
public class WeaponTriggerEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private final WPWeapon weapon;
	private final Player player;
	private boolean isCancelled = false;
	
	public WeaponTriggerEvent(WPWeapon weapon, Player player) {
		this.weapon = weapon;
		this.player = player;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		isCancelled = cancelled;
	}
	
	/**
	 * Returns the weapon that is firing
	 * 
	 * @return the weapon firing
	 */
	public WPWeapon getWeapon() {
		return weapon;
	}
	
	/**
	 * Returns the player using the weapon
	 * 
	 * @return the player using the weapon
	 */
	public Player getPlayer() {
		return player;
	}

}
