package com.zta192.AttributeAPI;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * An Attribute applies to all LivingEntities (unless stated otherwise)
 * Attributes are managed by Minecraft and each Entity as a value for each Attribute
 * This enum provides a cleaner way to reference Minecraft Attributes
 * This enum also includes custom attributes which may have resistance, potion effects
 *
 * @author Codisimus
 */
public enum Attribute {
    /* MINECRAFT */
    //ENUM_NAME(
    //    "full name",
    //    "id name",
    //    "friendly name"
    //),
    MAX_HEALTH(
        "Max Health",
        "generic.maxHealth",
        "health"
    ),
    FOLLOW_RANGE( //Zombies only
        "Follow Range",
        "generic.followRange",
        "follow"
    ),
    KNOCKBACK_RESISTANCE(
        "Knockback Resistance",
        "generic.knockbackResistance",
        "antiknockback"
    ),
    SPEED_MODIFIER(
        "Speed",
        "generic.movementSpeed",
        "speed"
    ),
    ATTACK_DAMAGE(
        "Attack Damage",
        "generic.attackDamage",
        "attack"
    ),
    JUMP_STRENGTH( //Horses only
        "Jump Strength",
        "horse.jumpStrength",
        "jump"
    ),
    SPAWN_REINFORCEMENTS( //Zombies only
        "Spawn Reinforcements",
        "zombie.spawnReinforcements",
        "spawn"
    ),

    /* PROSPECT */
    //ENUM_NAME(
    //    "full/id name",
    //    "friendly name",
    //    "resistance attribute",
    //    "status effect",
    //    "min value",
    //    "max value",
    //    "default value"
    //),
    POISON_RESISTANCE(
        "Poison Resistance",
        "antipoison"
    ),
    POISON_DAMAGE(
        "Poison Damage",
        "poison",
        POISON_RESISTANCE,
        PotionEffectType.POISON
    ),
    ICE_RESISTANCE(
        "Ice Resistance",
        "antiice"
    ),
    ICE_DAMAGE(
        "Ice Damage",
        "ice",
        ICE_RESISTANCE,
        PotionEffectType.SLOW
    ),
    FIRE_RESISTANCE(
        "Fire Resistance",
        "antifire"
    ),
    FIRE_DAMAGE(
        "Fire Damage",
        "fire",
        FIRE_RESISTANCE,
        null
    ),
    SATURATION(
        "Saturation",
        "saturation"
    ),
    EXHAUSTION(
        "Exhaustion",
        "exhaustion",
        SATURATION,
        PotionEffectType.HUNGER
    ),
    ADAPTATION(
        "Adaptation",
        "adaptation"
    ),
    SICKNESS(
        "Sickness",
        "sickness",
        ADAPTATION,
        PotionEffectType.WITHER
    ),
    SANITY(
        "Sanity",
        "sanity"
    ),
    CONFUSION(
        "Confusion",
        "confusion",
        SANITY,
        PotionEffectType.CONFUSION
    ),
    ACCOMODATION(
        "Accommodation",
        "accommodation"
    ),
    BLINDNESS(
        "Blindness",
        "blind",
        ACCOMODATION,
        PotionEffectType.BLINDNESS
    ),
    ARMOR_EFFICIENCY(
        "Armor Efficiency",
        "antieffect",
        0.0,
        1.0,
        0.3
    ),
    WEAPON_EFFECIENCY(
        "Weapon Efficiency",
        "effect",
        ARMOR_EFFICIENCY,
        null,
        0.0,
        1.0,
        0.3
    ),
    KNOCKBACK(
    	"Knockback",
    	"knockback"
    );
    
    /* PROSPECT */
    //ENUM_NAME(
    //    "full/id name",
    //    "friendly name",
    //    "resistance attribute",
    //    "status effect",
    //    "min value",
    //    "max value",
    //    "default value"
    //),

    private String name; //Full name for printing purposes
    private String attributeName; //id that Minecraft/Bukkit uses
    private String alias; //Short friendly name for referencing purposes
    private Attribute resistance; //Counter attribute
    private PotionEffectType potionEffect; //Status effect which this attribute may apply
    private double min = 0.0D;
    private double max = 1.7976931348623157E+308D;
    private double defaultValue = min;

    /**
     * Constructs a new custom Attribute
     *
     * @param name The Full name which will also be the id
     * @param alias The friendly shorthand name
     */
    private Attribute(String name, String alias) {
        this(name, name, alias);
        //Add the attribute to our list of custom attributes
        addAttribute();
    }

    /**
     * Constructs a new custom Attribute
     *
     * @param name The Full name which will also be the id
     * @param alias The friendly shorthand name
     * @param min The minimum range
     * @param max The maximum range
     * @param defaultValue The starting value which must be within the range (inclusive)
     */
    private Attribute(String name, String alias, double min, double max, double defaultValue) {
        this(name, name, alias);
        this.min = min;
        this.max = max;
        this.defaultValue = defaultValue;
        //Add the attribute to our list of custom attributes
        addAttribute();
    }

    /**
     * Constructs a new Attribute
     *
     * @param name The full name
     * @param attributeName The id name
     * @param alias The friendly shorthand name
     */
    private Attribute(String name, String attributeName, String alias) {
        this.name = name;
        this.attributeName = attributeName;
        this.alias = alias;
    }

    /**
     * Constructs a new custom Attribute
     *
     * @param name The Full name which will also be the id
     * @param alias The friendly shorthand name
     * @param resistance The Attribute which counters this one (may be null)
     * @param potionEffect The status effect which this Attribute may cause (may be null)
     * @param min The minimum range
     * @param max The maximum range
     * @param defaultValue The starting value which must be within the range (inclusive)
     */
    private Attribute(String name, String alias, Attribute resistance, PotionEffectType potionEffect, double min, double max, double defaultValue) {
        this.name = name;
        attributeName = name;
        this.alias = alias;
        this.resistance = resistance;
        this.potionEffect = potionEffect;
        this.min = min;
        this.max = max;
        this.defaultValue = defaultValue;
        //Add the attribute to our list of custom attributes
        addAttribute();
    }

    /**
     * Constructs a new custom Attribute
     *
     * @param name The Full name which will also be the id
     * @param alias The friendly shorthand name
     * @param resistance The Attribute which counters this one (may be null)
     * @param potionEffect The status effect which this Attribute may cause (may be null)
     */
    private Attribute(String name, String alias, Attribute resistance, PotionEffectType potionEffect) {
        this.name = name;
        attributeName = name;
        this.alias = alias;
        this.resistance = resistance;
        this.potionEffect = potionEffect;
        addAttribute();
    }

    /**
     * Adds this attribute to our list of custom attributes for referencing later
     */
    private void addAttribute() {
        AttributeAPI.addAttribute(this, min, max, defaultValue);
    }

    /**
     * Returns whether this Attribute is from Minecraft or custom created
     *
     * @return True if this Attribute is not coded into Minecraft
     */
    public boolean isCustom() {
        return AttributeAPI.isCustom(name);
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
        return attributeName;
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
     * Returns whether or not there is a counter attribute to this Attribute
     *
     * @return true if this Attribute has a counter attribute
     */
    public boolean hasResistance() {
        return resistance != null;
    }

    /**
     * Returns the counter attribute
     *
     * @return The counter attribute or null if there isn't one
     */
    public Attribute getResistance() {
        return resistance;
    }

    /**
     * Returns whether or not there is a status effect associated with this Attribute
     *
     * @return true if this Attribute has a status effect
     */
    public boolean hasPotionEffect() {
        return potionEffect != null;
    }

    /**
     * Constructs a Potion effect with the given duration and amplifier
     *
     * @param duration The duration of the effect
     * @param amplifier The strength of the potion (does not apply to all effects)
     * @return The new PotionEffect
     */
    public PotionEffect getPotionEffect(int duration, int amplifier) {
        return new PotionEffect(potionEffect, duration, amplifier);
    }

    /**
     * Finds the Attribute of the given name
     *
     * @param string The full name of the requested Attribute
     * @return The Attribute or null if it is not found
     */
    public static Attribute getAttribute(String name) {
        for (Attribute attribute : values()) {
            if (attribute.name.equals(name)) {
                return attribute;
            }
        }
        return null;
    }

    /**
     * Finds the Attribute of the given alias
     *
     * @param string The alias of the requested Attribute
     * @return The Attribute or null if it is not found
     */
    public static Attribute getAttributeByAlias(String alias) {
        for (Attribute attribute : values()) {
            if (attribute.alias.equals(alias)) {
                return attribute;
            }
        }
        return null;
    }

    /**
     * Initializes the Attribute enum so that custom Attributes are created and stored locally
     */
    static void init() {}
}
