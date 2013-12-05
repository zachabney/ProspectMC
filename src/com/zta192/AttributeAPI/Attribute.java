package com.zta192.AttributeAPI;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum Attribute {
    /* MINECRAFT */
    MAX_HEALTH(
        "Max Health",
        "generic.maxHealth",
        "health"
    ),
    FOLLOW_RANGE(
        "Follow Range",
        "generic.followRange",
        "follow"
    ),
    KNOCKBACK_RESISTANCE(
        "Knockback Resistance",
        "generic.knockbackResistance",
        "knockback"
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
    JUMP_STRENGTH(
        "Jump Strength",
        "horse.jumpStrength",
        "jump"
    ),
    SPAWN_REINFORCEMENTS(
        "Spawn Reinforcements",
        "zombie.spawnReinforcements",
        "spawn"
    ),

    /* PROSPECT */
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
    );

    private String name;
    private String attributeName;
    private String alias;
    private Attribute resistance;
    private PotionEffectType potionEffect;
    private double min = 0.0D;
    private double max = 1.7976931348623157E+308D;
    private double defaultValue = min;

    private Attribute(String name, String alias) {
        this(name, name, alias);
        addAttribute();
    }

    private Attribute(String name, String alias, double min, double max, double defaultValue) {
        this(name, name, alias);
        this.min = min;
        this.max = max;
        this.defaultValue = defaultValue;
        addAttribute();
    }

    private Attribute(String name, String attributeName, String alias) {
        this.name = name;
        this.attributeName = attributeName;
        this.alias = alias;
    }

    private Attribute(String name, String alias, Attribute resistance, PotionEffectType potionEffect, double min, double max, double defaultValue) {
        this.name = name;
        attributeName = name;
        this.alias = alias;
        this.resistance = resistance;
        this.potionEffect = potionEffect;
        this.min = min;
        this.max = max;
        this.defaultValue = defaultValue;
        addAttribute();
    }

    private Attribute(String name, String alias, Attribute resistance, PotionEffectType potionEffect) {
        this.name = name;
        attributeName = name;
        this.alias = alias;
        this.resistance = resistance;
        this.potionEffect = potionEffect;
        addAttribute();
    }

    private void addAttribute() {
        AttributeAPI.addAttribute(this, min, max, defaultValue);
    }

    public String getName() {
        return name;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public String getAlias() {
        return alias;
    }

    public boolean hasResistance() {
        return resistance != null;
    }

    public Attribute getResistance() {
        return resistance;
    }

    public boolean hasPotionEffect() {
        return potionEffect != null;
    }

    public PotionEffect getPotionEffect(int duration, int amplifier) {
        return new PotionEffect(potionEffect, duration, amplifier);
    }

    public static Attribute getAttribute(String string) {
        for (Attribute attribute : values()) {
            if (attribute.name.equals(string)) {
                return attribute;
            }
        }
        return null;
    }

    public static Attribute getAttributeByAlias(String string) {
        for (Attribute attribute : values()) {
            if (attribute.alias.equals(string)) {
                return attribute;
            }
        }
        return null;
    }

    public static void init() {}
}
