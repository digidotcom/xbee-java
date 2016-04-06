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
package com.digi.xbee.api.packet;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class XBeeChecksumTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	 * Test method for {@link com.digi.xbee.api.packet.XBeeChecksum#add(int)}.
	 */
	@Test
	public final void testAddIntOnce() {
		// Setup the resources for the test.
		XBeeChecksum c = new XBeeChecksum();
		int expectedResult = 253;
		
		// Call the method under test.
		c.add(2);
		
		// Verify the result.
		assertThat("Returned result is not the expected one", c.generate(), is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeChecksum#add(int)}.
	 */
	@Test
	public final void testAddIntTwice() {
		// Setup the resources for the test.
		XBeeChecksum c = new XBeeChecksum();
		int expectedResult = 248;
		
		// Call the method under test.
		c.add(2);
		c.add(5);
		
		// Verify the result.
		assertThat("Returned result is not the expected one", c.generate(), is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeChecksum#add(byte[])}.
	 */
	@Test
	public final void testAddNullArray() {
		// Setup the resources for the test.
		XBeeChecksum c = new XBeeChecksum();
		byte[] array = null;
		int added = 5;
		int expectedResult = 255 - added;
		c.add(added);
		
		// Call the method under test.
		c.add(array);
		
		// Verify the result.
		assertThat("Returned result is not the expected one", c.generate(), is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeChecksum#add(byte[])}.
	 */
	@Test
	public final void testAddArrayOnce() {
		// Setup the resources for the test.
		XBeeChecksum c = new XBeeChecksum();
		byte[] array = new byte[] {2, 6, 1};
		int expectedResult = 246;
		
		// Call the method under test.
		c.add(array);
		
		// Verify the result.
		assertThat("Returned result is not the expected one", c.generate(), is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeChecksum#add(byte[])}.
	 */
	@Test
	public final void testAddArrayTwice() {
		// Setup the resources for the test.
		XBeeChecksum c = new XBeeChecksum();
		byte[] array1 = new byte[] {2, 6, 1};
		byte[] array2 = new byte[] {5, 3};
		int expectedResult = 238;
		
		// Call the method under test.
		c.add(array1);
		c.add(array2);
		
		// Verify the result.
		assertThat("Returned result is not the expected one", c.generate(), is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeChecksum#reset()}.
	 */
	@Test
	public final void testReset() {
		// Setup the resources for the test.
		byte[] array = new byte[] {2, 6, 1};
		int expectedResult = 0xFF;
		XBeeChecksum c = new XBeeChecksum();
		c.add(array);
		
		// Call the method under test.
		c.reset();
		
		// Verify the result.
		assertThat("Returned result is not the expected one", c.generate(), is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeChecksum#validate()}.
	 */
	@Test
	public final void testValidateNotValid() {
		// Setup the resources for the test.
		byte[] array = new byte[] {2, 6, 1};
		XBeeChecksum c = new XBeeChecksum();
		c.add(array);
		
		// Call the method under test.
		boolean isValid = c.validate();
		
		// Verify the result.
		assertThat("Returned result is not the expected one", isValid, is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeChecksum#validate()}.
	 */
	@Test
	public final void testValidateValid() {
		// Setup the resources for the test.
		byte[] array = new byte[] {2, 6, 1};
		XBeeChecksum c = new XBeeChecksum();
		c.add(array);
		int generatedChecksum = c.generate();
		c.add(generatedChecksum);
		
		// Call the method under test.
		boolean isValid = c.validate();
		
		// Verify the result.
		assertThat("Returned result is not the expected one", isValid, is(equalTo(true)));
	}
}
