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
package com.digi.xbee.api.io;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class IOModeTest {

	// Constants.
	private static final int INVALID_ID = -1;
	
	// Variables.
	private IOMode[] ioModes;
	
	@Before
	public void setup() {
		// Retrieve the list of enum. values.
		ioModes = IOMode.values();
	}
	
	/**
	 * Verify that the name of each IO mode entry is valid.
	 */
	@Test
	public void testIOModeNames() {
		for (IOMode ioMode:ioModes) {
			assertNotNull(ioMode.getName());
			assertTrue(ioMode.getName().length() > 0);
		}
	}
	
	/**
	 * Verify that each IO mode enum. value can be retrieved statically 
	 * using its ID.
	 */
	@Test
	public void testIOModeStaticAccess() {
		for (IOMode ioMode:ioModes) {
			if (ioMode != IOMode.PWM)
				assertEquals(ioMode, IOMode.getIOMode(ioMode.getID()));
			else {
				assertEquals(IOMode.ADC, IOMode.getIOMode(ioMode.getID()));
				for (IOLine ioLine:IOLine.values()) {
					if (ioLine.hasPWMCapability())
						assertEquals(IOMode.PWM, IOMode.getIOMode(ioMode.getID(), ioLine));
					else
						assertEquals(IOMode.ADC, IOMode.getIOMode(ioMode.getID(), ioLine));
				}
			}
		}
	}
	
	/**
	 * Verify that when trying to get an IO mode not contained in the enumeration, 
	 * a null value is retrieved.
	 */
	@Test
	public void testNullIOModeIsRetrievedWithInvalidID() {
		assertNull(IOMode.getIOMode(INVALID_ID));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.io.IOMode#toString()}.
	 */
	@Test
	public void testToString() {
		for (IOMode ioMode:ioModes)
			assertEquals("toString() method does not produce the expected output",
					ioMode.getName(), ioMode.toString());
	}
}
