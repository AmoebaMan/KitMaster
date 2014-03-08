package net.amoebaman.kitmaster.sql;

public class SQLQueries {
	
	/** Macro intended to be replaced by a player's name */
	public static final String PLAYER_MACRO = "%player%";
	
	/** Macro intended to be replaced by a kit's name */
	public static final String KIT_MACRO = "%kit%";
	
	/** Macro intended to be replaced by a location string */
	public static final String LOCATION_MACRO = "%loc%";
	
	/** Macro intended to be replaced by any miscellaneous value */
	public static final String TIMESTAMP_MACRO = "%timestamp%";
	
	/** Macro intended to be replaced by the player's history list */
	public static final String HISTORY_MACRO = "%history%";
	
	/** SQL command to create KitMaster's database */
	public static final String CREATE_DATABASE =
			"CREATE DATABASE IF NOT EXISTS kitmaster";
	
	/** SQL command to use KitMaster's database */
	public static final String USE_DATABASE =
			"USE kitmaster";
	
	/** SQL command to create the timestamp table */
	public static final String CREATE_TIMESTAMP_TABLE =
			"CREATE TABLE IF NOT EXISTS timestamps(" +
			"player VARCHAR(16) NOT NULL, " +
			"PRIMARY KEY(player))";
	
	/** SQL command to add a column for a kit to the timestamp table */
	public static final String ADD_KIT_TO_TIMESTAMP_TABLE =
			"ALTER TABLE timestamps " +
			"ADD COLUMN `" + KIT_MACRO + "` BIGINT NOT NULL DEFAULT 0";
	
	/** SQL query to retrieve timestamp data for a player and kit */
	public static final String GET_TIMESTAMP =
			"SELECT `" + KIT_MACRO + "` FROM timestamps " +
			"WHERE player = \"" + PLAYER_MACRO + "\"";
	
	/** SQL command to update timestamp data for a player and kit */
	public static final String UPDATE_TIMESTAMP =
			"INSERT INTO timestamps(player, `" + KIT_MACRO + "`) " +
			"VALUES(\"" + PLAYER_MACRO + "\", " + TIMESTAMP_MACRO + ") " +
			"ON DUPLICATE KEY UPDATE `" + KIT_MACRO + "` = " + TIMESTAMP_MACRO;
	
	/** SQL command to create the sign record table */
	public static final String CREATE_SIGN_TABLE =
			"CREATE TABLE IF NOT EXISTS signs(" +
			"location VARCHAR(100) NOT NULL, " +
			"kit VARCHAR(50) NOT NULL, " +
			"PRIMARY KEY(location))";
			
	/** SQL query to retrieve the kit linked to the sign at a location */
	public static final String GET_SIGN_AT =
			"SELECT kit FROM signs " +
			"WHERE location = \"" + LOCATION_MACRO + "\"";
	
	/** SQL command to get all sign locations */
	public static final String GET_ALL_SIGNS =
			"SELECT location FROM signs";
	
	/** SQL command to link a kit to a sign at a location */
	public static final String SET_SIGN_AT = 
			"INSERT INTO signs(location, kit) " +
			"VALUES(\"" + LOCATION_MACRO + "\", \"" + KIT_MACRO + "\") " +
			"ON DUPLICATE KEY UPDATE kit = \"" + KIT_MACRO + "\"";
	
	/** SQL command to remove the entry for a sign at a location */
	public static final String REMOVE_SIGN_AT = 
			"DELETE FROM signs " +
			"WHERE location = \"" + LOCATION_MACRO + "\"";
	
	/** SQL command to create the history table */
	public static final String CREATE_HISTORY_TABLE =
			"CREATE TABLE IF NOT EXISTS history(" +
			"player VARCHAR(16) NOT NULL, " +
			"history TEXT NOT NULL, " +
			"PRIMARY KEY(player))";
	
	/** SQL query to retrieve the String containing the player's kit history */
	public static final String GET_HISTORY =
			"SELECT history FROM history " +
			"WHERE player = \"" + PLAYER_MACRO + "\"";
	
	/** SQL command to update the String containing the player's kit history */
	public static final String UPDATE_HISTORY =
			"INSERT INTO history(player, history) " +
			"VALUES(\"" + PLAYER_MACRO + "\", \"" + HISTORY_MACRO + "\") " +
			"ON DUPLICATE KEY UPDATE history = \"" + HISTORY_MACRO + "\"";
	
	/** SQL command to remove the entry for a player's history */
	public static final String REMOVE_HISTORY =
			"DELETE FROM history " +
			"WHERE player = \"" + PLAYER_MACRO + "\"";
	
}
