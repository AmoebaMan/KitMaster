package net.amoebaman.kitmaster.enums;

public enum Attribute{

	/**
	 * The timeout duration of this kit in seconds
	 */
	TIMEOUT(AttributeType.INTEGER, "timeout.duration", 0),
	
	/**
	 * Whether or not the kit will apply the same timeout to all players
	 */
	GLOBAL_TIMEOUT(AttributeType.BOOLEAN, "timeout.global", false),
	
	/**
	 * Whether or not the kit can only be taken once
	 */
	SINGLE_USE(AttributeType.BOOLEAN, "timeout.singleUse", false),
	
	/**
	 * Whether or not the kit can only be taken once per life
	 */
	SINGLE_USE_LIFE(AttributeType.BOOLEAN, "timeout.singleUsePerLife", false),
	
	/**
	 * Whether or not the kit will infinitely renew its potion effects (until cleared)
	 */
	INFINITE_EFFECTS(AttributeType.BOOLEAN, "infiniteEffects", false),
	
	/**
	 * Encompasses all other clear options
	 */
	CLEAR_ALL(AttributeType.BOOLEAN, "clear.all", false),
	
	/**
	 * Whether or not the kit should clear the player's inventory when taken
	 */
	CLEAR_INVENTORY(AttributeType.BOOLEAN, "clear.inventory", false),
	
	/**
	 * Whether or not the kit should clear the player's potion effects when taken
	 */
	CLEAR_EFFECTS(AttributeType.BOOLEAN, "clear.effects", false),
	
	/**
	 * Whether or not the kit should clear the player's kit-given permissions when taken
	 */
	CLEAR_PERMISSIONS(AttributeType.BOOLEAN, "clear.permissions", false),
	
	/**
	 * The name of the kit's intended parent
	 */
	PARENT(AttributeType.STRING, "inheritance.parent", ""),
	
	/**
	 * Whether or not the kit will attempt to blend its items and effects into those already present
	 */
	UPGRADE(AttributeType.BOOLEAN, "inheritance.upgrade", false),
	
	/**
	 * Whether or not the kit will require its parent's permissions as a supplement to its own
	 */
	REQUIRE_PARENT_PERMS(AttributeType.BOOLEAN, "inheritance.requireParentPerms", false),
	
	/**
	 * Whether or not the kit will consider its parent's permissions to apply to itself
	 */
	INHERIT_PARENT_PERMS(AttributeType.BOOLEAN, "inheritance.inheritParentPerms", true),
	
	/**
	 * The cost of the kit, only used if the economy handle is grabbed
	 */
	COST(AttributeType.DOUBLE, "economy.cost", 0.0),
	
	/**
	 * The amount of currency which will be given as part of the kit, only used if the economy handle is grabbed.
	 */
	CASH(AttributeType.DOUBLE, "economy.cash", 0.0),
	
	/**
	 * Whether or not the kit should restrict armor removal
	 */
	RESTRICT_ARMOR(AttributeType.BOOLEAN, "restrictions.armor", false),
	
	/**
	 * Whether or not the kit should restrict item dropping
	 */
	RESTRICT_DROPS(AttributeType.BOOLEAN, "restrictions.drops", false),
	
	/**
	 * Whether or not the kit should restrict item drops on death
	 */
	RESTRICT_DEATH_DROPS(AttributeType.BOOLEAN, "restrictions.deathDrops", false),
	
	/**
	 * Whether or not the kit should restrict item pickups
	 */
	RESTRICT_PICKUPS(AttributeType.BOOLEAN, "restrictions.pickups", false),
	
	/**
	 * Whether or not the kit should restrict players taking additional kits after this one
	 */
	RESTRICT_KITS(AttributeType.BOOLEAN, "restrictions.otherKits", false),
	
	/**
	 * Whether or not the kit will show in the /kitlist list
	 */
	SHOW_IN_LIST(AttributeType.BOOLEAN, "showInList", true),
	
	/**
	 * An identifier string used exclusively by third-party plugins to identify kits by means other than their names
	 */
	IDENTIFIER(AttributeType.STRING, "identifier", ""),
	
	/**
	 * The Heroes class that this kit will assign when taken
	 */
	HEROES_CLASS(AttributeType.STRING, "heroes.class", ""),
	
	/**
	 * The Heroes skills that this kit will make available when taken
	 */
	HEROES_SKILLS(AttributeType.STRING_ARRAY, "heroes.skills", new String[0]),
	
	/**
	 * The Heroes binds that this kit will register when taken
	 */
	HEROES_BINDS(AttributeType.STRING_ARRAY, "heroes.binds", new String[0]),
	
	;

	/**
	 * The class type that this attribute is defined by
	 */
	public final AttributeType type;
	
	/**
	 * The YAML path that is used to define this attribute when loading kits
	 */
	public final String path;
	
	/**
	 * The default value of this attribute if it is not explicitly defined in the kit's YAML file
	 */
	public final Object def;
	
	private Attribute(AttributeType clazz, String path, Object def){
		this.type = clazz;
		this.path = path;
		this.def = def;
	}

	/**
	 * Gets a more user-friendly version of this Attribute's enumerated name
	 */
	public String toString(){
		return name().toLowerCase().replace('_', ' ');
	}
	
	/**
	 * Attempts to grab the Attribute that matches the given String
	 * @param str The name of the desired attribute
	 * @return The matched Attribute, or null if no match is found
	 */
	public static Attribute matchName(String str){
		for(Attribute type : values())
			if(type.name().replace('_', ' ').equalsIgnoreCase(str))
				return type;
		return null;
	}
}
