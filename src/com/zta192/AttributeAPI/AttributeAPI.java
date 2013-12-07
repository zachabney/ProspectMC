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
 * Provides a non NMS method of accessing and modifying Attributes
 * Also allows for custom Attributes to be created
 *
 * @author Zach Abney
 */
public class AttributeAPI extends JavaPlugin {
    //Holds all custom Attributes (Attribute Name -> Minecraft Attribute Object)
    private static final HashMap<String, IAttribute> customAttributes = new HashMap<String, IAttribute>();

    @Override
    public void onEnable() {
        //Register the EntityListener
        Bukkit.getPluginManager().registerEvents(new EntityListener(), this);
        //Register the /attribute command
        new CommandHandler(this, "attribute").registerCommands(AttributeCommand.class);
        //Initialize all attributes (creates custom attributes so they are ready to add to Entities
        Attribute.init();
    }

    /**
     * Creates an IAttribute for custom Attributes to store locally
     *
     * @param attribute The Attribute to be created
     * @param min The minimum range
     * @param max The maximum range
     * @param defaultValue The starting value which must be within the range
     */
    static void addAttribute(Attribute attribute, double min, double max, double defaultValue) {
        customAttributes.put(attribute.getName(), new AttributeRanged(attribute.getAttributeName(), defaultValue, min, max).a(attribute.getName()));
    }

    /**
     * Returns true if the given Attribute is custom and not in Minecraft by default
     *
     * @param name The name of the attribute
     * @return true if the given Attribute is custom
     */
    static boolean isCustom(String name) {
        return customAttributes.containsKey(name);
    }

    /**
     * Returns a list of all custom Attributes in IAttribute form
     *
     * @return A list of all custom Attributes
     */
    static Collection<IAttribute> getAttributes() {
        return customAttributes.values();
    }

    /**
     * Returns the value that a LivingEntity has for a specific Attribute
     *
     * @param entity The given LivingEntity
     * @param attribute The Attribute to check it's value
     * @return The value of the Attribute or 0 if the Attribute does not apply to all LivingEntities (such as follow range)
     */
    public static double getValue(LivingEntity entity, Attribute attribute) {
        EntityLiving e = ((CraftLivingEntity) entity).getHandle();
        if (isCustom(attribute.getName())) {
            //Custom Attributes must be checked this way or they will break on reload
            return e.bc().a(attribute.getName()).getValue();
        } else {
            //Generic attributes must be checked this way as they are not always in the other collection
            IAttribute iAttribute;
            if (GenericAttributes.a.a().equals(attribute.getName())) {
                iAttribute = GenericAttributes.a;
            } else if (GenericAttributes.b.a().equals(attribute.getName())) {
                iAttribute = GenericAttributes.b;
            } else if (GenericAttributes.c.a().equals(attribute.getName())) {
                iAttribute = GenericAttributes.c;
            } else if (GenericAttributes.d.a().equals(attribute.getName())) {
                iAttribute = GenericAttributes.d;
            } else if (GenericAttributes.e.a().equals(attribute.getName())) {
                iAttribute = GenericAttributes.e;
            } else {
                //The Attribute does not apply to all LivingEntities
                return 0;
            }
            return e.getAttributeInstance(iAttribute).getValue();
        }
    }

    /**
     * Removes all AttributeModifiers from the given ItemStack
     *
     * @param stack The given ItemStack
     * @return The new ITemStack which has no AttributeModifiers
     */
    public static ItemStack clearModifiers(ItemStack stack) {
        net.minecraft.server.v1_7_R1.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound tag = getTag(nms);
        tag.remove("AttributeModifiers");
        nms.setTag(tag);
        return CraftItemStack.asCraftMirror(nms);
    }

    /**
     * Adds an AttributeModifier to the given ItemStack
     *
     * @param stack The given ItemStack
     * @param modifier The AttributeModifier to apply
     * @return The new ItemStack which has the additional modifier
     */
    public static ItemStack addModifier(ItemStack stack, AttributeModifier modifier) {
        net.minecraft.server.v1_7_R1.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound tag = getTag(nms);
        NBTTagList modifiers = tag.hasKey("AttributeModifiers") ? tag.getList("AttributeModifiers", 10) : new NBTTagList();
        modifiers.add(modifier.build());
        tag.set("AttributeModifiers", modifiers);
        nms.setTag(tag);
        return CraftItemStack.asCraftMirror(nms);
    }

    /**
     * Returns a list of all Attribute Modifiers on a given ItemStack
     *
     * @param stack The given ItemStack
     * @return A LinkedList of all modifiers on the ItemStack
     */
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

    /**
     * Returns the tag from an nms ItemStack
     * @param nms The Minecraft ItemStack
     * @return The NBTTagCompound which is the tag of the ItemStack
     */
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
