package com.zta192.AttributeAPI;

import java.lang.reflect.Field;
import java.util.*;
import net.minecraft.server.v1_7_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Zach Abney
 */
public class AttributeAPI extends JavaPlugin {
    private static final HashMap<String, IAttribute> customAttributes = new HashMap<String, IAttribute>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new EntityListener(), this);
        new CommandHandler(this, "attribute").registerCommands(AttributeCommand.class);
        Attribute.init();
    }

    static void addAttribute(Attribute attribute, double min, double max, double defaultValue) {
        customAttributes.put(attribute.getName(), new AttributeRanged(attribute.getAttributeName(), defaultValue, min, max).a(attribute.getName()));
    }

    static Collection<IAttribute> getAttributes() {
        return customAttributes.values();
    }

    public static double getValue(LivingEntity entity, Attribute attribute) {
        EntityLiving e = ((CraftLivingEntity) entity).getHandle();
        if (customAttributes.containsKey(attribute.getName())) {
            return e.bc().a(attribute.getName()).getValue();
        } else {
            return 0;
        }
    }

    public static ItemStack clearModifiers(ItemStack stack) {
        net.minecraft.server.v1_7_R1.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound tag = getTag(nms);
        tag.remove("AttributeModifiers");
        nms.setTag(tag);
        return CraftItemStack.asCraftMirror(nms);
    }

    public static ItemStack addModifier(ItemStack stack, AttributeModifier modifier) {
        net.minecraft.server.v1_7_R1.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound tag = getTag(nms);
        NBTTagList modifiers = tag.hasKey("AttributeModifiers") ? tag.getList("AttributeModifiers", 10) : new NBTTagList();
        modifiers.add(modifier.build());
        tag.set("AttributeModifiers", modifiers);
        nms.setTag(tag);
        return CraftItemStack.asCraftMirror(nms);
    }

    public static LinkedList<AttributeModifier> getModifiers(ItemStack stack) {
        NBTTagCompound parent = getTag(CraftItemStack.asNMSCopy(stack));
        LinkedList<AttributeModifier> attributes = new LinkedList<AttributeModifier>();
        if (!parent.hasKey("AttributeModifiers")) {
            return attributes;
        }
        List<NBTBase> attributeList = getList(parent.getList("AttributeModifiers", 6));
        for (NBTBase base : attributeList) {
            attributes.add(new AttributeModifier((NBTTagCompound) base));
        }
        return attributes;
    }

    private static NBTTagCompound getTag(net.minecraft.server.v1_7_R1.ItemStack nms) {
        return nms.hasTag() ? nms.getTag() : new NBTTagCompound();
    }

    private static <T> List<T> getList(NBTTagList list) {
        try {
            Field listField = NBTTagList.class.getDeclaredField("list");
            listField.setAccessible(true);
            return (List<T>) listField.get(list);
        } catch (Exception e) {
            throw new RuntimeException("Unable to access reflection.", e);
        }
    }
}
