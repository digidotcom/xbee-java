package com.digi.xbee.api.packet;

import java.util.HashMap;

import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This enumeration lists all the available frame types used in any XBee 
 * protocol.
 */
public enum APIFrameType {

	// Enumeration elements
	AT_COMMAND (0x08, "AT Command"),
	RX_64 (0x80, "RX (Receive) Packet 64-bit Address"),
	RX_16 (0x81, "RX (Receive) Packet 16-bit Address"),
	AT_COMMAND_RESPONSE (0x88, "AT Command Response"),
	RECEIVE_PACKET (0x90, "Receive Packet"),
	GENERIC (0xFF, "Generic");
	
	// Variables
	private final int idValue;
	
	private final String name;
	
	private static final HashMap<Integer, APIFrameType> lookupTable = new HashMap<Integer, APIFrameType>();
	
	static {
		for (APIFrameType type:values())
			lookupTable.put(type.getValue(), type);
	}
	
	/**
	 * Class constructor. Instances a new object of type XBee API Type with
	 * the given value and name.
	 * 
	 * @param idValue XBee frame type value.
	 * @param name XBee frame type name.
	 */
	APIFrameType(int idValue, String name) {
		this.idValue = idValue;
		this.name = name;
	}
	
	/**
	 * Retrieves the XBeeAPIType associated with the given ID value.
	 * 
	 * @param value ID value to retrieve APIFrameType;
	 * @return XBeeAPIType for the given ID value, null if it does not exist.
	 */
	public static APIFrameType get(int value) {
		APIFrameType type = lookupTable.get(value);
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
