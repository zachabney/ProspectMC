package net.prospectmc.damageapi;

import com.zta192.AttributeAPI.Attribute;
import com.zta192.AttributeAPI.AttributeAPI;
import java.util.ArrayList;
import java.util.EnumMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * A DamageSnapshot holds data of a specific LivingEntity at the time of attacking
 *
 * @author Codisimus
 */
public class DamageSnapshot {
    private static final ArrayList<Attribute> ATTRIBUTES = new ArrayList<Attribute>();
    private LivingEntity entity; //The LivingEntity causing damage
    private ItemStack weapon; //The weapon they used to cause the damage
    private EnumMap<Attribute, Double> attributeValues = new EnumMap<Attribute, Double>(Attribute.class); //A snapshot of relevent attribute values at the time
    private double weaponEfficiency; //A snapshot of their efficiency at the time

    /**
     * Add custom attributes that have status effects locally
     */
    static {
        for (Attribute attribute : Attribute.values()) {
            if (attribute.hasPotionEffect()) {
                ATTRIBUTES.add(attribute);
            }
        }
        ATTRIBUTES.add(Attribute.FIRE_DAMAGE);
        ATTRIBUTES.add(Attribute.KNOCKBACK);
    }

    /**
     * Constructs a new DamageSnapshot for the given entity
     *
     * @param entity The given LivingEntity
     */
    public DamageSnapshot(LivingEntity entity) {
        this.entity = entity;
        weapon = entity.getEquipment().getItemInHand();
        for (Attribute attribute : ATTRIBUTES) {
            double value = AttributeAPI.getValue(entity, attribute);
            //Only add attribute values that will matter
            if (value > 0) {
                attributeValues.put(attribute, value);
            }
        }
        weaponEfficiency = AttributeAPI.getValue(entity, Attribute.WEAPON_EFFECIENCY);
    }

    /**
     * Returns the Entity that this snapshot is for
     *
     * @return The LivingEntity that is represented
     */
    public LivingEntity getEntity() {
        return entity;
    }

    /**
     * Returns the weapon that was used
     *
     * @return The ItemStack which was used to cause damage
     */
    public ItemStack getWeapon() {
        return weapon;
    }

    //Too expensive?
    //public EnumSet<Attribute> possibleAttributeEffects(LivingEntity entity) {
    //    EnumSet<Attribute> attributes = EnumSet.noneOf(Attribute.class);
    //    for (Attribute attribute : attributeValues.keySet()) {
    //        double damage = attributeValues.get(attribute);
    //        if (attribute.hasResistance()) {
    //            damage -= AttributeAPI.getValue(entity, attribute.getResistance());
    //        }
    //        if (damage > 0) {
    //            attributes.add(attribute);
    //        }
    //    }
    //    return attributes;
    //}

    /**
     * Applies damage/status effects to the given Entity
     *
     * @param entity The LivingEntity to be damaged
     */
    public void applyDamage(LivingEntity entity) {
        double armorEffeciency = AttributeAPI.getValue(entity, Attribute.WEAPON_EFFECIENCY);

        //Apply Ice Damage
        double damage;
        if (attributeValues.containsKey(Attribute.ICE_DAMAGE)) {
            damage = (attributeValues.get(Attribute.ICE_DAMAGE) - AttributeAPI.getValue(entity, Attribute.ICE_RESISTANCE)) / 5;
            if (damage > 0) {
                DamageAPI.registerDamageCause(this.entity, DamageCause.MELTING, entity, 1);
                EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(this.entity, entity, EntityDamageByEntityEvent.DamageCause.MELTING, damage);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    double health = entity.getHealth() - event.getDamage();
                    if (health < 0) {
                        health = 0;
                    }
                    entity.setHealth(health);
                }
            }
        }

        for (Attribute attribute : attributeValues.keySet()) {
            //Calculate damage
            damage = attributeValues.get(attribute);
            if (attribute.hasResistance()) {
                damage -= AttributeAPI.getValue(entity, attribute.getResistance());
            }
            //Roll to see if the effect occurs
            if (damage > 0 && armorEffeciency < Math.random() && weaponEfficiency > Math.random()) {
                int duration = (int) damage * 20;
                if (attribute.hasPotionEffect()) {
                    PotionEffect effect = attribute.getPotionEffect(duration, 1);
                    entity.addPotionEffect(effect, true);
                    DamageCause cause = getDamageCause(effect);
                    if (cause != null) {
                        DamageAPI.registerDamageCause(this.entity, cause, entity, duration);
                    }
                } else {
                	switch(attribute) {
                	case FIRE_DAMAGE:
                		entity.setFireTicks(duration);
                    	DamageAPI.registerDamageCause(this.entity, DamageCause.FIRE_TICK, entity, duration);
                    	break;
                	case KNOCKBACK:
                			Bukkit.broadcastMessage("Knocking the bitch back");
                			entity.setVelocity(entity.getLocation().getDirection().multiply(-damage));
                		break;
                    default:
                    	break;
                	}
                }
            }
        }
    }

    /**
     * Returns the DamageCause related to the given PotionEffect
     *
     * @param effect The given PotionEffect
     * @return The DamageCause which occurs
     */
    private static DamageCause getDamageCause(PotionEffect effect) {
        if (effect.getType() == PotionEffectType.POISON) {
            return DamageCause.POISON;
        } else if (effect.getType() == PotionEffectType.WITHER) {
            return DamageCause.WITHER;
        } else {
            return null;
        }
    }
}
