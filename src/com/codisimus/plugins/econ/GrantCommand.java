package com.codisimus.plugins.econ;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GrantCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2 && args[1].matches("[0-9]+")) {
            Account account = Econ.getAccount(args[0]);
            account.give(Integer.parseInt(args[1]));
            sender.sendMessage("§6" + args[0] + "§5 has been granted §6" + args[1] + "gp §5and is now holding §6" + account.getBalance() + "gp");
            return true;
        } else {
            return false;
        }
    }
}
