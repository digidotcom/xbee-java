/*
 * Copyright 2019, Digi International Inc.
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
package com.digi.xbee.api.connection;

import java.util.HashMap;

/**
 * Enumerates the different connection types.
 * 
 * @since 1.3.0
 */
public enum ConnectionType {
	
	// Enumeration types.
	SERIAL(0x00, "Serial connection"),
	BLUETOOTH(0x01, "Bluetooth connection"),
	UNKNOWN(0xFF, "Unknown");

	// Variables.
	private int id;

	private String name;

	private static HashMap<Integer, ConnectionType> lookupTable = new HashMap<Integer, ConnectionType>();

	static {
		for (ConnectionType connectionType:values())
			lookupTable.put(connectionType.getID(), connectionType);
	}

	/**
	 * Class constructor. Instantiates a new {@code ConnectionType} enumeration
	 * entry with the given parameters.
	 *
	 * @param id Connection type ID.
	 * @param name Connection type name.
	 */
	private ConnectionType(int id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Retrieves the connection type ID.
	 *
	 * @return Connection type ID.
	 */
	public int getID() {
		return id;
	}

	/**
	 * Retrieves the connection type name.
	 *
	 * @return Connection type name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves the connection type for the given ID.
	 *
	 * @param id ID to retrieve the connection type.
	 *
	 * @return The connection type associated with the given ID.
	 *         {@code UNKNOWN} if the identifier is not in the enumeration.
	 */
	public static ConnectionType get(int id) {
		ConnectionType connectionType = lookupTable.get(id);
		if (connectionType != null)
			return connectionType;
		return UNKNOWN;
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
