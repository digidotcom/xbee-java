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

import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.packet.APIFrameType;

public class RemoteATCommandResponsePacketTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	public RemoteATCommandResponsePacketTest() {
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
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket#createPacket(byte[])}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a 
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Setup the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Remote AT Command Response packet payload cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		RemoteATCommandResponsePacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Setup the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Remote AT Command Response packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		RemoteATCommandResponsePacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Setup the resources for the test.
		int frameType = APIFrameType.REMOTE_AT_COMMAND_RESPONSE.getValue();
		//int frameID = 0xE7;
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		byte[] atCommand = "NI".getBytes();
		int status = ATCommandStatus.OK.getId();
		
		byte[] payload = new byte[12 + atCommand.length];
		payload[0] = (byte)frameType;
		//payload[1] = (byte)frameID;
		System.arraycopy(source64Addr.getValue(), 0, payload, 1, source64Addr.getValue().length);
		System.arraycopy(source16Addr.getValue(), 0, payload, 9, source16Addr.getValue().length);
		System.arraycopy(atCommand, 0, payload, 11, atCommand.length);
		payload[11 + atCommand.length] = (byte)status;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Remote AT Command Response packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		RemoteATCommandResponsePacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array not including the Frame type.</p>
	 */
	@Test
	public final void testCreatePacketPayloadNotIncludingFrameType() {
		// Setup the resources for the test.
		int frameID = 0xE7;
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		byte[] atCommand = "NI".getBytes();
		int status = ATCommandStatus.OK.getId();
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		byte[] payload = new byte[12 + atCommand.length + data.length];
		payload[0] = (byte)frameID;
		System.arraycopy(source64Addr.getValue(), 0, payload, 1, source64Addr.getValue().length);
		System.arraycopy(source16Addr.getValue(), 0, payload, 9, source16Addr.getValue().length);
		System.arraycopy(atCommand, 0, payload, 11, atCommand.length);
		payload[11 + atCommand.length] = (byte)status;
		System.arraycopy(data, 0, payload, 11 + atCommand.length, data.length);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not a Remote AT Command Response packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		RemoteATCommandResponsePacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket#createPacket(byte[])}.
	 * 
	 * <p>A valid Remote AT Command Response packet with the provided options 
	 * without data value is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutData() {
		// Setup the resources for the test.
		int frameType = APIFrameType.REMOTE_AT_COMMAND_RESPONSE.getValue();
		int frameID = 0xE7;
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		byte[] atCommand = "NI".getBytes();
		int status = ATCommandStatus.OK.getId();
		
		byte[] payload = new byte[13 + atCommand.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(source64Addr.getValue(), 0, payload, 2, source64Addr.getValue().length);
		System.arraycopy(source16Addr.getValue(), 0, payload, 10, source16Addr.getValue().length);
		System.arraycopy(atCommand, 0, payload, 12, atCommand.length);
		payload[12 + atCommand.length] = (byte)status;
		
		// Call the method under test.
		RemoteATCommandResponsePacket packet = RemoteATCommandResponsePacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned source 64-bit address is not the expected one", packet.get64bitSourceAddress(), is(equalTo(source64Addr)));
		assertThat("Returned source 16-bit address is not the expected one", packet.get16bitSourceAddress(), is(equalTo(source16Addr)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(atCommand))));
		assertThat("Returned status is not the expected one", packet.getStatus().getId(), is(equalTo(status)));
		assertThat("Returned value is not the expected one", packet.getCommandValue(), is(nullValue()));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket#createPacket(byte[])}.
	 * 
	 * <p>A valid Remote AT Command Response packet with the provided options 
	 * with value is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithData() {
		// Setup the resources for the test.
		int frameType = APIFrameType.REMOTE_AT_COMMAND_RESPONSE.getValue();
		int frameID = 0xE7;
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		byte[] atCommand = "NI".getBytes();
		int status = ATCommandStatus.OK.getId();
		byte[] value = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		byte[] payload = new byte[13 + atCommand.length + value.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(source64Addr.getValue(), 0, payload, 2, source64Addr.getValue().length);
		System.arraycopy(source16Addr.getValue(), 0, payload, 10, source16Addr.getValue().length);
		System.arraycopy(atCommand, 0, payload, 12, atCommand.length);
		payload[12 + atCommand.length] = (byte)status;
		System.arraycopy(value, 0, payload, 13 + atCommand.length, value.length);
		
		// Call the method under test.
		RemoteATCommandResponsePacket packet = RemoteATCommandResponsePacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned source 64-bit address is not the expected one", packet.get64bitSourceAddress(), is(equalTo(source64Addr)));
		assertThat("Returned source 16-bit address is not the expected one", packet.get16bitSourceAddress(), is(equalTo(source16Addr)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(atCommand))));
		assertThat("Returned status is not the expected one", packet.getStatus().getId(), is(equalTo(status)));
		assertThat("Returned Command value is not the expected one", packet.getCommandValue(), is(equalTo(value)));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
}
