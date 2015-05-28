/**
 * Copyright (c) 2015 Digi International Inc.,
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
import static org.hamcrest.core.IsNot.not;

import static org.junit.Assert.assertThat;

import java.util.LinkedHashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.powermock.api.mockito.PowerMockito;

import com.digi.xbee.api.exceptions.InvalidPacketException;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.SpecialByte;
import com.digi.xbee.api.utils.HexUtils;

public class XBeePacketTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	private static class TestXBeePacket extends XBeePacket {
		
		@Override
		public byte[] getPacketData() {
			return null;
		}

		@Override
		protected LinkedHashMap<String, String> getPacketParameters() {
			return null;
		}
	}
	
	public XBeePacketTest() {
	}

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
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#XBeePacket()}.
	 * 
	 * <p>Construct a new XBee packet.</p>
	 */
	@Test
	public final void testCreateXBeePacket() {
		
		// Call the method under test that should throw a NullPointerException.
		TestXBeePacket packet = new TestXBeePacket();
		
		// Verify the result.
		assertThat("XBee packet cannot be null", packet, is(not(equalTo(null))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#generateByteArray()}.
	 * 
	 * <p>Test the generate byte array method with null data.</p>
	 */
	@Test
	public final void testGenerateByteArrayNullData() {
		// Setup the resources for the test.
		TestXBeePacket packet = new TestXBeePacket();
		byte[] expectedByteArray = new byte[] {(byte)SpecialByte.HEADER_BYTE.getValue(), 0x00, 0x00,(byte)0xFF};

		// Call the method under test.
		byte[] data = packet.generateByteArray();

		// Verify the result.
		assertThat("Returned byte array cannot be null", data, is(not(equalTo(null))));
		assertThat("Returned byte array length is not the expected", data.length, is(equalTo(expectedByteArray.length)));
		assertThat("Returned byte array is not the expected", data, is(equalTo(expectedByteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#generateByteArray()}.
	 * 
	 * <p>Test the generate byte array method with empty data.</p>
	 */
	@Test
	public final void testGenerateByteArrayEmptyData() {
		// Setup the resources for the test.
		TestXBeePacket packet = PowerMockito.spy(new TestXBeePacket());
		PowerMockito.when(packet.getPacketData()).thenReturn(new byte[0]);
		byte[] expectedByteArray = new byte[] {(byte)SpecialByte.HEADER_BYTE.getValue(), 0x00, 0x00,(byte)0xFF};

		// Call the method under test.
		byte[] data = packet.generateByteArray();

		// Verify the result.
		assertThat("Returned byte array cannot be null", data, is(not(equalTo(null))));
		assertThat("Returned byte array length is not the expected", data.length, is(equalTo(expectedByteArray.length)));
		assertThat("Returned byte array is not the expected", data, is(equalTo(expectedByteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#generateByteArray()}.
	 * 
	 * <p>Test the generate byte array method with a valid array data.</p>
	 */
	@Test
	public final void testGenerateByteArray() {
		// Setup the resources for the test.
		byte[] dataArray = new byte[]{0x08, 0x01, 0x4E, 0x49, 0x4E, 0x61, 0x6D, 0x65};
		TestXBeePacket packet = PowerMockito.spy(new TestXBeePacket());
		PowerMockito.when(packet.getPacketData()).thenReturn(dataArray);
		
		byte[] expectedByteArray = new byte[dataArray.length + 4];
		expectedByteArray[0] = (byte)SpecialByte.HEADER_BYTE.getValue();
		expectedByteArray[1] = 0x00;
		expectedByteArray[2] = 0x08;
		System.arraycopy(dataArray, 0, expectedByteArray, 3, dataArray.length);
		expectedByteArray[expectedByteArray.length - 1] = (byte)0xDE;

		// Call the method under test.
		byte[] data = packet.generateByteArray();

		// Verify the result.
		assertThat("Returned byte array cannot be null", data, is(not(equalTo(null))));
		assertThat("Returned byte array length is not the expected", data.length, is(equalTo(expectedByteArray.length)));
		assertThat("Returned byte array is not the expected", data, is(equalTo(expectedByteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#generateByteArrayEscaped()}
	 * 
	 * <p>Test the generate byte array method with null data.</p>
	 */
	@Test
	public final void testGenerateByteArrayEscapedNullData() {
		// Setup the resources for the test.
		TestXBeePacket packet = new TestXBeePacket();
		byte[] expectedByteArray = new byte[] {(byte)SpecialByte.HEADER_BYTE.getValue(), 0x00, 0x00,(byte)0xFF};

		// Call the method under test.
		byte[] data = packet.generateByteArrayEscaped();

		// Verify the result.
		assertThat("Returned byte array cannot be null", data, is(not(equalTo(null))));
		assertThat("Returned byte array length is not the expected", data.length, is(equalTo(expectedByteArray.length)));
		assertThat("Returned byte array is not the expected", data, is(equalTo(expectedByteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#generateByteArray()}.
	 * 
	 * <p>Test the generate byte array method with empty data.</p>
	 */
	@Test
	public final void testGenerateByteArrayEscapedEmptyData() {
		// Setup the resources for the test.
		TestXBeePacket packet = PowerMockito.spy(new TestXBeePacket());
		PowerMockito.when(packet.getPacketData()).thenReturn(new byte[0]);
		byte[] expectedByteArray = new byte[] {(byte)SpecialByte.HEADER_BYTE.getValue(), 0x00, 0x00,(byte)0xFF};

		// Call the method under test.
		byte[] data = packet.generateByteArrayEscaped();

		// Verify the result.
		assertThat("Returned byte array cannot be null", data, is(not(equalTo(null))));
		assertThat("Returned byte array length is not the expected", data.length, is(equalTo(expectedByteArray.length)));
		assertThat("Returned byte array is not the expected", data, is(equalTo(expectedByteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#generateByteArray()}.
	 * 
	 * <p>Test the generate byte array method with a valid array data.</p>
	 */
	@Test
	public final void testGenerateByteArrayEscaped() {
		// Setup the resources for the test.
		byte[] dataArray = new byte[]{0x17, 0x01, 0x00, 0x13, (byte)0xA2, 0x00, 0x40, (byte)0xAD, 0x14, 0x2E, (byte)0xFF, (byte)0xFE, 0x02, 0x4E, 0x49};
		byte[] dataArrayEscaped = new byte[]{0x17, 0x01, 0x00, 0x7D, 0x33, (byte)0xA2, 0x00, 0x40, (byte)0xAD, 0x14, 0x2E, (byte)0xFF, (byte)0xFE, 0x02, 0x4E, 0x49};
		TestXBeePacket packet = PowerMockito.spy(new TestXBeePacket());
		PowerMockito.when(packet.getPacketData()).thenReturn(dataArray);
		
		byte[] expectedByteArray = new byte[dataArrayEscaped.length + 4];
		expectedByteArray[0] = (byte)SpecialByte.HEADER_BYTE.getValue();
		expectedByteArray[1] = 0x00;
		expectedByteArray[2] = 0x0F;
		System.arraycopy(dataArrayEscaped, 0, expectedByteArray, 3, dataArrayEscaped.length);
		expectedByteArray[expectedByteArray.length - 1] = 0x6D;

		// Call the method under test.
		byte[] data = packet.generateByteArrayEscaped();

		// Verify the result.
		assertThat("Returned byte array cannot be null", data, is(not(equalTo(null))));
		assertThat("Returned byte array length is not the expected", data.length, is(equalTo(expectedByteArray.length)));
		assertThat("Returned byte array is not the expected", data, is(equalTo(expectedByteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#getPacketLength()}.
	 * 
	 * <p>Test the get packet length with null data.</p>
	 */
	@Test
	public final void testGetPacketLengthNullData() {
		// Setup the resources for the test.
		TestXBeePacket packet = new TestXBeePacket();
		int expectedLength = 0;
		
		// Call the method under test.
		int length = packet.getPacketLength();
		
		// Verify the result.
		assertThat("The length of the packet is not the expected one", length, is(equalTo(expectedLength)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#getPacketLength()}.
	 * 
	 * <p>Test the get packet length with empty data.</p>
	 */
	@Test
	public final void testGetPacketLengthEmptyData() {
		// Setup the resources for the test.
		TestXBeePacket packet = PowerMockito.spy(new TestXBeePacket());
		PowerMockito.when(packet.getPacketData()).thenReturn(new byte[0]);
		int expectedLength = 0;
		
		// Call the method under test.
		int length = packet.getPacketLength();
		
		// Verify the result.
		assertThat("The length of the packet is not the expected one", length, is(equalTo(expectedLength)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#getPacketLength()}.
	 * 
	 * <p>Test the get packet length with valid data.</p>
	 */
	@Test
	public final void testGetPacketLength() {
		// Setup the resources for the test.
		byte[] dataArray = new byte[]{0x17, 0x01, 0x00, 0x13, (byte)0xA2, 0x00, 0x40, (byte)0xAD, 0x14, 0x2E, (byte)0xFF, (byte)0xFE, 0x02, 0x4E, 0x49};
		TestXBeePacket packet = PowerMockito.spy(new TestXBeePacket());
		PowerMockito.when(packet.getPacketData()).thenReturn(dataArray);
		int expectedLength = dataArray.length;
		
		// Call the method under test.
		int length = packet.getPacketLength();
		
		// Verify the result.
		assertThat("The length of the packet is not the expected one", length, is(equalTo(expectedLength)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#getChecksum()}.
	 * 
	 * <p>Test the get checksum with null data.</p>
	 */
	@Test
	public final void testGetChecksumNullData() {
		// Setup the resources for the test.
		TestXBeePacket packet = new TestXBeePacket();
		int expectedChecksum = (byte)0xFF & 0xFF;
		
		// Call the method under test.
		int checksum = packet.getChecksum();
		
		// Verify the result.
		assertThat("The checksum of the packet is not the expected one", checksum, is(equalTo(expectedChecksum)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#getChecksum()}.
	 * 
	 * <p>Test the get checksum with empty data.</p>
	 */
	@Test
	public final void testGetChecksumEmptyData() {
		// Setup the resources for the test.
		TestXBeePacket packet = PowerMockito.spy(new TestXBeePacket());
		PowerMockito.when(packet.getPacketData()).thenReturn(new byte[0]);
		int expectedChecksum = (byte)0xFF & 0xFF;
		
		// Call the method under test.
		int checksum = packet.getChecksum();
		
		// Verify the result.
		assertThat("The checksum of the packet is not the expected one", checksum, is(equalTo(expectedChecksum)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#getChecksum()}.
	 * 
	 * <p>Test the get packet checksum with valid data.</p>
	 */
	@Test
	public final void testGetChecksum() {
		// Setup the resources for the test.
		byte[] dataArray = new byte[]{0x17, 0x01, 0x00, 0x13, (byte)0xA2, 0x00, 0x40, (byte)0xAD, 0x14, 0x2E, (byte)0xFF, (byte)0xFE, 0x02, 0x4E, 0x49};
		TestXBeePacket packet = PowerMockito.spy(new TestXBeePacket());
		PowerMockito.when(packet.getPacketData()).thenReturn(dataArray);
		int expectedChecksum = 0x6D;
		
		// Call the method under test.
		int checksum = packet.getChecksum();
		
		// Verify the result.
		assertThat("The checksum of the packet is not the expected one", checksum, is(equalTo(expectedChecksum)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#parsePacket(String, com.digi.xbee.api.models.OperatingMode)}.
	 * 
	 * <p>Test parse a null string a {@code NullPointerException} is expected.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketNullString() throws InvalidPacketException {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Packet cannot be null.")));
		
		// Call the method under test.
		XBeePacket.parsePacket((String)null, OperatingMode.API);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#parsePacket(String, com.digi.xbee.api.models.OperatingMode)}.
	 * 
	 * <p>Test parse an empty string an {@code IllegalArgumentException} is expected.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketEmptyString() throws InvalidPacketException {
		// Setup the resources for the test.
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Packet length should be greater than 0.")));
		
		// Call the method under test.
		XBeePacket.parsePacket("", OperatingMode.API_ESCAPE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#parsePacket(String, com.digi.xbee.api.models.OperatingMode)}.
	 * 
	 * <p>Test parse a string not starting with 0x7E an {@code InvalidPacketException} is expected.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketNotStartingWithHeader() throws InvalidPacketException {
		// Setup the resources for the test.
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Invalid start delimiter.")));
		
		// Call the method under test.
		XBeePacket.parsePacket("data", OperatingMode.API_ESCAPE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#parsePacket(String, com.digi.xbee.api.models.OperatingMode)}.
	 * 
	 * <p>Test parse a string with null operating mode an {@code IllegalArgumentException} is expected.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketNullOperatingMode() throws InvalidPacketException {
		// Setup the resources for the test.
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Operating mode must be API or API Escaped.")));
		
		// Call the method under test.
		XBeePacket.parsePacket("data", null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#parsePacket(String, com.digi.xbee.api.models.OperatingMode)}.
	 * 
	 * <p>Test parse a string with UNKNOWN operating mode an {@code IllegalArgumentException} is expected.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketUnknownOperatingMode() throws InvalidPacketException {
		// Setup the resources for the test.
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Operating mode must be API or API Escaped.")));
		
		// Call the method under test.
		XBeePacket.parsePacket("data", OperatingMode.UNKNOWN);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#parsePacket(String, com.digi.xbee.api.models.OperatingMode)}.
	 * 
	 * <p>Test parse a string with AT operating mode an {@code IllegalArgumentException} is expected.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketATOperatingMode() throws InvalidPacketException {
		// Setup the resources for the test.
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Operating mode must be API or API Escaped.")));
		
		// Call the method under test.
		XBeePacket.parsePacket("data", OperatingMode.AT);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#parsePacket(String, com.digi.xbee.api.models.OperatingMode)}.
	 * 
	 * <p>Test parse a string in API using API escaped.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketStringAPIWithAPIEscaped() throws InvalidPacketException {
		// Setup the resources for the test.
		String dataString = "7E000F17010013A20040AD142EFFFE024E496D";
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Special byte not escaped: 0x13.")));
		
		// Call the method under test.
		XBeePacket.parsePacket(dataString, OperatingMode.API_ESCAPE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#parsePacket(String, com.digi.xbee.api.models.OperatingMode)}.
	 * 
	 * <p>Test parse a string in API escaped using API.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketStringAPIEscapedWithAPI() throws InvalidPacketException {
		// Setup the resources for the test.
		String dataString = "7E000F1701007D33A20040AD142EFFFE024E496D";
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Invalid checksum (expected 0x19).")));
		
		// Call the method under test.
		XBeePacket.parsePacket(dataString, OperatingMode.API);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#parsePacket(String, com.digi.xbee.api.models.OperatingMode)}.
	 * 
	 * <p>Test parse a string in API with invalid checksum.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketStringAPIInvalidChecksum() throws InvalidPacketException {
		// Setup the resources for the test.
		String dataString = "7E000F17010013A20040AD142EFFFE024E4969";
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Invalid checksum (expected 0x6D).")));
		
		// Call the method under test.
		XBeePacket.parsePacket(dataString, OperatingMode.API);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#parsePacket(String, com.digi.xbee.api.models.OperatingMode)}.
	 * 
	 * <p>Test parse a string in API escaped with invalid checksum.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketStringAPIEscapedInvalidChecksum() throws InvalidPacketException {
		// Setup the resources for the test.
		String dataString = "7E000F1701007D33A20040AD142EFFFE024E4961";
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Invalid checksum (expected 0x6D).")));
		
		// Call the method under test.
		XBeePacket.parsePacket(dataString, OperatingMode.API_ESCAPE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#parsePacket(String, com.digi.xbee.api.models.OperatingMode)}.
	 * 
	 * <p>Test parse a string in API with invalid length (longer).</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketStringAPIInvalidLengthLonger() throws InvalidPacketException {
		// Setup the resources for the test.
		String dataString = "7E001F17010013A20040AD142EFFFE024E496D";
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Error parsing packet: Incomplete packet.")));
		
		// Call the method under test.
		XBeePacket.parsePacket(dataString, OperatingMode.API);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#parsePacket(String, com.digi.xbee.api.models.OperatingMode)}.
	 * 
	 * <p>Test parse a string in API Escaped with invalid length (longer).</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketStringAPIEscapedInvalidLengthLonger() throws InvalidPacketException {
		// Setup the resources for the test.
		String dataString = "7E001F1701007D33A20040AD142EFFFE024E496D";
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Error parsing packet: Incomplete packet.")));
		
		// Call the method under test.
		XBeePacket.parsePacket(dataString, OperatingMode.API);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#parsePacket(String, com.digi.xbee.api.models.OperatingMode)}.
	 * 
	 * <p>Test parse a string in API with invalid length (shorter).</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketStringAPIInvalidLengthShorter() throws InvalidPacketException {
		// Setup the resources for the test.
		String dataString = "7E000217010013A20040AD142EFFFE024E496D";
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Invalid checksum (expected 0xE7).")));
		
		// Call the method under test.
		XBeePacket.parsePacket(dataString, OperatingMode.API);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#parsePacket(String, com.digi.xbee.api.models.OperatingMode)}.
	 * 
	 * <p>Test parse a string in API with invalid length (shorter).</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketStringAPIEscapedInvalidLengthShorter() throws InvalidPacketException {
		// Setup the resources for the test.
		String dataString = "7E00031701007D33A20040AD142EFFFE024E496D";
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Invalid checksum (expected 0xE7).")));
		
		// Call the method under test.
		XBeePacket.parsePacket(dataString, OperatingMode.API);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#parsePacket(String, com.digi.xbee.api.models.OperatingMode)}.
	 * 
	 * <p>Test parse a string in API valid frame.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketStringAPIValid() throws InvalidPacketException {
		// Setup the resources for the test.
		String dataString = "7E000F17010013A20040AD142EFFFE024E496D";
		
		// Call the method under test.
		XBeePacket packet = XBeePacket.parsePacket(dataString, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet cannot be null", packet, is(not(equalTo(null))));
		assertThat("Packet is not the expected", packet.generateByteArray(), is(equalTo(HexUtils.hexStringToByteArray(dataString))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#parsePacket(String, com.digi.xbee.api.models.OperatingMode)}.
	 * 
	 * <p>Test parse a string in API Escaped valid frame.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketStringAPIEscapedValid() throws InvalidPacketException {
		// Setup the resources for the test.
		String dataString = "7E000F1701007D33A20040AD142EFFFE024E496D";
		
		// Call the method under test.
		XBeePacket packet = XBeePacket.parsePacket(dataString, OperatingMode.API_ESCAPE);
		
		// Verify the result.
		assertThat("Packet cannot be null", packet, is(not(equalTo(null))));
		assertThat("Packet is not the expected", packet.generateByteArrayEscaped(), is(equalTo(HexUtils.hexStringToByteArray(dataString))));
	}
}
