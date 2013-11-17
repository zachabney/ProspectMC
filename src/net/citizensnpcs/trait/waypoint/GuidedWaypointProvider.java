package net.citizensnpcs.trait.waypoint;

import java.util.Iterator;
import java.util.List;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.Goal;
import net.citizensnpcs.api.ai.GoalSelector;
import net.citizensnpcs.api.ai.event.CancelReason;
import net.citizensnpcs.api.ai.event.NavigatorCallback;
import net.citizensnpcs.api.astar.AStarGoal;
import net.citizensnpcs.api.astar.AStarMachine;
import net.citizensnpcs.api.astar.AStarNode;
import net.citizensnpcs.api.astar.Agent;
import net.citizensnpcs.api.astar.Plan;
import net.citizensnpcs.api.command.CommandContext;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.persistence.PersistenceLoader;
import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.api.util.Messaging;
import net.citizensnpcs.api.util.prtree.DistanceResult;
import net.citizensnpcs.api.util.prtree.PRTree;
import net.citizensnpcs.api.util.prtree.Region3D;
import net.citizensnpcs.api.util.prtree.SimplePointND;
import net.citizensnpcs.util.Messages;
import net.citizensnpcs.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class GuidedWaypointProvider implements WaypointProvider {
    private final List<Waypoint> available = Lists.newArrayList();
    private GuidedAIGoal currentGoal;
    private final List<Waypoint> helpers = Lists.newArrayList();
    private NPC npc;
    private boolean paused;
    private PRTree<Region3D<Waypoint>> tree = PRTree.create(new Region3D.Converter<Waypoint>(), 30);

    @Override
    public WaypointEditor createEditor(final Player player, CommandContext args) {
        return new WaypointEditor() {
            private final WaypointMarkers markers = new WaypointMarkers(player.getWorld());
            private boolean showPath;

            @Override
            public void begin() {
                showPath();
                Messaging.sendTr(player, Messages.GUIDED_WAYPOINT_EDITOR_BEGIN);
            }

            private void createWaypointMarkers() {
                for (Waypoint waypoint : Iterables.concat(available, helpers)) {
                    markers.createWaypointMarker(waypoint);
                }
            }

            private void createWaypointMarkerWithData(Waypoint element) {
                Entity entity = markers.createWaypointMarker(element);
                if (entity == null)
                    return;
                entity.setMetadata("citizens.waypointhashcode",
                        new FixedMetadataValue(CitizensAPI.getPlugin(), element.hashCode()));
            }

            @Override
            public void end() {
                Messaging.sendTr(player, Messages.GUIDED_WAYPOINT_EDITOR_END);
                markers.destroyWaypointMarkers();
            }

            @EventHandler(ignoreCancelled = true)
            public void onPlayerChat(AsyncPlayerChatEvent event) {
                if (event.getMessage().equalsIgnoreCase("toggle path")) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(CitizensAPI.getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            togglePath();
                        }
                    });
                }
            }

            @EventHandler(ignoreCancelled = true)
            public void onPlayerInteract(PlayerInteractEvent event) {
                if (!event.getPlayer().equals(player) || event.getAction() == Action.PHYSICAL
                        || event.getClickedBlock() == null)
                    return;
                if (event.getPlayer().getWorld() != npc.getBukkitEntity().getWorld())
                    return;
                event.setCancelled(true);
                Location at = event.getClickedBlock().getLocation();
                Waypoint element = new Waypoint(at);
                if (player.isSneaking()) {
                    available.add(element);
                    Messaging.send(player, Messages.GUIDED_WAYPOINT_EDITOR_ADDED_AVAILABLE);
                } else {
                    helpers.add(element);
                    Messaging.send(player, Messages.GUIDED_WAYPOINT_EDITOR_ADDED_GUIDE);
                }
                createWaypointMarkerWithData(element);
                rebuildTree();
            }

            @EventHandler(ignoreCancelled = true)
            public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
                if (!event.getRightClicked().hasMetadata("citizens.waypointhashcode"))
                    return;
                int hashcode = event.getRightClicked().getMetadata("citizens.waypointhashcode").get(0).asInt();
                Iterator<Waypoint> itr = Iterables.concat(available, helpers).iterator();
                while (itr.hasNext()) {
                    if (itr.next().hashCode() == hashcode) {
                        itr.remove();
                        break;
                    }
                }
            }

            private void showPath() {
                for (Waypoint element : Iterables.concat(available, helpers)) {
                    createWaypointMarkerWithData(element);
                }
            }

            private void togglePath() {
                showPath = !showPath;
                if (showPath) {
                    createWaypointMarkers();
                    Messaging.sendTr(player, Messages.LINEAR_WAYPOINT_EDITOR_SHOWING_MARKERS);
                } else {
                    markers.destroyWaypointMarkers();
                    Messaging.sendTr(player, Messages.LINEAR_WAYPOINT_EDITOR_NOT_SHOWING_MARKERS);
                }
            }
        };
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public void load(DataKey key) {
        for (DataKey root : key.getRelative("availablewaypoints").getIntegerSubKeys()) {
            Waypoint waypoint = PersistenceLoader.load(Waypoint.class, root);
            if (waypoint == null)
                continue;
            available.add(waypoint);
        }
        for (DataKey root : key.getRelative("helperwaypoints").getIntegerSubKeys()) {
            Waypoint waypoint = PersistenceLoader.load(Waypoint.class, root);
            if (waypoint == null)
                continue;
            helpers.add(waypoint);
        }
        rebuildTree();
    }

    @Override
    public void onRemove() {
        npc.getDefaultGoalController().removeGoal(currentGoal);
    }

    @Override
    public void onSpawn(NPC npc) {
        this.npc = npc;
        if (currentGoal == null) {
            currentGoal = new GuidedAIGoal();
            CitizensAPI.registerEvents(currentGoal);
            npc.getDefaultGoalController().addGoal(currentGoal, 1);
        }
    }

    private void rebuildTree() {
        tree = PRTree.create(new Region3D.Converter<Waypoint>(), 30);
        tree.load(Lists.newArrayList(Iterables.transform(Iterables.<Waypoint> concat(available, helpers),
                new Function<Waypoint, Region3D<Waypoint>>() {
                    @Override
                    public Region3D<Waypoint> apply(Waypoint arg0) {
                        Location loc = arg0.getLocation();
                        Vector root = new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                        return new Region3D<Waypoint>(root, root, arg0);
                    }
                })));
    }

    @Override
    public void save(DataKey key) {
        key.removeKey("availablewaypoints");
        DataKey root = key.getRelative("availablewaypoints");
        for (int i = 0; i < available.size(); ++i) {
            PersistenceLoader.save(available.get(i), root.getRelative(i));
        }
        key.removeKey("helperwaypoints");
        root = key.getRelative("helperwaypoints");
        for (int i = 0; i < helpers.size(); ++i) {
            PersistenceLoader.save(helpers.get(i), root.getRelative(i));
        }
    }

    @Override
    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    private class GuidedAIGoal implements Goal {
        private GuidedPlan plan;

        @Override
        public void reset() {
            plan = null;
        }

        @Override
        public void run(GoalSelector selector) {
            if (plan.isComplete()) {
                selector.finish();
                return;
            }
            if (npc.getNavigator().isNavigating()) {
                return;
            }
            Waypoint current = plan.getCurrentWaypoint();
            npc.getNavigator().setTarget(current.getLocation());
            npc.getNavigator().getLocalParameters().addSingleUseCallback(new NavigatorCallback() {
                @Override
                public void onCompletion(CancelReason cancelReason) {
                    plan.update(npc);
                }
            });
        }

        @Override
        public boolean shouldExecute(GoalSelector selector) {
            if (paused || available.size() == 0 || !npc.isSpawned() || npc.getNavigator().isNavigating()) {
                return false;
            }
            Waypoint target = available.get(Util.getFastRandom().nextInt(available.size()));
            plan = ASTAR.runFully(new GuidedGoal(target), new GuidedNode(new Waypoint(npc.getStoredLocation())));
            return plan != null;
        }
    }

    private static class GuidedGoal implements AStarGoal<GuidedNode> {
        private final Waypoint dest;

        public GuidedGoal(Waypoint dest) {
            this.dest = dest;
        }

        @Override
        public float g(GuidedNode from, GuidedNode to) {
            return (float) from.distance(to.waypoint);
        }

        @Override
        public float getInitialCost(GuidedNode node) {
            return h(node);
        }

        @Override
        public float h(GuidedNode from) {
            return (float) from.distance(dest);
        }

        @Override
        public boolean isFinished(GuidedNode node) {
            return node.waypoint.equals(dest);
        }
    }

    private class GuidedNode extends AStarNode {
        private final Waypoint waypoint;

        public GuidedNode(Waypoint waypoint) {
            this.waypoint = waypoint;
        }

        @Override
        public Plan buildPlan() {
            return new GuidedPlan(this.<GuidedNode> getParents());
        }

        public double distance(Waypoint dest) {
            return waypoint.distance(dest);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            GuidedNode other = (GuidedNode) obj;
            if (waypoint == null) {
                if (other.waypoint != null) {
                    return false;
                }
            } else if (!waypoint.equals(other.waypoint)) {
                return false;
            }
            return true;
        }

        @Override
        public Iterable<AStarNode> getNeighbours() {
            List<DistanceResult<Region3D<Waypoint>>> res = tree.nearestNeighbour(Region3D
                    .<Waypoint> distanceCalculator(), Region3D.<Waypoint> alwaysAcceptNodeFilter(), 15,
                    new SimplePointND(waypoint.getLocation().getBlockX(), waypoint.getLocation().getBlockY(), waypoint
                            .getLocation().getBlockZ()));
            return Iterables.transform(res, new Function<DistanceResult<Region3D<Waypoint>>, AStarNode>() {
                @Override
                public AStarNode apply(DistanceResult<Region3D<Waypoint>> arg0) {
                    return new GuidedNode(arg0.get().getData());
                }
            });
        }

        @Override
        public int hashCode() {
            return 31 + ((waypoint == null) ? 0 : waypoint.hashCode());
        }
    }

    private static class GuidedPlan implements Plan {
        private int index = 0;
        private final Waypoint[] path;

        public GuidedPlan(Iterable<GuidedNode> path) {
            this.path = Iterables.toArray(Iterables.transform(path, new Function<GuidedNode, Waypoint>() {
                @Override
                public Waypoint apply(GuidedNode to) {
                    return to.waypoint;
                }
            }), Waypoint.class);
        }

        public Waypoint getCurrentWaypoint() {
            return path[index];
        }

        @Override
        public boolean isComplete() {
            return index >= path.length;
        }

        @Override
        public void update(Agent agent) {
            index++;
        }
    }

    private static final AStarMachine<GuidedNode, GuidedPlan> ASTAR = AStarMachine.createWithDefaultStorage();
}
