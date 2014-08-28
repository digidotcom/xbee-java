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

import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.packet.APIFrameType;

public class RX64IOPacketTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	public RX64IOPacketTest() {
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
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64IOPacket#createPacket(byte[])}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a 
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Setup the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("RX64 Address IO packet payload cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		RX64IOPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64IOPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Setup the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete RX64 Address IO packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		RX64IOPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64IOPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Setup the resources for the test.
		int frameType = APIFrameType.RX_IO_64.getValue();
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		int rssi = 40;
		
		byte[] payload = new byte[10];
		payload[0] = (byte)frameType;
		System.arraycopy(source64Addr.getValue(), 0, payload, 1, source64Addr.getValue().length);
		payload[9] = (byte)rssi;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete RX64 Address IO packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		RX64IOPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RX64IOPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketReceivedDataShorterThanNeeded() {
		// Setup the resources for the test.
		int frameType = APIFrameType.RX_IO_64.getValue();
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		int rssi = 40;
		byte[] data = new byte[]{(byte)0xFF, (byte)0xFF};
		
		byte[] payload = new byte[10 + data.length];
		payload[0] = (byte)frameType;
		System.arraycopy(source64Addr.getValue(), 0, payload, 1, source64Addr.getValue().length);
		payload[9] = (byte)rssi;
		System.arraycopy(data, 0, payload, 10, data.length);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("IO sample payload must be longer than 4.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		RX64IOPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64IOPacket#createPacket(byte[])}.
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
		byte[] data = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		
		byte[] payload = new byte[10 + data.length];
		System.arraycopy(source64Addr.getValue(), 0, payload, 0, source64Addr.getValue().length);
		payload[8] = (byte)rssi;
		payload[9] = (byte)options;
		System.arraycopy(data, 0, payload, 10, data.length);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not a RX64 Address IO packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		RX64IOPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64IOPacket#createPacket(byte[])}.
	 * 
	 * <p>A valid RX64 Address IO packet with the provided options without RF 
	 * data is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutData() {
		// Setup the resources for the test.
		int frameType = APIFrameType.RX_IO_64.getValue();
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		int rssi = 40;
		int options = 0x01;
		
		byte[] payload = new byte[11];
		payload[0] = (byte)frameType;
		System.arraycopy(source64Addr.getValue(), 0, payload, 1, source64Addr.getValue().length);
		payload[9] = (byte)rssi;
		payload[10] = (byte)options;
		
		// Call the method under test.
		RX64IOPacket packet = RX64IOPacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned source 64-bit address is not the expected one", packet.getSourceAddress(), is(equalTo(source64Addr)));
		assertThat("Returned RSSI is not the expected one", packet.getRSSI(), is(equalTo(rssi)));
		assertThat("Returned received options is not the expected one", packet.getReceiveOptions(), is(equalTo(options)));
		assertThat("Returned Received Data is not the expected one", packet.getReceivedData(), is(nullValue()));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.raw.RX64IOPacket#createPacket(byte[])}.
	 * 
	 * <p>A valid RX64 Address IO packet with the provided options and RF data 
	 * is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithData() {
		// Setup the resources for the test.
		int frameType = APIFrameType.RX_IO_64.getValue();
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		int rssi = 40;
		int options = 0x01;
		byte[] data = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		
		byte[] payload = new byte[11 + data.length];
		payload[0] = (byte)frameType;
		System.arraycopy(source64Addr.getValue(), 0, payload, 1, source64Addr.getValue().length);
		payload[9] = (byte)rssi;
		payload[10] = (byte)options;
		System.arraycopy(data, 0, payload, 11, data.length);
		
		// Call the method under test.
		RX64IOPacket packet = RX64IOPacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned source 64-bit address is not the expected one", packet.getSourceAddress(), is(equalTo(source64Addr)));
		assertThat("Returned RSSI is not the expected one", packet.getRSSI(), is(equalTo(rssi)));
		assertThat("Returned received options is not the expected one", packet.getReceiveOptions(), is(equalTo(options)));
		assertThat("Returned Received Data is not the expected one", packet.getReceivedData(), is(equalTo(data)));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
}
