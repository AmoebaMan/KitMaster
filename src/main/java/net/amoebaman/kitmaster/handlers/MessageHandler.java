package net.amoebaman.kitmaster.handlers;

import net.amoebaman.kitmaster.objects.Kit;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public class MessageHandler {

	public static ConfigurationSection yaml;
	
	public static String getPrefix(){
		return ChatColor.translateAlternateColorCodes('&', yaml.getString("prefix", "&o"));
	}
	
	public static String getMessage(String label){
		label = label.toLowerCase().replace(' ', '_');
		String message = getPrefix() + yaml.getString(label);
		message = ChatColor.translateAlternateColorCodes('&', message);
		return message;
	}
	
	public static String getMessage(String label, Kit context){
		String message = getMessage(label);
		message = message.replace("%kit%", context.name);
		if(context.getParent() != null)
			message = message.replace("%parent%", context.getParent().name);
		return message;
	}
	
}
