package com.zta192.AttributeAPI;

import com.zta192.AttributeAPI.AttributeModifier.Operation;
import com.zta192.AttributeAPI.CommandHandler.CodCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Executes Player Commands
 *
 * @author Codisimus
 */
public class AttributeCommand {
    @CodCommand(
        command = "&variable",
        usage = {
            "§2<command> <Operation> <Amount> <Attribute> §f=§b Add an Attribute modifier to the item in your hand"
        }
    )
    public boolean addAttribute(Player player, Operation operation, double amount, Attribute attribute) {
        if (player.getItemInHand() == null) {
            return false;
        }
        player.setItemInHand(AttributeAPI.addModifier(player.getItemInHand(), new AttributeModifier(attribute, amount, operation)));
        player.sendMessage("§5The attribute modifier has been added to your item");
        return true;
    }

    @CodCommand(
        command = "list",
        usage = {
            "§2<command> §f=§b Clear Attribute modifiers from the item in your hand"
        }
    )
    public boolean list(CommandSender sender) {
        StringBuilder sb = new StringBuilder();
        for (Attribute attribute : Attribute.values()) {
            sb.append(", ");
            sb.append(attribute.getAlias());
        }
        sender.sendMessage("§5Attributes: §6" + sb.substring(2));
        return true;
    }

    @CodCommand(
        command = "value",
        usage = {
            "§2<command> §f=§b List your value for the specified Attribute"
        }
    )
    public boolean value(Player player, Attribute attribute) {
        player.sendMessage("§5" + attribute.getName() + ": §6" + AttributeAPI.getValue(player, attribute));
        return true;
    }

    @CodCommand(
        command = "clear",
        usage = {
            "§2<command> §f=§b Clear Attribute modifiers from the item in your hand"
        }
    )
    public boolean clear(Player player) {
        if (player.getItemInHand() == null) {
            return false;
        }
        player.setItemInHand(AttributeAPI.clearModifiers(player.getItemInHand()));
        player.sendMessage("§5The attribute modifiers have been been cleared from your item");
        return true;
    }
}
