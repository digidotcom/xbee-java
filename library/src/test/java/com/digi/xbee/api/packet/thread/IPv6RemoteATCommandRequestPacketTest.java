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
package com.digi.xbee.api.packet.thread;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

import java.net.Inet6Address;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.digi.xbee.api.models.RemoteATCommandOptions;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

public class IPv6RemoteATCommandRequestPacketTest {

	// Constants.
	private static final String IPV6_ADDR = "FDB3:0001:0002:0000:0004:0005:0006:0007";

	// Variables.
	private int frameType = APIFrameType.IPV6_REMOTE_AT_COMMAND_REQUEST.getValue();
	private int frameID = 0xE7;
	private Inet6Address ipv6address;
	private String command = "NI";
	private int transmitOptions = RemoteATCommandOptions.OPTION_APPLY_CHANGES;
	private byte[] parameter = "hello".getBytes();
	private String parameterStr = "hello";

	@Rule
	public ExpectedException exception = ExpectedException.none();

	public IPv6RemoteATCommandRequestPacketTest() throws Exception {
		ipv6address = (Inet6Address) Inet6Address.getByName(IPV6_ADDR);
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
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#createPacket(byte[])}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a 
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Setup the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("IPv6 Remote AT command packet payload cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		IPv6RemoteATCommandRequestPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Setup the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete IPv6 Remote AT command packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		IPv6RemoteATCommandRequestPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Setup the resources for the test.
		String cmd = "A"; // Invalid AT command.
		
		byte[] payload = new byte[19 + cmd.getBytes().length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(ipv6address.getAddress(), 0, payload, 2, ipv6address.getAddress().length);
		payload[18] = (byte)transmitOptions;
		System.arraycopy(cmd.getBytes(), 0, payload, 19, cmd.getBytes().length);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete IPv6 Remote AT command packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		IPv6RemoteATCommandRequestPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array not including the Frame type.</p>
	 */
	@Test
	public final void testCreatePacketPayloadNotIncludingFrameType() {
		byte[] payload = new byte[20 + parameter.length];
		payload[0] = (byte)frameID;
		System.arraycopy(ipv6address.getAddress(), 0, payload, 1, ipv6address.getAddress().length);
		payload[17] = (byte)transmitOptions;
		System.arraycopy(command.getBytes(), 0, payload, 18, command.getBytes().length);
		System.arraycopy(parameter, 0, payload, 20, parameter.length);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not an IPv6 Remote AT command packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		IPv6RemoteATCommandRequestPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#createPacket(byte[])}.
	 * 
	 * <p>A valid IPv6 Remote AT command packet with the provided options without 
	 * data is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutData() {
		byte[] payload = new byte[19 + command.getBytes().length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(ipv6address.getAddress(), 0, payload, 2, ipv6address.getAddress().length);
		payload[18] = (byte)transmitOptions;
		System.arraycopy(command.getBytes(), 0, payload, 19, command.getBytes().length);
		
		// Call the method under test.
		IPv6RemoteATCommandRequestPacket packet = IPv6RemoteATCommandRequestPacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination IPv6 address is not the expected one", packet.getDestAddress(), is(equalTo(ipv6address)));
		assertThat("Returned transmit options is not the expected one", packet.getTransmitOptions(), is(equalTo(transmitOptions)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(command)));
		assertThat("Returned Parameter Data is not the expected one", packet.getParameter(), is(nullValue()));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#createPacket(byte[])}.
	 * 
	 * <p>A valid IPv6 Remote AT command packet with the provided options and data is 
	 * created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithData() {
		byte[] payload = new byte[21 + parameter.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(ipv6address.getAddress(), 0, payload, 2, ipv6address.getAddress().length);
		payload[18] = (byte)transmitOptions;
		System.arraycopy(command.getBytes(), 0, payload, 19, command.getBytes().length);
		System.arraycopy(parameter, 0, payload, 21, parameter.length);
		
		// Call the method under test.
		IPv6RemoteATCommandRequestPacket packet = IPv6RemoteATCommandRequestPacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination IPv6 address is not the expected one", packet.getDestAddress(), is(equalTo(ipv6address)));
		assertThat("Returned transmit options is not the expected one", packet.getTransmitOptions(), is(equalTo(transmitOptions)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(command)));
		assertThat("Returned Parameter Data is not the expected one", packet.getParameter(), is(equalTo(parameter)));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#IPv6RemoteATCommandRequestPacket(int, Inet6Address, int, String, String)}.
	 * 
	 * <p>Construct a new IPv6 Remote AT command packet but with a {@code null} 
	 * Destination address cannot be null.. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateIPv6RemoteATCommandRequestPacketIPv6AddressNullParameterString() {
		// Setup the resources for the test.
		ipv6address = null;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Destination address cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new IPv6RemoteATCommandRequestPacket(frameID, ipv6address, transmitOptions, command, parameterStr);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#IPv6RemoteATCommandRequestPacket(int, Inet6Address, int, String, byte[])}.
	 * 
	 * <p>Construct a new IPv6 Remote AT command packet but with a {@code null} 
	 * Destination address cannot be null.. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateIPv6RemoteATCommandRequestPacketIPv6AddressNullParameterByteArray() {
		// Setup the resources for the test.
		ipv6address = null;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Destination address cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new IPv6RemoteATCommandRequestPacket(frameID, ipv6address, transmitOptions, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#IPv6RemoteATCommandRequestPacket(int, Inet6Address, int, String, String)}.
	 * 
	 * <p>Construct a new IPv6 Remote AT command packet but with a receive options 
	 * bigger than 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateIPv6RemoteATCommandRequestPacketReceiveOptionsBiggerThan255ParameterString() {
		// Setup the resources for the test.
		transmitOptions = 621;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Transmit options can only be 2 or 0.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new IPv6RemoteATCommandRequestPacket(frameID, ipv6address, transmitOptions, command, parameterStr);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#IPv6RemoteATCommandRequestPacket(int, Inet6Address, int, String, byte[])}.
	 * 
	 * <p>Construct a new IPv6 Remote AT command packet but with a receive options 
	 * bigger than 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateIPv6RemoteATCommandRequestPacketReceiveOptionsBiggerThan255ParameterByteArray() {
		// Setup the resources for the test.
		transmitOptions = 621;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Transmit options can only be 2 or 0.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new IPv6RemoteATCommandRequestPacket(frameID, ipv6address, transmitOptions, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#IPv6RemoteATCommandRequestPacket(int, Inet6Address, int, String, String)}.
	 * 
	 * <p>Construct a new IPv6 Remote AT command packet but with a negative receive 
	 * options. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateIPv6RemoteATCommandRequestPacketReceiveOptionsNegativeParameterString() {
		// Setup the resources for the test.
		transmitOptions = -8;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Transmit options can only be 2 or 0.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new IPv6RemoteATCommandRequestPacket(frameID, ipv6address, transmitOptions, command, parameterStr);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#IPv6RemoteATCommandRequestPacket(int, Inet6Address, int, String, byte[])}.
	 * 
	 * <p>Construct a new IPv6 Remote AT command packet but with a negative receive 
	 * options. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateIPv6RemoteATCommandRequestPacketReceiveOptionsNegativeParameterByteArray() {
		// Setup the resources for the test.
		transmitOptions = -8;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Transmit options can only be 2 or 0.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new IPv6RemoteATCommandRequestPacket(frameID, ipv6address, transmitOptions, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#IPv6RemoteATCommandRequestPacket(int, Inet6Address, int, String, String)}.
	 * 
	 * <p>Construct a new IPv6 Remote AT command packet but with a {@code null} 
	 * command. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateIPv6RemoteATCommandRequestPacketCommandNullParameterString() {
		// Setup the resources for the test.
		command = null;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("AT command cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new IPv6RemoteATCommandRequestPacket(frameID, ipv6address, transmitOptions, command, parameterStr);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#IPv6RemoteATCommandRequestPacket(int, Inet6Address, int, String, byte[])}.
	 * 
	 * <p>Construct a new IPv6 Remote AT command packet but with a {@code null} 
	 * command. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateIPv6RemoteATCommandRequestPacketCommandNullParameterByteArray() {
		// Setup the resources for the test.
		command = null;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("AT command cannot be null.")));
		
		// Call the method under test that should throw n NullPointerException.
		new IPv6RemoteATCommandRequestPacket(frameID, ipv6address, transmitOptions, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#IPv6RemoteATCommandRequestPacket(int, Inet6Address, int, String, byte[])}.
	 * 
	 * <p>Construct a new IPv6 Remote AT command packet but with an invalid frame ID, 
	 * bigger than 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateIPv6RemoteATCommandRequestPacketInvalidFrameIDBiggerThan255ParameterString() {
		// Setup the resources for the test.
		frameID = 2000;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new IPv6RemoteATCommandRequestPacket(frameID, ipv6address, transmitOptions, command, parameterStr);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#IPv6RemoteATCommandRequestPacket(int, Inet6Address, int, String, byte[])}.
	 * 
	 * <p>Construct a new IPv6 Remote AT command packet but with an invalid frame ID, 
	 * bigger than 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateIPv6RemoteATCommandRequestPacketInvalidFrameIDBiggerThan255ParameterByteArray() {
		// Setup the resources for the test.
		frameID = 2000;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new IPv6RemoteATCommandRequestPacket(frameID, ipv6address, transmitOptions, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#IPv6RemoteATCommandRequestPacket(int, Inet6Address, int, String, String)}.
	 * 
	 * <p>Construct a new IPv6 Remote AT command packet but with an invalid frame ID 
	 * with negative value. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateIPv6RemoteATCommandRequestPacketInvalidFrameIDNegativeParameterString() {
		// Setup the resources for the test.
		frameID = -4;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new IPv6RemoteATCommandRequestPacket(frameID, ipv6address, transmitOptions, command, parameterStr);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#IPv6RemoteATCommandRequestPacket(int, Inet6Address, int, String, byte[])}.
	 * 
	 * <p>Construct a new IPv6 Remote AT command packet but with an invalid frame ID
	 * with negative value. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateIPv6RemoteATCommandRequestPacketInvalidFrameIDNegativeParameterByteArray() {
		// Setup the resources for the test.
		frameID = -4;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new IPv6RemoteATCommandRequestPacket(frameID, ipv6address, transmitOptions, command, parameter);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#IPv6RemoteATCommandRequestPacket(int, Inet6Address, int, String, String)}.
	 * 
	 * <p>Construct a new IPv6 Remote AT command packet but with an valid parameters 
	 * but without parameter value ({@code null}).</p>
	 */
	@Test
	public final void testCreateIPv6RemoteATCommandRequestPacketValidParameterNull() {
		// Setup the resources for the test.
		parameter = null;
		
		// Call the method under test.
		IPv6RemoteATCommandRequestPacket packet = new IPv6RemoteATCommandRequestPacket(frameID, ipv6address, transmitOptions, command, parameter);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(21)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination IPv6 address is not the expected one", packet.getDestAddress(), is(equalTo(ipv6address)));
		assertThat("Returned options are not the expected one", packet.getTransmitOptions(), is(equalTo(transmitOptions)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(command))));
		assertThat("Returned Command Data is not the expected one", packet.getParameter(), is(nullValue(byte[].class)));
		assertThat("Remote AT Command needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#IPv6RemoteATCommandRequestPacket(int, Inet6Address, int, String, String)}.
	 * 
	 * <p>Construct a new IPv6 Remote AT command packet but with an valid parameters 
	 * but with a parameter value.</p>
	 */
	@Test
	public final void testCreateIPv6RemoteATCommandRequestPacketValidParameterString() {
		// Call the method under test.
		IPv6RemoteATCommandRequestPacket packet = new IPv6RemoteATCommandRequestPacket(frameID, ipv6address, transmitOptions, command, parameterStr);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(21 + parameterStr.getBytes().length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination IPv6 address is not the expected one", packet.getDestAddress(), is(equalTo(ipv6address)));
		assertThat("Returned options are not the expected one", packet.getTransmitOptions(), is(equalTo(transmitOptions)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(command))));
		assertThat("Returned Command Data is not the expected one", packet.getParameterAsString(), is(equalTo(parameterStr)));
		assertThat("Remote AT Command needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#IPv6RemoteATCommandRequestPacket(int, Inet6Address, int, String, byte[])}.
	 * 
	 * <p>Construct a new IPv6 Remote AT command packet but with an valid parameters 
	 * but with a parameter value.</p>
	 */
	@Test
	public final void testCreateIPv6RemoteATCommandRequestPacketValidParameterByteArray() {
		// Call the method under test.
		IPv6RemoteATCommandRequestPacket packet = new IPv6RemoteATCommandRequestPacket(frameID, ipv6address, transmitOptions, command, parameter);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(21 + parameter.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination IPv6 address is not the expected one", packet.getDestAddress(), is(equalTo(ipv6address)));
		assertThat("Returned options are not the expected one", packet.getTransmitOptions(), is(equalTo(transmitOptions)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(command))));
		assertThat("Returned Command Data is not the expected one", packet.getParameter(), is(equalTo(parameter)));
		assertThat("Remote AT Command needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#getAPIData()}.
	 * 
	 * <p>Test the get API parameters but with a {@code null} parameter value.</p>
	 */
	@Test
	public final void testGetAPIDataATCommandParameterNull() {
		// Setup the resources for the test.
		parameter = null;
		IPv6RemoteATCommandRequestPacket packet = new IPv6RemoteATCommandRequestPacket(frameID, ipv6address, transmitOptions, command, parameter);
		
		byte[] expectedData = new byte[20];
		expectedData[0] = (byte)frameID;
		System.arraycopy(ipv6address.getAddress(), 0, expectedData, 1, ipv6address.getAddress().length);
		expectedData[17] = (byte)transmitOptions;
		System.arraycopy(command.getBytes(), 0, expectedData, 18, command.getBytes().length);
		
		// Call the method under test.
		byte[] data = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#getAPIData()}.
	 * 
	 * <p>Test the get API parameters but with a non-{@code null} parameter value.</p>
	 */
	@Test
	public final void testGetAPIDataATCommandParameterNotNull() {
		IPv6RemoteATCommandRequestPacket packet = new IPv6RemoteATCommandRequestPacket(frameID, ipv6address, transmitOptions, command, parameter);
		
		byte[] expectedData = new byte[20 + parameter.length];
		expectedData[0] = (byte)frameID;
		System.arraycopy(ipv6address.getAddress(), 0, expectedData, 1, ipv6address.getAddress().length);
		expectedData[17] = (byte)transmitOptions;
		System.arraycopy(command.getBytes(), 0, expectedData, 18, command.getBytes().length);
		System.arraycopy(parameter, 0, expectedData, 20, parameter.length);
		
		// Call the method under test.
		byte[] data = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with a {@code null} parameter value.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersATCommandParameterNull() {
		parameter = null;
		IPv6RemoteATCommandRequestPacket packet = new IPv6RemoteATCommandRequestPacket(frameID, ipv6address, transmitOptions, command, parameter);
		
		String expectedDestAddr = HexUtils.prettyHexString(ipv6address.getAddress()) + " (" + ipv6address.getHostAddress() + ")";
		String expectedOptions = HexUtils.prettyHexString(HexUtils.integerToHexString(transmitOptions, 1));
		String expectedATCommand = HexUtils.prettyHexString(command.getBytes()) + " (" + command + ")";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(3)));
		assertThat("Destination IPv6 Address is not the expected one", packetParams.get("Destination address"), is(equalTo(expectedDestAddr)));
		assertThat("Command options are not the expected one", packetParams.get("Command options"), is(equalTo(expectedOptions)));
		assertThat("AT Command is not the expected one", packetParams.get("AT Command"), is(equalTo(expectedATCommand)));
		assertThat("AT Parameter is not the expected one", packetParams.get("Parameter"), is(nullValue(String.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with a parameter value.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersATCommandParameterString() {
		IPv6RemoteATCommandRequestPacket packet = new IPv6RemoteATCommandRequestPacket(frameID, ipv6address, transmitOptions, command, parameterStr);
		
		String expectedDestAddr = HexUtils.prettyHexString(ipv6address.getAddress()) + " (" + ipv6address.getHostAddress() + ")";
		String expectedOptions = HexUtils.prettyHexString(HexUtils.integerToHexString(transmitOptions, 1));
		String expectedATCommand = HexUtils.prettyHexString(command.getBytes()) + " (" + command + ")";
		String expectedATParameter = HexUtils.prettyHexString(parameterStr.getBytes()) + " (" + parameterStr + ")";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(4)));
		assertThat("Destination IPv6 Address is not the expected one", packetParams.get("Destination address"), is(equalTo(expectedDestAddr)));
		assertThat("Command options are not the expected one", packetParams.get("Command options"), is(equalTo(expectedOptions)));
		assertThat("AT Command is not the expected one", packetParams.get("AT Command"), is(equalTo(expectedATCommand)));
		assertThat("AT Parameter is not the expected one", packetParams.get("Parameter"), is(equalTo(expectedATParameter)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with a parameter value.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersATCommandParameterByteArray() {
		IPv6RemoteATCommandRequestPacket packet = new IPv6RemoteATCommandRequestPacket(frameID, ipv6address, transmitOptions, command, parameter);
		
		String expectedDestAddr = HexUtils.prettyHexString(ipv6address.getAddress()) + " (" + ipv6address.getHostAddress() + ")";
		String expectedOptions = HexUtils.prettyHexString(HexUtils.integerToHexString(transmitOptions, 1));
		String expectedATCommand = HexUtils.prettyHexString(command.getBytes()) + " (" + command + ")";
		String expectedATParameter = HexUtils.prettyHexString(parameterStr.getBytes()) + " (" + parameterStr + ")";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(4)));
		assertThat("Destination IPv6 Address is not the expected one", packetParams.get("Destination address"), is(equalTo(expectedDestAddr)));
		assertThat("Command options are not the expected one", packetParams.get("Command options"), is(equalTo(expectedOptions)));
		assertThat("AT Command is not the expected one", packetParams.get("AT Command"), is(equalTo(expectedATCommand)));
		assertThat("AT Parameter is not the expected one", packetParams.get("Parameter"), is(equalTo(expectedATParameter)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with a parameter value.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersATCommandParameterByteArrayNonStringCmd() {
		// Setup the resources for the test.
		String command = "DL";
		byte[] parameter = new byte[]{0x6D, 0x79};
		IPv6RemoteATCommandRequestPacket packet = new IPv6RemoteATCommandRequestPacket(frameID, ipv6address, transmitOptions, command, parameter);
		
		String expectedDestAddr = HexUtils.prettyHexString(ipv6address.getAddress()) + " (" + ipv6address.getHostAddress() + ")";
		String expectedOptions = HexUtils.prettyHexString(HexUtils.integerToHexString(transmitOptions, 1));
		String expectedATCommand = HexUtils.prettyHexString(command.getBytes()) + " (" + command + ")";
		String expectedATParameter = HexUtils.prettyHexString(parameter);
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(4)));
		assertThat("Destination IPv6 Address is not the expected one", packetParams.get("Destination address"), is(equalTo(expectedDestAddr)));
		assertThat("Command options are not the expected one", packetParams.get("Command options"), is(equalTo(expectedOptions)));
		assertThat("AT Command is not the expected one", packetParams.get("AT Command"), is(equalTo(expectedATCommand)));
		assertThat("AT Parameter is not the expected one", packetParams.get("Parameter"), is(equalTo(expectedATParameter)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#setParameter(String)}.
	 * 
	 * <p>Test if a string parameter is properly configured.</p>
	 */
	@Test
	public final void testSetParameterString() {
		// Setup the resources for the test.
		String parameterToSet = "newValue";
		IPv6RemoteATCommandRequestPacket packet = new IPv6RemoteATCommandRequestPacket(frameID, ipv6address, transmitOptions, command, parameterStr);
		
		// Call the method under test.
		packet.setParameter(parameterToSet);
		
		// Verify the result.
		assertThat("Configured parameter must be '" + parameterToSet + "'", 
				packet.getParameterAsString(), is(equalTo(parameterToSet)));
		assertThat("Configured parameter must be '" + parameterToSet + "'", 
				packet.getParameter(), is(equalTo(parameterToSet.getBytes())));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#setParameter(String)}.
	 * 
	 * <p>Test if a string parameter with {@code null} value is properly 
	 * configured.</p>
	 */
	@Test
	public final void testSetParameterStringNull() {
		IPv6RemoteATCommandRequestPacket packet = new IPv6RemoteATCommandRequestPacket(frameID, ipv6address, transmitOptions, command, parameterStr);
		
		// Call the method under test.
		packet.setParameter((String)null);
		
		// Verify the result.
		assertThat("Configured parameter must be 'null'", 
				packet.getParameterAsString(), is(equalTo(null)));
		assertThat("Configured parameter must be 'null'", 
				packet.getParameter(), is(equalTo(null)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#setParameter(byte[])}.
	 * 
	 * <p>Test if a byte array parameter is properly configured.</p>
	 */
	@Test
	public final void testSetParameterByteArray() {
		// Setup the resources for the test.
		byte[] parameterToSet = new byte[]{0x6D, 0x79, 0x44, 0x65, 0x76, 0x69, 0x63, 0x65};
		IPv6RemoteATCommandRequestPacket packet = new IPv6RemoteATCommandRequestPacket(frameID, ipv6address, transmitOptions, command, parameter);
		
		// Call the method under test.
		packet.setParameter(parameterToSet);
		
		// Verify the result.
		assertThat("Configured parameter must be '" + new String(parameterToSet) + "'", 
				packet.getParameter(), is(equalTo(parameterToSet)));
		assertThat("Configured parameter must be '" + parameterToSet + "'", 
				packet.getParameterAsString(), is(equalTo(new String(parameterToSet))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#setParameter(byte[])}.
	 * 
	 * <p>Test if a byte array parameter with {@code null} value is properly 
	 * configured.</p>
	 */
	@Test
	public final void testSetParameterByteArrayNull() {
		IPv6RemoteATCommandRequestPacket packet = new IPv6RemoteATCommandRequestPacket(frameID, ipv6address, transmitOptions, command, parameter);
		
		// Call the method under test.
		packet.setParameter((byte[])null);
		
		// Verify the result.
		assertThat("Configured parameter must be 'null'", packet.getParameter(), is(equalTo(null)));
		assertThat("Configured parameter must be 'null'", packet.getParameterAsString(), is(equalTo(null)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#getParameterAsString()}.
	 * 
	 * <p>Test if a configured parameter is properly returned.</p>
	 */
	@Test
	public final void testGetParameterAsString() {
		IPv6RemoteATCommandRequestPacket packet = new IPv6RemoteATCommandRequestPacket(frameID, ipv6address, transmitOptions, command, parameterStr);
		
		// Call the method under test.
		String value = packet.getParameterAsString();
		
		// Verify the result.
		assertThat("Returned parameter must be '" + parameterStr + "'", value, is(equalTo(parameterStr)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#getParameterAsString()}.
	 * 
	 * <p>Test if a configured parameter is properly returned.</p>
	 */
	@Test
	public final void testGetParameterAsStringNullValue() {
		IPv6RemoteATCommandRequestPacket packet = new IPv6RemoteATCommandRequestPacket(frameID, ipv6address, transmitOptions, command, (String)null);
		
		// Call the method under test.
		String value = packet.getParameterAsString();
		
		// Verify the result.
		assertThat("Returned parameter must be 'null'", value, is(equalTo(null)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#getParameter()}.
	 * 
	 * <p>Test if a configured parameter is properly returned.</p>
	 */
	@Test
	public final void testGetParameter() {
		IPv6RemoteATCommandRequestPacket packet = new IPv6RemoteATCommandRequestPacket(frameID, ipv6address, transmitOptions, command, parameter);
		
		// Call the method under test.
		byte[] value = packet.getParameter();
		
		// Verify the result.
		assertThat("Returned parameter must be '" + HexUtils.prettyHexString(parameter) + "'", value, is(equalTo(parameter)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket#getParameter()}.
	 * 
	 * <p>Test if a configured parameter is properly returned.</p>
	 */
	@Test
	public final void testGetParameterNullValue() {
		IPv6RemoteATCommandRequestPacket packet = new IPv6RemoteATCommandRequestPacket(frameID, ipv6address, transmitOptions, command, (byte[])null);
		
		// Call the method under test.
		byte[] value = packet.getParameter();
		
		// Verify the result.
		assertThat("Returned parameter must be 'null'", value, is(equalTo(null)));
	}
}
