package com.amoebaman.kitmaster.enums;

public enum Attribute{

	/**
	 * The timeout duration of this kit in seconds.
	 */
	TIMEOUT(Integer.class, "timeout.duration", 0),
	
	/**
	 * Whether or not the kit will apply the same timeout to all players.
	 */
	GLOBAL_TIMEOUT(Boolean.class, "timeout.global", false),
	
	/**
	 * Whether or not the kit can only be taken once.
	 */
	SINGLE_USE(Boolean.class, "timeout.singleUse", false),
	
	/**
	 * Whether or not the kit can only be taken once per life.
	 */
	SINGLE_USE_LIFE(Boolean.class, "timeout.singleUsePerLife", false),
	
	/**
	 * Whether or not the kit will infinitely renew its potion effects (until cleared).
	 */
	INFINITE_EFFECTS(Boolean.class, "infiniteEffects", true),
	
	/**
	 * Encompasses all other clear options.
	 */
	CLEAR_ALL(Boolean.class, "clear.all", true),
	
	/**
	 * Whether or not the kit should clear the player's inventory when taken.
	 */
	CLEAR_INVENTORY(Boolean.class, "clear.inventory", false),
	
	/**
	 * Whether or not the kit should clear the player's potion effects when taken.
	 */
	CLEAR_EFFECTS(Boolean.class, "clear.effects", false),
	
	/**
	 * Whether or not the kit should clear the player's kit-given permissions when taken.
	 */
	CLEAR_PERMISSIONS(Boolean.class, "clear.permissions", false),
	
	/**
	 * The name of the kit's intended parent.
	 */
	PARENT(String.class, "inheritance.parent", ""),
	
	/**
	 * Whether or not the kit will attempt to blend its items and effects into those already present.
	 */
	UPGRADE(Boolean.class, "inheritance.upgrade", false),
	
	/**
	 * Whether or not the kit will require its parent's permissions as a supplement to its own.
	 */
	REQUIRE_PARENT_PERMS(Boolean.class, "inheritance.requireParentPerms", false),
	
	/**
	 * Whether or not the kit will consider its parent's permissions to apply to itself.
	 */
	INHERIT_PARENT_PERMS(Boolean.class, "inheritance.inheritParentPerms", true),
	
	/**
	 * The cost of the kit.  Only used if the economy handle is grabbed.
	 */
	COST(Double.class, "economy.cost", 0.0),
	
	/**
	 * The amount of currency which will be given as part of the kit.  Only used if the economy handle is grabbed.
	 */
	CASH(Double.class, "economy.cash", 0.0),
	
	/**
	 * Whether or not the kit will show in the /kitlist list.
	 */
	SHOW_IN_LIST(Boolean.class, "showInList", true),
	
	/**
	 * Whether or not the kit should suppress its notification when loaded.  Due to the way kits are loaded, this cannot be inherited.
	 */
	SUPPRESS_LOAD_NOTIFICATION(Boolean.class, "suppressLoadNotification", false),
	
	/**
	 * An identifier string used exclusively by third-party plugins to identify kits by means other than their names.  Never used in SuperKits.
	 */
	IDENTIFIER(String.class, "identifier", ""),
	;

	/**
	 * The class type that this attribute is defined by.
	 */
	public final Class<?> clazz;
	
	/**
	 * The YAML path that is used to define this attribute when loading kits.
	 */
	public final String path;
	
	/**
	 * The default value of this attribute if it is not explicitly defined in the kit's YAML file.
	 */
	public final Object def;
	
	private Attribute(Class<?> clazz, String path, Object def){
		this.clazz = clazz;
		this.path = path;
		this.def = def;
	}

	/**
	 * Gets a more user-friendly version of this Attribute's enumerated name.
	 */
	public String toString(){
		return name().toLowerCase().replace('_', ' ');
	}
	
	/**
	 * Attempts to grab the <code>Attribute</code> that matches the given String.
	 * @param str The name of the desired attribute.
	 * @return The matched <code>Attribute</code>, or <code>null</code> if no match is found.
	 */
	public static Attribute matchName(String str){
		for(Attribute type : values())
			if(type.name().replace('_', ' ').equalsIgnoreCase(str))
				return type;
		return null;
	}
}
