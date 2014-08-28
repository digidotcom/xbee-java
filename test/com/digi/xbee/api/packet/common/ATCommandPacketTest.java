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
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.digi.xbee.api.packet.APIFrameType;

public class ATCommandPacketTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	public ATCommandPacketTest() {
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
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandPacket#createPacket(byte[])}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a 
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Setup the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("AT Command packet payload cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		ATCommandPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Setup the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete AT Command packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		ATCommandPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Setup the resources for the test.
		int frameType = APIFrameType.AT_COMMAND.getValue();
		//int frameID = 0xE7;
		byte[] atCommand = "NI".getBytes();
		
		byte[] payload = new byte[1 + atCommand.length];
		payload[0] = (byte)frameType;
		//payload[1] = (byte)frameID;
		System.arraycopy(atCommand, 0, payload, 1, atCommand.length);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete AT Command packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		ATCommandPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandPacket#createPacket(byte[])}.
	 * 
	 * <p>If the AT command is longer than 2 characters, then the AT command is 
	 * considered to be only the 2 first chars, and the rest part of the command
	 * data.</p>
	 */
	@Test
	public final void testCreatePacketATCommandLonger() {
		// Setup the resources for the test.
		int frameType = APIFrameType.AT_COMMAND.getValue();
		int frameID = 0xE7;
		byte[] atCommand = "NIA".getBytes();
		
		byte[] payload = new byte[2 + atCommand.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(atCommand, 0, payload, 2, atCommand.length);
		
		
		// Call the method under test.
		ATCommandPacket packet = ATCommandPacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String("NI"))));
		assertThat("Returned Command Data is not the expected one", packet.getParameterAsString(), is(equalTo("A")));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array not including the Frame type.</p>
	 */
	@Test
	public final void testCreatePacketPayloadNotIncludingFrameType() {
		// Setup the resources for the test.
		int frameID = 0xE7;
		byte[] atCommand = "NI".getBytes();
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		byte[] payload = new byte[1 + atCommand.length + data.length];
		payload[0] = (byte)frameID;
		System.arraycopy(atCommand, 0, payload, 1, atCommand.length);
		System.arraycopy(data, 0, payload, 1 + atCommand.length, data.length);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not an AT Command packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		ATCommandPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandPacket#createPacket(byte[])}.
	 * 
	 * <p>A valid AT Command packet with the provided options without command 
	 * data is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutData() {
		// Setup the resources for the test.
		int frameType = APIFrameType.AT_COMMAND.getValue();
		int frameID = 0xE7;
		byte[] atCommand = "NI".getBytes();
		
		byte[] payload = new byte[2 + atCommand.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(atCommand, 0, payload, 2, atCommand.length);
		
		// Call the method under test.
		ATCommandPacket packet = ATCommandPacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(atCommand))));
		assertThat("Returned Command Data is not the expected one", packet.getParameter(), is(nullValue()));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandPacket#createPacket(byte[])}.
	 * 
	 * <p>A valid AT Command packet with the provided options with command data 
	 * is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithData() {
		// Setup the resources for the test.
		int frameType = APIFrameType.AT_COMMAND.getValue();
		int frameID = 0xE7;
		byte[] atCommand = "NI".getBytes();
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		byte[] payload = new byte[2 + atCommand.length + data.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(atCommand, 0, payload, 2, atCommand.length);
		System.arraycopy(data, 0, payload, 2 + atCommand.length, data.length);
		
		// Call the method under test.
		ATCommandPacket packet = ATCommandPacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(atCommand))));
		assertThat("Returned Command Data is not the expected one", packet.getParameter(), is(equalTo(data)));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
}
