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
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.common.TransmitPacket;
import com.digi.xbee.api.utils.HexUtils;

public class TransmitPacketTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	public TransmitPacketTest() {
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
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitPacket#createPacket(byte[])}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a 
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Setup the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Transmit packet payload cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		TransmitPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Setup the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Transmit packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		TransmitPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Setup the resources for the test.
		int frameType = APIFrameType.TRANSMIT_REQUEST.getValue();
		int frameID = 0xE7;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int broadcastRadious = 0x00;
		
		byte[] payload = new byte[13];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(dest64Addr.getValue(), 0, payload, 2, dest64Addr.getValue().length);
		System.arraycopy(dest16Addr.getValue(), 0, payload, 10, dest16Addr.getValue().length);
		payload[12] = (byte)broadcastRadious;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Transmit packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		TransmitPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array not including the Frame type.</p>
	 */
	@Test
	public final void testCreatePacketPayloadNotIncludingFrameType() {
		// Setup the resources for the test.
		int frameID = 0xE7;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int broadcastRadious = 0x00;
		int options = 0x04;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		byte[] payload = new byte[13 + data.length];
		payload[0] = (byte)frameID;
		System.arraycopy(dest64Addr.getValue(), 0, payload, 1, dest64Addr.getValue().length);
		System.arraycopy(dest16Addr.getValue(), 0, payload, 9, dest16Addr.getValue().length);
		payload[11] = (byte)broadcastRadious;
		payload[12] = (byte)options;
		System.arraycopy(data, 0, payload, 13, data.length);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not a Transmit packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		TransmitPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitPacket#createPacket(byte[])}.
	 * 
	 * <p>A valid API Transmit packet with the provided options without RF data 
	 * is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutData() {
		// Setup the resources for the test.
		int frameType = APIFrameType.TRANSMIT_REQUEST.getValue();
		int frameID = 0xE7;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int broadcastRadious = 0x00;
		int options = 0x04;
		
		byte[] payload = new byte[14];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(dest64Addr.getValue(), 0, payload, 2, dest64Addr.getValue().length);
		System.arraycopy(dest16Addr.getValue(), 0, payload, 10, dest16Addr.getValue().length);
		payload[12] = (byte)broadcastRadious;
		payload[13] = (byte)options;
		
		// Call the method under test.
		TransmitPacket packet = TransmitPacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination 64-bit address is not the expected one", packet.get64bitDestinationAddress(), is(equalTo(dest64Addr)));
		assertThat("Returned destination 16-bit address is not the expected one", packet.get16bitDestinationAddress(), is(equalTo(dest16Addr)));
		assertThat("Returned broadcast radious is not the expected one", packet.getBroadcastRadius(), is(equalTo(broadcastRadious)));
		assertThat("Returned transmit options is not the expected one", packet.getTransmitOptions(), is(equalTo(options)));
		assertThat("Returned RF Data is not the expected one", packet.getRFData(), is(nullValue()));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitPacket#createPacket(byte[])}.
	 * 
	 * <p>A valid API Transmit packet with the provided options and RF data is 
	 * created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithData() {
		// Setup the resources for the test.
		int frameType = APIFrameType.TRANSMIT_REQUEST.getValue();
		int frameID = 0xE7;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int broadcastRadious = 0x00;
		int options = 0x04;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		byte[] payload = new byte[14 + data.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(dest64Addr.getValue(), 0, payload, 2, dest64Addr.getValue().length);
		System.arraycopy(dest16Addr.getValue(), 0, payload, 10, dest16Addr.getValue().length);
		payload[12] = (byte)broadcastRadious;
		payload[13] = (byte)options;
		System.arraycopy(data, 0, payload, 14, data.length);
		
		// Call the method under test.
		TransmitPacket packet = TransmitPacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination 64-bit address is not the expected one", packet.get64bitDestinationAddress(), is(equalTo(dest64Addr)));
		assertThat("Returned destination 16-bit address is not the expected one", packet.get16bitDestinationAddress(), is(equalTo(dest16Addr)));
		assertThat("Returned broadcast radious is not the expected one", packet.getBroadcastRadius(), is(equalTo(broadcastRadious)));
		assertThat("Returned transmit options is not the expected one", packet.getTransmitOptions(), is(equalTo(options)));
		assertThat("Returned RF Data is not the expected one", packet.getRFData(), is(equalTo(data)));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitPacket#TransmitPacket(int, XBee64BitAddress, XBee16BitAddress, int, int, byte[])}.
	 * 
	 * <p>Construct a new Transmit packet but with a {@code null} 64-bit address. 
	 * This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateTransmitPacket64BitAddressNull() {
		// Setup the resources for the test.
		int frameID = 5;
		XBee64BitAddress dest64Addr = null;
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int broadcastRadious = 0;
		int options = 40;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("64-bit destination address cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new TransmitPacket(frameID, dest64Addr, dest16Addr, broadcastRadious, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitPacket#TransmitPacket(int, XBee64BitAddress, XBee16BitAddress, int, int, byte[])}.
	 * 
	 * <p>Construct a new Transmit packet but with a {@code null} 16-bit address. 
	 * This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateTransmitPacket16BitAddressNull() {
		// Setup the resources for the test.
		int frameID = 5;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = null;
		int broadcastRadious = 0;
		int options = 40;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("16-bit destination address cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new TransmitPacket(frameID, dest64Addr, dest16Addr, broadcastRadious, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitPacket#TransmitPacket(int, XBee64BitAddress, XBee16BitAddress, int, int, byte[])}.
	 * 
	 * <p>Construct a new Transmit packet but with a frame ID bigger than 255. 
	 * This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateTransmitPacketFrameIDBiggerThan255() {
		// Setup the resources for the test.
		int frameID = 524;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int broadcastRadious = 0;
		int options = 40;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new TransmitPacket(frameID, dest64Addr, dest16Addr, broadcastRadious, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitPacket#TransmitPacket(int, XBee64BitAddress, XBee16BitAddress, int, int, byte[])}.
	 * 
	 * <p>Construct a new Transmit packet but with a negative frame ID. This 
	 * must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateTransmitPacketFrameIDNegative() {
		// Setup the resources for the test.
		int frameID = -6;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int broadcastRadious = 0;
		int options = 40;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new TransmitPacket(frameID, dest64Addr, dest16Addr, broadcastRadious, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitPacket#TransmitPacket(int, XBee64BitAddress, XBee16BitAddress, int, int, byte[])}.
	 * 
	 * <p>Construct a new Transmit packet but with a broadcast radious bigger 
	 * than 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateTransmitPacketBroadcastRadiousBiggerThan255() {
		// Setup the resources for the test.
		int frameID = 5;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int broadcastRadious = 589;
		int options = 40;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Broadcast radius must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new TransmitPacket(frameID, dest64Addr, dest16Addr, broadcastRadious, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitPacket#TransmitPacket(int, XBee64BitAddress, XBee16BitAddress, int, int, byte[])}.
	 * 
	 * <p>Construct a new Transmit packet but with a negative broadcast radious.
	 * This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateTransmitPacketBroadcastRadiousNegative() {
		// Setup the resources for the test.
		int frameID = 5;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int broadcastRadious = -86;
		int options = 40;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Broadcast radius must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new TransmitPacket(frameID, dest64Addr, dest16Addr, broadcastRadious, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitPacket#TransmitPacket(int, XBee64BitAddress, XBee16BitAddress, int, int, byte[])}.
	 * 
	 * <p>Construct a new Transmit packet but with transmit options bigger 
	 * than 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateTransmitPacketTransmitOptinsBiggerThan255() {
		// Setup the resources for the test.
		int frameID = 5;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int broadcastRadious = 0;
		int options = 2360;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Transmit options must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new TransmitPacket(frameID, dest64Addr, dest16Addr, broadcastRadious, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitPacket#TransmitPacket(int, XBee64BitAddress, XBee16BitAddress, int, int, byte[])}.
	 * 
	 * <p>Construct a new Transmit packet but with a negative transmit options.
	 * This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateTransmitPacketTransmitOptionsNegative() {
		// Setup the resources for the test.
		int frameID = 5;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int broadcastRadious = 0;
		int options = -40;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Transmit options must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new TransmitPacket(frameID, dest64Addr, dest16Addr, broadcastRadious, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitPacket#TransmitPacket(int, XBee64BitAddress, XBee16BitAddress, int, int, byte[])}.
	 * 
	 * <p>Construct a new Transmit packet but with valid parameters but without 
	 * data ({@code null}).</p>
	 */
	@Test
	public final void testCreateTransmitPacketValidDataNull() {
		// Setup the resources for the test.
		int frameID = 0x65;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int broadcastRadious = 0;
		int options = 40;
		byte[] data = null;
		
		int expectedLength = 1 /* Frame type */ + 1 /* Frame ID */ + 8 /* 64-bit address */ + 2 /* 16-bit address */ + 1 /* broadcast radious */ + 1 /* options */;
		
		// Call the method under test.
		TransmitPacket packet = new TransmitPacket(frameID, dest64Addr, dest16Addr, broadcastRadious, options, data);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination 64-bit address is not the expected one", packet.get64bitDestinationAddress(), is(equalTo(dest64Addr)));
		assertThat("Returned destination 16-bit address is not the expected one", packet.get16bitDestinationAddress(), is(equalTo(dest16Addr)));
		assertThat("Returned broadcast radious is not the expected one", packet.getBroadcastRadius(), is(equalTo(broadcastRadious)));
		assertThat("Returned transmit options are not the expected one", packet.getTransmitOptions(), is(equalTo(options)));
		assertThat("Returned Command Data is not the expected one", packet.getRFData(), is(nullValue(byte[].class)));
		assertThat("Transmit packet needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitPacket#TransmitPacket(int, XBee64BitAddress, XBee16BitAddress, int, int, byte[])}.
	 * 
	 * <p>Construct a new Transmit packet but with valid parameters with data.</p>
	 */
	@Test
	public final void testCreateTransmitPacketValidDataNonNull() {
		// Setup the resources for the test.
		int frameID = 0x65;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int broadcastRadious = 0;
		int options = 40;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		int expectedLength = 1 /* Frame type */ + 1 /* Frame ID */ + 8 /* 64-bit address */ + 2 /* 16-bit address */ + 1 /* broadcast radious */ + 1 /* options */ + data.length /* Data */;
		
		// Call the method under test.
		TransmitPacket packet = new TransmitPacket(frameID, dest64Addr, dest16Addr, broadcastRadious, options, data);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination 64-bit address is not the expected one", packet.get64bitDestinationAddress(), is(equalTo(dest64Addr)));
		assertThat("Returned destination 16-bit address is not the expected one", packet.get16bitDestinationAddress(), is(equalTo(dest16Addr)));
		assertThat("Returned broadcast radious is not the expected one", packet.getBroadcastRadius(), is(equalTo(broadcastRadious)));
		assertThat("Returned transmit options are not the expected one", packet.getTransmitOptions(), is(equalTo(options)));
		assertThat("Returned Command Data is not the expected one", packet.getRFData(), is(equalTo(data)));
		assertThat("Transmit packet needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitPacket#getAPIData()}.
	 * 
	 * <p>Test the get API parameters but with a {@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIDataReceivedDataNull() {
		// Setup the resources for the test.
		int frameID = 0x65;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int broadcastRadious = 0;
		int options = 40;
		byte[] data = null;
		TransmitPacket packet = new TransmitPacket(frameID, dest64Addr, dest16Addr, broadcastRadious, options, data);
		
		int expectedLength = 1 /* Frame ID */ + 8 /* 64-bit address */ + 2 /* 16-bit address */ + 1 /* Broadcast radious */ + 1 /* options */;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		System.arraycopy(dest64Addr.getValue(), 0, expectedData, 1, dest64Addr.getValue().length);
		System.arraycopy(dest16Addr.getValue(), 0, expectedData, 9, dest16Addr.getValue().length);
		expectedData[11] = (byte)broadcastRadious;
		expectedData[12] = (byte)options;
		
		// Call the method under test.
		byte[] apiData = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", apiData, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitPacket#getAPIData()}.
	 * 
	 * <p>Test the get API parameters but with a not-{@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIDataReceivedDataNotNull() {
		// Setup the resources for the test.
		int frameID = 0x65;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int broadcastRadious = 0;
		int options = 40;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		TransmitPacket packet = new TransmitPacket(frameID, dest64Addr, dest16Addr, broadcastRadious, options, data);
		
		int expectedLength = 1 /* Frame ID */ + 8 /* 64-bit address */ + 2 /* 16-bit address */ + 1 /* Broadcast radious */ + 1 /* options */ + data.length /* Data */;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		System.arraycopy(dest64Addr.getValue(), 0, expectedData, 1, dest64Addr.getValue().length);
		System.arraycopy(dest16Addr.getValue(), 0, expectedData, 9, dest16Addr.getValue().length);
		expectedData[11] = (byte)broadcastRadious;
		expectedData[12] = (byte)options;
		System.arraycopy(data, 0, expectedData, 13, data.length);
		
		// Call the method under test.
		byte[] apiData = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", apiData, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitPacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with a {@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersReceivedDataNull() {
		// Setup the resources for the test.
		int frameID = 0x65;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int broadcastRadious = 0;
		int options = 40;
		byte[] data = null;
		TransmitPacket packet = new TransmitPacket(frameID, dest64Addr, dest16Addr, broadcastRadious, options, data);
		
		String expectedDest64Addr = HexUtils.prettyHexString(dest64Addr.getValue());
		String expectedDest16Addr = HexUtils.prettyHexString(dest16Addr.getValue());
		String expectedBroadcastRadious = HexUtils.prettyHexString(Integer.toHexString(broadcastRadious)) + " (" + broadcastRadious + ")";
		String expectedOptions = HexUtils.prettyHexString(Integer.toHexString(options));
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(4)));
		assertThat("Destination 64-bit Address is not the expected one", packetParams.get("64-bit dest. address"), is(equalTo(expectedDest64Addr)));
		assertThat("Destination 16-bit Address is not the expected one", packetParams.get("16-bit dest. address"), is(equalTo(expectedDest16Addr)));
		assertThat("Broadcast Radious is not the expected", packetParams.get("Broadcast radius"), is(equalTo(expectedBroadcastRadious)));
		assertThat("Transmit options are not the expected", packetParams.get("Options"), is(equalTo(expectedOptions)));
		assertThat("Data is not the expected", packetParams.get("RF data"), is(nullValue(String.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitPacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with a non-{@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersReceivedDataNotNull() {
		// Setup the resources for the test.
		int frameID = 0x65;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int broadcastRadious = 0;
		int options = 40;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		TransmitPacket packet = new TransmitPacket(frameID, dest64Addr, dest16Addr, broadcastRadious, options, data);
		
		String expectedDest64Addr = HexUtils.prettyHexString(dest64Addr.getValue());
		String expectedDest16Addr = HexUtils.prettyHexString(dest16Addr.getValue());
		String expectedOptions = HexUtils.prettyHexString(Integer.toHexString(options));
		String expectedBroadcastRadious = HexUtils.prettyHexString(Integer.toHexString(broadcastRadious)) + " (" + broadcastRadious + ")";
		String expectedData = HexUtils.prettyHexString(HexUtils.byteArrayToHexString(data));
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(5)));
		assertThat("Destination 64-bit Address is not the expected one", packetParams.get("64-bit dest. address"), is(equalTo(expectedDest64Addr)));
		assertThat("Destination 16-bit Address is not the expected one", packetParams.get("16-bit dest. address"), is(equalTo(expectedDest16Addr)));
		assertThat("Broadcast Radious is not the expected", packetParams.get("Broadcast radius"), is(equalTo(expectedBroadcastRadious)));
		assertThat("Receive options are not the expected", packetParams.get("Options"), is(equalTo(expectedOptions)));
		assertThat("Data is not the expected", packetParams.get("RF data"), is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitPacket#isBroadcast()}.
	 * 
	 * <p>Test if a Transmit packet is a broadcast packet address when 16-bit 
	 * and a 64-bit destination addresses are not broadcast.</p>
	 */
	@Test
	public final void testIsBroadcastWithNon16BitAndNon64BitBroadcastDestinationAddress() {
		// Setup the resources for the test.
		int frameID = 0x65;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int broadcastRadious = 0;
		int options = 40;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		TransmitPacket packet = new TransmitPacket(frameID, dest64Addr, dest16Addr, broadcastRadious, options, data);
		
		// Call the method under test and verify the result.
		assertThat("Packet should not be broadcast", packet.isBroadcast(), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitPacket#isBroadcast()}.
	 * 
	 * <p>Test if a Transmit packet is a broadcast packet address when both 
	 * destination addresses, 16-bit and 64-bit, are broadcast.</p>
	 */
	@Test
	public final void testIsBroadcastWith16BitAnd64BitBroadcastDestinationAddress() {
		// Setup the resources for the test.
		int frameID = 0x65;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("FFFF");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("FFFF");
		int broadcastRadious = 0;
		int options = 40;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		TransmitPacket packet = new TransmitPacket(frameID, dest64Addr, dest16Addr, broadcastRadious, options, data);
		
		// Call the method under test and verify the result.
		assertThat("Packet should be broadcast", packet.isBroadcast(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitPacket#isBroadcast()}.
	 * 
	 * <p>Test if a Transmit packet is a broadcast packet address when the 
	 * 16-bit destination address is not broadcast and 64-bit is.</p>
	 */
	@Test
	public final void testIsBroadcastWithNon16BitBroadcastAnd64BitBroadcastDestinationAddress() {
		// Setup the resources for the test.
		int frameID = 0x65;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("FFFF");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int broadcastRadious = 0;
		int options = 40;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		TransmitPacket packet = new TransmitPacket(frameID, dest64Addr, dest16Addr, broadcastRadious, options, data);
		
		// Call the method under test and verify the result.
		assertThat("Packet should be broadcast", packet.isBroadcast(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitPacket#isBroadcast()}.
	 * 
	 * <p>Test if a Transmit packet is a broadcast packet address when the 
	 * 16-bit destination address is broadcast but 64-bit is not.</p>
	 */
	@Test
	public final void testIsBroadcastWith16BitBroadcastAndNon64BitBroadcastDestinationAddress() {
		// Setup the resources for the test.
		int frameID = 0x65;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("FFFF");
		int broadcastRadious = 0;
		int options = 40;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		TransmitPacket packet = new TransmitPacket(frameID, dest64Addr, dest16Addr, broadcastRadious, options, data);
		
		// Call the method under test and verify the result.
		assertThat("Packet should be broadcast", packet.isBroadcast(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitPacket#getRFData())}.
	 */
	@Test
	public final void testGetRFDataNullData() {
		// Setup the resources for the test.
		int frameID = 0x65;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int broadcastRadious = 0;
		int options = 40;
		byte[] data = null;
		TransmitPacket packet = new TransmitPacket(frameID, dest64Addr, dest16Addr, broadcastRadious, options, data);
		
		// Call the method under test.
		byte[] result = packet.getRFData();
		
		// Verify the result.
		assertThat("RF Data must be the same", result, is(equalTo(data)));
		assertThat("RF Data must be null", result, is(nullValue(byte[].class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitPacket#getRFData())}.
	 */
	@Test
	public final void testGetRFDataValidData() {
		// Setup the resources for the test.
		int frameID = 0x65;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int broadcastRadious = 0;
		int options = 40;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		TransmitPacket packet = new TransmitPacket(frameID, dest64Addr, dest16Addr, broadcastRadious, options, data);
		
		// Call the method under test.
		byte[] result = packet.getRFData();
		
		// Verify the result.
		assertThat("RF Data must be the same", result, is(equalTo(data)));
		assertThat("RF Data must not be the same object", result.hashCode(), is(not(equalTo(data.hashCode()))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitPacket#setRFData(byte[])}.
	 */
	@Test
	public final void testSetRFDataNullData() {
		// Setup the resources for the test.
		int frameID = 0x65;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int broadcastRadious = 0;
		int options = 40;
		byte[] origData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		byte[] data = null;
		TransmitPacket packet = new TransmitPacket(frameID, dest64Addr, dest16Addr, broadcastRadious, options, origData);
		
		// Call the method under test.
		packet.setRFData(data);
		
		byte[] result = packet.getRFData();
		
		// Verify the result.
		assertThat("RF Data must be the same", result, is(equalTo(data)));
		assertThat("RF Data must be null", result, is(nullValue(byte[].class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitPacket#setRFData(byte[])}.
	 */
	@Test
	public final void testSetRFDataValidData() {
		// Setup the resources for the test.
		int frameID = 0x65;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int broadcastRadious = 0;
		int options = 0x84; /* bit 2 */
		byte[] origData = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		byte[] data = new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
		TransmitPacket packet = new TransmitPacket(frameID, dest64Addr, dest16Addr, broadcastRadious, options, origData);
		
		// Call the method under test.
		packet.setRFData(data);
		
		byte[] result = packet.getRFData();
		
		// Verify the result.
		assertThat("RF Data must be the same", result, is(equalTo(data)));
		assertThat("RF Data must not be the same object", result.hashCode(), is(not(equalTo(data.hashCode()))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitPacket#setRFData(byte[])}.
	 */
	@Test
	public final void testSetRFDataAndModifyOriginal() {
		// Setup the resources for the test.
		int frameID = 0x65;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int broadcastRadious = 0;
		int options = 0x84; /* bit 2 */
		byte[] origData = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		byte[] data = new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
		TransmitPacket packet = new TransmitPacket(frameID, dest64Addr, dest16Addr, broadcastRadious, options, origData);
		
		// Call the method under test.
		packet.setRFData(data);
		byte[] backup = Arrays.copyOf(data, data.length);
		data[0] = 0x11;
		data[1] = 0x12;
		
		byte[] result = packet.getRFData();
		
		// Verify the result.
		assertThat("RF Data must be the same as the setted data", result, is(equalTo(backup)));
		assertThat("RF Data must not be the current value of received data", result, is(not(equalTo(data))));
		assertThat("RF Data must not be the same object", result.hashCode(), is(not(equalTo(backup.hashCode()))));
		assertThat("RF Data must not be the same object", result.hashCode(), is(not(equalTo(data.hashCode()))));
	}
}
