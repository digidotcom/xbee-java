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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class XBeeDiscoveryStatusTest {
	
	// Variables.
	private static XBeeDiscoveryStatus[] discoveryStatusValues;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Retrieve the list of enum. values.
		discoveryStatusValues = XBeeDiscoveryStatus.values();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeDiscoveryStatus#get(int)}.
	 */
	@Test
	public void testGetWithASupportedValue() {
		// Setup the resources for the test.
		XBeeDiscoveryStatus expectedStatus = XBeeDiscoveryStatus.DISCOVERY_STATUS_EXTENDED_TIMEOUT_DISCOVERY;
		int id = expectedStatus.getId();
		
		// Call the method under test.
		XBeeDiscoveryStatus status = XBeeDiscoveryStatus.get(id);
		
		// Verify the result.
		assertThat("The identifier of both status does not have the same value", status.getId(), is(equalTo(id)));
		assertThat("Transmit status is not the expected", status, is(equalTo(expectedStatus)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeDiscoveryStatus#get(int)}.
	 */
	@Test
	public void testGetWithANonSupportedValue() {
		// Setup the resources for the test.
		XBeeDiscoveryStatus expectedStatus = XBeeDiscoveryStatus.DISCOVERY_STATUS_UNKNOWN;
		int id = 1234567890;
		
		// Call the method under test.
		XBeeDiscoveryStatus status = XBeeDiscoveryStatus.get(id);
		
		// Verify the result.
		assertThat("The identifier of both status does not have the same value", status.getId(), is(equalTo(expectedStatus.getId())));
		assertThat("The description of both status does not have the same value", status.getDescription(), is(equalTo(expectedStatus.getDescription())));
		assertThat("Transmit status is not the expected", status, is(equalTo(expectedStatus)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeDiscoveryStatus#getValue()}.
	 * 
	 * <p>Verify that the value of each XBeeDiscoveryStatus entry is valid.</p>
	 */
	@Test
	public void testXBeeDiscoveryStatusValues() {
		for (XBeeDiscoveryStatus status: discoveryStatusValues)
			assertTrue(status.getId() >= 0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeDiscoveryStatus#name()}.
	 * 
	 * <p>Verify that the name of each XBeeDiscoveryStatus entry is valid.</p>
	 */
	@Test
	public void testXBeeDiscoveryStatusNames() {
		for (XBeeDiscoveryStatus status: discoveryStatusValues) {
			assertNotNull(status.name());
			assertTrue(status.name().length() > 0);
		}
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeDiscoveryStatus#getDescription()}.
	 * 
	 * <p>Verify that the description of each XBeeDiscoveryStatus entry is valid.</p>
	 */
	@Test
	public void testXBeeDiscoveryStatusDescriptions() {
		for (XBeeDiscoveryStatus status: discoveryStatusValues) {
			assertNotNull(status.getDescription());
			assertTrue(status.getDescription().length() > 0);
		}
	}
	
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeDiscoveryStatus#get(int)}.
	 * 
	 * <p>Verify that each XBeeDiscoveryStatus entry can be retrieved statically using its ID.</p>
	 */
	@Test
	public void testXBeeDiscoveryStatusStaticAccess() {
		for (XBeeDiscoveryStatus status: discoveryStatusValues)
			assertEquals(status, XBeeDiscoveryStatus.get(status.getId()));
	}
}
