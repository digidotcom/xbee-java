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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HardwareVersionEnum.class})
public class HardwareVersionTest {

	/**
	 * Test method for {@link com.digi.xbee.api.models.HardwareVersion#get(int, String)}.
	 * 
	 * <p>Verify that it is not possible to get a HardWareVersion object statically if any of 
	 * the provided parameters is not valid using the get() method of the class.</p>
	 */
	@Test
	public void testGetHardwareVersionInvalid() {
		// Try to instantiate a HardwareVersion object with a null description.
		try {
			HardwareVersion.get(0, null);
		} catch(Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
		// Try to instantiate a HardwareVersion object with an invalid ID.
		try {
			HardwareVersion.get(-1, "Description");
		} catch(Exception e) {
			assertEquals(IllegalArgumentException.class, e.getClass());
		}
		// Try to instantiate a HardwareVersion object with an invalid description.
		try {
			HardwareVersion.get(0, "");
		} catch(Exception e) {
			assertEquals(IllegalArgumentException.class, e.getClass());
		}
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.HardwareVersion#get(int, String)}.
	 * 
	 * <p>Verify that it is possible to get a valid HardWareVersion object statically using 
	 * the get() method of the class.</p>
	 */
	@Test
	public void testGetHardwareVersionValid() {
		HardwareVersion hardwareVersion = HardwareVersion.get(0, "Description");
		
		assertEquals(0, hardwareVersion.getValue());
		assertEquals("Description", hardwareVersion.getDescription());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.HardwareVersion#HardwareVersion(String, int)}.
	 * 
	 * <p>Verify that HardwareVersion object can be created successfully.</p>
	 */
	@Test
	public void testCreateHardwareVersionSuccess() {
		HardwareVersion hardwareVersion = HardwareVersion.get(0, "Description");
		
		assertEquals(0, hardwareVersion.getValue());
		assertEquals("Description", hardwareVersion.getDescription());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.HardwareVersion#equals(Object)}.
	 * 
	 * <p>Verify that HardwareVersion equals method works as expected.</p>
	 */
	@Test
	public void testHardwareVersionEquals() {
		HardwareVersion hardwareVersion1 = HardwareVersion.get(0, "Description");
		HardwareVersion hardwareVersion2 = HardwareVersion.get(0, "Description");
		HardwareVersion hardwareVersion3 = HardwareVersion.get(0, "Different description");
		HardwareVersion hardwareVersion4 = HardwareVersion.get(1, "Description");
		HardwareVersion hardwareVersion5 = HardwareVersion.get(1, "Different description");
		
		assertTrue(hardwareVersion1.equals(hardwareVersion2));
		assertFalse(hardwareVersion1.equals(hardwareVersion3));
		assertFalse(hardwareVersion1.equals(hardwareVersion4));
		assertFalse(hardwareVersion1.equals(hardwareVersion5));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.HardwareVersion#get(int)}.
	 * 
	 * <p>Verify that a valid HardwareVersion is received when querying it using the static get() method 
	 * of the class and there is a HardwareVersionEnum entry for the provided ID.</p>
	 */
	@Test
	public void testGetHardwareVersionKnown() {
		// Prepare the HardwareVersionEnum class to return the XBP24B entry value when asked for one.
		PowerMockito.mockStatic(HardwareVersionEnum.class);
		PowerMockito.when(HardwareVersionEnum.get(Mockito.anyInt())).thenReturn(HardwareVersionEnum.XBP24B);
		
		HardwareVersion hardwareVersion = HardwareVersion.get(0);
		
		assertEquals(HardwareVersionEnum.XBP24B.getValue(), hardwareVersion.getValue());
		assertEquals(HardwareVersionEnum.XBP24B.getDescription(), hardwareVersion.getDescription());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.HardwareVersion#get(int)}.
	 * 
	 * <p>Verify that an Unknown HardwareVersion is received when querying it using the static get() method 
	 * of the class and there is not a HardwareVersionEnum entry for the provided ID.</p>
	 */
	@Test
	public void testGetHardwareVersionUnknown() {
		// Prepare the HardwareVersionEnum class to return a null entry value when asked for one.
		PowerMockito.mockStatic(HardwareVersionEnum.class);
		PowerMockito.when(HardwareVersionEnum.get(Mockito.anyInt())).thenReturn(null);
		
		HardwareVersion hardwareVersion = HardwareVersion.get(0);
		
		assertEquals(0, hardwareVersion.getValue());
		assertEquals("Unknown", hardwareVersion.getDescription());
	}
}
