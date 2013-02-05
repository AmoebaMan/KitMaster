package com.amoebaman.kitmaster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import com.amoebaman.kitmaster.controllers.ItemController;
import com.amoebaman.kitmaster.enums.Attribute;
import com.amoebaman.kitmaster.enums.GenericResult;
import com.amoebaman.kitmaster.enums.GiveKitContext;
import com.amoebaman.kitmaster.enums.GiveKitResult;
import com.amoebaman.kitmaster.enums.PermsResult;
import com.amoebaman.kitmaster.handlers.BookHandler;
import com.amoebaman.kitmaster.handlers.CustomItemHandler;
import com.amoebaman.kitmaster.handlers.FireworkEffectHandler;
import com.amoebaman.kitmaster.handlers.FireworkHandler;
import com.amoebaman.kitmaster.handlers.KitHandler;
import com.amoebaman.kitmaster.objects.Kit;
import com.amoebaman.kitmaster.utilities.CommandController;
import com.amoebaman.kitmaster.utilities.CommandController.CommandHandler;
import com.amoebaman.kitmaster.utilities.CommandController.SubCommandHandler;

public class KitMasterCommandHandler implements TabCompleter{

	public static KitMaster plugin;
	public static void init(KitMaster instance){
		CommandController.registerCommands(instance, new KitMasterCommandHandler());
	}

	public static String concatArgs(String... args){
		String result = "";
		for(String str : args)
			result += str + " ";
		return result.trim();
	}
	
	@CommandHandler(name = "kit")
	public void kit(Player player, String[] args){
		if (args.length < 1)
			return;
		if(!KitHandler.isKit(args[0])){
			player.sendMessage(ChatColor.ITALIC + "That kit does not exist");
			return;
		}
		Kit kit = KitHandler.getKit(args[0]);
		GiveKitResult result = KitMaster.giveKit(player, kit, GiveKitContext.COMMAND_TAKEN);
		if(KitMaster.DEBUG_KITS)
			KitMaster.logger().info("Result: " + result.name());
		return;
	}

	@CommandHandler(name = "givekit")
	@SubCommandHandler(parent = "kit", name = "give")
	public void givekit(CommandSender sender, String[] args){
		if (args.length < 2){
			sender.sendMessage(ChatColor.ITALIC + plugin.getCommand("givekit").getUsage());
			return;
		}
		Player target = Bukkit.getPlayer(args[0]);
		if(!KitHandler.isKit(args[1])){
			sender.sendMessage(ChatColor.ITALIC + "That kit does not exist");
			return;
		}
		Kit kit = KitHandler.getKit(args[1]);
		GiveKitResult result = KitMaster.giveKit(target, kit, GiveKitContext.COMMAND_GIVEN);
		if(KitMaster.DEBUG_KITS)
			KitMaster.logger().info("Result: " + result.name());
		target.sendMessage(ChatColor.ITALIC + "You have been given the " + kit.name + " kit");
		sender.sendMessage(ChatColor.ITALIC + target.getName() + " has been given the " + kit.name + " kit");
		return;
	}

	@CommandHandler(name = "kitlist")
	@SubCommandHandler(parent = "kit", name = "list")
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

	@CommandHandler(name = "kitinfo")
	@SubCommandHandler(parent = "kit", name = "info")
	public void kitinfo(CommandSender sender, String[] args){
		if(args.length < 1){
			sender.sendMessage(ChatColor.ITALIC + "Specify a kit - /kitinfo <kitname>");
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

	@CommandHandler(name = "reloadkits")
	@SubCommandHandler(parent = "kit", name = "reload")
	public void reloadkits(CommandSender sender, String[] args){
		try {
			KitMaster.reloadKits();
			sender.sendMessage(ChatColor.ITALIC + "Kits have been reloaded");
		} catch (Exception e) {
			e.printStackTrace();
			sender.sendMessage(ChatColor.ITALIC + "An error occurred while reloading kits");
		}
	}

	@SubCommandHandler(parent = "itemmeta", name = "savebook")
	public void savebook(Player player, String[] args){
		if(player.getItemInHand().getType() != Material.WRITTEN_BOOK && player.getItemInHand().getType() != Material.BOOK_AND_QUILL){
			player.sendMessage(ChatColor.ITALIC + "You need to hold a written book before using this command");
			return;
		}
		if(args.length == 0){
			player.sendMessage(ChatColor.ITALIC + plugin.getCommand("savebook").getUsage());
			return;
		}
		BookHandler.saveBook(player.getItemInHand(), args[0]);
		try {
			BookHandler.save(KitMaster.booksFile);
		}
		catch (IOException ioe) { 
			ioe.printStackTrace();
			player.sendMessage(ChatColor.ITALIC + "An error occurred while saving the book");
			return;
		}
		player.sendMessage(ChatColor.ITALIC + "Successfully saved the book under the name " + args[0]);
	}

	@SubCommandHandler(parent = "itemmeta", name = "loadbook")
	public void loadbook(Player player, String[] args){
		if(args.length == 0){
			player.sendMessage(ChatColor.ITALIC + plugin.getCommand("loadbook").getUsage());
			return;
		}
		if(!BookHandler.isBook(args[0])){
			player.sendMessage(ChatColor.ITALIC + "No book is saved under that name");
			return;
		}
		player.getInventory().addItem(BookHandler.getBook(args[0]));
		player.sendMessage(ChatColor.ITALIC + "Successfully loaded the book named " + args[0]);
	}

	@SubCommandHandler(parent = "itemmeta", name = "editbook")
	public void editbook(Player player, String[] args){
		if(args.length == 0){
			player.sendMessage(ChatColor.ITALIC + plugin.getCommand("editbook").getUsage());
			return;
		}
		if(!BookHandler.isBook(args[0])){
			player.sendMessage(ChatColor.ITALIC + "No book is saved under that name");
			return;
		}
		player.getInventory().addItem(BookHandler.getEditableBook(args[0]));
		player.sendMessage(ChatColor.ITALIC + "Successfully loaded the book named " + args[0]);
	}
	
	@SubCommandHandler(parent = "itemmeta", name = "savefirework")
	public void savefirework(Player player, String[] args){
		if(player.getItemInHand().getType() != Material.FIREWORK){
			player.sendMessage(ChatColor.ITALIC + "You need to hold a firework before using this command");
			return;
		}
		if(args.length == 0){
			player.sendMessage(ChatColor.ITALIC + plugin.getCommand("savefirework").getUsage());
			return;
		}
		FireworkHandler.saveFirework(player.getItemInHand(), args[0]);
		try {
			FireworkEffectHandler.save(KitMaster.fireworkEffectsFile);
			FireworkHandler.save(KitMaster.fireworksFile);
		}
		catch (IOException ioe) { 
			ioe.printStackTrace();
			player.sendMessage(ChatColor.ITALIC + "An error occurred while saving the firework");
			return;
		}
		player.sendMessage(ChatColor.ITALIC + "Successfully saved the firework under the name " + args[0]);
	}

	@SubCommandHandler(parent = "itemmeta", name = "loadfirework")
	public void loadfirework(Player player, String[] args){
		if(args.length == 0){
			player.sendMessage(ChatColor.ITALIC + plugin.getCommand("loadfirework").getUsage());
			return;
		}
		if(!FireworkHandler.isFirework(args[0])){
			player.sendMessage(ChatColor.ITALIC + "No firework is saved under that name");
			return;
		}
		player.getInventory().addItem(FireworkHandler.getFirework(args[0]));
		player.sendMessage(ChatColor.ITALIC + "Successfully loaded the firework named " + args[0]);
	}

	@SubCommandHandler(parent = "itemmeta", name = "setname")
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
	
	@SubCommandHandler(parent = "itemmeta", name = "addlore")
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
	
	@SubCommandHandler(parent = "itemmeta", name = "removelore")
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

	@SubCommandHandler(parent = "itemmeta", name = "saveitem")
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
		try {
			CustomItemHandler.save(KitMaster.itemsFile);
		}
		catch (IOException ioe) { 
			ioe.printStackTrace();
			player.sendMessage(ChatColor.ITALIC + "An error occurred while saving the item");
			return;
		}
		player.sendMessage(ChatColor.ITALIC + "Successfully saved the item under the name " + args[0]);
	}
	
	@SubCommandHandler(parent = "itemmeta", name = "loaditem")
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
	
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equals("kit")){
			List<String> names = new ArrayList<String>();
			String partial = "";
			if(args.length > 0)
				partial += args[0];
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