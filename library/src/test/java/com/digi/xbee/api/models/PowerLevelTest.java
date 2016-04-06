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

public class PowerLevelTest {

	// Variables.
	private PowerLevel[] powerLevelValues;
	
	
	@Before
	public void setup() {
		// Retrieve the list of enum. values.
		powerLevelValues = PowerLevel.values();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.PowerLevel#getValue()}.
	 * 
	 * <p>Verify that the ID of each {@code PowerLevel} entry is valid.</p>
	 */
	@Test
	public void testPowerLevelEnumValues() {
		for (PowerLevel powerLevel:powerLevelValues)
			assertTrue(powerLevel.getValue() >= 0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.PowerLevel#name()}.
	 * 
	 * <p>Verify that the name of each {@code PowerLevel} entry is valid.</p>
	 */
	@Test
	public void testPowerLevelEnumNames() {
		for (PowerLevel powerLevel:powerLevelValues) {
			assertNotNull(powerLevel.name());
			assertTrue(powerLevel.name().length() > 0);
		}
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.PowerLevel#getDescription()}.
	 * 
	 * <p>Verify that the description of each {@code PowerLevel} entry is valid.</p>
	 */
	@Test
	public void testPowerLevelEnumDescriptions() {
		for (PowerLevel powerLevel:powerLevelValues) {
			assertNotNull(powerLevel.getDescription());
			assertTrue(powerLevel.getDescription().length() > 0);
		}
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.PowerLevel#get(int)}.
	 * 
	 * <p>Verify that each {@code PowerLevel} entry can be retrieved statically using its 
	 * value.</p>
	 */
	@Test
	public void testPowerLevelStaticAccess() {
		for (PowerLevel powerLevel:powerLevelValues)
			assertEquals(powerLevel, PowerLevel.get(powerLevel.getValue()));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.PowerLevel#get(int)}.
	 * 
	 * <p>Verify that when trying to get an invalid {@code PowerLevel} entry, 
	 * UNKNOWN power level is retrieved.</p>
	 */
	@Test
	public void testPowerLevelStaticInvalidAccess() {
		assertEquals(PowerLevel.LEVEL_UNKNOWN, PowerLevel.get(10));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.PowerLevel#toString()}.
	 * 
	 * <p>Verify that the {@code toString()} method of a {@code PowerLevel} entry returns 
	 * its description correctly.</p>
	 */
	@Test
	public void testPowerLevelToString() {
		for (PowerLevel powerLevel:powerLevelValues)
			assertEquals(HexUtils.byteToHexString((byte)powerLevel.getValue()) + ": " + powerLevel.getDescription(), powerLevel.toString());
	}
}