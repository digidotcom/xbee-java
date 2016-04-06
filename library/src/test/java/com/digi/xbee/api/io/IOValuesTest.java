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

public class IOValuesTest {

	// Constants.
	private static final int INVALID_ID = -1;
	
	// Variables.
	private IOValue[] ioValues;
	
	@Before
	public void setup() {
		// Retrieve the list of enum. values.
		ioValues = IOValue.values();
	}
	
	/**
	 * Verify that the name of each IO Line enum entry is valid.
	 */
	@Test
	public void testIOValueNames() {
		for (IOValue ioValue:ioValues) {
			assertNotNull(ioValue.getName());
			assertTrue(ioValue.getName().length() > 0);
		}
	}
	
	/**
	 * Verify that each IO value enum. value can be retrieved statically 
	 * using its ID.
	 */
	@Test
	public void testIOValueStaticAccess() {
		for (IOValue ioValue:ioValues)
			assertEquals(ioValue, IOValue.getIOValue(ioValue.getID()));
	}
	
	/**
	 * Verify that when trying to get an IO value not contained in the enumeration, 
	 * a null value is retrieved.
	 */
	@Test
	public void testNullIOValueIsRetrievedWithInvalidID() {
		assertNull(IOValue.getIOValue(INVALID_ID));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.io.IOValue#toString()}.
	 */
	@Test
	public void testToString() {
		for (IOValue ioValue:ioValues)
			assertEquals("toString() method does not produce the expected output",
					ioValue.getName(), ioValue.toString());
	}
}
