package com.amoebaman.kitmaster.handlers;

import java.io.File;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.amoebaman.kitmaster.objects.Kit;
import com.amoebaman.kitmaster.utilities.S_Location;

public class SignHandler{

	private final static YamlConfiguration yamlConfig = new YamlConfiguration();
	
	public static void load(File file) throws IOException, InvalidConfigurationException{
		yamlConfig.options().pathSeparator('>');
		yamlConfig.load(file);
	}
	
	public static void save(File file) throws IOException{
		yamlConfig.options().pathSeparator('>');
		yamlConfig.save(file);
	}

	public static Kit getKitSign(Location loc){
		String locKey = S_Location.stringSave(loc);
		return KitHandler.getKit(yamlConfig.getString(locKey, ""));
	}

	public static void saveKitSign(Kit kit, Location loc){
		yamlConfig.set(S_Location.stringSave(loc), kit.name);
	}

	public static void removeKitSign(Location loc){
		yamlConfig.set(S_Location.stringSave(loc), null);
	}
	
	public static boolean isKitSign(Location loc){
		return getKitSign(loc) != null;
	}

	//TODO Awaiting proper implementation
	public static int purgeAbsentees(){
		return 0;
	}
	
	
}
