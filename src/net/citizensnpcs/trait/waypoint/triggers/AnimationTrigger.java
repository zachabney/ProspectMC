package net.citizensnpcs.trait.waypoint.triggers;

import java.util.Collection;
import java.util.List;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.util.PlayerAnimation;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class AnimationTrigger implements WaypointTrigger {
    @Persist(required = true)
    private List<PlayerAnimation> animations;

    public AnimationTrigger() {
    }

    public AnimationTrigger(Collection<PlayerAnimation> collection) {
        animations = Lists.newArrayList(animations);
    }

    @Override
    public String description() {
        return String.format("Animation Trigger [animating %s]", Joiner.on(", ").join(animations));
    }

    @Override
    public void onWaypointReached(NPC npc, Location waypoint) {
        Player player = (Player) npc.getBukkitEntity();
        for (PlayerAnimation animation : animations)
            animation.play(player);
    }
}
