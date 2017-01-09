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

import static org.junit.Assert.assertThat;

import java.util.LinkedHashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

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
		
		byte[] payload = new byte[3 + atCommand.length];
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
		assertThat("Returned status is not the expected one", packet.getStatus(), is(equalTo(ATCommandStatus.OK)));
		assertThat("Returned Command value is not the expected one", packet.getCommandValue(), is(nullValue()));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#createPacket(byte[])}.
	 * 
	 * <p>A valid AT Command Response packet with the provided options without 
	 * command value is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutDataWithUnknownStatus() {
		// Setup the resources for the test.
		int frameType = APIFrameType.AT_COMMAND_RESPONSE.getValue();
		int frameID = 0xE7;
		byte[] atCommand = "NI".getBytes();
		int status = 254;
		
		byte[] payload = new byte[3 + atCommand.length];
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
		assertThat("Returned status is not the expected one", packet.getStatus(), is(equalTo(ATCommandStatus.UNKNOWN)));
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
		assertThat("Returned status is not the expected one", packet.getStatus(), is(equalTo(ATCommandStatus.OK)));
		assertThat("Returned Command value is not the expected one", packet.getCommandValue(), is(equalTo(value)));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#createPacket(byte[])}.
	 * 
	 * <p>A valid AT Command Response packet with the provided options with 
	 * command value is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithDataWithUnknownStatus() {
		// Setup the resources for the test.
		int frameType = APIFrameType.AT_COMMAND_RESPONSE.getValue();
		int frameID = 0xE7;
		byte[] atCommand = "NI".getBytes();
		int status = 254;
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
		assertThat("Returned status is not the expected one", packet.getStatus(), is(equalTo(ATCommandStatus.UNKNOWN)));
		assertThat("Returned Command value is not the expected one", packet.getCommandValue(), is(equalTo(value)));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#ATCommandResponsePacket(int, ATCommandStatus, String, byte[])}.
	 * 
	 * <p>Construct a new AT Command Response packet but with a {@code null} 
	 * command. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateATCommandResponsePacketCommandNull() {
		// Setup the resources for the test.
		int frameID = 0x01;
		ATCommandStatus status = ATCommandStatus.OK;
		String command = null;
		byte[] parameter = "Param value".getBytes();
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("AT command cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new ATCommandResponsePacket(frameID, status, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#ATCommandResponsePacket(int, ATCommandStatus, String, byte[])}.
	 * 
	 * <p>Construct a new AT Command Response packet but with a {@code null} 
	 * status. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateATCommandResponsePacketStatusNull() {
		// Setup the resources for the test.
		int frameID = 0x01;
		ATCommandStatus status = null;
		String command = "NI";
		byte[] parameter = "Param value".getBytes();
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("AT command status cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new ATCommandResponsePacket(frameID, status, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#ATCommandResponsePacket(int, ATCommandStatus, String, byte[])}.
	 * 
	 * <p>Construct a new AT Command Response packet but with a frame ID bigger 
	 * than 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateATCommandResponsePacketFrameIDBiggerThan255() {
		// Setup the resources for the test.
		int frameID = 2000;
		ATCommandStatus status = ATCommandStatus.OK;
		String command = "NI";
		byte[] parameter = "Param value".getBytes();
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ATCommandResponsePacket(frameID, status, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#ATCommandResponsePacket(int, ATCommandStatus, String, byte[])}.
	 * 
	 * <p>Construct a new AT Command Response packet but with a negative frame 
	 * ID. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateATCommandResponsePacketFrameIDNegative() {
		// Setup the resources for the test.
		int frameID = -98;
		ATCommandStatus status = ATCommandStatus.OK;
		String command = "NI";
		byte[] parameter = "Param value".getBytes();
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ATCommandResponsePacket(frameID, status, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#ATCommandResponsePacket(int, ATCommandStatus, String, byte[])}.
	 * 
	 * <p>Construct a new AT Command Response packet but with an valid 
	 * parameters but without parameter value ({@code null}).</p>
	 */
	@Test
	public final void testCreateATCommandResponsePacketValidParameterNull() {
		// Setup the resources for the test.
		int frameID = 0x65;
		ATCommandStatus status = ATCommandStatus.OK;
		String command = "NI";
		byte[] parameter = null;
		
		int expectedLength = 1 /* Frame type */ + 1 /* Frame ID */ + 1 /* status */ + command.length() /* AT command */;
		
		// Call the method under test.
		ATCommandResponsePacket packet = new ATCommandResponsePacket(frameID, status, command, parameter);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(command))));
		assertThat("Returned Command Data is not the expected one", packet.getCommandValue(), is(nullValue(byte[].class)));
		assertThat("AT Command Response needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
		assertThat("Returned status is not the expected one", packet.getStatus(), is(equalTo(ATCommandStatus.OK)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#ATCommandResponsePacket(int, ATCommandStatus, String, byte[])}.
	 * 
	 * <p>Construct a new AT Command Response packet but with an valid 
	 * parameters but without parameter value ({@code null}).</p>
	 */
	@Test
	public final void testCreateATCommandResponsePacketValidParameterNullWithUnknownStatus() {
		// Setup the resources for the test.
		int frameID = 0x65;
		ATCommandStatus status = ATCommandStatus.UNKNOWN;
		String command = "NI";
		byte[] parameter = null;
		
		int expectedLength = 1 /* Frame type */ + 1 /* Frame ID */ + 1 /* status */ + command.length() /* AT command */;
		
		// Call the method under test.
		ATCommandResponsePacket packet = new ATCommandResponsePacket(frameID, status, command, parameter);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(command))));
		assertThat("Returned Command Data is not the expected one", packet.getCommandValue(), is(nullValue(byte[].class)));
		assertThat("AT Command Response needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
		assertThat("Returned status is not the expected one", packet.getStatus(), is(equalTo(ATCommandStatus.UNKNOWN)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#ATCommandResponsePacket(int, ATCommandStatus, String, byte[])}.
	 * 
	 * <p>Construct a new AT Command Response packet but with an valid 
	 * parameters but without parameter value ({@code null}).</p>
	 */
	@Test
	public final void testCreateATCommandResponsePacketValidParameter() {
		// Setup the resources for the test.
		int frameID = 0x65;
		ATCommandStatus status = ATCommandStatus.OK;
		String command = "NI";
		byte[] parameter = "Param value".getBytes();
		
		int expectedLength = 1 /* Frame type */ + 1 /* Frame ID */ + 1 /* status */ + command.length() /* AT command */ + parameter.length /* value */;
		
		// Call the method under test.
		ATCommandResponsePacket packet = new ATCommandResponsePacket(frameID, status, command, parameter);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(command))));
		assertThat("Returned Command Data is not the expected one", packet.getCommandValue(), is(equalTo(parameter)));
		assertThat("AT Command Response needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
		assertThat("Returned status is not the expected one", packet.getStatus(), is(equalTo(ATCommandStatus.OK)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#ATCommandResponsePacket(int, ATCommandStatus, String, byte[])}.
	 * 
	 * <p>Construct a new AT Command Response packet but with an valid 
	 * parameters but without parameter value ({@code null}).</p>
	 */
	@Test
	public final void testCreateATCommandResponsePacketValidParameterWithUnknownStatus() {
		// Setup the resources for the test.
		int frameID = 0x65;
		ATCommandStatus status = ATCommandStatus.UNKNOWN;
		String command = "NI";
		byte[] parameter = "Param value".getBytes();
		
		int expectedLength = 1 /* Frame type */ + 1 /* Frame ID */ + 1 /* status */ + command.length() /* AT command */ + parameter.length /* value */;
		
		// Call the method under test.
		ATCommandResponsePacket packet = new ATCommandResponsePacket(frameID, status, command, parameter);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(command))));
		assertThat("Returned Command Data is not the expected one", packet.getCommandValue(), is(equalTo(parameter)));
		assertThat("AT Command Response needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
		assertThat("Returned status is not the expected one", packet.getStatus(), is(equalTo(ATCommandStatus.UNKNOWN)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#getAPIData()}.
	 * 
	 * <p>Test the get API parameters but with a {@code null} parameter value.</p>
	 */
	@Test
	public final void testGetAPIDataATCommandParameterNull() {
		// Setup the resources for the test.
		int frameID = 0x10;
		ATCommandStatus status = ATCommandStatus.OK;
		String command = "NI";
		byte[] parameter = null;
		ATCommandResponsePacket packet = new ATCommandResponsePacket(frameID, status, command, parameter);
		
		int expectedLength = 1 /* Frame ID */ + 1 /* Status */ + command.length() /* AT command */;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		System.arraycopy(command.getBytes(), 0, expectedData, 1, command.length());
		expectedData[1 + command.length()] = (byte)status.getId();
		
		// Call the method under test.
		byte[] data = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#getAPIData()}.
	 * 
	 * <p>Test the get API parameters but with a {@code null} parameter value.</p>
	 */
	@Test
	public final void testGetAPIDataATCommandParameterNullWithUnknownStatus() {
		// Setup the resources for the test.
		int frameType = APIFrameType.AT_COMMAND_RESPONSE.getValue();
		int frameID = 0x10;
		byte[] atCommand = "NI".getBytes();
		int status = 254;
		
		byte[] payload = new byte[3 + atCommand.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(atCommand, 0, payload, 2, atCommand.length);
		payload[2 + atCommand.length] = (byte)status;
		
		ATCommandResponsePacket packet = ATCommandResponsePacket.createPacket(payload);
		
		byte[] expectedData = new byte[payload.length - 1]; /* Do not include the type */
		System.arraycopy(payload, 1, expectedData, 0, expectedData.length);
		
		// Call the method under test.
		byte[] data = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#getAPIData()}.
	 * 
	 * <p>Test the get API parameters but with a non-{@code null} parameter value.</p>
	 */
	@Test
	public final void testGetAPIDataATCommandParameterNotNull() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "NI";
		ATCommandStatus status = ATCommandStatus.OK;
		byte[] parameter = new byte[]{0x6D, 0x79, 0x44, 0x65, 0x76, 0x69, 0x63, 0x65};
		ATCommandResponsePacket packet = new ATCommandResponsePacket(frameID, status, command, parameter);
		
		int expectedLength = 1 /* Frame ID */ + 1 /* Status */ + command.length() /* AT command */ + parameter.length /* Value */;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		System.arraycopy(command.getBytes(), 0, expectedData, 1, command.length());
		expectedData[1 + command.length()] = (byte)status.getId();
		System.arraycopy(parameter, 0, expectedData, 2 + command.length(), parameter.length);
		
		// Call the method under test.
		byte[] data = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#getAPIData()}.
	 * 
	 * <p>Test the get API parameters but with a non-{@code null} parameter value.</p>
	 */
	@Test
	public final void testGetAPIDataATCommandParameterNotNullWitUnknownStatus() {
		// Setup the resources for the test.
		int frameType = APIFrameType.AT_COMMAND_RESPONSE.getValue();
		int frameID = 0x10;
		byte[] atCommand = "NI".getBytes();
		int status = 254;
		byte[] parameter = new byte[]{0x6D, 0x79, 0x44, 0x65, 0x76, 0x69, 0x63, 0x65};
		
		byte[] payload = new byte[3 + atCommand.length + parameter.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(atCommand, 0, payload, 2, atCommand.length);
		payload[2 + atCommand.length] = (byte)status;
		System.arraycopy(parameter, 0, payload, 3 + atCommand.length, parameter.length);
		
		ATCommandResponsePacket packet = ATCommandResponsePacket.createPacket(payload);
		
		byte[] expectedData = new byte[payload.length - 1]; /* Do not include the type */
		System.arraycopy(payload, 1, expectedData, 0, expectedData.length);
		
		// Call the method under test.
		byte[] data = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with a {@code null} parameter value.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersATCommandParameterNull() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "NI";
		ATCommandStatus status = ATCommandStatus.UNKNOWN;
		byte[] parameter = null;
		ATCommandResponsePacket packet = new ATCommandResponsePacket(frameID, status, command, parameter);
		
		String expectedATCommand = HexUtils.prettyHexString(command.getBytes()) + " (" + command + ")";
		String expectedStatus = Integer.toHexString(status.getId()).toUpperCase() + " (" + status.getDescription() + ")";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(2)));
		assertThat("Status is not the expected one", packetParams.get("Status"), is(equalTo(expectedStatus)));
		assertThat("AT Command is not the expected one", packetParams.get("AT Command"), is(equalTo(expectedATCommand)));
		assertThat("AT Response is not the expected one", packetParams.get("Response"), is(nullValue(String.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with a {@code null} parameter value.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersATCommandParameterNullWithUnknownStatus() {
		// Setup the resources for the test.
		int frameType = APIFrameType.AT_COMMAND_RESPONSE.getValue();
		int frameID = 0x10;
		String cmdString = "NI";
		byte[] atCommand = cmdString.getBytes();
		int status = 254;
		
		byte[] payload = new byte[3 + atCommand.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(atCommand, 0, payload, 2, atCommand.length);
		payload[2 + atCommand.length] = (byte)status;
		
		ATCommandResponsePacket packet = ATCommandResponsePacket.createPacket(payload);
		
		String expectedATCommand = HexUtils.prettyHexString(atCommand) + " (" + cmdString + ")";
		String expectedStatus = Integer.toHexString(status).toUpperCase() + " (" + ATCommandStatus.UNKNOWN.getDescription() + ")";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(2)));
		assertThat("Status is not the expected one", packetParams.get("Status"), is(equalTo(expectedStatus)));
		assertThat("AT Command is not the expected one", packetParams.get("AT Command"), is(equalTo(expectedATCommand)));
		assertThat("AT Response is not the expected one", packetParams.get("Response"), is(nullValue(String.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with a non-{@code null} parameter value.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersATCommandParameterNotNull() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "NI";
		ATCommandStatus status = ATCommandStatus.UNKNOWN;
		byte[] parameter = new byte[]{0x6D, 0x79, 0x44, 0x65, 0x76, 0x69, 0x63, 0x65};
		ATCommandResponsePacket packet = new ATCommandResponsePacket(frameID, status, command, parameter);
		
		String expectedATCommand = HexUtils.prettyHexString(command.getBytes()) + " (" + command + ")";
		String expectedStatus = Integer.toHexString(status.getId()).toUpperCase() + " (" + status.getDescription() + ")";
		String expectedATParameter = HexUtils.prettyHexString(parameter) + " (" + new String(parameter) + ")";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(3)));
		assertThat("Status is not the expected one", packetParams.get("Status"), is(equalTo(expectedStatus)));
		assertThat("AT Command is not the expected one", packetParams.get("AT Command"), is(equalTo(expectedATCommand)));
		assertThat("AT Response is not the expected one", packetParams.get("Response"), is(equalTo(expectedATParameter)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with a non-{@code null} parameter value.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersATCommandParameterNotNullNonStringCmd() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "DL";
		ATCommandStatus status = ATCommandStatus.UNKNOWN;
		byte[] parameter = new byte[]{0x6D, 0x79, 0x44, 0x65, 0x76, 0x69, 0x63, 0x65};
		ATCommandResponsePacket packet = new ATCommandResponsePacket(frameID, status, command, parameter);
		
		String expectedATCommand = HexUtils.prettyHexString(command.getBytes()) + " (" + command + ")";
		String expectedStatus = Integer.toHexString(status.getId()).toUpperCase() + " (" + status.getDescription() + ")";
		String expectedATParameter = HexUtils.prettyHexString(parameter);
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(3)));
		assertThat("Status is not the expected one", packetParams.get("Status"), is(equalTo(expectedStatus)));
		assertThat("AT Command is not the expected one", packetParams.get("AT Command"), is(equalTo(expectedATCommand)));
		assertThat("AT Response is not the expected one", packetParams.get("Response"), is(equalTo(expectedATParameter)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#isBroadcast()}.
	 * 
	 * <p>Test if a AT Command Response packet is a broadcast packet, never 
	 * should be.</p>
	 */
	@Test
	public final void testIsBroadcast() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "DL";
		ATCommandStatus status = ATCommandStatus.UNKNOWN;
		byte[] parameter = new byte[]{0x6D, 0x79, 0x44, 0x65, 0x76, 0x69, 0x63, 0x65};
		ATCommandResponsePacket packet = new ATCommandResponsePacket(frameID, status, command, parameter);
		
		// Call the method under test and verify the result.
		assertThat("Packet should not be broadcast", packet.isBroadcast(), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#setCommandValue(String)}.
	 * 
	 * <p>Test if a string command value is properly configured.</p>
	 */
	@Test
	public final void testSetCommandValueString() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "NI";
		ATCommandStatus status = ATCommandStatus.UNKNOWN;
		String valueToSet = "newNIValue";
		ATCommandResponsePacket packet = new ATCommandResponsePacket(frameID, status, command, new byte[0]);
		
		// Call the method under test.
		packet.setCommandValue(valueToSet);
		
		// Verify the result.
		assertThat("Configured command value must be '" + valueToSet + "'", 
				packet.getCommandValueAsString(), is(equalTo(valueToSet)));
		assertThat("Configured command value must be '" + valueToSet + "'", 
				packet.getCommandValue(), is(equalTo(valueToSet.getBytes())));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#setCommandValue(String)}.
	 * 
	 * <p>Test if a string command value with {@code null} value is properly 
	 * configured.</p>
	 */
	@Test
	public final void tesSetCommandValueStringNull() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "NI";
		ATCommandStatus status = ATCommandStatus.UNKNOWN;
		ATCommandResponsePacket packet = new ATCommandResponsePacket(frameID, status, command, new byte[0]);
		
		// Call the method under test.
		packet.setCommandValue((String)null);
		
		// Verify the result.
		assertThat("Configured command value must be 'null'", 
				packet.getCommandValueAsString(), is(equalTo(null)));
		assertThat("Configured command value must be 'null'", 
				packet.getCommandValue(), is(equalTo(null)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#setCommandValue(byte[])}.
	 * 
	 * <p>Test if a byte array command value is properly configured.</p>
	 */
	@Test
	public final void testSetCommandValueByteArray() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "NI";
		ATCommandStatus status = ATCommandStatus.UNKNOWN;
		byte[] valueToSet = new byte[]{0x6D, 0x79, 0x44, 0x65, 0x76, 0x69, 0x63, 0x65};
		ATCommandResponsePacket packet = new ATCommandResponsePacket(frameID, status, command, new byte[0]);
		
		// Call the method under test.
		packet.setCommandValue(valueToSet);
		
		// Verify the result.
		assertThat("Configured command value must be '" + new String(valueToSet) + "'", 
				packet.getCommandValue(), is(equalTo(valueToSet)));
		assertThat("Configured command value must be '" + valueToSet + "'", 
				packet.getCommandValueAsString(), is(equalTo(new String(valueToSet))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#setCommandValue(byte[])}.
	 * 
	 * <p>Test if a byte array command value with {@code null} value is properly 
	 * configured.</p>
	 */
	@Test
	public final void testSetCommandValueByteArrayNull() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "NI";
		ATCommandStatus status = ATCommandStatus.UNKNOWN;
		ATCommandResponsePacket packet = new ATCommandResponsePacket(frameID, status, command, new byte[0]);
		
		// Call the method under test.
		packet.setCommandValue((byte[])null);
		
		// Verify the result.
		assertThat("Configured command value must be 'null'", packet.getCommandValue(), is(equalTo(null)));
		assertThat("Configured command value must be 'null'", packet.getCommandValueAsString(), is(equalTo(null)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#getCommandValueAsString()}.
	 * 
	 * <p>Test if a configured command value is properly returned.</p>
	 */
	@Test
	public final void testGetCommandValueAsString() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "NI";
		ATCommandStatus status = ATCommandStatus.UNKNOWN;
		String valueToSet = "newNIValue";
		ATCommandResponsePacket packet = new ATCommandResponsePacket(frameID, status, command, valueToSet.getBytes());
		
		// Call the method under test.
		String value = packet.getCommandValueAsString();
		
		// Verify the result.
		assertThat("Returned command value must be '" + valueToSet + "'", value, is(equalTo(valueToSet)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#getCommandValueAsString()}.
	 * 
	 * <p>Test if a configured command value is properly returned.</p>
	 */
	@Test
	public final void testGetCommandValueAsStringNullValue() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "NI";
		ATCommandStatus status = ATCommandStatus.UNKNOWN;
		ATCommandResponsePacket packet = new ATCommandResponsePacket(frameID, status, command, null);
		
		// Call the method under test.
		String value = packet.getCommandValueAsString();
		
		// Verify the result.
		assertThat("Returned command value must be 'null'", value, is(equalTo(null)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#getCommandValue()}.
	 * 
	 * <p>Test if a configured command value is properly returned.</p>
	 */
	@Test
	public final void testGetCommandValue() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "NI";
		ATCommandStatus status = ATCommandStatus.UNKNOWN;
		String valueToSet = "newNIValue";
		ATCommandResponsePacket packet = new ATCommandResponsePacket(frameID, status, command, valueToSet.getBytes());
		
		// Call the method under test.
		byte[] value = packet.getCommandValue();
		
		// Verify the result.
		assertThat("Returned command value must be '" + valueToSet + "'", value, is(equalTo(valueToSet.getBytes())));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandResponsePacket#getCommandValue()}.
	 * 
	 * <p>Test if a configured command value is properly returned.</p>
	 */
	@Test
	public final void testGetCommandValueNullValue() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "NI";
		ATCommandStatus status = ATCommandStatus.UNKNOWN;
		ATCommandResponsePacket packet = new ATCommandResponsePacket(frameID, status, command, null);
		
		// Call the method under test.
		byte[] value = packet.getCommandValue();
		
		// Verify the result.
		assertThat("Returned command value must be 'null'", value, is(equalTo(null)));
	}
}
