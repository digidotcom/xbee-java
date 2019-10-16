/*
 * Copyright 2017-2019, Digi International Inc.
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
	/** @since 1.3.1 */
	MICROPYTHON(4, "MicroPython REPL"),
	/** @since 1.3.1 */
	BYPASS(5, "Bypass mode"),
	UNKNOWN(3, "Unknown");
	
	// Variables
	private final int id;
	
	private final String name;
	
	private final static HashMap<Integer, OperatingMode> lookupTable = new HashMap<Integer, OperatingMode>();
	
	static {
		for (OperatingMode operatingMode:values())
			lookupTable.put(operatingMode.getID(), operatingMode);
	}
	
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
	
	/**
	 * Returns the {@code OperatingMode} entry associated to the given value.
	 * 
	 * @param value Value of the {@code OperatingMode} to retrieve.
	 * 
	 * @return The {@code OperatingMode} entry associated to the given value,
	 *         {@code #UNKNOWN} if the value could not be found in the list.
	 * 
	 * @since 1.3.1
	 */
	public static OperatingMode get(int value) {
		OperatingMode operatingMode = lookupTable.get(value);
		if (operatingMode != null)
			return operatingMode;
		return OperatingMode.UNKNOWN;
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
