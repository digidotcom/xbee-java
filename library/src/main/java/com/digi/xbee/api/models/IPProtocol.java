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
 * Enumerates the different IP protocols.
 * 
 * @since 1.2.0
 */
public enum IPProtocol {

	// Enumeration types.
	UDP(0, "UDP"),
	TCP(1, "TCP"),
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
