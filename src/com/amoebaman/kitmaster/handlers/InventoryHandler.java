package com.amoebaman.kitmaster.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import net.milkbowl.vault.item.Items;
import net.milkbowl.vault.item.ItemInfo;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.amoebaman.kitmaster.utilities.ParseItemException;
import com.amoebaman.kitmaster.KitMaster;
import com.amoebaman.kitmaster.handlers.InventoryHandler.Armor.ArmorLevel;
import com.amoebaman.kitmaster.handlers.InventoryHandler.Armor.ArmorType;
import com.amoebaman.kitmaster.handlers.InventoryHandler.Weapon.WeaponLevel;
import com.amoebaman.kitmaster.handlers.InventoryHandler.Weapon.WeaponType;

public class InventoryHandler {

	private static final int MAX_SLOT_INDEX = 35;
	public static final HashMap<Material,Armor> armor = new HashMap<Material,Armor>();
	public static final HashMap<Material,Weapon> weapons = new HashMap<Material,Weapon>();

	/**
	 * Represents a piece of armor,
	 * @author Dennison
	 */
	public static class Armor{
		/** The level of the armor. */
		public ArmorLevel lvl;
		/** The type of the armor. */
		public ArmorType type;
		public Armor(ArmorType type, ArmorLevel lvl){this.lvl=lvl; this.type=type;}
		/** The levels of armor.  Higher ordinals correspond to more powerful armor. */
		public enum ArmorLevel{
			/** Mob heads, not yet implemented */
			MOB_HEAD,
			/** Wool hats, optional */
			WOOL,
			/** Leather armor */
			LEATHER,
			/** Golden (butter) armor */
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
	static {
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
	static {
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
		if(name.toLowerCase().contains("fire") && name.toLowerCase().contains("prot")) return Enchantment.PROTECTION_FIRE;
		if((name.toLowerCase().contains("exp") || name.toLowerCase().contains("blast")) && name.toLowerCase().contains("prot")) return Enchantment.PROTECTION_EXPLOSIONS;
		if((name.toLowerCase().contains("arrow") || name.toLowerCase().contains("proj")) && name.toLowerCase().contains("prot")) return Enchantment.PROTECTION_PROJECTILE;
		if(name.toLowerCase().contains("prot")) return Enchantment.PROTECTION_ENVIRONMENTAL;
		if(name.toLowerCase().contains("fall")) return Enchantment.PROTECTION_FALL;
		if(name.toLowerCase().contains("respiration")) return Enchantment.OXYGEN;
		if(name.toLowerCase().contains("aqua")) return Enchantment.WATER_WORKER;
		if(name.toLowerCase().contains("sharp")) return Enchantment.DAMAGE_ALL;
		if(name.toLowerCase().contains("smite")) return Enchantment.DAMAGE_UNDEAD;
		if(name.toLowerCase().contains("arth")) return Enchantment.DAMAGE_ARTHROPODS;
		if(name.toLowerCase().contains("knockback")) return Enchantment.KNOCKBACK;
		if(name.toLowerCase().contains("fire")) return Enchantment.FIRE_ASPECT;
		if(name.toLowerCase().contains("loot")) return Enchantment.LOOT_BONUS_MOBS;
		if(name.toLowerCase().contains("power")) return Enchantment.ARROW_DAMAGE;
		if(name.toLowerCase().contains("punch")) return Enchantment.ARROW_KNOCKBACK;
		if(name.toLowerCase().contains("flame")) return Enchantment.ARROW_FIRE;
		if(name.toLowerCase().contains("infin")) return Enchantment.ARROW_INFINITE; 
		if(name.toLowerCase().contains("dig") || name.toLowerCase().contains("eff")) return Enchantment.DIG_SPEED;
		if(name.toLowerCase().contains("dura") || name.toLowerCase().contains("break")) return Enchantment.DURABILITY;
		if(name.toLowerCase().contains("silk")) return Enchantment.SILK_TOUCH;
		if(name.toLowerCase().contains("fort")) return Enchantment.LOOT_BONUS_BLOCKS;
		if(name.toLowerCase().contains("thorn")) return Enchantment.THORNS;
		return null;
	}
	
	public static String getCommonNameByEnchantment(Enchantment enc){
		if(enc.equals(Enchantment.ARROW_DAMAGE)) return "power";
		if(enc.equals(Enchantment.ARROW_FIRE)) return "flame";
		if(enc.equals(Enchantment.ARROW_INFINITE)) return "infinity";
		if(enc.equals(Enchantment.ARROW_KNOCKBACK)) return "punch";
		if(enc.equals(Enchantment.DAMAGE_ALL)) return "sharpness";
		if(enc.equals(Enchantment.DAMAGE_ARTHROPODS)) return "bane of arthropods";
		if(enc.equals(Enchantment.DAMAGE_UNDEAD)) return "smite";
		if(enc.equals(Enchantment.DIG_SPEED)) return "efficiency";
		if(enc.equals(Enchantment.DURABILITY)) return "unbreaking";
		if(enc.equals(Enchantment.FIRE_ASPECT)) return "fire aspect";
		if(enc.equals(Enchantment.KNOCKBACK)) return "knockback";
		if(enc.equals(Enchantment.LOOT_BONUS_BLOCKS)) return "fortune";
		if(enc.equals(Enchantment.LOOT_BONUS_MOBS)) return "looting";
		if(enc.equals(Enchantment.OXYGEN)) return "respiration";
		if(enc.equals(Enchantment.PROTECTION_ENVIRONMENTAL)) return "protection";
		if(enc.equals(Enchantment.PROTECTION_EXPLOSIONS)) return "blast protection";
		if(enc.equals(Enchantment.PROTECTION_FALL)) return "feather falling";
		if(enc.equals(Enchantment.PROTECTION_FIRE)) return "fire protection";
		if(enc.equals(Enchantment.PROTECTION_PROJECTILE)) return "projectile protection";
		if(enc.equals(Enchantment.SILK_TOUCH)) return "silk touch";
		if(enc.equals(Enchantment.THORNS)) return "thorns";
		if(enc.equals(Enchantment.WATER_WORKER)) return "aqua affinity";
		return null;
	}

	/**
	 * Gets a <code>PotionEffectType</code> by it's more common name.
	 * @param name The common name of the desired effect.
	 * @return The effect whose common name matches the given name.
	 */
	public static PotionEffectType getEffectByCommonName(String name){
		name = name.toLowerCase();
		if(name.toLowerCase().contains("regen")) return PotionEffectType.REGENERATION;
		if(name.toLowerCase().contains("poison")) return PotionEffectType.POISON;
		if(name.toLowerCase().contains("strength")) return PotionEffectType.INCREASE_DAMAGE;
		if(name.toLowerCase().contains("weak")) return PotionEffectType.WEAKNESS;
		if(name.toLowerCase().contains("heal")) return PotionEffectType.HEAL;
		if(name.toLowerCase().contains("harm")) return PotionEffectType.HARM;
		if(name.toLowerCase().contains("speed") || name.toLowerCase().contains("swift")) return PotionEffectType.SPEED;
		if(name.toLowerCase().contains("slow")) return PotionEffectType.SLOW;
		if(name.toLowerCase().contains("haste")) return PotionEffectType.FAST_DIGGING;
		if(name.toLowerCase().contains("fat")) return PotionEffectType.SLOW_DIGGING;
		if(name.toLowerCase().contains("hung")) return PotionEffectType.HUNGER;
		if(name.toLowerCase().contains("resist")) return PotionEffectType.DAMAGE_RESISTANCE;
		if(name.toLowerCase().contains("blind")) return PotionEffectType.BLINDNESS;
		if(name.toLowerCase().contains("confus") || name.toLowerCase().contains("naus")) return PotionEffectType.CONFUSION;
		if(name.toLowerCase().contains("fire")) return PotionEffectType.FIRE_RESISTANCE;
		if(name.toLowerCase().contains("jump")) return PotionEffectType.JUMP;
		if(name.toLowerCase().contains("water") || name.toLowerCase().contains("aqua")) return PotionEffectType.WATER_BREATHING;
		if(name.toLowerCase().contains("invis")) return PotionEffectType.INVISIBILITY;
		if(name.toLowerCase().contains("night")) return PotionEffectType.NIGHT_VISION;
		if(name.toLowerCase().contains("wither")) return PotionEffectType.WITHER;
		return null;
	}
	
	public static String getCommonNameByEffect(PotionEffectType effect){
		if(effect.equals(PotionEffectType.BLINDNESS)) return "blindness";
		if(effect.equals(PotionEffectType.CONFUSION)) return "nausea";
		if(effect.equals(PotionEffectType.DAMAGE_RESISTANCE)) return "resistance";
		if(effect.equals(PotionEffectType.FAST_DIGGING)) return "haste";
		if(effect.equals(PotionEffectType.FIRE_RESISTANCE)) return "fire resistance";
		if(effect.equals(PotionEffectType.HARM)) return "harming";
		if(effect.equals(PotionEffectType.HEAL)) return "instant health";
		if(effect.equals(PotionEffectType.HUNGER)) return "hunger";
		if(effect.equals(PotionEffectType.INCREASE_DAMAGE)) return "strength";
		if(effect.equals(PotionEffectType.INVISIBILITY)) return "invisibility";
		if(effect.equals(PotionEffectType.JUMP)) return "jump boost";
		if(effect.equals(PotionEffectType.NIGHT_VISION)) return "night vision";
		if(effect.equals(PotionEffectType.POISON)) return "poison";
		if(effect.equals(PotionEffectType.REGENERATION)) return "regeneration";
		if(effect.equals(PotionEffectType.SLOW)) return "slowness";
		if(effect.equals(PotionEffectType.SLOW_DIGGING)) return "mining fatigue";
		if(effect.equals(PotionEffectType.SPEED)) return "swiftness";
		if(effect.equals(PotionEffectType.WATER_BREATHING)) return "water breathing";
		if(effect.equals(PotionEffectType.WEAKNESS)) return "weakness";
		if(effect.equals(PotionEffectType.WITHER)) return "wither";
		return null;
	}

	public static short getPotionDataByCommonName(String name){
		name = name.toLowerCase();
		if(isInt(name))
			return Short.parseShort(name);
		short data = 0;
		if(name.contains("water")) data = 0;
		if(name.contains("awkward")) data = 16;
		if(name.contains("thick")) data = 32;
		if(name.contains("mundane")) data = 8192;
		if(name.contains("clear")) data = 7;
		if(name.contains("diffuse")) data = 11;
		if(name.contains("artless")) data = 13;
		if(name.contains("thin")) data = 15;
		if(name.contains("bungling")) data = 23;
		if(name.contains("smooth")) data = 27;
		if(name.contains("suave")) data = 29;
		if(name.contains("debonair")) data = 31;
		if(name.contains("charming")) data = 39;
		if(name.contains("refined")) data = 43;
		if(name.contains("cordial")) data = 45;
		if(name.contains("sparkling")) data = 47;
		if(name.contains("potent")) data = 48;
		if(name.contains("rank")) data = 55;
		if(name.contains("acrid")) data = 59;
		if(name.contains("gross")) data = 61;
		if(name.contains("stinky")) data = 63;
		
		if(name.contains("regen")) data = 1;
		if(name.contains("speed") || name.contains("swift")) data = 2;
		if(name.contains("fire")) data = 3;
		if(name.contains("poison")) data = 4;
		if(name.contains("heal")) data = 5;
		if(name.contains("night")) data = 6;
		if(name.contains("weak")) data = 8;
		if(name.contains("strength")) data = 9;
		if(name.contains("slow")) data = 10;
		if(name.contains("harm") || name.contains("damage")) data = 12;
		if(name.contains("invis")) data = 14;

		if(name.contains("power") || name.contains("level")) data += 32;
		if(name.contains("ext") || name.contains("long")) data += 64;
		if(name.contains("splash") || name.contains("throw")) data += 16384; else data += 8192;
		
		return data;
	}
	
	/**
	 * Adds a list of <code>ItemStack</code> to a player's inventory.
	 * @param player The player to give the items to.
	 * @param items The items to give.
	 */
	public static void addItemsToInventory(Player player, List<ItemStack> items, boolean upgrade) {
		for (ItemStack stack : items)
			InventoryHandler.giveItemToPlayer(player, stack, upgrade, false);
	}

	/**
	 * Adds an <code>ItemStack</code> to a player's inventory.
	 * @param player The player to give the item to.
	 * @param items The item to give.
	 */
	public static void giveItemToPlayer(Player player, ItemStack stack, boolean upgrade, boolean reverse) {
		PlayerInventory inv = player.getInventory();
		Material mat = stack.getType();
		if(armor.containsKey(mat) && KitMaster.config().getBoolean("inventory.autoEquipArmor", true)){
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
		else if(weapons.containsKey(mat) && KitMaster.config().getBoolean("inventory.autoEquipWeapon", true)){
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
		for(String key : KitMaster.config().getConfigurationSection("maxStacks").getKeys(false)){
			try{
				if(getBaseStack(key).getType() == stack.getType())
					maxStackSize = KitMaster.config().getInt("maxStacks." + key);
			}
			catch(ParseItemException pie){}
		}
		if(stack.getAmount() <= maxStackSize)
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
			String[] alphaSplit = str.split(" ");
			String[] betaSplit = alphaSplit[0].split(":");

			ItemStack stack = getBaseStack(betaSplit[0]);

			if(betaSplit.length > 1){
				if(isInt(betaSplit[betaSplit.length - 1]))
					stack.setAmount(Integer.parseInt(betaSplit[betaSplit.length - 1]));
				else
					throw new ParseItemException("invalid amount specified", str);
			}

			if(betaSplit.length > 2)
				for(int i = 1; i < betaSplit.length - 1; i++){
					try{
						stack = applyTag(stack, betaSplit[i]);
					}
					catch(ParseItemException pie){ throw pie; }
				}

			for (int i = 1; i < alphaSplit.length;i++){
				EnchantmentWithLevel ewl = parseEnchantment(alphaSplit[i].trim());
				if(ewl == null)
					throw new ParseItemException("invalid enchantment specified", str);
				stack.addUnsafeEnchantment(ewl.enc, ewl.lvl);
			}

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
	public static ItemStack getBaseStack(String name) throws ParseItemException{
		if(ItemHandler.isCustomItem(name))
			return ItemHandler.getCustomItem(name);
		name = name.toLowerCase();
		Material type = null;
		if(type == null){
			try{ type = Material.getMaterial(Integer.decode(name)); }
			catch(NumberFormatException e){}
		}
		if(type == null)
			type = Material.matchMaterial(name);
		if((name.toLowerCase().contains("spawn") || name.toLowerCase().contains("mob")) && name.toLowerCase().contains("egg"))
			type = Material.MONSTER_EGG;
		if(name.toLowerCase().contains("skull"))
			type = Material.SKULL_ITEM;
		if(name.toLowerCase().contains("dye"))
			type = Material.INK_SACK;
		if(type == null && KitMaster.isVaultEnabled() && !name.toLowerCase().contains("legg")){
			ItemInfo item = Items.itemByName(name);
			if(item != null)
				type = item.getType();
		}
		if(type == null)
			throw new ParseItemException("unable to find matching material", name);
		return new ItemStack(type);
	}

	public static ItemStack applyTag(ItemStack stack, String tag) throws ParseItemException{
		if(!tag.isEmpty()){
			try{
				switch(stack.getType()){

				case MONSTER_EGG:
				case MOB_SPAWNER:
					EntityType entity = EntityType.fromName(tag);
					if(entity != null && entity.isSpawnable() && entity.isAlive())
						stack.setDurability(entity.getTypeId());
					break;
				case WRITTEN_BOOK:
						stack = BookHandler.loadBook(stack, tag);
					break;
				case LEATHER_HELMET:
				case LEATHER_CHESTPLATE:
				case LEATHER_LEGGINGS:
				case LEATHER_BOOTS:
					if(tag.startsWith("0x") || tag.startsWith("#")){
						LeatherArmorMeta leatherMeta = (LeatherArmorMeta) stack.getItemMeta();
						leatherMeta.setColor(Color.fromRGB(Integer.decode(tag.replace("#", "0x"))));
						stack.setItemMeta(leatherMeta);
					}
					break;
				case SKULL_ITEM:
					SkullMeta skullMeta = (SkullMeta) stack.getItemMeta();
					skullMeta.setOwner(tag);
					stack.setItemMeta(skullMeta);
					stack.setDurability((short) 3);
				case SKULL:
					if(tag.toLowerCase().toLowerCase().contains("skeleton"))
						stack.setDurability((short) 0);
					if(tag.toLowerCase().toLowerCase().contains("wither"))
						stack.setDurability((short) 1); 
					if(tag.toLowerCase().toLowerCase().contains("zombie"))
						stack.setDurability((short) 2);
					if(tag.toLowerCase().toLowerCase().contains("steve") || tag.toLowerCase().toLowerCase().contains("human"))
						stack.setDurability((short) 3);
					if(tag.toLowerCase().toLowerCase().contains("creeper"))
						stack.setDurability((short) 4);
					break;
				case FIREWORK_CHARGE:
					FireworkEffectMeta fireworkMeta = (FireworkEffectMeta) stack.getItemMeta();
					fireworkMeta.setEffect(FireworkEffectHandler.getFirework(tag));
					stack.setItemMeta(fireworkMeta);
					break;
				case FIREWORK:
					stack = FireworkHandler.loadFirework(stack, tag);
					break;
				case POTION:
					stack.setDurability(getPotionDataByCommonName(tag));
					stack = PotionHandler.loadPotion(stack, tag);
					break;
				case LOG:
				case LEAVES:
				case WOOD:
				case WOOD_STEP:
				case WOOD_DOUBLE_STEP:
				case WOOD_STAIRS:
					if(tag.equalsIgnoreCase("oak") || tag.equalsIgnoreCase("normal"))
						stack.setDurability((short) 0);
					if(tag.equalsIgnoreCase("spruce") || tag.equalsIgnoreCase("pine") || tag.equalsIgnoreCase("dark"))
						stack.setDurability((short) 1);
					if(tag.equalsIgnoreCase("birch") || tag.equalsIgnoreCase("light"))
						stack.setDurability((short) 2);
					if(tag.equalsIgnoreCase("jungle") || tag.equalsIgnoreCase("red"))
						stack.setDurability((short) 3);
					break;
				case WOOL:
				case INK_SACK:
					if(!isInt(tag) && DyeColor.valueOf(tag.toUpperCase()) != null)
						stack.setDurability(DyeColor.valueOf(tag.toUpperCase()).getData());
					break;
				case STEP:
					if(tag.equalsIgnoreCase("stone"))
						stack.setDurability((short) 0);
					if(tag.toLowerCase().contains("sandstone"))
						stack.setDurability((short) 1);
					if(tag.toLowerCase().contains("cobble"))
						stack.setDurability((short) 3);
					if(tag.equalsIgnoreCase("brick"))
						stack.setDurability((short) 4);
					if(tag.toLowerCase().contains("stone") && tag.toLowerCase().contains("brick"))
						stack.setDurability((short) 5);
					if(tag.toLowerCase().contains("seam"))
						stack.setDurability((short) 6);
					break;
				case SANDSTONE:
					if(tag.toLowerCase().contains("chiseled") || tag.toLowerCase().contains("creeper"))
						stack.setDurability((short) 1);
					if(tag.toLowerCase().contains("smooth") || tag.toLowerCase().contains("brick"))
						stack.setDurability((short) 2);
					break;
				case MONSTER_EGGS:
					if(tag.equalsIgnoreCase("stone") || tag.toLowerCase().contains("smooth"))
						stack.setDurability((short) 0);
					if(tag.toLowerCase().contains("brick"))
						stack.setDurability((short) 1);
					if(tag.toLowerCase().contains("cobble"))
						stack.setDurability((short) 2);
				case SMOOTH_BRICK:
					if(tag.toLowerCase().contains("mossy"))
						stack.setDurability((short) 1);
					if(tag.toLowerCase().contains("cracked") || tag.toLowerCase().contains("broken"))
						stack.setDurability((short) 2);
					if(tag.toLowerCase().contains("chiseled") || tag.toLowerCase().contains("circle"))
						stack.setDurability((short) 3);
				case COBBLESTONE:
					if(tag.toLowerCase().contains("mossy"))
						stack.setDurability((short) 1);
				case GOLDEN_APPLE:
					if(tag.toLowerCase().contains("enchanted") || tag.toLowerCase().contains("power") || tag.toLowerCase().contains("super"))
						stack.setDurability((short) 1);
				default: }

				if(isInt(tag))
					stack.setDurability(Short.parseShort(tag)); 
			}	
			catch(NumberFormatException nfe){ throw new ParseItemException("invalid item tag specified", tag); }
		}
		return stack;
	}

	public static String getTag(ItemStack stack){
		String name = null;
		switch(stack.getType()){
		case MONSTER_EGG:
		case MOB_SPAWNER:
			return EntityType.fromId(stack.getDurability()).name().toLowerCase();
		case WRITTEN_BOOK:
			name = BookHandler.getBookName(stack);
			if(name != null)
				return name;
			break;
		case LEATHER_HELMET:
		case LEATHER_CHESTPLATE:
		case LEATHER_LEGGINGS:
		case LEATHER_BOOTS:
			return "0x" + Integer.toHexString(((LeatherArmorMeta) stack.getItemMeta()).getColor().asRGB()).toUpperCase();
		case SKULL_ITEM:
			return ((SkullMeta) stack.getItemMeta()).getOwner();
		case FIREWORK_CHARGE:
			name = FireworkEffectHandler.getEffectName(((FireworkEffectMeta) stack.getItemMeta()).getEffect());
			if(name != null)
				return name;
			break;
		case FIREWORK:
			name = FireworkHandler.getFireworkName(stack);
			if(name != null)
				return name;
			break;
		case POTION:
			name = PotionHandler.getPotionName(stack);
			if(name != null)
				return name;
			break;
		case LOG:
		case LEAVES:
		case WOOD:
		case WOOD_STEP:
		case WOOD_DOUBLE_STEP:
		case WOOD_STAIRS:
			switch(stack.getDurability()){
			case 0: return "oak";
			case 1: return "spruce";
			case 2: return "birch";
			case 3: return "jungle";
			}
		case WOOL:
		case INK_SACK:
			return DyeColor.getByData((byte) stack.getDurability()).name().toLowerCase();
		case STEP:
			switch(stack.getDurability()){
			case 0: return "stone";
			case 1: return "sandstone";
			case 2: return null;
			case 3: return "cobblestone";
			case 4: return "brick";
			case 5: return "stone_brick";
			case 6: return "seamless";
			}
		case SANDSTONE:
			switch(stack.getDurability()){
			case 0: return null;
			case 1: return "chiseled";
			case 2: return "smooth";
			}
		case MONSTER_EGGS:
			switch(stack.getDurability()){
			case 0: return "stone";
			case 1: return "stone_brick";
			case 2: return "cobblestone";
			}
		case SMOOTH_BRICK:
			switch(stack.getDurability()){
			case 0: return null;
			case 1: return "mossy";
			case 2: return "cracked";
			case 3: return "chiseled";
			}
		case COBBLESTONE:
			switch(stack.getDurability()){
			case 0: return null;
			case 1: return "mossy";
			}
		case GOLDEN_APPLE:
			switch(stack.getDurability()){
			case 0: return null;
			case 1: return "enchanted";
			}
		default: }
		if(stack.getDurability() != 0)
			return String.valueOf(stack.getDurability());
		else
			return null;
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

	public static String itemToString(ItemStack stack){
		String str = stack.getType().name().toLowerCase();
		if(getTag(stack) != null)
			str += ":" + getTag(stack);
		str += ":" + stack.getAmount();
		for(Enchantment enc : stack.getEnchantments().keySet())
			str += " " + enc.getName().toLowerCase() + ":" + stack.getEnchantmentLevel(enc);
		return str;
	}

	/**
	 * Gets a user-friendly String that represents an <code>ItemStack</code>.
	 * @param stack The item to read.
	 * @return The string.
	 */
	public static String friendlyItemString(ItemStack stack){
		String str = capitalize((stack.getItemMeta().getDisplayName() == null) ? stack.getType().name().toLowerCase().replace("_", " ") : stack.getItemMeta().getDisplayName()) + (stack.getAmount() > 1 ? "s" : "");
		if(stack.getItemMeta().getDisplayName() == null){
			String tag = getTag(stack);
			if(tag != null)
				str = capitalize(tag) + " " + str;
		}
		str = stack.getAmount() + " " + str;
		if(!stack.getEnchantments().isEmpty())
			str += " with ";
		boolean first = true;
		for(Enchantment enc : stack.getEnchantments().keySet()){
			str += capitalize((first ? " " : ", ") + getCommonNameByEnchantment(enc)) + " " + romanNumerals(stack.getEnchantmentLevel(enc));
			first = false;
		}
		return str;
	}

	/**
	 * Gets a user-friendly String that represents an <code>PotionEffect</code>.
	 * @param stack The effect to read.
	 * @return The string.
	 */
	public static String friendlyEffectString(PotionEffect effect){
		return capitalize(getCommonNameByEffect(effect.getType())) + " " + romanNumerals(effect.getAmplifier() + 1) + " for " + effect.getDuration()/20 + " seconds";
	}
	
	public static String capitalize(String str){
		String[] words = str.split(" ");
		String result = "";
		for(String word : words){
			if(word.length() <= 1)
				result += word.toUpperCase() + " ";
			else
				result += word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase() + " ";
		}
		return result.trim();
	}

	private static final String[] romanNumerals = new String[]{"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
	private static final int[] ints = new int[]{1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
	public static String romanNumerals(int number){
		if(number <= 0 || number > 4000)
			return String.valueOf(number);
		String roman = "";
		for(int i = 0; i < romanNumerals.length; i++)
			while(number >= ints[i]){
				number -= ints[i];
				roman += romanNumerals[i];
			}
		return roman;
	}

	private static boolean isInt(String str){try{Integer.parseInt(str);return true;}catch(Exception e){return false;}}

}
