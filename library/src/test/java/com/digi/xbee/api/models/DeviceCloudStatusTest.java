/**
 * Copyright 2017, Digi International Inc.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES 
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR 
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES 
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN 
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF 
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
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