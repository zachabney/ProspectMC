package net.citizensnpcs.api.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import net.citizensnpcs.api.trait.Trait;

import org.bukkit.entity.EntityType;

@Retention(RetentionPolicy.RUNTIME)
public @interface Requirements {
    boolean selected() default false;

    Class<? extends Trait>[] traits() default {};
}