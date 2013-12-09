package net.prospectmc.damageapi;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

/**
 * A ProspectDamageEvent is the only damage event you ever need
 * nuff said.
 *
 * @author Codisimus
 */
public class ProspectDamageEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private LivingEntity victim;
    private Entity attacker;
    private double damage;
    private DamageCause cause;
    private DamageSnapshot damageSnapshot;

    /**
     * Constructs a new ProspectDamageEvent
     *
     * @param victim The LivingEntity being damaged
     * @param attacker The Entity causing the damage (Player, Mob, Projectile, etc) may be null
     * @param damage The amount of damage that is occurring
     * @param cause The DamageCause
     */
    public ProspectDamageEvent(LivingEntity victim, Entity attacker, double damage, DamageCause cause) {
        this.victim = victim;
        this.attacker = attacker;
        this.damage = damage;
        this.cause = cause;
        if (attacker != null && !isIceDamage()) {
            damageSnapshot = DamageAPI.getDamageSnapshot(attacker);
        }
    }

    /**
     * Returns the LivingEntity being damaged
     *
     * @return The LivingEntity being damaged
     */
    public LivingEntity getVictim() {
        return victim;
    }

    /**
     * Returns the victim assuming it to be a Player
     *
     * @return The Player who is the victim
     */
    public Player getVictimAsPlayer() {
        return (Player) victim;
    }

    /**
     * Returns true if the damage is from ice damage (status effect)
     *
     * @return true if the damage is from ice
     */
    public final boolean isIceDamage() {
        return cause == DamageCause.MELTING;
    }

    /**
     * Returns true if the damage is from a ranged attack
     *
     * @return true if the damage is from a ranged attack
     */
    public boolean isRangedAttack() {
        return cause == DamageCause.PROJECTILE;
    }

    /**
     * Returns true if the damage is from the effect of an attribute
     *
     * @return true if the damage is from an attribute
     */
    public boolean isAttributeEffect() {
        return DamageAPI.getAttacker(victim, cause) != null;
    }

    /**
     * Returns the Entity causing the damage
     *
     * @return The Entity causing the damage or null if there was no attacker
     */
    public Entity getAttacker() {
        if (hasAttacker()) {
            if (isAttributeEffect()) {
                return DamageAPI.getAttacker(victim, cause);
            } else if (isRangedAttack()) {
                return getShooter();
            }
        }
        return null;
    }

    /**
     * Returns the shooter of the projectile
     *
     * @return The LivingEntity which is represented by the damage snapshot
     */
    public LivingEntity getShooter() {
        return damageSnapshot == null ? null : damageSnapshot.getEntity();
    }

    /**
     * Returns the projectile which cause this event
     * @return The Entity which cause this event
     */
    public Entity getProjectile() {
        return attacker;
    }

    /**
     * Returns the attacker assuming it to be a Player
     *
     * @return The Player who is the attacker
     */
    public Player getAttackerAsPlayer() {
        return (Player) attacker;
    }

    /**
     * Returns the amount of damage which occurred
     *
     * @return The amount of damage which occurred
     */
    public double getDamage() {
        return damage;
    }

    /**
     * Sets the amount of damage to occur
     *
     * @param damage The new amount of damage
     */
    public void setDamage(double damage) {
        this.damage = damage;
    }

    /**
     * Returns the cause of damage
     *
     * @return The DamageCause
     */
    public DamageCause getCause() {
        return cause;
    }

    /**
     * Returns true if this event has a snapshot
     *
     * @return true if this event has a snapshot
     */
    boolean hasSnapshot() {
        return damageSnapshot != null;
    }

    /**
     * Returns the DamageSnapshot for the event
     *
     * @return The DamageSnapshot
     */
    public DamageSnapshot getDamageSnapshot() {
        return damageSnapshot;
    }

    /**
     * Returns the weapon used in this event
     *
     * @return The ItemStack used in this event
     */
    public ItemStack getWeapon() {
        return damageSnapshot.getWeapon();
    }

    /**
     * Returns true if this event will result in the victim being killed
     *
     * @return true if the victim will die
     */
    public boolean resultsInDeath() {
        return victim.getHealth() <= damage;
    }

    /**
     * Returns the killer even if the victim may not be dead
     *
     * @return The LivingEntity causing the death of the victim
     */
    public LivingEntity getKiller() {
        if (victim.getKiller() != null) {
            return victim.getKiller();
        }
        Entity attacker = getAttacker();
        if (attacker != null && attacker instanceof LivingEntity) {
            return (LivingEntity) attacker;
        }
        return null;
    }

    /**
     * Returns true if this event has an attacker
     *
     * @return true if this event has an attacker
     */
    public boolean hasAttacker() {
        return attacker != null;
    }

    /**
     * Returns true if the victim is a Player
     *
     * @return true if the victim is a Player
     */
    public boolean victimIsPlayer() {
        return victim instanceof Player;
    }

    /**
     * Returns true if both the victim and attacker are Players
     *
     * @return true if this event is Player versus Player
     */
    public boolean isPvP() {
        return hasAttacker() && victim instanceof Player && getAttacker() instanceof Player;
    }

    /**
     * Returns true if this event is canceled
     *
     * @return true if this event is canceled
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Cancels this event from occurring
     *
     * @param cancel true if this event should be canceled
     */
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
