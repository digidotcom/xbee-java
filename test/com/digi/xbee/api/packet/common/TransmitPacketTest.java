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
import static org.hamcrest.Matchers.nullValue;
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
		assertThat("Returned destination 64-bit address is not the expected one", packet.get64BitDestinationAddress(), is(equalTo(dest64Addr)));
		assertThat("Returned destination 16-bit address is not the expected one", packet.get16BitDestinationAddress(), is(equalTo(dest16Addr)));
		assertThat("Returned broadcast radious is not the expected one", packet.getBroadCastRadius(), is(equalTo(broadcastRadious)));
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
		assertThat("Returned destination 64-bit address is not the expected one", packet.get64BitDestinationAddress(), is(equalTo(dest64Addr)));
		assertThat("Returned destination 16-bit address is not the expected one", packet.get16BitDestinationAddress(), is(equalTo(dest16Addr)));
		assertThat("Returned broadcast radious is not the expected one", packet.getBroadCastRadius(), is(equalTo(broadcastRadious)));
		assertThat("Returned transmit options is not the expected one", packet.getTransmitOptions(), is(equalTo(options)));
		assertThat("Returned RF Data is not the expected one", packet.getRFData(), is(equalTo(data)));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
}
