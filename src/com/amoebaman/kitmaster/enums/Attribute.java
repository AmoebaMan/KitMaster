package com.amoebaman.kitmaster.enums;

public enum Attribute{

	/**
	 * Attribute that defines the timeout of this kit in seconds
	 */
	TIMEOUT(Integer.class, "timeout.duration", 0),
	
	/**
	 * Attribute that determines whether the kit will apply the same timeout to all players on the server.
	 */
	GLOBAL_TIMEOUT(Boolean.class, "timeout.global", false),
	
	/**
	 * Attribute that determines whether the kit will infinitely renew its potion effects (until cleared).
	 */
	INFINITE_EFFECTS(Boolean.class, "infiniteEffects", true),
	
	/**
	 * Attribute that amounts to a blanket application of all other "CLEAR" attributes
	 */
	CLEAR_ALL(Boolean.class, "clear.all", true),
	
	/**
	 * Attribute that determines whether the kit will clear the player's inventory before it is given.
	 */
	CLEAR_INVENTORY(Boolean.class, "clear.inventory", false),
	
	/**
	 * Attribute that determines whether the kit will clear all potion effects before it is given.
	 */
	CLEAR_EFFECTS(Boolean.class, "clear.effects", false),
	
	/**
	 * Attribute that determines whether the kit will remove all previously kit-applied permissions before it is given.
	 */
	CLEAR_PERMISSIONS(Boolean.class, "clear.permissions", false),
	
	/**
	 * Attribute that links the kit to its intended parent by the parent's name.
	 */
	PARENT(String.class, "inheritance.parent", ""),
	
	/**
	 * Attribute that determines whether the kit will attempt to blend its items and effects into those of its parents.
	 */
	UPGRADE(Boolean.class, "inheritance.upgrade", false),
	
	/**
	 * Attribute that determines whether the kit will require its parent's permissions as a supplement to its own.
	 */
	REQUIRE_PARENT_PERMS(Boolean.class, "inheritance.requireParentPerms", false),
	
	/**
	 * Attribute that determines whether the kit will consider its parent's permissions to apply to itself.
	 */
	INHERIT_PARENT_PERMS(Boolean.class, "inheritance.inheritParentPerms", true),
	
	/**
	 * Attribute that determines the effective cost of the kit.  Only used if the economy handle is grabbed.
	 */
	COST(Double.class, "economy.cost", 0.0),
	
	/**
	 * Attribute that determines the amount of currency which will be given as part of the kit.  Only used if the economy handle is grabbed.
	 */
	CASH(Double.class, "economy.cash", 0.0),
	
	/**
	 * Attribute that determines whether the kit will show in the /kitlist list.
	 */
	SHOW_IN_LIST(Boolean.class, "showInList", true),
	
	/**
	 * Attribute that determines whether the kit should suppress its notification when loaded.
	 */
	SUPPRESS_LOAD_NOTIFICATION(Boolean.class, "suppressLoadNotification", false),
	
	/**
	 * An identifier string used exclusively by third-party plugins to identify kits by means other than their names.  Never used in SuperKits.
	 */
	IDENTIFIER(String.class, "identifier", ""),
	;

	/**
	 * The class type that this attribute is defined by
	 */
	public final Class<?> clazz;
	
	/**
	 * The YAML path that is used to define this attribute when loading kits
	 */
	public final String path;
	
	/**
	 * The default value of this attribute if it is not explicitly defined in the kit's YAML file
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
