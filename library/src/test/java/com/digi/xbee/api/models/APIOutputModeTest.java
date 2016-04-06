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

import com.digi.xbee.api.utils.HexUtils;

public class APIOutputModeTest {

	// Variables.
	private APIOutputMode[] apiOutputModeValues;
	
	@Before
	public void setup() {
		// Retrieve the list of enum. values.
		apiOutputModeValues = APIOutputMode.values();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.APIOutputMode#getValue()}.
	 * 
	 * <p>Verify that the ID of each {@code APIOutputMode} entry is valid.</p>
	 */
	@Test
	public void testAPIOutputModeEnumValues() {
		for (APIOutputMode apiOutputMode:apiOutputModeValues)
			assertTrue(apiOutputMode.getValue() >= 0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.APIOutputMode#name()}.
	 * 
	 * <p>Verify that the name of each {@code APIOutputMode} entry is valid.</p>
	 */
	@Test
	public void testAPIOutputModeEnumNames() {
		for (APIOutputMode apiOutputMode:apiOutputModeValues) {
			assertNotNull(apiOutputMode.name());
			assertTrue(apiOutputMode.name().length() > 0);
		}
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.APIOutputMode#getDescription()}.
	 * 
	 * <p>Verify that the description of each {@code APIOutputMode} entry is valid.</p>
	 */
	@Test
	public void testAPIOutputModeEnumDescriptions() {
		for (APIOutputMode apiOutputMode:apiOutputModeValues) {
			assertNotNull(apiOutputMode.getDescription());
			assertTrue(apiOutputMode.getDescription().length() > 0);
		}
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.APIOutputMode#get(int)}.
	 * 
	 * <p>Verify that each {@code APIOutputMode} entry can be retrieved statically using its 
	 * value.</p>
	 */
	@Test
	public void testAPIOutputModeStaticAccess() {
		for (APIOutputMode apiOutputMode:apiOutputModeValues)
			assertEquals(apiOutputMode, APIOutputMode.get(apiOutputMode.getValue()));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.APIOutputMode#get(int)}.
	 * 
	 * <p>Verify that when trying to get an invalid {@code APIOutputMode} entry, 
	 * {@code null} is retrieved.</p>
	 */
	@Test
	public void testAPIOutputModeStaticInvalidAccess() {
		assertNull(APIOutputMode.get(10));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.APIOutputMode#toString()}.
	 * 
	 * <p>Verify that the {@code toString()} method of a {@code APIOutputMode} entry returns 
	 * its description correctly.</p>
	 */
	@Test
	public void testAPIOutputModeToString() {
		for (APIOutputMode apiOutputMode:apiOutputModeValues)
			assertEquals(HexUtils.byteToHexString((byte)apiOutputMode.getValue()) + ": " + apiOutputMode.getDescription(), apiOutputMode.toString());
	}
}