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
 * Enumerates the different IP protocols.
 * 
 * @since 1.2.0
 */
public enum IPProtocol {

	// Enumeration types.
	UDP(0, "UDP"),
	TCP(1, "TCP"),
	COAP(3, "CoAP"),
	TCP_SSL(4, "TCP SSL");

	// Variables.
	private int id;

	private String name;

	private static HashMap<Integer, IPProtocol> lookupTable = new HashMap<Integer, IPProtocol>();

	static {
		for (IPProtocol protocol:values())
			lookupTable.put(protocol.getID(), protocol);
	}

	/**
	 * Class constructor. Instantiates a new {@code IPProtocol} enumeration
	 * entry with the given parameters.
	 *
	 * @param id IP protocol ID.
	 * @param name IP protocol name.
	 */
	private IPProtocol(int id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Retrieves the IP protocol ID.
	 *
	 * @return IP protocol ID.
	 */
	public int getID() {
		return id;
	}

	/**
	 * Retrieves the IP protocol name.
	 *
	 * @return IP protocol name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves the IP protocol for the given ID.
	 *
	 * @param id ID to retrieve the IP protocol.
	 *
	 * @return The IP protocol associated with the given ID.
	 */
	public static IPProtocol get(int id) {
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
