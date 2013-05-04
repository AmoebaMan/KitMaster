package net.amoebaman.kitmaster.handlers;

import net.amoebaman.kitmaster.controllers.ItemController;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

public class CustomPotionHandler {

	public static ConfigurationSection yaml;
	
	private static ConfigurationSection getSection(String name){
		ConfigurationSection section = yaml.getConfigurationSection(name);
		if(section == null)
			for(String key : yaml.getKeys(false))
				if(key.equalsIgnoreCase(name))
					section = yaml.getConfigurationSection(key);
		return section;
	}

	public static boolean isPotion(String name){
		return getSection(name) != null;
	}
	
	public static void savePotion(ItemStack potion, String name){
		if(potion.getType() != Material.POTION)
			return;
		PotionMeta meta = (PotionMeta) potion.getItemMeta();
		ConfigurationSection potionYaml = yaml.createSection(name);
		for(int i = 0; i < meta.getCustomEffects().size(); i++){
			PotionEffect effect = meta.getCustomEffects().get(i);
			ConfigurationSection effectYaml = potionYaml.createSection("effect_" + i);
			effectYaml.set("type", effect.getType().getName().toLowerCase());
			effectYaml.set("duration", effect.getDuration());
			effectYaml.set("amplifier", effect.getAmplifier() + 1);
			effectYaml.set("showParticles", effect.isAmbient());
		}
	}
	
	public static ItemStack loadPotion(ItemStack potion, String name){
		if(potion.getType() != Material.POTION)
			return potion;
		PotionMeta meta = (PotionMeta) potion.getItemMeta();
		meta.clearCustomEffects();
		ConfigurationSection potionYaml = getSection(name);
		if(potionYaml == null)
			return potion;
		for(String index : potionYaml.getKeys(false)){
			ConfigurationSection effectYaml = potionYaml.getConfigurationSection(index);
			PotionEffect effect = new PotionEffect(ItemController.matchPotionEffect(effectYaml.getString("type")), effectYaml.getInt("duration", 600), effectYaml.getInt("amplifier", 1) - 1, effectYaml.getBoolean("showParticles", true));
			meta.addCustomEffect(effect, true);
		}
		if(!meta.getCustomEffects().isEmpty())
			meta.setMainEffect(meta.getCustomEffects().get(0).getType());
		potion.setItemMeta(meta);
		return potion;
	}
	
	public static ItemStack getPotion(String name){
		return loadPotion(new ItemStack(Material.POTION), name);
	}
	
	public static String getPotionName(ItemStack potion){
		for(String name : yaml.getKeys(false))
			if(getPotion(name).getItemMeta().equals(potion.getItemMeta()))
				return name;
		return null;
	}
}
