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

public class XBee64BitAddressTest {
	
	// Constants.
	private final static byte[] INVALID_BYTE_ARRAY = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09};
	private final static String INVALID_STRING = "Hello";
	
	private final static String VALID_STRING = "0x0013A20040A9E7ED";
	private final static String VALID_STRING_NO_PREFIX = "0013A20040A9E7ED";
	private final static byte[] VALID_BYTE_ARRAY = new byte[]{0x00, 0x13, (byte)0xA2, 0x00, 0x40, (byte)0xA9, (byte)0xE7, (byte)0xED};
	
	private final static String INCOMPLETE_STRING = "0x13A20040A9E7ED";
	private final static String INCOMPLETE_EXPECTED_STRING = VALID_STRING_NO_PREFIX;
	private final static byte[] INCOMPLETE_BYTE_ARRAY = new byte[]{0x13, (byte)0xA2, 0x00, 0x40, (byte)0xA9, (byte)0xE7, (byte)0xED};
	private final static byte[] INCOMPLETE_EXPECTED_BYTE_ARRAY = VALID_BYTE_ARRAY;
	
	private final static String EXPECTED_DEVICE_ID = "00000000-00000000-A20040FF-FFA9E7ED";
	
	@Test
	/**
	 * Verify that XBee 64 Bit Address object cannot be created using invalid parameters.
	 */
	public void testCreateWithInvalidParameters() {
		// Test with invalid byte number values (lesser than 0).
		try {
			new XBee64BitAddress(-1, -1, -1, -1, -1, -1, -1, -1);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
		// Test with invalid byte number values (greater than 255).
		try {
			new XBee64BitAddress(256, 256, 256, 256, 256, 256, 256, 256);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
		// Test with null byte array.
		try {
			new XBee64BitAddress((byte[])null);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), NullPointerException.class);
		}
		// Test with invalid byte array.
		try {
			new XBee64BitAddress(INVALID_BYTE_ARRAY);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
		// Test with null string.
		try {
			new XBee64BitAddress((String)null);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), NullPointerException.class);
		}
		// Test with invalid string.
		try {
			new XBee64BitAddress(INVALID_STRING);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
	}
	
	@Test
	/**
	 * Verify that XBee 64 Bit Address object can be created using valid byte
	 * values and the returned object values are correct.
	 */
	public void testCreateWithValidByteNumbers() {
		// Test with valid byte number values.
		XBee64BitAddress address = null;
		try {
			address = new XBee64BitAddress(VALID_BYTE_ARRAY[0] & 0xFF, VALID_BYTE_ARRAY[1] & 0xFF, VALID_BYTE_ARRAY[2] & 0xFF, VALID_BYTE_ARRAY[3] & 0xFF,
					(int)VALID_BYTE_ARRAY[4] & 0xFF, VALID_BYTE_ARRAY[5] & 0xFF, VALID_BYTE_ARRAY[6] & 0xFF, VALID_BYTE_ARRAY[7] & 0xFF);
		} catch (Exception e) {
			fail("This exception should have not been thrown.");
		}
		
		assertArrayEquals(VALID_BYTE_ARRAY, address.getValue());
		assertEquals(VALID_STRING_NO_PREFIX, address.toString());
		assertEquals(EXPECTED_DEVICE_ID, address.generateDeviceID());
	}
	
	@Test
	/**
	 * Verify that XBee 64 Bit Address object can be created using a valid
	 * byte array and the returned object values are correct.
	 */
	public void testCreateWithValidByteArray() {
		// Test with valid byte array.
		XBee64BitAddress address = null;
		try {
			address = new XBee64BitAddress(VALID_BYTE_ARRAY);
		} catch (Exception e) {
			fail("This exception should have not been thrown.");
		}
		
		assertArrayEquals(VALID_BYTE_ARRAY, address.getValue());
		assertEquals(VALID_STRING_NO_PREFIX, address.toString());
		assertEquals(EXPECTED_DEVICE_ID, address.generateDeviceID());
	}
	
	@Test
	/**
	 * Verify that XBee 64 Bit Address object can be created using a valid
	 * string and the returned object values are correct.
	 */
	public void testCreateWithValidString() {
		// Test with valid string.
		XBee64BitAddress address = null;
		try {
			address = new XBee64BitAddress(VALID_STRING);
		} catch (Exception e) {
			fail("This exception should have not been thrown.");
		}
		
		assertArrayEquals(VALID_BYTE_ARRAY, address.getValue());
		assertEquals(VALID_STRING_NO_PREFIX, address.toString());
		assertEquals(EXPECTED_DEVICE_ID, address.generateDeviceID());
	}
	
	@Test
	/**
	 * Verify that XBee 64 Bit Address object can be created using a valid
	 * string without '0x' prefix and the returned object values are correct.
	 */
	public void testCreateWithValidStringNoPrefix() {
		// Test with valid string without prefix.
		XBee64BitAddress address = null;
		try {
			address = new XBee64BitAddress(VALID_STRING_NO_PREFIX);
		} catch (Exception e) {
			fail("This exception should have not been thrown.");
		}
		
		assertArrayEquals(VALID_BYTE_ARRAY, address.getValue());
		assertEquals(VALID_STRING_NO_PREFIX, address.toString());
		assertEquals(EXPECTED_DEVICE_ID, address.generateDeviceID());
	}
	
	@Test
	/**
	 * Verify that XBee 64 Bit Address object can be created using an incomplete
	 * byte array and the returned object values are correct.
	 */
	public void testCreateWithIncompleteArray() {
		// Test with an incomplete byte array.
		XBee64BitAddress address = null;
		try {
			address = new XBee64BitAddress(INCOMPLETE_BYTE_ARRAY);
		} catch (Exception e) {
			fail("This exception should have not been thrown.");
		}
		
		assertArrayEquals(INCOMPLETE_EXPECTED_BYTE_ARRAY, address.getValue());
		assertEquals(INCOMPLETE_EXPECTED_STRING, address.toString());
		assertEquals(EXPECTED_DEVICE_ID, address.generateDeviceID());
	}
	
	@Test
	/**
	 * Verify that XBee 64 Bit Address object can be created using an incomplete
	 * string and the returned object values are correct.
	 */
	public void testCreateWithIncompleteString() {
		// Test with an incomplete string.
		XBee64BitAddress address = null;
		try {
			address = new XBee64BitAddress(INCOMPLETE_STRING);
		} catch (Exception e) {
			fail("This exception should have not been thrown.");
		}
		
		assertArrayEquals(INCOMPLETE_EXPECTED_BYTE_ARRAY, address.getValue());
		assertEquals(INCOMPLETE_EXPECTED_STRING, address.toString());
		assertEquals(EXPECTED_DEVICE_ID, address.generateDeviceID());
	}
}
