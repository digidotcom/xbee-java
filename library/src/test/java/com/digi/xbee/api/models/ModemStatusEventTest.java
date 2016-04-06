/**
 * Copyright (c) 2014-2016 Digi International Inc.,
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

public class ModemStatusEventTest {

	// Variables.
	private ModemStatusEvent[] modemStatusEventValues;
	
	
	@Before
	public void setup() {
		// Retrieve the list of enum. values.
		modemStatusEventValues = ModemStatusEvent.values();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.ModemStatusEvent#getId()}.
	 * 
	 * <p>Verify that the ID of each ModemStatusEvent entry is valid.</p>
	 */
	@Test
	public void testModemStatusEventValues() {
		for (ModemStatusEvent modemStatusEvent:modemStatusEventValues)
			assertTrue(modemStatusEvent.getId() >= 0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.ModemStatusEvent#name()}.
	 * 
	 * <p>Verify that the name of each ModemStatusEvent entry is valid.</p>
	 */
	@Test
	public void testModemStatusEventNames() {
		for (ModemStatusEvent modemStatusEvent:modemStatusEventValues) {
			assertNotNull(modemStatusEvent.name());
			assertTrue(modemStatusEvent.name().length() > 0);
		}
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.ModemStatusEvent#getDescription()}.
	 * 
	 * <p>Verify that the description of each ModemStatusEvent entry is valid.</p>
	 */
	@Test
	public void testModemStatusEventDescriptions() {
		for (ModemStatusEvent modemStatusEvent:modemStatusEventValues) {
			assertNotNull(modemStatusEvent.getDescription());
			assertTrue(modemStatusEvent.getDescription().length() > 0);
		}
	}
	
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.ModemStatusEvent#get(int)}.
	 * 
	 * <p>Verify that each ModemStatusEvent entry can be retrieved statically using its ID.</p>
	 */
	@Test
	public void testModemStatusEventStaticAccess() {
		for (ModemStatusEvent modemStatusEvent:modemStatusEventValues)
			assertEquals(modemStatusEvent, ModemStatusEvent.get(modemStatusEvent.getId()));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.ModemStatusEvent#toString()}.
	 * 
	 * <p>Verify that the {@code toString()} method of a ModemStatusEvent entry returns its 
	 * description correctly.</p>
	 */
	@Test
	public void testModemStatusEventToString() {
		for (ModemStatusEvent modemStatusEvent:modemStatusEventValues)
			assertEquals(String.format("0x%02X: %s", modemStatusEvent.getId(), modemStatusEvent.getDescription()), modemStatusEvent.toString());
	}
}
