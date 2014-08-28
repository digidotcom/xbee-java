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
package com.digi.xbee.api.packet;

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

public class UnkownXBeePacketTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();

	public UnkownXBeePacketTest() {
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
	 * Test method for {@link com.digi.xbee.api.packet.UnknownXBeePacket#createPacket(byte[])}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a 
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Setup the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Unknown packet payload cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		UnknownXBeePacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.UnknownXBeePacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Setup the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Unknown packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		UnknownXBeePacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.UnknownXBeePacket#createPacket(byte[])}.
	 * 
	 * <p>A valid API Generic packet with the provided options.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutData() {
		// Setup the resources for the test.
		int frameType = 0x50;
		
		byte[] payload = new byte[1];
		payload[0] = (byte)frameType;
		
		// Call the method under test.
		UnknownXBeePacket packet = UnknownXBeePacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned Received Data is not the expected one", packet.getRFData(), is(nullValue()));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.UnknownXBeePacket#createPacket(byte[])}.
	 * 
	 * <p>A valid API Generic packet with the provided options.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithData() {
		// Setup the resources for the test.
		int frameType = 0x60;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		byte[] payload = new byte[1 + data.length];
		payload[0] = (byte)frameType;
		System.arraycopy(data, 0, payload, 1, data.length);
		
		// Call the method under test.
		UnknownXBeePacket packet = UnknownXBeePacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned Received Data is not the expected one", packet.getRFData(), is(equalTo(data)));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
}
