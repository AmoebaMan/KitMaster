package net.amoebaman.kitmaster.utilities;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Contains methods for converting <code>Location</code>s to and from saveable forms.
 * @author Dennison
 */
public class S_Loc{
	
	/**
	 * Saves a <code>Location</code> to a <code>ConfigurationSection</code>.  This method will define fields within the section that describe its values.
	 * @param loc The location to save.
	 * @param config The section to save to.
	 */
	public static void configSave(Location loc, ConfigurationSection config){
		config.set("world", loc.getWorld().getName());
		config.set("x", loc.getX());
		config.set("y", loc.getY());
		config.set("z", loc.getZ());
		config.set("yaw", loc.getYaw());
		config.set("pitch", loc.getPitch());
	}
	
	/**
	 * Loads a <code>Location</code> from a <code>ConfigurationSection</code>, taking its values from fields in the section.  Values not defined will be set to their corresponding values from the origin location (0, 64, 0).
	 * @param config The section to load from.
	 * @return The loaded location.
	 */
	public static Location configLoad(ConfigurationSection config){
		if(config == null)
			return new Location(Bukkit.getWorlds().get(0), 0.5, 64.5, 0.5);
		return new Location(Bukkit.getWorld(config.getString("world", "world")), config.getDouble("x", 0.5), config.getDouble("y", 64.5), config.getDouble("z", 0.5), (float)config.getDouble("yaw", 0.0), (float)config.getDouble("pitch", 0.0));
	}

	/**
	 * Saves a <code>Location</code> to a String.
	 * @param loc The Location to save.
	 * @return The string.
	 */
	public static String stringSave(Location loc, boolean block){
		if(block)
			return loc.getWorld().getName() + "@" + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
		else
			return loc.getWorld().getName() + "@" + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch();
	}
	
	/**
	 * Loads a <code>Location</code> from a String.  If an error occurs while parsing the string, the origin location (0, 64, 0) will be returned.
	 * @param str The string to load from.
	 * @return The loaded location, or the origin location if an error occurs.
	 */
	public static Location stringLoad(String str){
		try{
			World world = Bukkit.getWorld(str.substring(0, str.indexOf("@")));
			String[] coords = str.substring(str.indexOf("@") + 1).split(",");
			Location toReturn = new Location(world, Integer.parseInt(coords[0]) + 0.5, Integer.parseInt(coords[1]) + 0.5, Integer.parseInt(coords[2]) + 0.5);
			if(coords.length > 3){
				toReturn.setYaw(Float.parseFloat(coords[3]));
				toReturn.setPitch(Float.parseFloat(coords[4]));
			}
			return toReturn;	
		}
		catch(Exception e){
			Bukkit.getLogger().severe("Was unable to parse Location from String: " + str);
			return new Location(Bukkit.getWorlds().get(0), 0.5, 64.5, 0.5, 0, 0);
		}
	}
	
	/**
	 * Gets a more user-friendly String version of a <code>Location</code>;
	 * @param loc The location.
	 * @return The string.
	 */
	public static String toString(Location loc){
		return "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ") in " + loc.getWorld().getName();
	}
}
