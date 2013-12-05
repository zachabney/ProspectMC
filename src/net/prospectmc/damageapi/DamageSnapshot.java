package net.prospectmc.damageapi;

import com.zta192.AttributeAPI.Attribute;
import com.zta192.AttributeAPI.AttributeAPI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class DamageSnapshot {
    private static final ArrayList<Attribute> ATTRIBUTES = new ArrayList<Attribute>();
    private LivingEntity entity;
    private ItemStack weapon;
    private EnumMap<Attribute, Double> attributeValues = new EnumMap<Attribute, Double>(Attribute.class);
    private double weaponEfficiency;

    static {
        for (Attribute attribute : Attribute.values()) {
            if (attribute.hasPotionEffect()) {
                ATTRIBUTES.add(attribute);
            }
        }
        ATTRIBUTES.add(Attribute.FIRE_DAMAGE);
    }

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

    public LivingEntity getEntity() {
        return entity;
    }

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

    void applyDamage(LivingEntity entity) {
        double armorEffeciency = AttributeAPI.getValue(entity, Attribute.WEAPON_EFFECIENCY);

        //Apply Ice Damage
        double damage;
        if (attributeValues.containsKey(Attribute.ICE_DAMAGE)) {
            damage = attributeValues.get(Attribute.ICE_DAMAGE) - AttributeAPI.getValue(entity, Attribute.ICE_RESISTANCE);
            if (damage > 0) {
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
            damage = attributeValues.get(attribute);
            if (attribute.hasResistance()) {
                damage -= AttributeAPI.getValue(entity, attribute.getResistance());
            }
            if (damage > 0 && armorEffeciency < Math.random() && weaponEfficiency > Math.random()) {
                int duration = (int) damage * 20;
                if (attribute.hasPotionEffect()) {
                    entity.addPotionEffect(attribute.getPotionEffect(duration, 1), true);
                } else if (attribute == Attribute.FIRE_DAMAGE) {
                    entity.setFireTicks(duration);
                }
            }
        }
    }
}
