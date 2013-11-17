package com.codisimus.plugins.regionapi;

import com.codisimus.plugins.regiontools.CommandHandler;
import com.codisimus.plugins.regiontools.Region;
import com.codisimus.plugins.regiontools.RegionSelector;
import net.prospectmc.Faction;
import org.bukkit.entity.Player;

/**
 *
 * @author Cody
 */
public class RegionAPICommands {
    @CommandHandler.CodCommand(
        command = "faction",
        weight = 1000,
        usage = {
            "§2<command> §f<§6Name§f> =§b Set the faction of a Region"
        },
        permission = "regionapi.facton",
        mustHaveSelection = true
    )
    public boolean faction(Player player, String string) {
        Region region = RegionSelector.getSelection(player);
        try {
            Faction faction = string.equals("none") ? Faction.UNDECIDED : Faction.valueOf(string.toUpperCase());
            RegionAPI.setFaction(region, faction);
            player.sendMessage("§5Region §6" + region.getName() + "§5 is now part of the §6" + faction.name() + "§5 faction.");
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @CommandHandler.CodCommand(
        command = "tier",
        weight = 1001,
        usage = {
            "§2<command> §f<§6Amount§f> =§b Set the tier of a Region"
        },
        permission = "regionapi.tier",
        mustHaveSelection = true
    )
    public boolean tier(Player player, int tier) {
        Region region = RegionSelector.getSelection(player);
        RegionAPI.setTier(region, tier);
        player.sendMessage("§5Region §6" + region.getName() + "§5 is now §6T" + tier);
        return true;
    }
}
