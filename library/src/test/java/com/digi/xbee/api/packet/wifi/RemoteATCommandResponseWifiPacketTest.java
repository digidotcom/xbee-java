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
package com.digi.xbee.api.packet.wifi;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.models.ATStringCommands;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

@PrepareForTest({Inet4Address.class, RemoteATCommandResponseWifiPacket.class})
@RunWith(PowerMockRunner.class)
public class RemoteATCommandResponseWifiPacketTest {

	// Constants.
	private static final String IP_ADDRESS = "10.10.11.12";

	// Variables.
	private int frameType = APIFrameType.REMOTE_AT_COMMAND_RESPONSE_WIFI.getValue();
	private int frameID = 0xE7;
	private Inet4Address sourceAddress;
	private String command = "BD";
	private ATCommandStatus status = ATCommandStatus.INVALID_PARAMETER;
	private byte[] commandValue = "123".getBytes();

	@Rule
	public ExpectedException exception = ExpectedException.none();

	public RemoteATCommandResponseWifiPacketTest() throws Exception {
		sourceAddress = (Inet4Address) Inet4Address.getByName(IP_ADDRESS);
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
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandResponseWifiPacket#createPacket(byte[])}.
	 *
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Setup the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Remote AT Command Response (Wi-Fi) packet payload cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		RemoteATCommandResponseWifiPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandResponseWifiPacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Setup the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Remote AT Command Response (Wi-Fi) packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		RemoteATCommandResponseWifiPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandResponseWifiPacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Setup the resources for the test.
		byte[] payload = new byte[12];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(new byte[]{0x00, 0x00, 0x00, 0x00}, 0, payload, 2, 4);
		System.arraycopy(sourceAddress.getAddress(), 0, payload, 6, sourceAddress.getAddress().length);
		System.arraycopy(command.getBytes(), 0, payload, 10, command.length());

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Remote AT Command Response (Wi-Fi) packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		RemoteATCommandResponseWifiPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandResponseWifiPacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array not including the Frame type.</p>
	 */
	@Test
	public final void testCreatePacketPayloadNotIncludingFrameType() {
		// Setup the resources for the test.
		byte[] payload = new byte[13];
		payload[0] = (byte)frameID;
		System.arraycopy(new byte[]{0x00, 0x00, 0x00, 0x00}, 0, payload, 1, 4);
		System.arraycopy(sourceAddress.getAddress(), 0, payload, 5, sourceAddress.getAddress().length);
		System.arraycopy(command.getBytes(), 0, payload, 9, command.length());
		payload[11] = (byte)status.getId();
		payload[12] = (byte)status.getId();

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not a Remote AT Command Response (Wi-Fi) packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		RemoteATCommandResponseWifiPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.RemoteATCommandResponseWifiPacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array with an invalid IP address.</p>
	 */
	@Test
	public final void testCreatePacketPayloadInvalidIP() throws Exception {
		// Setup the resources for the test.
		byte[] payload = new byte[13];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(new byte[]{0x00, 0x00, 0x00, 0x00}, 0, payload, 2, 4);
		System.arraycopy(sourceAddress.getAddress(), 0, payload, 6, sourceAddress.getAddress().length);
		System.arraycopy(command.getBytes(), 0, payload, 10, command.length());
		payload[12] = (byte)status.getId();

		PowerMockito.mockStatic(Inet4Address.class);
		PowerMockito.when(Inet4Address.getByAddress(Mockito.any(byte[].class))).thenThrow(new UnknownHostException());

		exception.expect(IllegalArgumentException.class);

		// Call the method under test that should throw an IllegalArgumentException.
		RemoteATCommandResponseWifiPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandResponseWifiPacket#createPacket(byte[])}.
	 *
	 * <p>A valid Remote AT Command Response (Wi-Fi) packet with the provided options without
	 * data is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutData() {
		// Setup the resources for the test.
		byte[] payload = new byte[13];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(new byte[]{0x00, 0x00, 0x00, 0x00}, 0, payload, 2, 4);
		System.arraycopy(sourceAddress.getAddress(), 0, payload, 6, sourceAddress.getAddress().length);
		System.arraycopy(command.getBytes(), 0, payload, 10, command.length());
		payload[12] = (byte)status.getId();

		// Call the method under test.
		RemoteATCommandResponseWifiPacket packet = RemoteATCommandResponseWifiPacket.createPacket(payload);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned source address is not the expected one", packet.getSourceAddress(), is(equalTo(sourceAddress)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(command)));
		assertThat("Returned status is not the expected one", packet.getStatus(), is(equalTo(status)));
		assertThat("Returned Parameter Data is not the expected one", packet.getCommandValue(), is(nullValue()));
		assertThat("Returned Parameter Data is not the expected one", packet.getCommandValueAsString(), is(nullValue()));

		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandResponseWifiPacket#createPacket(byte[])}.
	 *
	 * <p>A valid Remote AT Command Response (Wi-Fi) packet with the provided options.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithData() {
		// Setup the resources for the test.
		byte[] payload = new byte[13 + commandValue.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(new byte[]{0x00, 0x00, 0x00, 0x00}, 0, payload, 2, 4);
		System.arraycopy(sourceAddress.getAddress(), 0, payload, 6, sourceAddress.getAddress().length);
		System.arraycopy(command.getBytes(), 0, payload, 10, command.length());
		payload[12] = (byte)status.getId();
		System.arraycopy(commandValue, 0, payload, 13, commandValue.length);

		// Call the method under test.
		RemoteATCommandResponseWifiPacket packet = RemoteATCommandResponseWifiPacket.createPacket(payload);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned source address is not the expected one", packet.getSourceAddress(), is(equalTo(sourceAddress)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(command)));
		assertThat("Returned status is not the expected one", packet.getStatus(), is(equalTo(status)));
		assertThat("Returned Parameter Data is not the expected one", packet.getCommandValue(), is(equalTo(commandValue)));
		assertThat("Returned Parameter Data is not the expected one", packet.getCommandValueAsString(), is(equalTo(new String(commandValue))));

		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandResponseWifiPacket#RemoteATCommandResponseWifiPacket(int, Inet4Address, String, ATCommandStatus, byte[])}.
	 *
	 * <p>Construct a new Remote AT Command Response (Wi-Fi) packet but with a frame ID
	 * bigger than 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandResponseWifiPacketFrameIDBiggerThan255() {
		// Setup the resources for the test.
		frameID = 256;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new RemoteATCommandResponseWifiPacket(frameID, sourceAddress, command, status, commandValue);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandResponseWifiPacket#RemoteATCommandResponseWifiPacket(int, Inet4Address, String, ATCommandStatus, byte[])}.
	 *
	 * <p>Construct a new Remote AT Command Response (Wi-Fi) packet but with a negative
	 * frame ID. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandResponseWifiPacketFrameIDNegative() {
		// Setup the resources for the test.
		frameID = -1;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new RemoteATCommandResponseWifiPacket(frameID, sourceAddress, command, status, commandValue);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandResponseWifiPacket#RemoteATCommandResponseWifiPacket(int, Inet4Address, String, ATCommandStatus, byte[])}.
	 *
	 * <p>Construct a new Remote AT Command Response (Wi-Fi) packet but with a {@code null}
	 * source address. This must throw an {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandResponseWifiPacketSourceAddressNull() {
		// Setup the resources for the test.
		sourceAddress = null;

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Source address cannot be null.")));

		// Call the method under test that should throw an NullPointerException.
		new RemoteATCommandResponseWifiPacket(frameID, sourceAddress, command, status, commandValue);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandResponseWifiPacket#RemoteATCommandResponseWifiPacket(int, Inet4Address, String, ATCommandStatus, byte[])}.
	 *
	 * <p>Construct a new Remote AT Command Response (Wi-Fi) packet but with a {@code null}
	 * command. This must throw an {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandResponseWifiPacketCommandNull() {
		// Setup the resources for the test.
		command = null;

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("AT command cannot be null.")));

		// Call the method under test that should throw an NullPointerException.
		new RemoteATCommandResponseWifiPacket(frameID, sourceAddress, command, status, commandValue);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandResponseWifiPacket#RemoteATCommandResponseWifiPacket(int, Inet4Address, String, ATCommandStatus, byte[])}.
	 *
	 * <p>Construct a new Remote AT Command Response (Wi-Fi) packet but with a {@code null}
	 * status. This must throw an {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandResponseWifiPacketStatusNull() {
		// Setup the resources for the test.
		status = null;

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("AT command status cannot be null.")));

		// Call the method under test that should throw an NullPointerException.
		new RemoteATCommandResponseWifiPacket(frameID, sourceAddress, command, status, commandValue);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandResponseWifiPacket#RemoteATCommandResponseWifiPacket(int, Inet4Address, String, ATCommandStatus, byte[])}.
	 *
	 * <p>Construct a new Remote AT Command Response (Wi-Fi) packet with valid parameters
	 * but without parameter value ({@code null}).</p>
	 */
	@Test
	public final void testCreateRemoteATCommandResponseWifiPacketValidDataNull() {
		// Setup the resources for the test.
		commandValue = null;

		int expectedLength = 13;

		// Call the method under test.
		RemoteATCommandResponseWifiPacket packet = new RemoteATCommandResponseWifiPacket(frameID, sourceAddress, command, status, commandValue);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned source address is not the expected one", packet.getSourceAddress(), is(equalTo(sourceAddress)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(command)));
		assertThat("Returned status is not the expected one", packet.getStatus(), is(equalTo(status)));
		assertThat("Returned Parameter Data is not the expected one", packet.getCommandValue(), is(nullValue()));
		assertThat("Returned Parameter Data is not the expected one", packet.getCommandValueAsString(), is(nullValue()));
		assertThat("Remote AT Command needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandResponseWifiPacket#RemoteATCommandResponseWifiPacket(int, Inet4Address, String, ATCommandStatus, byte[])}.
	 *
	 * <p>Construct a new Remote AT Command Response (Wi-Fi) packet with valid parameters
	 * but without parameter value ({@code null}).</p>
	 */
	@Test
	public final void testCreateRemoteATCommandResponseWifiPacketValidDataNotNull() {
		// Setup the resources for the test.
		int expectedLength = 16;

		// Call the method under test.
		RemoteATCommandResponseWifiPacket packet = new RemoteATCommandResponseWifiPacket(frameID, sourceAddress, command, status, commandValue);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned source address is not the expected one", packet.getSourceAddress(), is(equalTo(sourceAddress)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(command)));
		assertThat("Returned status is not the expected one", packet.getStatus(), is(equalTo(status)));
		assertThat("Returned Parameter Data is not the expected one", packet.getCommandValue(), is(equalTo(commandValue)));
		assertThat("Returned Parameter Data is not the expected one", packet.getCommandValueAsString(), is(equalTo(new String(commandValue))));
		assertThat("Remote AT Command needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandResponseWifiPacket#getAPIData()}.
	 *
	 * <p>Test the get API parameters but with a {@code null} parameter value.</p>
	 */
	@Test
	public final void testGetAPIDataDataNull() {
		// Setup the resources for the test.
		commandValue = null;

		RemoteATCommandResponseWifiPacket packet = new RemoteATCommandResponseWifiPacket(frameID, sourceAddress, command, status, commandValue);

		int expectedLength = 12;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		System.arraycopy(new byte[]{0x00, 0x00, 0x00, 0x00}, 0, expectedData, 1, 4);
		System.arraycopy(sourceAddress.getAddress(), 0, expectedData, 5, sourceAddress.getAddress().length);
		System.arraycopy(command.getBytes(), 0, expectedData, 9, command.length());
		expectedData[11] = (byte)status.getId();

		// Call the method under test.
		byte[] data = packet.getAPIData();

		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandResponseWifiPacket#getAPIData()}.
	 *
	 * <p>Test the get API parameters.</p>
	 */
	@Test
	public final void testGetAPIDataDataNotNull() {
		// Setup the resources for the test.
		RemoteATCommandResponseWifiPacket packet = new RemoteATCommandResponseWifiPacket(frameID, sourceAddress, command, status, commandValue);

		int expectedLength = 12 + commandValue.length;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		System.arraycopy(new byte[]{0x00, 0x00, 0x00, 0x00}, 0, expectedData, 1, 4);
		System.arraycopy(sourceAddress.getAddress(), 0, expectedData, 5, sourceAddress.getAddress().length);
		System.arraycopy(command.getBytes(), 0, expectedData, 9, command.length());
		expectedData[11] = (byte)status.getId();
		System.arraycopy(commandValue, 0, expectedData, 12, commandValue.length);

		// Call the method under test.
		byte[] data = packet.getAPIData();

		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandResponseWifiPacket#getAPIPacketParameters()}.
	 *
	 * <p>Test the get API parameters but with a {@code null} parameter value.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersATCommandParameterNull() {
		// Setup the resources for the test.
		commandValue = null;

		RemoteATCommandResponseWifiPacket packet = new RemoteATCommandResponseWifiPacket(frameID, sourceAddress, command, status, commandValue);

		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();

		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(3)));
		assertThat("Source address is not the expected one", packetParams.get("Source address"), is(equalTo("00 00 00 00 " + HexUtils.prettyHexString(HexUtils.byteArrayToHexString(sourceAddress.getAddress())) + " (" + sourceAddress.getHostAddress() + ")")));
		assertThat("AT Command is not the expected one", packetParams.get("AT Command"), is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(command.getBytes())) + " (" + command + ")")));
		assertThat("AT Parameter is not the expected one", packetParams.get("Status"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(status.getId(), 1)) + " (" + status.getDescription() + ")")));
		assertThat("AT Parameter is not the expected one", packetParams.get("Response"), is(nullValue()));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandResponseWifiPacket#getAPIPacketParameters()}.
	 *
	 * <p>Test the get API parameters but with a parameter value.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersATCommandParameterNotString() {
		// Setup the resources for the test.
		RemoteATCommandResponseWifiPacket packet = new RemoteATCommandResponseWifiPacket(frameID, sourceAddress, command, status, commandValue);

		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();

		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(4)));
		assertThat("Source address is not the expected one", packetParams.get("Source address"), is(equalTo("00 00 00 00 " + HexUtils.prettyHexString(HexUtils.byteArrayToHexString(sourceAddress.getAddress())) + " (" + sourceAddress.getHostAddress() + ")")));
		assertThat("AT Command is not the expected one", packetParams.get("AT Command"), is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(command.getBytes())) + " (" + command + ")")));
		assertThat("AT Parameter is not the expected one", packetParams.get("Status"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(status.getId(), 1)) + " (" + status.getDescription() + ")")));
		assertThat("AT Parameter is not the expected one", packetParams.get("Response"), is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(commandValue)))));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandResponseWifiPacket#getAPIPacketParameters()}.
	 *
	 * <p>Test the get API parameters but with a parameter value.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersATCommandParameterString() {
		// Setup the resources for the test.
		command = ATStringCommands.KY.getCommand();

		RemoteATCommandResponseWifiPacket packet = new RemoteATCommandResponseWifiPacket(frameID, sourceAddress, command, status, commandValue);

		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();

		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(4)));
		assertThat("Source address is not the expected one", packetParams.get("Source address"), is(equalTo("00 00 00 00 " + HexUtils.prettyHexString(HexUtils.byteArrayToHexString(sourceAddress.getAddress())) + " (" + sourceAddress.getHostAddress() + ")")));
		assertThat("AT Command is not the expected one", packetParams.get("AT Command"), is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(command.getBytes())) + " (" + command + ")")));
		assertThat("AT Parameter is not the expected one", packetParams.get("Status"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(status.getId(), 1)) + " (" + status.getDescription() + ")")));
		assertThat("AT Parameter is not the expected one", packetParams.get("Response"), is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(commandValue)) + " (" + new String(commandValue) + ")")));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandResponseWifiPacket#isBroadcast()}.
	 *
	 * <p>Test the is broadcast method.</p>
	 */
	@Test
	public final void testIsBroadcast() {
		// Set up the resources for the test.
		RemoteATCommandResponseWifiPacket packet = new RemoteATCommandResponseWifiPacket(frameID, sourceAddress, command, status, commandValue);

		// Call the method under test and verify the result.
		assertThat("Packet should not be broadcast", packet.isBroadcast(), is(equalTo(false)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandResponseWifiPacket#setCommandValue(String)}.
	 */
	@Test
	public final void testSetCommandValueStringNull() {
		// Setup the resources for the test.
		RemoteATCommandResponseWifiPacket packet = new RemoteATCommandResponseWifiPacket(frameID, sourceAddress, command, status, commandValue);

		String value = null;

		// Call the method under test.
		packet.setCommandValue(value);

		// Verify the result.
		assertThat("Returned parameter is not the expected", packet.getCommandValue(), is(nullValue()));
		assertThat("Returned parameter is not the expected", packet.getCommandValueAsString(), is(nullValue()));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandResponseWifiPacket#setCommandValue(String)}.
	 */
	@Test
	public final void testSetCommandValueStringNotNull() {
		// Setup the resources for the test.
		RemoteATCommandResponseWifiPacket packet = new RemoteATCommandResponseWifiPacket(frameID, sourceAddress, command, status, commandValue);

		String value = "new value";

		// Call the method under test.
		packet.setCommandValue(value);

		// Verify the result.
		assertThat("Returned parameter is not the expected", packet.getCommandValue(), is(equalTo(value.getBytes())));
		assertThat("Returned parameter is not the expected", packet.getCommandValueAsString(), is(equalTo(value)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandResponseWifiPacket#setParameter(byte[])}.
	 */
	@Test
	public final void testSetCommandValueByteArrayNull() {
		// Setup the resources for the test.
		RemoteATCommandResponseWifiPacket packet = new RemoteATCommandResponseWifiPacket(frameID, sourceAddress, command, status, commandValue);

		commandValue = null;

		// Call the method under test.
		packet.setCommandValue(commandValue);

		// Verify the result.
		assertThat("Returned parameter is not the expected", packet.getCommandValue(), is(nullValue()));
		assertThat("Returned parameter is not the expected", packet.getCommandValueAsString(), is(nullValue()));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandResponseWifiPacket#setParameter(byte[])}.
	 */
	@Test
	public final void testSetCommandValueByteArrayNotNull() {
		// Setup the resources for the test.
		RemoteATCommandResponseWifiPacket packet = new RemoteATCommandResponseWifiPacket(frameID, sourceAddress, command, status, commandValue);

		commandValue = "value".getBytes();

		// Call the method under test.
		packet.setCommandValue(commandValue);

		// Verify the result.
		assertThat("Returned parameter is not the expected", packet.getCommandValue(), is(equalTo(commandValue)));
		assertThat("Returned parameter is not the expected", packet.getCommandValueAsString(), is(equalTo(new String(commandValue))));
	}
}
