package edu.vt.tlos.domain.binary;

import java.util.ArrayList;

public class Block4 {

	public static final int BLOCK_4 = 13;
	
	private int propertiesType;
	
	private PropertyBlock1 propertyBlock1;
	
	private ArrayList<Object> propertyBlocks = new ArrayList<Object>();
	
	public ArrayList<Object> getPropertyBlocks() {
		return propertyBlocks;
	}
	public void setPropertyBlocks(ArrayList<Object> propertyBlocks) {
		this.propertyBlocks = propertyBlocks;
	}
	public int getPropertiesType() {
		return propertiesType;
	}
	public void setPropertiesType(int propertiesType) {
		this.propertiesType = propertiesType;
	}
	
	public PropertyBlock1 getPropertyBlock1() {
		return propertyBlock1;
	}
	public void setPropertyBlock1(PropertyBlock1 propertyBlock1) {
		this.propertyBlock1 = propertyBlock1;
	}
	
}
