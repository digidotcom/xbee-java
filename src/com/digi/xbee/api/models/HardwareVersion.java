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

public class HardwareVersion {
	
	// Variables.
	private final int value;
	
	private final String description;
	
	/**
	 * Class constructor. Instances a new HardwareVersion object with the given 
	 * parameters.
	 * 
	 * @param value The hardware version value.
	 * @param description The hardware version description.
	 */
	private HardwareVersion(int value, String description) {
		this.value = value;
		this.description = description;
	}
	
	/**
	 * Retrieves the Hardware version value.
	 * 
	 * @return Hardware version value.
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Retrieves the Hardware version description.
	 * 
	 * @return Hardware version description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Retrieves the Hardware version associated to the given ID.
	 * 
	 * @param value Hardware version value to retrieve.
	 * @return The Hardware version for the given value, null if not exists.
	 */
	public static HardwareVersion get(int value) {
		HardwareVersionEnum hvEnum = HardwareVersionEnum.get(value);
		if (hvEnum == null)
			return new HardwareVersion(value, "Unknown");
		return new HardwareVersion(hvEnum.getValue(), hvEnum.getDescription());
	}
	
	/**
	 * Retrieves the Hardware version associated to the given ID.
	 * 
	 * @param value Hardware version value to retrieve.
	 * @param description The hardware description.
	 * @return The Hardware version for the given ID, null if not exists.
	 */
	public static HardwareVersion get(int value, String description) {
		return new HardwareVersion(value, description);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "" + value;
	}
}
