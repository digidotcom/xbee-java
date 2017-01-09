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
import com.digi.xbee.api.models.XBeeDiscoveryStatus;
import com.digi.xbee.api.models.XBeeTransmitStatus;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

public class TransmitStatusPacketTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();

	public TransmitStatusPacketTest() {
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
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitStatusPacket#createPacket(byte[])}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a 
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Setup the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Transmit Status packet payload cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		TransmitStatusPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitStatusPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Setup the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Transmit Status packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		TransmitStatusPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitStatusPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Setup the resources for the test.
		int frameType = APIFrameType.TRANSMIT_STATUS.getValue();
		int frameID = 0xE7;
		XBee16BitAddress address = new XBee16BitAddress("B45C");
		int retryCount = 3;
		int deliveryStatus = XBeeTransmitStatus.SUCCESS.getId();
		
		byte[] payload = new byte[6];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(address.getValue(), 0, payload, 2, address.getValue().length);
		payload[4] = (byte)retryCount;
		payload[5] = (byte)deliveryStatus;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Transmit Status packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		TransmitStatusPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitStatusPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array not including the Frame type.</p>
	 */
	@Test
	public final void testCreatePacketPayloadNotIncludingFrameType() {
		// Setup the resources for the test.
		int frameID = 0xE7;
		XBee16BitAddress address = new XBee16BitAddress("B45C");
		int retryCount = 3;
		int deliveryStatus = XBeeTransmitStatus.SUCCESS.getId();
		int discoveryStatus = XBeeDiscoveryStatus.DISCOVERY_STATUS_NO_DISCOVERY_OVERHEAD.getId();
		
		byte[] payload = new byte[7];
		payload[0] = (byte)frameID;
		System.arraycopy(address.getValue(), 0, payload, 1, address.getValue().length);
		payload[3] = (byte)retryCount;
		payload[4] = (byte)deliveryStatus;
		payload[5] = (byte)discoveryStatus;
		payload[6] = 0; // Just to have the minimum size.
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not a Transmit Status packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		TransmitStatusPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitStatusPacket#createPacket(byte[])}.
	 * 
	 * <p>A valid API Transmit Status packet with the provided options.</p>
	 */
	@Test
	public final void testCreatePacketValidPayload() {
		// Setup the resources for the test.
		int frameType = APIFrameType.TRANSMIT_STATUS.getValue();
		int frameID = 0xE7;
		XBee16BitAddress address = new XBee16BitAddress("B45C");
		int retryCount = 3;
		int deliveryStatus = XBeeTransmitStatus.SUCCESS.getId();
		int discoveryStatus = XBeeDiscoveryStatus.DISCOVERY_STATUS_NO_DISCOVERY_OVERHEAD.getId();
		
		byte[] payload = new byte[7];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(address.getValue(), 0, payload, 2, address.getValue().length);
		payload[4] = (byte)retryCount;
		payload[5] = (byte)deliveryStatus;
		payload[6] = (byte)discoveryStatus;
		
		// Call the method under test.
		TransmitStatusPacket packet = TransmitStatusPacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination 16-bit address is not the expected one", packet.get16bitDestinationAddress(), is(equalTo(address)));
		assertThat("Returned retry count is not the expected one", packet.getTransmitRetryCount(), is(equalTo(retryCount)));
		assertThat("Returned delivery status is not the expected one", packet.getTransmitStatus().getId(), is(equalTo(deliveryStatus)));
		assertThat("Returned discovery status is not the expected one", packet.getDiscoveryStatus().getId(), is(equalTo(discoveryStatus)));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitStatusPacket#createPacket(byte[])}.
	 * 
	 * <p>A valid API Transmit Status packet with the provided options and unknown delivery status.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadUnknownDeliveryStatus() {
		// Setup the resources for the test.
		int frameType = APIFrameType.TRANSMIT_STATUS.getValue();
		int frameID = 0xE7;
		XBee16BitAddress address = new XBee16BitAddress("B45C");
		int retryCount = 3;
		int deliveryStatus = 255;
		int discoveryStatus = XBeeDiscoveryStatus.DISCOVERY_STATUS_NO_DISCOVERY_OVERHEAD.getId();
		
		byte[] payload = new byte[7];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(address.getValue(), 0, payload, 2, address.getValue().length);
		payload[4] = (byte)retryCount;
		payload[5] = (byte)deliveryStatus;
		payload[6] = (byte)discoveryStatus;
		
		// Call the method under test.
		TransmitStatusPacket packet = TransmitStatusPacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination 16-bit address is not the expected one", packet.get16bitDestinationAddress(), is(equalTo(address)));
		assertThat("Returned retry count is not the expected one", packet.getTransmitRetryCount(), is(equalTo(retryCount)));
		assertThat("Returned delivery status id is not the expected one", packet.getTransmitStatus().getId(), is(equalTo(XBeeTransmitStatus.UNKNOWN.getId())));
		assertThat("Returned delivery status description is not the expected one", packet.getTransmitStatus().getDescription(), is(equalTo(XBeeTransmitStatus.UNKNOWN.getDescription())));
		assertThat("Returned discovery status is not the expected one", packet.getDiscoveryStatus(), is(equalTo(XBeeDiscoveryStatus.DISCOVERY_STATUS_NO_DISCOVERY_OVERHEAD)));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitStatusPacket#createPacket(byte[])}.
	 * 
	 * <p>A valid API Transmit Status packet with the provided options and unknown discovery status.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadUnknownDiscoveryStatus() {
		// Setup the resources for the test.
		int frameType = APIFrameType.TRANSMIT_STATUS.getValue();
		int frameID = 0xE7;
		XBee16BitAddress address = new XBee16BitAddress("B45C");
		int retryCount = 3;
		int deliveryStatus = XBeeTransmitStatus.BROADCAST_FAILED.getId();;
		int discoveryStatus = 255;
		
		byte[] payload = new byte[7];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(address.getValue(), 0, payload, 2, address.getValue().length);
		payload[4] = (byte)retryCount;
		payload[5] = (byte)deliveryStatus;
		payload[6] = (byte)discoveryStatus;
		
		// Call the method under test.
		TransmitStatusPacket packet = TransmitStatusPacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination 16-bit address is not the expected one", packet.get16bitDestinationAddress(), is(equalTo(address)));
		assertThat("Returned retry count is not the expected one", packet.getTransmitRetryCount(), is(equalTo(retryCount)));
		assertThat("Returned delivery status is not the expected one", packet.getTransmitStatus(), is(equalTo(XBeeTransmitStatus.BROADCAST_FAILED)));
		assertThat("Returned discovery status is not the expected one", packet.getDiscoveryStatus(), is(equalTo(XBeeDiscoveryStatus.DISCOVERY_STATUS_UNKNOWN)));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitStatusPacket#TransmitStatusPacket(int, XBee16BitAddress, int, XBeeTransmitStatus, XBeeDiscoveryStatus)}.
	 * 
	 * <p>Construct a new Transmit Status packet but with a {@code null} 16-bit 
	 * address. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateTransmitStatusPacket16BitAddressNull() {
		// Setup the resources for the test.
		int frameID = 5;
		XBee16BitAddress dest16Addr = null;
		int retryCount = 0;
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.SUCCESS;
		XBeeDiscoveryStatus discoveryStatus = XBeeDiscoveryStatus.DISCOVERY_STATUS_NO_DISCOVERY_OVERHEAD;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("16-bit destination address cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new TransmitStatusPacket(frameID, dest16Addr, retryCount, transmitStatus, discoveryStatus);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitStatusPacket#TransmitStatusPacket(int, XBee16BitAddress, int, XBeeTransmitStatus, XBeeDiscoveryStatus)}.
	 * 
	 * <p>Construct a new Transmit Status packet but with a {@code null} 
	 * transmit status. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateTransmitStatusPacketTransmitStatusNull() {
		// Setup the resources for the test.
		int frameID = 5;
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int retryCount = 0;
		XBeeTransmitStatus transmitStatus = null;
		XBeeDiscoveryStatus discoveryStatus = XBeeDiscoveryStatus.DISCOVERY_STATUS_NO_DISCOVERY_OVERHEAD;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Delivery status cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new TransmitStatusPacket(frameID, dest16Addr, retryCount, transmitStatus, discoveryStatus);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitStatusPacket#TransmitStatusPacket(int, XBee16BitAddress, int, XBeeTransmitStatus, XBeeDiscoveryStatus)}.
	 * 
	 * <p>Construct a new Transmit Status packet but with a {@code null} 
	 * discovery status. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateTransmitStatusPacketDiscoveryStatusNull() {
		// Setup the resources for the test.
		int frameID = 5;
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int retryCount = 0;
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.SUCCESS;
		XBeeDiscoveryStatus discoveryStatus = null;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Discovery status cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new TransmitStatusPacket(frameID, dest16Addr, retryCount, transmitStatus, discoveryStatus);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitStatusPacket#TransmitStatusPacket(int, XBee16BitAddress, int, XBeeTransmitStatus, XBeeDiscoveryStatus)}.
	 * 
	 * <p>Construct a new Transmit Status packet but with a frame ID bigger 
	 * than 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateTransmitStatusPacketFrameIDBiggerThan255() {
		// Setup the resources for the test.
		int frameID = 2398;
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int retryCount = 0;
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.SUCCESS;
		XBeeDiscoveryStatus discoveryStatus = XBeeDiscoveryStatus.DISCOVERY_STATUS_NO_DISCOVERY_OVERHEAD;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw a IllegalArgumentException.
		new TransmitStatusPacket(frameID, dest16Addr, retryCount, transmitStatus, discoveryStatus);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitStatusPacket#TransmitStatusPacket(int, XBee16BitAddress, int, XBeeTransmitStatus, XBeeDiscoveryStatus)}.
	 * 
	 * <p>Construct a new Transmit Status packet but with a negative frame ID. 
	 * This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateTransmitStatusPacketFrameIDNegative() {
		// Setup the resources for the test.
		int frameID = -2398;
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int retryCount = 0;
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.SUCCESS;
		XBeeDiscoveryStatus discoveryStatus = XBeeDiscoveryStatus.DISCOVERY_STATUS_NO_DISCOVERY_OVERHEAD;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw a IllegalArgumentException.
		new TransmitStatusPacket(frameID, dest16Addr, retryCount, transmitStatus, discoveryStatus);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitStatusPacket#TransmitStatusPacket(int, XBee16BitAddress, int, XBeeTransmitStatus, XBeeDiscoveryStatus)}.
	 * 
	 * <p>Construct a new Transmit Status packet but with a retry count bigger 
	 * than 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateTransmitStatusPacketRetryCountBiggerThan255() {
		// Setup the resources for the test.
		int frameID = 85;
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int retryCount = 865;
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.SUCCESS;
		XBeeDiscoveryStatus discoveryStatus = XBeeDiscoveryStatus.DISCOVERY_STATUS_NO_DISCOVERY_OVERHEAD;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Transmit retry count must be between 0 and 255.")));
		
		// Call the method under test that should throw a IllegalArgumentException.
		new TransmitStatusPacket(frameID, dest16Addr, retryCount, transmitStatus, discoveryStatus);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitStatusPacket#TransmitStatusPacket(int, XBee16BitAddress, int, XBeeTransmitStatus, XBeeDiscoveryStatus)}.
	 * 
	 * <p>Construct a new Transmit Status packet but with a negative retry count. 
	 * This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateTransmitStatusPacketRetryCountNegative() {
		// Setup the resources for the test.
		int frameID = 85;
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int retryCount = -865;
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.SUCCESS;
		XBeeDiscoveryStatus discoveryStatus = XBeeDiscoveryStatus.DISCOVERY_STATUS_NO_DISCOVERY_OVERHEAD;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Transmit retry count must be between 0 and 255.")));
		
		// Call the method under test that should throw a IllegalArgumentException.
		new TransmitStatusPacket(frameID, dest16Addr, retryCount, transmitStatus, discoveryStatus);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitStatusPacket#TransmitStatusPacket(int, XBee16BitAddress, int, XBeeTransmitStatus, XBeeDiscoveryStatus)}.
	 * 
	 * <p>Construct a new Transmit Status packet with valid parameters.</p>
	 */
	@Test
	public final void testCreateTransmitStatusPacketValid() {
		// Setup the resources for the test.
		int frameID = 85;
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int retryCount = 2;
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.SUCCESS;
		XBeeDiscoveryStatus discoveryStatus = XBeeDiscoveryStatus.DISCOVERY_STATUS_NO_DISCOVERY_OVERHEAD;
		
		int expectedLength = 1 /* Frame type */ + 1 /* Frame ID */ + 2 /* 16-bit address */ + 1 /* retry count */ + 1 /* delivery status */ + 1 /* discovery status */;
		
		// Call the method under test.
		TransmitStatusPacket packet = new TransmitStatusPacket(frameID, dest16Addr, retryCount, transmitStatus, discoveryStatus);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination 16-bit address is not the expected one", packet.get16bitDestinationAddress(), is(equalTo(dest16Addr)));
		assertThat("Returned retry count is not the expected one", packet.getTransmitRetryCount(), is(equalTo(retryCount)));
		assertThat("Returned delivery status is not the expected one", packet.getTransmitStatus(), is(equalTo(transmitStatus)));
		assertThat("Returned discovery status is not the expected one", packet.getDiscoveryStatus(), is(equalTo(discoveryStatus)));
		assertThat("Transmit status packet needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitStatusPacket#TransmitStatusPacket(int, XBee16BitAddress, int, XBeeTransmitStatus, XBeeDiscoveryStatus)}.
	 * 
	 * <p>Construct a new Transmit Status packet with valid parameters but an unknown delivery status.</p>
	 */
	@Test
	public final void testCreateTransmitStatusPacketValidWithUnknownDeliveryStatus() {
		// Setup the resources for the test.
		int frameID = 85;
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int retryCount = 2;
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.get(255);
		XBeeDiscoveryStatus discoveryStatus = XBeeDiscoveryStatus.DISCOVERY_STATUS_NO_DISCOVERY_OVERHEAD;
		
		int expectedLength = 1 /* Frame type */ + 1 /* Frame ID */ + 2 /* 16-bit address */ + 1 /* retry count */ + 1 /* delivery status */ + 1 /* discovery status */;
		
		// Call the method under test.
		TransmitStatusPacket packet = new TransmitStatusPacket(frameID, dest16Addr, retryCount, transmitStatus, discoveryStatus);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination 16-bit address is not the expected one", packet.get16bitDestinationAddress(), is(equalTo(dest16Addr)));
		assertThat("Returned retry count is not the expected one", packet.getTransmitRetryCount(), is(equalTo(retryCount)));
		assertThat("Returned delivery status is not the expected one", packet.getTransmitStatus(), is(equalTo(transmitStatus)));
		assertThat("Returned discovery status is not the expected one", packet.getDiscoveryStatus(), is(equalTo(XBeeDiscoveryStatus.DISCOVERY_STATUS_NO_DISCOVERY_OVERHEAD)));
		assertThat("Transmit status packet needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitStatusPacket#TransmitStatusPacket(int, XBee16BitAddress, int, XBeeTransmitStatus, XBeeDiscoveryStatus)}.
	 * 
	 * <p>Construct a new Transmit Status packet with valid parameters but an unknown discovery status.</p>
	 */
	@Test
	public final void testCreateTransmitStatusPacketValidWithUnknownDiscoveryStatus() {
		// Setup the resources for the test.
		int frameID = 85;
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int retryCount = 2;
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.ADDRESS_NOT_FOUND;
		XBeeDiscoveryStatus discoveryStatus = XBeeDiscoveryStatus.DISCOVERY_STATUS_UNKNOWN;
		
		int expectedLength = 1 /* Frame type */ + 1 /* Frame ID */ + 2 /* 16-bit address */ + 1 /* retry count */ + 1 /* delivery status */ + 1 /* discovery status */;
		
		// Call the method under test.
		TransmitStatusPacket packet = new TransmitStatusPacket(frameID, dest16Addr, retryCount, transmitStatus, discoveryStatus);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination 16-bit address is not the expected one", packet.get16bitDestinationAddress(), is(equalTo(dest16Addr)));
		assertThat("Returned retry count is not the expected one", packet.getTransmitRetryCount(), is(equalTo(retryCount)));
		assertThat("Returned delivery status is not the expected one", packet.getTransmitStatus(), is(equalTo(transmitStatus)));
		assertThat("Returned discovery status is not the expected one", packet.getDiscoveryStatus(), is(equalTo(XBeeDiscoveryStatus.DISCOVERY_STATUS_UNKNOWN)));
		assertThat("Transmit status packet needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitStatusPacket#getAPIData()}.
	 * 
	 * <p>Test the get API parameters.</p>
	 */
	@Test
	public final void testGetAPIData() {
		// Setup the resources for the test.
		int frameID = 0x65;
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int retryCount = 2;
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.SUCCESS;
		XBeeDiscoveryStatus discoveryStatus = XBeeDiscoveryStatus.DISCOVERY_STATUS_NO_DISCOVERY_OVERHEAD;
		TransmitStatusPacket packet = new TransmitStatusPacket(frameID, dest16Addr, retryCount, transmitStatus, discoveryStatus);
		
		int expectedLength = 1 /* Frame ID */ + 2 /* 16-bit address */ + 1 /* retry count */ + 1 /* delivery status */ + 1 /* discovery status */;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		System.arraycopy(dest16Addr.getValue(), 0, expectedData, 1, dest16Addr.getValue().length);
		expectedData[3] = (byte)retryCount;
		expectedData[4] = (byte)transmitStatus.getId();
		expectedData[5] = (byte)discoveryStatus.getId();
		
		// Call the method under test.
		byte[] apiData = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", apiData, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitStatusPacket#getAPIData()}.
	 * 
	 * <p>Test the get API parameters with unknown delivery status.</p>
	 */
	@Test
	public final void testGetAPIDataUnknownTransmitStatus() {
		// Setup the resources for the test.
		int frameType = APIFrameType.TRANSMIT_STATUS.getValue();
		int frameID = 0xE7;
		XBee16BitAddress address = new XBee16BitAddress("B45C");
		int retryCount = 3;
		int deliveryStatus = 255;
		int discoveryStatus = XBeeDiscoveryStatus.DISCOVERY_STATUS_NO_DISCOVERY_OVERHEAD.getId();
		
		byte[] payload = new byte[7];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(address.getValue(), 0, payload, 2, address.getValue().length);
		payload[4] = (byte)retryCount;
		payload[5] = (byte)deliveryStatus;
		payload[6] = (byte)discoveryStatus;
		
		TransmitStatusPacket packet = TransmitStatusPacket.createPacket(payload);
		
		byte[] expectedData = new byte[payload.length - 1]; /* Do not include the type */
		System.arraycopy(payload, 1, expectedData, 0, expectedData.length);
		
		// Call the method under test.
		byte[] apiData = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", apiData, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitStatusPacket#getAPIData()}.
	 * 
	 * <p>Test the get API parameters with unknown discovery status.</p>
	 */
	@Test
	public final void testGetAPIDataUnknownDiscoveryStatus() {
		// Setup the resources for the test.
		int frameType = APIFrameType.TRANSMIT_STATUS.getValue();
		int frameID = 0xE7;
		XBee16BitAddress address = new XBee16BitAddress("B45C");
		int retryCount = 3;
		int deliveryStatus = XBeeTransmitStatus.NO_ACK.getId();
		int discoveryStatus = 255;
		
		byte[] payload = new byte[7];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(address.getValue(), 0, payload, 2, address.getValue().length);
		payload[4] = (byte)retryCount;
		payload[5] = (byte)deliveryStatus;
		payload[6] = (byte)discoveryStatus;
		
		TransmitStatusPacket packet = TransmitStatusPacket.createPacket(payload);
		
		byte[] expectedData = new byte[payload.length - 1]; /* Do not include the type */
		System.arraycopy(payload, 1, expectedData, 0, expectedData.length);
		
		// Call the method under test.
		byte[] apiData = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", apiData, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitStatusPacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters.</p>
	 */
	@Test
	public final void testGetAPIPacketParameters() {
		// Setup the resources for the test.
		int frameID = 0x65;
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int retryCount = 2;
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.SUCCESS;
		XBeeDiscoveryStatus discoveryStatus = XBeeDiscoveryStatus.DISCOVERY_STATUS_NO_DISCOVERY_OVERHEAD;
		TransmitStatusPacket packet = new TransmitStatusPacket(frameID, dest16Addr, retryCount, transmitStatus, discoveryStatus);
		
		String expectedDest16Addr = HexUtils.prettyHexString(dest16Addr.getValue());
		String expectedRetryCount = HexUtils.prettyHexString(Integer.toHexString(retryCount)) + " (" + retryCount + ")";
		String expectedTransmitStatus = HexUtils.prettyHexString(Integer.toHexString(transmitStatus.getId())) + " (" + transmitStatus.getDescription() + ")";
		String expectedDiscoveryStatus = HexUtils.prettyHexString(Integer.toHexString(discoveryStatus.getId())) + " (" + discoveryStatus.getDescription() + ")";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(4)));
		assertThat("Destination 16-bit Address is not the expected one", packetParams.get("16-bit dest. address"), is(equalTo(expectedDest16Addr)));
		assertThat("Retry count is not the expected", packetParams.get("Tx. retry count"), is(equalTo(expectedRetryCount)));
		assertThat("Delivery status is not the expected", packetParams.get("Delivery status"), is(equalTo(expectedTransmitStatus)));
		assertThat("Discovery status not the expected", packetParams.get("Discovery status"), is(equalTo(expectedDiscoveryStatus)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitStatusPacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters with unknown transmit status.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersWithUnknownTransmitStatus() {
		// Setup the resources for the test.
		int frameType = APIFrameType.TRANSMIT_STATUS.getValue();
		int frameID = 0xE7;
		XBee16BitAddress address = new XBee16BitAddress("B45C");
		int retryCount = 3;
		int deliveryStatus = 255;
		int discoveryStatus = XBeeDiscoveryStatus.DISCOVERY_STATUS_ADDRESS_DISCOVERY.getId();
		
		byte[] payload = new byte[7];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(address.getValue(), 0, payload, 2, address.getValue().length);
		payload[4] = (byte)retryCount;
		payload[5] = (byte)deliveryStatus;
		payload[6] = (byte)discoveryStatus;
		
		TransmitStatusPacket packet = TransmitStatusPacket.createPacket(payload);
		
		String expectedDest16Addr = HexUtils.prettyHexString(address.getValue());
		String expectedRetryCount = HexUtils.prettyHexString(Integer.toHexString(retryCount)) + " (" + retryCount + ")";
		String expectedTransmitStatus = HexUtils.prettyHexString(Integer.toHexString(deliveryStatus).toUpperCase()) + " (" + XBeeTransmitStatus.UNKNOWN.getDescription() + ")";
		String expectedDiscoveryStatus = HexUtils.prettyHexString(Integer.toHexString(discoveryStatus).toUpperCase()) + " (" + XBeeDiscoveryStatus.DISCOVERY_STATUS_ADDRESS_DISCOVERY.getDescription() + ")";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(4)));
		assertThat("Destination 16-bit Address is not the expected one", packetParams.get("16-bit dest. address"), is(equalTo(expectedDest16Addr)));
		assertThat("Retry count is not the expected", packetParams.get("Tx. retry count"), is(equalTo(expectedRetryCount)));
		assertThat("Delivery status is not the expected", packetParams.get("Delivery status"), is(equalTo(expectedTransmitStatus)));
		assertThat("Discovery status not the expected", packetParams.get("Discovery status"), is(equalTo(expectedDiscoveryStatus)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitStatusPacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters with unknown discovery status.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersWithUnknownDiscoveryStatus() {
		// Setup the resources for the test.
		int frameType = APIFrameType.TRANSMIT_STATUS.getValue();
		int frameID = 0xE7;
		XBee16BitAddress address = new XBee16BitAddress("B45C");
		int retryCount = 3;
		int deliveryStatus = XBeeTransmitStatus.NO_ACK.getId();
		int discoveryStatus = 255;
		
		byte[] payload = new byte[7];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(address.getValue(), 0, payload, 2, address.getValue().length);
		payload[4] = (byte)retryCount;
		payload[5] = (byte)deliveryStatus;
		payload[6] = (byte)discoveryStatus;
		
		TransmitStatusPacket packet = TransmitStatusPacket.createPacket(payload);
		
		String expectedDest16Addr = HexUtils.prettyHexString(address.getValue());
		String expectedRetryCount = HexUtils.prettyHexString(Integer.toHexString(retryCount)) + " (" + retryCount + ")";
		String expectedTransmitStatus = HexUtils.prettyHexString(Integer.toHexString(deliveryStatus).toUpperCase()) + " (" + XBeeTransmitStatus.NO_ACK.getDescription() + ")";
		String expectedDiscoveryStatus = HexUtils.prettyHexString(Integer.toHexString(discoveryStatus).toUpperCase()) + " (" + XBeeDiscoveryStatus.DISCOVERY_STATUS_UNKNOWN.getDescription() + ")";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(4)));
		assertThat("Destination 16-bit Address is not the expected one", packetParams.get("16-bit dest. address"), is(equalTo(expectedDest16Addr)));
		assertThat("Retry count is not the expected", packetParams.get("Tx. retry count"), is(equalTo(expectedRetryCount)));
		assertThat("Delivery status is not the expected", packetParams.get("Delivery status"), is(equalTo(expectedTransmitStatus)));
		assertThat("Discovery status not the expected", packetParams.get("Discovery status"), is(equalTo(expectedDiscoveryStatus)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.TransmitStatusPacket#isBroadcast()}.
	 * 
	 * <p>Test if a Transmit status packet is a broadcast packet, never should 
	 * be.</p>
	 */
	@Test
	public final void testIsBroadcast() {
		// Setup the resources for the test.
		int frameID = 0x65;
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int retryCount = 2;
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.SUCCESS;
		XBeeDiscoveryStatus discoveryStatus = XBeeDiscoveryStatus.DISCOVERY_STATUS_NO_DISCOVERY_OVERHEAD;
		TransmitStatusPacket packet = new TransmitStatusPacket(frameID, dest16Addr, retryCount, transmitStatus, discoveryStatus);
		
		// Call the method under test and verify the result.
		assertThat("Packet should not be broadcast", packet.isBroadcast(), is(equalTo(false)));
	}
}
