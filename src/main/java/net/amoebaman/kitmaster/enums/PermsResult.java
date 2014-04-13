package net.amoebaman.kitmaster.enums;

public enum PermsResult {
	
	/**
	 * Selection of the kit is allowed
	 */
	ALL(GenericResult.YES, "available"),
	
	/**
	 * Only sign selection is allowed
	 */
	SIGN_ONLY(GenericResult.CONDITIONAL, "signs only"),
	
	/**
	 * Only command selection is allowed
	 */
	COMMAND_ONLY(GenericResult.CONDITIONAL, "commands only"),
	
	/**
	 * Selection of the kit is not allowed
	 */
	NONE(GenericResult.NO, "not available"),
	
	/**
	 * Selection of the kit is allowed, but only because of a parent's permissions
	 */
	INHERIT_ALL(GenericResult.YES, "available"),
	
	/**
	 * Only sign selection is allowed, but only because of a parent's permissions
	 */
	INHERIT_SIGN_ONLY(GenericResult.CONDITIONAL, "signs only"),
	
	/**
	 * Only command selection is allowed, but only because of a parent's permissions
	 */
	INHERIT_COMMAND_ONLY(GenericResult.CONDITIONAL, "commands only"),
	
	/**
	 * Selection of the kit is not allowed, but only because of a parent's permissions
	 */
	INHERIT_NONE(GenericResult.NO, "not available"),
	
	/**
	 * The kit does not exist, so permissions could not be evaluated
	 */
	NULL_KIT(GenericResult.EXCEPTION, "kit does not exist"),
	;
	
	/**
	 * The simplified result that corresponds to this
	 */
	public GenericResult generic;
	
	/**
	 * The message that corresponds to this
	 */
	public String message;
	private PermsResult(GenericResult generic, String message){
		this.generic = generic; 
		this.message = message;
	}
	
}
