package net.amoebaman.kitmaster.handlers;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.amoebaman.kitmaster.KitMaster;
import net.amoebaman.kitmaster.controllers.ItemController;
import net.amoebaman.kitmaster.enums.Attribute;
import net.amoebaman.kitmaster.enums.PermsResult;
import net.amoebaman.kitmaster.objects.Kit;
import net.amoebaman.kitmaster.utilities.ParseItemException;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.google.common.collect.Lists;

public class KitHandler {

	private static final ArrayList<Kit> kits = new ArrayList<Kit>();
	public static Kit editKit;
	
	/**
	 * Loads kits into the internal kit list from a file.  If the file is suffixed with .kit, the file will be immediately loaded.  If the file is a directory, all files within it suffixed with .kit will be automatically loaded.  If neither condition is met, nothing will happen.
	 * @param file The file to load kits from.
	 */
	public static void loadKits(File file){
		if(file.isDirectory()){
			ArrayList<File> children = Lists.newArrayList(file.listFiles(new FileFilter(){ public boolean accept(File file){ return file.getName().endsWith(".kit") || file.getName().equals("kits.yml"); } }));
			Collections.sort(children, new Comparator<File>(){ public int compare(File f1, File f2) { return f1.getName().compareTo(f2.getName()); }});
			for(File child : children)
				loadFromKitFile(child);
		}
		else if(file.getName().equals("kits.yml"))
			loadFromKitsYaml(file);
		else if(file.getName().endsWith(".kit"))
			loadFromKitFile(file);
	}

	private static void loadFromKitFile(File file) {
		String name = file.getName().replaceAll("\\.kit", "");
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
		addKit(parseKit(name, yaml));
	}
	
	private static void loadFromKitsYaml(File file){
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
		for(String name : yaml.getKeys(false))
			addKit(parseKit(name, yaml.getConfigurationSection(name)));
	}

	public static boolean saveKit(Kit kit){
		addKit(kit);
		File file = new File(KitMaster.kitsDirectory + "/" + kit.name + ".kit");
		YamlConfiguration yaml = new YamlConfiguration();
		for(Entry<String, Object> e : kit.serialize().entrySet())
			yaml.set(e.getKey(), e.getValue());
		try {
			yaml.save(file);
			return true;
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
 	/**
 	 * Parses a kit from a file.  All kits are parsed using YAML.
 	 * @param file The file to parse a kit from.
 	 * @return The kit parsed from the file.
 	 */
	public static Kit parseKit(String name, ConfigurationSection yaml){
		
		List<ItemStack> items = new ArrayList<ItemStack>();
		List<PotionEffect> effects = new ArrayList<PotionEffect>();
		List<String> permissions = new ArrayList<String>();
		HashMap<Attribute, Object> attributes = new HashMap<Attribute, Object>();
		/*
		 * Parse the kit's items
		 */
		for(String str : yaml.getStringList("items")){
			try{
				ItemStack stack = ItemController.parseItem(str);
				if(stack != null)
					items.add(stack);
			}
			catch(ParseItemException e){
				e.printStackTrace();
				continue;
			}
		}
		/*
		 * Parse the kit's effects
		 */
		for(String str : yaml.getStringList("effects")){
			try{
				PotionEffect effect = ItemController.parseEffect(str);
				if(effect != null)
					effects.add(effect);
			}catch(ParseItemException e){
				e.printStackTrace();
				continue;
			}
		}
		/*
		 * Retrive the String list defining the kit's permissions
		 */
		permissions = yaml.getStringList("permissions");
		if(permissions == null)
			permissions = new ArrayList<String>();
		/*
		 * For each defined attribute, retrive it from its designated YAML path
		 */
		for(Attribute type : Attribute.values()){
			Object value = yaml.get(type.path, null);
			if(type.type != null && value != null && type.type.matches(value)){
				if(type.type.matches(value))
					attributes.put(type, value);
				else
					KitMaster.logger().severe("Failed to define attribute " + type.name() + " for kit " + name + ": the defined value was of the wrong type");
			}
		}
		/*
		 * SuperKits legacy compatibility for clear options
		 */
		if(yaml.getBoolean("clearInventory", false))
			attributes.put(Attribute.CLEAR_ALL, true);
		/*
		 * Return the finished kit
		 */
		return new Kit(name, items, effects, permissions, attributes);
	}

	private static void addKit(Kit kit){
		while(kits.contains(kit))
			kits.remove(kit);
		kits.add(kit);
		if(!kit.booleanAttribute(Attribute.SUPPRESS_LOAD_NOTIFICATION))
			KitMaster.logger().info("Successfully loaded the " + kit.name + " kit");
	}
	
	/**
	 * Gets a kit by its name.  This method will ignore case.  If no kit maches the argument precisely, it will consider abbreviations.
	 * @param kitName The name of the desired kit.
	 * @return The kit whose name matches the argument, or null if no kit was found.
	 */
	public static Kit getKit(String kitName){
		if(kitName == null || kitName.equals(""))
			return null;
		for(Kit kit : kits){
			if(kit == null || kit.name == null)
				continue;
			if(kit.name.equalsIgnoreCase(kitName))
				return kit.applyParentAttributes();
		}
		for(Kit kit : kits){
			if(kit == null || kit.name == null)
				continue;
			if(kit.name.toLowerCase().startsWith(kitName.toLowerCase()))
				return kit.applyParentAttributes();
		}
		return null;
	}
	
	/**
	 * Gets a kit by its identifier.  The identifier is an attribute that is defined inside the kit, and has no function unless a third-party plugin uses it.  This method will ignore case.  If no kit matches the argument precisely, it will consider abbreviations.
	 * @param identifier The identifier of the desired kit.
	 * @return The kit whose identifier matches the argument, or null if no kit was found.
	 */
	public static Kit getKitByIdentifier(String identifier){
		if(identifier == null || identifier.equals(""))
			return null;
		for(Kit kit : kits){
			if(kit == null || kit.name == null)
				continue;
			if(kit.stringAttribute(Attribute.IDENTIFIER).equalsIgnoreCase(identifier))
				return kit.applyParentAttributes();
		}
		for(Kit kit : kits){
			if(kit == null || kit.name == null)
				continue;
			if(kit.stringAttribute(Attribute.IDENTIFIER).toLowerCase().startsWith(identifier.toLowerCase()))
				return kit.applyParentAttributes();
		}
		return null;
	}

	/**
	 * Tests whether a kit exists that matches the given name.
	 * @param kitName The name of the kit to search for.
	 * @return True if a kit was found that matches the given name, false otherwise.
	 */
	public static boolean isKit(String kitName){
		return getKit(kitName) != null;
	}

	/**
	 * Gets the list of all registered kits.
	 * @return The list of kits.
	 */
	public static ArrayList<Kit> getKits(){
		return kits;
	}

	/**
	 * Tests a <code>CommandSender</code>'s permissions to access a kit.
	 * @param sender The CommandSender to test.
	 * @param kit The kit to test.
	 * @return The <code>PermsResult</code> that reflects the nature of the testee's permissions for the given kit.
	 */
	public static PermsResult getKitPerms(CommandSender sender, Kit kit){

		//If the kit is null, we obviously can't check it
		if(kit == null)
			return PermsResult.NULL_KIT;

		//Check conditions, from lowest to highest priority
		//Permissions always stack, never cancel
		//Default to no permissions
		PermsResult result = PermsResult.NONE;

		//First check for individual permissions
		boolean hasSignPerms = sender.hasPermission("kitmaster.sign." + kit.name) || sender.hasPermission("kitmaster.kit." + kit.name);
		boolean hasCommandPerms = sender.hasPermission("kitmaster.cmd." + kit.name) || sender.hasPermission("kitmaster.kit." + kit.name);
		if(hasSignPerms || hasCommandPerms){
			if(!hasCommandPerms)
				result = PermsResult.SIGN_ONLY;
			else if(!hasSignPerms)
				result = PermsResult.COMMAND_ONLY;
			else
				result = PermsResult.ALL;
		}

		//Next check for blanket (star) nodes
		if(sender.hasPermission("kitmaster.sign.*")){
			if(result == PermsResult.COMMAND_ONLY)
				result = PermsResult.ALL;
			else if(result != PermsResult.ALL)
				result = PermsResult.SIGN_ONLY;
		}
		if(sender.hasPermission("kitmaster.cmd.*")){
			if(result == PermsResult.SIGN_ONLY)
				result = PermsResult.ALL;
			else if(result != PermsResult.ALL)
				result = PermsResult.COMMAND_ONLY;
		}
		if(sender.hasPermission("kitmaster.kit.*"))
			result = PermsResult.ALL;

		//Now let's see if there are any parents to consider
		Kit parentKit = kit.getParent();
		if(parentKit != null){	

			//Test the parent kit's permissions
			//Again, always stack, never cancel
			PermsResult parentResult = getKitPerms(sender, parentKit);
			switch(parentResult){
			case SIGN_ONLY:
			case INHERIT_SIGN_ONLY:
				if(kit.booleanAttribute(Attribute.INHERIT_PARENT_PERMS)){
					if(result == PermsResult.COMMAND_ONLY)
						result = PermsResult.INHERIT_ALL;
					else if(result != PermsResult.ALL)
						result = PermsResult.INHERIT_SIGN_ONLY;
				}
				break;
			case COMMAND_ONLY:
			case INHERIT_COMMAND_ONLY:
				if(kit.booleanAttribute(Attribute.INHERIT_PARENT_PERMS)){
					if(result == PermsResult.SIGN_ONLY)
						result = PermsResult.INHERIT_ALL;
					else if(result != PermsResult.ALL)
						result = PermsResult.INHERIT_COMMAND_ONLY;
				}
				break;
			case ALL:
			case INHERIT_ALL:
				if(kit.booleanAttribute(Attribute.INHERIT_PARENT_PERMS))
					result = PermsResult.INHERIT_ALL;
				break;
			case NONE:
			case INHERIT_NONE:
				if(kit.booleanAttribute(Attribute.REQUIRE_PARENT_PERMS))
					result = PermsResult.INHERIT_NONE;
				break;
			case NULL_KIT: }
		}

		if(KitMaster.DEBUG_PERMS)
			KitMaster.logger().info("Performed permissions check for player " + sender.getName() + " and kit " + kit.name + " => result was " + result.name());
		return result;
	}

}
