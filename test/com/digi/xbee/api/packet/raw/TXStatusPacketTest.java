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
package com.digi.xbee.api.packet.raw;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.Is.is;

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
		assertThat("Returned status is not the expected one", packet.getTransmitStatus().getId(), is(equalTo(status)));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
}
