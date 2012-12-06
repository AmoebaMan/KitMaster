package com.amoebaman.kitmaster.enums;

public enum GiveKitContext{
	
	/**
	 * The kit was given by an admin using the /givekit command.
	 */
	COMMAND_GIVEN				(true),
	
	/**
	 * The kit was given because it was the parent kit of another kit.
	 */
	PARENT_GIVEN				(true),
	
	/**
	 * The kit was given by a 3rd party plugin.
	 */
	PLUGIN_GIVEN				(false),
	
	/**
	 * The kit was given by a 3rd party plugin, and will override any permission or timeout checks.  Only use if you explicitly need to override these checks.
	 */
	PLUGIN_GIVEN_OVERRIDE		(true),
	
	/**
	 * The kit was taken by the player using the /kit command.
	 */
	COMMAND_TAKEN				(false),
	
	/**
	 * The kit was taken by the player by punching a kit selection sign.
	 */
	SIGN_TAKEN					(false),
	;
	
	/**
	 * Whether or not the context will automatically override permissions and timeout checks.
	 */
	public final boolean overrides;
	private GiveKitContext(boolean overrides){ this.overrides = overrides; }
}