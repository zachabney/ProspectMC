package com.codisimus.plugins.regionapi;

import com.codisimus.plugins.regiontools.Region;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import net.prospectmc.Faction;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

public class DynmapHook {
    private static final String infowindow =
            "<div class=\"infowindow\"><span style=\"font-size:120%;\">%regionname%</span><br />"
            + " Faction: <span style=\"font-weight:bold;\">%factionname%</span><br />"
            + " Tier: <span style=\"font-weight:bold;\">%tier%</span><br />"
            + " Player Count: <span style=\"font-weight:bold;\">%playercount%</span><br />"
            + " Status: <span style=\"font-weight:bold;\">%status%</span></div>";
    private MarkerSet set;
    private EnumMap<Faction, AreaStyle> styles = new EnumMap<Faction, AreaStyle>(Faction.class);
    private HashMap<String, AreaMarker> resareas = new HashMap<String, AreaMarker>();

    public void activate() {
        MarkerAPI markerapi = ((DynmapAPI) Bukkit.getPluginManager().getPlugin("dynmap")).getMarkerAPI();
        if (markerapi == null) {
            RegionAPI.logger.severe("Error loading dynmap marker API!");
            return;
        }

        set = markerapi.getMarkerSet("regiontools.markerset");
        if (set == null) {
            set = markerapi.createMarkerSet("regiontools.markerset", "Regions", null, false);
        } else {
            set.setMarkerSetLabel("Regions");
        }
        if (set == null) {
            RegionAPI.logger.severe("Error creating marker set");
            return;
        }
        set.setLayerPriority(10);
        set.setHideByDefault(false);

        styles.put(Faction.EMPIRE, new AreaStyle("#0000FF"));
        styles.put(Faction.SETTLERS, new AreaStyle("#FF0000"));
        styles.put(Faction.UNDECIDED, new AreaStyle("#FFFF00"));

        initializeRegions();
        new BukkitRunnable() {
            @Override
            public void run() {
                updateRegions();
            }
        }.runTaskTimerAsynchronously(RegionAPI.plugin, 20 * 10, 20 * 60);

        RegionAPI.logger.info("Regions will be displayed on the dynmap");
    }

    private String formatInfoWindow(Region r, AreaMarker m) {
        String v = "<div class=\"regioninfo\">" + infowindow + "</div>";
        v = v.replace("%regionname%", m.getLabel());
        v = v.replace("%factionname%", RegionAPI.getFaction(r).name());
        v = v.replace("%tier%", "T" + RegionAPI.getTier(r));
        v = v.replace("%playercount%", String.valueOf(InhabitorListener.getPlayersInRegion(r).size()));
        //v = v.replace("%status%", r.getStatus().name);
        return v;
    }

    private void addStyle(Region r, AreaMarker m) {
        AreaStyle as = styles.get(RegionAPI.getFaction(r));
        int sc = 16711680;
        int fc = 16711680;
        try {
            sc = Integer.parseInt(as.strokecolor.substring(1), 16);
            fc = Integer.parseInt(as.fillcolor.substring(1), 16);
        } catch (NumberFormatException localNumberFormatException) {
        }
        m.setLineStyle(as.strokeweight, as.strokeopacity, sc);
        m.setFillStyle(as.fillopacity, fc);
        if (as.label != null) {
            m.setLabel(as.label);
        }
    }

    private void handleRegion(World world, Region r, Map<String, AreaMarker> newmap) {
        String name = r.getName();
        String path = r.getPath();
        Block l0 = world.getBlockAt(r.getX1(), r.getLowerY(), r.getZ1());
        Block l1 = world.getBlockAt(r.getX2(), r.getUpperY(), r.getZ2());

        LinkedList<Block> border = r.getBorder();
        border.add(border.getFirst());
        double[] x = new double[border.size()];
        double[] z = new double[border.size()];
        int i = 0;
        for (Block block : border) {
            x[i] = block.getX();
            z[i] = block.getZ();
            i++;
        }

        AreaMarker m = (AreaMarker) resareas.remove(path);
        if (m == null) {
            m = set.createAreaMarker(path, name, false, world.getName(), x, z, false);
            if (m == null) {
                return;
            }
            if (l1.getY() > l0.getY()) {
                m.setRangeY(l1.getY() - l0.getY(), l1.getY() - l0.getY());
            } else {
                m.setRangeY(l0.getY() - l1.getY(), l0.getY() - l1.getY());
            }
        } else {
            m.setCornerLocations(x, z);
            m.setLabel(name);
        }

        newmap.put(path, m);
    }

    private void initializeRegions() {
        HashMap<String, AreaMarker> newmap = new HashMap<String, AreaMarker>();

        World w = Bukkit.getWorlds().get(0);
        for (Region r : RegionAPI.getRegions()) {
            if (r.size() > 1) {
                handleRegion(w, r, newmap);
            }
        }

        for (AreaMarker oldm : resareas.values()) {
            oldm.deleteMarker();
        }

        resareas = newmap;
    }

    private void updateRegions() {
        for (Region r : RegionAPI.getRegions()) {
            if (r.size() > 1) {
                AreaMarker m = resareas.get(r.getPath());
                addStyle(r, m);
                m.setDescription(formatInfoWindow(r, m));
            }
        }
    }

    private static class AreaStyle {
        String strokecolor;
        double strokeopacity = 0.8D;
        int strokeweight = 3;
        String fillcolor;
        double fillopacity = 0.35D;
        String label = null;

        AreaStyle(String color) {
            strokecolor = color;
            fillcolor = color;
        }
    }
}
