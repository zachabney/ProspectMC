package net.citizensnpcs.trait;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.citizensnpcs.Settings.Setting;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.command.CommandConfigurable;
import net.citizensnpcs.api.command.CommandContext;
import net.citizensnpcs.api.exception.NPCLoadException;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.util.Util;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class LookClose extends Trait implements Toggleable, CommandConfigurable {
    private boolean enabled = Setting.DEFAULT_LOOK_CLOSE.asBoolean();
    private Player lookingAt;
    private double range = Setting.DEFAULT_LOOK_CLOSE_RANGE.asDouble();
    private boolean realisticLooking = Setting.DEFAULT_REALISTIC_LOOKING.asBoolean();

    public LookClose() {
        super("lookclose");
    }

    private boolean canSeeTarget() {
        return realisticLooking ? npc.getBukkitEntity().hasLineOfSight(lookingAt) : true;
    }

    @Override
    public void configure(CommandContext args) {
        range = args.getFlagDouble("range", range);
        range = args.getFlagDouble("r", range);
        realisticLooking = args.hasFlag('r');
    }

    private void findNewTarget() {
        List<Entity> nearby = npc.getBukkitEntity().getNearbyEntities(range, range, range);
        final Location npcLocation = npc.getBukkitEntity().getLocation(NPC_LOCATION);
        Collections.sort(nearby, new Comparator<Entity>() {
            @Override
            public int compare(Entity o1, Entity o2) {
                double d1 = o1.getLocation().distanceSquared(npcLocation);
                double d2 = o2.getLocation().distanceSquared(npcLocation);
                return Double.compare(d1, d2);
            }
        });
        for (Entity entity : nearby) {
            if (entity.getType() != EntityType.PLAYER)
                continue;
            if (CitizensAPI.getNPCRegistry().getNPC(entity) != null)
                continue;
            lookingAt = (Player) entity;
            return;
        }
        lookingAt = null;
    }

    private boolean hasInvalidTarget() {
        if (lookingAt == null)
            return true;
        if (!lookingAt.isOnline() || lookingAt.getWorld() != npc.getBukkitEntity().getWorld()
                || lookingAt.getLocation().distanceSquared(npc.getBukkitEntity().getLocation()) > range) {
            lookingAt = null;
        }
        return lookingAt == null;
    }

    @Override
    public void load(DataKey key) throws NPCLoadException {
        enabled = key.getBoolean("enabled", key.getBoolean(""));
        // TODO: remove key.getBoolean("") ^ after a few updates
        range = key.getDouble("range", range);
        realisticLooking = key.getBoolean("realisticlooking", key.getBoolean("realistic-looking"));
    }

    public void lookClose(boolean lookClose) {
        enabled = lookClose;
    }

    @Override
    public void onDespawn() {
        lookingAt = null;
    }

    @Override
    public void run() {
        if (!enabled || !npc.isSpawned() || npc.getNavigator().isNavigating())
            return;
        if (hasInvalidTarget()) {
            findNewTarget();
        }
        if (lookingAt != null && canSeeTarget()) {
            Util.faceEntity(npc.getBukkitEntity(), lookingAt);
        }
    }

    @Override
    public void save(DataKey key) {
        if (key.keyExists("")) {
            // TODO: remove after a few updates
            key.removeKey("");
        }
        if (key.keyExists("realistic-looking")) {
            // TODO: remove after a few updates
            key.removeKey("realistic-looking");
        }
        key.setBoolean("enabled", enabled);
        key.setDouble("range", range);
        key.setBoolean("realisticlooking", realisticLooking);
    }

    public void setRange(int range) {
        this.range = range;
    }

    public void setRealisticLooking(boolean realistic) {
        this.realisticLooking = realistic;
    }

    @Override
    public boolean toggle() {
        enabled = !enabled;
        return enabled;
    }

    @Override
    public String toString() {
        return "LookClose{" + enabled + "}";
    }

    private static final Location NPC_LOCATION = new Location(null, 0, 0, 0);
}