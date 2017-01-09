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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class XBeeTransmitStatusTest {
	
	// Variables.
	private static XBeeTransmitStatus[] transmitStatusValues;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Retrieve the list of enum. values.
		transmitStatusValues = XBeeTransmitStatus.values();
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
	 * Test method for {@link com.digi.xbee.api.models.XBeeTransmitStatus#get(int)}.
	 */
	@Test
	public void testGetWithASupportedValue() {
		// Setup the resources for the test.
		XBeeTransmitStatus expectedStatus = XBeeTransmitStatus.ADDRESS_NOT_FOUND;
		int id = expectedStatus.getId();
		
		// Call the method under test.
		XBeeTransmitStatus status = XBeeTransmitStatus.get(id);
		
		// Verify the result.
		assertThat("The identifier of both status does not have the same value", status.getId(), is(equalTo(id)));
		assertThat("Transmit status is not the expected", status, is(equalTo(expectedStatus)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeTransmitStatus#get(int)}.
	 */
	@Test
	public void testGetWithANonSupportedValue() {
		// Setup the resources for the test.
		XBeeTransmitStatus expectedStatus = XBeeTransmitStatus.UNKNOWN;
		int id = 1234567890;
		
		// Call the method under test.
		XBeeTransmitStatus status = XBeeTransmitStatus.get(id);
		
		// Verify the result.
		assertThat("The identifier of both status does not have the same value", status.getId(), is(equalTo(expectedStatus.getId())));
		assertThat("The description of both status does not have the same value", status.getDescription(), is(equalTo(expectedStatus.getDescription())));
		assertThat("Transmit status is not the expected", status, is(equalTo(expectedStatus)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeTransmitStatus#getValue()}.
	 * 
	 * <p>Verify that the value of each XBeeTransmitStatus entry is valid.</p>
	 */
	@Test
	public void testXBeeTransmitStatusValues() {
		for (XBeeTransmitStatus status: transmitStatusValues)
			assertTrue(status.getId() >= 0 || status.getId() == -1);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeTransmitStatus#name()}.
	 * 
	 * <p>Verify that the name of each XBeeTransmitStatus entry is valid.</p>
	 */
	@Test
	public void testXBeeTransmitStatusNames() {
		for (XBeeTransmitStatus status: transmitStatusValues) {
			assertNotNull(status.name());
			assertTrue(status.name().length() > 0);
		}
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeTransmitStatus#getDescription()}.
	 * 
	 * <p>Verify that the description of each XBeeTransmitStatus entry is valid.</p>
	 */
	@Test
	public void testXBeeTransmitStatusDescriptions() {
		for (XBeeTransmitStatus status: transmitStatusValues) {
			assertNotNull(status.getDescription());
			assertTrue(status.getDescription().length() > 0);
		}
	}
	
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeTransmitStatus#get(int)}.
	 * 
	 * <p>Verify that each XBeeTransmitStatus entry can be retrieved statically using its ID.</p>
	 */
	@Test
	public void testXBeeTransmitStatusStaticAccess() {
		for (XBeeTransmitStatus status: transmitStatusValues) {
			// Do not check deprecated entry.
			if (status == XBeeTransmitStatus.UDP_SRC_PORT_NOT_MATCH_LISTENING_PORT)
				continue;
			assertEquals(status, XBeeTransmitStatus.get(status.getId()));
		}
	}
}
