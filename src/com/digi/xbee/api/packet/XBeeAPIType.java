package com.digi.xbee.api.packet;

import java.util.HashMap;

import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This enumeration lists all the available frame types used in the ZigBee protocol
 * according to the XBee/XBee-PRO ZB RF Modules manual.
 */
public enum XBeeAPIType {

	// Enumeration elements
	AT_COMMAND (0x08, "AT Command"),
	RX_64 (0x80, "RX (Receive) Packet 64-bit Address"),
	RX_16 (0x81, "RX (Receive) Packet 16-bit Address"),
	AT_COMMAND_RESPONSE (0x88, "AT Command Response"),
	RECEIVE_PACKET (0x90, "Receive Packet"),
	GENERIC (0xFF, "Generic");
	
	// Variables
	private int idValue;
	
	private String name;
	
	private static HashMap<Integer, XBeeAPIType> lookupTable = new HashMap<Integer, XBeeAPIType>();
	
	static {
		for (XBeeAPIType type:values())
			lookupTable.put(type.getValue(), type);
	}
	
	/**
	 * Class constructor. Instances a new object of type XBee API Type with
	 * the given value and name.
	 * 
	 * @param idValue XBee API type value.
	 * @param name XBee API type name.
	 */
	XBeeAPIType(int idValue, String name) {
		this.idValue = idValue;
		this.name = name;
	}
	
	/**
	 * Retrieves the XBeeAPIType associated with the given ID value.
	 * 
	 * @param value ID value to retrieve XBeeAPIType;
	 * @return XBeeAPIType for the given ID value, null if it does not exist.
	 */
	public static XBeeAPIType get(int value) {
		XBeeAPIType type = lookupTable.get(value);
		return type; 
	}
	
	/**
	 * Retrieves the XBee API type value.
	 * 
	 * @return The XBee API type value.
	 */
	public int getValue() {
		return idValue;
	}
	
	/**
	 * Retrieves XBee API type name.
	 * 
	 * @return XBee API type name.
	 */
	public String getName() {
		return name;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString() {
		return "(" + HexUtils.byteArrayToHexString(ByteUtils.intToByteArray(idValue)) + ") " + name;
	}
}
