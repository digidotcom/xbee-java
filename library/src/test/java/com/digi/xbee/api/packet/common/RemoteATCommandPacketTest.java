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

import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

public class RemoteATCommandPacketTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	public RemoteATCommandPacketTest() {
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
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#createPacket(byte[])}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a 
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Setup the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Remote AT Command packet payload cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		RemoteATCommandPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Setup the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Remote AT Command packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		RemoteATCommandPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Setup the resources for the test.
		int frameType = APIFrameType.REMOTE_AT_COMMAND_REQUEST.getValue();
		int frameID = 0xE7;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int options = 0x06;
		String cmd = "A"; // Invalid AT command.
		
		byte[] payload = new byte[14];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(dest64Addr.getValue(), 0, payload, 2, dest64Addr.getValue().length);
		System.arraycopy(dest16Addr.getValue(), 0, payload, 10, dest16Addr.getValue().length);
		payload[12] = (byte)options;
		System.arraycopy(cmd.getBytes(), 0, payload, 13, cmd.getBytes().length);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Remote AT Command packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		RemoteATCommandPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#createPacket(byte[])}.
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
		int options = 0x06;
		String cmd = "BD";
		byte[] data = new byte[]{0x00, 0x6F, 0x6C, 0x61};
		
		byte[] payload = new byte[14 + data.length];
		payload[0] = (byte)frameID;
		System.arraycopy(dest64Addr.getValue(), 0, payload, 1, dest64Addr.getValue().length);
		System.arraycopy(dest16Addr.getValue(), 0, payload, 9, dest16Addr.getValue().length);
		payload[11] = (byte)options;
		System.arraycopy(cmd.getBytes(), 0, payload, 12, cmd.getBytes().length);
		System.arraycopy(data, 0, payload, 14, data.length);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not a Remote AT Command packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		RemoteATCommandPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#createPacket(byte[])}.
	 * 
	 * <p>A valid Remote AT Command packet with the provided options without 
	 * data is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutData() {
		// Setup the resources for the test.
		int frameType = APIFrameType.REMOTE_AT_COMMAND_REQUEST.getValue();
		int frameID = 0xE7;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int options = 0x06;
		String cmd = "BD";
		
		byte[] payload = new byte[15];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(dest64Addr.getValue(), 0, payload, 2, dest64Addr.getValue().length);
		System.arraycopy(dest16Addr.getValue(), 0, payload, 10, dest16Addr.getValue().length);
		payload[12] = (byte)options;
		System.arraycopy(cmd.getBytes(), 0, payload, 13, cmd.getBytes().length);
		
		// Call the method under test.
		RemoteATCommandPacket packet = RemoteATCommandPacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination 64-bit address is not the expected one", packet.get64bitDestinationAddress(), is(equalTo(dest64Addr)));
		assertThat("Returned destination 16-bit address is not the expected one", packet.get16bitDestinationAddress(), is(equalTo(dest16Addr)));
		assertThat("Returned transmit options is not the expected one", packet.getTransmitOptions(), is(equalTo(options)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(cmd)));
		assertThat("Returned Parameter Data is not the expected one", packet.getParameter(), is(nullValue()));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#createPacket(byte[])}.
	 * 
	 * <p>A valid Remote AT Command packet with the provided options and data is 
	 * created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithData() {
		// Setup the resources for the test.
		int frameType = APIFrameType.REMOTE_AT_COMMAND_REQUEST.getValue();
		int frameID = 0xE7;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int options = 0x06;
		String cmd = "BD";
		byte[] data = new byte[]{0x00, 0x6F, 0x6C, 0x61};
		
		byte[] payload = new byte[15 + data.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(dest64Addr.getValue(), 0, payload, 2, dest64Addr.getValue().length);
		System.arraycopy(dest16Addr.getValue(), 0, payload, 10, dest16Addr.getValue().length);
		payload[12] = (byte)options;
		System.arraycopy(cmd.getBytes(), 0, payload, 13, cmd.getBytes().length);
		System.arraycopy(data, 0, payload, 15, data.length);
		
		// Call the method under test.
		RemoteATCommandPacket packet = RemoteATCommandPacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination 64-bit address is not the expected one", packet.get64bitDestinationAddress(), is(equalTo(dest64Addr)));
		assertThat("Returned destination 16-bit address is not the expected one", packet.get16bitDestinationAddress(), is(equalTo(dest16Addr)));
		assertThat("Returned transmit options is not the expected one", packet.getTransmitOptions(), is(equalTo(options)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(cmd)));
		assertThat("Returned Parameter Data is not the expected one", packet.getParameter(), is(equalTo(data)));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#RemoteATCommandPacket(int, XBee64BitAddress, XBee16BitAddress, int, String, String)}.
	 * 
	 * <p>Construct a new Remote AT Command packet but with a {@code null} 
	 * 64-bit destination address. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandPacket64BitAddressNullParameterString() {
		// Setup the resources for the test.
		int frameID = 0x01;
		XBee64BitAddress dest64Addr = null;
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int options = 23;
		String command = "NI";
		String parameter = "Param value";
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("64-bit destination address cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#RemoteATCommandPacket(int, XBee64BitAddress, XBee16BitAddress, int, String, byte[])}.
	 * 
	 * <p>Construct a new Remote AT Command packet but with a {@code null} 
	 * 64-bit destination address. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandPacket64BitAddressNullParameterByteArray() {
		// Setup the resources for the test.
		int frameID = 0x01;
		XBee64BitAddress dest64Addr = null;
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int options = 23;
		String command = "NI";
		byte[] parameter = new byte[]{0x50, 0x61, 0x72, 0x61, 0x6D, 0x20, 0x76, 0x61, 0x6C, 0x75, 0x65};
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("64-bit destination address cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#RemoteATCommandPacket(int, XBee64BitAddress, XBee16BitAddress, int, String, String)}.
	 * 
	 * <p>Construct a new Remote AT Command packet but with a {@code null} 
	 * 16-bit destination address. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandPacket16BitAddressNullParameterString() {
		// Setup the resources for the test.
		int frameID = 0x01;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = null;
		int options = 23;
		String command = "NI";
		String parameter = "Param value";
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("16-bit destination address cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#RemoteATCommandPacket(int, XBee64BitAddress, XBee16BitAddress, int, String, byte[])}.
	 * 
	 * <p>Construct a new Remote AT Command packet but with a {@code null} 
	 * 16-bit destination address. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandPacket16BitAddressNullParameterByteArray() {
		// Setup the resources for the test.
		int frameID = 0x01;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = null;
		int options = 23;
		String command = "NI";
		byte[] parameter = new byte[]{0x50, 0x61, 0x72, 0x61, 0x6D, 0x20, 0x76, 0x61, 0x6C, 0x75, 0x65};
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("16-bit destination address cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#RemoteATCommandPacket(int, XBee64BitAddress, XBee16BitAddress, int, String, String)}.
	 * 
	 * <p>Construct a new Remote AT Command packet but with a receive options 
	 * bigger than 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandPacketReceiveOptionsBiggerThan255ParameterString() {
		// Setup the resources for the test.
		int frameID = 0x01;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int options = 621;
		String command = "NI";
		String parameter = "Param value";
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Options value must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#RemoteATCommandPacket(int, XBee64BitAddress, XBee16BitAddress, int, String, byte[])}.
	 * 
	 * <p>Construct a new Remote AT Command packet but with a receive options 
	 * bigger than 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandPacketReceiveOptionsBiggerThan255ParameterByteArray() {
		// Setup the resources for the test.
		int frameID = 0x01;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int options = 621;
		String command = "NI";
		byte[] parameter = new byte[]{0x50, 0x61, 0x72, 0x61, 0x6D, 0x20, 0x76, 0x61, 0x6C, 0x75, 0x65};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Options value must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#RemoteATCommandPacket(int, XBee64BitAddress, XBee16BitAddress, int, String, String)}.
	 * 
	 * <p>Construct a new Remote AT Command packet but with a negative receive 
	 * options. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandPacketReceiveOptionsNegativeParameterString() {
		// Setup the resources for the test.
		int frameID = 0x01;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int options = -8;
		String command = "NI";
		String parameter = "Param value";
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Options value must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#RemoteATCommandPacket(int, XBee64BitAddress, XBee16BitAddress, int, String, byte[])}.
	 * 
	 * <p>Construct a new Remote AT Command packet but with a negative receive 
	 * options. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandPacketReceiveOptionsNegativeParameterByteArray() {
		// Setup the resources for the test.
		int frameID = 0x01;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int options = -8;
		String command = "NI";
		byte[] parameter = new byte[]{0x50, 0x61, 0x72, 0x61, 0x6D, 0x20, 0x76, 0x61, 0x6C, 0x75, 0x65};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Options value must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#RemoteATCommandPacket(int, XBee64BitAddress, XBee16BitAddress, int, String, String)}.
	 * 
	 * <p>Construct a new Remote AT Command packet but with a {@code null} 
	 * command. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandPacketCommandNullParameterString() {
		// Setup the resources for the test.
		int frameID = 0x01;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int options = 23;
		String command = null;
		String parameter = "Param value";
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("AT command cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#RemoteATCommandPacket(int, XBee64BitAddress, XBee16BitAddress, int, String, byte[])}.
	 * 
	 * <p>Construct a new Remote AT Command packet but with a {@code null} 
	 * command. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandPacketCommandNullParameterByteArray() {
		// Setup the resources for the test.
		int frameID = 0x01;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int options = 23;
		String command = null;
		byte[] parameter = new byte[]{0x50, 0x61, 0x72, 0x61, 0x6D, 0x20, 0x76, 0x61, 0x6C, 0x75, 0x65};
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("AT command cannot be null.")));
		
		// Call the method under test that should throw n NullPointerException.
		new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#RemoteATCommandPacket(int, XBee64BitAddress, XBee16BitAddress, int, String, byte[])}.
	 * 
	 * <p>Construct a new Remote AT Command packet but with an invalid frame ID, 
	 * bigger than 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandPacketInvalidFrameIDBiggerThan255ParameterString() {
		// Setup the resources for the test.
		int frameID = 2000;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int options = 23;
		String command = "NI";
		String parameter = "Param value";
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#RemoteATCommandPacket(int, XBee64BitAddress, XBee16BitAddress, int, String, byte[])}.
	 * 
	 * <p>Construct a new Remote AT Command packet but with an invalid frame ID, 
	 * bigger than 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandPacketInvalidFrameIDBiggerThan255ParameterByteArray() {
		// Setup the resources for the test.
		int frameID = 2000;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int options = 23;
		String command = "NI";
		byte[] parameter = new byte[]{0x50, 0x61, 0x72, 0x61, 0x6D, 0x20, 0x76, 0x61, 0x6C, 0x75, 0x65};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#RemoteATCommandPacket(int, XBee64BitAddress, XBee16BitAddress, int, String, String)}.
	 * 
	 * <p>Construct a new Remote AT Command packet but with an invalid frame ID 
	 * with negative value. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandPacketInvalidFrameIDNegativeParameterString() {
		// Setup the resources for the test.
		int frameID = -4;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int options = 23;
		String command = "NI";
		String parameter = "Param value";
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#RemoteATCommandPacket(int, XBee64BitAddress, XBee16BitAddress, int, String, byte[])}.
	 * 
	 * <p>Construct a new Remote AT Command packet but with an invalid frame ID
	 * with negative value. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandPacketInvalidFrameIDNegativeParameterByteArray() {
		// Setup the resources for the test.
		int frameID = -4;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int options = 23;
		String command = "NI";
		byte[] parameter = new byte[]{0x50, 0x61, 0x72, 0x61, 0x6D, 0x20, 0x76, 0x61, 0x6C, 0x75, 0x65};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#RemoteATCommandPacket(int, XBee64BitAddress, XBee16BitAddress, int, String, String)}.
	 * 
	 * <p>Construct a new Remote AT Command packet but with an valid parameters 
	 * but without parameter value ({@code null}).</p>
	 */
	@Test
	public final void testCreateRemoteATCommandPacketValidParameterNull() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int options = 23;
		String command = "NI";
		String parameter = null;
		
		int expectedLength = 1 /* Frame type */ + 1 /* Frame ID */ + 8 /* 64-bit address */ + 2 /* 16-bit address */ + 1 /* Options */ + command.length() /* AT command */;
		
		// Call the method under test.
		RemoteATCommandPacket packet = new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, parameter);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination 64-bit address is not the expected one", packet.get64bitDestinationAddress(), is(equalTo(dest64Addr)));
		assertThat("Returned destination 16-bit address is not the expected one", packet.get16bitDestinationAddress(), is(equalTo(dest16Addr)));
		assertThat("Returned options are not the expected one", packet.getTransmitOptions(), is(equalTo(options)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(command))));
		assertThat("Returned Command Data is not the expected one", packet.getParameter(), is(nullValue(byte[].class)));
		assertThat("Remote AT Command needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#RemoteATCommandPacket(int, XBee64BitAddress, XBee16BitAddress, int, String, String)}.
	 * 
	 * <p>Construct a new Remote AT Command packet but with an valid parameters 
	 * but with a parameter value.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandPacketValidParameterString() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int options = 23;
		String command = "NI";
		String parameter = "MyDevice";
		
		int expectedLength = 1 /* Frame type */ + 1 /* Frame ID */ + 8 /* 64-bit address */ + 2 /* 16-bit address */ + 1 /* Options */ + command.length() /* AT command */ + parameter.length() /* Value */;
		
		// Call the method under test.
		RemoteATCommandPacket packet = new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, parameter);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination 64-bit address is not the expected one", packet.get64bitDestinationAddress(), is(equalTo(dest64Addr)));
		assertThat("Returned destination 16-bit address is not the expected one", packet.get16bitDestinationAddress(), is(equalTo(dest16Addr)));
		assertThat("Returned options are not the expected one", packet.getTransmitOptions(), is(equalTo(options)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(command))));
		assertThat("Returned Command Data is not the expected one", packet.getParameter(), is(equalTo(parameter.getBytes())));
		assertThat("Remote AT Command needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#RemoteATCommandPacket(int, XBee64BitAddress, XBee16BitAddress, int, String, byte[])}.
	 * 
	 * <p>Construct a new Remote AT Command packet but with an valid parameters 
	 * but with a parameter value.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandPacketValidParameterByteArray() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int options = 23;
		String command = "NI";
		byte[] parameter = new byte[]{0x6D, 0x79, 0x44, 0x65, 0x76, 0x69, 0x63, 0x65};
		
		int expectedLength = 1 /* Frame type */ + 1 /* Frame ID */ + 8 /* 64-bit address */ + 2 /* 16-bit address */ + 1 /* Options */ + command.length() /* AT command */ + parameter.length /* Value */;
		
		// Call the method under test.
		RemoteATCommandPacket packet = new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, parameter);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination 64-bit address is not the expected one", packet.get64bitDestinationAddress(), is(equalTo(dest64Addr)));
		assertThat("Returned destination 16-bit address is not the expected one", packet.get16bitDestinationAddress(), is(equalTo(dest16Addr)));
		assertThat("Returned options are not the expected one", packet.getTransmitOptions(), is(equalTo(options)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(command))));
		assertThat("Returned Command Data is not the expected one", packet.getParameter(), is(equalTo(parameter)));
		assertThat("Remote AT Command needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#getAPIData()}.
	 * 
	 * <p>Test the get API parameters but with a {@code null} parameter value.</p>
	 */
	@Test
	public final void testGetAPIDataATCommandParameterNull() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int options = 23;
		String command = "NI";
		String parameter = null;
		RemoteATCommandPacket packet = new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, parameter);
		
		int expectedLength = 1 /* Frame ID */ + 8 /* 64-bit address */ + 2 /* 16-bit address */ + 1 /* Options */ + command.length() /* AT command */;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		System.arraycopy(dest64Addr.getValue(), 0, expectedData, 1, dest64Addr.getValue().length);
		System.arraycopy(dest16Addr.getValue(), 0, expectedData, 9, dest16Addr.getValue().length);
		expectedData[11] = (byte)options;
		System.arraycopy(command.getBytes(), 0, expectedData, 12, command.length());
		
		// Call the method under test.
		byte[] data = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#getAPIData()}.
	 * 
	 * <p>Test the get API parameters but with a non-{@code null} parameter value.</p>
	 */
	@Test
	public final void testGetAPIDataATCommandParameterNotNull() {
		// Setup the resources for the test.
		int frameID = 0x10;
		String command = "NI";
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int options = 23;
		byte[] parameter = new byte[]{0x6D, 0x79, 0x44, 0x65, 0x76, 0x69, 0x63, 0x65};
		RemoteATCommandPacket packet = new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, parameter);
		
		int expectedLength = 1 /* Frame ID */ + 8 /* 64-bit address */ + 2 /* 16-bit address */ + 1 /* Options */ + command.length() /* AT command */ + parameter.length /* Value */;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		System.arraycopy(dest64Addr.getValue(), 0, expectedData, 1, dest64Addr.getValue().length);
		System.arraycopy(dest16Addr.getValue(), 0, expectedData, 9, dest16Addr.getValue().length);
		expectedData[11] = (byte)options;
		System.arraycopy(command.getBytes(), 0, expectedData, 12, command.length());
		System.arraycopy(parameter, 0, expectedData, 12 + command.length(), parameter.length);
		
		// Call the method under test.
		byte[] data = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with a {@code null} parameter value.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersATCommandParameterNull() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int options = 23;
		String command = "NI";
		String parameter = null;
		RemoteATCommandPacket packet = new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, parameter);
		
		String expectedDest64Addr = HexUtils.prettyHexString(dest64Addr.getValue());
		String expectedDest16Addr = HexUtils.prettyHexString(dest16Addr.getValue());
		String expectedOptions = HexUtils.prettyHexString(HexUtils.integerToHexString(options, 1));
		String expectedATCommand = HexUtils.prettyHexString(command.getBytes()) + " (" + command + ")";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(4)));
		assertThat("Destination 64-bit Address is not the expected one", packetParams.get("64-bit dest. address"), is(equalTo(expectedDest64Addr)));
		assertThat("Destination 16-bit Address is not the expected one", packetParams.get("16-bit dest. address"), is(equalTo(expectedDest16Addr)));
		assertThat("Command options are not the expected one", packetParams.get("Command options"), is(equalTo(expectedOptions)));
		assertThat("AT Command is not the expected one", packetParams.get("AT Command"), is(equalTo(expectedATCommand)));
		assertThat("AT Parameter is not the expected one", packetParams.get("Parameter"), is(nullValue(String.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with a parameter value.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersATCommandParameterString() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int options = 23;
		String command = "NI";
		String parameter = "myDevice";
		RemoteATCommandPacket packet = new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, parameter);
		
		String expectedDest64Addr = HexUtils.prettyHexString(dest64Addr.getValue());
		String expectedDest16Addr = HexUtils.prettyHexString(dest16Addr.getValue());
		String expectedOptions = HexUtils.prettyHexString(HexUtils.integerToHexString(options, 1));
		String expectedATCommand = HexUtils.prettyHexString(command.getBytes()) + " (" + command + ")";
		String expectedATParameter = HexUtils.prettyHexString(parameter.getBytes()) + " (" + parameter + ")";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(5)));
		assertThat("Destination 64-bit Address is not the expected one", packetParams.get("64-bit dest. address"), is(equalTo(expectedDest64Addr)));
		assertThat("Destination 16-bit Address is not the expected one", packetParams.get("16-bit dest. address"), is(equalTo(expectedDest16Addr)));
		assertThat("Command options are not the expected one", packetParams.get("Command options"), is(equalTo(expectedOptions)));
		assertThat("AT Command is not the expected one", packetParams.get("AT Command"), is(equalTo(expectedATCommand)));
		assertThat("AT Parameter is not the expected one", packetParams.get("Parameter"), is(equalTo(expectedATParameter)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with a parameter value.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersATCommandParameterByteArray() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int options = 23;
		String command = "NI";
		byte[] parameter = new byte[]{0x6D, 0x79, 0x44, 0x65, 0x76, 0x69, 0x63, 0x65};
		RemoteATCommandPacket packet = new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, parameter);
		
		String expectedDest64Addr = HexUtils.prettyHexString(dest64Addr.getValue());
		String expectedDest16Addr = HexUtils.prettyHexString(dest16Addr.getValue());
		String expectedOptions = HexUtils.prettyHexString(HexUtils.integerToHexString(options, 1));
		String expectedATCommand = HexUtils.prettyHexString(command.getBytes()) + " (" + command + ")";
		String expectedATParameter = HexUtils.prettyHexString(parameter) + " (" + new String(parameter) + ")";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(5)));
		assertThat("Destination 64-bit Address is not the expected one", packetParams.get("64-bit dest. address"), is(equalTo(expectedDest64Addr)));
		assertThat("Destination 16-bit Address is not the expected one", packetParams.get("16-bit dest. address"), is(equalTo(expectedDest16Addr)));
		assertThat("Command options are not the expected one", packetParams.get("Command options"), is(equalTo(expectedOptions)));
		assertThat("AT Command is not the expected one", packetParams.get("AT Command"), is(equalTo(expectedATCommand)));
		assertThat("AT Parameter is not the expected one", packetParams.get("Parameter"), is(equalTo(expectedATParameter)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with a parameter value.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersATCommandParameterByteArrayNonStringCmd() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int options = 23;
		String command = "DL";
		byte[] parameter = new byte[]{0x6D, 0x79};
		RemoteATCommandPacket packet = new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, parameter);
		
		String expectedDest64Addr = HexUtils.prettyHexString(dest64Addr.getValue());
		String expectedDest16Addr = HexUtils.prettyHexString(dest16Addr.getValue());
		String expectedOptions = HexUtils.prettyHexString(HexUtils.integerToHexString(options, 1));
		String expectedATCommand = HexUtils.prettyHexString(command.getBytes()) + " (" + command + ")";
		String expectedATParameter = HexUtils.prettyHexString(parameter);
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(5)));
		assertThat("Destination 64-bit Address is not the expected one", packetParams.get("64-bit dest. address"), is(equalTo(expectedDest64Addr)));
		assertThat("Destination 16-bit Address is not the expected one", packetParams.get("16-bit dest. address"), is(equalTo(expectedDest16Addr)));
		assertThat("Command options are not the expected one", packetParams.get("Command options"), is(equalTo(expectedOptions)));
		assertThat("AT Command is not the expected one", packetParams.get("AT Command"), is(equalTo(expectedATCommand)));
		assertThat("AT Parameter is not the expected one", packetParams.get("Parameter"), is(equalTo(expectedATParameter)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#isBroadcast()}.
	 * 
	 * <p>Test if a Remote AT Command packet is a broadcast packet address when 
	 * 16-bit and a 64-bit destination addresses are not broadcast.</p>
	 */
	@Test
	public final void testIsBroadcastWithNon16BitAndNon64BitBroadcastDestinationAddress() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("D817");
		int options = 23;
		String command = "DL";
		byte[] parameter = new byte[]{0x6D, 0x79};
		RemoteATCommandPacket packet = new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, parameter);
		
		// Call the method under test and verify the result.
		assertThat("Packet should not be broadcast", packet.isBroadcast(), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#isBroadcast()}.
	 * 
	 * <p>Test if a Remote AT Command packet is a broadcast packet address when 
	 * both destination addresses, 16-bit and 64-bit, are broadcast.</p>
	 */
	@Test
	public final void testIsBroadcastWith16BitAnd64BitBroadcastDestinationAddress() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("FFFF");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("FFFF");
		int options = 23;
		String command = "DL";
		byte[] parameter = new byte[]{0x6D, 0x79};
		RemoteATCommandPacket packet = new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, parameter);
		
		// Call the method under test and verify the result.
		assertThat("Packet should be broadcast", packet.isBroadcast(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#isBroadcast()}.
	 * 
	 * <p>Test if a Remote AT Command packet is a broadcast packet address when 
	 * the 16-bit destination address is not broadcast and 64-bit is.</p>
	 */
	@Test
	public final void testIsBroadcastWithNon16BitBroadcastAnd64BitBroadcastDestinationAddress() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("FFFF");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("DEF1");
		int options = 23;
		String command = "DL";
		byte[] parameter = new byte[]{0x6D, 0x79};
		RemoteATCommandPacket packet = new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, parameter);
		
		// Call the method under test and verify the result.
		assertThat("Packet should be broadcast", packet.isBroadcast(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#isBroadcast()}.
	 * 
	 * <p>Test if a Remote AT Command packet is a broadcast packet address when 
	 * the 16-bit destination address is broadcast but 64-bit is not.</p>
	 */
	@Test
	public final void testIsBroadcastWith16BitBroadcastAndNon64BitBroadcastDestinationAddress() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("FFFF");
		int options = 23;
		String command = "DL";
		byte[] parameter = new byte[]{0x6D, 0x79};
		RemoteATCommandPacket packet = new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, parameter);
		
		// Call the method under test and verify the result.
		assertThat("Packet should be broadcast", packet.isBroadcast(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#setParameter(String)}.
	 * 
	 * <p>Test if a string parameter is properly configured.</p>
	 */
	@Test
	public final void testSetParameterString() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("FFFF");
		int options = 23;
		String command = "NI";
		String parameterToSet = "newNIValue";
		RemoteATCommandPacket packet = new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, "");
		
		// Call the method under test.
		packet.setParameter(parameterToSet);
		
		// Verify the result.
		assertThat("Configured parameter must be '" + parameterToSet + "'", 
				packet.getParameterAsString(), is(equalTo(parameterToSet)));
		assertThat("Configured parameter must be '" + parameterToSet + "'", 
				packet.getParameter(), is(equalTo(parameterToSet.getBytes())));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#setParameter(String)}.
	 * 
	 * <p>Test if a string parameter with {@code null} value is properly 
	 * configured.</p>
	 */
	@Test
	public final void testSetParameterStringNull() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("FFFF");
		int options = 23;
		String command = "NI";
		RemoteATCommandPacket packet = new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, "");
		
		// Call the method under test.
		packet.setParameter((String)null);
		
		// Verify the result.
		assertThat("Configured parameter must be 'null'", 
				packet.getParameterAsString(), is(equalTo(null)));
		assertThat("Configured parameter must be 'null'", 
				packet.getParameter(), is(equalTo(null)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#setParameter(byte[])}.
	 * 
	 * <p>Test if a byte array parameter is properly configured.</p>
	 */
	@Test
	public final void testSetParameterByteArray() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("FFFF");
		int options = 23;
		String command = "NI";
		byte[] parameterToSet = new byte[]{0x6D, 0x79, 0x44, 0x65, 0x76, 0x69, 0x63, 0x65};
		RemoteATCommandPacket packet = new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, new byte[0]);
		
		// Call the method under test.
		packet.setParameter(parameterToSet);
		
		// Verify the result.
		assertThat("Configured parameter must be '" + new String(parameterToSet) + "'", 
				packet.getParameter(), is(equalTo(parameterToSet)));
		assertThat("Configured parameter must be '" + parameterToSet + "'", 
				packet.getParameterAsString(), is(equalTo(new String(parameterToSet))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#setParameter(byte[])}.
	 * 
	 * <p>Test if a byte array parameter with {@code null} value is properly 
	 * configured.</p>
	 */
	@Test
	public final void testSetParameterByteArrayNull() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("FFFF");
		int options = 23;
		String command = "NI";
		RemoteATCommandPacket packet = new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, new byte[0]);
		
		// Call the method under test.
		packet.setParameter((byte[])null);
		
		// Verify the result.
		assertThat("Configured parameter must be 'null'", packet.getParameter(), is(equalTo(null)));
		assertThat("Configured parameter must be 'null'", packet.getParameterAsString(), is(equalTo(null)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#getParameterAsString()}.
	 * 
	 * <p>Test if a configured parameter is properly returned.</p>
	 */
	@Test
	public final void testGetParameterAsString() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("FFFF");
		int options = 23;
		String command = "NI";
		String parameterToSet = "newNIValue";
		RemoteATCommandPacket packet = new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, parameterToSet);
		
		// Call the method under test.
		String value = packet.getParameterAsString();
		
		// Verify the result.
		assertThat("Returned parameter must be '" + parameterToSet + "'", value, is(equalTo(parameterToSet)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#getParameterAsString()}.
	 * 
	 * <p>Test if a configured parameter is properly returned.</p>
	 */
	@Test
	public final void testGetParameterAsStringNullValue() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("FFFF");
		int options = 23;
		String command = "NI";
		RemoteATCommandPacket packet = new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, (String)null);
		
		// Call the method under test.
		String value = packet.getParameterAsString();
		
		// Verify the result.
		assertThat("Returned parameter must be 'null'", value, is(equalTo(null)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#getParameter()}.
	 * 
	 * <p>Test if a configured parameter is properly returned.</p>
	 */
	@Test
	public final void testGetParameter() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("FFFF");
		int options = 23;
		String command = "NI";
		String parameterToSet = "newNIValue";
		RemoteATCommandPacket packet = new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, parameterToSet);
		
		// Call the method under test.
		byte[] value = packet.getParameter();
		
		// Verify the result.
		assertThat("Returned parameter must be '" + parameterToSet + "'", value, is(equalTo(parameterToSet.getBytes())));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.RemoteATCommandPacket#getParameter()}.
	 * 
	 * <p>Test if a configured parameter is properly returned.</p>
	 */
	@Test
	public final void testGetParameterNullValue() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("FFFF");
		int options = 23;
		String command = "NI";
		RemoteATCommandPacket packet = new RemoteATCommandPacket(frameID, dest64Addr, dest16Addr, options, command, (byte[])null);
		
		// Call the method under test.
		byte[] value = packet.getParameter();
		
		// Verify the result.
		assertThat("Returned parameter must be 'null'", value, is(equalTo(null)));
	}
}
