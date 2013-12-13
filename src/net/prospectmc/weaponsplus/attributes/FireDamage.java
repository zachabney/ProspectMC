package net.prospectmc.weaponsplus.attributes;

import net.prospectmc.attributeapi.Attribute;
import net.prospectmc.damageapi.DamageAPI;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * FireDamage is an Attribute which ignites enemies in flames
 *
 * @author Codisimus
 */
public class FireDamage extends Attribute {

    /**
     * Constructs a new Attribute with a resistance
     *
     * @param name The Full name which will also be the id
     * @param alias The friendly shorthand name
     * @param resistance The counter Attribute
     */
    public FireDamage(String name, String alias, Attribute resistance) {
        super(name, alias, resistance);
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
     * Ignites the given Entity for the specified amount of time
     *
     * @param victim The given Entity
     * @param value The duration of the effect in seconds
     * @param attacker The Entity which caused the effect
     */
    @Override
    public void applyEffect(LivingEntity victim, double value, LivingEntity attacker) {
        int duration = (int) (value * 20);
        victim.setFireTicks(duration);
        DamageAPI.registerDamageCause(attacker, DamageCause.FIRE_TICK, victim, duration);
    }
}
