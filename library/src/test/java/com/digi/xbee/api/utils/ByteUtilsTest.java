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
package com.digi.xbee.api.utils;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ByteUtilsTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();

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
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#readBytes(int, java.io.ByteArrayInputStream)}.
	 */
	@Test
	public final void testReadBytesNullInputStream() {
		// Setup the resources for the test.
		ByteArrayInputStream in = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Input stream cannot be null.")));
		
		// Call the method under test.
		ByteUtils.readBytes(2, in);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#readBytes(int, java.io.ByteArrayInputStream)}.
	 */
	@Test
	public final void testReadBytesNegativeNumberOfBytes() {
		// Setup the resources for the test.
		ByteArrayInputStream in = new ByteArrayInputStream(new byte[0]);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Number of bytes to read must be equal or greater than 0.")));
		
		// Call the method under test.
		ByteUtils.readBytes(-5, in);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#readBytes(int, java.io.ByteArrayInputStream)}.
	 */
	@Test
	public final void testReadBytesEmptyInputStream() {
		// Setup the resources for the test.
		byte[] contents = new byte[0];
		ByteArrayInputStream in = new ByteArrayInputStream(contents);
		
		// Call the method under test.
		byte[] result = ByteUtils.readBytes(2, in);
		
		// Verify the result.
		assertThat("Returned byte array must be empty", result.length, is(equalTo(0)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#readBytes(int, java.io.ByteArrayInputStream)}.
	 */
	@Test
	public final void testReadBytesMoreBytesThanAvailable() {
		// Setup the resources for the test.
		byte[] contents = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04};
		ByteArrayInputStream in = new ByteArrayInputStream(contents);
		
		// Call the method under test.
		byte[] result = ByteUtils.readBytes(contents.length + 1, in);
		
		// Verify the result.
		assertThat("Returned byte array lenght must be " + contents.length, result.length, is(equalTo(contents.length)));
		assertThat("Returned byte array must be equal to 'contents'", result, is(equalTo(contents)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#readBytes(int, java.io.ByteArrayInputStream)}.
	 */
	@Test
	public final void testReadBytesExactTheAvailable() {
		// Setup the resources for the test.
		byte[] contents = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04};
		ByteArrayInputStream in = new ByteArrayInputStream(contents);
		
		// Call the method under test.
		byte[] result = ByteUtils.readBytes(contents.length, in);
		
		// Verify the result.
		assertThat("Returned byte array lenght must be " + contents.length, result.length, is(equalTo(contents.length)));
		assertThat("Returned byte array must be equal to 'contents'", result, is(equalTo(contents)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#readBytes(int, java.io.ByteArrayInputStream)}.
	 */
	@Test
	public final void testReadBytesLessThanAvailable() {
		// Setup the resources for the test.
		byte[] contents = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04};
		byte[] expectedResult = new byte[]{0x00, 0x01, 0x02, 0x03};
		ByteArrayInputStream in = new ByteArrayInputStream(contents);
		
		// Call the method under test.
		byte[] result = ByteUtils.readBytes(contents.length - 1, in);
		
		// Verify the result.
		assertThat("Returned byte array lenght must be " + (contents.length - 1), result.length, is(equalTo(contents.length - 1)));
		assertThat("Returned byte array must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#readString(ByteArrayInputStream)}.
	 */
	@Test
	public final void testReadStringNullInputStream() {
		// Setup the resources for the test.
		ByteArrayInputStream in = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Input stream cannot be null.")));
		
		// Call the method under test.
		ByteUtils.readString(in);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#readString(ByteArrayInputStream)}.
	 */
	@Test
	public final void testReadStringEmptyInputStream() {
		// Setup the resources for the test.
		byte[] contents = new byte[0];
		ByteArrayInputStream in = new ByteArrayInputStream(contents);
		
		// Call the method under test.
		String result = ByteUtils.readString(in);
		
		// Verify the result.
		assertThat("Returned string must be empty", result.length(), is(equalTo(0)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#readString(ByteArrayInputStream)}.
	 */
	@Test
	public final void testReadStringNotNullTerminated() {
		// Setup the resources for the test.
		byte[] contents = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05};
		ByteArrayInputStream in = new ByteArrayInputStream(contents);
		
		// Call the method under test.
		String result = ByteUtils.readString(in);
		
		// Verify the result.
		assertThat("Returned string lenght must be " + contents.length, result.length(), is(equalTo(contents.length)));
		assertThat("Returned string must be equal to 'contents'", result, is(equalTo(new String(contents))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#readString(ByteArrayInputStream)}.
	 */
	@Test
	public final void testReadStringNullTerminated() {
		// Setup the resources for the test.
		byte[] contents = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x00};
		byte[] expectedResult = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05};
		ByteArrayInputStream in = new ByteArrayInputStream(contents);
		
		// Call the method under test.
		String result = ByteUtils.readString(in);
		
		// Verify the result.
		assertThat("Returned string lenght must be " + contents.length, result.length(), is(equalTo(expectedResult.length)));
		assertThat("Returned string must be equal to 'expectedResult'", result, is(equalTo(new String(expectedResult))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#readString(ByteArrayInputStream)}.
	 */
	@Test
	public final void testReadStringNullTerminatedAndMore() {
		// Setup the resources for the test.
		byte[] contents = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x00, 0x06, 0x07, 0x08};
		byte[] expectedResult = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05};
		ByteArrayInputStream in = new ByteArrayInputStream(contents);
		
		// Call the method under test.
		String result = ByteUtils.readString(in);
		
		// Verify the result.
		assertThat("Returned string lenght must be " + contents.length, result.length(), is(equalTo(expectedResult.length)));
		assertThat("Returned string must be equal to 'expectedResult'", result, is(equalTo(new String(expectedResult))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#longToByteArray(long)}.
	 */
	@Test
	public final void testLongToByteArray() {
		// Setup the resources for the test.
		long l = 0x1234859623475620L;
		byte[] expectedResult = new byte[]{0x12, 0x34, (byte)0x85, (byte)0x96, 0x23, 0x47, 0x56, 0x20};
		
		// Call the method under test.
		byte[] result = ByteUtils.longToByteArray(l);
		
		// Verify the result.
		assertThat("Returned byte array must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#byteArrayToLong(byte[])}.
	 */
	@Test
	public final void testByteArrayToLongNullArray() {
		// Setup the resources for the test.
		byte[] byteArray = null;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Byte array cannot be null.")));
		
		// Call the method under test.
		ByteUtils.byteArrayToLong(byteArray);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#byteArrayToLong(byte[])}.
	 */
	@Test
	public final void testByteArrayToLongEmptyArray() {
		// Setup the resources for the test.
		byte[] byteArray = new byte[0];
		long expectedResult = 0;
		
		// Call the method under test.
		long result = ByteUtils.byteArrayToLong(byteArray);
		
		// Verify the result.
		assertThat("Returned long must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#byteArrayToLong(byte[])}.
	 */
	@Test
	public final void testByteArrayToLongLessThan8Bytes() {
		// Setup the resources for the test.
		byte[] byteArray = new byte[]{0x12, 0x34, (byte)0x85, (byte)0x96, 0x23, 0x47};
		long expectedResult = 0x123485962347L;
		
		// Call the method under test.
		long result = ByteUtils.byteArrayToLong(byteArray);
		
		// Verify the result.
		assertThat("Returned long must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#byteArrayToLong(byte[])}.
	 */
	@Test
	public final void testByteArrayToLong8Bytes() {
		// Setup the resources for the test.
		byte[] byteArray = new byte[]{0x12, 0x34, (byte)0x85, (byte)0x96, 0x23, 0x47, 0x56, 0x20};
		long expectedResult = 0x1234859623475620L;
		
		// Call the method under test.
		long result = ByteUtils.byteArrayToLong(byteArray);
		
		// Verify the result.
		assertThat("Returned long must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#byteArrayToLong(byte[])}.
	 */
	@Test
	public final void testByteArrayToLongMoreThan8Bytes() {
		// Setup the resources for the test.
		byte[] byteArray = new byte[]{0x12, 0x34, (byte)0x85, (byte)0x96, 0x23, 0x47, 0x56, 0x20, 0x63, 0x67};
		long expectedResult = 0x1234859623475620L;
		
		// Call the method under test.
		long result = ByteUtils.byteArrayToLong(byteArray);
		
		// Verify the result.
		assertThat("Returned long must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#byteArrayToLong(byte[])}
	 * and {@link com.digi.xbee.api.utils.ByteUtils#longToByteArray(long)}.
	 */
	@Test
	public final void testLongToByteArrayAndByteArrayToLong() {
		// Setup the resources for the test.
		long l = 0x1234859623475620L;
		
		// Call the method under test.
		long result = ByteUtils.byteArrayToLong(ByteUtils.longToByteArray(l));
		
		// Verify the result.
		assertThat("Returned long must be equal to initial long", result, is(equalTo(l)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#intToByteArray(int)}.
	 */
	@Test
	public final void testIntToByteArray() {
		// Setup the resources for the test.
		int i = 0x98563214;
		byte[] expectedResult = new byte[]{(byte)0x98, 0x56, 0x32, 0x14};
		
		// Call the method under test.
		byte[] result = ByteUtils.intToByteArray(i);
		
		// Verify the result.
		assertThat("Returned byte array must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#byteArrayToInt(byte[])}.
	 */
	@Test
	public final void testByteArrayToIntNullArray() {
		// Setup the resources for the test.
		byte[] byteArray = null;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Byte array cannot be null.")));
		
		// Call the method under test.
		ByteUtils.byteArrayToInt(byteArray);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#byteArrayToInt(byte[])}.
	 */
	@Test
	public final void testByteArrayToIntEmptyArray() {
		// Setup the resources for the test.
		byte[] byteArray = new byte[0];
		int expectedResult = 0;
		
		// Call the method under test.
		int result = ByteUtils.byteArrayToInt(byteArray);
		
		// Verify the result.
		assertThat("Returned int must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#byteArrayToInt(byte[])}.
	 */
	@Test
	public final void testByteArrayToIntLessThan4Bytes() {
		// Setup the resources for the test.
		byte[] byteArray = new byte[]{0x12, 0x34};
		int expectedResult = 0x1234;
		
		// Call the method under test.
		int result = ByteUtils.byteArrayToInt(byteArray);
		
		// Verify the result.
		assertThat("Returned int must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#byteArrayToInt(byte[])}.
	 */
	@Test
	public final void testByteArrayToInt4Bytes() {
		// Setup the resources for the test.
		byte[] byteArray = new byte[]{0x12, 0x34, (byte)0x85, (byte)0x96};
		int expectedResult = 0x12348596;
		
		// Call the method under test.
		int result = ByteUtils.byteArrayToInt(byteArray);
		
		// Verify the result.
		assertThat("Returned int must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#byteArrayToInt(byte[])}.
	 */
	@Test
	public final void testByteArrayToIntMoreThan4Bytes() {
		// Setup the resources for the test.
		byte[] byteArray = new byte[]{0x12, 0x34, (byte)0x85, (byte)0x96, 0x23, 0x47, 0x56, 0x20, 0x63, 0x67};
		int expectedResult = 0x12348596;
		
		// Call the method under test.
		int result = ByteUtils.byteArrayToInt(byteArray);
		
		// Verify the result.
		assertThat("Returned int must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#intToByteArray(int)}
	 * and {@link com.digi.xbee.api.utils.ByteUtils#byteArrayToInt(byte[])}.
	 */
	@Test
	public final void testIntToByteArrayAndByteArrayToInt() {
		// Setup the resources for the test.
		int i = 0x12348596;
		
		// Call the method under test.
		int result = ByteUtils.byteArrayToInt(ByteUtils.intToByteArray(i));
		
		// Verify the result.
		assertThat("Returned int must be equal to initial int", result, is(equalTo(i)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#shortToByteArray(short)}.
	 */
	@Test
	public final void testShortToByteArray() {
		// Setup the resources for the test.
		short s = 0x7FFF;
		byte[] expectedResult = new byte[]{0x7F, (byte)0xFF};
		
		// Call the method under test.
		byte[] result = ByteUtils.shortToByteArray(s);
		
		// Verify the result.
		assertThat("Returned byte array must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#byteArrayToShort(byte[])}.
	 */
	@Test
	public final void testByteArrayToShortNullArray() {
		// Setup the resources for the test.
		byte[] byteArray = null;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Byte array cannot be null.")));
		
		// Call the method under test.
		ByteUtils.byteArrayToShort(byteArray);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#byteArrayToShort(byte[])}.
	 */
	@Test
	public final void testByteArrayToShortEmptyArray() {
		// Setup the resources for the test.
		byte[] byteArray = new byte[0];
		short expectedResult = 0;
		
		// Call the method under test.
		short result = ByteUtils.byteArrayToShort(byteArray);
		
		// Verify the result.
		assertThat("Returned int must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#byteArrayToShort(byte[])}.
	 */
	@Test
	public final void testByteArrayToShortLessThan2Bytes() {
		// Setup the resources for the test.
		byte[] byteArray = new byte[]{0x12};
		short expectedResult = 0x12;
		
		// Call the method under test.
		short result = ByteUtils.byteArrayToShort(byteArray);
		
		// Verify the result.
		assertThat("Returned short must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#byteArrayToShort(byte[])}.
	 */
	@Test
	public final void testByteArrayToShort2Bytes() {
		// Setup the resources for the test.
		byte[] byteArray = new byte[]{0x12, 0x34};
		short expectedResult = 0x1234;
		
		// Call the method under test.
		short result = ByteUtils.byteArrayToShort(byteArray);
		
		// Verify the result.
		assertThat("Returned short must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#byteArrayToShort(byte[])}.
	 */
	@Test
	public final void testByteArrayToShortMoreThan2Bytes() {
		// Setup the resources for the test.
		byte[] byteArray = new byte[]{0x12, 0x34, (byte)0x85, (byte)0x96};
		short expectedResult = 0x1234;
		
		// Call the method under test.
		short result = ByteUtils.byteArrayToShort(byteArray);
		
		// Verify the result.
		assertThat("Returned short must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#shortToByteArray(short)}
	 * and {@link com.digi.xbee.api.utils.ByteUtils#byteArrayToShort(byte[])}.
	 */
	@Test
	public final void testShortToByteArrayAndByteArrayToShort() {
		// Setup the resources for the test.
		short s = (short)0xFFFF;
		
		// Call the method under test.
		short result = ByteUtils.byteArrayToShort(ByteUtils.shortToByteArray(s));
		
		// Verify the result.
		assertThat("Returned short must be equal to initial short", result, is(equalTo(s)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#stringToByteArray(String)}.
	 */
	@Test
	public final void testStringToByteArrayNullString() {
		// Setup the resources for the test.
		String s = null;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Value cannot be null.")));
		
		// Call the method under test.
		ByteUtils.stringToByteArray(s);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#stringToByteArray(String)}.
	 */
	@Test
	public final void testStringToByteArrayEmptyString() {
		// Setup the resources for the test.
		String s = "";
		
		// Call the method under test.
		byte[] result = ByteUtils.stringToByteArray(s);
		
		// Verify the result.
		assertThat("Returned byte array must be empty", result.length, is(equalTo(0)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#stringToByteArray(String)}.
	 */
	@Test
	public final void testStringToByteArray() {
		// Setup the resources for the test.
		String s = "Hello";
		byte[] expectedResult =  new byte[]{'H', 'e', 'l', 'l', 'o'};
		
		// Call the method under test.
		byte[] result = ByteUtils.stringToByteArray(s);
		
		// Verify the result.
		assertThat("Returned byte array must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#byteArrayToString(byte[])}.
	 */
	@Test
	public final void testByteArrayToStringNullArray() {
		// Setup the resources for the test.
		byte[] byteArray = null;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Byte array cannot be null.")));
		
		// Call the method under test.
		ByteUtils.byteArrayToString(byteArray);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#byteArrayToString(String)}.
	 */
	@Test
	public final void testByteArrayToStringEmptyArray() {
		// Setup the resources for the test.
		byte[] byteArray = new byte[0];
		String expectedResult = "";
		
		// Call the method under test.
		String result = ByteUtils.byteArrayToString(byteArray);
		
		// Verify the result.
		assertThat("Returned string must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#byteArrayToString(String)}.
	 */
	@Test
	public final void testByteArrayToString() {
		// Setup the resources for the test.
		byte[] byteArray = new byte[]{'H', 'e', 'l', 'l', 'o'};
		String expectedResult = "Hello";
		
		// Call the method under test.
		String result = ByteUtils.byteArrayToString(byteArray);
		
		// Verify the result.
		assertThat("Returned string must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#stringToByteArray(String)}
	 * and {@link com.digi.xbee.api.utils.ByteUtils#byteArrayToString(byte[])}.
	 */
	@Test
	public final void testStringToByteArrayAndByteArrayToString() {
		// Setup the resources for the test.
		String s = "Hello";
		
		// Call the method under test.
		String result = ByteUtils.byteArrayToString(ByteUtils.stringToByteArray(s));
		
		// Verify the result.
		assertThat("Returned string must be equal to initial string", result, is(equalTo(s)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#byteToInt(byte)}.
	 */
	@Test
	public final void testByteToInt() {
		// Setup the resources for the test.
		byte b = (byte)0xF8;
		int expectedResult = 0xF8;
		
		// Call the method under test.
		int result = ByteUtils.byteToInt(b);
		
		// Verify the result.
		assertThat("Returned int must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#isBitEnabled(int, int)}.
	 */
	@Test
	public final void testIsBitEnabledNegativePosition() {
		// Setup the resources for the test.
		int n = 0xFFFF;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Bit position must be between 0 and 31.")));
		
		// Call the method under test.
		ByteUtils.isBitEnabled(n, -9);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#isBitEnabled(int, int)}.
	 */
	@Test
	public final void testIsBitEnabledPositionGreaterThanValid() {
		// Setup the resources for the test.
		int n = 0xFFFF;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Bit position must be between 0 and 31.")));
		
		// Call the method under test.
		ByteUtils.isBitEnabled(n, 400);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#isBitEnabled(int, int)}.
	 */
	@Test
	public final void testIsBitEnabledTrue() {
		// Setup the resources for the test.
		int n = 0xFFFF;
		boolean expectedResult = true;
		
		// Call the method under test.
		boolean result = ByteUtils.isBitEnabled(n, 5);
		
		// Verify the result.
		assertThat("Bit must be enabled", result, is(equalTo(expectedResult)));
	}
	
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#isBitEnabled(int, int)}.
	 */
	@Test
	public final void testIsBitEnabledFalse() {
		// Setup the resources for the test.
		int n = 0x0;
		boolean expectedResult = false;
		
		// Call the method under test.
		boolean result = ByteUtils.isBitEnabled(n, 5);
		
		// Verify the result.
		assertThat("Bit must be disabled", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#readIntegerFromByte(byte, int, int)}.
	 */
	@Test
	public final void testReadIntegerFromByteNegativeOffset() {
		// Setup the resources for the test.
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Offset must be between 0 and 7.")));
		
		// Call the method under test.
		ByteUtils.readIntegerFromByte((byte)25, -8, 1);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#readIntegerFromByte(byte, int, int)}.
	 */
	@Test
	public final void testReadIntegerFromByteOffsetGreaterThanLen() {
		// Setup the resources for the test.
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Offset must be between 0 and 7.")));
		
		// Call the method under test.
		ByteUtils.readIntegerFromByte((byte)25, 100, 1);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#readIntegerFromByte(byte, int, int)}.
	 */
	@Test
	public final void testReadIntegerFromByteNegativeLength() {
		// Setup the resources for the test.
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Length must be between 0 and 8.")));
		
		// Call the method under test.
		ByteUtils.readIntegerFromByte((byte)25, 0, -1);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#readIntegerFromByte(byte, int, int)}.
	 */
	@Test
	public final void testReadIntegerFromByteLengthGreaterThan8() {
		// Setup the resources for the test.
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Length must be between 0 and 8.")));
		
		// Call the method under test.
		ByteUtils.readIntegerFromByte((byte)25, 0, 100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#readIntegerFromByte(byte, int, int)}.
	 */
	@Test
	public final void testReadIntegerFromByteOffsetLengthPlusOffsetBiggerThan8() {
		// Setup the resources for the test.
		int expectedResult = 3;
		
		// Call the method under test.
		int result = ByteUtils.readIntegerFromByte((byte)54, 4, 5);
		
		// Verify the result.
		assertThat("Returned int must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#readIntegerFromByte(byte, int, int)}.
	 */
	@Test
	public final void testReadIntegerFromByteOffset() {
		// Setup the resources for the test.
		int expectedResult = 0xB; // 0b1011
		
		// Call the method under test.
		int result = ByteUtils.readIntegerFromByte((byte)0x36 /* 0b110110 */, 1, 4);
		
		// Verify the result.
		assertThat("Returned int must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#readBooleanFromByte(byte, int)}.
	 */
	@Test
	public final void testReadBooleanFromByteOffsetNegative() {
		// Setup the resources for the test.
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Bit offset must be between 0 and 7.")));
		
		// Call the method under test.
		ByteUtils.readBooleanFromByte((byte)25, -9);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#readBooleanFromByte(byte, int)}.
	 */
	@Test
	public final void testReadBooleanFromByteOffsetBiggerThan7() {
		// Setup the resources for the test.
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Bit offset must be between 0 and 7.")));
		
		// Call the method under test.
		ByteUtils.readBooleanFromByte((byte)25, 100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#readBooleanFromByte(byte, int)}.
	 */
	@Test
	public final void testReadBooleanFromByteTrue() {
		// Setup the resources for the test.
		byte n = (byte)0x08;
		boolean expectedResult = true;
		
		// Call the method under test.
		boolean result = ByteUtils.readBooleanFromByte(n, 3);
		
		// Verify the result.
		assertThat("Read boolean must be true", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#readBooleanFromByte(byte, int)}.
	 */
	@Test
	public final void testReadBooleanFromByteFalse() {
		// Setup the resources for the test.
		byte n = (byte)0xEF;
		boolean expectedResult = false;
		
		// Call the method under test.
		boolean result = ByteUtils.readBooleanFromByte(n, 4);
		
		// Verify the result.
		assertThat("Read boolean must be false", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#readUntilCR(ByteArrayInputStream)}.
	 */
	@Test
	public final void testReadUntilCRNullInputStream() {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Input stream cannot be null.")));
		
		// Call the method under test.
		ByteUtils.readUntilCR(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#readUntilCR(ByteArrayInputStream)}.
	 */
	@Test
	public final void testReadUntilCREmptyInputStream() {
		// Setup the resources for the test.
		ByteArrayInputStream in = new ByteArrayInputStream(new byte[0]);
		
		// Call the method under test.
		byte[] result = ByteUtils.readUntilCR(in);
		
		// Verify the result.
		assertThat("Read byte array must be empty", result.length, is(equalTo(0)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#readUntilCR(ByteArrayInputStream)}.
	 */
	@Test
	public final void testReadUntilCRNoCR() {
		// Setup the resources for the test.
		byte[] contents = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x00, 0x06, 0x07, 0x08};
		ByteArrayInputStream in = new ByteArrayInputStream(contents);
		
		// Call the method under test.
		byte[] result = ByteUtils.readUntilCR(in);
		
		// Verify the result.
		assertThat("Read byte array must be equal to 'contents'", result, is(equalTo(contents)));
		assertThat("Remaining bytes to read must be 0", in.available(), is(equalTo(0)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#readUntilCR(ByteArrayInputStream)}.
	 */
	@Test
	public final void testReadUntilCR() {
		// Setup the resources for the test.
		byte[] contents = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x00, 0x06, 0x07, 0x08, 0x0D, (byte)0x89, 0x25, 0x01};
		byte[] expectedResult = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x00, 0x06, 0x07, 0x08};
		ByteArrayInputStream in = new ByteArrayInputStream(contents);
		
		// Call the method under test.
		byte[] result = ByteUtils.readUntilCR(in);
		
		// Verify the result.
		assertThat("Read byte array must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
		assertThat("Remaining bytes to read must be " + (contents.length - expectedResult.length - 1), in.available(), is(equalTo(contents.length - expectedResult.length - 1)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#newByteArray(byte[], int)}.
	 */
	@Test
	public final void testNewByteArrayNullByteArray() {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Data cannot be null.")));
		
		// Call the method under test.
		ByteUtils.newByteArray(null, 1);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#newByteArray(byte[], int)}.
	 */
	@Test
	public final void testNewByteArrayNegativeSize() {
		// Setup the resources for the test.
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Final size must be equal or greater than 0.")));
		
		// Call the method under test.
		ByteUtils.newByteArray(new byte[0], -5);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#newByteArray(byte[], int)}.
	 */
	@Test
	public final void testNewByteArrayEmptyByteArray() {
		// Setup the resources for the test.
		byte[] expectedResult = new byte[]{'0', '0'};
		
		// Call the method under test.
		byte[] result = ByteUtils.newByteArray(new byte[0], expectedResult.length);
		
		// Verify the result.
		assertThat("Result byte array must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#newByteArray(byte[], int)}.
	 */
	@Test
	public final void testNewByteArrayZeroFinalSize() {
		// Setup the resources for the test.
		byte[] original = new byte[]{'1', '2', '3', '4'};
		byte[] expectedResult = new byte[0];
		
		// Call the method under test.
		byte[] result = ByteUtils.newByteArray(original, 0);
		
		// Verify the result.
		assertThat("Result byte array must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#newByteArray(byte[], int)}.
	 */
	@Test
	public final void testNewByteArrayFinalSizeLessThanOriginal() {
		// Setup the resources for the test.
		byte[] original = new byte[]{'1', '2', '3', '4'};
		byte[] expectedResult = new byte[]{'1'};
		
		// Call the method under test.
		byte[] result = ByteUtils.newByteArray(original, 1);
		
		// Verify the result.
		assertThat("Result byte array must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#newByteArray(byte[], int)}.
	 */
	@Test
	public final void testNewByteArrayFinalSizeSameAsOriginal() {
		// Setup the resources for the test.
		byte[] original = new byte[]{'1', '2', '3', '4'};
		byte[] expectedResult = new byte[]{'1', '2', '3', '4'};
		
		// Call the method under test.
		byte[] result = ByteUtils.newByteArray(original, 4);
		
		// Verify the result.
		assertThat("Result byte array must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#newByteArray(byte[], int)}.
	 */
	@Test
	public final void testNewByteArrayFinalSizeGreaterThanOriginal() {
		// Setup the resources for the test.
		byte[] original = new byte[]{'1', '2', '3', '4'};
		byte[] expectedResult = new byte[]{'0', '0', '1', '2', '3', '4'};
		
		// Call the method under test.
		byte[] result = ByteUtils.newByteArray(original, 6);
		
		// Verify the result.
		assertThat("Result byte array must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#swapByteArray(byte[])}.
	 */
	@Test
	public final void testSwapByteArrayNullByteArray() {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Source cannot be null.")));
		
		// Call the method under test.
		ByteUtils.swapByteArray(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#swapByteArray(byte[])}.
	 */
	@Test
	public final void testSwapByteArrayEmptyArray() {
		// Setup the resources for the test.
		byte[] original = new byte[0];
		byte[] expectedResult = new byte[0];
		
		// Call the method under test.
		byte[] result = ByteUtils.swapByteArray(original);
		
		// Verify the result.
		assertThat("Result byte array must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.ByteUtils#swapByteArray(byte[])}.
	 */
	@Test
	public final void testSwapByteArray() {
		// Setup the resources for the test.
		byte[] original = new byte[] {'B', 'y', 'e', ' ', 'm', 'y', ' ', 'f', 'r', 'i', 'e', 'n', 'd'};
		byte[] expectedResult = new byte[]{'d', 'n', 'e', 'i', 'r', 'f', ' ', 'y', 'm', ' ', 'e', 'y', 'B'};
		
		// Call the method under test.
		byte[] result = ByteUtils.swapByteArray(original);
		
		// Verify the result.
		assertThat("Result byte array must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
}
