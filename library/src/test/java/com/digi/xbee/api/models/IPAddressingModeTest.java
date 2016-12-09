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

public class IPAddressingModeTest {

	// Variables.
	private IPAddressingMode[] modes;


	@Before
	public void setup() {
		// Retrieve the list of enum. values.
		modes = IPAddressingMode.values();
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.IPAddressingMode#getID()}.
	 *
	 * <p>Verify that the ID of each {@code IPAddressingMode} entry is valid.</p>
	 */
	@Test
	public void testIPAddressingModeEnumValues() {
		for (IPAddressingMode mode:modes)
			assertTrue(mode.getID() >= 0);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.IPAddressingMode#name()}.
	 *
	 * <p>Verify that the name of each {@code IPAddressingMode} entry is valid.
	 * </p>
	 */
	@Test
	public void testIPAddressingModeEnumNames() {
		for (IPAddressingMode mode:modes) {
			assertNotNull(mode.name());
			assertTrue(mode.name().length() > 0);
		}
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.IPAddressingMode#get(int)}.
	 *
	 * <p>Verify that each {@code IPAddressingMode} entry can be retrieved
	 * statically using its value.</p>
	 */
	@Test
	public void testIPAddressingModeStaticAccess() {
		for (IPAddressingMode mode:modes)
			assertEquals(mode, IPAddressingMode.get(mode.getID()));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.IPAddressingMode#toString()}.
	 *
	 * <p>Verify that the {@code toString()} method of a {@code IPAddressingMode}
	 * entry returns its description correctly.</p>
	 */
	@Test
	public void testIPAddressingModeToString() {
		for (IPAddressingMode mode:modes)
			assertEquals(mode.getName(), mode.toString());
	}
}