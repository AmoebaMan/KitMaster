package com.amoebaman.kitmaster.handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.amoebaman.kitmaster.controllers.ItemController;
import com.amoebaman.kitmaster.utilities.ParseItemException;
import com.google.common.collect.Lists;

public class CustomItemHandler {

	private static final YamlConfiguration yaml = new YamlConfiguration();
	
	public static void load(File file) throws IOException, InvalidConfigurationException{
		yaml.load(file);
	}
	
	public static void save(File file) throws IOException{
		yaml.save(file);
	}
	
	public static void addSample(){
		ItemStack stack = new ItemStack(Material.IRON_SWORD);
		stack.addEnchantment(Enchantment.DAMAGE_ALL, 10);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("The Daisy Cutter");
		meta.setLore(Lists.newArrayList("Strikes down daisies by the hundreds", "Forged by the mighty AmoebaMan"));
		stack.setItemMeta(meta);
		saveCustomItem(stack, "Daisy_Cutter");
		yaml.set("auto-rename." + ItemController.itemToString(new ItemStack(Material.WOOD_SWORD)), "Beat Stick");
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
		section.set("item", ItemController.itemToString(stack));
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
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', section.getString("name", null)));
		List<String> lore = new ArrayList<String>();
		for(String line : section.getStringList("lore"))
			lore.add(ChatColor.translateAlternateColorCodes('&', line));
		meta.setLore(lore);
		stack.setItemMeta(meta);
		return stack;
	}
	
}
