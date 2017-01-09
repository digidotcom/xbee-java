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

public class HardwareVersionEnumTest {

	// Variables.
	private HardwareVersionEnum[] hardwareVersionValues;
	
	
	@Before
	public void setup() {
		// Retrieve the list of enum. values.
		hardwareVersionValues = HardwareVersionEnum.values();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.HardwareVersionEnum#getValue()}.
	 * 
	 * <p>Verify that the value of each HardwareVersionEnum entry is valid.</p>
	 */
	@Test
	public void testHardwareVersionEnumValues() {
		for (HardwareVersionEnum hardwareVersionEnum:hardwareVersionValues)
			assertTrue(hardwareVersionEnum.getValue() >= 0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.HardwareVersionEnum#name()}.
	 * 
	 * <p>Verify that the name of each HardwareVersionEnum entry is valid.</p>
	 */
	@Test
	public void testHardwareVersionEnumNames() {
		for (HardwareVersionEnum hardwareVersionEnum:hardwareVersionValues) {
			assertNotNull(hardwareVersionEnum.name());
			assertTrue(hardwareVersionEnum.name().length() > 0);
		}
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.HardwareVersionEnum#getDescription()}.
	 * 
	 * <p>Verify that the description of each HardwareVersionEnum entry is valid.</p>
	 */
	@Test
	public void testHardwareVersionEnumDescriptions() {
		for (HardwareVersionEnum hardwareVersionEnum:hardwareVersionValues) {
			assertNotNull(hardwareVersionEnum.getDescription());
			assertTrue(hardwareVersionEnum.getDescription().length() > 0);
		}
	}
	
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.HardwareVersionEnum#get(int)}.
	 * 
	 * <p>Verify that each HardwareVersionEnum entry can be retrieved statically using its ID.</p>
	 */
	@Test
	public void testHardwareVersionEnumStaticAccess() {
		for (HardwareVersionEnum hardwareVersionEnum:hardwareVersionValues)
			assertEquals(hardwareVersionEnum, HardwareVersionEnum.get(hardwareVersionEnum.getValue()));
	}
}
