package com.amoebaman.kitmaster.handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkHandler {

	private static final YamlConfiguration yaml = new YamlConfiguration();
	
	public static void load(File file) throws IOException, InvalidConfigurationException{
		yaml.load(file);
	}
	
	public static void save(File file) throws IOException{
		yaml.save(file);
	}
	
	public static void addSample(){
		ItemStack sample = new ItemStack(Material.FIREWORK);
		FireworkMeta meta = (FireworkMeta) sample.getItemMeta();
		meta.addEffect(FireworkEffectHandler.getFirework("sample_effect_1"));
		meta.addEffect(FireworkEffectHandler.getFirework("sample_effect_2"));
		meta.setPower(2);
		sample.setItemMeta(meta);
		saveFirework(sample, "sample_firework");
	}
	
	private static ConfigurationSection getSection(String name){
		ConfigurationSection section = yaml.getConfigurationSection(name);
		if(section == null)
			for(String key : yaml.getKeys(false))
				if(key.equalsIgnoreCase(name))
					section = yaml.getConfigurationSection(key);
		return section;
	}

	public static boolean isFirework(String name){
		return getSection(name) != null;
	}
	
	public static void saveFirework(ItemStack firework, String name){
		if(firework.getType() != Material.FIREWORK)
			return;
		FireworkMeta meta = (FireworkMeta) firework.getItemMeta();
		ConfigurationSection fireworkYaml = yaml.createSection(name);
		fireworkYaml.set("fuse", meta.getPower());
		List<String> bursts = new ArrayList<String>();
		for(int i = 0; i < meta.getEffectsSize(); i++){
			FireworkEffect burst = meta.getEffects().get(i);
			String burstName;
			if(FireworkEffectHandler.getEffectName(burst) != null){
				burstName =  name + "_burst_" + i;
				FireworkEffectHandler.saveFirework(burst, burstName);
			}
			else
				burstName = FireworkEffectHandler.getEffectName(burst);
			bursts.add(burstName);
		}
		fireworkYaml.set("bursts", bursts);
	}
	
	public static ItemStack loadFirework(ItemStack firework, String name){
		if(firework.getType() != Material.FIREWORK)
			return firework;
		ConfigurationSection fireworkYaml = getSection(name);
		if(fireworkYaml == null)
			return firework;
		FireworkMeta meta = (FireworkMeta) firework.getItemMeta();
		meta.setPower(fireworkYaml.getInt("fuse"));
		for(String burstName : fireworkYaml.getStringList("bursts"))
			meta.addEffect(FireworkEffectHandler.getFirework(burstName));
		firework.setItemMeta(meta);
		return firework;
	}
	
	public static ItemStack getFirework(String name){
		return loadFirework(new ItemStack(Material.FIREWORK), name);
	}
	
	public static String getFireworkName(ItemStack firework){
		for(String name : yaml.getKeys(false))
			if(getFirework(name).getItemMeta().equals(firework.getItemMeta()))
				return name;
		return null;
	}

}
