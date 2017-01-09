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
 * Enumerates the different IP addressing modes.
 * 
 * @since 1.2.0
 */
public enum IPAddressingMode {

	// Enumeration types.
	DHCP(0, "DHCP"),
	STATIC(1, "Static");

	// Variables.
	private int id;

	private String name;

	private static HashMap<Integer, IPAddressingMode> lookupTable = new HashMap<Integer, IPAddressingMode>();

	static {
		for (IPAddressingMode mode:values())
			lookupTable.put(mode.getID(), mode);
	}

	/**
	 * Class constructor. Instantiates a new {@code IPAddressingMode} enumeration
	 * entry with the given parameters.
	 *
	 * @param id IP addressing mode ID.
	 * @param name IP addressing mode name.
	 */
	private IPAddressingMode(int id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Retrieves the IP addressing mode ID.
	 *
	 * @return IP addressing mode ID.
	 */
	public int getID() {
		return id;
	}

	/**
	 * Retrieves the IP addressing mode name.
	 *
	 * @return IP addressing mode name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves the IP addressing mode for the given ID.
	 *
	 * @param id ID to retrieve the IP addressing mode.
	 *
	 * @return The IP addressing mode associated with the given ID.
	 */
	public static IPAddressingMode get(int id) {
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
