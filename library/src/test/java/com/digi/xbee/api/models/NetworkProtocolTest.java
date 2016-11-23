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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class NetworkProtocolTest {

	// Variables.
	private NetworkProtocol[] protocolValues;


	@Before
	public void setup() {
		// Retrieve the list of enum. values.
		protocolValues = NetworkProtocol.values();
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.NetworkProtocol#getID()}.
	 *
	 * <p>Verify that the ID of each {@code NetworkProtocol} entry is valid.</p>
	 */
	@Test
	public void testNetworkProtocolEnumValues() {
		for (NetworkProtocol protocol:protocolValues)
			assertTrue(protocol.getID() >= 0);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.NetworkProtocol#name()}.
	 *
	 * <p>Verify that the name of each {@code NetworkProtocol} entry is valid.
	 * </p>
	 */
	@Test
	public void testNetworkProtocolEnumNames() {
		for (NetworkProtocol protocol:protocolValues) {
			assertNotNull(protocol.name());
			assertTrue(protocol.name().length() > 0);
		}
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.NetworkProtocol#get(int)}.
	 *
	 * <p>Verify that each {@code NetworkProtocol} entry can be retrieved
	 * statically using its value.</p>
	 */
	@Test
	public void testNetworkProtocolStaticAccess() {
		for (NetworkProtocol protocol:protocolValues)
			assertEquals(protocol, NetworkProtocol.get(protocol.getID()));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.NetworkProtocol#toString()}.
	 *
	 * <p>Verify that the {@code toString()} method of a {@code NetworkProtocol}
	 * entry returns its description correctly.</p>
	 */
	@Test
	public void testNetworkProtocolToString() {
		for (NetworkProtocol protocol:protocolValues)
			assertEquals(protocol.getName(), protocol.toString());
	}
}