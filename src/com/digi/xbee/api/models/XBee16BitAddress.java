package com.digi.xbee.api.models;

import java.util.Arrays;

import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a 16 bit address of the XBee ZigBee protocol. In this
 * protocol, each device is assigned a 16 bit address when it joins a network.
 */
public class XBee16BitAddress {

	// Constants
	public static final String BROADCAST_ADDRESS = "0xFFFE";
	public static final String UNKNOWN_ADDRESS = "0xFFFE";
	
	// Variables
	private int hsb;
	private int lsb;
	
	/**
	 * Class constructor. Instances a new object of type XBee16BitAddress
	 * with the given parameters.
	 * 
	 * @param hsb High significant byte of the address.
	 * @param lsb Low significant byte of the address.
	 */
	public XBee16BitAddress(int hsb, int lsb) {
		this.hsb = hsb;
		this.lsb = lsb;
	}
	
	/**
	 * Class constructor. Instances a new object of type XBee16BitAddress
	 * with the given parameters.
	 * 
	 * @param address Address as byte array.
	 */
	public XBee16BitAddress(byte[] address) {
		this.hsb = address[0];
		this.lsb = address[1];
	}
	
	/**
	 * Class constructor. Instances a new object of type XBee16BitAddress
	 * with the given parameters.
	 * 
	 * @param address Address as integer array.
	 */
	public XBee16BitAddress(int[] address) {
		this.hsb = address[0];
		this.lsb = address[1];
	}
	
	/**
	 * Class constructor. Instances a new object of type XBee16BitAddress
	 * with the given parameters.
	 * 
	 * @param address String containing the address.
	 */
	public XBee16BitAddress(String address) {
		byte[] byteAddress = HexUtils.hexStringToByteArray(address);
		hsb = byteAddress[0];
		lsb = byteAddress[1];
	}
	
	/**
	 * Instances a new broadcast XBee16BitAddress.
	 * 
	 * @return Broadcast XBee 16 bit address.
	 */
	public static XBee16BitAddress BroadcastAddress() {
		return new XBee16BitAddress(BROADCAST_ADDRESS);
	}
	
	/**
	 * Retrieves the address high significant byte.
	 * 
	 * @return Address high significant byte.
	 */
	public int getHsb() {
		return hsb;
	}
	
	/**
	 * Sets the address high significant byte.
	 * 
	 * @param hsb Address high significant byte.
	 */
	public void setHsb(int hsb) {
		this.hsb = hsb;
	}
	
	/**
	 * Retrieves the address low significant byte.
	 * 
	 * @return Address low significant byte.
	 */
	public int getLsb() {
		return lsb;
	}
	
	/**
	 * Sets the address low significant byte.
	 * 
	 * @param lsb Address low significant byte.
	 */
	public void setLsb(int lsb) {
		this.lsb = lsb;
	}
	
	/**
	 * Retrieves the address value.
	 * 
	 * @return Address value as byte array.
	 */
	public byte[] getValue() {
		return new byte[] {(byte) hsb, (byte) lsb};
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof XBee16BitAddress))
			return false;
		XBee16BitAddress addr = (XBee16BitAddress)obj;
		return Arrays.equals(addr.getValue(), getValue());
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return HexUtils.byteArrayToHexString(new byte[] {(byte)hsb, (byte)lsb});
	}
}
