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

/**
 * Enumerates the different Wi-Fi encryption types.
 * 
 * @since 1.2.0
 */
public enum WiFiEncryptionType {

	// Enumeration types.
	NONE(0, "No security"),
	WPA(1, "WPA (TKIP) security"),
	WPA2(2, "WPA2 (AES) security"),
	WEP(3, "WEP security");

	// Variables.
	private int id;

	private String name;

	private static HashMap<Integer, WiFiEncryptionType> lookupTable = new HashMap<Integer, WiFiEncryptionType>();

	static {
		for (WiFiEncryptionType encryptionType:values())
			lookupTable.put(encryptionType.getID(), encryptionType);
	}

	/**
	 * Class constructor. Instantiates a new {@code WiFiEncryptionType} enumeration
	 * entry with the given parameters.
	 *
	 * @param id Encryption type ID.
	 * @param name Encryption type name.
	 */
	private WiFiEncryptionType(int id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Retrieves the encryption type ID.
	 *
	 * @return Encryption type ID.
	 */
	public int getID() {
		return id;
	}

	/**
	 * Retrieves the encryption type name.
	 *
	 * @return Encryption type name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves the encryption type for the given ID.
	 *
	 * @param id ID to retrieve the encryption type.
	 *
	 * @return The encryption type associated with the given ID.
	 */
	public static WiFiEncryptionType get(int id) {
		return lookupTable.get(id);
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
