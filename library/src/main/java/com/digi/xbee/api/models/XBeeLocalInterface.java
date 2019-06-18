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
package com.digi.xbee.api.models;

import java.util.HashMap;

/**
 * Enumerates the different XBee local interfaces used in the Relay API packets.
 *
 * @since 1.3.0
 */
public enum XBeeLocalInterface {

	// Enumeration types
	SERIAL(0, "Serial port"),
	BLUETOOTH(1, "Bluetooth Low Energy"),
	MICROPYTHON(2, "MicroPython"),
	UNKNOWN(-1, "Unknown");

	// Variables
	private int id;

	private String description;

	private static HashMap<Integer, XBeeLocalInterface> lookupTable = new HashMap<>();

	static {
		for (XBeeLocalInterface localInterface : values())
			lookupTable.put(localInterface.getID(), localInterface);
	}

	/**
	 * Class constructor. Instantiates a new {@code XBeeLocalInterface} entry
	 * with the given parameters.
	 *
	 * @param id {@code XBeeLocalInterface} ID code.
	 * @param description {@code XBeeLocalInterface} description.
	 */
	XBeeLocalInterface(int id, String description) {
		this.id = id;
		this.description = description;
	}

	/**
	 * Retrieves the {@code XBeeLocalInterface} ID code.
	 *
	 * @return {@code XBeeLocalInterface} ID code.
	 */
	public int getID() {
		return id;
	}

	/**
	 * Retrieves the {@code XBeeLocalInterface} description.
	 *
	 * @return {@code XBeeLocalInterface} description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Retrieves the {@code XBeeLocalInterface} for the given ID code.
	 *
	 * @param id ID code to retrieve the {@code XBeeLocalInterface}.
	 *
	 * @return The {@code XBeeLocalInterface} associated with the given ID code,
	 *         {@code UNKNOWN} if there is not any XBee local interface
	 *         associated to the provided ID.
	 */
	public static XBeeLocalInterface get(int id) {
		if (lookupTable.get(id) != null)
			return lookupTable.get(id);
		return UNKNOWN;
	}
}
