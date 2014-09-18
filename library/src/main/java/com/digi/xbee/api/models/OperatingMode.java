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

/**
 * Enumerates the different working modes of the XBee device.
 */
public enum OperatingMode {

	// Enumeration types
	AT(0, "AT mode"),
	API(1, "API mode"),
	API_ESCAPE(2, "API mode with escaped characters"),
	UNKNOWN(3, "Unknown");
	
	// Variables
	private final int id;
	
	private final String name;
	
	/**
	 * Creates a new XBee mode with the given ID.
	 * 
	 * @param id XBee mode ID.
	 * @param name XBee mode name.
	 */
	OperatingMode(int id, String name) {
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
	@Override
	public String toString() {
		return name;
	}
}
