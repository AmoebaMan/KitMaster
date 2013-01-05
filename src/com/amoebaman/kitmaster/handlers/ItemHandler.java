package com.amoebaman.kitmaster.handlers;

import java.io.File;
import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.amoebaman.kitmaster.objects.ParseItemException;
import com.google.common.collect.Lists;

public class ItemHandler {

	private static final YamlConfiguration yaml = new YamlConfiguration();
	
	public static void load(File file) throws IOException, InvalidConfigurationException{
		yaml.load(file);
	}
	
	public static void save(File file) throws IOException{
		yaml.save(file);
	}
	
	public static void addSample(){
		ItemStack stack = new ItemStack(Material.IRON_SWORD);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("The Daisy Cutter");
		meta.setLore(Lists.newArrayList("Strikes down daisies by the hundreds", "Forged by the mighty AmoebaMan"));
		stack.setItemMeta(meta);
		saveCustomItem(stack, "Daisy_Cutter");
		yaml.set("auto-rename." + InventoryHandler.itemToString(new ItemStack(Material.WOOD_SWORD)), "Beat Stick");
	}
	
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
		section.set("item", InventoryHandler.itemToString(stack));
		section.set("data", stack.getData().getData());
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
		ItemStack stack = InventoryHandler.parseItem(section.getString("item"));
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(section.getString("name", null));
		meta.setLore(section.getStringList("lore"));
		stack.setItemMeta(meta);
		return stack;
	}
	
}
