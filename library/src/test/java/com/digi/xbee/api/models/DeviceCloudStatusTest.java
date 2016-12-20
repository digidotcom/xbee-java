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

public class DeviceCloudStatusTest {

	// Variables.
	private DeviceCloudStatus[] dcStatuses;


	@Before
	public void setup() {
		// Retrieve the list of enum. values.
		dcStatuses = DeviceCloudStatus.values();
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.DeviceCloudStatus#getID()}.
	 *
	 * <p>Verify that the ID of each {@code DeviceCloudStatus} entry is valid.</p>
	 */
	@Test
	public void testDeviceCloudStatusEnumValues() {
		for (DeviceCloudStatus status:dcStatuses)
			assertTrue(status.getID() >= 0);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.DeviceCloudStatus#getName()}.
	 *
	 * <p>Verify that the name of each {@code DeviceCloudStatus} entry is valid.
	 * </p>
	 */
	@Test
	public void testDeviceCloudStatusEnumNames() {
		for (DeviceCloudStatus status:dcStatuses) {
			assertNotNull(status.getName());
			assertTrue(status.name().length() > 0);
		}
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.DeviceCloudStatus#get(int)}.
	 *
	 * <p>Verify that each {@code DeviceCloudStatus} entry can be retrieved
	 * statically using its value.</p>
	 */
	@Test
	public void testDeviceCloudStatusStaticAccess() {
		for (DeviceCloudStatus status:dcStatuses)
			assertEquals(status, DeviceCloudStatus.get(status.getID()));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.DeviceCloudStatus#toString()}.
	 *
	 * <p>Verify that the {@code toString()} method of a {@code DeviceCloudStatus}
	 * entry returns its description correctly.</p>
	 */
	@Test
	public void testDeviceCloudStatusToString() {
		for (DeviceCloudStatus status:dcStatuses)
			assertEquals(status.getName(), status.toString());
	}
}