package net.amoebaman.kitmaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.amoebaman.kitmaster.controllers.ItemController;
import net.amoebaman.kitmaster.enums.Attribute;
import net.amoebaman.kitmaster.enums.GenericResult;
import net.amoebaman.kitmaster.enums.GiveKitContext;
import net.amoebaman.kitmaster.enums.GiveKitResult;
import net.amoebaman.kitmaster.enums.PermsResult;
import net.amoebaman.kitmaster.handlers.BookHandler;
import net.amoebaman.kitmaster.handlers.CustomItemHandler;
import net.amoebaman.kitmaster.handlers.FireworkHandler;
import net.amoebaman.kitmaster.handlers.KitHandler;
import net.amoebaman.kitmaster.objects.Kit;
import net.amoebaman.kitmaster.utilities.CommandController;
import net.amoebaman.kitmaster.utilities.CommandController.CommandHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import com.google.common.collect.Lists;


public class KitMasterCommandHandler implements TabCompleter{

	public static KitMaster plugin;
	public static void init(KitMaster instance){
		CommandController.registerCommands(new KitMasterCommandHandler());
	}

	public static String concatArgs(String... args){
		String result = "";
		for(String str : args)
			result += str + " ";
		return result.trim();
	}
	
	@CommandHandler(cmd = "kitdebug")
	public void kitDebug(CommandSender sender, String[] args){
		Actions.debugNextGiveKit = true;
	}
	
	@CommandHandler(cmd = "kit")
	public void kit(Player player, String[] args){
		if (args.length < 1)
			return;
		if(!KitHandler.isKit(args[0])){
			player.sendMessage(ChatColor.ITALIC + "That kit does not exist");
			return;
		}
		GiveKitResult result = Actions.giveKit(player, KitHandler.getKit(args[0]), GiveKitContext.COMMAND_TAKEN);
		if(KitMaster.DEBUG_KITS)
			KitMaster.logger().info("Result: " + result.name());
		return;
	}

	@CommandHandler(cmd = "givekit", permissions = "kitmaster.give", permissionMessage = "You dont have permission to give kits")
	public void givekit(CommandSender sender, String[] args){
		if (args.length < 2){
			sender.sendMessage(ChatColor.ITALIC + "Include a player to give to and a kit to give");
			return;
		}
		Player target = Bukkit.getPlayer(args[0]);
		if(!KitHandler.isKit(args[1])){
			sender.sendMessage(ChatColor.ITALIC + "That kit does not exist");
			return;
		}
		Kit kit = KitHandler.getKit(args[1]);
		GiveKitResult result = Actions.giveKit(target, kit, GiveKitContext.COMMAND_GIVEN);
		if(KitMaster.DEBUG_KITS)
			KitMaster.logger().info("Result: " + result.name());
		target.sendMessage(ChatColor.ITALIC + "You have been given the " + kit.name + " kit");
		sender.sendMessage(ChatColor.ITALIC + target.getName() + " has been given the " + kit.name + " kit");
		return;
	}

	@CommandHandler(cmd = "kitlist", permissions = "kitmaster.list", permissionMessage = "You don't have permission to view kits")
	public void kitlist(CommandSender sender, String[] args){
		sender.sendMessage(ChatColor.GREEN + "Available kits:");
		for(Kit kit : KitHandler.getKits()){
			PermsResult perms = KitHandler.getKitPerms(sender, kit);
			if(perms.generic.bool && kit.applyParentAttributes().booleanAttribute(Attribute.SHOW_IN_LIST)){
				String message = ChatColor.ITALIC + " - " + kit.name;
				if(perms.generic == GenericResult.CONDITIONAL)
					message += " - " + ChatColor.YELLOW + ChatColor.ITALIC + perms.message;
				sender.sendMessage(message);
			}
		}
		if(sender.hasPermission("kitmaster.take"))
			sender.sendMessage(ChatColor.GREEN + "Use '/kit <kitname>' to take a kit");
		if(sender.hasPermission("kitmaster.info"))
			sender.sendMessage(ChatColor.GREEN + "Use '/kitinfo <kitname>' to see more about a kit");
	}

	@CommandHandler(cmd = "kitinfo", permissions = "kitmaster.list", permissionMessage = "You don't have permissions to get info about kits")
	public void kitinfo(CommandSender sender, String[] args){
		if(args.length < 1){
			sender.sendMessage(ChatColor.ITALIC + "Include a kit to learn about");
			return;
		}
		Kit kit = KitHandler.getKit(args[0]);
		if(KitHandler.getKitPerms(sender, kit).generic == GenericResult.NO){
			sender.sendMessage(ChatColor.ITALIC + "You don't have permission to view the " + kit.name + " kit");
			return;
		}
		if(kit == null){
			sender.sendMessage(ChatColor.ITALIC + "That kit does not exist");
			return;
		}
		sender.sendMessage(ChatColor.ITALIC + "Kit info for " + kit.name);
		sender.sendMessage(ChatColor.ITALIC + "Items:");
		for(ItemStack item : kit.items)
			sender.sendMessage(ChatColor.ITALIC + "  - " + ItemController.friendlyItemString(item));
		sender.sendMessage(ChatColor.ITALIC + "Effects:");
		for(PotionEffect effect : kit.effects)
			sender.sendMessage(ChatColor.ITALIC + "  - " + ItemController.friendlyEffectString(effect));
		sender.sendMessage(ChatColor.ITALIC + "Permissions:");
		for(String perm : kit.permissions)
			sender.sendMessage(ChatColor.ITALIC + "  - " + perm);
		for(Attribute attribute : kit.attributes.keySet())
			sender.sendMessage(ChatColor.ITALIC + attribute.toString() + ": " + kit.getAttribute(attribute));
	}

	@CommandHandler(cmd = "reloadkits", permissions = "kitmaster.reload", permissionMessage = "You don't have permission to reload kits")
	public void reloadkits(CommandSender sender, String[] args){
		try {
			KitMaster.reloadKits();
			sender.sendMessage(ChatColor.ITALIC + "Kits have been reloaded");
		} catch (Exception e) {
			e.printStackTrace();
			sender.sendMessage(ChatColor.ITALIC + "An error occurred while reloading kits");
		}
	}

	@CommandHandler(cmd = "itemmeta savebook", permissions = "kitmaster.meta", permissionMessage = "You don't have permission to manage item metadata")
	public void savebook(Player player, String[] args){
		if(player.getItemInHand().getType() != Material.WRITTEN_BOOK && player.getItemInHand().getType() != Material.BOOK_AND_QUILL){
			player.sendMessage(ChatColor.ITALIC + "You need to hold a written book before using this command");
			return;
		}
		if(args.length == 0){
			player.sendMessage(ChatColor.ITALIC + "Include an identifier to save the book with");
			return;
		}
		BookHandler.saveBook(player.getItemInHand(), args[0]);
		KitMaster.saveCustomData();
		player.sendMessage(ChatColor.ITALIC + "Successfully saved the book under the name " + args[0]);
	}

	@CommandHandler(cmd = "itemmeta loadbook", permissions = "kitmaster.meta", permissionMessage = "You don't have permission to manage item metadata")
	public void loadbook(Player player, String[] args){
		if(args.length == 0){
			player.sendMessage(ChatColor.ITALIC + "Include the identifier of the book to load");
			return;
		}
		if(!BookHandler.isBook(args[0])){
			player.sendMessage(ChatColor.ITALIC + "No book is saved under that name");
			return;
		}
		player.getInventory().addItem(BookHandler.getBook(args[0]));
		player.sendMessage(ChatColor.ITALIC + "Successfully loaded the book named " + args[0]);
	}

	@CommandHandler(cmd = "itemmeta editbook", permissions = "kitmaster.meta", permissionMessage = "You don't have permission to manage item metadata")
	public void editbook(Player player, String[] args){
		if(args.length == 0){
			player.sendMessage(ChatColor.ITALIC + "Include the identifier of the book to edit");
			return;
		}
		if(!BookHandler.isBook(args[0])){
			player.sendMessage(ChatColor.ITALIC + "No book is saved under that name");
			return;
		}
		player.getInventory().addItem(BookHandler.getEditableBook(args[0]));
		player.sendMessage(ChatColor.ITALIC + "Successfully loaded the book named " + args[0]);
	}
	
	@CommandHandler(cmd = "itemmeta savefirework", permissions = "kitmaster.meta", permissionMessage = "You don't have permission to manage item metadata")
	public void savefirework(Player player, String[] args){
		if(player.getItemInHand().getType() != Material.FIREWORK){
			player.sendMessage(ChatColor.ITALIC + "You need to hold a firework before using this command");
			return;
		}
		if(args.length == 0){
			player.sendMessage(ChatColor.ITALIC + "Include an identifier to save the firework with");
			return;
		}
		FireworkHandler.saveFirework(player.getItemInHand(), args[0]);
		KitMaster.saveCustomData();
		player.sendMessage(ChatColor.ITALIC + "Successfully saved the firework under the name " + args[0]);
	}

	@CommandHandler(cmd = "itemmeta loadfirework", permissions = "kitmaster.meta", permissionMessage = "You don't have permission to manage item metadata")
	public void loadfirework(Player player, String[] args){
		if(args.length == 0){
			player.sendMessage(ChatColor.ITALIC + "Include the identifier of the firework to load");
			return;
		}
		if(!FireworkHandler.isFirework(args[0])){
			player.sendMessage(ChatColor.ITALIC + "No firework is saved under that name");
			return;
		}
		player.getInventory().addItem(FireworkHandler.getFirework(args[0]));
		player.sendMessage(ChatColor.ITALIC + "Successfully loaded the firework named " + args[0]);
	}

	@CommandHandler(cmd = "itemmeta setname", permissions = "kitmaster.meta", permissionMessage = "You don't have permission to manage item metadata")
	public void setname(Player player, String[] args){
		if(player.getItemInHand() == null){
			player.sendMessage(ChatColor.ITALIC + "You need to hold an item before using this command");
			return;
		}
		if(args.length == 0){
			player.sendMessage(ChatColor.ITALIC + "Include a new name for the item");
			return;
		}
		String name = ChatColor.translateAlternateColorCodes('&', concatArgs(args));
		ItemMeta meta = player.getItemInHand().getItemMeta();
		meta.setDisplayName(ChatColor.RESET + name);
		player.getItemInHand().setItemMeta(meta);
		player.sendMessage(ChatColor.ITALIC + "Renamed your held item to " + name);
	}
	
	@CommandHandler(cmd = "itemmeta addlore", permissions = "kitmaster.meta", permissionMessage = "You don't have permission to manage item metadata")
	public void addlore(Player player, String[] args){
		if(player.getItemInHand() == null){
			player.sendMessage(ChatColor.ITALIC + "You need to hold an item before using this command");
			return;
		}
		if(args.length == 0){
			player.sendMessage(ChatColor.ITALIC + "Include a new line of lore for the item");
			return;
		}
		String line = ChatColor.translateAlternateColorCodes('&', concatArgs(args));
		ItemMeta meta = player.getItemInHand().getItemMeta();
		List<String> lore = meta.getLore();
		if(lore == null)
			lore = new ArrayList<String>();
		lore.add(ChatColor.RESET + line);
		meta.setLore(lore);
		player.getItemInHand().setItemMeta(meta);
		player.sendMessage(ChatColor.ITALIC + "Added \"" + line + ChatColor.RESET + ChatColor.ITALIC + "\" to your held item");
	}
	
	@CommandHandler(cmd = "itemmeta removelore", permissions = "kitmaster.meta", permissionMessage = "You don't have permission to manage item metadata")
	public void removelore(Player player, String[] args){
		if(player.getItemInHand() == null){
			player.sendMessage(ChatColor.ITALIC + "You need to hold an item before using this command");
			return;
		}
		if(args.length == 0){
			player.sendMessage(ChatColor.ITALIC + "Include the line number to remove");
			return;
		}
		int num;
		try{
			num = Integer.parseInt(args[0]);
		}
		catch(NumberFormatException nfe){
			player.sendMessage(ChatColor.ITALIC + "Include the line number to remove");
			return;
		}
		ItemMeta meta = player.getItemInHand().getItemMeta();
		List<String> lore = meta.getLore();
		if(lore == null)
			lore = new ArrayList<String>();
		try{
			lore.remove(num - 1);
		}
		catch(IndexOutOfBoundsException iobe){
			player.sendMessage(ChatColor.ITALIC + "Your held item doesn't have that many lines of lore");
			return;
		}
		meta.setLore(lore);
		player.getItemInHand().setItemMeta(meta);
		player.sendMessage(ChatColor.ITALIC + "Removed line " + num + " from your held item");
	}

	@CommandHandler(cmd = "itemmeta saveitem", permissions = "kitmaster.meta", permissionMessage = "You don't have permission to manage item metadata")
	public void savemeta(Player player, String[] args){
		if(player.getItemInHand() == null){
			player.sendMessage(ChatColor.ITALIC + "You need to hold an item before using this command");
			return;
		}
		if(args.length == 0){
			player.sendMessage(ChatColor.ITALIC + "Include an identifier for this custom item");
			return;
		}
		CustomItemHandler.saveCustomItem(player.getItemInHand(), args[0]);
		KitMaster.saveCustomData();
		player.sendMessage(ChatColor.ITALIC + "Successfully saved the item under the name " + args[0]);
	}
	
	@CommandHandler(cmd = "itemmeta loaditem", permissions = "kitmaster.meta", permissionMessage = "You don't have permission to manage item metadata")
	public void loadmeta(Player player, String[] args){
		if(args.length == 0){
			player.sendMessage(ChatColor.ITALIC + "Include the identifier of the custom item");
			return;
		}
		if(!CustomItemHandler.isCustomItem(args[0])){
			player.sendMessage(ChatColor.ITALIC + "No item is saved under that name");
			return;
		}
		try {
			player.getInventory().addItem(CustomItemHandler.getCustomItem(args[0]));
		}
		catch (Exception e) {
			e.printStackTrace();
			player.sendMessage(ChatColor.ITALIC + "An error occurred while loading the item");
			return;
		}
		player.sendMessage(ChatColor.ITALIC + "Successfully loaded the item named " + args[0]);
	}
	
	/*

	@CommandHandler(cmd = "editkit")
	public void editkit(CommandSender sender){
		if(KitHandler.editKit == null)
			sender.sendMessage(ChatColor.ITALIC + "There is no kit currently being edited");
		else
			sender.sendMessage(ChatColor.ITALIC + "The " + KitHandler.editKit.name + " kit is currently being edited");
	}
	
	@CommandHandler(cmd = "editkit select")
	public void selectedit(CommandSender sender, String[] args){
		if(args.length == 0){
			sender.sendMessage(ChatColor.ITALIC + "Include the name of the kit to edit");
			return;
		}
		if(!KitHandler.isKit(args[0])){
			sender.sendMessage(ChatColor.ITALIC + "That kit does not exist");
			return;
		}
		Bukkit.dispatchCommand(sender, "editkit save");
		KitHandler.editKit = KitHandler.getKit(args[0]);
		sender.sendMessage(ChatColor.ITALIC + "You are now editing the " + KitHandler.editKit.name + " kit");
	}
	
	@CommandHandler(cmd = "editkit save")
	public void saveedit(CommandSender sender, String[] args){
		if(KitHandler.editKit == null){
			sender.sendMessage(ChatColor.ITALIC + "There is no kit currently being edited");
			return;
		}
		KitHandler.saveKit(KitHandler.editKit);
		sender.sendMessage(ChatColor.ITALIC + "The " + KitHandler.editKit.name + " kit has been saved");
		KitHandler.editKit = null;
	}
	
	@CommandHandler(cmd = "editkit view")
	public void viewedit(CommandSender sender, String[] args){
		if(KitHandler.editKit == null){
			sender.sendMessage(ChatColor.ITALIC + "There is no kit currently being edited");
			return;
		}
		Kit kit = KitHandler.editKit;
		sender.sendMessage(ChatColor.ITALIC + "Kit info for " + kit.name);
		sender.sendMessage(ChatColor.ITALIC + "Items:");
		for(ItemStack item : kit.items)
			sender.sendMessage(ChatColor.ITALIC + "  - " + ItemController.friendlyItemString(item));
		sender.sendMessage(ChatColor.ITALIC + "Effects:");
		for(PotionEffect effect : kit.effects)
			sender.sendMessage(ChatColor.ITALIC + "  - " + ItemController.friendlyEffectString(effect));
		sender.sendMessage(ChatColor.ITALIC + "Permissions:");
		for(String perm : kit.permissions)
			sender.sendMessage(ChatColor.ITALIC + "  - " + perm);
		for(Attribute attribute : kit.attributes.keySet())
			sender.sendMessage(ChatColor.ITALIC + attribute.toString() + ": " + kit.getAttribute(attribute));
	}
	
	@CommandHandler(cmd = "editkit setattribute")
	public void setattribute(CommandSender sender, String[] args){
		if(KitHandler.editKit == null){
			sender.sendMessage(ChatColor.ITALIC + "There is no kit currently being edited");
			return;
		}
		if(args.length < 2){
			sender.sendMessage(ChatColor.ITALIC + "Include an attribute and the value to set it to");
			return;
		}
//		Attribute type = Attribute.matchName(args[0]);
//		TODO
	}
	
	*/
	
	@CommandHandler(cmd = "inventory-kit", permissions = "kitmaster.edit", permissionMessage = "You don't have permission to create kits")
	public void inventoryKit(Player player, String[] args){
		if(args.length == 0){
			player.sendMessage(ChatColor.ITALIC + "Include the name of the new kit");
			return;
		}
		if(KitHandler.isKit(args[0])){
			player.sendMessage(ChatColor.ITALIC + "That kit name is already in use");
			return;
		}
		Kit newKit = new Kit(args[0], Lists.newArrayList(player.getInventory().getContents()), new ArrayList<PotionEffect>(), new ArrayList<String>(), new HashMap<Attribute, Object>());
		KitHandler.saveKit(newKit);
		KitMaster.saveCustomData();
		player.sendMessage(ChatColor.ITALIC + "The kit named " + args[0] + " has been created from the contents of your inventory");
		player.sendMessage(ChatColor.ITALIC + "It has been saved to " + KitMaster.KITS_DIR + "/" + args[0] + ".kit");
	}
	
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		if(args.length == 0)
			return null;
		
		if(command.getName().equals("kit") || command.getName().equals("kitinfo") || (command.getName().equals("givekit") && args.length == 2)){
			List<String> names = new ArrayList<String>();
			String partial = args[args.length - 1];
			for(Kit kit : KitHandler.getKits()){
				PermsResult perms = KitHandler.getKitPerms(sender, kit);
				if(perms.generic == GenericResult.YES || perms == PermsResult.COMMAND_ONLY || perms == PermsResult.INHERIT_COMMAND_ONLY)
					if(kit.applyParentAttributes().booleanAttribute(Attribute.SHOW_IN_LIST))
						if(partial.isEmpty() || kit.name.toLowerCase().startsWith(partial.toLowerCase()))
							names.add(kit.name);
			}
			return names;
		}
		
		return null;
	}
	
}