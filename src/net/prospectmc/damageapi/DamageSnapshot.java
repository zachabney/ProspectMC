package net.prospectmc.damageapi;

import java.util.ArrayList;
import java.util.HashMap;
import net.prospectmc.attributeapi.Attribute;
import net.prospectmc.attributeapi.AttributeAPI;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

/**
 * A DamageSnapshot holds data of a specific LivingEntity at the time of attacking
 *
 * @author Codisimus
 */
public class DamageSnapshot {
    private static final ArrayList<Attribute> ATTRIBUTES = new ArrayList<Attribute>();
    static Attribute WEAPON_EFFECIENCY;
    static Attribute ARMOR_EFFECIENCY;
    private LivingEntity entity; //The LivingEntity causing damage
    private ItemStack weapon; //The weapon they used to cause the damage
    private HashMap<Attribute, Double> attributeValues = new HashMap<Attribute, Double>(); //A snapshot of relevent attribute values at the time
    private double weaponEfficiency; //A snapshot of their efficiency at the time

    /**
     * Add custom attributes that have status effects locally
     */
    static {
        for (Attribute attribute : AttributeAPI.getAttributes()) {
            if (attribute.hasEffect()) {
                ATTRIBUTES.add(attribute);
            }
        }
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
        weaponEfficiency = AttributeAPI.getValue(entity, WEAPON_EFFECIENCY);
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
    public void applyEffect(LivingEntity entity) {

        for (Attribute attribute : attributeValues.keySet()) {
            //Calculate the Attribute value
            double value = attributeValues.get(attribute);
            if (attribute.hasResistance()) {
                value -= attribute.calculateResistance(entity);
                if (value <= 0) {
                    continue;
                }
            }

            if (attribute.dependantOnEfficiency()) {
                //Roll to see if the effect occurs
                double armorEffeciency = AttributeAPI.getValue(entity, ARMOR_EFFECIENCY);
                if (armorEffeciency >= Math.random() || weaponEfficiency <= Math.random()) {
                    continue;
                }
            }
        }
    }
}
