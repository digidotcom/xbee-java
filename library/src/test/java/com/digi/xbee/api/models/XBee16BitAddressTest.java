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
		// Test with invalid HSB (lesser than 0).
		try {
			new XBee16BitAddress(-1, 5);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
		// Test with invalid LSB (lesser than 0).
		try {
			new XBee16BitAddress(5, -1);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
		// Test with invalid HSB (greater than 255).
		try {
			new XBee16BitAddress(256, 5);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
		// Test with invalid LSB (greater than 255).
		try {
			new XBee16BitAddress(5, 256);
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
		// Test with empty byte array.
		try {
			new XBee16BitAddress(new byte[0]);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
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
		// Test with empty string.
		try {
			new XBee16BitAddress("");
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
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
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBee16BitAddress#equals(Object)}.
	 * 
	 * <p>Test the equals method with a {@code null} value.</p>
	 */
	@Test
	public final void testEqualsWithNull() {
		// Setup the resources for the test.
		XBee16BitAddress addr = new XBee16BitAddress("0x1234");
		
		// Call the method under test.
		boolean areEqual = addr.equals(null);
		
		// Verify the result.
		assertThat("16-bit address cannot be equal to null", areEqual, is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBee16BitAddress#equals(Object)}.
	 * 
	 * <p>Test the equals method with a non {@code XBee16BitAddress} value.</p>
	 */
	@Test
	public final void testEqualsWithNonXBeePacket() {
		// Setup the resources for the test.
		XBee16BitAddress addr = new XBee16BitAddress("0x1234");
		
		// Call the method under test.
		boolean areEqual = addr.equals(new Object());
		
		// Verify the result.
		assertThat("16-bit address cannot be equal to an Object", areEqual, is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.models.XBee16BitAddress(Object)}.
	 * 
	 * <p>Test the equals method with different {@code XBee16BitAddress}.</p>
	 */
	@Test
	public final void testEqualsWithDifferentXBeePacket() {
		// Setup the resources for the test.
		XBee16BitAddress addr1 = new XBee16BitAddress("0x1234");
		XBee16BitAddress addr2 = new XBee16BitAddress("0x5678");
		
		// Call the method under test.
		boolean areEqual1 = addr1.equals(addr2);
		boolean areEqual2 = addr2.equals(addr1);
		
		// Verify the result.
		assertThat("16-bit addr1 must be different from 16-bit addr2", areEqual1, is(equalTo(false)));
		assertThat("16-bit addr2 must be different from 16-bit addr1", areEqual2, is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBee16BitAddress#equals(Object)}.
	 * 
	 * <p>Test the equals method with equal {@code XBee16BitAddress}.</p>
	 */
	@Test
	public final void testEqualsIsSymetric() {
		// Setup the resources for the test.
		XBee16BitAddress addr1 = new XBee16BitAddress("0x1234");
		XBee16BitAddress addr2 = new XBee16BitAddress("0x1234");
		
		// Call the method under test.
		boolean areEqual1 = addr1.equals(addr2);
		boolean areEqual2 = addr2.equals(addr1);
		
		// Verify the result.
		assertThat("16-bit addr1 must be equal to 16-bit addr2", areEqual1, is(equalTo(true)));
		assertThat("16-bit addr2 must be equal to 16-bit addr1", areEqual2, is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBee16BitAddress#equals(Object)}.
	 */
	@Test
	public final void testEqualsIsReflexive() {
		// Setup the resources for the test.
		XBee16BitAddress addr = new XBee16BitAddress("0x1234");
		
		// Call the method under test.
		boolean areEqual = addr.equals(addr);
		
		// Verify the result.
		assertThat("16-bits addr must be equal to itself", areEqual, is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBee16BitAddress#equals(Object)}.
	 */
	@Test
	public final void testEqualsIsTransitive() {
		// Setup the resources for the test.
		XBee16BitAddress addr1 = new XBee16BitAddress("0x1234");
		XBee16BitAddress addr2 = new XBee16BitAddress("0x1234");
		XBee16BitAddress addr3 = new XBee16BitAddress("0x1234");
		
		// Call the method under test.
		boolean areEqual1 = addr1.equals(addr2);
		boolean areEqual2 = addr2.equals(addr3);
		boolean areEqual3 = addr1.equals(addr3);
		
		// Verify the result.
		assertThat("16-bits addr1 must be equal to 16-bits addr2", areEqual1, is(equalTo(true)));
		assertThat("16-bits addr2 must be equal to 16-bits addr3", areEqual2, is(equalTo(true)));
		assertThat("16-bits addr1 must be equal to 16-bits addr3", areEqual3, is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBee16BitAddress#equals(Object)}.
	 */
	@Test
	public final void testEqualsIsConsistent() {
		// Setup the resources for the test.
		XBee16BitAddress addr1 = new XBee16BitAddress("0x1234");
		XBee16BitAddress addr2 = new XBee16BitAddress("0x1234");
		XBee16BitAddress addr3 = new XBee16BitAddress("0x5678");
		
		// Verify the result.
		assertThat("Consistent test fail addr1,addr2", addr1.equals(addr2), is(equalTo(true)));
		assertThat("Consistent test fail addr1,addr2", addr1.equals(addr2), is(equalTo(true)));
		assertThat("Consistent test fail addr1,addr2", addr1.equals(addr2), is(equalTo(true)));
		assertThat("Consistent test fail addr3,addr1", addr3.equals(addr1), is(equalTo(false)));
		assertThat("Consistent test fail addr3,addr1", addr3.equals(addr1), is(equalTo(false)));
		assertThat("Consistent test fail addr3,addr1", addr3.equals(addr1), is(equalTo(false)));

	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBee16BitAddress#hashCode()}.
	 */
	@Test
	public final void testHashCodeWithEqualPackets() {
		// Setup the resources for the test.
		XBee16BitAddress addr1 = new XBee16BitAddress("0x1234");
		XBee16BitAddress addr2 = new XBee16BitAddress("0x1234");
		
		// Call the method under test.
		int hashAddr1 = addr1.hashCode();
		int hashAddr2 = addr2.hashCode();
		
		// Verify the result.
		assertThat("16-bit addr1 must be equal to 16-bit addr2", addr1.equals(addr2), is(equalTo(true)));
		assertThat("16-bit addr2 must be equal to 16-bit addr1", addr2.equals(addr1), is(equalTo(true)));
		assertThat("Hash codes must be equal", hashAddr1, is(equalTo(hashAddr2)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBee16BitAddress#hashCode()}.
	 */
	@Test
	public final void testHashCodeWithDifferentPackets() {
		// Setup the resources for the test.
		XBee16BitAddress addr1 = new XBee16BitAddress("0x1234");
		XBee16BitAddress addr2 = new XBee16BitAddress("0x5678");
		
		// Call the method under test.
		int hashAddr1 = addr1.hashCode();
		int hashAddr2 = addr2.hashCode();
		
		// Verify the result.
		assertThat("16-bit addr1 must be different from 16-bit addr2", addr1.equals(addr2), is(equalTo(false)));
		assertThat("16-bit addr2 must be different from to 16-bit addr1", addr2.equals(addr1), is(equalTo(false)));
		assertThat("Hash codes must be different", hashAddr1, is(not(equalTo(hashAddr2))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBee16BitAddress#hashCode()}.
	 */
	@Test
	public final void testHashCodeIsConsistent() {
		// Setup the resources for the test.
		XBee16BitAddress addr = new XBee16BitAddress("0x1234");
		
		int initialHashCode = addr.hashCode();
		
		// Verify the result.
		assertThat("Consistent hashcode test fails", addr.hashCode(), is(equalTo(initialHashCode)));
		assertThat("Consistent hashcode test fails", addr.hashCode(), is(equalTo(initialHashCode)));
	}
}
