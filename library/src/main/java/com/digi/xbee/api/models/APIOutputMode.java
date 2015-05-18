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

import java.util.HashMap;

import com.digi.xbee.api.utils.HexUtils;

/**
 * Enumerates the different API output modes. The API output mode establishes 
 * the way data will be output through the serial interface of an XBee device.
 */
public enum APIOutputMode {

	// Enumeration entries
	MODE_NATIVE(0x00, "Native"),
	MODE_EXPLICIT(0x01, "Explicit"),
	MODE_EXPLICIT_ZDO_PASSTHRU(0x03, "Explicit with ZDO Passthru");
	
	// Variables
	private final int value;
	
	private final String description;
	
	private final static HashMap<Integer, APIOutputMode> lookupTable = new HashMap<Integer, APIOutputMode>();
	
	static {
		for (APIOutputMode apiOutputMode:values())
			lookupTable.put(apiOutputMode.getValue(), apiOutputMode);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code APIOutputMode} enumeration
	 * entry with the given parameters.
	 * 
	 * @param value API output mode value 
	 * @param description API output mode description.
	 */
	private APIOutputMode(int value, String description) {
		this.value = value;
		this.description = description;
	}
	
	/**
	 * Returns the API output mode value.
	 * 
	 * @return The API output mode value.
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Returns the API output mode description.
	 * 
	 * @return The API output mode description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Returns the {@code APIOutputMode} entry associated to the given value.
	 * 
	 * @param value Value of the {@code APIOutputMode} to retrieve.
	 * 
	 * @return The {@code APIOutputMode} entry associated to the given value, 
	 *         {@code null} if the value could not be found in the 
	 *         list.
	 */
	public static APIOutputMode get(int value) {
		APIOutputMode apiOutputMode = lookupTable.get(value);
		if (apiOutputMode != null)
			return apiOutputMode;
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return HexUtils.byteToHexString((byte)value) + ": " + description;
	}
}
