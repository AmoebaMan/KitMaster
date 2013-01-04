package com.amoebaman.kitmaster.enums;

public enum GiveKitResult {
	
	/**
	 * The kit was successfully given. 
	 */
	SUCCESS,
	
	/**
	 * The kit was not given because the player did not have permissions.
	 */
	FAIL_NO_PERMS,
	
	/**
	 * The kit was not given because the player had not waited for the timeout.
	 */
	FAIL_TIMEOUT,
	
	/**
	 * The kit was not given because the player had already taken it.
	 */
	FAIL_SINGLE_USE,
	
	/**
	 * The kit was not given because a 3rd party plugin cancelled it.
	 */
	FAIL_CANCELLED,
	
	/**
	 * The kit was not given because the player could not afford it.
	 */
	FAIL_COST,
	
	/**
	 * The kit was not given because another kit restricts further kits.
	 */
	FAIL_RESTRICTED,
	
	/**
	 * The kit was not given because it was null.
	 */
	FAIL_NULL_KIT,
	;
	
}
