package com.digi.xbee.api.models;

/**
 * Enumerates the different working modes of the XBee device.
 */
public enum XBeeMode {

	// Enumeration types
	AT(0, "AT mode"),
	API(1, "API mode"),
	API_ESCAPE(2, "API mode with escaped characters"),
	UNKNOWN(3, "Unknown");
	
	// Variables
	private int id;
	
	private String name;
	
	/**
	 * Creates a new XBee mode with the given ID.
	 * 
	 * @param id XBee mode ID.
	 * @param name XBee mode name.
	 */
	XBeeMode(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	/**
	 * Retrieves the XBee mode ID.
	 * 
	 * @return XBee mode ID.
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Retrieves the XBee mode name.
	 * 
	 * @return XBee mode name.
	 */
	public String getName() {
		return name;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString() {
		return name;
	}
}
