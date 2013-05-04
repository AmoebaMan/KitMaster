package net.amoebaman.kitmaster.handlers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.configuration.ConfigurationSection;

public class FireworkEffectHandler {

	public static ConfigurationSection yaml;
	
	private static ConfigurationSection getSection(String name){
		ConfigurationSection section = yaml.getConfigurationSection(name);
		if(section == null)
			for(String key : yaml.getKeys(false))
				if(key.equalsIgnoreCase(name))
					section = yaml.getConfigurationSection(key);
		return section;
	}

	public static boolean isFireworkEffect(String name){
		return getSection(name) != null;
	}
	
	public static void saveFirework(FireworkEffect firework, String name){
		ConfigurationSection effectYaml = yaml.createSection(name);
		effectYaml.set("type", firework.getType().name().toLowerCase());
		effectYaml.set("trail", firework.hasTrail());
		effectYaml.set("flicker", firework.hasFlicker());
		List<String> primaryColors = new ArrayList<String>();
		for(Color color : firework.getColors())
			primaryColors.add("0x" + Integer.toHexString(color.asRGB()));
		effectYaml.set("primary-colors", primaryColors);
		List<String> fadeColors = new ArrayList<String>();
		for(Color color : firework.getFadeColors())
			fadeColors.add("0x" + Integer.toHexString(color.asRGB()));
		effectYaml.set("fade-colors", fadeColors);
	}
	
	public static FireworkEffect getFirework(String name){
		if(!isFireworkEffect(name))
			return null;
		ConfigurationSection effectYaml = getSection(name);
		if(effectYaml == null)
			return null;
		FireworkEffect.Builder firework = FireworkEffect.builder();
		firework.with(Type.valueOf(effectYaml.getString("type").toUpperCase()));
		firework.trail(effectYaml.getBoolean("trail"));
		firework.flicker(effectYaml.getBoolean("flicker"));
		for(String colorName : effectYaml.getStringList("primary-colors"))
			firework.withColor(Color.fromRGB(Integer.decode(colorName)));
		for(String colorName : effectYaml.getStringList("fade-colors"))
			firework.withFade(Color.fromRGB(Integer.decode(colorName)));
		return firework.build();
	}
	
	public static String getEffectName(FireworkEffect effect){
		for(String name : yaml.getKeys(false))
			if(getFirework(name).equals(effect))
				return name;
		return null;
	}
	
}
