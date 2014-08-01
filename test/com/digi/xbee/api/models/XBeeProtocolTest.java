/**
* Copyright (c) 2014 Digi International Inc.,
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class XBeeProtocolTest {

	// Constants.
	private static final int INVALID_ID = -1;
	
	// Variables.
	private XBeeProtocol[] values;
	
	@Before
	public void setup() {
		// Retrieve the list of enum values.
		values = XBeeProtocol.values();
	}
	
	@Test
	/**
	 * Verifies that the name of each XBee Protocol Enum entry is valid.
	 */
	public void testXBeeProtocolNames() {
		// Verify the name of each enum entry is not null and length is greater than 0.
		for (XBeeProtocol protocol:values) {
			assertNotNull(protocol.name());
			assertTrue(protocol.name().length() > 0);
		}
	}
	
	@Test
	/**
	 * Verifies that the description of each XBee Protocol Enum entry is valid.
	 */
	public void testXBeeProtocolDescriptions() {
		// Verify the description of each enum entry is not null and length is greater than 0.
		for (XBeeProtocol protocol:values) {
			assertNotNull(protocol.getDescription());
			assertTrue(protocol.getDescription().length() > 0);
		}
	}
	
	@Test
	/**
	 * Verifies that each of the XBee Protocol enum values can be retrieved statically using their IDs.
	 */
	public void testXBeeProtocolStaticAccess() {
		// Verify that each of the XBee Protocol enum values can be retrieved statically using their IDs.
		for (XBeeProtocol protocol:values)
			assertEquals(protocol, XBeeProtocol.get(protocol.getID()));
	}
	
	@Test
	/**
	 * Verifies that "UNKNOWN" protocol is retrieved statically using an invalid ID.
	 */
	public void testUnknownProtocolIsRetrievedWithInvalidID() {
		// Verify that "UNKNOWN" protocol is retrieved statically using an invalid ID.
		assertEquals(XBeeProtocol.UNKNOWN, XBeeProtocol.get(INVALID_ID));
	}
}
