package net.citizensnpcs.editor;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.util.Messaging;
import net.citizensnpcs.util.Messages;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GenericEquipper implements Equipper {
    @Override
    public void equip(Player equipper, NPC toEquip) {
        ItemStack hand = equipper.getItemInHand();
        Equipment trait = toEquip.getTrait(Equipment.class);
        int slot = 0;
        Material type = hand == null ? Material.AIR : hand.getType();
        // First, determine the slot to edit
        switch (type) {
            case SKULL_ITEM:
            case PUMPKIN:
            case JACK_O_LANTERN:
            case LEATHER_HELMET:
            case CHAINMAIL_HELMET:
            case GOLD_HELMET:
            case IRON_HELMET:
            case DIAMOND_HELMET:
                if (!equipper.isSneaking())
                    slot = 1;
                break;
            case LEATHER_CHESTPLATE:
            case CHAINMAIL_CHESTPLATE:
            case GOLD_CHESTPLATE:
            case IRON_CHESTPLATE:
            case DIAMOND_CHESTPLATE:
                if (!equipper.isSneaking())
                    slot = 2;
                break;
            case LEATHER_LEGGINGS:
            case CHAINMAIL_LEGGINGS:
            case GOLD_LEGGINGS:
            case IRON_LEGGINGS:
            case DIAMOND_LEGGINGS:
                if (!equipper.isSneaking())
                    slot = 3;
                break;
            case LEATHER_BOOTS:
            case CHAINMAIL_BOOTS:
            case GOLD_BOOTS:
            case IRON_BOOTS:
            case DIAMOND_BOOTS:
                if (!equipper.isSneaking())
                    slot = 4;
                break;
            case AIR:
                for (int i = 0; i < 5; i++) {
                    if (trait.get(i) != null && trait.get(i).getType() != Material.AIR) {
                        equipper.getWorld().dropItemNaturally(toEquip.getBukkitEntity().getLocation(), trait.get(i));
                        trait.set(i, null);
                    }
                }
                Messaging.sendTr(equipper, Messages.EQUIPMENT_EDITOR_ALL_ITEMS_REMOVED, toEquip.getName());
                break;
            default:
                break;
        }
        // Drop any previous equipment on the ground
        ItemStack equippedItem = trait.get(slot);
        if (equippedItem != null && equippedItem.getType() != Material.AIR)
            equipper.getWorld().dropItemNaturally(toEquip.getBukkitEntity().getLocation(), equippedItem);

        // Now edit the equipment based on the slot
        if (type != Material.AIR) {
            // Set the proper slot with one of the item
            ItemStack clone = hand.clone();
            clone.setAmount(1);
            trait.set(slot, clone);
            hand.setAmount(hand.getAmount() - 1);
            equipper.setItemInHand(hand);
        }
    }
}
