package com.amoebaman.kitmaster.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import net.milkbowl.vault.item.Items;
import net.milkbowl.vault.item.ItemInfo;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.amoebaman.kitmaster.utilities.ParseItemException;
import com.amoebaman.kitmaster.KitMaster;
import com.amoebaman.kitmaster.handlers.InventoryUtil.Armor.ArmorLevel;
import com.amoebaman.kitmaster.handlers.InventoryUtil.Armor.ArmorType;
import com.amoebaman.kitmaster.handlers.InventoryUtil.Weapon.WeaponLevel;
import com.amoebaman.kitmaster.handlers.InventoryUtil.Weapon.WeaponType;

public class InventoryUtil {

	private static final int MAX_SLOT_INDEX = 35;
	
	/**
	 * Represents a piece of armor,
	 * @author Dennison
	 */
	public static class Armor{
		/** The level of the armor. */
		public ArmorLevel lvl;
		/** The type of the armor. */
		public ArmorType type;
		protected Armor(ArmorType type, ArmorLevel lvl){this.lvl=lvl; this.type=type;}
		/** The levels of armor.  Higher ordinals correspond to more powerful armor. */
		public enum ArmorLevel{
			/** Mob heads, not yet implemented */
			MOB_HEAD,
			/** Leather armor */
			LEATHER,
			/** Golden (butter_ armor */
			GOLD,
			/** Chainmail armor */
			CHAIN,
			/** Iron armor */
			IRON,
			/** Diamond armor */
			DIAMOND
		};
		/** The types of armor. */
		public enum ArmorType{
			/** Helmet armor */
			HAT,
			/** Chestplate armor */
			SHIRT,
			/** Leggings armor */
			PANTS,
			/** Boots armor */
			BOOTS
		};
	}
	/** Stores the <code>Armor</code> that corresponds to each <code>Material</code>. */
	public static final HashMap<Material,Armor> armor;
	static {
		armor = new HashMap<Material,Armor>();
		
		armor.put(Material.LEATHER_HELMET,new Armor(ArmorType.HAT, ArmorLevel.LEATHER));
		armor.put(Material.GOLD_HELMET,new Armor(ArmorType.HAT, ArmorLevel.GOLD));
		armor.put(Material.CHAINMAIL_HELMET,new Armor(ArmorType.HAT, ArmorLevel.CHAIN));
		armor.put(Material.IRON_HELMET,new Armor(ArmorType.HAT, ArmorLevel.IRON));
		armor.put(Material.DIAMOND_HELMET,new Armor(ArmorType.HAT, ArmorLevel.DIAMOND));

		armor.put(Material.LEATHER_CHESTPLATE,new Armor(ArmorType.SHIRT,ArmorLevel.LEATHER));
		armor.put(Material.GOLD_CHESTPLATE,new Armor(ArmorType.SHIRT,ArmorLevel.GOLD));
		armor.put(Material.CHAINMAIL_CHESTPLATE,new Armor(ArmorType.SHIRT,ArmorLevel.CHAIN));
		armor.put(Material.IRON_CHESTPLATE,new Armor(ArmorType.SHIRT,ArmorLevel.IRON));
		armor.put(Material.DIAMOND_CHESTPLATE,new Armor(ArmorType.SHIRT,ArmorLevel.DIAMOND));

		armor.put(Material.LEATHER_LEGGINGS,new Armor(ArmorType.PANTS,ArmorLevel.LEATHER));
		armor.put(Material.GOLD_LEGGINGS,new Armor(ArmorType.PANTS,ArmorLevel.GOLD));
		armor.put(Material.CHAINMAIL_LEGGINGS,new Armor(ArmorType.PANTS,ArmorLevel.CHAIN));
		armor.put(Material.IRON_LEGGINGS,new Armor(ArmorType.PANTS,ArmorLevel.IRON));
		armor.put(Material.DIAMOND_LEGGINGS,new Armor(ArmorType.PANTS,ArmorLevel.DIAMOND));
		
		armor.put(Material.LEATHER_BOOTS,new Armor(ArmorType.BOOTS,ArmorLevel.LEATHER));
		armor.put(Material.GOLD_BOOTS,new Armor(ArmorType.BOOTS,ArmorLevel.GOLD));
		armor.put(Material.CHAINMAIL_BOOTS,new Armor(ArmorType.BOOTS,ArmorLevel.CHAIN));
		armor.put(Material.IRON_BOOTS,new Armor(ArmorType.BOOTS,ArmorLevel.IRON));
		armor.put(Material.DIAMOND_BOOTS,new Armor(ArmorType.BOOTS,ArmorLevel.DIAMOND));
	}
	
	/**
	 * Represents a weapon
	 * @author Dennison
	 */
	public static class Weapon{
		/** The level of the weapon. */
		public WeaponLevel lvl;
		/** The type of the weapon. */
		public WeaponType type;
		Weapon(WeaponType type, WeaponLevel lvl){this.lvl=lvl; this.type=type;}
		/** The levels of weapon.  Higher ordinals correspond to more powerful weapons. */
		public enum WeaponLevel{
			/** Wooden weapons */
			WOOD,
			/** Stone weapons */
			STONE,
			/** Golden (butter) weapons */
			GOLD,
			/** Iron weapons */
			IRON,
			/** Diamond weapons */
			DIAMOND
		};
		/** The types of weapon.  Higher ordinals correspond to more powerful weapons. */
		public enum WeaponType{
			/** Axes */
			AXE,
			/** Bows */
			BOW,
			/** Swords */
			SWORD
		};
	}
	/** Stores the <code>Weapon</code> that corresponds to each <code>Material</code>. */
	public static final HashMap<Material,Weapon> weapons;
	static {
		weapons = new HashMap<Material,Weapon>();
		
		weapons.put(Material.WOOD_SWORD,new Weapon(WeaponType.SWORD, WeaponLevel.WOOD));
		weapons.put(Material.STONE_SWORD,new Weapon(WeaponType.SWORD, WeaponLevel.STONE));
		weapons.put(Material.GOLD_SWORD,new Weapon(WeaponType.SWORD, WeaponLevel.GOLD));
		weapons.put(Material.IRON_SWORD,new Weapon(WeaponType.SWORD, WeaponLevel.IRON));
		weapons.put(Material.DIAMOND_SWORD,new Weapon(WeaponType.SWORD, WeaponLevel.DIAMOND));
		
		weapons.put(Material.WOOD_AXE,new Weapon(WeaponType.AXE, WeaponLevel.WOOD));
		weapons.put(Material.STONE_AXE,new Weapon(WeaponType.AXE, WeaponLevel.STONE));
		weapons.put(Material.GOLD_AXE,new Weapon(WeaponType.AXE, WeaponLevel.GOLD));
		weapons.put(Material.IRON_AXE,new Weapon(WeaponType.AXE, WeaponLevel.IRON));
		weapons.put(Material.DIAMOND_AXE,new Weapon(WeaponType.AXE, WeaponLevel.DIAMOND));

		weapons.put(Material.BOW,new Weapon(WeaponType.BOW, WeaponLevel.WOOD));
	}
	
	/**
	 * Stores both the type and level of an enchantment.
	 * @author Dennison
	 */
	public static class EnchantmentWithLevel{
		/** The enchantment type. */
		public Enchantment enc;
		/** The enchantment level. */
		public Integer lvl;
		public String toString(){return(enc!=null?enc.getName():"null")+":"+lvl;}
	}
	
	/**
	 * Gets an <code>Enchantment</code> by it's more common name.
	 * @param name The common name of the desired enchantment.
	 * @return The enchantment whose common name matches the given name.
	 */
	public static Enchantment getEnchantmentByCommonName(String name){
		name = name.toLowerCase();
		if(name.contains("fire") && name.contains("prot")) return Enchantment.PROTECTION_FIRE;
		if((name.contains("exp") || name.contains("blast")) && name.contains("prot")) return Enchantment.PROTECTION_EXPLOSIONS;
		if((name.contains("arrow") || name.contains("proj")) && name.contains("prot")) return Enchantment.PROTECTION_PROJECTILE;
		if(name.contains("prot")) return Enchantment.PROTECTION_ENVIRONMENTAL;
		if(name.contains("fall")) return Enchantment.PROTECTION_FALL;
		if(name.contains("respiration")) return Enchantment.OXYGEN;
		if(name.contains("aqua")) return Enchantment.WATER_WORKER;
		if(name.contains("sharp")) return Enchantment.DAMAGE_ALL;
		if(name.contains("smite")) return Enchantment.DAMAGE_UNDEAD;
		if(name.contains("arth")) return Enchantment.DAMAGE_ARTHROPODS;
		if(name.contains("knockback")) return Enchantment.KNOCKBACK;
		if(name.contains("fire")) return Enchantment.FIRE_ASPECT;
		if(name.contains("loot")) return Enchantment.LOOT_BONUS_MOBS;
		if(name.contains("power")) return Enchantment.ARROW_DAMAGE;
		if(name.contains("punch")) return Enchantment.ARROW_KNOCKBACK;
		if(name.contains("flame")) return Enchantment.ARROW_FIRE;
		if(name.contains("infin")) return Enchantment.ARROW_INFINITE; 
		if(name.contains("dig") || name.contains("eff")) return Enchantment.DIG_SPEED;
		if(name.contains("dura") || name.contains("break")) return Enchantment.DURABILITY;
		if(name.contains("silk")) return Enchantment.SILK_TOUCH;
		if(name.contains("fort")) return Enchantment.LOOT_BONUS_BLOCKS;return null;
	}
	
	
	/**
	 * Gets a <code>PotionEffectType</code> by it's more common name.
	 * @param name The common name of the desired effect.
	 * @return The effect whose common name matches the given name.
	 */
	public static PotionEffectType getEffectByCommonName(String name){
		name = name.toLowerCase();
		if(name.contains("regen")) return PotionEffectType.REGENERATION;
		if(name.contains("poison")) return PotionEffectType.POISON;
		if(name.contains("strength")) return PotionEffectType.INCREASE_DAMAGE;
		if(name.contains("weak")) return PotionEffectType.WEAKNESS;
		if(name.contains("heal")) return PotionEffectType.HEAL;
		if(name.contains("harm")) return PotionEffectType.HARM;
		if(name.contains("speed") || name.contains("swift")) return PotionEffectType.SPEED;
		if(name.contains("slow")) return PotionEffectType.SLOW;
		if(name.contains("haste")) return PotionEffectType.FAST_DIGGING;
		if(name.contains("fat")) return PotionEffectType.SLOW_DIGGING;
		if(name.contains("hung")) return PotionEffectType.HUNGER;
		if(name.contains("resist")) return PotionEffectType.DAMAGE_RESISTANCE;
		if(name.contains("blind")) return PotionEffectType.BLINDNESS;
		if(name.contains("confus") || name.contains("naus")) return PotionEffectType.CONFUSION;
		if(name.contains("fire")) return PotionEffectType.FIRE_RESISTANCE;
		if(name.contains("jump")) return PotionEffectType.JUMP;
		if(name.contains("water") || name.contains("aqua")) return PotionEffectType.WATER_BREATHING;
		if(name.contains("invis")) return PotionEffectType.INVISIBILITY;
		if(name.contains("night")) return PotionEffectType.NIGHT_VISION;
		return null;
	}

	
	/**
	 * Adds a list of <code>ItemStack</code> to a player's inventory.
	 * @param player The player to give the items to.
	 * @param items The items to give.
	 */
	public static void addItemsToInventory(Player player, List<ItemStack> items, boolean upgrade) {
		for (ItemStack stack : items)
			InventoryUtil.addItemToInventory(player, stack, upgrade, false);
	}
	
	/**
	 * Adds an <code>ItemStack</code> to a player's inventory.
	 * @param player The player to give the item to.
	 * @param items The item to give.
	 */
	public static void addItemToInventory(Player player, ItemStack stack, boolean upgrade, boolean reverse) {
		PlayerInventory inv = player.getInventory();
		Material mat = stack.getType();
		if(armor.containsKey(mat)){
			/*
			 * If the new armor is better, equip it and put the old armor in the inventory
			 * If the old armor is better, put the new armor in the inventory
			 */
			ItemStack oldArmor = getArmorSlot(inv,armor.get(mat));
			boolean empty = (oldArmor == null || oldArmor.getType() == Material.AIR);
			boolean better = empty ? true : armorSlotBetter(armor.get(oldArmor.getType()),armor.get(mat));
			if (empty || better){
				switch (armor.get(mat).type){
				case HAT: inv.setHelmet(stack); break;
				case SHIRT: inv.setChestplate(stack); break;
				case PANTS: inv.setLeggings(stack); break;
				case BOOTS: inv.setBoots(stack); break;
				}
			} 
			if(!empty){
				if(better)
					addItemToInventory(inv, oldArmor, upgrade, true);
				else
					addItemToInventory(inv, stack, upgrade, reverse);
			}		
		}
		else if(weapons.containsKey(mat)){
			ItemStack oldWeapon = inv.getItemInHand();
			boolean empty = oldWeapon == null || oldWeapon.getType() == Material.AIR;
			boolean better = empty ? true : weaponSlotBetter(weapons.get(oldWeapon.getType()), weapons.get(mat));
			if(empty || better)
				inv.setItemInHand(stack);
			if(!empty){
				if(better)
					addItemToInventory(inv, oldWeapon, upgrade, true);
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
		if(stack.getType() == Material.POTION)
			maxStackSize = 16;
		if(stack.getAmount() <= maxStackSize){
			inv.setItem(getAddIndex(inv, reverse), stack);
			return;
		}
		if(maxStackSize != 64){
			ItemStack fullStack = stack.clone();
			List<ItemStack> items = new ArrayList<ItemStack>();
			while(stack.getAmount() > maxStackSize){
				items.add(fullStack.clone());
				stack.setAmount(stack.getAmount() - maxStackSize);
			}
			items.add(stack);
			for(ItemStack item : items)
				inv.setItem(getAddIndex(inv, reverse), item);
		}
		else
			inv.setItem(getAddIndex(inv, reverse), stack);
	}
	
	private static int getAddIndex(Inventory inv, boolean reverse){
		int addIndex;
		if(reverse){
			addIndex = MAX_SLOT_INDEX;
			while(inv.getItem(addIndex) != null)
				addIndex --;
		}
		else{
			addIndex = 0;
			while(inv.getItem(addIndex) != null)
				addIndex ++;
		}
		return addIndex;
	}

	/**
	 * Parses an <code>ItemStack</code> from a String.
	 * @param str The String to parse from.
	 * @return The item parsed.
	 * @throws ParseItemException If <code>str</code> is not formatted properly.
	 */
	public static ItemStack parseItem(String str) throws ParseItemException{
		try{
			String[] enchantSplit = str.split(" ");
			String[] itemSplit = enchantSplit[0].split(":");
			
			Material mat = getMat(itemSplit[0]);
			if(mat == null)
				throw new ParseItemException("invalid item specified", str);
			int amount = 1;
			short data = 0;
			if(itemSplit.length > 1){
				if(isInt(itemSplit[itemSplit.length - 1]))
					amount = Integer.parseInt(itemSplit[itemSplit.length - 1]);
				else{
					System.out.println(mat.name() + "|" + amount + "|" + data + "|" + itemSplit[1]);
					throw new ParseItemException("invalid amount specified", str);
				}
				if(itemSplit.length > 2){
					if(isInt(itemSplit[1]))
						data = Short.parseShort(itemSplit[1]);
					else if(mat == Material.MONSTER_EGG || mat == Material.MOB_SPAWNER){
						EntityType entity = EntityType.fromName(itemSplit[1]);
						if(entity != null && entity.isSpawnable() && entity.isAlive())
							data = entity.getTypeId();
						else{
							System.out.println(mat.name() + "|" + amount + "|" + data + "|" + itemSplit[1]);
							throw new ParseItemException("invalid entity type specified", str);
						}
					}
					else if(mat == Material.WRITTEN_BOOK)
						return KitMaster.getBook(itemSplit[1]);
					else{
						System.out.println(mat.name() + "|" + amount + "|" + data + "|" + itemSplit[1]);
						throw new ParseItemException("invalid data specified", str);
					}
				}
			}
			ItemStack stack = new ItemStack(mat, amount, data);
			
			for (int i = 1; i < enchantSplit.length;i++){
				EnchantmentWithLevel ewl = parseEnchantment(enchantSplit[i].trim());
				if(ewl == null)
					throw new ParseItemException("invalid enchantment specified", str);
				stack.addUnsafeEnchantment(ewl.enc, ewl.lvl);
			}
			
			if(stack.getType() == Material.BOOK || stack.getType() == Material.WRITTEN_BOOK)
				return KitMaster.getBook(itemSplit[1]);
			
			return stack;
		}
		catch(Exception e){
			e.printStackTrace();
			throw new ParseItemException("unable to parse item", str);
		}
	}
	
	/**
	 * Parses a <code>PotionEffect</code> from a String.
	 * @param str The String to parse from.
	 * @return The effect parsed.
	 * @throws ParseItemException If <code>str</code> is not formatted properly.
	 */
	public static PotionEffect parseEffect(String str) throws ParseItemException{
		try{
			String[] split = str.split(":");
			PotionEffectType type = PotionEffectType.getByName(split[0]);
			if(type == null)
				type = getEffectByCommonName(split[0]);
			if(type == null && isInt(split[0])){
				type = PotionEffectType.getById(Integer.parseInt(split[0]));
			}
			if(type == null)
				type = PotionEffectType.getByName(split[0]);
			if(type == null)
				throw new ParseItemException("invalid effect specified", str);
			return new PotionEffect(type, split.length > 2 ? Integer.parseInt(split[2]) * 20 : 1200, split.length > 1 ? Integer.parseInt(split[1]) - 1 : 0);
		}
		catch(NumberFormatException e){
			throw new ParseItemException("invalid amplifier/duration", str);
		}
	}
	
	/**
	 * Parses an <code>EnchantmentWithLevel</code> from a String.
	 * @param str The String to parse from.
	 * @return The enchantment parsed.
	 */
	public static EnchantmentWithLevel parseEnchantment(String str) {
		str = str.toLowerCase();
		String[] split = str.split(":");
		Enchantment enc = Enchantment.getByName(split[0]);
		if(enc == null)
			enc = getEnchantmentByCommonName(split[0]);
		if(enc == null)
			return null;
		int lvl = 1;
		if(split.length > 1)
			lvl = isInt(split[1]) ? Integer.parseInt(split[1]) : 1;
		EnchantmentWithLevel ewl = new EnchantmentWithLevel();
		ewl.enc = enc;
		ewl.lvl = lvl;
		return ewl;
	}

	/**
	 * Gets a material by its enumerated name, common name, or type ID.
	 * @param name The name to search for.
	 * @return The item matched
	 */
	public static Material getMat(String name) {
		name = name.toLowerCase();
		Material mat = null;
		if(mat == null){
			try{ mat = Material.getMaterial(Integer.parseInt(name)); }
			catch(NumberFormatException e){}
		}
		if(mat == null)
			mat = Material.matchMaterial(name);
		if((name.contains("spawn") || name.contains("mob")) && name.contains("egg"))
			mat = Material.MONSTER_EGG;
		if(KitMaster.isVaultEnabled() && mat == null && !name.contains("legg")){
			ItemInfo item = Items.itemByName(name);
			if(item != null)
				mat = item.getType();
		}
		if(name.equalsIgnoreCase("book"))
			mat = Material.BOOK;
		return mat;
	}

	private static ItemStack getArmorSlot(PlayerInventory inv, Armor armor) {
		switch (armor.type){
		case HAT: return inv.getHelmet();
		case SHIRT: return inv.getChestplate();
		case PANTS: return inv.getLeggings();
		case BOOTS:return inv.getBoots();
		}
		return null;
	}

	private static boolean armorSlotBetter(Armor oldArmor, Armor newArmor) {
		if (oldArmor == null || newArmor == null)
			return false;
		return oldArmor.lvl.ordinal() < newArmor.lvl.ordinal();
	}

	private static boolean weaponSlotBetter(Weapon oldWeapon, Weapon newWeapon) {
		if (oldWeapon == null || newWeapon == null)
			return false;
		if(oldWeapon.type == newWeapon.type)
			return oldWeapon.lvl.ordinal() < newWeapon.lvl.ordinal();
		else
			return oldWeapon.type.ordinal() < newWeapon.type.ordinal();
	}
	
	/**
	 * Gets a user-friendly String that represents an <code>ItemStack</code>.
	 * @param stack The item to read.
	 * @return The string.
	 */
	public static String itemToString(ItemStack stack){
		String str = stack.getAmount() + " " + stack.getType().name().toLowerCase().replaceAll("_", " ") + (stack.getData().getData() == 0 ? "" : ":" + stack.getData().getData());
		for(Enchantment enc : stack.getEnchantments().keySet())
			str += " " + enc.getName().toLowerCase().replaceAll("_", "-") + ":" + stack.getEnchantmentLevel(enc);
		return str;
	}

	/**
	 * Gets a user-friendly String that represents an <code>PotionEffect</code>.
	 * @param stack The effect to read.
	 * @return The string.
	 */
	public static String effectToString(PotionEffect effect){
		return effect.getType().getName().toLowerCase().replaceAll("_", "-") + ":" + (effect.getAmplifier() + 1) + " for " + effect.getDuration()/20.0 + " seconds";
	}

	/**
	 * Tests whether a String can be safely interpreted as an int.
	 * @param str The string to test.
	 * @return True if <code>Integer.parseInt(String)</code> can be safely used on <code>str</code>, false otherwise.
	 */
	public static boolean isInt(String str){try{Integer.parseInt(str);return true;}catch(Exception e) {return false;}}
	
}
