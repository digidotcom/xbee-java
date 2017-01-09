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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

public class XBeeIMEIAddressTest {
	
	// Constants.
	private final static byte[] INVALID_BYTE_ARRAY = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
	private final static String INVALID_STRING = "Hello world";
	private final static String INVALID_STRING_2 = "0x0123456789ABCDEF0123";
	
	private final static byte[] VALID_BYTE_ARRAY = new byte[]{0x01, 0x23, 0x45, 0x67, (byte)0x89, 0x01, 0x23, 0x45};
	private final static String VALID_STRING = "123456789012345";
	
	private final static byte[] INCOMPLETE_BYTE_ARRAY = new byte[]{0x23, 0x45};
	private final static String INCOMPLETE_STRING = "2345";
	
	private final static String EXPECTED_STRING = "123456789012345";
	private final static String EXPECTED_INCOMPLETE_STRING = "000000000002345";
	
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeIMEIAddress#XBeeIMEIAddress(byte[])},
	 * {@link com.digi.xbee.api.models.XBeeIMEIAddress#XBeeIMEIAddress(int[])} and
	 * {@link com.digi.xbee.api.models.XBeeIMEIAddress#XBeeIMEIAddress(String)}.
	 * 
	 * <p>Verify that IMEI object cannot be created using invalid parameters.</p>
	 */
	@Test
	public void testCreateWithInvalidParameters() {
		// Test with null byte array.
		try {
			new XBeeIMEIAddress((byte[])null);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), NullPointerException.class);
		}
		// Test with invalid byte array.
		try {
			new XBeeIMEIAddress(INVALID_BYTE_ARRAY);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
		// Test with null string.
		try {
			new XBeeIMEIAddress((String)null);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), NullPointerException.class);
		}
		// Test with invalid strings.
		try {
			new XBeeIMEIAddress(INVALID_STRING);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
		try {
			new XBeeIMEIAddress(INVALID_STRING_2);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		}
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeIMEIAddress#XBeeIMEIAddress(byte[])}.
	 * 
	 * <p>Verify that IMEI Address object can be created using a valid byte 
	 * array and the returned object value is correct.</p>
	 */
	@Test
	public void testCreateWithValidByteArray() {
		// Test with valid byte array.
		XBeeIMEIAddress imei = null;
		try {
			imei = new XBeeIMEIAddress(VALID_BYTE_ARRAY);
		} catch (Exception e) {
			fail("This exception should have not been thrown.");
		}
		
		assertEquals(EXPECTED_STRING, imei.getValue());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeIMEIAddress#XBeeIMEIAddress(String)}.
	 * 
	 * <p>Verify that IMEI Address object can be created using a valid string 
	 * and the returned object value is correct.</p>
	 */
	@Test
	public void testCreateWithValidString() {
		// Test with valid string.
		XBeeIMEIAddress imei = null;
		try {
			imei = new XBeeIMEIAddress(VALID_STRING);
		} catch (Exception e) {
			fail("This exception should have not been thrown.");
		}
		
		assertEquals(EXPECTED_STRING, imei.getValue());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeIMEIAddress#XBeeIMEIAddress(byte[])}.
	 * 
	 * <p>Verify that IMEI Address object can be created using an incomplete byte 
	 * array and the returned object value is correct.</p>
	 */
	@Test
	public void testCreateWithIncompleteByteArray() {
		// Test with an incomplete byte array.
		XBeeIMEIAddress imei = null;
		try {
			imei = new XBeeIMEIAddress(INCOMPLETE_BYTE_ARRAY);
		} catch (Exception e) {
			fail("This exception should have not been thrown.");
		}
		
		assertEquals(EXPECTED_INCOMPLETE_STRING, imei.getValue());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeIMEIAddress#XBeeIMEIAddress(String)}.
	 * 
	 * <p>Verify that IMEI Address object can be created using an incomplete
	 * string and the returned object value is correct.</p>
	 */
	@Test
	public void testCreateWithIncompleteString() {
		// Test with an incomplete string.
		XBeeIMEIAddress imei = null;
		try {
			imei = new XBeeIMEIAddress(INCOMPLETE_STRING);
		} catch (Exception e) {
			fail("This exception should have not been thrown.");
		}
		
		assertEquals(EXPECTED_INCOMPLETE_STRING, imei.getValue());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeIMEIAddress#equals(Object)}.
	 * 
	 * <p>Test the equals method with a {@code null} value.</p>
	 */
	@Test
	public final void testEqualsWithNull() {
		// Setup the resources for the test.
		XBeeIMEIAddress imei = new XBeeIMEIAddress("1234");
		
		// Call the method under test.
		boolean areEqual = imei.equals(null);
		
		// Verify the result.
		assertThat("IMEI address cannot be equal to null", areEqual, is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeIMEIAddress#equals(Object)}.
	 * 
	 * <p>Test the equals method with a non {@code XBeeIMEIAddress} value.</p>
	 */
	@Test
	public final void testEqualsWithNonIMEI() {
		// Setup the resources for the test.
		XBeeIMEIAddress imei = new XBeeIMEIAddress("1234");
		
		// Call the method under test.
		boolean areEqual = imei.equals(new Object());
		
		// Verify the result.
		assertThat("IMEI address cannot be equal to an Object", areEqual, is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeIMEIAddress(Object)}.
	 * 
	 * <p>Test the equals method with different {@code XBeeIMEIAddress}.</p>
	 */
	@Test
	public final void testEqualsWithDifferentIMEI() {
		// Setup the resources for the test.
		XBeeIMEIAddress imei1 = new XBeeIMEIAddress("1234");
		XBeeIMEIAddress imei2 = new XBeeIMEIAddress("5678");
		
		// Call the method under test.
		boolean areEqual1 = imei1.equals(imei2);
		boolean areEqual2 = imei2.equals(imei1);
		
		// Verify the result.
		assertThat("IMEI imei1 must be different from IMEI imei2", areEqual1, is(equalTo(false)));
		assertThat("IMEI imei2 must be different from IMEI imei1", areEqual2, is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeIMEIAddress#equals(Object)}.
	 * 
	 * <p>Test the equals method with equal {@code XBeeIMEIAddress}.</p>
	 */
	@Test
	public final void testEqualsIsSymetric() {
		// Setup the resources for the test.
		XBeeIMEIAddress imei1 = new XBeeIMEIAddress("1234");
		XBeeIMEIAddress imei2 = new XBeeIMEIAddress("1234");
		
		// Call the method under test.
		boolean areEqual1 = imei1.equals(imei2);
		boolean areEqual2 = imei2.equals(imei1);
		
		// Verify the result.
		assertThat("IMEI imei1 must be equal to IMEI imei2", areEqual1, is(equalTo(true)));
		assertThat("IMEI imei2 must be equal to IMEI imei1", areEqual2, is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeIMEIAddress#equals(Object)}.
	 */
	@Test
	public final void testEqualsIsReflexive() {
		// Setup the resources for the test.
		XBeeIMEIAddress imei = new XBeeIMEIAddress("1234");
		
		// Call the method under test.
		boolean areEqual = imei.equals(imei);
		
		// Verify the result.
		assertThat("IMEI imei must be equal to itself", areEqual, is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeIMEIAddress#equals(Object)}.
	 */
	@Test
	public final void testEqualsIsTransitive() {
		// Setup the resources for the test.
		XBeeIMEIAddress imei1 = new XBeeIMEIAddress("1234");
		XBeeIMEIAddress imei2 = new XBeeIMEIAddress("1234");
		XBeeIMEIAddress imei3 = new XBeeIMEIAddress("1234");
		
		// Call the method under test.
		boolean areEqual1 = imei1.equals(imei2);
		boolean areEqual2 = imei2.equals(imei3);
		boolean areEqual3 = imei1.equals(imei3);
		
		// Verify the result.
		assertThat("IMEI imei1 must be equal to IMEI imei2", areEqual1, is(equalTo(true)));
		assertThat("IMEI imei2 must be equal to IMEI imei3", areEqual2, is(equalTo(true)));
		assertThat("IMEI imei1 must be equal to IMEI imei3", areEqual3, is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeIMEIAddress#equals(Object)}.
	 */
	@Test
	public final void testEqualsIsConsistent() {
		// Setup the resources for the test.
		XBeeIMEIAddress imei1 = new XBeeIMEIAddress("1234");
		XBeeIMEIAddress imei2 = new XBeeIMEIAddress("1234");
		XBeeIMEIAddress imei3 = new XBeeIMEIAddress("5678");
		
		// Verify the result.
		assertThat("Consistent test fail imei1,imei2", imei1.equals(imei2), is(equalTo(true)));
		assertThat("Consistent test fail imei1,imei2", imei1.equals(imei2), is(equalTo(true)));
		assertThat("Consistent test fail imei1,imei2", imei1.equals(imei2), is(equalTo(true)));
		assertThat("Consistent test fail imei3,imei1", imei3.equals(imei1), is(equalTo(false)));
		assertThat("Consistent test fail imei3,imei1", imei3.equals(imei1), is(equalTo(false)));
		assertThat("Consistent test fail imei3,imei1", imei3.equals(imei1), is(equalTo(false)));

	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeIMEIAddress#hashCode()}.
	 */
	@Test
	public final void testHashCodeWithEqualPackets() {
		// Setup the resources for the test.
		XBeeIMEIAddress imei1 = new XBeeIMEIAddress("1234");
		XBeeIMEIAddress imei2 = new XBeeIMEIAddress("1234");
		
		// Call the method under test.
		int hashIMEI1 = imei1.hashCode();
		int hashIMEI2 = imei2.hashCode();
		
		// Verify the result.
		assertThat("IMEI imei1 must be equal to IMEI imei2", imei1.equals(imei2), is(equalTo(true)));
		assertThat("IMEI imei2 must be equal to IMEI imei1", imei2.equals(imei1), is(equalTo(true)));
		assertThat("Hash codes must be equal", hashIMEI1, is(equalTo(hashIMEI2)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeIMEIAddress#hashCode()}.
	 */
	@Test
	public final void testHashCodeWithDifferentPackets() {
		// Setup the resources for the test.
		XBeeIMEIAddress imei1 = new XBeeIMEIAddress("1234");
		XBeeIMEIAddress imei2 = new XBeeIMEIAddress("5678");
		
		// Call the method under test.
		int hashIMEI1 = imei1.hashCode();
		int hashIMEI2 = imei2.hashCode();
		
		// Verify the result.
		assertThat("IMEI imei1 must be different from IMEI imei2", imei1.equals(imei2), is(equalTo(false)));
		assertThat("IMEI imei2 must be different from to IMEI imei1", imei2.equals(imei1), is(equalTo(false)));
		assertThat("Hash codes must be different", hashIMEI1, is(not(equalTo(hashIMEI2))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeIMEIAddress#hashCode()}.
	 */
	@Test
	public final void testHashCodeIsConsistent() {
		// Setup the resources for the test.
		XBeeIMEIAddress imei = new XBeeIMEIAddress("1234");
		
		int initialHashCode = imei.hashCode();
		
		// Verify the result.
		assertThat("Consistent hashcode test fails", imei.hashCode(), is(equalTo(initialHashCode)));
		assertThat("Consistent hashcode test fails", imei.hashCode(), is(equalTo(initialHashCode)));
	}
}
