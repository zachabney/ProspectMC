package net.citizensnpcs.npc;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitFactory;
import net.citizensnpcs.api.trait.TraitInfo;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Inventory;
import net.citizensnpcs.api.trait.trait.Spawned;
import net.citizensnpcs.api.trait.trait.Speech;
import net.citizensnpcs.trait.Anchors;
import net.citizensnpcs.trait.CurrentLocation;
import net.citizensnpcs.trait.Gravity;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.Poses;
import net.citizensnpcs.trait.text.Text;
import net.citizensnpcs.trait.waypoint.Waypoints;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class CitizensTraitFactory implements TraitFactory {
    private final List<TraitInfo> defaultTraits = Lists.newArrayList();
    private final Map<String, TraitInfo> registered = Maps.newHashMap();

    public CitizensTraitFactory() {
        registerTrait(TraitInfo.create(Anchors.class).withName("anchors"));
        registerTrait(TraitInfo.create(Equipment.class).withName("equipment"));
        registerTrait(TraitInfo.create(Gravity.class).withName("gravity"));
        registerTrait(TraitInfo.create(Inventory.class).withName("inventory"));
        registerTrait(TraitInfo.create(CurrentLocation.class).withName("location"));
        registerTrait(TraitInfo.create(LookClose.class).withName("lookclose"));
        registerTrait(TraitInfo.create(Poses.class).withName("poses"));
        registerTrait(TraitInfo.create(Spawned.class).withName("spawned"));
        registerTrait(TraitInfo.create(Speech.class).withName("speech"));
        registerTrait(TraitInfo.create(Text.class).withName("text"));
        registerTrait(TraitInfo.create(Waypoints.class).withName("waypoints"));

        for (String trait : registered.keySet()) {
            INTERNAL_TRAITS.add(trait);
        }
    }

    @Override
    public void addDefaultTraits(NPC npc) {
        for (TraitInfo info : defaultTraits) {
            npc.addTrait(create(info));
        }
    }

    private <T extends Trait> T create(TraitInfo info) {
        return info.tryCreateInstance();
    }

    @Override
    public <T extends Trait> T getTrait(Class<T> clazz) {
        for (TraitInfo entry : registered.values()) {
            if (clazz == entry.getTraitClass())
                return create(entry);
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Trait> T getTrait(String name) {
        TraitInfo info = registered.get(name);
        if (info == null)
            return null;
        return (T) create(info);
    }

    @Override
    public Class<? extends Trait> getTraitClass(String name) {
        TraitInfo info = registered.get(name.toLowerCase());
        return info == null ? null : info.getTraitClass();
    }

    @Override
    public boolean isInternalTrait(Trait trait) {
        return INTERNAL_TRAITS.contains(trait.getName());
    }

    @Override
    public void registerTrait(TraitInfo info) {
        Preconditions.checkNotNull(info, "info cannot be null");
        if (registered.containsKey(info.getTraitName()))
            throw new IllegalArgumentException("trait name already registered");
        registered.put(info.getTraitName(), info);
        if (info.isDefaultTrait()) {
            defaultTraits.add(info);
        }
    }

    private static final Set<String> INTERNAL_TRAITS = Sets.newHashSet();
}