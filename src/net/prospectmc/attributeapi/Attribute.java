package net.prospectmc.attributeapi;

import net.minecraft.server.v1_7_R1.IAttribute;
import org.bukkit.entity.LivingEntity;

/**
 * An Attribute applies to all LivingEntities (unless stated otherwise)
 * Attributes are managed by Minecraft and each Entity as a value for each Attribute
 * Attributes may have resistance and effects
 *
 * @author Codisimus
 */
public class Attribute {
    private String name; //Full name for printing purposes
    private String alias; //Short friendly name for referencing purposes
    private Attribute resistance; //The Attribute to counter this one
    private IAttribute iAttribute; //The NMS of the Attribute

    /**
     * Constructs a new Attribute
     *
     * @param name The Full name which will also be the id
     * @param alias The friendly shorthand name
     */
    public Attribute(String name, String alias) {
        this.name = name;
        this.alias = alias;
        resistance = null;
    }

    /**
     * Constructs a new Attribute with a resistance
     *
     * @param name The Full name which will also be the id
     * @param alias The friendly shorthand name
     * @param resistance The counter Attribute
     */
    public Attribute(String name, String alias, Attribute resistance) {
        this.name = name;
        this.alias = alias;
        this.resistance = resistance;
    }

    /**
     * Returns whether this Attribute is from Minecraft or custom created
     *
     * @return True if this Attribute is not coded into Minecraft
     */
    public boolean isCustom() {
        return true;
    }

    /**
     * Returns the full name of the Attribute
     * This name is user friendly
     *
     * @return The full name of the Attribute
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the id name of the Attribute
     *
     * @return The id name of the Attribute
     */
    public String getAttributeName() {
        return name;
    }

    /**
     * Returns the shorthand name of the Attribute
     * This should not be used for printing to the end-user
     *
     * @return The shorthand name of the Attribute
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Sets the Attribute which counters this one
     *
     * @param resistance The counter Attribute
     */
    public void setResistance(Attribute resistance) {
        this.resistance = resistance;
    }

    /**
     * Returns the Attribute which counters this one
     *
     * @return The counter Attribute
     */
    public Attribute getResistance() {
        return resistance;
    }

    /**
     * Returns true if this attribute has a resistance
     *
     * @return true if this attribute has a resistance
     */
    public boolean hasResistance() {
        return resistance == null;
    }

    /**
     * Returns the amount of resistance that the given Entity has against this Attribute
     *
     * @param entity The given Entity
     * @return The amount of resistance
     */
    public double calculateResistance(LivingEntity entity) {
        return hasResistance()
               ? AttributeAPI.getValue(entity, resistance)
               : 0;
    }

    /**
     * Returns true if this Attribute has an effect
     *
     * @return true if this Attribute has an effect
     */
    public boolean hasEffect() {
        return false;
    }

    /**
     * Returns true if this Attribute may not apply it's effect due to weapon/armor efficiency
     *
     * @return true if this Attribute depends on efficiency
     */
    public boolean dependantOnEfficiency() {
        return false;
    }

    /**
     * Applies the effect of this Attribute to the given Entity
     *
     * @param victim The given Entity
     * @param value The value of the effect which may be power or duration
     * @param attacker The Entity which caused the effect
     */
    public void applyEffect(LivingEntity victim, double value, LivingEntity attacker) {
    }

    /**
     * Sets the IAttribute of this Attribute
     *
     * @param iAttribute the new IAttribute
     */
    void setIAttribute(IAttribute iAttribute) {
        this.iAttribute = iAttribute;
    }

    /**
     * Returns the IAttribute of this Attribute
     *
     * @return iAttribute the NMS Attribute
     */
    IAttribute getIAttribute() {
        return iAttribute;
    }
}
