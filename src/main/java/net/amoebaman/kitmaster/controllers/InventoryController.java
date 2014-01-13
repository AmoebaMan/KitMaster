package net.amoebaman.kitmaster.controllers;

import java.util.ArrayList;
import java.util.List;

import net.amoebaman.kitmaster.KitMaster;
import net.amoebaman.kitmaster.objects.Armor;
import net.amoebaman.kitmaster.objects.Weapon;
import net.amoebaman.kitmaster.utilities.ParseItemException;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;


public class InventoryController {
	
	/**
	 * Adds a List of ItemStacks to a Player's inventory.
	 * @param player the Player to give the items to
	 * @param items the list of items to give
	 */
	public static void addItemsToInventory(Player player, List<ItemStack> items, boolean upgrade) {
		for (ItemStack stack : items)
			InventoryController.giveItemToPlayer(player, stack, upgrade, false);
	}

	/**
	 * Adds an ItemStack to a Player's inventory.
	 * @param player the Player to give the item to
	 * @param items the item to give
	 */
	public static void giveItemToPlayer(Player player, ItemStack stack, boolean upgrade, boolean reverse) {
		if(stack == null)
			return;
		PlayerInventory inv = player.getInventory();
		/*
		 * Apply item personalization text macros
		 * %player% is replaced with the player's name
		 * %disaply% is replaced with the player's display name
		 */
		ItemMeta meta = stack.getItemMeta();
		if(meta.hasDisplayName())
			meta.setDisplayName(meta.getDisplayName().replace("%player%", player.getName()).replace("%display%", player.getDisplayName()));
		if(meta.hasLore()){
			List<String> lore = meta.getLore();
			for(int i = 0; i < lore.size(); i++)
				lore.set(i, lore.get(i).replace("%player%", player.getName()).replace("%display%", player.getDisplayName()));
			meta.setLore(lore);
		}
		stack.setItemMeta(meta);
		/*
		 * If the material is armor and KitMaster is configured to auto-equip armor...
		 */
		if(Armor.isValid(stack.getType()) && KitMaster.config().getBoolean("inventory.autoEquipArmor", true)){
			/*
			 * If the new armor is better, equip it and put the old armor in the inventory
			 * If the old armor is better, put the new armor in the inventory
			 */
			Armor newArmor = new Armor(stack);
			Armor oldArmor = new Armor(Armor.getExisting(player, newArmor.getType()));
			boolean empty = !oldArmor.isValid();
			boolean better = empty ? true : newArmor.isBetterThan(oldArmor);
			if (empty || better)
				newArmor.putInSlot(player);
			else if(!empty){
				if(better)
					addItemToInventory(inv, oldArmor.getItem(), upgrade, true);
				else
					addItemToInventory(inv, stack, upgrade, reverse);
			}		
		}
		/*
		 * If the material is a weapon and KitMaster is configured to auto-equip weapons...
		 */
		else if(Weapon.isValid(stack.getType()) && KitMaster.config().getBoolean("inventory.autoEquipWeapon", true)){
			/*
			 * If the new weapon is better, equip it and put the old weapon in the inventory
			 * If the old weapon is better, put the new weapon in the inventory
			 */
			Weapon newWeapon = new Weapon(stack);
			Weapon oldWeapon = new Weapon(Weapon.getExisting(player));
			boolean empty = !oldWeapon.isValid();
			boolean better = empty ? true : newWeapon.isBetterThan(oldWeapon);
			if(empty || better)
				newWeapon.putInSlot(player);
			else if(!empty){
				if(better)
					addItemToInventory(inv, oldWeapon.getItem(), upgrade, true);
				else
					addItemToInventory(inv, stack, upgrade, reverse);
			}
		}
		else
			addItemToInventory(inv, stack, upgrade, reverse);
	}

	private static void addItemToInventory(Inventory inv, ItemStack stack, boolean upgrade, boolean reverse){
		if(upgrade && inv.contains(stack.getType())){
			ItemStack existing = inv.getItem(inv.first(stack.getType())).clone();
			if(existing.getData().getData() == stack.getData().getData()){
				inv.remove(stack.getType());
				if(existing.getAmount() > stack.getAmount() && stack.getAmount() > 0)
					stack.setAmount(existing.getAmount());
				for(Enchantment enc : Enchantment.values()){
					int lvl1 = existing.containsEnchantment(enc) ? existing.getEnchantmentLevel(enc) : -1;
					int lvl2 = stack.containsEnchantment(enc) ? stack.getEnchantmentLevel(enc) : -1;
					int newLvl = lvl1 >= lvl2 ? lvl1 : lvl2;
					if(newLvl != -1)
						stack.addUnsafeEnchantment(enc, newLvl);
				}
			}
		}
		int maxStackSize = stack.getType().getMaxStackSize();
		for(String key : KitMaster.config().getConfigurationSection("maxStacks").getKeys(false)){
			try{
				if(ItemController.getBaseStack(key).getType() == stack.getType())
					maxStackSize = KitMaster.config().getInt("maxStacks." + key);
			}
			catch(ParseItemException pie){}
		}
		if(stack.getAmount() <= maxStackSize && inv.firstEmpty() != -1)
			inv.setItem(getAddIndex(inv, reverse), stack);
		else if(maxStackSize != 64){
			ItemStack fullStack = stack.clone();
			fullStack.setAmount(maxStackSize);
			List<ItemStack> items = new ArrayList<ItemStack>();
			while(stack.getAmount() > maxStackSize){
				items.add(fullStack.clone());
				stack.setAmount(stack.getAmount() - maxStackSize);
			}
			items.add(stack);
			for(ItemStack item : items)
				if(inv.firstEmpty() != -1)
					inv.setItem(getAddIndex(inv, reverse), item);
		}
		else if(inv.firstEmpty() != -1)
			inv.setItem(getAddIndex(inv, reverse), stack);
	}

	private static final int MAX_SLOT_INDEX = 35;
	
	private static int getAddIndex(Inventory inv, boolean reverse){
		if(inv.firstEmpty() == -1)
			return -1;
		
		int addIndex;
		if(reverse){
			addIndex = MAX_SLOT_INDEX;
			while(inv.getItem(addIndex) != null)
				addIndex --;
			return addIndex;
		}
		else
			return inv.firstEmpty();
	}

}
