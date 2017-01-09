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
package com.digi.xbee.api.io;

import java.util.HashMap;

/**
 * Enumerates the possible values of a {@code IOLine} configured as digital 
 * I/O.
 * 
 * @see IOLine
 */
public enum IOValue {

	// Enumeration types.
	LOW(4, "Low"),
	HIGH(5, "High");
	
	// Variables.
	private final static HashMap <Integer, IOValue> lookupTable = new HashMap<Integer, IOValue>();
	
	private final int id;
	
	private final String name;
	
	static {
		for (IOValue ioValue:values())
			lookupTable.put(ioValue.getID(), ioValue);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code IOValue} enumeration entry 
	 * with the given parameters.
	 * 
	 * @param id IO value ID.
	 * @param name IO value name.
	 */
	private IOValue(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	/**
	 * Returns the ID of the IO value.
	 * 
	 * @return The ID of the IO value.
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Returns the name of the IO value.
	 * 
	 * @return The name of the IO value.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the {@code IOValue} associated to the provided value ID.
	 * 
	 * @param valueID The ID of the {@code IOValue} to retrieve.
	 * 
	 * @return The {@code IOValue} associated to the provided value ID.
	 */
	public static IOValue getIOValue(int valueID) {
		if (lookupTable.containsKey(valueID))
			return lookupTable.get(valueID);
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
