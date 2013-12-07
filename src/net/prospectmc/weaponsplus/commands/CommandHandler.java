package net.prospectmc.weaponsplus.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeSet;

import net.prospectmc.weaponsplus.Weapon;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.ChatPaginator;

/**
 * A manager to handle commands and validation.
 * 
 * @author Zach Abney
 *
 */
public class CommandHandler implements CommandExecutor {
	
	public final JavaPlugin plugin;
	private static CommandHandler instance;
	
	private HashMap<String, TreeSet<Method>> methods = new HashMap<String, TreeSet<Method>>();
	private HashMap<String, CommandProperties> metas = new HashMap<String, CommandProperties>();
	
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface CommandProperties {
		
		String command();
		String[] aliases() default {};
		String[] usage() default {"<command>"};
		String description() default "";
		String parentCommand() default "";
		String[] permissions() default {};
		int weight() default 1;
		int minArgs() default -1;
		int maxArgs() default -1;
		boolean hideFromHelp() default false;
		
	}
	
	private enum ParameterType {
		STRING, INT, BOOLEAN, PLAYER, MATERIAL, WEAPON;
		
		public static ParameterType getType(Class<?> parameter) {
			try {
				return ParameterType.valueOf(parameter.getSimpleName().toUpperCase());
			} catch (Exception e) {
				return null;
			}
		}
		
	}
	
	public CommandHandler(JavaPlugin plugin) {
		this.plugin = plugin;
		instance = this;
	}
	
	/**
	 * Registers the methods with the {@literal @}CommandProperties annotation
	 * to use for a command.
	 * 
	 * @param commandClass The class that contains the methods to register
	 */
	public void registerCommands(Class<?> commandClass) {
		for(Method method : commandClass.getMethods()) {
			CommandProperties annotation = method.getAnnotation(CommandProperties.class);
			if(annotation == null) continue;
			boolean groupedCommand = !annotation.parentCommand().isEmpty();
			if(method.getReturnType() != boolean.class) {
				plugin.getLogger().warning("The method " + method.getName() + " does not return a boolean.");
				continue;
			}
			if(!method.getParameterTypes()[0].equals(method.getParameterTypes()[0].equals(Player.class) ? Player.class : CommandSender.class)) {
				plugin.getLogger().warning("The method " + method.getName() + " should have "
						+ (method.getParameterTypes()[0].equals(Player.class) ? "Player" : "CommandSender") + " as a first parameter.");
				continue;
			}
			String bukkitCommand = groupedCommand ? annotation.parentCommand() : annotation.command();
			PluginCommand command = plugin.getCommand(bukkitCommand);
			if(command == null) {
				plugin.getLogger().warning("Command " + bukkitCommand + " was not found in plugin.yml");
				continue;
			}
			command.setExecutor(this);
			String commandKey = (groupedCommand ? bukkitCommand : "") + annotation.command();
			if(!methods.containsKey(commandKey)) methods.put(commandKey, new TreeSet<Method>(new Comparator<Method>() {
				@Override
				public int compare(Method m1, Method m2) {
					Class<?>[] requestedParameters = m1.getParameterTypes();
					if(requestedParameters[requestedParameters.length - 1] == String[].class) {
						return 1;
					}
					return -1;
				}
			}));
			methods.get(commandKey).add(method);
			if(!(metas.containsKey(commandKey) && metas.get(commandKey).weight() < annotation.weight())) metas.put(commandKey, annotation);
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String command = cmd.getName();
		int max = 0;
		boolean groupedCommand = false;
		if(args.length > 0) {
			for(CommandProperties properties : metas.values()) {
				if(!properties.parentCommand().isEmpty()) {
					LinkedList<String> aliases = new LinkedList<String>();
					aliases.add(properties.command());
					for(String alias : properties.aliases()) aliases.add(alias);
					subCommandLoop:
					for(String subCommand : aliases) {
						String[] subCommands = subCommand.split(" ");
						int lowestLength = subCommands.length < args.length ? subCommands.length : args.length;
						if(max < lowestLength) {
							for(int i = 0; i < lowestLength; i++) {
								if(args[i].equalsIgnoreCase(subCommands[i])) {
									if(i == subCommands.length - 1) {
										groupedCommand = !properties.parentCommand().isEmpty();
										command = cmd.getName() + properties.command();
										max = i;
									}
								} else continue subCommandLoop;
							}
						}
					}
				}
			}
		}
		String[] commandArgs = groupedCommand ? Arrays.copyOfRange(args, max+1 <= args.length ? max+1 : max, args.length) : args;
		if(methods.containsKey(command)) {
			handleCommand(sender, command, commandArgs);
			return true;
		}
		StringBuilder fullCommand = new StringBuilder();
		fullCommand.append("/" + label.toLowerCase());
		for(String arg : args) fullCommand.append(" " + arg);
		sender.sendMessage((sender instanceof Player ? "§c" : "") + "The command " + fullCommand.toString() + " was not found!");
		displayUsage(sender, cmd.getName());
		return true;
	}
	
	/**
	 * Handles the command by determining with method to use based on validation
	 * 
	 * @param sender The person/object invoking the command
	 * @param command The command in it's key state in the methods & metas map
	 * @param args The arguments passed with the command
	 */
	private void handleCommand(CommandSender sender, String command, String[] args) {
		try {
			CommandProperties meta = metas.get(command);
			if(!containsPermission(sender, meta.permissions())) {
				sender.sendMessage("§4Sorry, but you do not have access to perform this command.");
				return;
			}
			
			// Iterate through each method of the comamnd to find one which has matching parameters
			methodLoop:
			for(Method method : methods.get(command)) {
				Class<?>[] requestedParameters = method.getParameterTypes();
				Object[] parameters = new Object[requestedParameters.length];
				if(requestedParameters[0] == Player.class && !(sender instanceof Player)) {
					// Invalid CommandSender
					continue;
				}
				parameters[0] = sender;
				
				//Check if there is a variable parameters (String[])
				if(requestedParameters[requestedParameters.length - 1] == String[].class) {
					parameters[requestedParameters.length - 1] = args;
					requestedParameters = Arrays.copyOfRange(requestedParameters, 0, requestedParameters.length - 1);
				} else if(requestedParameters.length - 1 != args.length) {
					// Invalid amount of arguments
					continue;
				}
				
				// Loop through each argument to see if they match the parameters
				for(int i = 0; i < args.length; i++) {
					if(requestedParameters.length <= i+1) break;
					Object obj = validate(args[i], requestedParameters[i + 1]);
					if(obj == null) {
						// Invalid parameter type
						continue methodLoop;
					} else {
						// Place the returned object in the array of parameters
						parameters[i + 1] = obj;
					}
				}
				
				CommandProperties methodMeta = method.getAnnotation(CommandProperties.class);
				if(methodMeta.minArgs() != -1 && args.length < methodMeta.minArgs()) {
					sender.sendMessage("§cNot enough arguments.");
					displayUsage(sender, command);
					return;
				}
				if(methodMeta.maxArgs() != -1 && args.length > methodMeta.maxArgs()) {
					sender.sendMessage("§cToo many arguments.");
					displayUsage(sender, command);
					return;
				}
				
				// The parameters fit, call the method
				if(!(Boolean) method.invoke(method.getDeclaringClass().newInstance(), parameters)) {
					// The method wants the usage of the command to be displayed
					displayUsage(sender, command);
				}
				return;
			}
			displayUsage(sender, command);
		} catch (Exception e) {
			sender.sendMessage("§cAn error occured while trying to perform this command.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Shows an autogenerated usage message to the sender
	 * 
	 * @param sender The person/object to show the usage to
	 * @param command The command to show the usage of
	 */
	private void displayUsage(CommandSender sender, String command) {
		sender.sendMessage("§eUsage:");
		if(metas.containsKey(command)) {
			CommandProperties properties = metas.get(command);
			for(String string : properties.usage()) {
				sender.sendMessage("§e   - " + (string.startsWith("/") ? "" : "/") + string.replace("<command>", properties.parentCommand().isEmpty() ? properties.command() : properties.parentCommand()));
			}
		} else sender.sendMessage("§e   -" + plugin.getCommand(command).getUsage().replace("<command>", command));
	}
	
	/**
	 * Shows an autogenerated help message to the sender
	 * <p>
	 * <b>WARNING: THIS SHOULD ONLY BE CALLED FROM WITHIN A COMMAND METHOD ITSELF!</b>
	 * </p>
	 * 
	 * @param sender The person/object to show the help message to
	 */
	public void displayHelp(CommandSender sender) {
		StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[2];
		String className = stackTrace.getClassName();
		String methodName = stackTrace.getMethodName();
		CommandProperties meta = null;
		String key = null;
		for(Entry<String, TreeSet<Method>> entry : methods.entrySet()) {
			Method method = entry.getValue().first();
			if(method.getDeclaringClass().getName().equals(className) && method.getName().equals(methodName)) {
				meta = metas.get(entry.getKey());
				key = entry.getKey();
				break;
			}
		}
		if(meta == null || key == null) return;
		boolean groupedCommand = !meta.parentCommand().isEmpty();
		String bukkitCommand = groupedCommand ? meta.parentCommand() : meta.command();
		StringBuilder header = new StringBuilder();
		header.append("§e==========[ §fHelp: /" + bukkitCommand + "§e ]=");
		for(int i = ChatColor.stripColor(header.toString()).length(); i < ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH; i++) header.append("=");
		sender.sendMessage(header.toString());
		if(metas.containsKey(bukkitCommand)) {
			CommandProperties bukkitMeta = metas.get(bukkitCommand);
			if(!bukkitMeta.hideFromHelp()) {
				String metaDesc = bukkitMeta.description();
				sender.sendMessage("§6/" + bukkitCommand + ": §f" + (metaDesc.isEmpty() ? plugin.getCommand(bukkitCommand).getDescription() : metaDesc));
			}
		}
		for(CommandProperties properties : metas.values()) {
			if(properties.hideFromHelp()) continue;
			if(!containsPermission(sender, meta.permissions())) continue;
			if(properties.parentCommand().equalsIgnoreCase(bukkitCommand)) {
				sender.sendMessage("§6/" + bukkitCommand + " " + properties.command() + ": §f" + (properties.description().isEmpty() ? properties.usage()[0].replace("<command>", properties.command()) : properties.description()));
			}
		}
	}
	
	/**
	 * Validates a given argument and the required class it should match
	 * 
	 * @param string The given argument to test
	 * @param parameter The class to test the given argument against
	 * @return The object if the argument matches & null if it doesn't
	 */
	public Object validate(String string, Class<?> parameter) {
		ParameterType type = ParameterType.getType(parameter);
		if(type == null) return null;
		switch(type) {
		case STRING:
			return string;
		case INT:
			if(!string.matches("^\\d+$")) return null;
			return Integer.valueOf(string);
		case BOOLEAN:
			if(string.matches("(?i)true|on|yes")) return true;
			if(string.matches("(?i)false|off|no")) return false;
			return null;
		case PLAYER:
			return Bukkit.getPlayer(string);
		case MATERIAL:
			return Material.getMaterial(string.toUpperCase());
		case WEAPON:
			return Weapon.getWeapon(string);
		default:
			return null;
		}
	}
	
	/**
	 * Checks if a sender has any permission in the array of permissions
	 * 
	 * @param sender The sender to check permissions on
	 * @param permissions The array of permissions to check
	 * @return true if the sender has one of the permissions
	 */
	public boolean containsPermission(CommandSender sender, String[] permissions) {
		if(permissions.length == 0) return true;
		for(String permission : permissions) {
			if(sender.hasPermission(permission)) return true;
		}
		return false;
	}
	
	public static CommandHandler getInstance() {
		return instance;
	}
	
}
