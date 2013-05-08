package net.amoebaman.kitmaster.enums;

public enum ClearKitsContext{
	
	/**
	 * The player took a kit that is configured to clear kits.
	 */
	KIT_ATTRIBUTE,
	
	/**
	 * The player died, and the plugin is configured to clear kits on death.
	 */
	PLAYER_DEATH,
	
	/**
	 * The player disconnected, and the plugin is configured to clear kits on disconnect.
	 */
	PLAYER_DISCONNECT,
	
	/**
	 * The plugin was disabled (usually due to server reload/shutdown), and is configured to clear kits on disable.
	 */
	PLUGIN_DISABLE,
	
	/**
	 * A third-party plugin used the API to clear the player's kits.
	 */
	PLUGIN_ORDER
	;
	
}