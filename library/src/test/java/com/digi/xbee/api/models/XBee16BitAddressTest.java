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

public class XBee16BitAddressTest {
	
	// Constants.
	private final static byte[] INVALID_BYTE_ARRAY = new byte[]{0x01, 0x02, 0x03};
	private final static String INVALID_STRING = "Hello";
	
	private final static String VALID_STRING = "0x2142";
	private final static String VALID_STRING_NO_PREFIX = "2142";
	private final static byte[] VALID_BYTE_ARRAY = new byte[]{0x21, 0x42};
	private final static int VALID_HSB = 33;
	private final static int VALID_LSB = 66;
	
	private final static String INCOMPLETE_STRING = "0x2";
	private final static String INCOMPLETE_EXPECTED_STRING = "0002";
	private final static byte[] INCOMPLETE_BYTE_ARRAY = new byte[]{0x02};
	private final static byte[] INCOMPLETE_EXPECTED_BYTE_ARRAY = new byte[]{0x00, 0x02};
	private final static int INCOMPLETE_HSB = 0;
	private final static int INCOMPLETE_LSB = 2;
	
	@Test
	/**
	 * Verify that XBee 16 Bit Address object cannot be created using invalid parameters.
	 */
	public void testCreateWithInvalidParameters() {
		// Test with invalid HSB and LSB (lesser than 0).
		try {
			new XBee16BitAddress(-1, -1);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
		// Test with invalid HSB and LSB (greater than 255).
		try {
			new XBee16BitAddress(256, 256);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
		// Test with null byte array.
		try {
			new XBee16BitAddress((byte[])null);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), NullPointerException.class);
		}
		// Test with invalid byte array.
		try {
			new XBee16BitAddress(INVALID_BYTE_ARRAY);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
		// Test with null string.
		try {
			new XBee16BitAddress((String)null);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), NullPointerException.class);
		}
		// Test with invalid string.
		try {
			new XBee16BitAddress(INVALID_STRING);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
	}
	
	@Test
	/**
	 * Verify that XBee 16 Bit Address object can be created using valid HSB and
	 * LSB values and the returned object values are correct.
	 */
	public void testCreateWithValidHSBLSB() {
		// Test with valid HSB and LSB values.
		XBee16BitAddress address = null;
		try {
			address = new XBee16BitAddress(VALID_HSB, VALID_LSB);
		} catch (Exception e) {
			fail("This exception should have not been thrown.");
		}
		
		assertEquals(VALID_HSB, address.getHsb());
		assertEquals(VALID_LSB, address.getLsb());
		assertArrayEquals(VALID_BYTE_ARRAY, address.getValue());
		assertEquals(VALID_STRING_NO_PREFIX, address.toString());
	}
	
	@Test
	/**
	 * Verify that XBee 16 Bit Address object can be created using a valid
	 * byte array and the returned object values are correct.
	 */
	public void testCreateWithValidByteArray() {
		// Test with valid byte array.
		XBee16BitAddress address = null;
		try {
			address = new XBee16BitAddress(VALID_BYTE_ARRAY);
		} catch (Exception e) {
			fail("This exception should have not been thrown.");
		}
		
		assertEquals(VALID_HSB, address.getHsb());
		assertEquals(VALID_LSB, address.getLsb());
		assertArrayEquals(VALID_BYTE_ARRAY, address.getValue());
		assertEquals(VALID_STRING_NO_PREFIX, address.toString());
	}
	
	@Test
	/**
	 * Verify that XBee 16 Bit Address object can be created using a valid
	 * string and the returned object values are correct.
	 */
	public void testCreateWithValidString() {
		// Test with valid string.
		XBee16BitAddress address = null;
		try {
			address = new XBee16BitAddress(VALID_STRING);
		} catch (Exception e) {
			fail("This exception should have not been thrown.");
		}
		
		assertEquals(VALID_HSB, address.getHsb());
		assertEquals(VALID_LSB, address.getLsb());
		assertArrayEquals(VALID_BYTE_ARRAY, address.getValue());
		assertEquals(VALID_STRING_NO_PREFIX, address.toString());
	}
	
	@Test
	/**
	 * Verify that XBee 16 Bit Address object can be created using a valid
	 * string with '0x' prefix and the returned object values are correct.
	 */
	public void testCreateWithValidStringNoPrefix() {
		// Test with valid string without prefix.
		XBee16BitAddress address = null;
		try {
			address = new XBee16BitAddress(VALID_STRING_NO_PREFIX);
		} catch (Exception e) {
			fail("This exception should have not been thrown.");
		}
		
		assertEquals(VALID_HSB, address.getHsb());
		assertEquals(VALID_LSB, address.getLsb());
		assertArrayEquals(VALID_BYTE_ARRAY, address.getValue());
		assertEquals(VALID_STRING_NO_PREFIX, address.toString());
	}
	
	@Test
	/**
	 * Verify that XBee 16 Bit Address object can be created using an incomplete
	 * byte array and the returned object values are correct.
	 */
	public void testCreateWithIncompleteArray() {
		// Test with an incomplete byte array.
		XBee16BitAddress address = null;
		try {
			address = new XBee16BitAddress(INCOMPLETE_BYTE_ARRAY);
		} catch (Exception e) {
			fail("This exception should have not been thrown.");
		}
		
		assertEquals(INCOMPLETE_HSB, address.getHsb());
		assertEquals(INCOMPLETE_LSB, address.getLsb());
		assertArrayEquals(INCOMPLETE_EXPECTED_BYTE_ARRAY, address.getValue());
		assertEquals(INCOMPLETE_EXPECTED_STRING, address.toString());
	}
	
	@Test
	/**
	 * Verify that XBee 16 Bit Address object can be created using an incomplete
	 * string and the returned object values are correct.
	 */
	public void testCreateWithIncompleteString() {
		// Test with an incomplete string.
		XBee16BitAddress address = null;
		try {
			address = new XBee16BitAddress(INCOMPLETE_STRING);
		} catch (Exception e) {
			fail("This exception should have not been thrown.");
		}
		
		assertEquals(INCOMPLETE_HSB, address.getHsb());
		assertEquals(INCOMPLETE_LSB, address.getLsb());
		assertArrayEquals(INCOMPLETE_EXPECTED_BYTE_ARRAY, address.getValue());
		assertEquals(INCOMPLETE_EXPECTED_STRING, address.toString());
	}
}
