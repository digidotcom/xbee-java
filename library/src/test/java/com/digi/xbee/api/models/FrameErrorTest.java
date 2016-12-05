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

public class FrameErrorTest {

	// Variables.
	private FrameError[] errors;


	@Before
	public void setup() {
		// Retrieve the list of enum. values.
		errors = FrameError.values();
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.FrameError#getID()}.
	 *
	 * <p>Verify that the ID of each {@code FrameError} entry is valid.</p>
	 */
	@Test
	public void testFrameErrorEnumValues() {
		for (FrameError error:errors)
			assertTrue(error.getID() >= 0);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.FrameError#getName()}.
	 *
	 * <p>Verify that the name of each {@code FrameError} entry is valid.
	 * </p>
	 */
	@Test
	public void testFrameErrorEnumNames() {
		for (FrameError error:errors) {
			assertNotNull(error.getName());
			assertTrue(error.name().length() > 0);
		}
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.FrameError#get(int)}.
	 *
	 * <p>Verify that each {@code FrameError} entry can be retrieved
	 * statically using its value.</p>
	 */
	@Test
	public void testFrameErrorStaticAccess() {
		for (FrameError error:errors)
			assertEquals(error, FrameError.get(error.getID()));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.FrameError#toString()}.
	 *
	 * <p>Verify that the {@code toString()} method of a {@code FrameError}
	 * entry returns its description correctly.</p>
	 */
	@Test
	public void testFrameErrorToString() {
		for (FrameError error:errors)
			assertEquals(error.getName(), error.toString());
	}
}