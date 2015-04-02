package models;

import java.util.ArrayList;

public abstract class Bean {
	protected String identifier;
	protected String relationship;
	
	public abstract String get(String field);
	
	public abstract void set(String field, String data);
	
	public abstract ArrayList<String> fieldsList(); 
}

