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
 * Enumerates the different association indication status for the Cellular 
 * protocol.
 * 
 * @since 1.2.0
 */
public enum CellularAssociationIndicationStatus {

	// Enumeration entries
	SUCCESSFULLY_CONNECTED(0x00, "Connected to the Internet."),
	REGISTERING_CELLULAR_NETWORK(0x22, "Registering to cellular network"),
	CONNECTING_INTERNET(0x23, "Connecting to the Internet"),
	BYPASS_MODE(0x2F, "Bypass mode active"),
	INITIALIZING(0xFF, "Initializing");
	
	// Variables
	private final int value;
	
	private final String description;
	
	private final static HashMap<Integer, CellularAssociationIndicationStatus> lookupTable = new HashMap<Integer, CellularAssociationIndicationStatus>();
	
	static {
		for (CellularAssociationIndicationStatus associationIndicationStatus:values())
			lookupTable.put(associationIndicationStatus.getValue(), associationIndicationStatus);
	}
	
	/**
	 * Class constructor. Instantiates a new 
	 * {@code CellularAssociationIndicationStatus} enumeration entry with the 
	 * given parameters.
	 * 
	 * @param value Cellular association indication status value.
	 * @param description Cellular association indication status description.
	 */
	CellularAssociationIndicationStatus(int value, String description) {
		this.value = value;
		this.description = description;
	}
	
	/**
	 * Returns the Cellular association indication status value.
	 * 
	 * @return The Cellular association indication status value.
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Returns the Cellular association indication status description.
	 * 
	 * @return The Cellular association indication status description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Returns the {@code CellularAssociationIndicationStatus} associated to 
	 * the given value.
	 * 
	 * @param value Value of the Cellular association indication status to 
	 *              retrieve.
	 * 
	 * @return The Cellular association indication status of the associated 
	 *         value, {@code null} if it could not be found in the table.
	 */
	public static CellularAssociationIndicationStatus get(int value) {
		return lookupTable.get(value);
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
