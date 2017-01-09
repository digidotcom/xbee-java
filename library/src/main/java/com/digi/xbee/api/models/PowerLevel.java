/**
 * Copyright 2017, Digi International Inc.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES 
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR 
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES 
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN 
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF 
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package com.digi.xbee.api.models;

import java.util.HashMap;

import com.digi.xbee.api.utils.HexUtils;

/**
 * Enumerates the different power levels. The power level indicates the output 
 * power value of a radio when transmitting data.
 */
public enum PowerLevel {

	// Enumeration entries
	LEVEL_LOWEST(0x00, "Lowest"),
	LEVEL_LOW(0x01, "Low"),
	LEVEL_MEDIUM(0x02, "Medium"),
	LEVEL_HIGH(0x03, "High"),
	LEVEL_HIGHEST(0x04, "Highest"),
	LEVEL_UNKNOWN(0xFF, "Unknown");
	
	// Variables
	private final int value;
	
	private final String description;
	
	private final static HashMap<Integer, PowerLevel> lookupTable = new HashMap<Integer, PowerLevel>();
	
	static {
		for (PowerLevel powerLevel:values())
			lookupTable.put(powerLevel.getValue(), powerLevel);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code PowerLevel} enumeration
	 * entry with the given parameters.
	 * 
	 * @param value Power level value 
	 * @param description Power level description.
	 */
	private PowerLevel(int value, String description) {
		this.value = value;
		this.description = description;
	}
	
	/**
	 * Returns the power level value.
	 * 
	 * @return The power level value.
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Returns the power level description.
	 * 
	 * @return The power level description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Returns the {@code PowerLevel} entry associated to the given value.
	 * 
	 * @param value Value of the {@code PowerLevel} to retrieve.
	 * 
	 * @return The {@code PowerLevel} entry associated to the given value, 
	 *         {@code #LEVEL_UNKNOWN} if the value could not be found in the 
	 *         list.
	 */
	public static PowerLevel get(int value) {
		PowerLevel powerLevel = lookupTable.get(value);
		if (powerLevel != null)
			return powerLevel;
		return LEVEL_UNKNOWN;
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
