package net.amoebaman.kitmaster.enums;

import java.util.List;

public enum AttributeType {

	STRING(String.class),
	STRING_LIST(List.class),
	INTEGER(Integer.class),
	DOUBLE(Double.class),
	BOOLEAN(Boolean.class),
	;
	
	public final Class<?> clazz;
	private AttributeType(Class<?> clazz){
		this.clazz = clazz;
	}
	
	public boolean matches(Object value){
		try{
			clazz.cast(value);
			return true;
		}
		catch(ClassCastException cce){
			return false;
		}
	}
	
}
