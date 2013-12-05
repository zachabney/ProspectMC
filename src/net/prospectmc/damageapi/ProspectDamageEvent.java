package net.prospectmc.damageapi;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

public class ProspectDamageEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private LivingEntity victim;
    private Entity attacker;
    private double damage;
    private DamageCause cause;
    private DamageSnapshot damageSnapshot;

    public ProspectDamageEvent(LivingEntity victim, Entity attacker, double damage, DamageCause cause) {
        this.victim = victim;
        this.attacker = attacker;
        this.damage = damage;
        this.cause = cause;
        if (attacker != null && cause != DamageCause.MELTING) {
            damageSnapshot = DamageAPI.getDamageSnapshot(attacker);
        }
    }

    public LivingEntity getVictim() {
        return victim;
    }

    public Player getVictimPlayer() {
        return (Player) victim;
    }

    public Entity getAttacker() {
        return attacker;
    }

    public Player getAttackerPlayer() {
        return (Player) attacker;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public DamageCause getCause() {
        return cause;
    }

    boolean hasSnapshot() {
        return damageSnapshot != null;
    }

    public DamageSnapshot getDamageSnapshot() {
        return damageSnapshot;
    }

    public ItemStack getWeapon() {
        return damageSnapshot.getWeapon();
    }

    public boolean resultsInDeath() {
        return victim.getHealth() <= damage;
    }

    public boolean hasAttacker() {
        return attacker != null;
    }

    public boolean victimIsPlayer() {
        return victim instanceof Player;
    }

    public boolean isPvP() {
        return hasAttacker() && victim instanceof Player && attacker instanceof Player;
    }

    public boolean isCancelled() {
        return cancelled;
    }

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
