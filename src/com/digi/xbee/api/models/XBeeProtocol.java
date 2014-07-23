/**
* Copyright (c) 2014 Digi International Inc.,
* All rights not expressly granted are reserved.
*
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/.
*
* Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
* =======================================================================
*/
package com.digi.xbee.api.models;

import java.util.HashMap;

/**
 * Enumeration class listing the available XBee protocols.
 */
public enum XBeeProtocol {
	
	// Enumeration entries
	ZIGBEE(0, "ZigBee"),
	RAW_802_15_4(1, "802.15.4"),
	XBEE_WIFI(2, "Wi-Fi"),
	DIGI_MESH(3, "DigiMesh"),
	XCITE(4, "XCite"),
	XTEND(5, "XTend (Legacy)"),
	XTEND_DM(6, "XTend (DigiMesh)"),
	SMART_ENERGY(7, "Smart Energy"),
	DIGI_POINT(8, "Point-to-multipoint"),
	ZNET(9, "ZNet 2.5"),
	XC(10, "XSC"),
	UNKNOWN(99, "Unknown");
	
	// Variables
	private static final HashMap<Integer, XBeeProtocol> lookupTable = new HashMap<Integer, XBeeProtocol>();
	
	private final int id;
	
	private final String description;
	
	static {
		for (XBeeProtocol xbeeProtocol:values())
			lookupTable.put(xbeeProtocol.getID(), xbeeProtocol);
	}
	
	/**
	 * Class constructor. Instances a new object of type XBeeProtocol
	 * for the enumeration.
	 * 
	 * @param id Protocol ID.
	 * @param description Protocol description.
	 */
	XBeeProtocol(int id, String description) {
		this.id = id;
		this.description = description;
	}
	
	/**
	 * Retrieves the XBee protocol ID.
	 * 
	 * @return XBee protocol ID.
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Retrieves the XBee protocol description.
	 * 
	 * @return XBee protocol description.
	 */
	public String getDescription() {
		return description;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString() {
		return description;
	}
	
	/**
	 * Retrieves the XBeeProtocol corresponding to the given ID.
	 * 
	 * @param id The ID of the protocol to retrieve.
	 * @return The XBeeProtocol corresponding to the ID.
	 */
	public static XBeeProtocol get(int id) {
		return lookupTable.get(id);
	}
}
