package net.amoebaman.kitmaster.handlers;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;

import net.amoebaman.kitmaster.KitMaster;
import net.amoebaman.kitmaster.objects.Kit;
import net.amoebaman.kitmaster.sql.SQLQueries;
import net.amoebaman.kitmaster.utilities.S_Loc;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;


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
		String locStr = S_Loc.stringSave(loc, true);
		if(KitMaster.isSQLRunning()){
			ResultSet set = KitMaster.getSQL().executeQuery(SQLQueries.GET_SIGN_AT.replace(SQLQueries.LOCATION_MACRO, locStr));
			return KitHandler.getKit(KitMaster.getSQL().getFirstResult(set, "kit", String.class));
		}
		else
			return KitHandler.getKit(yamlConfig.getString(locStr, ""));
	}
	
	public static void saveKitSign(Kit kit, Location loc){
		if(KitMaster.isSQLRunning())
			KitMaster.getSQL().executeCommand(SQLQueries.SET_SIGN_AT.replace(SQLQueries.LOCATION_MACRO, S_Loc.stringSave(loc, true)).replace(SQLQueries.KIT_MACRO, kit.name));
		else
			yamlConfig.set(S_Loc.stringSave(loc, true), kit.name);
	}
	
	public static void removeKitSign(Location loc){
		if(KitMaster.isSQLRunning())
			KitMaster.getSQL().executeCommand(SQLQueries.REMOVE_SIGN_AT.replace(SQLQueries.LOCATION_MACRO, S_Loc.stringSave(loc, true)));
			else
				yamlConfig.set(S_Loc.stringSave(loc, true), null);
	}
	
	public static boolean isKitSign(Location loc){
		return getKitSign(loc) != null;
	}
	
	//TODO Awaiting proper implementation
	public static int purgeAbsentees(){
		return 0;
	}
	
	
}
