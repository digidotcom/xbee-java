/**
 * Copyright (c) 2016-2017 Digi International Inc.,
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

public class WiFiEncryptionTypeTest {

	// Variables.
	private WiFiEncryptionType[] encryptionTypeValues;

	@Before
	public void setup() {
		// Retrieve the list of enum. values.
		encryptionTypeValues = WiFiEncryptionType.values();
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.WiFiEncryptionType#getID()}.
	 *
	 * <p>Verify that the ID of each {@code WiFiEncryptionType} entry is valid.</p>
	 */
	@Test
	public void testWiFiEncryptionTypeEnumValues() {
		for (WiFiEncryptionType encryptionType:encryptionTypeValues)
			assertTrue(encryptionType.getID() >= 0);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.WiFiEncryptionType#name()}.
	 *
	 * <p>Verify that the name of each {@code WiFiEncryptionType} entry is valid.
	 * </p>
	 */
	@Test
	public void testWiFiEncryptionTypeEnumNames() {
		for (WiFiEncryptionType encryptionType:encryptionTypeValues) {
			assertNotNull(encryptionType.name());
			assertTrue(encryptionType.name().length() > 0);
		}
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.WiFiEncryptionType#get(int)}.
	 *
	 * <p>Verify that each {@code WiFiEncryptionType} entry can be retrieved
	 * statically using its value.</p>
	 */
	@Test
	public void testWiFiEncryptionTypeStaticAccess() {
		for (WiFiEncryptionType encryptionType:encryptionTypeValues)
			assertEquals(encryptionType, WiFiEncryptionType.get(encryptionType.getID()));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.WiFiEncryptionType#toString()}.
	 *
	 * <p>Verify that the {@code toString()} method of a {@code WiFiEncryptionType}
	 * entry returns its description correctly.</p>
	 */
	@Test
	public void testWiFiEncryptionTypeToString() {
		for (WiFiEncryptionType encryptionType:encryptionTypeValues)
			assertEquals(encryptionType.getName(), encryptionType.toString());
	}
}