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
package com.digi.xbee.api.io;

public enum IOValue {

	// Enumeration types.
	LOW(4, "Low"),
	HIGH(5, "High");
	
	// Variables.
	private final int id;
	
	private final String name;
	
	/**
	 * Creates a new IO value with the given ID and name.
	 * 
	 * @param id IO value ID.
	 * @param name IO value name.
	 */
	IOValue(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	/**
	 * Retrieves the IO value ID.
	 * 
	 * @return IO value ID.
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Retrieves the IO value name.
	 * 
	 * @return IO value name.
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
