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

import com.digi.xbee.api.utils.HexUtils;

public class CellularAssociationIndicationStatusTest {

	// Variables.
	private CellularAssociationIndicationStatus[] associationIndicationStatusValues;
	
	
	@Before
	public void setup() {
		// Retrieve the list of enum. values.
		associationIndicationStatusValues = CellularAssociationIndicationStatus.values();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.CellularAssociationIndicationStatus#getValue()}.
	 * 
	 * <p>Verify that the ID of each {@code CellularAssociationIndicationStatus} 
	 * entry is valid.</p>
	 */
	@Test
	public void testAssociationIndicationStatusValues() {
		for (CellularAssociationIndicationStatus associationIndicationStatus:associationIndicationStatusValues)
			assertTrue(associationIndicationStatus.getValue() >= 0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.CellularAssociationIndicationStatus#name()}.
	 * 
	 * <p>Verify that the name of each {@code CellularAssociationIndicationStatus} 
	 * entry is valid.</p>
	 */
	@Test
	public void testAssociationIndicationStatusNames() {
		for (CellularAssociationIndicationStatus associationIndicationStatus:associationIndicationStatusValues) {
			assertNotNull(associationIndicationStatus.name());
			assertTrue(associationIndicationStatus.name().length() > 0);
		}
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.CellularAssociationIndicationStatus#getDescription()}.
	 * 
	 * <p>Verify that the description of each {@code CellularAssociationIndicationStatus} 
	 * entry is valid.</p>
	 */
	@Test
	public void testAssociationIndicationStatusDescriptions() {
		for (CellularAssociationIndicationStatus associationIndicationStatus:associationIndicationStatusValues) {
			assertNotNull(associationIndicationStatus.getDescription());
			assertTrue(associationIndicationStatus.getDescription().length() > 0);
		}
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.CellularAssociationIndicationStatus#get(int)}.
	 * 
	 * <p>Verify that each {@code CellularAssociationIndicationStatus} entry can be 
	 * retrieved statically using its value.</p>
	 */
	@Test
	public void testAssociationIndicationStatusStaticAccess() {
		for (CellularAssociationIndicationStatus associationIndicationStatus:associationIndicationStatusValues)
			assertEquals(associationIndicationStatus, CellularAssociationIndicationStatus.get(associationIndicationStatus.getValue()));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.CellularAssociationIndicationStatus#get(int)}.
	 * 
	 * <p>Verify that when trying to get an invalid {@code CellularAssociationIndicationStatus} 
	 * entry, a {@code null} entry is retrieved.</p>
	 */
	@Test
	public void testAssociationIndicationStatusStaticInvalidAccess() {
		assertNull(CellularAssociationIndicationStatus.get(0xAA));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.CellularAssociationIndicationStatus#toString()}.
	 * 
	 * <p>Verify that the {@code toString()} method of an {@code CellularAssociationIndicationStatus} 
	 * entry returns its description correctly.</p>
	 */
	@Test
	public void testAssociationIndicationStatusToString() {
		for (CellularAssociationIndicationStatus associationIndicationStatus:associationIndicationStatusValues)
			assertEquals(HexUtils.byteToHexString((byte)associationIndicationStatus.getValue()) + ": " + associationIndicationStatus.getDescription(), associationIndicationStatus.toString());
	}
}