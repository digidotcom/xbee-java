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
 * Enumerates the different network protocols.
 */
public enum NetworkProtocol {

	// Enumeration types.
	UDP(0, "UDP"),
	TCP(1, "TCP"),
	TCP_SSL(4, "TCP SSL");

	// Variables.
	private int id;

	private String name;

	private static HashMap<Integer, NetworkProtocol> lookupTable = new HashMap<Integer, NetworkProtocol>();

	static {
		for (NetworkProtocol protocol:values())
			lookupTable.put(protocol.getID(), protocol);
	}

	/**
	 * Class constructor. Instantiates a new {@code NetworkProtocol} enumeration
	 * entry with the given parameters.
	 *
	 * @param id Network protocol ID.
	 * @param name Network protocol name.
	 */
	private NetworkProtocol(int id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Retrieves the network protocol ID.
	 *
	 * @return Network protocol ID.
	 */
	public int getID() {
		return id;
	}

	/**
	 * Retrieves the network protocol name.
	 *
	 * @return Network protocol name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves the network protocol for the given ID.
	 *
	 * @param id ID to retrieve the network protocol.
	 *
	 * @return The network protocol associated with the given ID.
	 */
	public static NetworkProtocol get(int id) {
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
