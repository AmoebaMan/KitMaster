package com.amoebaman.kitmaster.utilities;

import java.util.logging.Logger;

public class ParseItemException extends Exception{

	private static final long serialVersionUID = 3575515956836332252L;
	private String line;
	
	public ParseItemException(String message, String line){
		super(message);
		this.line = line;
	}
	
	@Override
	public void printStackTrace(){
		Logger.getLogger("minecraft").severe("Unable to parse line: " + line);
		super.printStackTrace();
	}
	
}