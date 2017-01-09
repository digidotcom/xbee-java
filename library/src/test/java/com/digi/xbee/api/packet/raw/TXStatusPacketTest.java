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

import java.util.LinkedHashMap;

import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.digi.xbee.api.models.XBeeTransmitStatus;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

public class TXStatusPacketTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();

	public TXStatusPacketTest() {
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
	 * Test method for {@link com.digi.xbee.api.packet.raw.TXStatusPacket#createPacket(byte[])}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a 
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Setup the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("TX Status packet payload cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		TXStatusPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.TXStatusPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Setup the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete TX Status packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		TXStatusPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.TXStatusPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Setup the resources for the test.
		int frameType = APIFrameType.TX_STATUS.getValue();
		int frameID = 0xE7;
		
		byte[] payload = new byte[2];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete TX Status packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		TXStatusPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.TXStatusPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array not including the Frame type.</p>
	 */
	@Test
	public final void testCreatePacketPayloadNotIncludingFrameType() {
		// Setup the resources for the test.
		int frameID = 0xE7;
		int status = XBeeTransmitStatus.SUCCESS.getId();
		
		byte[] payload = new byte[3];
		payload[0] = (byte)frameID;
		payload[1] = (byte)status;
		payload[2] = 0; // Just to have the minimum size.
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not a TX Status packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		TXStatusPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.TXStatusPacket#createPacket(byte[])}.
	 * 
	 * <p>A valid API TX Status packet with the provided options.</p>
	 */
	@Test
	public final void testCreatePacketValidPayload() {
		// Setup the resources for the test.
		int frameType = APIFrameType.TX_STATUS.getValue();
		int frameID = 0xE7;
		int status = XBeeTransmitStatus.SUCCESS.getId();
		
		byte[] payload = new byte[3];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		payload[2] = (byte)status;
		
		// Call the method under test.
		TXStatusPacket packet = TXStatusPacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned status is not the expected one", packet.getTransmitStatus(), is(equalTo(XBeeTransmitStatus.SUCCESS)));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.TXStatusPacket#createPacket(byte[])}.
	 * 
	 * <p>A valid API TX Status packet with the provided options and unknown status.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadUnknownStatus() {
		// Setup the resources for the test.
		int frameType = APIFrameType.TX_STATUS.getValue();
		int frameID = 0xE7;
		int status = 255;
		
		byte[] payload = new byte[3];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		payload[2] = (byte)status;
		
		// Call the method under test.
		TXStatusPacket packet = TXStatusPacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned status id is not the expected one", packet.getTransmitStatus().getId(), is(equalTo(XBeeTransmitStatus.UNKNOWN.getId())));
		assertThat("Returned status description is not the expected one", packet.getTransmitStatus().getDescription(), is(equalTo(XBeeTransmitStatus.UNKNOWN.getDescription())));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.TXStatusPacket#TXStatusPacket(int, XBeeTransmitStatus)}.
	 * 
	 * <p>Construct a new TX Status packet but with a {@code null} 16-bit 
	 * address. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateTXStatusPacketTransmitStatusNull() {
		// Setup the resources for the test.
		int frameID = 5;
		XBeeTransmitStatus transmitStatus = null;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Transmit status cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new TXStatusPacket(frameID, transmitStatus);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.TXStatusPacket#TXStatusPacket(int, XBeeTransmitStatus)}.
	 * 
	 * <p>Construct a new TX Status packet but with a frame ID bigger 
	 * than 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateTXStatusPacketFrameIDBiggerThan255() {
		// Setup the resources for the test.
		int frameID = 2398;
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.SUCCESS;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw a IllegalArgumentException.
		new TXStatusPacket(frameID, transmitStatus);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.TXStatusPacket#TXStatusPacket(int, XBeeTransmitStatus)}.
	 * 
	 * <p>Construct a new TX Status packet but with a negative frame ID. 
	 * This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateTXStatusPacketFrameIDNegative() {
		// Setup the resources for the test.
		int frameID = -2398;
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.SUCCESS;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw a IllegalArgumentException.
		new TXStatusPacket(frameID, transmitStatus);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.TXStatusPacket#TXStatusPacket(int, XBeeTransmitStatus)}.
	 * 
	 * <p>Construct a new TX Status packet with valid parameters.</p>
	 */
	@Test
	public final void testCreateTXStatusPacketValid() {
		// Setup the resources for the test.
		int frameID = 85;
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.SUCCESS;
		
		int expectedLength = 1 /* Frame type */ + 1 /* Frame ID */ + 1 /* transmit status */;
		
		// Call the method under test.
		TXStatusPacket packet = new TXStatusPacket(frameID, transmitStatus);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned delivery status is not the expected one", packet.getTransmitStatus(), is(equalTo(transmitStatus)));
		assertThat("TX Status packet needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.TXStatusPacket#TXStatusPacket(int, XBeeTransmitStatus)}.
	 * 
	 * <p>Construct a new TX Status packet with valid parameters.</p>
	 */
	@Test
	public final void testCreateTXStatusPacketValidWithUnknownStatus() {
		// Setup the resources for the test.
		int frameID = 85;
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.UNKNOWN;
		
		int expectedLength = 1 /* Frame type */ + 1 /* Frame ID */ + 1 /* transmit status */;
		
		// Call the method under test.
		TXStatusPacket packet = new TXStatusPacket(frameID, transmitStatus);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned status is not the expected one", packet.getTransmitStatus(), is(equalTo(transmitStatus)));
		assertThat("TX Status packet needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.TXStatusPacket#getAPIData()}.
	 * 
	 * <p>Test the get API parameters.</p>
	 */
	@Test
	public final void testGetAPIData() {
		// Setup the resources for the test.
		int frameID = 0x65;
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.SUCCESS;
		TXStatusPacket packet = new TXStatusPacket(frameID, transmitStatus);
		
		int expectedLength = 1 /* Frame ID */ + 1 /* transmit status */;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		expectedData[1] = (byte)transmitStatus.getId();
		
		// Call the method under test.
		byte[] apiData = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", apiData, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.TXStatusPacket#getAPIData()}.
	 * 
	 * <p>Test the get API parameters with unknown status.</p>
	 */
	@Test
	public final void testGetAPIDataUnknownStatus() {
		// Setup the resources for the test.
		int frameType = APIFrameType.TX_STATUS.getValue();
		int frameID = 0x65;
		int status = 255;
		
		byte[] payload = new byte[3];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		payload[2] = (byte)status;
		
		TXStatusPacket packet = TXStatusPacket.createPacket(payload);
		
		byte[] expectedData = new byte[payload.length - 1]; /* Do not include the type */
		System.arraycopy(payload, 1, expectedData, 0, expectedData.length);
		
		// Call the method under test.
		byte[] apiData = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", apiData, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.TXStatusPacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters.</p>
	 */
	@Test
	public final void testGetAPIPacketParameters() {
		// Setup the resources for the test.
		int frameID = 0x65;
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.SUCCESS;
		TXStatusPacket packet = new TXStatusPacket(frameID, transmitStatus);
		
		String expectedTransmitStatus = HexUtils.prettyHexString(Integer.toHexString(transmitStatus.getId())) + " (" + transmitStatus.getDescription() + ")";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(1)));
		assertThat("Delivery status is not the expected", packetParams.get("Status"), is(equalTo(expectedTransmitStatus)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.TXStatusPacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters with unknown status.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersWithUnknownStatus() {
		// Setup the resources for the test.
		int frameType = APIFrameType.TX_STATUS.getValue();
		int frameID = 0x65;
		int status = 255;
		
		byte[] payload = new byte[3];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		payload[2] = (byte)status;
		
		TXStatusPacket packet = TXStatusPacket.createPacket(payload);
		
		String expectedTransmitStatus = HexUtils.prettyHexString(Integer.toHexString(status).toUpperCase()) + " (" + XBeeTransmitStatus.UNKNOWN.getDescription() + ")";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(1)));
		assertThat("Delivery status is not the expected", packetParams.get("Status"), is(equalTo(expectedTransmitStatus)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.TXStatusPacket#isBroadcast()}.
	 * 
	 * <p>Test if a TX Status packet is a broadcast packet, never should be.</p>
	 */
	@Test
	public final void testIsBroadcast() {
		// Setup the resources for the test.
		int frameID = 0x65;
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.SUCCESS;
		TXStatusPacket packet = new TXStatusPacket(frameID, transmitStatus);
		
		// Call the method under test and verify the result.
		assertThat("Packet should not be broadcast", packet.isBroadcast(), is(equalTo(false)));
	}
}
