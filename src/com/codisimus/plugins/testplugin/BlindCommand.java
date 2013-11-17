package com.codisimus.plugins.testplugin;

import com.codisimus.plugins.testplugin.CommandHandler.CodCommand;
import org.bukkit.entity.Player;

/**
 * Executes Player Commands
 *
 * @author Codisimus
 */
public class BlindCommand {
    @CodCommand(command =  "blind")
    public boolean stop(Player player, String[] args) {
        player.setWalkSpeed(0);
        return true;
    }
}
