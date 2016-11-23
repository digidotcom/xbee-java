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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

public class IP32BitAddressTest {

	// Constants.
	private final static byte[] INVALID_BYTE_ARRAY = new byte[]{0x01, 0x17, 0x7B, (byte)0xEA, 0x4B};
	private final static int[] INVALID_INT_ARRAY = new int[]{1, 17, 123, 234, 345};
	private final static String INVALID_STRING = "Hello world";
	private final static String INVALID_STRING_2 = "1.580.123.1024";

	private final static byte[] VALID_BYTE_ARRAY = new byte[]{0x01, 0x17, 0x7B, (byte)0xEA};
	private final static int[] VALID_INT_ARRAY = new int[]{1, 23, 123, 234};
	private final static String VALID_STRING = "1.23.123.234";

	private final static byte[] INCOMPLETE_BYTE_ARRAY = new byte[]{0x00, 0x00, 0x7B, (byte)0xEA};
	private final static int[] INCOMPLETE_INT_ARRAY = new int[]{123, 234};

	private final static byte[] EXPECTED_BYTE_ARRAY = new byte[]{0x01, 0x17, 0x7B, (byte)0xEA};
	private final static String EXPECTED_STRING = "1.23.123.234";

	private final static byte[] EXPECTED_INCOMPLETE_BYTE_ARRAY = new byte[]{0x00, 0x00, 0x7B, (byte)0xEA};
	private final static String EXPECTED_INCOMPLETE_STRING = "0.0.123.234";

	/**
	 * Test method for {@link com.digi.xbee.api.models.IP32BitAddress#IP32BitAddress(byte[])},
	 * {@link com.digi.xbee.api.models.IP32BitAddress#IP32BitAddress(int[])} and
	 * {@link com.digi.xbee.api.models.IP32BitAddress#IP32BitAddress(String)}.
	 *
	 * <p>Verify that IP object cannot be created using invalid parameters.</p>
	 */
	@Test
	public void testCreateWithInvalidParameters() {
		// Test with null byte array.
		try {
			new IP32BitAddress((byte[])null);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), NullPointerException.class);
		}
		// Test with invalid byte array.
		try {
			new IP32BitAddress(INVALID_BYTE_ARRAY);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
		// Test with null int array.
		try {
			new IP32BitAddress((int[])null);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), NullPointerException.class);
		}
		// Test with invalid int array.
		try {
			new IP32BitAddress(INVALID_INT_ARRAY);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
		// Test with null string.
		try {
			new IP32BitAddress((String)null);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), NullPointerException.class);
		}
		// Test with invalid strings.
		try {
			new IP32BitAddress(INVALID_STRING);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
		try {
			new IP32BitAddress(INVALID_STRING_2);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.IP32BitAddress#IP32BitAddress(byte[])}.
	 *
	 * <p>Verify that IP Address object can be created using a valid byte
	 * array and the returned object values are correct.</p>
	 */
	@Test
	public void testCreateWithValidByteArray() {
		// Test with valid byte array.
		IP32BitAddress address = null;
		try {
			address = new IP32BitAddress(VALID_BYTE_ARRAY);
		} catch (Exception e) {
			fail("This exception should have not been thrown.");
		}

		assertArrayEquals(EXPECTED_BYTE_ARRAY, address.getValue());
		assertEquals(EXPECTED_STRING, address.getValueString());
		assertEquals(EXPECTED_STRING, address.toString());
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.IP32BitAddress#IP32BitAddress(int[])}.
	 *
	 * <p>Verify that IP Address object can be created using a valid int
	 * array and the returned object values are correct.</p>
	 */
	@Test
	public void testCreateWithValidIntArray() {
		// Test with valid int array.
		IP32BitAddress address = null;
		try {
			address = new IP32BitAddress(VALID_INT_ARRAY);
		} catch (Exception e) {
			fail("This exception should have not been thrown.");
		}

		assertArrayEquals(EXPECTED_BYTE_ARRAY, address.getValue());
		assertEquals(EXPECTED_STRING, address.getValueString());
		assertEquals(EXPECTED_STRING, address.toString());
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.IP32BitAddress#IP32BitAddress(String)}.
	 *
	 * <p>Verify that IP Address object can be created using a valid string
	 * and the returned object values are correct.</p>
	 */
	@Test
	public void testCreateWithValidString() {
		// Test with valid string.
		IP32BitAddress address = null;
		try {
			address = new IP32BitAddress(VALID_STRING);
		} catch (Exception e) {
			fail("This exception should have not been thrown.");
		}

		assertArrayEquals(EXPECTED_BYTE_ARRAY, address.getValue());
		assertEquals(EXPECTED_STRING, address.getValueString());
		assertEquals(EXPECTED_STRING, address.toString());
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.IP32BitAddress#IP32BitAddress(byte[])}.
	 *
	 * <p>Verify that IP Address object can be created using an incomplete byte
	 * array and the returned object values are correct.</p>
	 */
	@Test
	public void testCreateWithIncompleteByteArray() {
		// Test with an incomplete byte array.
		IP32BitAddress address = null;
		try {
			address = new IP32BitAddress(INCOMPLETE_BYTE_ARRAY);
		} catch (Exception e) {
			fail("This exception should have not been thrown.");
		}

		assertArrayEquals(EXPECTED_INCOMPLETE_BYTE_ARRAY, address.getValue());
		assertEquals(EXPECTED_INCOMPLETE_STRING, address.getValueString());
		assertEquals(EXPECTED_INCOMPLETE_STRING, address.toString());
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.IP32BitAddress#IP32BitAddress(int[])}.
	 *
	 * <p>Verify that IP Address object can be created using an incomplete int
	 * array and the returned object values are correct.</p>
	 */
	@Test
	public void testCreateWithIncompleteIntArray() {
		// Test with an incomplete byte array.
		IP32BitAddress address = null;
		try {
			address = new IP32BitAddress(INCOMPLETE_INT_ARRAY);
		} catch (Exception e) {
			fail("This exception should have not been thrown.");
		}

		assertArrayEquals(EXPECTED_INCOMPLETE_BYTE_ARRAY, address.getValue());
		assertEquals(EXPECTED_INCOMPLETE_STRING, address.getValueString());
		assertEquals(EXPECTED_INCOMPLETE_STRING, address.toString());
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.IP32BitAddress#equals(Object)}.
	 *
	 * <p>Test the equals method with a {@code null} value.</p>
	 */
	@Test
	public final void testEqualsWithNull() {
		// Setup the resources for the test.
		IP32BitAddress ip = new IP32BitAddress("10.101.2.100");

		// Call the method under test.
		boolean areEqual = ip.equals(null);

		// Verify the result.
		assertThat("IP address cannot be equal to null", areEqual, is(equalTo(false)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.IP32BitAddress#equals(Object)}.
	 *
	 * <p>Test the equals method with a non {@code IP32BitAddress} value.</p>
	 */
	@Test
	public final void testEqualsWithNonIP() {
		// Setup the resources for the test.
		IP32BitAddress ip = new IP32BitAddress("10.101.2.100");

		// Call the method under test.
		boolean areEqual = ip.equals(new Object());

		// Verify the result.
		assertThat("IP address cannot be equal to an Object", areEqual, is(equalTo(false)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.IP32BitAddress(Object)}.
	 *
	 * <p>Test the equals method with different {@code IP32BitAddress}.</p>
	 */
	@Test
	public final void testEqualsWithDifferentIP() {
		// Setup the resources for the test.
		IP32BitAddress ip1 = new IP32BitAddress("10.101.2.100");
		IP32BitAddress ip2 = new IP32BitAddress("192.163.1.100");

		// Call the method under test.
		boolean areEqual1 = ip1.equals(ip2);
		boolean areEqual2 = ip2.equals(ip1);

		// Verify the result.
		assertThat("IP ip1 must be different from IP ip2", areEqual1, is(equalTo(false)));
		assertThat("IP ip2 must be different from IP ip1", areEqual2, is(equalTo(false)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.IP32BitAddress#equals(Object)}.
	 *
	 * <p>Test the equals method with equal {@code IP32BitAddress}.</p>
	 */
	@Test
	public final void testEqualsIsSymetric() {
		// Setup the resources for the test.
		IP32BitAddress ip1 = new IP32BitAddress("10.101.2.100");
		IP32BitAddress ip2 = new IP32BitAddress("10.101.2.100");

		// Call the method under test.
		boolean areEqual1 = ip1.equals(ip2);
		boolean areEqual2 = ip2.equals(ip1);

		// Verify the result.
		assertThat("IP ip1 must be equal to IP ip2", areEqual1, is(equalTo(true)));
		assertThat("IP ip2 must be equal to IP ip1", areEqual2, is(equalTo(true)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.IP32BitAddress#equals(Object)}.
	 */
	@Test
	public final void testEqualsIsReflexive() {
		// Setup the resources for the test.
		IP32BitAddress ip = new IP32BitAddress("10.101.2.100");

		// Call the method under test.
		boolean areEqual = ip.equals(ip);

		// Verify the result.
		assertThat("IP ip must be equal to itself", areEqual, is(equalTo(true)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.IP32BitAddress#equals(Object)}.
	 */
	@Test
	public final void testEqualsIsTransitive() {
		// Setup the resources for the test.
		IP32BitAddress ip1 = new IP32BitAddress("10.101.2.100");
		IP32BitAddress ip2 = new IP32BitAddress("10.101.2.100");
		IP32BitAddress ip3 = new IP32BitAddress("10.101.2.100");

		// Call the method under test.
		boolean areEqual1 = ip1.equals(ip2);
		boolean areEqual2 = ip2.equals(ip3);
		boolean areEqual3 = ip1.equals(ip3);

		// Verify the result.
		assertThat("IP ip1 must be equal to IP ip2", areEqual1, is(equalTo(true)));
		assertThat("IP ip2 must be equal to IP ip3", areEqual2, is(equalTo(true)));
		assertThat("IP ip1 must be equal to IP ip3", areEqual3, is(equalTo(true)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.IP32BitAddress#equals(Object)}.
	 */
	@Test
	public final void testEqualsIsConsistent() {
		// Setup the resources for the test.
		IP32BitAddress ip1 = new IP32BitAddress("10.101.2.100");
		IP32BitAddress ip2 = new IP32BitAddress("10.101.2.100");
		IP32BitAddress ip3 = new IP32BitAddress("192.163.1.100");

		// Verify the result.
		assertThat("Consistent test fail ip1,ip2", ip1.equals(ip2), is(equalTo(true)));
		assertThat("Consistent test fail ip1,ip2", ip1.equals(ip2), is(equalTo(true)));
		assertThat("Consistent test fail ip1,ip2", ip1.equals(ip2), is(equalTo(true)));
		assertThat("Consistent test fail ip3,ip1", ip3.equals(ip1), is(equalTo(false)));
		assertThat("Consistent test fail ip3,ip1", ip3.equals(ip1), is(equalTo(false)));
		assertThat("Consistent test fail ip3,ip1", ip3.equals(ip1), is(equalTo(false)));

	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.IP32BitAddress#hashCode()}.
	 */
	@Test
	public final void testHashCodeWithEqualPackets() {
		// Setup the resources for the test.
		IP32BitAddress ip1 = new IP32BitAddress("10.101.2.100");
		IP32BitAddress ip2 = new IP32BitAddress("10.101.2.100");

		// Call the method under test.
		int hashIP1 = ip1.hashCode();
		int hashIP2 = ip2.hashCode();

		// Verify the result.
		assertThat("IP ip1 must be equal to IP ip2", ip1.equals(ip2), is(equalTo(true)));
		assertThat("IP ip2 must be equal to IP ip1", ip2.equals(ip1), is(equalTo(true)));
		assertThat("Hash codes must be equal", hashIP1, is(equalTo(hashIP2)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.IP32BitAddress#hashCode()}.
	 */
	@Test
	public final void testHashCodeWithDifferentPackets() {
		// Setup the resources for the test.
		IP32BitAddress ip1 = new IP32BitAddress("10.101.2.100");
		IP32BitAddress ip2 = new IP32BitAddress("192.163.1.100");

		// Call the method under test.
		int hashIP1 = ip1.hashCode();
		int hashIP2 = ip2.hashCode();

		// Verify the result.
		assertThat("IP ip1 must be different from IP ip2", ip1.equals(ip2), is(equalTo(false)));
		assertThat("IP ip2 must be different from to IP ip1", ip2.equals(ip1), is(equalTo(false)));
		assertThat("Hash codes must be different", hashIP1, is(not(equalTo(hashIP2))));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.IP32BitAddress#hashCode()}.
	 */
	@Test
	public final void testHashCodeIsConsistent() {
		// Setup the resources for the test.
		IP32BitAddress ip = new IP32BitAddress("10.101.2.100");

		int initialHashCode = ip.hashCode();

		// Verify the result.
		assertThat("Consistent hashcode test fails", ip.hashCode(), is(equalTo(initialHashCode)));
		assertThat("Consistent hashcode test fails", ip.hashCode(), is(equalTo(initialHashCode)));
	}
}
