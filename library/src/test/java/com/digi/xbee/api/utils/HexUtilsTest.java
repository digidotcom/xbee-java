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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.digi.xbee.api.utils.HexUtils;

public class HexUtilsTest {

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
	 * Test method for {@link com.digi.xbee.api.utils.HexUtils#byteArrayToHexString(byte[])}.
	 */
	@Test
	public final void testByteArrayToHexStringNullByteArray() {
		// Setup the resources for the test.
		byte[] array = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Value to convert cannot be null.")));
		
		// Call the method under test.
		HexUtils.byteArrayToHexString(array);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.HexUtils#byteArrayToHexString(byte[])}.
	 */
	@Test
	public final void testByteArrayToHexString() {
		// Setup the resources for the test.
		byte[] array = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05};
		String expectedResult = "0102030405";
		
		// Call the method under test.
		String result = HexUtils.byteArrayToHexString(array);
		
		// Verify the result.
		assertThat("Returned string must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.HexUtils#byteToHexString(byte)}.
	 */
	@Test
	public final void testByteToHexString() {
		// Setup the resources for the test.
		byte b = (byte)0xE8;
		String expectedResult = "E8";
		
		// Call the method under test.
		String result = HexUtils.byteToHexString(b);
		
		// Verify the result.
		assertThat("Returned string must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.HexUtils#hexStringToByteArray(String)}.
	 */
	@Test
	public final void testHexStringToByteArrayNullString() {
		// Setup the resources for the test.
		String s = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Value to convert cannot be null.")));
		
		// Call the method under test.
		HexUtils.hexStringToByteArray(s);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.HexUtils#hexStringToByteArray(String)}.
	 */
	@Test
	public final void testHexStringToByteArrayEmptyStrng() {
		// Setup the resources for the test.
		String s = "";
		byte[] expectedResult = new byte[0];
		
		// Call the method under test.
		byte[] result = HexUtils.hexStringToByteArray(s);
		
		// Verify the result.
		assertThat("Returned byte array must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.HexUtils#hexStringToByteArray(String)}.
	 */
	@Test
	public final void testHexStringToByteArray() {
		// Setup the resources for the test.
		String s = "0102030405";
		byte[] expectedResult = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05};
		
		// Call the method under test.
		byte[] result = HexUtils.hexStringToByteArray(s);
		
		// Verify the result.
		assertThat("Returned byte array must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.HexUtils#containsLetters(String)}.
	 */
	@Test
	public final void testContainsLettersNullParameter() {
		// Setup the resources for the test.
		String s = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Parameter cannot be null.")));
		
		// Call the method under test.
		HexUtils.containsLetters(s);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.HexUtils#containsLetters(String)}.
	 */
	@Test
	public final void testContainsLettersEmptyParameter() {
		// Setup the resources for the test.
		String s = "";
		boolean expectedResult = false;
		
		// Call the method under test.
		boolean result = HexUtils.containsLetters(s);
		
		// Verify the result.
		assertThat("The given string does not contain letters", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.HexUtils#containsLetters(String)}.
	 */
	@Test
	public final void testContainsLettersTrue() {
		// Setup the resources for the test.
		String s = "0123456789abc";
		boolean expectedResult = true;
		
		// Call the method under test.
		boolean result = HexUtils.containsLetters(s);
		
		// Verify the result.
		assertThat("The given string contains letters", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.HexUtils#containsLetters(String)}.
	 */
	@Test
	public final void testContainsLettersFalse() {
		// Setup the resources for the test.
		String s = "013456789";
		boolean expectedResult = false;
		
		// Call the method under test.
		boolean result = HexUtils.containsLetters(s);
		
		// Verify the result.
		assertThat("The given string does not contain letters", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.HexUtils#integerToHexString(int, int)}.
	 */
	@Test
	public final void testIntegerToHexStringNegativeMinBytes() {
		// Setup the resources for the test.
		int v = 0x12345678;
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Minimum number of bytes must be greater than 0.")));
		
		// Call the method under test.
		HexUtils.integerToHexString(v, -2);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.HexUtils#integerToHexString(int, int)}.
	 */
	@Test
	public final void testIntegerToHexStringZeroMinBytes() {
		// Setup the resources for the test.
		int v = 0x12345678;
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Minimum number of bytes must be greater than 0.")));
		
		// Call the method under test.
		HexUtils.integerToHexString(v, 0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.HexUtils#integerToHexString(int, int)}.
	 */
	@Test
	public final void testIntegerToHexString() {
		// Setup the resources for the test.
		int v = 0x12345678;
		String expectedResult = "12345678";
		
		// Call the method under test.
		String result = HexUtils.integerToHexString(v, 4);
		
		// Verify the result.
		assertThat("Returned string must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.HexUtils#integerToHexString(int, int)}.
	 */
	@Test
	public final void testIntegerToHexStringMoreBytes() {
		// Setup the resources for the test.
		int v = 0x0000000012345678;
		String expectedResult = "0000000012345678";
		
		// Call the method under test.
		String result = HexUtils.integerToHexString(v, 8);
		
		// Verify the result.
		assertThat("Returned string must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.HexUtils#prettyHexString(String)}.
	 */
	@Test
	public final void testPrettyHexStringNullString() {
		// Setup the resources for the test.
		String s = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Hexadecimal string cannot be null.")));
		
		// Call the method under test.
		HexUtils.prettyHexString(s);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.HexUtils#prettyHexString(String)}.
	 */
	@Test
	public final void testPrettyHexStringNonHexadecimalString() {
		// Setup the resources for the test.
		String s = "tyurw";
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Given string cannot contain non-hexadecimal characters.")));
		
		// Call the method under test.
		HexUtils.prettyHexString(s);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.HexUtils#prettyHexString(String)}.
	 */
	@Test
	public final void testPrettyHexStringEmptyString() {
		// Setup the resources for the test.
		String s = "";
		String expectedResult = "";
		
		// Call the method under test.
		String result = HexUtils.prettyHexString(s);
		
		// Verify the result.
		assertThat("Returned string must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.HexUtils#prettyHexString(String)}.
	 */
	@Test
	public final void testPrettyHexStringGeneral() {
		// Setup the resources for the test.
		String s = "1e2589d3a896f47c90b";
		String expectedResult = "01 E2 58 9D 3A 89 6F 47 C9 0B";
		
		// Call the method under test.
		String result = HexUtils.prettyHexString(s);
		
		// Verify the result.
		assertThat("Returned string must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.HexUtils#prettyHexString(byte[])}.
	 */
	@Test
	public final void testPrettyHexStringNullByteArray() {
		// Setup the resources for the test.
		byte[] array = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Value to convert cannot be null.")));
		
		// Call the method under test.
		HexUtils.prettyHexString(array);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.HexUtils#prettyHexString(byte[])}.
	 */
	@Test
	public final void testPrettyHexStringEmptyByteArray() {
		// Setup the resources for the test.
		byte[] array = new byte[0];
		String expectedResult = "";
		
		// Call the method under test.
		String result = HexUtils.prettyHexString(array);
		
		// Verify the result.
		assertThat("Returned string must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.HexUtils#prettyHexString(String)}.
	 */
	@Test
	public final void testPrettyHexStringWithByteArrayGeneral() {
		// Setup the resources for the test.
		byte[] array = new byte[]{0x01, (byte)0xe2, 0x58, (byte)0x9d, 0x3a, (byte)0x89, 0x6f, 0x47, (byte)0xc9, 0x0b};
		String expectedResult = "01 E2 58 9D 3A 89 6F 47 C9 0B";
		
		// Call the method under test.
		String result = HexUtils.prettyHexString(array);
		
		// Verify the result.
		assertThat("Returned string must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.utils.HexUtils#prettyHexString(String)}.
	 */
	@Test
	public final void testPrettyHexStringWithByteArrayNonHexChars() {
		// Setup the resources for the test.
		byte[] array = "tyurw".getBytes();
		String expectedResult = "74 79 75 72 77";
		
		// Call the method under test.
		String result = HexUtils.prettyHexString(array);
		
		// Verify the result.
		assertThat("Returned string must be equal to 'expectedResult'", result, is(equalTo(expectedResult)));
	}
}
