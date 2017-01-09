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

import com.digi.xbee.api.utils.HexUtils;

public class APIOutputModeTest {

	// Variables.
	private APIOutputMode[] apiOutputModeValues;
	
	@Before
	public void setup() {
		// Retrieve the list of enum. values.
		apiOutputModeValues = APIOutputMode.values();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.APIOutputMode#getValue()}.
	 * 
	 * <p>Verify that the ID of each {@code APIOutputMode} entry is valid.</p>
	 */
	@Test
	public void testAPIOutputModeEnumValues() {
		for (APIOutputMode apiOutputMode:apiOutputModeValues)
			assertTrue(apiOutputMode.getValue() >= 0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.APIOutputMode#name()}.
	 * 
	 * <p>Verify that the name of each {@code APIOutputMode} entry is valid.</p>
	 */
	@Test
	public void testAPIOutputModeEnumNames() {
		for (APIOutputMode apiOutputMode:apiOutputModeValues) {
			assertNotNull(apiOutputMode.name());
			assertTrue(apiOutputMode.name().length() > 0);
		}
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.APIOutputMode#getDescription()}.
	 * 
	 * <p>Verify that the description of each {@code APIOutputMode} entry is valid.</p>
	 */
	@Test
	public void testAPIOutputModeEnumDescriptions() {
		for (APIOutputMode apiOutputMode:apiOutputModeValues) {
			assertNotNull(apiOutputMode.getDescription());
			assertTrue(apiOutputMode.getDescription().length() > 0);
		}
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.APIOutputMode#get(int)}.
	 * 
	 * <p>Verify that each {@code APIOutputMode} entry can be retrieved statically using its 
	 * value.</p>
	 */
	@Test
	public void testAPIOutputModeStaticAccess() {
		for (APIOutputMode apiOutputMode:apiOutputModeValues)
			assertEquals(apiOutputMode, APIOutputMode.get(apiOutputMode.getValue()));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.APIOutputMode#get(int)}.
	 * 
	 * <p>Verify that when trying to get an invalid {@code APIOutputMode} entry, 
	 * {@code null} is retrieved.</p>
	 */
	@Test
	public void testAPIOutputModeStaticInvalidAccess() {
		assertNull(APIOutputMode.get(10));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.APIOutputMode#toString()}.
	 * 
	 * <p>Verify that the {@code toString()} method of a {@code APIOutputMode} entry returns 
	 * its description correctly.</p>
	 */
	@Test
	public void testAPIOutputModeToString() {
		for (APIOutputMode apiOutputMode:apiOutputModeValues)
			assertEquals(HexUtils.byteToHexString((byte)apiOutputMode.getValue()) + ": " + apiOutputMode.getDescription(), apiOutputMode.toString());
	}
}