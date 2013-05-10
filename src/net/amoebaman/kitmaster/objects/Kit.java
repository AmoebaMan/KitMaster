package net.amoebaman.kitmaster.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.amoebaman.kitmaster.controllers.ItemController;
import net.amoebaman.kitmaster.enums.Attribute;
import net.amoebaman.kitmaster.handlers.KitHandler;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;


public class Kit implements Cloneable, ConfigurationSerializable{
	
	/**
	 * The name of the kit
	 */
	public final String name;
	
	/**
	 * The set of items that the kit will give
	 */
	public final ArrayList<ItemStack> items = new ArrayList<ItemStack>();
	
	/**
	 * The set of potion effects that the kit will apply
	 */
	public final ArrayList<PotionEffect> effects = new ArrayList<PotionEffect>();
	
	/**
	 * The set of (temporary) permissions that the kit will grant
	 */
	public final ArrayList<String> permissions = new ArrayList<String>();
	
	/**
	 * The set of attributes that have been defined for the kit
	 */
	public final HashMap<Attribute, Object> attributes = new HashMap<Attribute, Object>();
	
	/**
	 * Constructs a kit from the given specifications.  The lists/maps will be copied onto the kit's lists/maps rather than adopted.
	 * @param name The name of the kit
	 * @param items The items of the kit
	 * @param effects The applied potion effects of the kit
	 * @param permissions The granted permissions of the kit
	 * @param attributes The defined attributes of the kit
	 */
	public Kit(String name, List<ItemStack> items, List<PotionEffect> effects, List<String> permissions, HashMap<Attribute, Object> attributes){
		this.name = name;
		this.items.addAll(items);
		this.effects.addAll(effects);
		this.permissions.addAll(permissions);
		this.attributes.putAll(attributes);
	}
	
	/**
	 * Kits are reperesented in String form just by their name
	 */
	public String toString(){
		return name;
	}
	
	/**
	 * Two kits are considered equal so long as their names match, in order to prevent two kits from possessing the same name
	 */
	public boolean equals(Object other){
		if(other instanceof Kit)
			return other.toString().equals(toString());
		return false;
	}
	
	/**
	 * Similar to the implementation of equals(Object), kits are hashed by their names
	 */
	public int hashCode(){
		return toString().hashCode();
	}
	
	/**
	 * Tests whether the kit's items contain an item
	 * @param stack The ItemStack to test for
	 * @return True if the kit contains the given ItemStack
	 */
	public boolean containsItem(ItemStack stack){
		if(stack == null)
			return false;
		for(ItemStack item : items)
			if(item.getType() == stack.getType())
				return true;
		return false;
	}

	/**
	 * Tests whether the kit's items contain an effect
	 * @param potion The PotionEffect to test for
	 * @return True if the kit contains the given PotionEffect
	 */
	public boolean containsEffect(PotionEffect potion){
		if(potion == null)
			return false;
		for(PotionEffect effect : effects)
			if(effect.getType() == potion.getType())
				return true;
		return false;
	}
	
	/**
	 * Retrives the value of an attribute as it has been defined for this kit
	 * @param type The Attribute to retrieve
	 * @return The value of the attribute, or the attribute's default value if it was not explicitly defined
	 */
	public Object getAttribute(Attribute type){
		if(attributes.containsKey(type))
			return attributes.get(type);
		return type.def;
	}
	
	/**
	 * Retrives the value of an attribute as a boolean.  See getAttribute(Attribute)
	 */
	public boolean booleanAttribute(Attribute type){ return (Boolean) getAttribute(type); }

	/**
	 * Retrives the value of an attribute as an int.  See getAttribute(Attribute)
	 */
	public int integerAttribute(Attribute type){ return (Integer) getAttribute(type); }

	/**
	 * Retrives the value of an attribute as a double.  See getAttribute(Attribute)
	 */
	public double doubleAttribute(Attribute type){ return (Double) getAttribute(type); }
	
	/**
	 * Retrives the value of an attribute as a String.  See getAttribute(Attribute)
	 */
	public String stringAttribute(Attribute type){ return (String) getAttribute(type); }

	/**
	 * Defines/redefines the value of an attribute for this kit.  If newValue does not match type's enumerated class type, the operation will fail.
	 * @param type The attribute type to set
	 * @param newValue The new value for the attribute
	 * @return False if <code>newValue</code> did not match the designated class of <code>type</code>, true otherwise
	 */
	public boolean setAttribute(Attribute type, Object newValue){
		if(type.type.matches(newValue)){
			attributes.put(type, newValue);
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Gets the kit that this kit regards as its parent
	 * @return The parent kit, or null if no parent kit is found
	 */
	public Kit getParent(){
		return KitHandler.getKit((String) attributes.get(Attribute.PARENT));
	}
	
	/**
	 * Gets a new copy of this kit that replaces all undefined attributes of this kit with those defined by its parent.
	 * This call will cascade recursively upwards, applying parents of parents as well.
	 * @return A copy of this kit with its parent's (and grandparents') attributes applied
	 */
	public Kit applyParentAttributes(){
		Kit clone = clone();
		Kit parent = clone.getParent();
		if(parent != null)
			for(Attribute type : parent.attributes.keySet())
				if(!clone.attributes.containsKey(type) && type != Attribute.IDENTIFIER)
					clone.attributes.put(type, parent.getAttribute(type));
		return clone;
	}
	
	/**
	 * Clones the kit for safe modification
	 * @return A perfect copy of this kit
	 */
	public Kit clone(){
		return new Kit(name, items, effects, permissions, attributes);
	}
	
	/**
	 * Serializes this kit into a Map for easy Configuration storage
	 * @returns A map of key to value that can be placed into a configuration and parsed back with the KitHandler to get the same kit
	 */
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		List<String> itemStrings = new ArrayList<String>();
		for(ItemStack stack : items)
			itemStrings.add(ItemController.itemToString(stack));
		while(itemStrings.contains(null))
			itemStrings.remove(null);
		map.put("items", itemStrings);
		
		List<String> effectStrings = new ArrayList<String>();
		for(PotionEffect effect : effects)
			effectStrings.add(ItemController.effectToString(effect));
		map.put("effects", effectStrings);
		
		map.put("permissions", permissions);
		
		for(Attribute type : attributes.keySet())
			map.put(type.path, attributes.get(type));
		
		return map;
	}
}
