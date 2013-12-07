package net.prospectmc.weaponsplus.commands;

import net.prospectmc.weaponsplus.Weapon;
import net.prospectmc.weaponsplus.commands.CommandHandler.CommandProperties;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


/**
 * 
 * @author Zach Abney
 *
 */
public class WeaponsPlusCommands {
	
	@CommandProperties(
		command = "wp",
		aliases = {"weaponsplus"},
		description = "Show the help page for WeaponsPlus.")
	public boolean help(CommandSender sender) {
		CommandHandler.getInstance().displayHelp(sender);
		return true;
	}
	
	@CommandProperties(
		command = "help",
		aliases = {"?"},
		parentCommand = "wp",
		description = "Show the help page for WeaponsPlus.",
		usage = {"/wp help"},
		hideFromHelp = true)
	public boolean helpGC(CommandSender sender) {
		return help(sender);
	}
	
	@CommandProperties(
		command = "give",
		parentCommand = "wp",
		description = "Give a player a WeaponsPlus item.",
		usage = {"/wp give [player] [amount] <item>"})
	public boolean give(CommandSender sender, Player player, int amount, Weapon weapon) {
		player.getInventory().addItem(weapon.getItem(amount));
		return true;
	}
	
	@CommandProperties(
		command = "give",
		parentCommand = "wp",
		description = "Give your self a WeaponsPlus item.",
		usage = {"/wp give [amount] <item>"})
	public boolean give(Player player, int amount, Weapon weapon) {
		return give(player, player, amount, weapon);
	}
	
	@CommandProperties(
		command = "give",
		parentCommand = "wp",
		description = "Give your self one WeaponsPlus item.",
		usage = {"/wp give <item>"})
	public boolean give(Player player, Weapon weapon) {
		return give(player, 1, weapon);
	}
	
	@CommandProperties(
		command = "give",
		parentCommand = "wp",
		description = "Give a player one WeaponsPlus item.",
		usage = {"/wp give [player] <item>"})
	public boolean give(CommandSender sender, Player player, Weapon weapon) {
		return give(sender, player, 1, weapon);
	}
	
	@CommandProperties(
		command = "type",
		parentCommand = "wp")
	public boolean whatAmIHolding(Player player) {
		Weapon type = Weapon.getType(player.getItemInHand());
		if(type == null) {
			player.sendMessage("§cThis is not a WeaponsPlus item.");
			return true;
		}
		player.sendMessage(type.name());
		return true;
	}
	
}
