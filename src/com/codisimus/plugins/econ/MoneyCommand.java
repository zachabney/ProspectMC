package com.codisimus.plugins.econ;

import com.codisimus.plugins.banker.Banker;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0 && sender instanceof Player) {
            Player player = (Player) sender;
            String playerName = player.getName();
            Account account = Econ.getAccount(playerName);
            account.sync();
            account.updateWallet();
            player.sendMessage("§5Holding: §6" + account.getBalance() + "gp §5Bank: §6" + Banker.getBank(playerName).balance() + "gp");
            return true;
        } else {
            return false;
        }
    }
}
