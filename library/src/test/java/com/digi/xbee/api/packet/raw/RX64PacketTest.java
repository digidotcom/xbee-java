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
package com.digi.xbee.api.packet.raw;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

import java.util.Arrays;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

public class RX64PacketTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	public RX64PacketTest() {
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
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64Packet#createPacket(byte[])}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a 
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Setup the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("RX64 packet payload cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		RX64Packet.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64Packet#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Setup the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete RX64 packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		RX64Packet.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64Packet#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Setup the resources for the test.
		int frameType = APIFrameType.RX_64.getValue();
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		int rssi = 40;
		
		byte[] payload = new byte[10];
		payload[0] = (byte)frameType;
		System.arraycopy(source64Addr.getValue(), 0, payload, 1, source64Addr.getValue().length);
		payload[9] = (byte)rssi;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete RX64 packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		RX64Packet.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64Packet#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array not including the Frame type.</p>
	 */
	@Test
	public final void testCreatePacketPayloadNotIncludingFrameType() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		int rssi = 40;
		int options = 0x01;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		byte[] payload = new byte[10 + data.length];
		System.arraycopy(source64Addr.getValue(), 0, payload, 0, source64Addr.getValue().length);
		payload[8] = (byte)rssi;
		payload[9] = (byte)options;
		System.arraycopy(data, 0, payload, 10, data.length);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not a RX64 packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		RX64Packet.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64Packet#createPacket(byte[])}.
	 * 
	 * <p>A valid API RX64 packet with the provided options without RF data is 
	 * created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutData() {
		// Setup the resources for the test.
		int frameType = APIFrameType.RX_64.getValue();
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		int rssi = 40;
		int options = 0x01;
		
		byte[] payload = new byte[11];
		payload[0] = (byte)frameType;
		System.arraycopy(source64Addr.getValue(), 0, payload, 1, source64Addr.getValue().length);
		payload[9] = (byte)rssi;
		payload[10] = (byte)options;
		
		// Call the method under test.
		RX64Packet packet = RX64Packet.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned source 64-bit address is not the expected one", packet.get64bitSourceAddress(), is(equalTo(source64Addr)));
		assertThat("Returned RSSI is not the expected one", packet.getRSSI(), is(equalTo(rssi)));
		assertThat("Returned received options is not the expected one", packet.getReceiveOptions(), is(equalTo(options)));
		assertThat("Returned Received Data is not the expected one", packet.getRFData(), is(nullValue()));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64Packet#createPacket(byte[])}.
	 * 
	 * <p>A valid API RX64 packet with the provided options and RF data is 
	 * created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithData() {
		// Setup the resources for the test.
		int frameType = APIFrameType.RX_64.getValue();
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		int rssi = 40;
		int options = 0x01;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		byte[] payload = new byte[11 + data.length];
		payload[0] = (byte)frameType;
		System.arraycopy(source64Addr.getValue(), 0, payload, 1, source64Addr.getValue().length);
		payload[9] = (byte)rssi;
		payload[10] = (byte)options;
		System.arraycopy(data, 0, payload, 11, data.length);
		
		// Call the method under test.
		RX64Packet packet = RX64Packet.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned source 64-bit address is not the expected one", packet.get64bitSourceAddress(), is(equalTo(source64Addr)));
		assertThat("Returned RSSI is not the expected one", packet.getRSSI(), is(equalTo(rssi)));
		assertThat("Returned received options is not the expected one", packet.getReceiveOptions(), is(equalTo(options)));
		assertThat("Returned Received Data is not the expected one", packet.getRFData(), is(equalTo(data)));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64Packet#RX64Packet(XBee64BitAddress, int, int, byte[])}.
	 * 
	 * <p>Construct a new RX64 packet but with a {@code null} 64-bit address. 
	 * This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateRX64Packet16BitAddressNull() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = null;
		int rssi = 25;
		int options = 40;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("64-bit source address cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new RX64Packet(source64Addr, rssi, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64Packet#RX64Packet(XBee64BitAddress, int, int, byte[])}.
	 * 
	 * <p>Construct a new RX64 packet but with a RSSI bigger than 100. 
	 * This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateRX64PacketRssiBiggerThan100() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		int rssi = 725;
		int options = 40;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("RSSI value must be between 0 and 100.")));
		
		// Call the method under test that should throw a NullPointerException.
		new RX64Packet(source64Addr, rssi, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64Packet#RX64Packet(XBee64BitAddress, int, int, byte[])}.
	 * 
	 * <p>Construct a new RX64 packet but with a negative RSSI. 
	 * This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateRX64PacketRssiNegative() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		int rssi = -5;
		int options = 40;
		byte[] data = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("RSSI value must be between 0 and 100.")));
		
		// Call the method under test that should throw a NullPointerException.
		new RX64Packet(source64Addr, rssi, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64Packet#RX64Packet(XBee64BitAddress, int, int, byte[])}.
	 * 
	 * <p>Construct a new RX64 packet but with a RSSI bigger than 255. 
	 * This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateRX64PacketOptionsBiggerThan255() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		int rssi = 75;
		int options = 863;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Receive options value must be between 0 and 255.")));
		
		// Call the method under test that should throw a NullPointerException.
		new RX64Packet(source64Addr, rssi, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64Packet#RX64Packet(XBee64BitAddress, int, int, byte[])}.
	 * 
	 * <p>Construct a new RX64 packet but with a negative options. 
	 * This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateRX64PacketOptionsNegative() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		int rssi = 75;
		int options = -12;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Receive options value must be between 0 and 255.")));
		
		// Call the method under test that should throw a NullPointerException.
		new RX64Packet(source64Addr, rssi, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64Packet#RX64Packet(XBee64BitAddress, int, int, byte[])}.
	 * 
	 * <p>Construct a new RX64 packet but with {@code null} data.</p>
	 */
	@Test
	public final void testCreateRX64PacketReceiveDataNull() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		int rssi = 75;
		int options = 40;
		byte[] data = null;
		
		int expectedLength = 1 /* Frame type */ + 8 /* 64-bit address */ + 1 /* RSSI */ + 1 /* options */;
		
		// Call the method under test.
		RX64Packet packet = new RX64Packet(source64Addr, rssi, options, data);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned source 64-bit address is not the expected one", packet.get64bitSourceAddress(), is(equalTo(source64Addr)));
		assertThat("Returned RSSI is not the expected one", packet.getRSSI(), is(equalTo(rssi)));
		assertThat("Returned received options is not the expected one", packet.getReceiveOptions(), is(equalTo(options)));
		assertThat("Returned RF Data is not the expected one", packet.getRFData(), is(equalTo(data)));
		assertThat("RX64 packet does NOT need API Frame ID", packet.needsAPIFrameID(), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64Packet#RX64Packet(XBee64BitAddress, int, int, byte[])}.
	 * 
	 * <p>Construct a new RX64 IO packet but with data length less than 5. 
	 * This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateRX64Packet() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		int rssi = 75;
		int options = 40;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		int expectedLength = 1 /* Frame type */ + 8 /* 64-bit address */ + 1 /* RSSI */ + 1 /* options */ + data.length /* Data */;
		
		// Call the method under test.
		RX64Packet packet = new RX64Packet(source64Addr, rssi, options, data);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned source 64-bit address is not the expected one", packet.get64bitSourceAddress(), is(equalTo(source64Addr)));
		assertThat("Returned RSSI is not the expected one", packet.getRSSI(), is(equalTo(rssi)));
		assertThat("Returned received options is not the expected one", packet.getReceiveOptions(), is(equalTo(options)));
		assertThat("Returned RF Data is not the expected one", packet.getRFData(), is(equalTo(data)));
		assertThat("RX64 packet does NOT need API Frame ID", packet.needsAPIFrameID(), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64Packet#getAPIData()}.
	 * 
	 * <p>Test the get API parameters but with a {@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIDataReceivedDataNull() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		int rssi = 75;
		int options = 40;
		byte[] receivedData = null;
		RX64Packet packet = new RX64Packet(source64Addr, rssi, options, receivedData);
		
		int expectedLength = 8 /* 64-bit address */ + 1 /* RSSI */ + 1 /* options */;
		byte[] expectedData = new byte[expectedLength];
		System.arraycopy(source64Addr.getValue(), 0, expectedData, 0, source64Addr.getValue().length);
		expectedData[8] = (byte)rssi;
		expectedData[9] = (byte)options;
		
		// Call the method under test.
		byte[] data = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64Packet#getAPIData()}.
	 * 
	 * <p>Test the get API parameters but with a non-{@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIDataReceivedDataNotNull() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		int rssi = 75;
		int options = 40;
		byte[] receivedData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		RX64Packet packet = new RX64Packet(source64Addr, rssi, options, receivedData);
		
		int expectedLength = 8 /* 64-bit address */ + 1 /* RSSI */ + 1 /* options */ + receivedData.length /* Data */;
		byte[] expectedData = new byte[expectedLength];
		System.arraycopy(source64Addr.getValue(), 0, expectedData, 0, source64Addr.getValue().length);
		expectedData[8] = (byte)rssi;
		expectedData[9] = (byte)options;
		System.arraycopy(receivedData, 0, expectedData, 10, receivedData.length);
		
		// Call the method under test.
		byte[] data = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64Packet#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with {@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersReceivedDataNull() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		int rssi = 75;
		int options = 40;
		byte[] receivedData = null;
		RX64Packet packet = new RX64Packet(source64Addr, rssi, options, receivedData);
		
		String expectedSource64Addr = HexUtils.prettyHexString(source64Addr.getValue());
		String expectedRSSI = HexUtils.prettyHexString(HexUtils.integerToHexString(rssi, 1));
		String expectedOptions = HexUtils.prettyHexString(Integer.toHexString(options));
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(3)));
		assertThat("Source 64-bit Address is not the expected one", packetParams.get("64-bit source address"), is(equalTo(expectedSource64Addr)));
		assertThat("RSSI is not the expected", packetParams.get("RSSI"), is(equalTo(expectedRSSI)));
		assertThat("Options are not the expected", packetParams.get("Options"), is(equalTo(expectedOptions)));
		assertThat("RF data is not the expected", packetParams.get("RF data"), is(nullValue(String.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64Packet#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with non-{@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersReceivedDataNotNull() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		int rssi = 75;
		int options = 40;
		byte[] receivedData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		RX64Packet packet = new RX64Packet(source64Addr, rssi, options, receivedData);
		
		String expectedSource64Addr = HexUtils.prettyHexString(source64Addr.getValue());
		String expectedRSSI = HexUtils.prettyHexString(HexUtils.integerToHexString(rssi, 1));
		String expectedOptions = HexUtils.prettyHexString(Integer.toHexString(options));
		String expectedReceivedData = HexUtils.prettyHexString(HexUtils.byteArrayToHexString(receivedData));
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(4)));
		assertThat("Source 64-bit Address is not the expected one", packetParams.get("64-bit source address"), is(equalTo(expectedSource64Addr)));
		assertThat("RSSI is not the expected", packetParams.get("RSSI"), is(equalTo(expectedRSSI)));
		assertThat("Options are not the expected", packetParams.get("Options"), is(equalTo(expectedOptions)));
		assertThat("Received data is not the expected", packetParams.get("RF data"), is(equalTo(expectedReceivedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64Packet#isBroadcast()}.
	 * 
	 * <p>Test if an API RX64 packet is a broadcast packet when broadcast options 
	 * are not enabled in the options.</p>
	 */
	@Test
	public final void testIsBroadcastWithNonBroadcastOption() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		int rssi = 75;
		int options = 0x18;
		byte[] receivedData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		RX64Packet packet = new RX64Packet(source64Addr, rssi, options, receivedData);
		
		// Call the method under test and verify the result.
		assertThat("Packet should not be broadcast", packet.isBroadcast(), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64Packet#isBroadcast()}.
	 * 
	 * <p>Test if an API RX64 packet is a broadcast packet when broadcast (bit 1 
	 * - Address broadcast) is enabled in the options.</p>
	 */
	@Test
	public final void testIsBroadcastWithBroadcastOptionBit1Enabled() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		int rssi = 75;
		int options = 0xA2; /* bit 1 */
		byte[] receivedData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		RX64Packet packet = new RX64Packet(source64Addr, rssi, options, receivedData);
		
		// Call the method under test and verify the result.
		assertThat("Packet should be broadcast", packet.isBroadcast(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64Packet#isBroadcast()}.
	 * 
	 * <p>Test if an API RX64 packet is a broadcast packet when broadcast (bit 2 
	 * - PAN broadcast) is enabled in the options.</p>
	 */
	@Test
	public final void testIsBroadcastWithBroadcastOptionBit2Enabled() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		int rssi = 75;
		int options = 0xCC; /* bit 2 */
		byte[] receivedData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		RX64Packet packet = new RX64Packet(source64Addr, rssi, options, receivedData);
		
		// Call the method under test and verify the result.
		assertThat("Packet should be broadcast", packet.isBroadcast(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64Packet#getRFData())}.
	 */
	@Test
	public final void testGetRFDataNullData() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		int rssi = 75;
		int options = 0x14; /* bit 2 */
		byte[] receivedData = null;
		RX64Packet packet = new RX64Packet(source64Addr, rssi, options, receivedData);
		
		// Call the method under test.
		byte[] result = packet.getRFData();
		
		// Verify the result.
		assertThat("RF Data must be the same", result, is(equalTo(receivedData)));
		assertThat("RF Data must be null", result, is(nullValue(byte[].class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64Packet#getRFData())}.
	 */
	@Test
	public final void testGetRFDataValidData() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		int rssi = 75;
		int options = 0x14; /* bit 2 */
		byte[] receivedData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		RX64Packet packet = new RX64Packet(source64Addr, rssi, options, receivedData);
		
		// Call the method under test.
		byte[] result = packet.getRFData();
		
		// Verify the result.
		assertThat("RF Data must be the same", result, is(equalTo(receivedData)));
		assertThat("RF Data must not be the same object", result.hashCode(), is(not(equalTo(receivedData.hashCode()))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64Packet#setRFData(byte[])}.
	 */
	@Test
	public final void testSetRFDataNullData() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		int rssi = 75;
		int options = 0x14; /* bit 2 */
		byte[] origData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		byte[] receivedData = null;
		RX64Packet packet = new RX64Packet(source64Addr, rssi, options, origData);
		
		// Call the method under test.
		packet.setRFData(receivedData);
		
		byte[] result = packet.getRFData();
		
		// Verify the result.
		assertThat("RF Data must be the same", result, is(equalTo(receivedData)));
		assertThat("RF Data must be null", result, is(nullValue(byte[].class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64Packet#setRFData(byte[])}.
	 */
	@Test
	public final void testSetRFDataValidData() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		int rssi = 75;
		int options = 0x84; /* bit 2 */
		byte[] origData = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		byte[] receivedData = new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
		RX64Packet packet = new RX64Packet(source64Addr, rssi, options, origData);
		
		// Call the method under test.
		packet.setRFData(receivedData);
		
		byte[] result = packet.getRFData();
		
		// Verify the result.
		assertThat("RF Data must be the same", result, is(equalTo(receivedData)));
		assertThat("RF Data must not be the same object", result.hashCode(), is(not(equalTo(receivedData.hashCode()))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64Packet#setRFData(byte[])}.
	 */
	@Test
	public final void testSetRFDataAndModifyOriginal() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		int rssi = 75;
		int options = 0x84; /* bit 2 */
		byte[] origData = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		byte[] receivedData = new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
		RX64Packet packet = new RX64Packet(source64Addr, rssi, options, origData);
		
		// Call the method under test.
		packet.setRFData(receivedData);
		byte[] backup = Arrays.copyOf(receivedData, receivedData.length);
		receivedData[0] = 0x11;
		receivedData[1] = 0x12;
		
		byte[] result = packet.getRFData();
		
		// Verify the result.
		assertThat("RF Data must be the same as the setted data", result, is(equalTo(backup)));
		assertThat("RF Data must not be the current value of received data", result, is(not(equalTo(receivedData))));
		assertThat("RF Data must not be the same object", result.hashCode(), is(not(equalTo(backup.hashCode()))));
		assertThat("RF Data must not be the same object", result.hashCode(), is(not(equalTo(receivedData.hashCode()))));
	}
}
