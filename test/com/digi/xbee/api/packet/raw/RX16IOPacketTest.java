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
import static org.hamcrest.core.IsNull.nullValue;

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

public class RX16IOPacketTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	public RX16IOPacketTest() {
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
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16IOPacket#createPacket(byte[])}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a 
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Setup the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("RX16 Address IO packet payload cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		RX16IOPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16IOPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Setup the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete RX16 Address IO packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		RX16IOPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16IOPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Setup the resources for the test.
		int frameType = APIFrameType.RX_IO_16.getValue();
		XBee16BitAddress source16Addr = new XBee16BitAddress("A1B2");
		int rssi = 40;
		
		byte[] payload = new byte[4];
		payload[0] = (byte)frameType;
		System.arraycopy(source16Addr.getValue(), 0, payload, 1, source16Addr.getValue().length);
		payload[3] = (byte)rssi;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete RX16 Address IO packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		RX16IOPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RX16IOPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketReceivedDataShorterThanNeeded() {
		// Setup the resources for the test.
		int frameType = APIFrameType.RX_IO_16.getValue();
		XBee16BitAddress source16Addr = new XBee16BitAddress("A1B2");
		int rssi = 40;
		int options = 0x01;
		byte[] data = new byte[]{(byte)0xFF, (byte)0xFF};
		
		byte[] payload = new byte[5 + data.length];
		payload[0] = (byte)frameType;
		System.arraycopy(source16Addr.getValue(), 0, payload, 1, source16Addr.getValue().length);
		payload[3] = (byte)rssi;
		payload[4] = (byte)options;
		System.arraycopy(data, 0, payload, 5, data.length);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("IO sample payload must be longer than 4.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		RX16IOPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16IOPacket#createPacket(byte[])}.
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
		byte[] data = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		
		byte[] payload = new byte[4 + data.length];
		System.arraycopy(source16Addr.getValue(), 0, payload, 0, source16Addr.getValue().length);
		payload[2] = (byte)rssi;
		payload[3] = (byte)options;
		System.arraycopy(data, 0, payload, 4, data.length);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not a RX16 Address IO packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		RX16IOPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16IOPacket#createPacket(byte[])}.
	 * 
	 * <p>A valid API RX16 Address IO packet with the provided options without 
	 * RF data is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutData() {
		// Setup the resources for the test.
		int frameType = APIFrameType.RX_IO_16.getValue();
		XBee16BitAddress source16Addr = new XBee16BitAddress("A1B2");
		int rssi = 40;
		int options = 0x01;
		
		byte[] payload = new byte[5];
		payload[0] = (byte)frameType;
		System.arraycopy(source16Addr.getValue(), 0, payload, 1, source16Addr.getValue().length);
		payload[3] = (byte)rssi;
		payload[4] = (byte)options;
		
		// Call the method under test.
		RX16IOPacket packet = RX16IOPacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned source 16-bit address is not the expected one", packet.getSourceAddress(), is(equalTo(source16Addr)));
		assertThat("Returned RSSI is not the expected one", packet.getRSSI(), is(equalTo(rssi)));
		assertThat("Returned received options is not the expected one", packet.getReceiveOptions(), is(equalTo(options)));
		assertThat("Returned Received Data is not the expected one", packet.getReceivedData(), is(nullValue()));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX16IOPacket#createPacket(byte[])}.
	 * 
	 * <p>A valid API RX16 Address IO packet with the provided options and RF 
	 * data is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithData() {
		// Setup the resources for the test.
		int frameType = APIFrameType.RX_IO_16.getValue();
		XBee16BitAddress source16Addr = new XBee16BitAddress("A1B2");
		int rssi = 40;
		int options = 0x01;
		byte[] data = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		
		byte[] payload = new byte[5 + data.length];
		payload[0] = (byte)frameType;
		System.arraycopy(source16Addr.getValue(), 0, payload, 1, source16Addr.getValue().length);
		payload[3] = (byte)rssi;
		payload[4] = (byte)options;
		System.arraycopy(data, 0, payload, 5, data.length);
		
		// Call the method under test.
		RX16IOPacket packet = RX16IOPacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned source 16-bit address is not the expected one", packet.getSourceAddress(), is(equalTo(source16Addr)));
		assertThat("Returned RSSI is not the expected one", packet.getRSSI(), is(equalTo(rssi)));
		assertThat("Returned received options is not the expected one", packet.getReceiveOptions(), is(equalTo(options)));
		assertThat("Returned Received Data is not the expected one", packet.getReceivedData(), is(equalTo(data)));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
}
