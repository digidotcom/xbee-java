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

import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

public class RX16PacketTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	public RX16PacketTest() {
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
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16Packet#createPacket(byte[])}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a 
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Setup the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("RX16 packet payload cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		RX16Packet.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16Packet#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Setup the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete RX16 packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		RX16Packet.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16Packet#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Setup the resources for the test.
		int frameType = APIFrameType.RX_16.getValue();
		XBee16BitAddress source16Addr = new XBee16BitAddress("A1B2");
		int rssi = 40;
		
		byte[] payload = new byte[4];
		payload[0] = (byte)frameType;
		System.arraycopy(source16Addr.getValue(), 0, payload, 1, source16Addr.getValue().length);
		payload[3] = (byte)rssi;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete RX16 packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		RX16Packet.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16Packet#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array not including the Frame type.</p>
	 */
	@Test
	public final void testCreatePacketPayloadNotIncludingFrameType() {
		// Setup the resources for the test.
		XBee16BitAddress source16Addr = new XBee16BitAddress("A1B2");
		int rssi = 40;
		int options = 0x01;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		byte[] payload = new byte[4 + data.length];
		System.arraycopy(source16Addr.getValue(), 0, payload, 0, source16Addr.getValue().length);
		payload[2] = (byte)rssi;
		payload[3] = (byte)options;
		System.arraycopy(data, 0, payload, 4, data.length);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not a RX16 packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		RX16Packet.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16Packet#createPacket(byte[])}.
	 * 
	 * <p>A valid API RX16 packet with the provided options without RF data 
	 * is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutData() {
		// Setup the resources for the test.
		int frameType = APIFrameType.RX_16.getValue();
		XBee16BitAddress source16Addr = new XBee16BitAddress("A1B2");
		int rssi = 40;
		int options = 0x01;
		
		byte[] payload = new byte[5];
		payload[0] = (byte)frameType;
		System.arraycopy(source16Addr.getValue(), 0, payload, 1, source16Addr.getValue().length);
		payload[3] = (byte)rssi;
		payload[4] = (byte)options;
		
		// Call the method under test.
		RX16Packet packet = RX16Packet.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned source 16-bit address is not the expected one", packet.get16bitSourceAddress(), is(equalTo(source16Addr)));
		assertThat("Returned RSSI is not the expected one", packet.getRSSI(), is(equalTo(rssi)));
		assertThat("Returned received options is not the expected one", packet.getReceiveOptions(), is(equalTo(options)));
		assertThat("Returned Received Data is not the expected one", packet.getRFData(), is(nullValue()));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16Packet#createPacket(byte[])}.
	 * 
	 * <p>A valid API RX16 packet with the provided options and RF data is 
	 * created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithData() {
		// Setup the resources for the test.
		int frameType = APIFrameType.RX_16.getValue();
		XBee16BitAddress source16Addr = new XBee16BitAddress("A1B2");
		int rssi = 40;
		int options = 0x01;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		byte[] payload = new byte[5 + data.length];
		payload[0] = (byte)frameType;
		System.arraycopy(source16Addr.getValue(), 0, payload, 1, source16Addr.getValue().length);
		payload[3] = (byte)rssi;
		payload[4] = (byte)options;
		System.arraycopy(data, 0, payload, 5, data.length);
		
		// Call the method under test.
		RX16Packet packet = RX16Packet.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned source 16-bit address is not the expected one", packet.get16bitSourceAddress(), is(equalTo(source16Addr)));
		assertThat("Returned RSSI is not the expected one", packet.getRSSI(), is(equalTo(rssi)));
		assertThat("Returned received options is not the expected one", packet.getReceiveOptions(), is(equalTo(options)));
		assertThat("Returned Received Data is not the expected one", packet.getRFData(), is(equalTo(data)));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16Packet#RX16Packet(XBee16BitAddress, int, int, byte[])}.
	 * 
	 * <p>Construct a new RX16 packet but with a {@code null} 16-bit address. 
	 * This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateRX16Packet16BitAddressNull() {
		// Setup the resources for the test.
		XBee16BitAddress source16Addr = null;
		int rssi = 25;
		int options = 40;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("16-bit source address cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new RX16Packet(source16Addr, rssi, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16Packet#RX16Packet(XBee16BitAddress, int, int, byte[])}.
	 * 
	 * <p>Construct a new RX16 packet but with a RSSI bigger than 100. 
	 * This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateRX16PacketRssiBiggerThan100() {
		// Setup the resources for the test.
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int rssi = 725;
		int options = 40;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("RSSI value must be between 0 and 100.")));
		
		// Call the method under test that should throw a NullPointerException.
		new RX16Packet(source16Addr, rssi, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16Packet#RX16Packet(XBee16BitAddress, int, int, byte[])}.
	 * 
	 * <p>Construct a new RX16 packet but with a negative RSSI. 
	 * This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateRX16PacketRssiNegative() {
		// Setup the resources for the test.
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int rssi = -5;
		int options = 40;
		byte[] data = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("RSSI value must be between 0 and 100.")));
		
		// Call the method under test that should throw a NullPointerException.
		new RX16Packet(source16Addr, rssi, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16Packet#RX16Packet(XBee16BitAddress, int, int, byte[])}.
	 * 
	 * <p>Construct a new RX16 packet but with a RSSI bigger than 255. 
	 * This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateRX16PacketOptionsBiggerThan255() {
		// Setup the resources for the test.
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int rssi = 75;
		int options = 863;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Receive options value must be between 0 and 255.")));
		
		// Call the method under test that should throw a NullPointerException.
		new RX16Packet(source16Addr, rssi, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16Packet#RX16Packet(XBee16BitAddress, int, int, byte[])}.
	 * 
	 * <p>Construct a new RX16 packet but with a negative options. 
	 * This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateRX16PacketOptionsNegative() {
		// Setup the resources for the test.
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int rssi = 75;
		int options = -12;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Receive options value must be between 0 and 255.")));
		
		// Call the method under test that should throw a NullPointerException.
		new RX16Packet(source16Addr, rssi, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16Packet#RX16Packet(XBee16BitAddress, int, int, byte[])}.
	 * 
	 * <p>Construct a new RX16 packet but with {@code null} data.</p>
	 */
	@Test
	public final void testCreateRX16PacketReceiveDataNull() {
		// Setup the resources for the test.
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int rssi = 75;
		int options = 40;
		byte[] data = null;
		
		int expectedLength = 1 /* Frame type */ + 2 /* 16-bit address */ + 1 /* RSSI */ + 1 /* options */;
		
		// Call the method under test.
		RX16Packet packet = new RX16Packet(source16Addr, rssi, options, data);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned source 16-bit address is not the expected one", packet.get16bitSourceAddress(), is(equalTo(source16Addr)));
		assertThat("Returned RSSI is not the expected one", packet.getRSSI(), is(equalTo(rssi)));
		assertThat("Returned received options is not the expected one", packet.getReceiveOptions(), is(equalTo(options)));
		assertThat("Returned RF Data is not the expected one", packet.getRFData(), is(equalTo(data)));
		assertThat("RX16 packet does NOT need API Frame ID", packet.needsAPIFrameID(), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16Packet#RX16Packet(XBee16BitAddress, int, int, byte[])}.
	 * 
	 * <p>Construct a new RX16 IO packet but with data length less than 5. 
	 * This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateRX16Packet() {
		// Setup the resources for the test.
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int rssi = 75;
		int options = 40;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		int expectedLength = 1 /* Frame type */ + 2 /* 16-bit address */ + 1 /* RSSI */ + 1 /* options */ + data.length /* Data */;
		
		// Call the method under test.
		RX16Packet packet = new RX16Packet(source16Addr, rssi, options, data);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned source 16-bit address is not the expected one", packet.get16bitSourceAddress(), is(equalTo(source16Addr)));
		assertThat("Returned RSSI is not the expected one", packet.getRSSI(), is(equalTo(rssi)));
		assertThat("Returned received options is not the expected one", packet.getReceiveOptions(), is(equalTo(options)));
		assertThat("Returned RF Data is not the expected one", packet.getRFData(), is(equalTo(data)));
		assertThat("RX16 packet does NOT need API Frame ID", packet.needsAPIFrameID(), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16Packet#getAPIData()}.
	 * 
	 * <p>Test the get API parameters but with a {@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIDataReceivedDataNull() {
		// Setup the resources for the test.
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int rssi = 75;
		int options = 40;
		byte[] receivedData = null;
		RX16Packet packet = new RX16Packet(source16Addr, rssi, options, receivedData);
		
		int expectedLength = 2 /* 16-bit address */ + 1 /* RSSI */ + 1 /* options */;
		byte[] expectedData = new byte[expectedLength];
		System.arraycopy(source16Addr.getValue(), 0, expectedData, 0, source16Addr.getValue().length);
		expectedData[2] = (byte)rssi;
		expectedData[3] = (byte)options;
		
		// Call the method under test.
		byte[] data = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16Packet#getAPIData()}.
	 * 
	 * <p>Test the get API parameters but with a non-{@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIDataReceivedDataNotNull() {
		// Setup the resources for the test.
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int rssi = 75;
		int options = 40;
		byte[] receivedData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		RX16Packet packet = new RX16Packet(source16Addr, rssi, options, receivedData);
		
		int expectedLength = 2 /* 16-bit address */ + 1 /* RSSI */ + 1 /* options */ + receivedData.length /* Data */;
		byte[] expectedData = new byte[expectedLength];
		System.arraycopy(source16Addr.getValue(), 0, expectedData, 0, source16Addr.getValue().length);
		expectedData[2] = (byte)rssi;
		expectedData[3] = (byte)options;
		System.arraycopy(receivedData, 0, expectedData, 4, receivedData.length);
		
		// Call the method under test.
		byte[] data = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16Packet#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with {@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersReceivedDataNull() {
		// Setup the resources for the test.
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int rssi = 75;
		int options = 40;
		byte[] receivedData = null;
		RX16Packet packet = new RX16Packet(source16Addr, rssi, options, receivedData);
		
		String expectedSource16Addr = HexUtils.prettyHexString(source16Addr.getValue());
		String expectedRSSI = HexUtils.prettyHexString(HexUtils.integerToHexString(rssi, 1));
		String expectedOptions = HexUtils.prettyHexString(Integer.toHexString(options));
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(3)));
		assertThat("Source 16-bit Address is not the expected one", packetParams.get("16-bit source address"), is(equalTo(expectedSource16Addr)));
		assertThat("RSSI is not the expected", packetParams.get("RSSI"), is(equalTo(expectedRSSI)));
		assertThat("Options are not the expected", packetParams.get("Options"), is(equalTo(expectedOptions)));
		assertThat("RF data is not the expected", packetParams.get("RF data"), is(nullValue(String.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16Packet#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with non-{@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersReceivedDataNotNull() {
		// Setup the resources for the test.
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int rssi = 75;
		int options = 40;
		byte[] receivedData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		RX16Packet packet = new RX16Packet(source16Addr, rssi, options, receivedData);
		
		String expectedSource16Addr = HexUtils.prettyHexString(source16Addr.getValue());
		String expectedRSSI = HexUtils.prettyHexString(HexUtils.integerToHexString(rssi, 1));
		String expectedOptions = HexUtils.prettyHexString(Integer.toHexString(options));
		String expectedReceivedData = HexUtils.prettyHexString(HexUtils.byteArrayToHexString(receivedData));
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(4)));
		assertThat("Source 16-bit Address is not the expected one", packetParams.get("16-bit source address"), is(equalTo(expectedSource16Addr)));
		assertThat("RSSI is not the expected", packetParams.get("RSSI"), is(equalTo(expectedRSSI)));
		assertThat("Options are not the expected", packetParams.get("Options"), is(equalTo(expectedOptions)));
		assertThat("Received data is not the expected", packetParams.get("RF data"), is(equalTo(expectedReceivedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16Packet#isBroadcast()}.
	 * 
	 * <p>Test if a RX16 packet is a broadcast packet when broadcast options 
	 * are not enabled in the options.</p>
	 */
	@Test
	public final void testIsBroadcastWithNonBroadcastOption() {
		// Setup the resources for the test.
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int rssi = 75;
		int options = 0x00;
		byte[] receivedData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		RX16Packet packet = new RX16Packet(source16Addr, rssi, options, receivedData);
		
		// Call the method under test and verify the result.
		assertThat("Packet should not be broadcast", packet.isBroadcast(), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16Packet#isBroadcast()}.
	 * 
	 * <p>Test if a RX16 packet is a broadcast packet when broadcast (bit 1 - 
	 * Address broadcast) is enabled in the options.</p>
	 */
	@Test
	public final void testIsBroadcastWithBroadcastOptionBit1Enabled() {
		// Setup the resources for the test.
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int rssi = 75;
		int options = 0x52; /* bit 1 */
		byte[] receivedData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		RX16Packet packet = new RX16Packet(source16Addr, rssi, options, receivedData);
		
		// Call the method under test and verify the result.
		assertThat("Packet should be broadcast", packet.isBroadcast(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16Packet#isBroadcast()}.
	 * 
	 * <p>Test if a RX16 packet is a broadcast packet when broadcast (bit 2 - 
	 * PAN broadcast) is enabled in the options.</p>
	 */
	@Test
	public final void testIsBroadcastWithBroadcastOptionBit2Enabled() {
		// Setup the resources for the test.
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int rssi = 75;
		int options = 0x14; /* bit 2 */
		byte[] receivedData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		RX16Packet packet = new RX16Packet(source16Addr, rssi, options, receivedData);
		
		// Call the method under test and verify the result.
		assertThat("Packet should be broadcast", packet.isBroadcast(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16Packet#getRFData())}.
	 */
	@Test
	public final void testGetRFDataNullData() {
		// Setup the resources for the test.
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int rssi = 75;
		int options = 0x14; /* bit 2 */
		byte[] receivedData = null;
		RX16Packet packet = new RX16Packet(source16Addr, rssi, options, receivedData);
		
		// Call the method under test.
		byte[] result = packet.getRFData();
		
		// Verify the result.
		assertThat("RF Data must be the same", result, is(equalTo(receivedData)));
		assertThat("RF Data must be null", result, is(nullValue(byte[].class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16Packet#getRFData())}.
	 */
	@Test
	public final void testGetRFDataValidData() {
		// Setup the resources for the test.
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int rssi = 75;
		int options = 0x14; /* bit 2 */
		byte[] receivedData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		RX16Packet packet = new RX16Packet(source16Addr, rssi, options, receivedData);
		
		// Call the method under test.
		byte[] result = packet.getRFData();
		
		// Verify the result.
		assertThat("RF Data must be the same", result, is(equalTo(receivedData)));
		assertThat("RF Data must not be the same object", result.hashCode(), is(not(equalTo(receivedData.hashCode()))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16Packet#setRFData(byte[])}.
	 */
	@Test
	public final void testSetRFDataNullData() {
		// Setup the resources for the test.
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int rssi = 75;
		int options = 0x14; /* bit 2 */
		byte[] origData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		byte[] receivedData = null;
		RX16Packet packet = new RX16Packet(source16Addr, rssi, options, origData);
		
		// Call the method under test.
		packet.setRFData(receivedData);
		
		byte[] result = packet.getRFData();
		
		// Verify the result.
		assertThat("RF Data must be the same", result, is(equalTo(receivedData)));
		assertThat("RF Data must be null", result, is(nullValue(byte[].class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16Packet#setRFData(byte[])}.
	 */
	@Test
	public final void testSetRFDataValidData() {
		// Setup the resources for the test.
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int rssi = 75;
		int options = 0x84; /* bit 2 */
		byte[] origData = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		byte[] receivedData = new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
		RX16Packet packet = new RX16Packet(source16Addr, rssi, options, origData);
		
		// Call the method under test.
		packet.setRFData(receivedData);
		
		byte[] result = packet.getRFData();
		
		// Verify the result.
		assertThat("RF Data must be the same", result, is(equalTo(receivedData)));
		assertThat("RF Data must not be the same object", result.hashCode(), is(not(equalTo(receivedData.hashCode()))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16Packet#setRFData(byte[])}.
	 */
	@Test
	public final void testSetRFDataAndModifyOriginal() {
		// Setup the resources for the test.
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int rssi = 75;
		int options = 0x84; /* bit 2 */
		byte[] origData = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		byte[] receivedData = new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
		RX16Packet packet = new RX16Packet(source16Addr, rssi, options, origData);
		
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
