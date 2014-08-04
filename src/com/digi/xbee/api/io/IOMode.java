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

import java.util.HashMap;

/**
 * Enumerates the different Input/Output modes that an IO line can be 
 * configured with.
 */
public enum IOMode {

	// Enumeration types.
	DISABLED(0, "Disabled"),
	SPECIAL_FUNCTIONALITY(1, "Firmware special functionality"),
	PWM(2, "PWM output"),
	ADC(2, "Analog to Digital Converter"),
	DIGITAL_IN(3, "Digital input"),
	DIGITAL_OUT_LOW(4, "Digital output, Low"),
	DIGITAL_OUT_HIGH(5, "Digital output, High");
	
	// Variables
	private final static HashMap <Integer, IOMode> lookupTable = new HashMap<Integer, IOMode>();
	
	private final int id;
	
	private final String name;
	
	static {
		for (IOMode ioMode:values())
			lookupTable.put(ioMode.getID(), ioMode);
	}
	
	/**
	 * Creates a new IO mode with the given ID.
	 * 
	 * @param id IO mode ID.
	 * @param name IO mode name.
	 */
	IOMode(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	/**
	 * Retrieves the IO mode ID.
	 * 
	 * @return IO mode ID.
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Retrieves the IO mode name.
	 * 
	 * @return IO mode name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Retrieves the IOMode corresponding to the provided mode ID.
	 * 
	 * @param modeID The ID of the IOMode to retrieve.
	 * @return The IOMode corresponding to the provided mode ID.
	 */
	public static IOMode getIOMode(int modeID) {
		return getIOMode(modeID, null);
	}
	
	/**
	 * Retrieves the IOMode corresponding to the provided mode ID and 
	 * IO line.
	 * 
	 * @param modeID The ID of the IOMode to retrieve.
	 * @param ioLine The IO line to retrieve its IOMode.
	 * @return The IOMode corresponding to the provided mode ID and 
	 *         IO line.
	 */
	public static IOMode getIOMode(int modeID, IOLine ioLine) {
		// If IO line is provided and IO value is 2, check PWM capability.
		if (modeID == ADC.getID()) {
			if (ioLine != null && ioLine.hasPWMCapability())
				return PWM;
			return ADC;
		}
		// Look for the value in the table.
		if (lookupTable.containsKey(modeID))
			return lookupTable.get(modeID);
		return null;
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
