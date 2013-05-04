package net.amoebaman.kitmaster.utilities;

import java.util.logging.Logger;

public class LoadKitException extends Exception{

	private static final long serialVersionUID = -7976593988158108656L;
	private String kitName;
	
	public LoadKitException(String message, String kitName){
		super(message);
		this.kitName = kitName;
	}
	
	@Override
	public void printStackTrace(){
		Logger.getLogger("minecraft").severe("Unable to load kit: " + kitName);
		super.printStackTrace();
	}
	
}