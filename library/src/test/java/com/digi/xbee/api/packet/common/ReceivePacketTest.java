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
package com.digi.xbee.api.packet.common;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.LinkedHashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.common.ReceivePacket;
import com.digi.xbee.api.utils.HexUtils;

public class ReceivePacketTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	public ReceivePacketTest() {
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
	 * Test method for {@link com.digi.xbee.api.packet.common.ReceivePacket#createPacket(byte[])}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a 
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Setup the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Receive packet payload cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		ReceivePacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ReceivePacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Setup the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Receive packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		ReceivePacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ReceivePacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Setup the resources for the test.
		int frameType = APIFrameType.RECEIVE_PACKET.getValue();
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		
		byte[] payload = new byte[11];
		payload[0] = (byte)frameType;
		System.arraycopy(source64Addr.getValue(), 0, payload, 1, source64Addr.getValue().length);
		System.arraycopy(source64Addr.getValue(), 0, payload, 9, source16Addr.getValue().length);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Receive packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		ReceivePacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ReceivePacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array not including the Frame type.</p>
	 */
	@Test
	public final void testCreatePacketPayloadNotIncludingFrameType() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int options = 40;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		byte[] payload = new byte[11 + data.length];
		System.arraycopy(source64Addr.getValue(), 0, payload, 0, source64Addr.getValue().length);
		System.arraycopy(source16Addr.getValue(), 0, payload, 8, source16Addr.getValue().length);
		payload[10] = (byte)options;
		System.arraycopy(data, 0, payload, 11, data.length);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not a Receive packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		ReceivePacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ReceivePacket#createPacket(byte[])}.
	 * 
	 * <p>A valid API Receive packet with the provided options without RF data is 
	 * created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutData() {
		// Setup the resources for the test.
		int frameType = APIFrameType.RECEIVE_PACKET.getValue();
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int options = 40;
		
		byte[] payload = new byte[12];
		payload[0] = (byte)frameType;
		System.arraycopy(source64Addr.getValue(), 0, payload, 1, source64Addr.getValue().length);
		System.arraycopy(source16Addr.getValue(), 0, payload, 9, source16Addr.getValue().length);
		payload[11] = (byte)options;
		
		// Call the method under test.
		ReceivePacket packet = ReceivePacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned source 64-bit address is not the expected one", packet.get64bitSourceAddress(), is(equalTo(source64Addr)));
		assertThat("Returned source 16-bit address is not the expected one", packet.get16bitSourceAddress(), is(equalTo(source16Addr)));
		assertThat("Returned received options is not the expected one", packet.getReceiveOptions(), is(equalTo(options)));
		assertThat("Returned Received Data is not the expected one", packet.getRFData(), is(nullValue()));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ReceivePacket#createPacket(byte[])}.
	 * 
	 * <p>A valid API Receive packet with the provided options and RF data is 
	 * created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithData() {
		// Setup the resources for the test.
		int frameType = APIFrameType.RECEIVE_PACKET.getValue();
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int options = 40;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		byte[] payload = new byte[12 + data.length];
		payload[0] = (byte)frameType;
		System.arraycopy(source64Addr.getValue(), 0, payload, 1, source64Addr.getValue().length);
		System.arraycopy(source16Addr.getValue(), 0, payload, 9, source16Addr.getValue().length);
		payload[11] = (byte)options;
		System.arraycopy(data, 0, payload, 12, data.length);
		
		// Call the method under test.
		ReceivePacket packet = ReceivePacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned source 64-bit address is not the expected one", packet.get64bitSourceAddress(), is(equalTo(source64Addr)));
		assertThat("Returned source 16-bit address is not the expected one", packet.get16bitSourceAddress(), is(equalTo(source16Addr)));
		assertThat("Returned received options is not the expected one", packet.getReceiveOptions(), is(equalTo(options)));
		assertThat("Returned Received Data is not the expected one", packet.getRFData(), is(equalTo(data)));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ReceivePacket#ReceivePacket(XBee64BitAddress, XBee16BitAddress, int, byte[])}.
	 * 
	 * <p>Construct a new Receive packet but with a {@code null} 64-bit address. 
	 * This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateReceivePacket64BitAddressNull() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = null;
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int options = 40;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("64-bit source address cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new ReceivePacket(source64Addr, source16Addr, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ReceivePacket#ReceivePacket(XBee64BitAddress, XBee16BitAddress, int, byte[])}.
	 * 
	 * <p>Construct a new Receive packet but with a {@code null} 16-bit address. 
	 * This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateReceivePacket16BitAddressNull() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = null;
		int options = 40;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("16-bit source address cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new ReceivePacket(source64Addr, source16Addr, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ReceivePacket#ReceivePacket(XBee64BitAddress, XBee16BitAddress, int, byte[])}.
	 * 
	 * <p>Construct a new Receive packet but with a receive options bigger than
	 *  255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateReceivePacketReceiveOptionsBiggerThan255() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int options = 621;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Receive options value must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ReceivePacket(source64Addr, source16Addr, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ReceivePacket#ReceivePacket(XBee64BitAddress, XBee16BitAddress, int, byte[])}.
	 * 
	 * <p>Construct a new Receive packet but with a negative receive options. 
	 * This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateReceivePacketReceiveOptionsNegative() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int options = -8;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Receive options value must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ReceivePacket(source64Addr, source16Addr, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ReceivePacket#ReceivePacket(XBee64BitAddress, XBee16BitAddress, int, byte[])}.
	 * 
	 * <p>Construct a new Receive packet but with {@code null} data.</p>
	 */
	@Test
	public final void testCreateReceivePacketReceiveDataNull() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int options = 0x52;
		byte[] data = null;
		
		int expectedLength = 1 /* Frame type */ + 8 /* 64-bit address */ + 2 /* 16-bit address */ + 1 /* options */;
		
		// Call the method under test.
		ReceivePacket packet = new ReceivePacket(source64Addr, source16Addr, options, data);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned source 64-bit address is not the expected one", packet.get64bitSourceAddress(), is(equalTo(source64Addr)));
		assertThat("Returned source 16-bit address is not the expected one", packet.get16bitSourceAddress(), is(equalTo(source16Addr)));
		assertThat("Returned received options is not the expected one", packet.getReceiveOptions(), is(equalTo(options)));
		assertThat("Returned Data is not the expected one", packet.getRFData(), is(equalTo(data)));
		assertThat("Receive packet does NOT need API Frame ID", packet.needsAPIFrameID(), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ReceivePacket#ReceivePacket(XBee64BitAddress, XBee16BitAddress, int, byte[])}.
	 * 
	 * <p>Construct a new Receive packet but with non-{@code null} data.</p>
	 */
	@Test
	public final void testCreateReceivePacketReceiveDataNotNull() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int options = 0x52;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		int expectedLength = 1 /* Frame type */ + 8 /* 64-bit address */ + 2 /* 16-bit address */ + 1 /* options */ + data.length /* Data */;
		
		// Call the method under test.
		ReceivePacket packet = new ReceivePacket(source64Addr, source16Addr, options, data);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned source 64-bit address is not the expected one", packet.get64bitSourceAddress(), is(equalTo(source64Addr)));
		assertThat("Returned source 16-bit address is not the expected one", packet.get16bitSourceAddress(), is(equalTo(source16Addr)));
		assertThat("Returned received options is not the expected one", packet.getReceiveOptions(), is(equalTo(options)));
		assertThat("Returned Data is not the expected one", packet.getRFData(), is(equalTo(data)));
		assertThat("Receive packet does NOT need API Frame ID", packet.needsAPIFrameID(), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ReceivePacket#getAPIData()}.
	 * 
	 * <p>Test the get API parameters but with a {@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIDataReceivedDataNull() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int options = 0x52;
		byte[] receivedData = null;
		ReceivePacket packet = new ReceivePacket(source64Addr, source16Addr, options, receivedData);
		
		int expectedLength = 8 /* 64-bit address */ + 2 /* 16-bit address */ + 1 /* options */;
		byte[] expectedData = new byte[expectedLength];
		System.arraycopy(source64Addr.getValue(), 0, expectedData, 0, source64Addr.getValue().length);
		System.arraycopy(source16Addr.getValue(), 0, expectedData, 8, source16Addr.getValue().length);
		expectedData[10] = (byte)options;
		
		// Call the method under test.
		byte[] data = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ReceivePacket#getAPIData()}.
	 * 
	 * <p>Test the get API parameters but with a not-{@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIDataReceivedDataNotNull() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int options = 0x52;
		byte[] receivedData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		ReceivePacket packet = new ReceivePacket(source64Addr, source16Addr, options, receivedData);
		
		int expectedLength = 8 /* 64-bit address */ + 2 /* 16-bit address */ + 1 /* options */ + receivedData.length /* Data */;
		byte[] expectedData = new byte[expectedLength];
		System.arraycopy(source64Addr.getValue(), 0, expectedData, 0, source64Addr.getValue().length);
		System.arraycopy(source16Addr.getValue(), 0, expectedData, 8, source16Addr.getValue().length);
		expectedData[10] = (byte)options;
		System.arraycopy(receivedData, 0, expectedData, 11, receivedData.length);
		
		// Call the method under test.
		byte[] data = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ReceivePacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with a {@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersReceivedDataNull() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int options = 40;
		byte[] receivedData = null;
		ReceivePacket packet = new ReceivePacket(source64Addr, source16Addr, options, receivedData);
		
		String expectedSource64Addr = HexUtils.prettyHexString(source64Addr.getValue());
		String expectedSource16Addr = HexUtils.prettyHexString(source16Addr.getValue());
		String expectedOptions = HexUtils.prettyHexString(Integer.toHexString(options));
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(3)));
		assertThat("Source 64-bit Address is not the expected one", packetParams.get("64-bit source address"), is(equalTo(expectedSource64Addr)));
		assertThat("Source 16-bit Address is not the expected one", packetParams.get("16-bit source address"), is(equalTo(expectedSource16Addr)));
		assertThat("Receive options are not the expected", packetParams.get("Receive options"), is(equalTo(expectedOptions)));
		assertThat("Received data is not the expected", packetParams.get("RF data"), is(nullValue(String.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ReceivePacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with a non-{@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersReceivedDataNotNull() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int options = 40;
		byte[] receivedData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		ReceivePacket packet = new ReceivePacket(source64Addr, source16Addr, options, receivedData);
		
		String expectedSource64Addr = HexUtils.prettyHexString(source64Addr.getValue());
		String expectedSource16Addr = HexUtils.prettyHexString(source16Addr.getValue());
		String expectedOptions = HexUtils.prettyHexString(Integer.toHexString(options));
		String expectedReceivedData = HexUtils.prettyHexString(HexUtils.byteArrayToHexString(receivedData));
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(4)));
		assertThat("Source 64-bit Address is not the expected one", packetParams.get("64-bit source address"), is(equalTo(expectedSource64Addr)));
		assertThat("Source 16-bit Address is not the expected one", packetParams.get("16-bit source address"), is(equalTo(expectedSource16Addr)));
		assertThat("Receive options are not the expected", packetParams.get("Receive options"), is(equalTo(expectedOptions)));
		assertThat("Received data is not the expected", packetParams.get("RF data"), is(equalTo(expectedReceivedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ReceivePacket#isBroadcast()}.
	 * 
	 * <p>Test if an API Receive packet is a broadcast packet when broadcast 
	 * options is not enabled.</p>
	 */
	@Test
	public final void testIsBroadcastWithNonBroadcastOption() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int options = 0x19;
		byte[] receivedData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		ReceivePacket packet = new ReceivePacket(source64Addr, source16Addr, options, receivedData);
		
		// Call the method under test and verify the result.
		assertThat("Packet should not be broadcast", packet.isBroadcast(), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ReceivePacket#isBroadcast()}.
	 * 
	 * <p>Test if an API Receive packet is a broadcast packet when broadcast is 
	 * enabled in the options.</p>
	 */
	@Test
	public final void testIsBroadcastWithBroadcastOptionEnabled() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int options = 0x8A; /* bit 1 */
		byte[] receivedData = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		ReceivePacket packet = new ReceivePacket(source64Addr, source16Addr, options, receivedData);
		
		// Call the method under test and verify the result.
		assertThat("Packet should be broadcast", packet.isBroadcast(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ReceivePacket#getRFData())}.
	 */
	@Test
	public final void testGetRFDataNullData() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int options = 0x8A; /* bit 1 */
		byte[] receivedData = null;
		ReceivePacket packet = new ReceivePacket(source64Addr, source16Addr, options, receivedData);
		
		// Call the method under test.
		byte[] result = packet.getRFData();
		
		// Verify the result.
		assertThat("RF Data must be the same", result, is(equalTo(receivedData)));
		assertThat("RF Data must be null", result, is(nullValue(byte[].class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ReceivePacket#getRFData())}.
	 */
	@Test
	public final void testGetRFDataValidData() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int options = 0x8A; /* bit 1 */
		byte[] receivedData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		ReceivePacket packet = new ReceivePacket(source64Addr, source16Addr, options, receivedData);
		
		// Call the method under test.
		byte[] result = packet.getRFData();
		
		// Verify the result.
		assertThat("RF Data must be the same", result, is(equalTo(receivedData)));
		assertThat("RF Data must not be the same object", result.hashCode(), is(not(equalTo(receivedData.hashCode()))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ReceivePacket#setRFData(byte[])}.
	 */
	@Test
	public final void testSetRFDataNullData() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int options = 0x8A; /* bit 1 */
		byte[] origData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		byte[] receivedData = null;
		ReceivePacket packet = new ReceivePacket(source64Addr, source16Addr, options, origData);
		
		// Call the method under test.
		packet.setRFData(receivedData);
		
		byte[] result = packet.getRFData();
		
		// Verify the result.
		assertThat("RF Data must be the same", result, is(equalTo(receivedData)));
		assertThat("RF Data must be null", result, is(nullValue(byte[].class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ReceivePacket#setRFData(byte[])}.
	 */
	@Test
	public final void testSetRFDataValidData() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int options = 0x84; /* bit 2 */
		byte[] origData = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		byte[] receivedData = new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
		ReceivePacket packet = new ReceivePacket(source64Addr, source16Addr, options, origData);
		
		// Call the method under test.
		packet.setRFData(receivedData);
		
		byte[] result = packet.getRFData();
		
		// Verify the result.
		assertThat("RF Data must be the same", result, is(equalTo(receivedData)));
		assertThat("RF Data must not be the same object", result.hashCode(), is(not(equalTo(receivedData.hashCode()))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ReceivePacket#setRFData(byte[])}.
	 */
	@Test
	public final void testSetRFDataAndModifyOriginal() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int options = 0x84; /* bit 2 */
		byte[] origData = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		byte[] receivedData = new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
		ReceivePacket packet = new ReceivePacket(source64Addr, source16Addr, options, origData);
		
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
