package edu.vt.tlos.domain.binary;

import java.util.ArrayList;


public class PropertyBlock3 {

	public static final int PROP_BLOCK_3 = 102;
	
	private String key;
	private ArrayList<String> values = new ArrayList<String>();
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public ArrayList<String> getValues() {
		return values;
	}
	public void setValues(ArrayList<String> values) {
		this.values = values;
	}
	
}
