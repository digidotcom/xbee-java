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

import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

public class ATCommandQueuePacketTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	public ATCommandQueuePacketTest() {
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
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#createPacket(byte[])}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a 
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Setup the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("AT Command Queue packet payload cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		ATCommandQueuePacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Setup the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete AT Command Queue packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		ATCommandQueuePacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Setup the resources for the test.
		int frameType = APIFrameType.AT_COMMAND_QUEUE.getValue();
		//int frameID = 0xE7;
		byte[] atCommand = "NI".getBytes();
		
		byte[] payload = new byte[1 + atCommand.length];
		payload[0] = (byte)frameType;
		//payload[1] = (byte)frameID;
		System.arraycopy(atCommand, 0, payload, 1, atCommand.length);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete AT Command Queue packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		ATCommandQueuePacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#createPacket(byte[])}.
	 * 
	 * <p>If the AT command is longer than 2 characters, then the AT command is 
	 * considered to be only the 2 first chars, and the rest part of the command
	 * data.</p>
	 */
	@Test
	public final void testCreatePacketATCommandLonger() {
		// Setup the resources for the test.
		int frameType = APIFrameType.AT_COMMAND_QUEUE.getValue();
		int frameID = 0xE7;
		byte[] atCommand = "NIA".getBytes();
		
		byte[] payload = new byte[2 + atCommand.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(atCommand, 0, payload, 2, atCommand.length);
		
		
		// Call the method under test.
		ATCommandQueuePacket packet = ATCommandQueuePacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String("NI"))));
		assertThat("Returned Command Data is not the expected one", packet.getParameterAsString(), is(equalTo("A")));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#createPacket(byte[])}.
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
		exception.expectMessage(is(equalTo("Payload is not an AT Command Queue packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		ATCommandQueuePacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#createPacket(byte[])}.
	 * 
	 * <p>A valid AT Command packet with the provided options without command 
	 * data is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutData() {
		// Setup the resources for the test.
		int frameType = APIFrameType.AT_COMMAND_QUEUE.getValue();
		int frameID = 0xE7;
		byte[] atCommand = "NI".getBytes();
		
		byte[] payload = new byte[2 + atCommand.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(atCommand, 0, payload, 2, atCommand.length);
		
		// Call the method under test.
		ATCommandQueuePacket packet = ATCommandQueuePacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(atCommand))));
		assertThat("Returned Command Data is not the expected one", packet.getParameter(), is(nullValue()));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#createPacket(byte[])}.
	 * 
	 * <p>A valid AT Command packet with the provided options with command data 
	 * is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithData() {
		// Setup the resources for the test.
		int frameType = APIFrameType.AT_COMMAND_QUEUE.getValue();
		int frameID = 0xE7;
		byte[] atCommand = "NI".getBytes();
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		byte[] payload = new byte[2 + atCommand.length + data.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(atCommand, 0, payload, 2, atCommand.length);
		System.arraycopy(data, 0, payload, 2 + atCommand.length, data.length);
		
		// Call the method under test.
		ATCommandQueuePacket packet = ATCommandQueuePacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(atCommand))));
		assertThat("Returned Command Data is not the expected one", packet.getParameter(), is(equalTo(data)));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#ATCommandQueuePacket(int, String, String)}.
	 * 
	 * <p>Construct a new AT Command packet but with a {@code null} command. 
	 * This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateATCommandQueuePacketCommandNullParameterString() {
		// Setup the resources for the test.
		int frameID = 0x01;
		String command = null;
		String parameter = "Param value";
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("AT command cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new ATCommandQueuePacket(frameID, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#ATCommandQueuePacket(int, String, byte[])}.
	 * 
	 * <p>Construct a new AT Command packet but with a {@code null} command. 
	 * This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateATCommandQueuePacketCommandNullParameterByteArray() {
		// Setup the resources for the test.
		int frameID = 0x01;
		String command = null;
		byte[] parameter = new byte[]{0x50, 0x61, 0x72, 0x61, 0x6D, 0x20, 0x76, 0x61, 0x6C, 0x75, 0x65};
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("AT command cannot be null.")));
		
		// Call the method under test that should throw n NullPointerException.
		new ATCommandQueuePacket(frameID, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#ATCommandQueuePacket(int, String, String)}.
	 * 
	 * <p>Construct a new AT Command packet but with an invalid frame ID, 
	 * bigger than 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateATCommandQueuePacketInvalidFrameIDBiggerThan255ParameterString() {
		// Setup the resources for the test.
		int frameID = 2000;
		String command = "NI";
		String parameter = "Param value";
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ATCommandQueuePacket(frameID, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#ATCommandQueuePacket(int, String, byte[])}.
	 * 
	 * <p>Construct a new AT Command packet but with an invalid frame ID, 
	 * bigger than 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateATCommandQueuePacketInvalidFrameIDBiggerThan255ParameterByteArray() {
		// Setup the resources for the test.
		int frameID = 2000;
		String command = "NI";
		byte[] parameter = new byte[]{0x50, 0x61, 0x72, 0x61, 0x6D, 0x20, 0x76, 0x61, 0x6C, 0x75, 0x65};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ATCommandQueuePacket(frameID, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#ATCommandQueuePacket(int, String, String)}.
	 * 
	 * <p>Construct a new AT Command packet but with an invalid frame ID with 
	 * negative value. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateATCommandQueuePacketInvalidFrameIDNegativeParameterString() {
		// Setup the resources for the test.
		int frameID = -4;
		String command = "NI";
		String parameter = "Param value";
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ATCommandQueuePacket(frameID, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#ATCommandQueuePacket(int, String, byte[])}.
	 * 
	 * <p>Construct a new AT Command packet but with an invalid frame ID with 
	 * negative value. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateATCommandQueuePacketInvalidFrameIDNegativeParameterByteArray() {
		// Setup the resources for the test.
		int frameID = -4;
		String command = "NI";
		byte[] parameter = new byte[]{0x50, 0x61, 0x72, 0x61, 0x6D, 0x20, 0x76, 0x61, 0x6C, 0x75, 0x65};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ATCommandQueuePacket(frameID, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#ATCommandQueuePacket(int, String, String)}.
	 * 
	 * <p>Construct a new AT Command packet but with an valid parameters but
	 * without parameter value ({@code null}).</p>
	 */
	@Test
	public final void testCreateATCommandQueuePacketValidParameterNull() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "NI";
		String parameter = null;
		
		int expectedLength = 1 /* Frame type */ + 1 /* Frame ID */ + command.length() /* AT command */;
		
		// Call the method under test.
		ATCommandQueuePacket packet = new ATCommandQueuePacket(frameID, command, parameter);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(command))));
		assertThat("Returned Command Data is not the expected one", packet.getParameter(), is(nullValue(byte[].class)));
		assertThat("AT Command needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#ATCommandQueuePacket(int, String, String)}.
	 * 
	 * <p>Construct a new AT Command packet but with an valid parameters but
	 * with a parameter value.</p>
	 */
	@Test
	public final void testCreateATCommandQueuePacketValidParameterString() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "NI";
		String parameter = "MyDevice";
		
		int expectedLength = 1 /* Frame type */ + 1 /* Frame ID */ + command.length() /* AT command */ + parameter.length() /* Value */;
		
		// Call the method under test.
		ATCommandQueuePacket packet = new ATCommandQueuePacket(frameID, command, parameter);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(command))));
		assertThat("Returned Command Data is not the expected one", packet.getParameter(), is(equalTo(parameter.getBytes())));
		assertThat("AT Command needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#ATCommandQueuePacket(int, String, byte[])}.
	 * 
	 * <p>Construct a new AT Command packet but with an valid parameters but
	 * with a parameter value.</p>
	 */
	@Test
	public final void testCreateATCommandQueuePacketValidParameterByteArray() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "NI";
		byte[] parameter = new byte[]{0x6D, 0x79, 0x44, 0x65, 0x76, 0x69, 0x63, 0x65};
		
		int expectedLength = 1 /* Frame type */ + 1 /* Frame ID */ + command.length() /* AT command */ + parameter.length /* Value */;
		
		// Call the method under test.
		ATCommandQueuePacket packet = new ATCommandQueuePacket(frameID, command, parameter);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(command))));
		assertThat("Returned Command Data is not the expected one", packet.getParameter(), is(equalTo(parameter)));
		assertThat("AT Command needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#getAPIData()}.
	 * 
	 * <p>Test the get API parameters but with a {@code null} parameter value.</p>
	 */
	@Test
	public final void testGetAPIDataATCommandParameterNull() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "NI";
		String parameter = null;
		ATCommandQueuePacket packet = new ATCommandQueuePacket(frameID, command, parameter);
		
		int expectedLength = 1 /* Frame ID */ + command.length() /* AT command */;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		System.arraycopy(command.getBytes(), 0, expectedData, 1, command.length());
		
		// Call the method under test.
		byte[] data = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#getAPIData()}.
	 * 
	 * <p>Test the get API parameters but with a non-{@code null} parameter value.</p>
	 */
	@Test
	public final void testGetAPIDataATCommandParameterNotNull() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "NI";
		byte[] parameter = new byte[]{0x6D, 0x79, 0x44, 0x65, 0x76, 0x69, 0x63, 0x65};
		ATCommandQueuePacket packet = new ATCommandQueuePacket(frameID, command, parameter);
		
		int expectedLength = 1 /* Frame ID */ + command.length() /* AT command */ + parameter.length /* Value */;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		System.arraycopy(command.getBytes(), 0, expectedData, 1, command.length());
		System.arraycopy(parameter, 0, expectedData, 1 + command.length(), parameter.length);
		
		// Call the method under test.
		byte[] data = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with a {@code null} parameter value.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersATCommandParameterNull() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "NI";
		String parameter = null;
		ATCommandQueuePacket packet = new ATCommandQueuePacket(frameID, command, parameter);
		
		String expectedATCommand = HexUtils.prettyHexString(command.getBytes()) + " (" + command + ")";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(1)));
		assertThat("AT Command is not the expected one", packetParams.get("AT Command"), is(equalTo(expectedATCommand)));
		assertThat("AT Parameter is not the expected one", packetParams.get("Parameter"), is(nullValue(String.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with a parameter value.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersATCommandParameterString() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "NI";
		String parameter = "myDevice";
		ATCommandQueuePacket packet = new ATCommandQueuePacket(frameID, command, parameter);
		
		String expectedATCommand = HexUtils.prettyHexString(command.getBytes()) + " (" + command + ")";
		String expectedATParameter = HexUtils.prettyHexString(parameter.getBytes()) + " (" + parameter + ")";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(2)));
		assertThat("AT Command is not the expected one", packetParams.get("AT Command"), is(equalTo(expectedATCommand)));
		assertThat("AT Parameter is not the expected one", packetParams.get("Parameter"), is(equalTo(expectedATParameter)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with a parameter value.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersATCommandParameterByteArray() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "NI";
		byte[] parameter = new byte[]{0x6D, 0x79, 0x44, 0x65, 0x76, 0x69, 0x63, 0x65};
		ATCommandQueuePacket packet = new ATCommandQueuePacket(frameID, command, parameter);
		
		String expectedATCommand = HexUtils.prettyHexString(command.getBytes()) + " (" + command + ")";
		String expectedATParameter = HexUtils.prettyHexString(parameter) + " (" + new String(parameter) + ")";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(2)));
		assertThat("AT Command is not the expected one", packetParams.get("AT Command"), is(equalTo(expectedATCommand)));
		assertThat("AT Parameter is not the expected one", packetParams.get("Parameter"), is(equalTo(expectedATParameter)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with a parameter value.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersATCommandParameterByteArrayNonStringCmd() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "DL";
		byte[] parameter = new byte[]{0x6D, 0x79};
		ATCommandQueuePacket packet = new ATCommandQueuePacket(frameID, command, parameter);
		
		String expectedATCommand = HexUtils.prettyHexString(command.getBytes()) + " (" + command + ")";
		String expectedATParameter = HexUtils.prettyHexString(parameter);
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(2)));
		assertThat("AT Command is not the expected one", packetParams.get("AT Command"), is(equalTo(expectedATCommand)));
		assertThat("AT Parameter is not the expected one", packetParams.get("Parameter"), is(equalTo(expectedATParameter)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#isBroadcast()}.
	 * 
	 * <p>Test if a AT Command Queue packet is a broadcast packet, never should 
	 * be.</p>
	 */
	@Test
	public final void testIsBroadcast() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "DL";
		byte[] parameter = new byte[]{0x6D, 0x79};
		ATCommandQueuePacket packet = new ATCommandQueuePacket(frameID, command, parameter);
		
		// Call the method under test and verify the result.
		assertThat("Packet should not be broadcast", packet.isBroadcast(), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#setParameter(String)}.
	 * 
	 * <p>Test if a string parameter is properly configured.</p>
	 */
	@Test
	public final void testSetParameterString() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "NI";
		String parameterToSet = "newNIValue";
		ATCommandQueuePacket packet = new ATCommandQueuePacket(frameID, command, "");
		
		// Call the method under test.
		packet.setParameter(parameterToSet);
		
		// Verify the result.
		assertThat("Configured parameter must be '" + parameterToSet + "'", 
				packet.getParameterAsString(), is(equalTo(parameterToSet)));
		assertThat("Configured parameter must be '" + parameterToSet + "'", 
				packet.getParameter(), is(equalTo(parameterToSet.getBytes())));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#setParameter(String)}.
	 * 
	 * <p>Test if a string parameter with {@code null} value is properly 
	 * configured.</p>
	 */
	@Test
	public final void testSetParameterStringNull() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "NI";
		ATCommandQueuePacket packet = new ATCommandQueuePacket(frameID, command, "");
		
		// Call the method under test.
		packet.setParameter((String)null);
		
		// Verify the result.
		assertThat("Configured parameter must be 'null'", 
				packet.getParameterAsString(), is(equalTo(null)));
		assertThat("Configured parameter must be 'null'", 
				packet.getParameter(), is(equalTo(null)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#setParameter(byte[])}.
	 * 
	 * <p>Test if a byte array parameter is properly configured.</p>
	 */
	@Test
	public final void testSetParameterByteArray() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "NI";
		byte[] parameterToSet = new byte[]{0x6D, 0x79, 0x44, 0x65, 0x76, 0x69, 0x63, 0x65};
		ATCommandQueuePacket packet = new ATCommandQueuePacket(frameID, command, new byte[0]);
		
		// Call the method under test.
		packet.setParameter(parameterToSet);
		
		// Verify the result.
		assertThat("Configured parameter must be '" + new String(parameterToSet) + "'", 
				packet.getParameter(), is(equalTo(parameterToSet)));
		assertThat("Configured parameter must be '" + parameterToSet + "'", 
				packet.getParameterAsString(), is(equalTo(new String(parameterToSet))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#setParameter(byte[])}.
	 * 
	 * <p>Test if a byte array parameter with {@code null} value is properly 
	 * configured.</p>
	 */
	@Test
	public final void testSetParameterByteArrayNull() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "NI";
		ATCommandQueuePacket packet = new ATCommandQueuePacket(frameID, command, new byte[0]);
		
		// Call the method under test.
		packet.setParameter((byte[])null);
		
		// Verify the result.
		assertThat("Configured parameter must be 'null'", packet.getParameter(), is(equalTo(null)));
		assertThat("Configured parameter must be 'null'", packet.getParameterAsString(), is(equalTo(null)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#getParameterAsString()}.
	 * 
	 * <p>Test if a configured parameter is properly returned.</p>
	 */
	@Test
	public final void testGetParameterAsString() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "NI";
		String parameterToSet = "newNIValue";
		ATCommandQueuePacket packet = new ATCommandQueuePacket(frameID, command, parameterToSet);
		
		// Call the method under test.
		String value = packet.getParameterAsString();
		
		// Verify the result.
		assertThat("Returned parameter must be '" + parameterToSet + "'", value, is(equalTo(parameterToSet)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#getParameterAsString()}.
	 * 
	 * <p>Test if a configured parameter is properly returned.</p>
	 */
	@Test
	public final void testGetParameterAsStringNullValue() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "NI";
		ATCommandQueuePacket packet = new ATCommandQueuePacket(frameID, command, (String)null);
		
		// Call the method under test.
		String value = packet.getParameterAsString();
		
		// Verify the result.
		assertThat("Returned parameter must be 'null'", value, is(equalTo(null)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#getParameter()}.
	 * 
	 * <p>Test if a configured parameter is properly returned.</p>
	 */
	@Test
	public final void testGetParameter() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "NI";
		String parameterToSet = "newNIValue";
		ATCommandQueuePacket packet = new ATCommandQueuePacket(frameID, command, parameterToSet);
		
		// Call the method under test.
		byte[] value = packet.getParameter();
		
		// Verify the result.
		assertThat("Returned parameter must be '" + parameterToSet + "'", value, is(equalTo(parameterToSet.getBytes())));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ATCommandQueuePacket#getParameter()}.
	 * 
	 * <p>Test if a configured parameter is properly returned.</p>
	 */
	@Test
	public final void testGetParameterNullValue() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "NI";
		ATCommandQueuePacket packet = new ATCommandQueuePacket(frameID, command, (byte[])null);
		
		// Call the method under test.
		byte[] value = packet.getParameter();
		
		// Verify the result.
		assertThat("Returned parameter must be 'null'", value, is(equalTo(null)));
	}
}
