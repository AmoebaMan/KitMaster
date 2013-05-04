package net.amoebaman.kitmaster.handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.amoebaman.kitmaster.objects.Kit;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;


public class HistoryHandler {

	private static YamlConfiguration yamlConfig;
	
	public static void load(File file) throws IOException{
		yamlConfig = YamlConfiguration.loadConfiguration(file);
	}
	
	public static void save(File file) throws IOException{
		yamlConfig.save(file);
	}
	
	private static List<Kit> toKitList(List<String> stringList){
		List<Kit> kitList = new ArrayList<Kit>();
		for(String kitName : stringList)
			if(KitHandler.isKit(kitName))
				kitList.add(KitHandler.getKit(kitName));
		return kitList;
	}
	
	public static List<Kit> getHistory(OfflinePlayer player){
		List<String> history = yamlConfig.getStringList(player.getName());
		return toKitList(history);
	}
	
	public static void addToHistory(OfflinePlayer player, Kit kit){
		List<String> history = yamlConfig.getStringList(player.getName());
		history.add(kit.name);
		yamlConfig.set(player.getName(), history);
	}

	public static void resetHistory(OfflinePlayer player){
		yamlConfig.set(player.getName(), null);
	}
	
	public static List<OfflinePlayer> getPlayers(){
		List<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
		for(String name : yamlConfig.getKeys(false))
			players.add(Bukkit.getOfflinePlayer(name));
		while(players.contains(null))
			players.remove(null);
		return players;
	}
	
}
