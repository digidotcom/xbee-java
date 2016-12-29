/**
 * Copyright (c) 2016 Digi International Inc.,
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
