package com.zta192.AttributeAPI;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.server.v1_6_R3.NBTBase;
import net.minecraft.server.v1_6_R3.NBTTagCompound;
import net.minecraft.server.v1_6_R3.NBTTagList;

import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 
 * @author Zach Abney
 *
 */
public class AttributeAPI extends JavaPlugin {
	
	@Override
	public void onEnable() {
		
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public static ItemStack addAttribute(ItemStack stack, Attribute attribute) {
		net.minecraft.server.v1_6_R3.ItemStack nms = CraftItemStack.asNMSCopy(stack);
		NBTTagCompound parent = getParent(nms);
		NBTTagList attributes = parent.hasKey("AttributeModifiers") ? parent.getList("AttributeModifiers") : new NBTTagList();
		attributes.add(attribute.build());
		parent.set("AttributeModifiers", attributes);
		nms.setTag(parent);
		return CraftItemStack.asCraftMirror(nms);
	}
	
	public static LinkedList<Attribute> getAttributes(ItemStack stack) {
		net.minecraft.server.v1_6_R3.ItemStack nms = CraftItemStack.asNMSCopy(stack);
		NBTTagCompound parent = getParent(nms);
		LinkedList<Attribute> attributes = new LinkedList<Attribute>();
		if(!parent.hasKey("AttributeModifiers")) return attributes;
		List<NBTBase> attributeList = getList(parent.getList("AttributeModifiers"));
		for(NBTBase base : attributeList) {
			attributes.add(new Attribute((NBTTagCompound) base));
		}
		return attributes;
	}
	
	public static NBTTagCompound getParent(net.minecraft.server.v1_6_R3.ItemStack nms) {
		return nms.getTag() == null ? new NBTTagCompound() : nms.getTag();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> getList(NBTTagList list) {
		try {
			Field listField = NBTTagList.class.getDeclaredField("list");
			listField.setAccessible(true);
			return (List<T>) listField.get(list);
		} catch (Exception e) {
			throw new RuntimeException("Unable to access reflection.", e);
		}
	}
	
}
