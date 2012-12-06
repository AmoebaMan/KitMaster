package com.amoebaman.kitmaster.handlers;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.amoebaman.kitmaster.enums.Attribute;
import com.amoebaman.kitmaster.enums.GiveKitResult;
import com.amoebaman.kitmaster.objects.Kit;

public class TimeStampHandler {

	private static YamlConfiguration yamlConfig;
	
	public static void load(File file) throws IOException{
		yamlConfig = YamlConfiguration.loadConfiguration(file);
	}
	
	public static void save(File file) throws IOException{
		yamlConfig.save(file);
	}

	public static long getTimeStamp(Player player, Kit kit){
		String sectionName = player == null ? "global" : player.getName();
		ConfigurationSection playerSection = yamlConfig.getConfigurationSection(sectionName);
		if(playerSection == null){
			yamlConfig.createSection(sectionName);
			return 0;
		}
		return playerSection.getLong(kit.name, 0);
	}

	public static void setTimeStamp(Player player, Kit kit){
		String sectionName = player == null ? "global" : player.getName();
		ConfigurationSection playerSection = yamlConfig.getConfigurationSection(sectionName);
		if(playerSection == null)
			playerSection = yamlConfig.createSection(sectionName);
		playerSection.set(kit.name, System.currentTimeMillis());
	}

	public static GiveKitResult timeoutCheck(Player player, Kit kit){
		long timestamp = TimeStampHandler.getTimeStamp(kit.booleanAttribute(Attribute.GLOBAL_TIMEOUT) ? null : player, kit);
		long timeout = kit.integerAttribute(Attribute.TIMEOUT);
		if(player.hasPermission("kitmaster.shorttimeout") && !kit.booleanAttribute(Attribute.GLOBAL_TIMEOUT))
			timeout /= 3;
		
		if(System.currentTimeMillis() < timestamp + (timeout * 1000))
			return GiveKitResult.FAIL_TIMEOUT;	
		if(kit.integerAttribute(Attribute.TIMEOUT) < 0)
			return GiveKitResult.FAIL_SINGLE_USE;
		return GiveKitResult.SUCCESS;
	}

	public static int timeoutLeft(Player player, Kit kit){
		long timestamp = TimeStampHandler.getTimeStamp(kit.booleanAttribute(Attribute.GLOBAL_TIMEOUT) ? null : player, kit);
		long timeout = kit.integerAttribute(Attribute.TIMEOUT);
		if(player.hasPermission("kitmaster.shorttimeout") && !kit.booleanAttribute(Attribute.GLOBAL_TIMEOUT))
			timeout /= 3;
		return (int)(((timestamp + (timeout * 1000)) - System.currentTimeMillis()) / 1000);
	}
	
}
