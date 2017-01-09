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
package com.digi.xbee.api.io;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class IOModeTest {

	// Constants.
	private static final int INVALID_ID = -1;
	
	// Variables.
	private IOMode[] ioModes;
	
	@Before
	public void setup() {
		// Retrieve the list of enum. values.
		ioModes = IOMode.values();
	}
	
	/**
	 * Verify that the name of each IO mode entry is valid.
	 */
	@Test
	public void testIOModeNames() {
		for (IOMode ioMode:ioModes) {
			assertNotNull(ioMode.getName());
			assertTrue(ioMode.getName().length() > 0);
		}
	}
	
	/**
	 * Verify that each IO mode enum. value can be retrieved statically 
	 * using its ID.
	 */
	@Test
	public void testIOModeStaticAccess() {
		for (IOMode ioMode:ioModes) {
			if (ioMode != IOMode.PWM)
				assertEquals(ioMode, IOMode.getIOMode(ioMode.getID()));
			else {
				assertEquals(IOMode.ADC, IOMode.getIOMode(ioMode.getID()));
				for (IOLine ioLine:IOLine.values()) {
					if (ioLine.hasPWMCapability())
						assertEquals(IOMode.PWM, IOMode.getIOMode(ioMode.getID(), ioLine));
					else
						assertEquals(IOMode.ADC, IOMode.getIOMode(ioMode.getID(), ioLine));
				}
			}
		}
	}
	
	/**
	 * Verify that when trying to get an IO mode not contained in the enumeration, 
	 * a null value is retrieved.
	 */
	@Test
	public void testNullIOModeIsRetrievedWithInvalidID() {
		assertNull(IOMode.getIOMode(INVALID_ID));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.io.IOMode#toString()}.
	 */
	@Test
	public void testToString() {
		for (IOMode ioMode:ioModes)
			assertEquals("toString() method does not produce the expected output",
					ioMode.getName(), ioMode.toString());
	}
}
