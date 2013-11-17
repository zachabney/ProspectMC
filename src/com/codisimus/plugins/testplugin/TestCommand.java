package com.codisimus.plugins.testplugin;

import com.codisimus.plugins.testplugin.CommandHandler.CodCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Executes Player Commands
 *
 * @author Codisimus
 */
public class TestCommand {

    @CodCommand(
        command =  "ping",
        aliases = {"s"},
        usage = {
            "§2<command> §f=§b Find a Player"
        }
    )
    public boolean ping(CommandSender sender, Player player) {
        sender.sendMessage("X: " + player.getLocation().getBlockX());
        sender.sendMessage("Y: " + player.getLocation().getBlockY());
        sender.sendMessage("Z: " + player.getLocation().getBlockZ());
        return true;
    }

    @CodCommand(command =  "health")
    public boolean health(Player player, int x) {
        player.setMaxHealth(x);
        player.setHealth(x);
        player.sendMessage("Health set to " + x);
        return true;
    }

    @CodCommand(command =  "blind")
    public boolean stop(CommandSender sender, Player player) {
        player.setWalkSpeed(0);
        return true;
    }

    @CodCommand(command =  "unblind")
    public boolean go(CommandSender sender, Player player) {
        player.setWalkSpeed(0.2F);
        return true;
    }

    @CodCommand(command =  "double")
    public boolean command(Player player, double x) {
        player.sendMessage(String.valueOf(x));
        return true;
    }

    @CodCommand(command =  "boolean")
    public boolean command(Player player, boolean x) {
        player.sendMessage(String.valueOf(x));
        return true;
    }

    @CodCommand(command =  "integer")
    public boolean command(CommandSender sender, Integer x) {
        sender.sendMessage(String.valueOf(x));
        return true;
    }

    @CodCommand(command =  "mat")
    public boolean command(CommandSender sender, Material mat) {
        sender.sendMessage(mat.toString());
        return true;
    }
}