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

import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.packet.APIFrameType;

public class ATCommandResponsePacketTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	public ATCommandResponsePacketTest() {
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
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#createPacket(byte[])}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a 
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Setup the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("AT Command Response packet payload cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		ATCommandResponsePacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Setup the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete AT Command Response packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		ATCommandResponsePacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Setup the resources for the test.
		int frameType = APIFrameType.AT_COMMAND_RESPONSE.getValue();
		//int frameID = 0xE7;
		byte[] atCommand = "NI".getBytes();
		int status = ATCommandStatus.OK.getId();
		
		byte[] payload = new byte[2 + atCommand.length];
		payload[0] = (byte)frameType;
		//payload[1] = (byte)frameID;
		System.arraycopy(atCommand, 0, payload, 1, atCommand.length);
		payload[3] = (byte)status;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete AT Command Response packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		ATCommandResponsePacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array not including the Frame type.</p>
	 */
	@Test
	public final void testCreatePacketPayloadNotIncludingFrameType() {
		// Setup the resources for the test.
		int frameID = 0xE7;
		byte[] atCommand = "NI".getBytes();
		int status = ATCommandStatus.OK.getId();
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		byte[] payload = new byte[2 + atCommand.length + data.length];
		payload[0] = (byte)frameID;
		System.arraycopy(atCommand, 0, payload, 1, atCommand.length);
		payload[3] = (byte)status;
		System.arraycopy(data, 0, payload, 2 + atCommand.length, data.length);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not an AT Command Response packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		ATCommandResponsePacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#createPacket(byte[])}.
	 * 
	 * <p>A valid AT Command Response packet with the provided options without 
	 * command value is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutData() {
		// Setup the resources for the test.
		int frameType = APIFrameType.AT_COMMAND_RESPONSE.getValue();
		int frameID = 0xE7;
		byte[] atCommand = "NI".getBytes();
		int status = ATCommandStatus.OK.getId();
		
		byte[] payload = new byte[2 + atCommand.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(atCommand, 0, payload, 2, atCommand.length);
		payload[4] = (byte)status;
		
		// Call the method under test.
		ATCommandResponsePacket packet = ATCommandResponsePacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(atCommand))));
		assertThat("Returned status is not the expected one", packet.getStatus().getId(), is(equalTo(status)));
		assertThat("Returned Command value is not the expected one", packet.getCommandValue(), is(nullValue()));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#createPacket(byte[])}.
	 * 
	 * <p>A valid AT Command Response packet with the provided options with 
	 * command value is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithData() {
		// Setup the resources for the test.
		int frameType = APIFrameType.AT_COMMAND_RESPONSE.getValue();
		int frameID = 0xE7;
		byte[] atCommand = "NI".getBytes();
		int status = ATCommandStatus.OK.getId();
		byte[] value = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		byte[] payload = new byte[3 + atCommand.length + value.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(atCommand, 0, payload, 2, atCommand.length);
		payload[2 + atCommand.length] = (byte)status;
		System.arraycopy(value, 0, payload, 3 + atCommand.length, value.length);
		
		// Call the method under test.
		ATCommandResponsePacket packet = ATCommandResponsePacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(atCommand))));
		assertThat("Returned status is not the expected one", packet.getStatus().getId(), is(equalTo(status)));
		assertThat("Returned Command value is not the expected one", packet.getCommandValue(), is(equalTo(value)));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
}
