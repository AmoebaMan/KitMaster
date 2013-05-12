package net.amoebaman.kitmaster.handlers;

import java.util.ArrayList;
import java.util.List;

import net.amoebaman.kitmaster.controllers.ItemController;
import net.amoebaman.kitmaster.utilities.ParseItemException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomItemHandler {
	
	public static ConfigurationSection yaml;
	
	private static ConfigurationSection getSection(String name){
		ConfigurationSection section = yaml.getConfigurationSection(name);
		if(section == null)
			for(String key : yaml.getKeys(false))
				if(key.equalsIgnoreCase(name))
					section = yaml.getConfigurationSection(key);
		return section;
	}
	
	public static boolean isCustomItem(String name){
		return getSection(name) != null;
	}
	
	public static void saveCustomItem(ItemStack stack, String name){
		ConfigurationSection section = yaml.createSection(name);
		section.set("item", ItemController.itemToString(stack, false));
		ItemMeta meta = stack.getItemMeta();
		section.set("name", meta.getDisplayName());
		section.set("lore", meta.getLore());
	}
	
	public static ItemStack getCustomItem(String name) throws ParseItemException{
		ConfigurationSection section = getSection(name);
		if(section == null)
			return null;
		if(isCustomItem(section.getString("item").split(":")[0]))
			return null;
		ItemStack stack = ItemController.parseItem(section.getString("item"));
		ItemMeta meta = stack.getItemMeta();
		if(section.getString("name") != null)
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', section.getString("name", null)));
		if(section.getStringList("lore") != null){
			List<String> lore = new ArrayList<String>();
			for(String line : section.getStringList("lore"))
				lore.add(ChatColor.translateAlternateColorCodes('&', line));
			meta.setLore(lore);
		}
		stack.setItemMeta(meta);
		return stack;
	}
	
}
