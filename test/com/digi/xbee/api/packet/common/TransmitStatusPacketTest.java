/**
 * Copyright (c) 2014 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */
package com.digi.xbee.api.packet.common;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
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
		assertThat("Returned destination 16-bit address is not the expected one", packet.get16BitDestinationAddress(), is(equalTo(address)));
		assertThat("Returned retry count is not the expected one", packet.getTransmitRetryCount(), is(equalTo(retryCount)));
		assertThat("Returned delivery status is not the expected one", packet.getTransmitStatus().getId(), is(equalTo(deliveryStatus)));
		assertThat("Returned discovery status is not the expected one", packet.getDiscoveryStatus().getId(), is(equalTo(discoveryStatus)));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
}
