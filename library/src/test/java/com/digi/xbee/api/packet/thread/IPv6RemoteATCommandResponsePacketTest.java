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

import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

public class IPv6RemoteATCommandResponsePacketTest {

	// Constants.
	private static final String IPV6_ADDR = "FDB3:0001:0002:0000:0004:0005:0006:0007";

	// Variables.
	private int frameType = APIFrameType.IPV6_REMOTE_AT_COMMAND_RESPONSE.getValue();
	private int frameID = 0xE7;
	private Inet6Address ipv6address;
	private String command = "NI";
	private ATCommandStatus status = ATCommandStatus.OK;
	private String valueStr = "device_name";
	private byte[] value = valueStr.getBytes();

	@Rule
	public ExpectedException exception = ExpectedException.none();

	public IPv6RemoteATCommandResponsePacketTest() throws Exception {
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
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#createPacket(byte[])}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a 
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Setup the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("IPv6 Remote AT command response packet payload cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		IPv6RemoteATCommandResponsePacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Setup the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete IPv6 Remote AT command response packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		IPv6RemoteATCommandResponsePacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#createPacket(byte[])}.
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
		System.arraycopy(cmd.getBytes(), 0, payload, 18, cmd.getBytes().length);
		payload[19] = (byte)status.getId();
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete IPv6 Remote AT command response packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		IPv6RemoteATCommandResponsePacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array not including the Frame type.</p>
	 */
	@Test
	public final void testCreatePacketPayloadNotIncludingFrameType() {
		byte[] payload = new byte[20 + value.length];
		payload[0] = (byte)frameID;
		System.arraycopy(ipv6address.getAddress(), 0, payload, 1, ipv6address.getAddress().length);
		System.arraycopy(command.getBytes(), 0, payload, 17, command.getBytes().length);
		payload[18] = (byte)status.getId();
		System.arraycopy(value, 0, payload, 19, value.length);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not an IPv6 Remote AT command response packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		IPv6RemoteATCommandResponsePacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#createPacket(byte[])}.
	 * 
	 * <p>A valid IPv6 Remote AT command response packet with the provided options without 
	 * command value is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutCommandValue() {
		byte[] payload = new byte[19 + command.getBytes().length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(ipv6address.getAddress(), 0, payload, 2, ipv6address.getAddress().length);
		System.arraycopy(command.getBytes(), 0, payload, 18, command.getBytes().length);
		payload[20] = (byte)status.getId();
		
		// Call the method under test.
		IPv6RemoteATCommandResponsePacket packet = IPv6RemoteATCommandResponsePacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned source IPv6 address is not the expected one", packet.getSourceAddress(), is(equalTo(ipv6address)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(command))));
		assertThat("Returned status is not the expected one", packet.getStatus(), is(equalTo(status)));
		assertThat("Returned value is not the expected one", packet.getCommandValue(), is(nullValue()));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#createPacket(byte[])}.
	 * 
	 * <p>A valid IPv6 Remote AT command response packet with the provided options and command value is 
	 * created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithCommandValue() {
		byte[] payload = new byte[21 + value.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(ipv6address.getAddress(), 0, payload, 2, ipv6address.getAddress().length);
		System.arraycopy(command.getBytes(), 0, payload, 18, command.getBytes().length);
		payload[20] = (byte)status.getId();
		System.arraycopy(value, 0, payload, 21, value.length);
		
		// Call the method under test.
		IPv6RemoteATCommandResponsePacket packet = IPv6RemoteATCommandResponsePacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned source IPv6 address is not the expected one", packet.getSourceAddress(), is(equalTo(ipv6address)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(command))));
		assertThat("Returned status is not the expected one", packet.getStatus(), is(equalTo(status)));
		assertThat("Returned value is not the expected one", packet.getCommandValue(), is(equalTo(value)));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#IPv6RemoteATCommandResponsePacket(int, Inet6Address, String, ATCommandStatus, String)}.
	 * 
	 * <p>Construct a new IPv6 Remote AT command response packet with a {@code null} 
	 * source address. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateIPv6RemoteATCommandResponsePacketIPv6AddressNullCommandValueString() {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Source address cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new IPv6RemoteATCommandResponsePacket(frameID, null, command, status, valueStr);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#IPv6RemoteATCommandResponsePacket(int, Inet6Address, String, ATCommandStatus, byte[])}.
	 * 
	 * <p>Construct a new IPv6 Remote AT command response packet with a {@code null} 
	 * source address. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateIPv6RemoteATCommandResponsePacketIPv6AddressNullCommandValueByteArray() {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Source address cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new IPv6RemoteATCommandResponsePacket(frameID, null, command, status, value);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#IPv6RemoteATCommandResponsePacket(int, Inet6Address, String, ATCommandStatus, String)}.
	 * 
	 * <p>Construct a new IPv6 Remote AT command response packet with a {@code null} 
	 * command. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateIPv6RemoteATCommandResponsePacketCommandNullCommandValueString() {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("AT command cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new IPv6RemoteATCommandResponsePacket(frameID, ipv6address, null, status, valueStr);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#IPv6RemoteATCommandResponsePacket(int, Inet6Address, String, ATCommandStatus, byte[])}.
	 * 
	 * <p>Construct a new IPv6 Remote AT command response packet with a {@code null} 
	 * command. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateIPv6RemoteATCommandResponsePacketCommandNullCommandValueByteArray() {
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("AT command cannot be null.")));
		
		// Call the method under test that should throw n NullPointerException.
		new IPv6RemoteATCommandResponsePacket(frameID, ipv6address, null, status, value);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#IPv6RemoteATCommandResponsePacket(int, Inet6Address, String, ATCommandStatus, String)}.
	 * 
	 * <p>Construct a new IPv6 Remote AT command response packet but with a 
	 * {@code null} status. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateIPv6RemoteATCommandResponsePacketStatusNullCommandValueString() {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("AT command status cannot be null.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new IPv6RemoteATCommandResponsePacket(frameID, ipv6address, command, null, valueStr);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#IPv6RemoteATCommandResponsePacket(int, Inet6Address, String, ATCommandStatus, byte[])}.
	 * 
	 * <p>Construct a new IPv6 Remote AT command response packet but with a 
	 * {@code null} status. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateIPv6RemoteATCommandResponsePacketStatusNullCommandValueByteArray() {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("AT command status cannot be null.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new IPv6RemoteATCommandResponsePacket(frameID, ipv6address, command, null, value);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#IPv6RemoteATCommandResponsePacket(int, Inet6Address, String, ATCommandStatus, String)}.
	 * 
	 * <p>Construct a new IPv6 Remote AT command response packet with an invalid frame ID, 
	 * (bigger than 255) This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateIPv6RemoteATCommandResponsePacketInvalidFrameIDBiggerThan255CommandValueString() {
		// Setup the resources for the test.
		frameID = 2000;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new IPv6RemoteATCommandResponsePacket(frameID, ipv6address, command, status, valueStr);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#IPv6RemoteATCommandResponsePacket(int, Inet6Address, String, ATCommandStatus, byte[])}.
	 * 
	 * <p>Construct a new IPv6 Remote AT command response packet with an invalid frame ID 
	 * (bigger than 255) This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateIPv6RemoteATCommandResponsePacketInvalidFrameIDBiggerThan255CommandValueByteArray() {
		// Setup the resources for the test.
		frameID = 2000;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new IPv6RemoteATCommandResponsePacket(frameID, ipv6address, command, status, value);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#IPv6RemoteATCommandResponsePacket(int, Inet6Address, String, ATCommandStatus, String)}.
	 * 
	 * <p>Construct a new IPv6 Remote AT command response packet with an invalid frame ID
	 * (negative value) This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateIPv6RemoteATCommandResponsePacketInvalidFrameIDNegativeCommandValueString() {
		// Setup the resources for the test.
		int frameID = -4;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new IPv6RemoteATCommandResponsePacket(frameID, ipv6address, command, status, valueStr);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#IPv6RemoteATCommandResponsePacket(int, Inet6Address, String, ATCommandStatus, byte[])}.
	 * 
	 * <p>Construct a new IPv6 Remote AT command response packet with an invalid frame ID
	 * (negative value) This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateIPv6RemoteATCommandResponsePacketInvalidFrameIDNegativeCommandValueByteArray() {
		// Setup the resources for the test.
		int frameID = -4;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new IPv6RemoteATCommandResponsePacket(frameID, ipv6address, command, status, value);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#IPv6RemoteATCommandResponsePacket(int, Inet6Address, String, ATCommandStatus, String)}.
	 * 
	 * <p>Construct a new IPv6 Remote AT command response packet with valid parameters 
	 * and without command value ({@code null}).</p>
	 */
	@Test
	public final void testCreateIPv6RemoteATCommandResponsePacketValidCommandValueNull() {
		// Call the method under test.
		IPv6RemoteATCommandResponsePacket packet = new IPv6RemoteATCommandResponsePacket(frameID, ipv6address, command, 
				status, (String)null);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(21)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned source IPv6 address is not the expected one", packet.getSourceAddress(), is(equalTo(ipv6address)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(command))));
		assertThat("Returned status is not the expected one", packet.getStatus(), is(equalTo(status)));
		assertThat("Returned Command value is not the expected one", packet.getCommandValueAsString(), is(nullValue(String.class)));
		assertThat("Remote AT Command needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#IPv6RemoteATCommandResponsePacket(int, Inet6Address, String, ATCommandStatus, String)}.
	 * 
	 * <p>Construct a new IPv6 Remote AT command response packet with valid parameters 
	 * and command value as string.</p>
	 */
	@Test
	public final void testCreateIPv6RemoteATCommandResponsePacketValidCommandValueString() {
		// Call the method under test.
		IPv6RemoteATCommandResponsePacket packet = new IPv6RemoteATCommandResponsePacket(frameID, ipv6address, command, 
				status, valueStr);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(21 + valueStr.getBytes().length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned source IPv6 address is not the expected one", packet.getSourceAddress(), is(equalTo(ipv6address)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(command))));
		assertThat("Returned status is not the expected one", packet.getStatus(), is(equalTo(status)));
		assertThat("Returned Command value is not the expected one", packet.getCommandValueAsString(), is(equalTo(valueStr)));
		assertThat("Remote AT Command needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#IPv6RemoteATCommandResponsePacket(int, Inet6Address, String, ATCommandStatus, byte[])}.
	 * 
	 * <p>Construct a new IPv6 Remote AT command response packet with valid parameters 
	 * and command value as byte array.</p>
	 */
	@Test
	public final void testCreateIPv6RemoteATCommandResponsePacketValidCommandValueByteArray() {
		// Call the method under test.
		IPv6RemoteATCommandResponsePacket packet = new IPv6RemoteATCommandResponsePacket(frameID, ipv6address, command, 
				status, value);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(21 + value.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned source IPv6 address is not the expected one", packet.getSourceAddress(), is(equalTo(ipv6address)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(command))));
		assertThat("Returned status is not the expected one", packet.getStatus(), is(equalTo(status)));
		assertThat("Returned Command value is not the expected one", packet.getCommandValue(), is(equalTo(value)));
		assertThat("Remote AT Command needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#getAPIData()}.
	 * 
	 * <p>Test the get API data with a {@code null} command value.</p>
	 */
	@Test
	public final void testGetAPIDataATCommandValueNull() {
		// Setup the resources for the test.
		IPv6RemoteATCommandResponsePacket packet = new IPv6RemoteATCommandResponsePacket(frameID, ipv6address, command, 
				status, (String)null);
		
		byte[] expectedData = new byte[20];
		expectedData[0] = (byte)frameID;
		System.arraycopy(ipv6address.getAddress(), 0, expectedData, 1, ipv6address.getAddress().length);
		System.arraycopy(command.getBytes(), 0, expectedData, 17, command.getBytes().length);
		expectedData[19] = (byte)status.getId();
		
		// Call the method under test.
		byte[] data = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#getAPIData()}.
	 * 
	 * <p>Test the get API data with a non-{@code null} command value.</p>
	 */
	@Test
	public final void testGetAPIDataATCommandValueNotNull() {
		// Setup the resources for the test.
		IPv6RemoteATCommandResponsePacket packet = new IPv6RemoteATCommandResponsePacket(frameID, ipv6address, command, 
				status, value);
		
		byte[] expectedData = new byte[20 + value.length];
		expectedData[0] = (byte)frameID;
		System.arraycopy(ipv6address.getAddress(), 0, expectedData, 1, ipv6address.getAddress().length);
		System.arraycopy(command.getBytes(), 0, expectedData, 17, command.getBytes().length);
		expectedData[19] = (byte)status.getId();
		System.arraycopy(value, 0, expectedData, 20, value.length);
		
		// Call the method under test.
		byte[] data = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with a {@code null} command value.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersATCommandValueNull() {
		// Setup the resources for the test.
		String command = "NI";
		byte[] commandValue = null;
		IPv6RemoteATCommandResponsePacket packet = new IPv6RemoteATCommandResponsePacket(frameID, ipv6address, command, 
				status, commandValue);
		
		String expectedSourcetAddr = HexUtils.prettyHexString(ipv6address.getAddress()) + " (" + ipv6address.getHostAddress() + ")";
		String expectedATCommand = HexUtils.prettyHexString(command.getBytes()) + " (" + command + ")";
		String expectedStatus = HexUtils.prettyHexString(HexUtils.integerToHexString(status.getId(), 1)) + " (" + status.getDescription() + ")";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(3)));
		assertThat("Source IPv6 Address is not the expected one", packetParams.get("Source address"), is(equalTo(expectedSourcetAddr)));
		assertThat("AT Command is not the expected one", packetParams.get("AT Command"), is(equalTo(expectedATCommand)));
		assertThat("Status is not the expected one", packetParams.get("Status"), is(equalTo(expectedStatus)));
		assertThat("AT Command value is not the expected one", packetParams.get("Response"), is(nullValue(String.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with a parameter value.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersATCommandParameterString() {
		// Setup the resources for the test.
		String command = "NI";
		String commandValue = "Device name";
		IPv6RemoteATCommandResponsePacket packet = new IPv6RemoteATCommandResponsePacket(frameID, ipv6address, command, 
				status, commandValue);
		
		String expectedSourcetAddr = HexUtils.prettyHexString(ipv6address.getAddress()) + " (" + ipv6address.getHostAddress() + ")";
		String expectedATCommand = HexUtils.prettyHexString(command.getBytes()) + " (" + command + ")";
		String expectedStatus = HexUtils.prettyHexString(HexUtils.integerToHexString(status.getId(), 1)) + " (" + status.getDescription() + ")";
		String expectedATCommandValue = HexUtils.prettyHexString(commandValue.getBytes()) + " (" + commandValue + ")";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(4)));
		assertThat("Source IPv6 Address is not the expected one", packetParams.get("Source address"), is(equalTo(expectedSourcetAddr)));
		assertThat("AT Command is not the expected one", packetParams.get("AT Command"), is(equalTo(expectedATCommand)));
		assertThat("Status is not the expected one", packetParams.get("Status"), is(equalTo(expectedStatus)));
		assertThat("AT Command value is not the expected one", packetParams.get("Response"), is(equalTo(expectedATCommandValue)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with a command value.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersATCommandValueByteArray() {
		// Setup the resources for the test.
		String command = "NI";
		byte[] commandValue = new byte[]{0x6D, 0x79};
		IPv6RemoteATCommandResponsePacket packet = new IPv6RemoteATCommandResponsePacket(frameID, ipv6address, command, 
				status, commandValue);
		
		String expectedSourcetAddr = HexUtils.prettyHexString(ipv6address.getAddress()) + " (" + ipv6address.getHostAddress() + ")";
		String expectedATCommand = HexUtils.prettyHexString(command.getBytes()) + " (" + command + ")";
		String expectedStatus = HexUtils.prettyHexString(HexUtils.integerToHexString(status.getId(), 1)) + " (" + status.getDescription() + ")";
		String expectedATCommandValue = HexUtils.prettyHexString(commandValue) + " (" + new String(commandValue) + ")";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(4)));
		assertThat("Source IPv6 Address is not the expected one", packetParams.get("Source address"), is(equalTo(expectedSourcetAddr)));
		assertThat("AT Command is not the expected one", packetParams.get("AT Command"), is(equalTo(expectedATCommand)));
		assertThat("Status is not the expected one", packetParams.get("Status"), is(equalTo(expectedStatus)));
		assertThat("AT Command value is not the expected one", packetParams.get("Response"), is(equalTo(expectedATCommandValue)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with command value.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersATCommandValueByteArrayNonStringCmd() {
		// Setup the resources for the test.
		String command = "DL";
		byte[] commandValue = new byte[]{0x6D, 0x79};
		IPv6RemoteATCommandResponsePacket packet = new IPv6RemoteATCommandResponsePacket(frameID, ipv6address, command, 
				status, commandValue);
		
		String expectedSourcetAddr = HexUtils.prettyHexString(ipv6address.getAddress()) + " (" + ipv6address.getHostAddress() + ")";
		String expectedATCommand = HexUtils.prettyHexString(command.getBytes()) + " (" + command + ")";
		String expectedStatus = HexUtils.prettyHexString(HexUtils.integerToHexString(status.getId(), 1)) + " (" + status.getDescription() + ")";
		String expectedATCommandValue = HexUtils.prettyHexString(commandValue);
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(4)));
		assertThat("Source IPv6 Address is not the expected one", packetParams.get("Source address"), is(equalTo(expectedSourcetAddr)));
		assertThat("AT Command is not the expected one", packetParams.get("AT Command"), is(equalTo(expectedATCommand)));
		assertThat("Status is not the expected one", packetParams.get("Status"), is(equalTo(expectedStatus)));
		assertThat("AT Command value is not the expected one", packetParams.get("Response"), is(equalTo(expectedATCommandValue)));
	}
	
	/**
	 * Test method for @link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#isBroadcast()}.
	 * 
	 * <p>Test if an IPv6 Remote AT command response packet is a broadcast packet. This must 
	 * throw a {@code UnsupportedOperationException}.</p>
	 */
	@SuppressWarnings("deprecation")
	@Test
	public final void testIsBroadcast() {
		// Setup the resources for the test.
		IPv6RemoteATCommandResponsePacket packet = new IPv6RemoteATCommandResponsePacket(frameID, ipv6address, command, 
				status, new byte[0]);
		
		exception.expect(UnsupportedOperationException.class);
		
		// Call the method under test and verify the result.
		packet.isBroadcast();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#setCommandValue(String)}.
	 * 
	 * <p>Test if a string command value is properly configured.</p>
	 */
	@Test
	public final void testSetCommandValueString() {
		// Setup the resources for the test.
		IPv6RemoteATCommandResponsePacket packet = new IPv6RemoteATCommandResponsePacket(frameID, ipv6address, command, 
				status, new byte[0]);
		
		// Call the method under test.
		packet.setCommandValue(valueStr);
		
		// Verify the result.
		assertThat("Configured command value must be '" + valueStr + "'", 
				packet.getCommandValueAsString(), is(equalTo(valueStr)));
		assertThat("Configured command value must be '" + valueStr + "'", 
				packet.getCommandValue(), is(equalTo(valueStr.getBytes())));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#setCommandValue(String)}.
	 * 
	 * <p>Test if a string command value with {@code null} value is properly 
	 * configured.</p>
	 */
	@Test
	public final void testSetCommandValueStringNull() {
		IPv6RemoteATCommandResponsePacket packet = new IPv6RemoteATCommandResponsePacket(frameID, ipv6address, command, 
				status, new byte[0]);
		
		// Call the method under test.
		packet.setCommandValue((String)null);
		
		// Verify the result.
		assertThat("Configured command value must be 'null'", 
				packet.getCommandValueAsString(), is(equalTo(null)));
		assertThat("Configured command value must be 'null'", 
				packet.getCommandValue(), is(equalTo(null)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#setCommandValue(byte[])}.
	 * 
	 * <p>Test if a byte array command value is properly configured.</p>
	 */
	@Test
	public final void testSetCommandValueByteArray() {
		// Setup the resources for the test.
		byte[] parameterToSet = new byte[]{0x6D, 0x79, 0x44, 0x65, 0x76, 0x69, 0x63, 0x65};
		IPv6RemoteATCommandResponsePacket packet = new IPv6RemoteATCommandResponsePacket(frameID, ipv6address, command, 
				status, new byte[0]);
		
		// Call the method under test.
		packet.setCommandValue(value);
		
		// Verify the result.
		assertThat("Configured command value must be '" + new String(parameterToSet) + "'", 
				packet.getCommandValue(), is(equalTo(value)));
		assertThat("Configured command value must be '" + parameterToSet + "'", 
				packet.getCommandValueAsString(), is(equalTo(new String(value))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#setCommandValue(byte[])}.
	 * 
	 * <p>Test if a byte array command value with {@code null} value is properly 
	 * configured.</p>
	 */
	@Test
	public final void testSetCommandValueByteArrayNull() {
		IPv6RemoteATCommandResponsePacket packet = new IPv6RemoteATCommandResponsePacket(frameID, ipv6address, command, 
				status, new byte[0]);
		
		// Call the method under test.
		packet.setCommandValue((byte[])null);
		
		// Verify the result.
		assertThat("Configured command value must be 'null'", packet.getCommandValue(), is(equalTo(null)));
		assertThat("Configured command value must be 'null'", packet.getCommandValueAsString(), is(equalTo(null)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#getCommandValueAsString()}.
	 * 
	 * <p>Test if a configured command value is properly returned.</p>
	 */
	@Test
	public final void testGetCommandValueAsString() {
		IPv6RemoteATCommandResponsePacket packet = new IPv6RemoteATCommandResponsePacket(frameID, ipv6address, command, 
				status, valueStr);
		
		// Call the method under test.
		String commandValue = packet.getCommandValueAsString();
		
		// Verify the result.
		assertThat("Returned command value must be '" + valueStr + "'", commandValue, is(equalTo(valueStr)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#getCommandValueAsString()}.
	 * 
	 * <p>Test if a configured command value is properly returned.</p>
	 */
	@Test
	public final void testGetCommandValueAsStringNullValue() {
		IPv6RemoteATCommandResponsePacket packet = new IPv6RemoteATCommandResponsePacket(frameID, ipv6address, command, 
				status, (String)null);
		
		// Call the method under test.
		String commandValue = packet.getCommandValueAsString();
		
		// Verify the result.
		assertThat("Returned command value must be 'null'", commandValue, is(equalTo(null)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#getCommandValue()}.
	 * 
	 * <p>Test if a configured command value is properly returned.</p>
	 */
	@Test
	public final void testGetCommandValue() {
		IPv6RemoteATCommandResponsePacket packet = new IPv6RemoteATCommandResponsePacket(frameID, ipv6address, command, 
				status, value);
		
		// Call the method under test.
		byte[] commandValue = packet.getCommandValue();
		
		// Verify the result.
		assertThat("Returned command value must be '" + HexUtils.prettyHexString(value) + "'", commandValue, is(equalTo(value)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket#getCommandValue()}.
	 * 
	 * <p>Test if a configured command value is properly returned.</p>
	 */
	@Test
	public final void testGetGetCommandValueNullValue() {
		IPv6RemoteATCommandResponsePacket packet = new IPv6RemoteATCommandResponsePacket(frameID, ipv6address, command, 
				status, (byte[])null);
		
		// Call the method under test.
		byte[] commandValue = packet.getCommandValue();
		
		// Verify the result.
		assertThat("Returned command value must be 'null'", commandValue, is(equalTo(null)));
	}
}
