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
		TestXBeePacket packet = new TestXBeePacket() {
			public byte[] getPacketData() {
				return new byte[0];
			}
		};
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
		final byte[] dataArray = new byte[]{0x08, 0x01, 0x4E, 0x49, 0x4E, 0x61, 0x6D, 0x65};
		TestXBeePacket packet = new TestXBeePacket() {
			public byte[] getPacketData() {
				return dataArray;
			}
		};
		
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
		TestXBeePacket packet = new TestXBeePacket() {
			public byte[] getPacketData() {
				return new byte[0];
			}
		};
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
		final byte[] dataArray = new byte[]{0x17, 0x01, 0x00, 0x13, (byte)0xA2, 0x00, 0x40, (byte)0xAD, 0x14, 0x2E, (byte)0xFF, (byte)0xFE, 0x02, 0x4E, 0x49};
		byte[] dataArrayEscaped = new byte[]{0x17, 0x01, 0x00, 0x7D, 0x33, (byte)0xA2, 0x00, 0x40, (byte)0xAD, 0x14, 0x2E, (byte)0xFF, (byte)0xFE, 0x02, 0x4E, 0x49};
		TestXBeePacket packet = new TestXBeePacket() {
			public byte[] getPacketData() {
				return dataArray;
			}
		};
		
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
		TestXBeePacket packet = new TestXBeePacket() {
			public byte[] getPacketData() {
				return new byte[0];
			}
		};
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
		final byte[] dataArray = new byte[]{0x17, 0x01, 0x00, 0x13, (byte)0xA2, 0x00, 0x40, (byte)0xAD, 0x14, 0x2E, (byte)0xFF, (byte)0xFE, 0x02, 0x4E, 0x49};
		TestXBeePacket packet = new TestXBeePacket() {
			public byte[] getPacketData() {
				return dataArray;
			}
		};
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
		TestXBeePacket packet = new TestXBeePacket() {
			public byte[] getPacketData() {
				return new byte[0];
			}
		};
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
		final byte[] dataArray = new byte[]{0x17, 0x01, 0x00, 0x13, (byte)0xA2, 0x00, 0x40, (byte)0xAD, 0x14, 0x2E, (byte)0xFF, (byte)0xFE, 0x02, 0x4E, 0x49};
		TestXBeePacket packet = new TestXBeePacket() {
			public byte[] getPacketData() {
				return dataArray;
			}
		};
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
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#parsePacket(byte[], com.digi.xbee.api.models.OperatingMode)}.
	 * 
	 * <p>Test parse a null byte array a {@code NullPointerException} is expected.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketNullByteArray() throws InvalidPacketException {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Packet byte array cannot be null.")));
		
		// Call the method under test.
		XBeePacket.parsePacket((byte[])null, OperatingMode.API);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#parsePacket(byte[], com.digi.xbee.api.models.OperatingMode)}.
	 * 
	 * <p>Test parse an array with UNKNOWN operating mode an {@code IllegalArgumentException} is expected.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketByteArrayUnknownOperatingMode() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteArray = {0x7E, 0x00, 0x08, 0x08, 0x01, 0x4E, 0x49, 0x41, 0x54, 0x49, (byte)0x81, (byte)0x7E};
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Operating mode must be API or API Escaped.")));
		
		// Call the method under test.
		XBeePacket.parsePacket(byteArray, OperatingMode.UNKNOWN);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#parsePacket(byte[], com.digi.xbee.api.models.OperatingMode)}.
	 * 
	 * <p>Test parse an array with AT operating mode an {@code IllegalArgumentException} is expected.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketByteArrayATOperatingMode() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteArray = {0x7E, 0x00, 0x08, 0x08, 0x01, 0x4E, 0x49, 0x41, 0x54, 0x49, (byte)0x81, (byte)0x7E};
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Operating mode must be API or API Escaped.")));
		
		// Call the method under test.
		XBeePacket.parsePacket(byteArray, OperatingMode.AT);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#parsePacket(byte[], com.digi.xbee.api.models.OperatingMode)}.
	 * 
	 * <p>Test parse an empty byte array an {@code IllegalArgumentException} is expected.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketEmptyByteArray() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteArray = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Packet length should be greater than 0.")));
		
		// Call the method under test.
		XBeePacket.parsePacket(byteArray, OperatingMode.API);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#parsePacket(byte[], com.digi.xbee.api.models.OperatingMode)}.
	 * 
	 * <p>Test parse a string in API with invalid length (shorter).</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketOnlyOneByte() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteArray = {0x66};
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Error parsing packet: Incomplete packet.")));
		
		// Call the method under test that should throw a InvalidPacketException.
		XBeePacket.parsePacket(byteArray, OperatingMode.API);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#parsePacket(byte[], com.digi.xbee.api.models.OperatingMode)}.
	 * 
	 * <p>Test parse a string in API with invalid length (shorter).</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketOnlyTwoByte() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteArray = {0x7E, (byte)0xFF};
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Error parsing packet: Incomplete packet.")));
		
		// Call the method under test that should throw a InvalidPacketException.
		XBeePacket.parsePacket(byteArray, OperatingMode.API);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#parsePacket(byte[], com.digi.xbee.api.models.OperatingMode)}.
	 * 
	 * <p>An {@code InvalidPacketException} exception must be thrown when the 
	 * frame to parse is not starting with the header byte.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketNotHeaderByte() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteArray = {0x68, 0x00, 0x08, 0x08, 0x01, 0x4E, 0x49, 0x41, 0x54, 0x49, (byte)0x81, (byte)0x7E};
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Invalid start delimiter.")));
		
		// Call the method under test that should throw a InvalidPacketException.
		XBeePacket.parsePacket(byteArray, OperatingMode.API);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#equals(Object)}.
	 * 
	 * <p>Test the equals method with a {@code null} value.</p>
	 */
	@Test
	public final void testEqualsWithNull() {
		// Setup the resources for the test.
		final byte[] dataArray = new byte[]{0x17, 0x01, 0x00, 0x13, (byte)0xA2, 0x00, 0x40, (byte)0xAD, 0x14, 0x2E, (byte)0xFF, (byte)0xFE, 0x02, 0x4E, 0x49};
		TestXBeePacket packet = new TestXBeePacket() {
			public byte[] getPacketData() {
				return dataArray;
			}
		};
		
		// Call the method under test.
		boolean areEqual = packet.equals(null);
		
		// Verify the result.
		assertThat("Packet cannot be equal to null", areEqual, is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#equals(Object)}.
	 * 
	 * <p>Test the equals method with a non {@code XBeePacket} value.</p>
	 */
	@Test
	public final void testEqualsWithNonXBeePacket() {
		// Setup the resources for the test.
		final byte[] dataArray = new byte[]{0x17, 0x01, 0x00, 0x13, (byte)0xA2, 0x00, 0x40, (byte)0xAD, 0x14, 0x2E, (byte)0xFF, (byte)0xFE, 0x02, 0x4E, 0x49};
		TestXBeePacket packet = new TestXBeePacket() {
			public byte[] getPacketData() {
				return dataArray;
			}
		};
		
		// Call the method under test.
		boolean areEqual = packet.equals(new Object());
		
		// Verify the result.
		assertThat("Packet cannot be equal to an Object", areEqual, is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#equals(Object)}.
	 * 
	 * <p>Test the equals method with different {@code XBeePacket}.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testEqualsWithDifferentXBeePacket() throws InvalidPacketException {
		// Setup the resources for the test.
		String dataString = "7E000808014E496E616D65BE";
		XBeePacket packet1 = XBeePacket.parsePacket(dataString, OperatingMode.API);
		
		final byte[] dataArray = new byte[]{0x17, 0x01, 0x00, 0x13, (byte)0xA2, 0x00, 0x40, (byte)0xAD, 0x14, 0x2E, (byte)0xFF, (byte)0xFE, 0x02, 0x4E, 0x49};
		TestXBeePacket packet2 = new TestXBeePacket() {
			public byte[] getPacketData() {
				return dataArray;
			}
		};
		
		// Call the method under test.
		boolean areEqual1 = packet1.equals(packet2);
		boolean areEqual2 = packet2.equals(packet1);
		
		// Verify the result.
		assertThat("Packet1 must be different from packet2", areEqual1, is(equalTo(false)));
		assertThat("Packet2 must be different from packet1", areEqual2, is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#equals(Object)}.
	 * 
	 * <p>Test the equals method with equal {@code XBeePacket}.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testEqualsIsSymetric() throws InvalidPacketException {
		// Setup the resources for the test.
		String dataString = "7E000F17010013A20040AD142EFFFE024E496D";
		XBeePacket packet1 = XBeePacket.parsePacket(dataString, OperatingMode.API);
		
		final byte[] dataArray = new byte[]{0x17, 0x01, 0x00, 0x13, (byte)0xA2, 0x00, 0x40, (byte)0xAD, 0x14, 0x2E, (byte)0xFF, (byte)0xFE, 0x02, 0x4E, 0x49};
		TestXBeePacket packet2 = new TestXBeePacket() {
			public byte[] getPacketData() {
				return dataArray;
			}
		};
		
		// Call the method under test.
		boolean areEqual1 = packet1.equals(packet2);
		boolean areEqual2 = packet2.equals(packet1);
		
		// Verify the result.
		assertThat("Packet1 must be equal to packet2", areEqual1, is(equalTo(true)));
		assertThat("Packet2 must be equal to packet1", areEqual2, is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#equals(Object)}.
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testEqualsIsReflexive() throws InvalidPacketException {
		// Setup the resources for the test.
		String dataString = "7E000F17010013A20040AD142EFFFE024E496D";
		XBeePacket packet = XBeePacket.parsePacket(dataString, OperatingMode.API);
		
		// Call the method under test.
		boolean areEqual = packet.equals(packet);
		
		// Verify the result.
		assertThat("Packet must be equal to itself", areEqual, is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#equals(Object)}.
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testEqualsIsTransitive() throws InvalidPacketException {
		// Setup the resources for the test.
		String dataString = "7E000F17010013A20040AD142EFFFE024E496D";
		XBeePacket packet1 = XBeePacket.parsePacket(dataString, OperatingMode.API);
		
		final byte[] dataArray = new byte[]{0x17, 0x01, 0x00, 0x13, (byte)0xA2, 0x00, 0x40, (byte)0xAD, 0x14, 0x2E, (byte)0xFF, (byte)0xFE, 0x02, 0x4E, 0x49};
		TestXBeePacket packet2 = new TestXBeePacket() {
			public byte[] getPacketData() {
				return dataArray;
			}
		};
		
		byte[] data = new byte[]{0x7E, 0x00, 0x0F, 0x17, 0x01, 0x00, 0x13, (byte)0xA2, 0x00, 0x40, (byte)0xAD, 0x14, 0x2E, (byte)0xFF, (byte)0xFE, 0x02, 0x4E, 0x49, 0x6D};
		XBeePacket packet3 = XBeePacket.parsePacket(data, OperatingMode.API);
		
		// Call the method under test.
		boolean areEqual1 = packet1.equals(packet2);
		boolean areEqual2 = packet2.equals(packet3);
		boolean areEqual3 = packet1.equals(packet3);
		
		// Verify the result.
		assertThat("Packet1 must be equal to packet2", areEqual1, is(equalTo(true)));
		assertThat("Packet2 must be equal to packet3", areEqual2, is(equalTo(true)));
		assertThat("Packet1 must be equal to packet3", areEqual3, is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#equals(Object)}.
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testEqualsIsConsistent() throws InvalidPacketException {
		// Setup the resources for the test.
		String dataString = "7E000F17010013A20040AD142EFFFE024E496D";
		XBeePacket packet1 = XBeePacket.parsePacket(dataString, OperatingMode.API);
		
		final byte[] dataArray = new byte[]{0x17, 0x01, 0x00, 0x13, (byte)0xA2, 0x00, 0x40, (byte)0xAD, 0x14, 0x2E, (byte)0xFF, (byte)0xFE, 0x02, 0x4E, 0x49};
		TestXBeePacket packet2 = new TestXBeePacket() {
			public byte[] getPacketData() {
				return dataArray;
			}
		};
		
		byte[] data = new byte[]{0x7E, 0x00, 0x08, 0x08, 0x01, 0x4E, 0x49, 0x6E, 0x61, 0x6D, 0x65, (byte)0xBE};
		XBeePacket packet3 = XBeePacket.parsePacket(data, OperatingMode.API);
		
		// Verify the result.
		assertThat("Consistent test fail packet1,packet2", packet1.equals(packet2), is(equalTo(true)));
		assertThat("Consistent test fail packet1,packet2", packet1.equals(packet2), is(equalTo(true)));
		assertThat("Consistent test fail packet1,packet2", packet1.equals(packet2), is(equalTo(true)));
		assertThat("Consistent test fail packet3,packet1", packet3.equals(packet1), is(equalTo(false)));
		assertThat("Consistent test fail packet3,packet1", packet3.equals(packet1), is(equalTo(false)));
		assertThat("Consistent test fail packet3,packet1", packet3.equals(packet1), is(equalTo(false)));

	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#hashCode()}.
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testHashCodeWithEqualPackets() throws InvalidPacketException {
		// Setup the resources for the test.
		String dataString = "7E000F17010013A20040AD142EFFFE024E496D";
		XBeePacket packet1 = XBeePacket.parsePacket(dataString, OperatingMode.API);
		
		final byte[] dataArray = new byte[]{0x17, 0x01, 0x00, 0x13, (byte)0xA2, 0x00, 0x40, (byte)0xAD, 0x14, 0x2E, (byte)0xFF, (byte)0xFE, 0x02, 0x4E, 0x49};
		TestXBeePacket packet2 = new TestXBeePacket() {
			public byte[] getPacketData() {
				return dataArray;
			}
		};
		
		// Call the method under test.
		int hashPacket1 = packet1.hashCode();
		int hashPacket2 = packet2.hashCode();
		
		// Verify the result.
		assertThat("Packet1 must be equal to packet2", packet1.equals(packet2), is(equalTo(true)));
		assertThat("Packet2 must be equal to packet1", packet2.equals(packet1), is(equalTo(true)));
		assertThat("Hash codes must be equal", hashPacket1, is(equalTo(hashPacket2)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#hashCode()}.
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testHashCodeWithDifferentPackets() throws InvalidPacketException {
		// Setup the resources for the test.
		String dataString = "7E000808014E496E616D65BE";
		XBeePacket packet1 = XBeePacket.parsePacket(dataString, OperatingMode.API);
		
		final byte[] dataArray = new byte[]{0x17, 0x01, 0x00, 0x13, (byte)0xA2, 0x00, 0x40, (byte)0xAD, 0x14, 0x2E, (byte)0xFF, (byte)0xFE, 0x02, 0x4E, 0x49};
		TestXBeePacket packet2 = new TestXBeePacket() {
			public byte[] getPacketData() {
				return dataArray;
			}
		};
		
		// Call the method under test.
		int hashPacket1 = packet1.hashCode();
		int hashPacket2 = packet2.hashCode();
		
		// Verify the result.
		assertThat("Packet1 must be different from packet2", packet1.equals(packet2), is(equalTo(false)));
		assertThat("Packet2 must be different from to packet1", packet2.equals(packet1), is(equalTo(false)));
		assertThat("Hash codes must be different", hashPacket1, is(not(equalTo(hashPacket2))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacket#hashCode()}.
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testHashCodeIsConsistent() throws InvalidPacketException {
		// Setup the resources for the test.
		String dataString = "7E000808014E496E616D65BE";
		XBeePacket packet = XBeePacket.parsePacket(dataString, OperatingMode.API);
		
		int initialHashCode = packet.hashCode();
		
		// Verify the result.
		assertThat("Consistent hashcode test fails", packet.hashCode(), is(equalTo(initialHashCode)));
		assertThat("Consistent hashcode test fails", packet.hashCode(), is(equalTo(initialHashCode)));
	}
}
