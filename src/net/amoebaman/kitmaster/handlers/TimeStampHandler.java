package net.amoebaman.kitmaster.handlers;

import java.io.File;
import java.io.IOException;

import net.amoebaman.kitmaster.enums.Attribute;
import net.amoebaman.kitmaster.enums.GiveKitResult;
import net.amoebaman.kitmaster.objects.Kit;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;


public class TimeStampHandler {

	private static YamlConfiguration yamlConfig;
	
	public static void load(File file) throws IOException{
		yamlConfig = YamlConfiguration.loadConfiguration(file);
	}
	
	public static void save(File file) throws IOException{
		OfflinePlayer player;
		Kit kit;
		for(String name : yamlConfig.getKeys(false))
			if(yamlConfig.isConfigurationSection(name))
				for(String kitName : yamlConfig.getConfigurationSection(name).getKeys(false)){
					player = Bukkit.getOfflinePlayer(name);
					kit = KitHandler.getKit(kitName);
					if(!kit.booleanAttribute(Attribute.SINGLE_USE) && timeoutLeft(player, kit) <= 0)
						yamlConfig.set(name + "." + kitName, null);
				}
					
		yamlConfig.save(file);
	}

	public static long getTimeStamp(OfflinePlayer player, Kit kit){
		String sectionName = player == null ? "global" : player.getName();
		ConfigurationSection playerSection = yamlConfig.getConfigurationSection(sectionName);
		if(playerSection == null){
			yamlConfig.createSection(sectionName);
			return 0;
		}
		return playerSection.getLong(kit.name, 0);
	}

	public static void setTimeStamp(OfflinePlayer player, Kit kit){
		String sectionName = player == null ? "global" : player.getName();
		ConfigurationSection playerSection = yamlConfig.getConfigurationSection(sectionName);
		if(playerSection == null)
			playerSection = yamlConfig.createSection(sectionName);
		playerSection.set(kit.name, System.currentTimeMillis());
	}
	
	public static void clearTimeStamp(OfflinePlayer player, Kit kit){
		String sectionName = player == null ? "global" : player.getName();
		ConfigurationSection playerSection = yamlConfig.getConfigurationSection(sectionName);
		if(playerSection == null)
			playerSection = yamlConfig.createSection(sectionName);
		playerSection.set(kit.name, null);
	}

	public static GiveKitResult timeoutCheck(OfflinePlayer player, Kit kit){
		long timestamp = getTimeStamp(kit.booleanAttribute(Attribute.GLOBAL_TIMEOUT) ? null : player, kit);
		long timeout = kit.integerAttribute(Attribute.TIMEOUT);
		if(System.currentTimeMillis() < timestamp + (timeout * 1000))
			return GiveKitResult.FAIL_TIMEOUT;	
		if(timestamp > 0 && (kit.booleanAttribute(Attribute.SINGLE_USE) || kit.booleanAttribute(Attribute.SINGLE_USE_LIFE)))
			return GiveKitResult.FAIL_SINGLE_USE;
		return GiveKitResult.SUCCESS;
	}

	public static int timeoutLeft(OfflinePlayer player, Kit kit){
		long timestamp = getTimeStamp(kit.booleanAttribute(Attribute.GLOBAL_TIMEOUT) ? null : player, kit);
		long timeout = kit.integerAttribute(Attribute.TIMEOUT);
		return (int)(((timestamp + (timeout * 1000)) - System.currentTimeMillis()) / 1000);
	}
	
}
