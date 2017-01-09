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
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

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
		try {
			new XBee64BitAddress(-1, 7, 6, 5, 4, 3, 2, 1);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
		try {
			new XBee64BitAddress(7, -1, 6, 5, 4, 3, 2, 1);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
		try {
			new XBee64BitAddress(7, 6, -1, 5, 4, 3, 2, 1);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
		try {
			new XBee64BitAddress(7, 6, 5, -1, 4, 3, 2, 1);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
		try {
			new XBee64BitAddress(7, 6, 5, 4, -1, 3, 2, 1);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
		try {
			new XBee64BitAddress(7, 6, 5, 4, 3, -1, 2, 1);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
		try {
			new XBee64BitAddress(7, 6, 5, 4, 3, 2, -1, 1);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
		try {
			new XBee64BitAddress(7, 6, 5, 4, 3, 2, 1, -1);
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
		try {
			new XBee64BitAddress(256, 7, 6, 5, 4, 3, 2, 1);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
		try {
			new XBee64BitAddress(7, 256, 6, 5, 4, 3, 2, 1);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
		try {
			new XBee64BitAddress(7, 6, 256, 5, 4, 3, 2, 1);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
		try {
			new XBee64BitAddress(7, 6, 5, 256, 4, 3, 2, 1);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
		try {
			new XBee64BitAddress(7, 6, 5, 4, 256, 3, 2, 1);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
		try {
			new XBee64BitAddress(7, 6, 5, 4, 3, 256, 2, 1);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
		try {
			new XBee64BitAddress(7, 6, 5, 4, 3, 2, 256, 1);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
		try {
			new XBee64BitAddress(7, 6, 5, 4, 3, 2, 1, 256);
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
		// Test with empty byte array.
		try {
			new XBee64BitAddress(new byte[0]);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
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
		// Test with empty string.
		try {
			new XBee64BitAddress("");
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
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
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBee64BitAddress#equals(Object)}.
	 * 
	 * <p>Test the equals method with a {@code null} value.</p>
	 */
	@Test
	public final void testEqualsWithNull() {
		// Setup the resources for the test.
		XBee64BitAddress addr = new XBee64BitAddress("0x0013A20040123456");
		
		// Call the method under test.
		boolean areEqual = addr.equals(null);
		
		// Verify the result.
		assertThat("64-bit address cannot be equal to null", areEqual, is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBee64BitAddress#equals(Object)}.
	 * 
	 * <p>Test the equals method with a non {@code XBee64BitAddress} value.</p>
	 */
	@Test
	public final void testEqualsWithNonXBeePacket() {
		// Setup the resources for the test.
		XBee64BitAddress addr = new XBee64BitAddress("0x0013A20040123456");
		
		// Call the method under test.
		boolean areEqual = addr.equals(new Object());
		
		// Verify the result.
		assertThat("64-bit address cannot be equal to an Object", areEqual, is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.models.XBee64BitAddress(Object)}.
	 * 
	 * <p>Test the equals method with different {@code XBee64BitAddress}.</p>
	 */
	@Test
	public final void testEqualsWithDifferentXBeePacket() {
		// Setup the resources for the test.
		XBee64BitAddress addr1 = new XBee64BitAddress("0x0013A20040123456");
		XBee64BitAddress addr2 = new XBee64BitAddress("0x0013A20040789012");
		
		// Call the method under test.
		boolean areEqual1 = addr1.equals(addr2);
		boolean areEqual2 = addr2.equals(addr1);
		
		// Verify the result.
		assertThat("64-bit addr1 must be different from 64-bit addr2", areEqual1, is(equalTo(false)));
		assertThat("64-bit addr2 must be different from 64-bit addr1", areEqual2, is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBee64BitAddress#equals(Object)}.
	 * 
	 * <p>Test the equals method with equal {@code XBee64BitAddress}.</p>
	 */
	@Test
	public final void testEqualsIsSymetric() {
		// Setup the resources for the test.
		XBee64BitAddress addr1 = new XBee64BitAddress("0x0013A20040123456");
		XBee64BitAddress addr2 = new XBee64BitAddress("0x0013A20040123456");
		
		// Call the method under test.
		boolean areEqual1 = addr1.equals(addr2);
		boolean areEqual2 = addr2.equals(addr1);
		
		// Verify the result.
		assertThat("64-bit addr1 must be equal to 64-bit addr2", areEqual1, is(equalTo(true)));
		assertThat("64-bit addr2 must be equal to 64-bit addr1", areEqual2, is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBee64BitAddress#equals(Object)}.
	 */
	@Test
	public final void testEqualsIsReflexive() {
		// Setup the resources for the test.
		XBee64BitAddress addr = new XBee64BitAddress("0x0013A20040123456");
		
		// Call the method under test.
		boolean areEqual = addr.equals(addr);
		
		// Verify the result.
		assertThat("64-bits addr must be equal to itself", areEqual, is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBee64BitAddress#equals(Object)}.
	 */
	@Test
	public final void testEqualsIsTransitive() {
		// Setup the resources for the test.
		XBee64BitAddress addr1 = new XBee64BitAddress("0x0013A20040123456");
		XBee64BitAddress addr2 = new XBee64BitAddress("0x0013A20040123456");
		XBee64BitAddress addr3 = new XBee64BitAddress("0x0013A20040123456");
		
		// Call the method under test.
		boolean areEqual1 = addr1.equals(addr2);
		boolean areEqual2 = addr2.equals(addr3);
		boolean areEqual3 = addr1.equals(addr3);
		
		// Verify the result.
		assertThat("64-bits addr1 must be equal to 64-bits addr2", areEqual1, is(equalTo(true)));
		assertThat("64-bits addr2 must be equal to 64-bits addr3", areEqual2, is(equalTo(true)));
		assertThat("64-bits addr1 must be equal to 64-bits addr3", areEqual3, is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBee64BitAddress#equals(Object)}.
	 */
	@Test
	public final void testEqualsIsConsistent() {
		// Setup the resources for the test.
		XBee64BitAddress addr1 = new XBee64BitAddress("0x0013A20040123456");
		XBee64BitAddress addr2 = new XBee64BitAddress("0x0013A20040123456");
		XBee64BitAddress addr3 = new XBee64BitAddress("0x0013A20040789012");
		
		// Verify the result.
		assertThat("Consistent test fail addr1,addr2", addr1.equals(addr2), is(equalTo(true)));
		assertThat("Consistent test fail addr1,addr2", addr1.equals(addr2), is(equalTo(true)));
		assertThat("Consistent test fail addr1,addr2", addr1.equals(addr2), is(equalTo(true)));
		assertThat("Consistent test fail addr3,addr1", addr3.equals(addr1), is(equalTo(false)));
		assertThat("Consistent test fail addr3,addr1", addr3.equals(addr1), is(equalTo(false)));
		assertThat("Consistent test fail addr3,addr1", addr3.equals(addr1), is(equalTo(false)));

	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBee64BitAddress#hashCode()}.
	 */
	@Test
	public final void testHashCodeWithEqualPackets() {
		// Setup the resources for the test.
		XBee64BitAddress addr1 = new XBee64BitAddress("0x0013A20040123456");
		XBee64BitAddress addr2 = new XBee64BitAddress("0x0013A20040123456");
		
		// Call the method under test.
		int hashAddr1 = addr1.hashCode();
		int hashAddr2 = addr2.hashCode();
		
		// Verify the result.
		assertThat("64-bit addr1 must be equal to 64-bit addr2", addr1.equals(addr2), is(equalTo(true)));
		assertThat("64-bit addr2 must be equal to 64-bit addr1", addr2.equals(addr1), is(equalTo(true)));
		assertThat("Hash codes must be equal", hashAddr1, is(equalTo(hashAddr2)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBee64BitAddress#hashCode()}.
	 */
	@Test
	public final void testHashCodeWithDifferentPackets() {
		// Setup the resources for the test.
		XBee64BitAddress addr1 = new XBee64BitAddress("0x0013A20040123456");
		XBee64BitAddress addr2 = new XBee64BitAddress("0x0013A20040789012");
		
		// Call the method under test.
		int hashAddr1 = addr1.hashCode();
		int hashAddr2 = addr2.hashCode();
		
		// Verify the result.
		assertThat("64-bit addr1 must be different from 64-bit addr2", addr1.equals(addr2), is(equalTo(false)));
		assertThat("64-bit addr2 must be different from to 64-bit addr1", addr2.equals(addr1), is(equalTo(false)));
		assertThat("Hash codes must be different", hashAddr1, is(not(equalTo(hashAddr2))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBee64BitAddress#hashCode()}.
	 */
	@Test
	public final void testHashCodeIsConsistent() {
		// Setup the resources for the test.
		XBee64BitAddress addr = new XBee64BitAddress("0x0013A20040123456");
		
		int initialHashCode = addr.hashCode();
		
		// Verify the result.
		assertThat("Consistent hashcode test fails", addr.hashCode(), is(equalTo(initialHashCode)));
		assertThat("Consistent hashcode test fails", addr.hashCode(), is(equalTo(initialHashCode)));
	}
}
