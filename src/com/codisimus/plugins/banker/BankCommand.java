package com.codisimus.plugins.banker;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BankCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 0 || !(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        Bank bank = Banker.getBank(player.getName());
        bank.open();
        return true;
    }
}
