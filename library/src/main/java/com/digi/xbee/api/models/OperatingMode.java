/**
 * Copyright (c) 2014-2015 Digi International Inc.,
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
 * Enumerates the different working modes of the XBee device. The operating 
 * mode establishes the way a user communicates with an XBee device through 
 * its serial interface.
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
	 * Class constructor. Instantiates a new {@code OperatingMode} enumeration 
	 * entry with the given parameters.
	 * 
	 * @param id Operating mode ID.
	 * @param name Operating mode name.
	 */
	private OperatingMode(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	/**
	 * Returns the operating mode ID.
	 * 
	 * @return Operating mode ID.
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Returns the operating mode name.
	 * 
	 * @return Operating mode name.
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
