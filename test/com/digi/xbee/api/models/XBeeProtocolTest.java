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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class XBeeProtocolTest {

	// Constants.
	private static final int INVALID_ID = -1;
	
	// Variables.
	private XBeeProtocol[] xbeeProtocolValues;
	
	@Before
	public void setup() {
		// Retrieve the list of enum. values.
		xbeeProtocolValues = XBeeProtocol.values();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeProtocol#getID()}.
	 * 
	 * <p>Verify that the value of each HardwareVersionEnum entry is valid.</p>
	 */
	@Test
	public void testHardwareVersionEnumValues() {
		for (XBeeProtocol xbeeProtocol:xbeeProtocolValues)
			assertTrue(xbeeProtocol.getID() >= 0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeProtocol#name()}.
	 * 
	 * <p>Verify that the name of each XBeeProtocol entry is valid.</p>
	 */
	@Test
	public void testXBeeProtocolNames() {
		for (XBeeProtocol xbeeProtocol:xbeeProtocolValues) {
			assertNotNull(xbeeProtocol.name());
			assertTrue(xbeeProtocol.name().length() > 0);
		}
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeProtocol#getDescription()}.
	 * 
	 * <p>Verify that the description of each XBeeProtocol entry is valid.</p>
	 */
	@Test
	public void testXBeeProtocolDescriptions() {
		for (XBeeProtocol xbeeProtocol:xbeeProtocolValues) {
			assertNotNull(xbeeProtocol.getDescription());
			assertTrue(xbeeProtocol.getDescription().length() > 0);
		}
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeProtocol#get(int)}.
	 * 
	 * <p>Verify that each XBeeProtocol entry can be retrieved statically using its ID.</p>
	 */
	@Test
	public void testXBeeProtocolStaticAccess() {
		for (XBeeProtocol xbeeProtocol:xbeeProtocolValues)
			assertEquals(xbeeProtocol, XBeeProtocol.get(xbeeProtocol.getID()));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeProtocol#get(int)}.
	 * 
	 * <p>Verify that "UNKNOWN" protocol is retrieved statically using an invalid ID.</p>
	 */
	@Test
	public void testUnknownProtocolIsRetrievedWithInvalidID() {
		assertEquals(XBeeProtocol.UNKNOWN, XBeeProtocol.get(INVALID_ID));
	}
	
	// TODO: We need to test the determineProtocol() method. For that purpose we need to create a table 
	// with all the existing firmware files from XCTU providing their HW version, FW version and protocol.
}
