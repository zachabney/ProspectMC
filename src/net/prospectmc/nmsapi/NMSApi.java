package net.prospectmc.nmsapi;

import net.minecraft.server.v1_7_R1.NBTTagCompound;

import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The NMSApi contains the basic setting, getting, and removing
 * basic NMS variables on ItemStack.
 * 
 * @author Zach Abney
 *
 */
public class NMSApi extends JavaPlugin {
	
	public static final String PLUGIN_COMPOUNDS = "PluginCompounds";
	
	private enum AcceptedNMSValue {
		BOOLEAN, BYTE, DOUBLE, FLOAT, INTEGER, LONG, SHORT, STRING;
	}
	
	/**
	 * Adds NMS variables to the given ItemStack.
	 * <br>
	 * <br>
	 * Note: This will override pervious NMS if it has the same key
	 * 
	 * @param plugin The name of the plugin the variables are associated to
	 * @param stack The ItemStack to apply the NMS
	 * @param key The variable key
	 * @param value The variable value
	 * @return the ItemStack with the NMS variables applied
	 */
	public static ItemStack addNMS(String plugin, ItemStack stack, String key, Object value) {
		net.minecraft.server.v1_7_R1.ItemStack nms = CraftItemStack.asNMSCopy(stack);
		NBTTagCompound tc = nms.getTag();
		if(tc == null) tc = new NBTTagCompound();
		
		NBTTagCompound pluginCompounds = tc.getCompound(PLUGIN_COMPOUNDS);
		if(pluginCompounds == null) pluginCompounds = new NBTTagCompound();
		NBTTagCompound pluginCompound = pluginCompounds.getCompound(plugin);
		if(pluginCompound == null) pluginCompound = new NBTTagCompound();
		
		AcceptedNMSValue nmsValue;
		try {
			nmsValue = AcceptedNMSValue.valueOf(value.getClass().getSimpleName().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid value passed");
		}
		switch(nmsValue) {
		case BOOLEAN:
			pluginCompound.setBoolean(key, (boolean) value);
			break;
		case BYTE:
			pluginCompound.setByte(key, (byte) value);
			break;
		case DOUBLE:
			pluginCompound.setDouble(key, (double) value);
			break;
		case FLOAT:
			pluginCompound.setFloat(key, (float) value);
			break;
		case INTEGER:
			pluginCompound.setInt(key, (int) value);
			break;
		case LONG:
			pluginCompound.setLong(key, (long) value);
			break;
		case SHORT:
			pluginCompound.setShort(key, (short) value);
			break;
		case STRING:
			pluginCompound.setString(key, (String) value);
			break;
		}
		
		pluginCompounds.set(plugin, pluginCompound);
		tc.set(PLUGIN_COMPOUNDS, pluginCompounds);
		nms.setTag(tc);
		return CraftItemStack.asCraftMirror(nms);
	}
	
	/**
	 * Adds NMS variables to the given ItemStack.
	 * <br>
	 * <br>
	 * Note: This will override pervious NMS if it has the same key
	 * 
	 * @param plugin The plugin the variables are associated to
	 * @param stack The ItemStack to apply the NMS
	 * @param key The variable key
	 * @param value The variable value
	 * @return the ItemStack with the NMS variables applied
	 */
	public static ItemStack addNMS(JavaPlugin plugin, ItemStack stack, String key, Object value) {
		return addNMS(plugin.getName(), stack, key, value);
	}
	
	/**
	 * Removes the NMS variable with the specified key
	 * 
	 * @param plugin The name of the plugin associated with the variable
	 * @param stack The ItemStack to remove the NMS
	 * @param key The variable key
	 * @return the ItemStack with the NMS removed
	 */
	public static ItemStack removeNMS(String plugin, ItemStack stack, String key) {
		net.minecraft.server.v1_7_R1.ItemStack nms = CraftItemStack.asNMSCopy(stack);
		NBTTagCompound tc = nms.getTag();
		if(tc == null) return stack;
		NBTTagCompound pluginCompounds = tc.getCompound(PLUGIN_COMPOUNDS);
		if(pluginCompounds == null) return stack;
		NBTTagCompound pluginCompound = pluginCompounds.getCompound(plugin);
		if(pluginCompound == null) return stack;
		pluginCompound.remove(key);
		if(pluginCompound.isEmpty()) pluginCompounds.remove(plugin);
		if(pluginCompounds.isEmpty()) tc.remove(PLUGIN_COMPOUNDS);
		nms.setTag(tc);
		return CraftItemStack.asCraftMirror(nms);
	}
	
	/**
	 * Removes the NMS variable with the specified key
	 * 
	 * @param plugin The plugin associated with the variable
	 * @param stack The ItemStack to remove the NMS
	 * @param key The variable key
	 * @return the ItemStack with the NMS removed
	 */
	public static ItemStack removeNMS(JavaPlugin plugin, ItemStack stack, String key) {
		return removeNMS(plugin.getName(), stack, key);
	}
	
	/**
	 * Returns the NMS value with the specified key for the given ItemStack
	 * 
	 * @param plugin The name of the plugin associated with the NMS variables
	 * @param stack The ItemStack to get the NMS from
	 * @param key The variable key
	 * @param type The type of object to be returned
	 * @return the NMS value with the specified key
	 */
	public static <T> T getNMS(String plugin, ItemStack stack, String key, Class<T> type) {
		AcceptedNMSValue nmsValue;
		try {
			nmsValue = AcceptedNMSValue.valueOf(type.getSimpleName().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid type");
		}
		net.minecraft.server.v1_7_R1.ItemStack nms = CraftItemStack.asNMSCopy(stack);
		NBTTagCompound tc = nms.getTag();
		if(tc == null) return null;
		
		NBTTagCompound pluginCompounds = tc.getCompound(PLUGIN_COMPOUNDS);
		if(pluginCompounds == null) return null;
		NBTTagCompound pluginCompound = pluginCompounds.getCompound(plugin);
		if(pluginCompound == null) return null;
		
		if(!pluginCompound.hasKey(key)) return null;
		
		switch(nmsValue) {
		case BOOLEAN:
			return type.cast(pluginCompound.getBoolean(key));
		case BYTE:
			return type.cast(pluginCompound.getByte(key));
		case DOUBLE:
			return type.cast(pluginCompound.getDouble(key));
		case FLOAT:
			return type.cast(pluginCompound.getFloat(key));
		case INTEGER:
			return type.cast(pluginCompound.getInt(key));
		case LONG:
			return type.cast(pluginCompound.getLong(key));
		case SHORT:
			return type.cast(pluginCompound.getShort(key));
		case STRING:
			return type.cast(pluginCompound.getString(key));
		default:
			return null;
		}
	}
	
	/**
	 * Returns the NMS value with the specified key for the given ItemStack
	 * 
	 * @param plugin The plugin associated with the NMS variables
	 * @param stack The ItemStack to get the NMS from
	 * @param key The variable key
	 * @param type The type of object to be returned
	 * @return the NMS value with the specified key
	 */
	public static <T> T getNMS(JavaPlugin plugin, ItemStack stack, String key, Class<T> type) {
		return getNMS(plugin.getName(), stack, key, type);
	}
	
}
