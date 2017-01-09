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
import static org.hamcrest.core.IsNull.nullValue;

import java.util.LinkedHashMap;

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
import com.digi.xbee.api.utils.HexUtils;

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
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket#RemoteATCommandResponsePacket(int, XBee64BitAddress, XBee16BitAddress, String, ATCommandStatus, byte[])}.
	 * 
	 * <p>Construct a new Remote AT Command Response packet but with a 
	 * {@code null} 64-bit destination address. This must throw a 
	 * {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandResponsePacket64bitAddressNull() {
		// Setup the resources for the test.
		int frameID = 0x01;
		XBee64BitAddress source64Addr = null;
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		ATCommandStatus status = ATCommandStatus.OK;
		String command = null;
		byte[] parameter = "Param value".getBytes();
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("64-bit source address cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new RemoteATCommandResponsePacket(frameID, source64Addr, source16Addr, command, status, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket#RemoteATCommandResponsePacket(int, XBee64BitAddress, XBee16BitAddress, String, ATCommandStatus, byte[])}.
	 * 
	 * <p>Construct a new Remote AT Command Response packet but with a 
	 * {@code null} 16-bit destination address. This must throw a 
	 * {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandResponsePacket16titAddressNull() {
		// Setup the resources for the test.
		int frameID = 0x01;
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = null;
		ATCommandStatus status = ATCommandStatus.OK;
		String command = null;
		byte[] parameter = "Param value".getBytes();
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("16-bit source address cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new RemoteATCommandResponsePacket(frameID, source64Addr, source16Addr, command, status, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket#RemoteATCommandResponsePacket(int, XBee64BitAddress, XBee16BitAddress, String, ATCommandStatus, byte[])}.
	 * 
	 * <p>Construct a new Remote AT Command Response packet but with a 
	 * {@code null} command. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandResponsePacketCommandNull() {
		// Setup the resources for the test.
		int frameID = 0x01;
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		ATCommandStatus status = ATCommandStatus.OK;
		String command = null;
		byte[] parameter = "Param value".getBytes();
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("AT command cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new RemoteATCommandResponsePacket(frameID, source64Addr, source16Addr, command, status, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket#RemoteATCommandResponsePacket(int, XBee64BitAddress, XBee16BitAddress, String, ATCommandStatus, byte[])}.)}.
	 * 
	 * <p>Construct a new Remote AT Command Response packet but with a 
	 * {@code null} status. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandResponsePacketStatusNull() {
		// Setup the resources for the test.
		int frameID = 0x01;
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		ATCommandStatus status = null;
		String command = "NI";
		byte[] parameter = "Param value".getBytes();
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("AT command status cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new RemoteATCommandResponsePacket(frameID, source64Addr, source16Addr, command, status, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket#RemoteATCommandResponsePacket(int, XBee64BitAddress, XBee16BitAddress, String, ATCommandStatus, byte[])}.)}.
	 * 
	 * <p>Construct a new Remtoe AT Command Response packet but with a frame ID 
	 * bigger than 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateATCommandResponsePacketFrameIDBiggerThan255() {
		// Setup the resources for the test.
		int frameID = 2000;
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		ATCommandStatus status = ATCommandStatus.OK;
		String command = "NI";
		byte[] parameter = "Param value".getBytes();
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new RemoteATCommandResponsePacket(frameID, source64Addr, source16Addr, command, status, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket#RemoteATCommandResponsePacket(int, XBee64BitAddress, XBee16BitAddress, String, ATCommandStatus, byte[])}.)}.
	 * 
	 * <p>Construct a new Remote AT Command Response packet but with a negative
	 * frame ID. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateATCommandResponsePacketFrameIDNegative() {
		// Setup the resources for the test.
		int frameID = -98;
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		ATCommandStatus status = ATCommandStatus.OK;
		String command = "NI";
		byte[] parameter = "Param value".getBytes();
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new RemoteATCommandResponsePacket(frameID, source64Addr, source16Addr, command, status, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket#RemoteATCommandResponsePacket(int, XBee64BitAddress, XBee16BitAddress, String, ATCommandStatus, byte[])}.)}.
	 * 
	 * <p>Construct a new Remote AT Command Response packet but with an valid 
	 * parameters but without parameter value ({@code null}).</p>
	 */
	@Test
	public final void testCreateATCommandResponsePacketValidParameterNull() {
		// Setup the resources for the test.
		int frameID = 0x65;
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		ATCommandStatus status = ATCommandStatus.OK;
		String command = "NI";
		byte[] parameter = null;
		
		int expectedLength = 1 /* Frame type */ + 1 /* Frame ID */ + 8 /* 64-bit address */ + 2 /* 16-bit address */ + 1 /* status */ + command.length() /* AT command */;
		
		// Call the method under test.
		RemoteATCommandResponsePacket packet = new RemoteATCommandResponsePacket(frameID, source64Addr, source16Addr, command, status, parameter);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned source 64-bit address is not the expected one", packet.get64bitSourceAddress(), is(equalTo(source64Addr)));
		assertThat("Returned source 16-bit address is not the expected one", packet.get16bitSourceAddress(), is(equalTo(source16Addr)));
		assertThat("Returned status is not the expected one", packet.getStatus(), is(equalTo(status)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(command))));
		assertThat("Returned Command Data is not the expected one", packet.getCommandValue(), is(nullValue(byte[].class)));
		assertThat("Remote AT Command Response needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket#RemoteATCommandResponsePacket(int, XBee64BitAddress, XBee16BitAddress, String, ATCommandStatus, byte[])}.)}.
	 * 
	 * <p>Construct a new Remote AT Command Response packet but with an valid 
	 * parameters but without parameter value ({@code null}).</p>
	 */
	@Test
	public final void testCreateATCommandResponsePacketValidParameter() {
		// Setup the resources for the test.
		int frameID = 0x65;
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		ATCommandStatus status = ATCommandStatus.OK;
		String command = "NI";
		byte[] parameter = "Param value".getBytes();
		
		int expectedLength = 1 /* Frame type */ + 1 /* Frame ID */ + 8 /* 64-bit address */ + 2 /* 16-bit address */ + 1 /* status */ + command.length() /* AT command */ + parameter.length /* value */;
		
		// Call the method under test.
		RemoteATCommandResponsePacket packet = new RemoteATCommandResponsePacket(frameID, source64Addr, source16Addr, command, status, parameter);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned source 64-bit address is not the expected one", packet.get64bitSourceAddress(), is(equalTo(source64Addr)));
		assertThat("Returned source 16-bit address is not the expected one", packet.get16bitSourceAddress(), is(equalTo(source16Addr)));
		assertThat("Returned status is not the expected one", packet.getStatus(), is(equalTo(status)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(command))));
		assertThat("Returned Command Data is not the expected one", packet.getCommandValue(), is(equalTo(parameter)));
		assertThat("Remote AT Command Response needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket#getAPIData()}.
	 * 
	 * <p>Test the get API parameters but with a {@code null} parameter value.</p>
	 */
	@Test
	public final void testGetAPIDataATCommandParameterNull() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		ATCommandStatus status = ATCommandStatus.OK;
		String command = "NI";
		byte[] parameter = null;
		RemoteATCommandResponsePacket packet = new RemoteATCommandResponsePacket(frameID, source64Addr, source16Addr, command, status, parameter);
		
		int expectedLength = 1 /* Frame ID */ + 8 /* 64-bit address */ + 2 /* 16-bit address */ + 1 /* Options */ + command.length() /* AT command */;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		System.arraycopy(source64Addr.getValue(), 0, expectedData, 1, source64Addr.getValue().length);
		System.arraycopy(source16Addr.getValue(), 0, expectedData, 9, source16Addr.getValue().length);
		System.arraycopy(command.getBytes(), 0, expectedData, 11, command.length());
		expectedData[13] = (byte)status.getId();
		
		// Call the method under test.
		byte[] data = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket#getAPIData()}.
	 * 
	 * <p>Test the get API parameters but with a non-{@code null} parameter value.</p>
	 */
	@Test
	public final void testGetAPIDataATCommandParameterNotNull() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "NI";
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		ATCommandStatus status = ATCommandStatus.OK;
		byte[] parameter = new byte[]{0x6D, 0x79, 0x44, 0x65, 0x76, 0x69, 0x63, 0x65};
		RemoteATCommandResponsePacket packet = new RemoteATCommandResponsePacket(frameID, source64Addr, source16Addr, command, status, parameter);
		
		int expectedLength = 1 /* Frame ID */ + 8 /* 64-bit address */ + 2 /* 16-bit address */ + 1 /* Options */ + command.length() /* AT command */ + parameter.length /* Value */;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		System.arraycopy(source64Addr.getValue(), 0, expectedData, 1, source64Addr.getValue().length);
		System.arraycopy(source16Addr.getValue(), 0, expectedData, 9, source16Addr.getValue().length);
		System.arraycopy(command.getBytes(), 0, expectedData, 11, command.length());
		expectedData[13] = (byte)status.getId();
		System.arraycopy(parameter, 0, expectedData, 12 + command.length(), parameter.length);
		
		// Call the method under test.
		byte[] data = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with a {@code null} parameter value.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersATCommandParameterNull() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		ATCommandStatus status = ATCommandStatus.OK;
		String command = "NI";
		byte[] parameter = null;
		RemoteATCommandResponsePacket packet = new RemoteATCommandResponsePacket(frameID, source64Addr, source16Addr, command, status, parameter);
		
		String expectedDest64Addr = HexUtils.prettyHexString(source64Addr.getValue());
		String expectedDest16Addr = HexUtils.prettyHexString(source16Addr.getValue());
		String expectedStatus = HexUtils.prettyHexString(HexUtils.integerToHexString(status.getId(), 1)) + " (" + status.getDescription() + ")";
		String expectedATCommand = HexUtils.prettyHexString(command.getBytes()) + " (" + command + ")";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(4)));
		assertThat("Source 64-bit Address is not the expected one", packetParams.get("64-bit source address"), is(equalTo(expectedDest64Addr)));
		assertThat("Source 16-bit Address is not the expected one", packetParams.get("16-bit source address"), is(equalTo(expectedDest16Addr)));
		assertThat("Command status is not the expected one", packetParams.get("Status"), is(equalTo(expectedStatus)));
		assertThat("AT Command is not the expected one", packetParams.get("AT Command"), is(equalTo(expectedATCommand)));
		assertThat("AT Parameter is not the expected one", packetParams.get("Response"), is(nullValue(String.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with a parameter value.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersATCommandParameterNonNull() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		ATCommandStatus status = ATCommandStatus.OK;
		String command = "NI";
		byte[] parameter = "myDevice".getBytes();
		RemoteATCommandResponsePacket packet = new RemoteATCommandResponsePacket(frameID, source64Addr, source16Addr, command, status, parameter);
		
		String expectedDest64Addr = HexUtils.prettyHexString(source64Addr.getValue());
		String expectedDest16Addr = HexUtils.prettyHexString(source16Addr.getValue());
		String expectedStatus = HexUtils.prettyHexString(HexUtils.integerToHexString(status.getId(), 1)) + " (" + status.getDescription() + ")";
		String expectedATCommand = HexUtils.prettyHexString(command.getBytes()) + " (" + command + ")";
		String expectedATParameter = HexUtils.prettyHexString(parameter) + " (" + new String(parameter) + ")";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(5)));
		assertThat("Source 64-bit Address is not the expected one", packetParams.get("64-bit source address"), is(equalTo(expectedDest64Addr)));
		assertThat("Source 16-bit Address is not the expected one", packetParams.get("16-bit source address"), is(equalTo(expectedDest16Addr)));
		assertThat("Command status is not the expected one", packetParams.get("Status"), is(equalTo(expectedStatus)));
		assertThat("AT Command is not the expected one", packetParams.get("AT Command"), is(equalTo(expectedATCommand)));
		assertThat("AT Parameter is not the expected one", packetParams.get("Response"), is(equalTo(expectedATParameter)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with a parameter value.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersATCommandParameterByteArrayNonStringCmd() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		ATCommandStatus status = ATCommandStatus.OK;
		String command = "DL";
		byte[] parameter = new byte[]{0x6D, 0x79};
		RemoteATCommandResponsePacket packet = new RemoteATCommandResponsePacket(frameID, source64Addr, source16Addr, command, status, parameter);
		
		String expectedDest64Addr = HexUtils.prettyHexString(source64Addr.getValue());
		String expectedDest16Addr = HexUtils.prettyHexString(source16Addr.getValue());
		String expectedStatus = HexUtils.prettyHexString(HexUtils.integerToHexString(status.getId(), 1)) + " (" + status.getDescription() + ")";
		String expectedATCommand = HexUtils.prettyHexString(command.getBytes()) + " (" + command + ")";
		String expectedATParameter = HexUtils.prettyHexString(parameter);
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(5)));
		assertThat("Source 64-bit Address is not the expected one", packetParams.get("64-bit source address"), is(equalTo(expectedDest64Addr)));
		assertThat("Source 16-bit Address is not the expected one", packetParams.get("16-bit source address"), is(equalTo(expectedDest16Addr)));
		assertThat("Command status is not the expected one", packetParams.get("Status"), is(equalTo(expectedStatus)));
		assertThat("AT Command is not the expected one", packetParams.get("AT Command"), is(equalTo(expectedATCommand)));
		assertThat("AT Parameter is not the expected one", packetParams.get("Response"), is(equalTo(expectedATParameter)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket#isBroadcast()}.
	 * 
	 * <p>Test if a Remote AT command response packet is a broadcast packet, 
	 * never should be.</p>
	 */
	@Test
	public final void testIsBroadcast() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		ATCommandStatus status = ATCommandStatus.OK;
		String command = "DL";
		byte[] parameter = new byte[]{0x6D, 0x79};
		RemoteATCommandResponsePacket packet = new RemoteATCommandResponsePacket(frameID, source64Addr, source16Addr, command, status, parameter);
		
		// Call the method under test and verify the result.
		assertThat("Packet should not be broadcast", packet.isBroadcast(), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket#setCommandValue(String)}.
	 * 
	 * <p>Test if a string command value is properly configured.</p>
	 */
	@Test
	public final void testSetCommandValueString() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		ATCommandStatus status = ATCommandStatus.OK;
		String command = "NI";
		String valueToSet = "newNIValue";
		RemoteATCommandResponsePacket packet = new RemoteATCommandResponsePacket(frameID, source64Addr, source16Addr, command, status, new byte[0]);
		
		// Call the method under test.
		packet.setCommandValue(valueToSet);
		
		// Verify the result.
		assertThat("Configured command value must be '" + valueToSet + "'", 
				packet.getCommandValueAsString(), is(equalTo(valueToSet)));
		assertThat("Configured command value must be '" + valueToSet + "'", 
				packet.getCommandValue(), is(equalTo(valueToSet.getBytes())));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket#setCommandValue(String)}.
	 * 
	 * <p>Test if a string command value with {@code null} value is properly 
	 * configured.</p>
	 */
	@Test
	public final void tesSetCommandValueStringNull() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		ATCommandStatus status = ATCommandStatus.OK;
		String command = "NI";
		RemoteATCommandResponsePacket packet = new RemoteATCommandResponsePacket(frameID, source64Addr, source16Addr, command, status, new byte[0]);
		
		// Call the method under test.
		packet.setCommandValue((String)null);
		
		// Verify the result.
		assertThat("Configured command value must be 'null'", 
				packet.getCommandValueAsString(), is(equalTo(null)));
		assertThat("Configured command value must be 'null'", 
				packet.getCommandValue(), is(equalTo(null)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket#setCommandValue(byte[])}.
	 * 
	 * <p>Test if a byte array command value is properly configured.</p>
	 */
	@Test
	public final void testSetCommandValueByteArray() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		ATCommandStatus status = ATCommandStatus.OK;
		String command = "NI";
		byte[] valueToSet = new byte[]{0x6D, 0x79, 0x44, 0x65, 0x76, 0x69, 0x63, 0x65};
		RemoteATCommandResponsePacket packet = new RemoteATCommandResponsePacket(frameID, source64Addr, source16Addr, command, status, new byte[0]);
		
		// Call the method under test.
		packet.setCommandValue(valueToSet);
		
		// Verify the result.
		assertThat("Configured command value must be '" + new String(valueToSet) + "'", 
				packet.getCommandValue(), is(equalTo(valueToSet)));
		assertThat("Configured command value must be '" + valueToSet + "'", 
				packet.getCommandValueAsString(), is(equalTo(new String(valueToSet))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket#setCommandValue(byte[])}.
	 * 
	 * <p>Test if a byte array command value with {@code null} value is properly 
	 * configured.</p>
	 */
	@Test
	public final void testSetCommandValueByteArrayNull() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		ATCommandStatus status = ATCommandStatus.OK;
		String command = "NI";
		RemoteATCommandResponsePacket packet = new RemoteATCommandResponsePacket(frameID, source64Addr, source16Addr, command, status, new byte[0]);
		
		// Call the method under test.
		packet.setCommandValue((byte[])null);
		
		// Verify the result.
		assertThat("Configured command value must be 'null'", packet.getCommandValue(), is(equalTo(null)));
		assertThat("Configured command value must be 'null'", packet.getCommandValueAsString(), is(equalTo(null)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket#getCommandValueAsString()}.
	 * 
	 * <p>Test if a configured command value is properly returned.</p>
	 */
	@Test
	public final void testGetCommandValueAsString() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		ATCommandStatus status = ATCommandStatus.OK;
		String command = "NI";
		String valueToSet = "newNIValue";
		RemoteATCommandResponsePacket packet = new RemoteATCommandResponsePacket(frameID, source64Addr, source16Addr, command, status, valueToSet.getBytes());
		
		// Call the method under test.
		String value = packet.getCommandValueAsString();
		
		// Verify the result.
		assertThat("Returned command value must be '" + valueToSet + "'", value, is(equalTo(valueToSet)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket#getCommandValueAsString()}.
	 * 
	 * <p>Test if a configured command value is properly returned.</p>
	 */
	@Test
	public final void testGetCommandValueAsStringNullValue() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		ATCommandStatus status = ATCommandStatus.OK;
		String command = "NI";
		RemoteATCommandResponsePacket packet = new RemoteATCommandResponsePacket(frameID, source64Addr, source16Addr, command, status, null);
		
		// Call the method under test.
		String value = packet.getCommandValueAsString();
		
		// Verify the result.
		assertThat("Returned command value must be 'null'", value, is(equalTo(null)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket#getCommandValue()}.
	 * 
	 * <p>Test if a configured command value is properly returned.</p>
	 */
	@Test
	public final void testGetCommandValue() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		ATCommandStatus status = ATCommandStatus.OK;
		String command = "NI";
		String valueToSet = "newNIValue";
		RemoteATCommandResponsePacket packet = new RemoteATCommandResponsePacket(frameID, source64Addr, source16Addr, command, status, valueToSet.getBytes());
		
		// Call the method under test.
		byte[] value = packet.getCommandValue();
		
		// Verify the result.
		assertThat("Returned command value must be '" + valueToSet + "'", value, is(equalTo(valueToSet.getBytes())));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket#getCommandValue()}.
	 * 
	 * <p>Test if a configured command value is properly returned.</p>
	 */
	@Test
	public final void testGetCommandValueNullValue() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		ATCommandStatus status = ATCommandStatus.OK;
		String command = "NI";
		RemoteATCommandResponsePacket packet = new RemoteATCommandResponsePacket(frameID, source64Addr, source16Addr, command, status, null);
		
		// Call the method under test.
		byte[] value = packet.getCommandValue();
		
		// Verify the result.
		assertThat("Returned command value must be 'null'", value, is(equalTo(null)));
	}
}
