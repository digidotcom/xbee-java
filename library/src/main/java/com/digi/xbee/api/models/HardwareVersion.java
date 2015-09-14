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
 * This class represents the hardware version number of an XBee device.
 */
public class HardwareVersion {
	
	// Constants.
	private static final int HASH_SEED = 23;
	
	// Variables.
	private final int value;
	
	private final String description;
	
	/**
	 * Class constructor. Instantiates a new {@code HardwareVersion} object 
	 * with the given parameters.
	 * 
	 * @param value The hardware version numeric value.
	 * @param description The hardware version description.
	 * 
	 * @throws IllegalArgumentException if {@code value < 0} or
	 *                                  if {@code description.length() < 1}.
	 * @throws NullPointerException if {@code description == null}.
	 */
	private HardwareVersion(int value, String description) {
		if (description == null)
			throw new NullPointerException("Description cannot be null.");
		if (value < 0)
			throw new IllegalArgumentException("Value cannot be less than 0.");
		if (description.length() < 1)
			throw new IllegalArgumentException("Description cannot be empty.");
		
		this.value = value;
		this.description = description;
	}
	
	/**
	 * Returns the Hardware version numeric value.
	 * 
	 * @return Hardware version numeric value.
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Returns the Hardware version description.
	 * 
	 * @return Hardware version description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Returns the {@code HardwareVersion} object associated to the given 
	 * numeric value.
	 * 
	 * @param value Numeric value of the {@code HardwareVersion} retrieve.
	 * 
	 * @return The {@code HardwareVersion} associated to the given value.
	 */
	public static HardwareVersion get(int value) {
		HardwareVersionEnum hvEnum = HardwareVersionEnum.get(value);
		if (hvEnum == null)
			return new HardwareVersion(value, "Unknown");
		return new HardwareVersion(hvEnum.getValue(), hvEnum.getDescription());
	}
	
	/**
	 * Returns the {@code HardwareVersion} object associated to the given 
	 * numeric value and description.
	 * 
	 * @param value Numeric value of the {@code HardwareVersion} retrieve.
	 * @param description Description of the {@code HardwareVersion} retrieve.
	 * 
	 * @return The {@code HardwareVersion} associated to the given value and 
	 *         description.
	 * 
	 * @throws IllegalArgumentException if {@code value < 0} or
	 *                                  if {@code description.length() < 1}.
	 * @throws NullPointerException if {@code description == null}.
	 */
	public static HardwareVersion get(int value, String description) {
		return new HardwareVersion(value, description);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof HardwareVersion))
			return false;
		HardwareVersion hwVersion = (HardwareVersion)obj;
		if (hwVersion.getValue() == getValue() 
				&& hwVersion.getDescription().equals(getDescription()))
			return true;
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = HASH_SEED * (HASH_SEED + value);
		return hash;
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
