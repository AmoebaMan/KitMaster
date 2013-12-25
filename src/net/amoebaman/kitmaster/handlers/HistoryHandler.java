package net.amoebaman.kitmaster.handlers;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.amoebaman.kitmaster.KitMaster;
import net.amoebaman.kitmaster.objects.Kit;
import net.amoebaman.kitmaster.sql.SQLQueries;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.collect.Lists;


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
	
	private static List<String> toStringList(List<Kit> kitList){
		List<String> stringList = new ArrayList<String>();
		for(Kit kit : kitList)
			stringList.add(kit.name);
		return stringList;
	}
	
	public static List<Kit> getHistory(OfflinePlayer player){
		if(KitMaster.isSQLRunning()){
			ResultSet result = KitMaster.getSQL().executeQuery(SQLQueries.GET_HISTORY.replace(SQLQueries.PLAYER_MACRO, player.getName()));
			String str = KitMaster.getSQL().getFirstResult(result, "history", String.class);
			if(str == null)
				return new ArrayList<Kit>();
			else
				return toKitList(Lists.newArrayList(str.split(",")));
		}
		else{
			List<String> history = yamlConfig.getStringList(player.getName());
			return toKitList(history);
		}
	}
	
	public static void addToHistory(OfflinePlayer player, Kit kit){
		List<Kit> history = getHistory(player);
		if(KitMaster.isSQLRunning()){
			String str = "";
			for(Kit each : history)
				str += each.name + ",";
			str += kit.name;
			KitMaster.getSQL().executeCommand(SQLQueries.UPDATE_HISTORY.replace(SQLQueries.PLAYER_MACRO, player.getName()).replace(SQLQueries.HISTORY_MACRO, str), false);
		}
		else{
			history.add(kit);
			yamlConfig.set(player.getName(), toStringList(history));
		}
	}
	
	public static void resetHistory(OfflinePlayer player){
		if(KitMaster.isSQLRunning())
			KitMaster.getSQL().executeCommand(SQLQueries.REMOVE_HISTORY.replace(SQLQueries.PLAYER_MACRO, player.getName()), false);
		else
			yamlConfig.set(player.getName(), null);
	}
	
	public static List<OfflinePlayer> getPlayers(){
		List<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
		
		if(KitMaster.isSQLRunning()){
			try{
				ResultSet set = KitMaster.getSQL().executeQuery("SELECT player FROM history");
				while(set.next())
					players.add(Bukkit.getOfflinePlayer(set.getString("player")));
			}
			catch(Exception e){ e.printStackTrace(); }
		}
		else{
			for(String name : yamlConfig.getKeys(false))
				players.add(Bukkit.getOfflinePlayer(name));
		}
		
		while(players.contains(null))
			players.remove(null);
		return players;
	}
	
}
