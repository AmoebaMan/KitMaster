package net.amoebaman.kitmaster.enums;

public enum GenericResult {
	
	/**
	 * Yes
	 */
	YES(true),
	
	/**
	 * No
	 */
	NO(false),
	
	/**
	 * Depends on circumstances
	 */
	CONDITIONAL(true),
	
	/**
	 * Something unexpected occurred
	 */
	EXCEPTION(false);
	
	/**
	 * The general evaluation of this result
	 */
	public boolean bool;
	private GenericResult(boolean bool){
		this.bool = bool;
	}
}
