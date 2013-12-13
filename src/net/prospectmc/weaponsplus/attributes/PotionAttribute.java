package net.prospectmc.weaponsplus.attributes;

import net.prospectmc.attributeapi.Attribute;
import net.prospectmc.damageapi.DamageAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * A PotionAttribute applies a potion effect and may or may not have a resistance
 *
 * @author Codisimus
 */
public class PotionAttribute extends Attribute {
    private PotionEffectType potionEffect; //Status effect which this attribute may apply
    private int amplifier; //The power of the status effect

    /**
     * Constructs a new custom Attribute
     *
     * @param name The Full name which will also be the id
     * @param alias The friendly shorthand name
     * @param potionEffect The status effect which this Attribute may cause
     * @param amplifier The power of the status effect
     */
    private PotionAttribute(String name, String alias, PotionEffectType potionEffect, int amplifier) {
        super(name, alias);
        this.potionEffect = potionEffect;
        this.amplifier = amplifier;
    }

    /**
     * Constructs a new custom Attribute
     *
     * @param name The Full name which will also be the id
     * @param alias The friendly shorthand name
     * @param resistance The Attribute which counters this one
     * @param potionEffect The status effect which this Attribute may cause
     * @param amplifier The power of the status effect
     */
    private PotionAttribute(String name, String alias, PotionAttribute resistance, PotionEffectType potionEffect, int amplifier) {
        super(name, alias, resistance);
        this.potionEffect = potionEffect;
        this.amplifier = amplifier;
    }

    /**
     * Returns true because PotionAttributes have effects
     *
     * @return true always
     */
    @Override
    public boolean hasEffect() {
        return true;
    }

    /**
     * Returns true because PotionAttributes depend on efficiency
     *
     * @return true always
     */
    @Override
    public boolean dependantOnEfficiency() {
        return true;
    }

    /**
     * Applies the Potion effect to the given Entity for the specified amount of time
     *
     * @param victim The given Entity
     * @param value The duration of the effect in seconds
     * @param attacker The Entity which caused the effect
     */
    @Override
    public void applyEffect(LivingEntity victim, double value, LivingEntity attacker) {
        //Check if the Attribute is Ice Damage
        if (potionEffect == PotionEffectType.SLOW) {
            DamageAPI.registerDamageCause(attacker, DamageCause.MELTING, victim, 1);
            EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(attacker, victim, DamageCause.MELTING, value);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                double health = victim.getHealth() - event.getDamage();
                if (health < 0) {
                    health = 0;
                }
                victim.setHealth(health);
            }
        }

        int duration = (int) (value * 20);
        PotionEffect effect = getPotionEffect(duration);
        victim.addPotionEffect(effect, true);

        DamageCause cause = getDamageCause();
        if (cause != null) {
            DamageAPI.registerDamageCause(attacker, cause, victim, duration);
        }
    }

    /**
     * Constructs a Potion effect with the given duration
     *
     * @param duration The duration of the effect in seconds
     * @return The new PotionEffect
     */
    public PotionEffect getPotionEffect(int duration) {
        return new PotionEffect(potionEffect, duration, amplifier);
    }

    /**
     * Returns the DamageCause related to the given PotionEffect
     *
     * @param effect The given PotionEffect
     * @return The DamageCause which occurs
     */
    private DamageCause getDamageCause() {
        if (potionEffect == PotionEffectType.POISON) {
            return DamageCause.POISON;
        } else if (potionEffect == PotionEffectType.WITHER) {
            return DamageCause.WITHER;
        } else {
            return null;
        }
    }
}
