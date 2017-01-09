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

import com.digi.xbee.api.IPDevice;
import com.digi.xbee.api.models.ATStringCommands;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

@PrepareForTest({Inet4Address.class, RemoteATCommandWifiPacket.class})
@RunWith(PowerMockRunner.class)
public class RemoteATCommandWifiPacketTest {

	// Constants.
	private static final String IP_ADDRESS = "10.10.11.12";

	// Variables.
	private int frameType = APIFrameType.REMOTE_AT_COMMAND_REQUEST_WIFI.getValue();
	private int frameID = 0xE7;
	private Inet4Address destAddress;
	private int transmitOptions = 0x06;
	private String command = "BD";
	private byte[] parameter = new byte[]{0x00, 0x6F, 0x6C, 0x61};

	@Rule
	public ExpectedException exception = ExpectedException.none();

	public RemoteATCommandWifiPacketTest() throws Exception {
		destAddress = (Inet4Address) Inet4Address.getByName(IP_ADDRESS);
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
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandWifiPacket#createPacket(byte[])}.
	 *
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Setup the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Remote AT Command Request (Wi-Fi) packet payload cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		RemoteATCommandWifiPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandWifiPacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Setup the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Remote AT Command Request (Wi-Fi) packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		RemoteATCommandWifiPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandWifiPacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Setup the resources for the test.
		byte[] payload = new byte[11];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(new byte[]{0x00, 0x00, 0x00, 0x00}, 0, payload, 2, 4);
		System.arraycopy(destAddress.getAddress(), 0, payload, 6, destAddress.getAddress().length);
		payload[10] = (byte)transmitOptions;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Remote AT Command Request (Wi-Fi) packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		RemoteATCommandWifiPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandWifiPacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array not including the Frame type.</p>
	 */
	@Test
	public final void testCreatePacketPayloadNotIncludingFrameType() {
		// Setup the resources for the test.
		byte[] payload = new byte[12 + parameter.length];
		payload[0] = (byte)frameID;
		System.arraycopy(new byte[]{0x00, 0x00, 0x00, 0x00}, 0, payload, 1, 4);
		System.arraycopy(destAddress.getAddress(), 0, payload, 5, destAddress.getAddress().length);
		payload[9] = (byte)transmitOptions;
		System.arraycopy(command.getBytes(), 0, payload, 10, command.getBytes().length);
		System.arraycopy(parameter, 0, payload, 12, parameter.length);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not a Remote AT Command Request (Wi-Fi) packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		RemoteATCommandWifiPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.RemoteATCommandWifiPacket#createPacket(byte[])}.
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
		System.arraycopy(destAddress.getAddress(), 0, payload, 6, destAddress.getAddress().length);
		payload[10] = (byte)transmitOptions;
		System.arraycopy(command.getBytes(), 0, payload, 11, command.getBytes().length);

		PowerMockito.mockStatic(Inet4Address.class);
		PowerMockito.when(Inet4Address.getByAddress(Mockito.any(byte[].class))).thenThrow(new UnknownHostException());

		exception.expect(IllegalArgumentException.class);

		// Call the method under test that should throw an IllegalArgumentException.
		RemoteATCommandWifiPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandWifiPacket#createPacket(byte[])}.
	 *
	 * <p>A valid Remote AT Command Request (Wi-Fi) packet with the provided options without
	 * data is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutData() {
		// Setup the resources for the test.
		byte[] payload = new byte[13];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(new byte[]{0x00, 0x00, 0x00, 0x00}, 0, payload, 2, 4);
		System.arraycopy(destAddress.getAddress(), 0, payload, 6, destAddress.getAddress().length);
		payload[10] = (byte)transmitOptions;
		System.arraycopy(command.getBytes(), 0, payload, 11, command.getBytes().length);

		// Call the method under test.
		RemoteATCommandWifiPacket packet = RemoteATCommandWifiPacket.createPacket(payload);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination address is not the expected one", packet.getDestinationAddress(), is(equalTo(destAddress)));
		assertThat("Returned transmit options is not the expected one", packet.getTransmitOptions(), is(equalTo(transmitOptions)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(command)));
		assertThat("Returned Parameter Data is not the expected one", packet.getParameter(), is(nullValue()));

		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandWifiPacket#createPacket(byte[])}.
	 *
	 * <p>A valid Remote AT Command Request (Wi-Fi) packet with the provided options and data is
	 * created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithData() {
		// Setup the resources for the test.
		byte[] payload = new byte[13 + parameter.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(new byte[]{0x00, 0x00, 0x00, 0x00}, 0, payload, 2, 4);
		System.arraycopy(destAddress.getAddress(), 0, payload, 6, destAddress.getAddress().length);
		payload[10] = (byte)transmitOptions;
		System.arraycopy(command.getBytes(), 0, payload, 11, command.getBytes().length);
		System.arraycopy(parameter, 0, payload, 13, parameter.length);

		// Call the method under test.
		RemoteATCommandWifiPacket packet = RemoteATCommandWifiPacket.createPacket(payload);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination address is not the expected one", packet.getDestinationAddress(), is(equalTo(destAddress)));
		assertThat("Returned transmit options is not the expected one", packet.getTransmitOptions(), is(equalTo(transmitOptions)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(command)));
		assertThat("Returned Parameter Data is not the expected one", packet.getParameter(), is(equalTo(parameter)));

		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandWifiPacket#RemoteATCommandWifiPacket(int, Inet4Address, int, String, byte[])}.
	 *
	 * <p>Construct a new Remote AT Command Request (Wi-Fi) packet but with a frame ID
	 * bigger than 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandWifiPacketFrameIDBiggerThan255() {
		// Setup the resources for the test.
		frameID = 256;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new RemoteATCommandWifiPacket(frameID, destAddress, transmitOptions, command, parameter);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandWifiPacket#RemoteATCommandWifiPacket(int, Inet4Address, int, String, byte[])}.
	 *
	 * <p>Construct a new Remote AT Command Request (Wi-Fi) packet but with a negative
	 * frame ID. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandWifiPacketFrameIDNegative() {
		// Setup the resources for the test.
		frameID = -1;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new RemoteATCommandWifiPacket(frameID, destAddress, transmitOptions, command, parameter);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandWifiPacket#RemoteATCommandWifiPacket(int, Inet4Address, int, String, byte[])}.
	 *
	 * <p>Construct a new Remote AT Command Request (Wi-Fi) packet but with a {@code null}
	 * destination address. This must throw an {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandWifiPacketDestAddressNull() {
		// Setup the resources for the test.
		destAddress = null;

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Destination address cannot be null.")));

		// Call the method under test that should throw an NullPointerException.
		new RemoteATCommandWifiPacket(frameID, destAddress, transmitOptions, command, parameter);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandWifiPacket#RemoteATCommandWifiPacket(int, Inet4Address, int, String, byte[])}.
	 *
	 * <p>Construct a new Remote AT Command Request (Wi-Fi) packet but with options
	 * bigger than 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandWifiPacketOptionsBiggerThan255() {
		// Setup the resources for the test.
		transmitOptions = 256;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Receive options must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new RemoteATCommandWifiPacket(frameID, destAddress, transmitOptions, command, parameter);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandWifiPacket#RemoteATCommandWifiPacket(int, Inet4Address, int, String, byte[])}.
	 *
	 * <p>Construct a new Remote AT Command Request (Wi-Fi) packet but with negative
	 * options. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandWifiPacketOptionsNegative() {
		// Setup the resources for the test.
		transmitOptions = -1;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Receive options must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new RemoteATCommandWifiPacket(frameID, destAddress, transmitOptions, command, parameter);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandWifiPacket#RemoteATCommandWifiPacket(int, Inet4Address, int, String, byte[])}.
	 *
	 * <p>Construct a new Remote AT Command Request (Wi-Fi) packet but with a {@code null}
	 * command. This must throw an {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandWifiPacketCommandNull() {
		// Setup the resources for the test.
		command = null;

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("AT command cannot be null.")));

		// Call the method under test that should throw an NullPointerException.
		new RemoteATCommandWifiPacket(frameID, destAddress, transmitOptions, command, parameter);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandWifiPacket#RemoteATCommandWifiPacket(int, Inet4Address, int, String, byte[])}.
	 *
	 * <p>Construct a new Remote AT Command Request (Wi-Fi) packet with valid parameters
	 * but without parameter value ({@code null}).</p>
	 */
	@Test
	public final void testCreateRemoteATCommandWifiPacketValidDataNull() {
		// Setup the resources for the test.
		parameter = null;

		int expectedLength = 13;

		// Call the method under test.
		RemoteATCommandWifiPacket packet = new RemoteATCommandWifiPacket(frameID, destAddress, transmitOptions, command, parameter);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination address is not the expected one", packet.getDestinationAddress(), is(equalTo(destAddress)));
		assertThat("Returned options are not the expected one", packet.getTransmitOptions(), is(equalTo(transmitOptions)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(command))));
		assertThat("Returned Command Data is not the expected one", packet.getParameter(), is(nullValue()));
		assertThat("Remote AT Command needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandWifiPacket#RemoteATCommandWifiPacket(int, Inet4Address, int, String, byte[])}.
	 *
	 * <p>Construct a new Remote AT Command Request (Wi-Fi) packet with valid parameters.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandWifiPacketValidDataNotNull() {
		// Setup the resources for the test.
		int expectedLength = 13 + parameter.length;

		// Call the method under test.
		RemoteATCommandWifiPacket packet = new RemoteATCommandWifiPacket(frameID, destAddress, transmitOptions, command, parameter);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination address is not the expected one", packet.getDestinationAddress(), is(equalTo(destAddress)));
		assertThat("Returned options are not the expected one", packet.getTransmitOptions(), is(equalTo(transmitOptions)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(command))));
		assertThat("Returned Command Data is not the expected one", packet.getParameter(), is(equalTo(parameter)));
		assertThat("Remote AT Command needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandWifiPacket#RemoteATCommandW}.
	 *
	 * <p>Construct a new Remote AT Command Request (Wi-Fi) packet with valid parameters.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandWifiPacketValidDataStringNull() {
		// Setup the resources for the test.
		int expectedLength = 13;

		// Call the method under test.
		RemoteATCommandWifiPacket packet = new RemoteATCommandWifiPacket(frameID, destAddress, transmitOptions, command, (String)null);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination address is not the expected one", packet.getDestinationAddress(), is(equalTo(destAddress)));
		assertThat("Returned options are not the expected one", packet.getTransmitOptions(), is(equalTo(transmitOptions)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(command))));
		assertThat("Returned Command Data is not the expected one", packet.getParameter(), is(nullValue()));
		assertThat("Remote AT Command needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandWifiPacket#RemoteATCommandW}.
	 *
	 * <p>Construct a new Remote AT Command Request (Wi-Fi) packet with valid parameters.</p>
	 */
	@Test
	public final void testCreateRemoteATCommandWifiPacketValidDataString() {
		// Setup the resources for the test.
		int expectedLength = 13 + parameter.length;

		// Call the method under test.
		RemoteATCommandWifiPacket packet = new RemoteATCommandWifiPacket(frameID, destAddress, transmitOptions, command, new String(parameter));

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination address is not the expected one", packet.getDestinationAddress(), is(equalTo(destAddress)));
		assertThat("Returned options are not the expected one", packet.getTransmitOptions(), is(equalTo(transmitOptions)));
		assertThat("Returned AT Command is not the expected one", packet.getCommand(), is(equalTo(new String(command))));
		assertThat("Returned Command Data is not the expected one", packet.getParameter(), is(equalTo(parameter)));
		assertThat("Remote AT Command needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandWifiPacket#getAPIData()}.
	 *
	 * <p>Test the get API parameters but with a {@code null} parameter value.</p>
	 */
	@Test
	public final void testGetAPIDataDataNull() {
		// Setup the resources for the test.
		parameter = null;

		RemoteATCommandWifiPacket packet = new RemoteATCommandWifiPacket(frameID, destAddress, transmitOptions, command, parameter);

		int expectedLength = 12;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		System.arraycopy(new byte[]{0x00, 0x00, 0x00, 0x00}, 0, expectedData, 2, 4);
		System.arraycopy(destAddress.getAddress(), 0, expectedData, 5, destAddress.getAddress().length);
		expectedData[9] = (byte)transmitOptions;
		System.arraycopy(command.getBytes(), 0, expectedData, 10, command.length());

		// Call the method under test.
		byte[] data = packet.getAPIData();

		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandWifiPacket#getAPIData()}.
	 *
	 * <p>Test the get API parameters but with a {@code null} parameter value.</p>
	 */
	@Test
	public final void testGetAPIDataDataNotNull() {
		// Setup the resources for the test.
		RemoteATCommandWifiPacket packet = new RemoteATCommandWifiPacket(frameID, destAddress, transmitOptions, command, parameter);

		int expectedLength = 12 + parameter.length;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		System.arraycopy(new byte[]{0x00, 0x00, 0x00, 0x00}, 0, expectedData, 2, 4);
		System.arraycopy(destAddress.getAddress(), 0, expectedData, 5, destAddress.getAddress().length);
		expectedData[9] = (byte)transmitOptions;
		System.arraycopy(command.getBytes(), 0, expectedData, 10, command.length());
		System.arraycopy(parameter, 0, expectedData, 12, parameter.length);

		// Call the method under test.
		byte[] data = packet.getAPIData();

		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandWifiPacket#getAPIPacketParameters()}.
	 *
	 * <p>Test the get API parameters but with a {@code null} parameter value.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersATCommandParameterNull() {
		// Setup the resources for the test.
		parameter = null;

		RemoteATCommandWifiPacket packet = new RemoteATCommandWifiPacket(frameID, destAddress, transmitOptions, command, parameter);

		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();

		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(3)));
		assertThat("Destination address is not the expected one", packetParams.get("Destination address"), is(equalTo("00 00 00 00 " + HexUtils.prettyHexString(HexUtils.byteArrayToHexString(destAddress.getAddress())) + " (" + destAddress.getHostAddress() + ")")));
		assertThat("Command options are not the expected one", packetParams.get("Transmit options"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(transmitOptions, 1)))));
		assertThat("AT Command is not the expected one", packetParams.get("AT Command"), is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(command.getBytes())) + " (" + command + ")")));
		assertThat("AT Parameter is not the expected one", packetParams.get("Parameter"), is(nullValue()));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandWifiPacket#getAPIPacketParameters()}.
	 *
	 * <p>Test the get API parameters but with a parameter value.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersATCommandParameterNotString() {
		// Setup the resources for the test.
		RemoteATCommandWifiPacket packet = new RemoteATCommandWifiPacket(frameID, destAddress, transmitOptions, command, parameter);

		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();

		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(4)));
		assertThat("Destination address is not the expected one", packetParams.get("Destination address"), is("00 00 00 00 " + HexUtils.prettyHexString(HexUtils.byteArrayToHexString(destAddress.getAddress())) + " (" + destAddress.getHostAddress() + ")"));
		assertThat("Command options are not the expected one", packetParams.get("Transmit options"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(transmitOptions, 1)))));
		assertThat("AT Command is not the expected one", packetParams.get("AT Command"), is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(command.getBytes())) + " (" + command + ")")));
		assertThat("AT Parameter is not the expected one", packetParams.get("Parameter"), is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(parameter)))));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandWifiPacket#getAPIPacketParameters()}.
	 *
	 * <p>Test the get API parameters but with a parameter value.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersATCommandParameterString() {
		// Setup the resources for the test.
		command = ATStringCommands.NI.getCommand();

		RemoteATCommandWifiPacket packet = new RemoteATCommandWifiPacket(frameID, destAddress, transmitOptions, command, parameter);

		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();

		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(4)));
		assertThat("Destination address is not the expected one", packetParams.get("Destination address"), is("00 00 00 00 " + HexUtils.prettyHexString(HexUtils.byteArrayToHexString(destAddress.getAddress())) + " (" + destAddress.getHostAddress() + ")"));
		assertThat("Command options are not the expected one", packetParams.get("Transmit options"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(transmitOptions, 1)))));
		assertThat("AT Command is not the expected one", packetParams.get("AT Command"), is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(command.getBytes())) + " (" + command + ")")));
		assertThat("AT Parameter is not the expected one", packetParams.get("Parameter"), is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(parameter)) + " (" + new String(parameter) + ")")));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandWifiPacket#isBroadcast()}.
	 *
	 * <p>Test the is broadcast method.</p>
	 */
	@Test
	public final void testIsBroadcastFalse() {
		// Set up the resources for the test.
		RemoteATCommandWifiPacket packet = new RemoteATCommandWifiPacket(frameID, destAddress, transmitOptions, command, parameter);

		// Call the method under test and verify the result.
		assertThat("Packet should not be broadcast", packet.isBroadcast(), is(equalTo(false)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandWifiPacket#isBroadcast()}.
	 *
	 * <p>Test the is broadcast method.</p>
	 *
	 * @throws Exception
	 */
	@Test
	public final void testIsBroadcastTrue() throws Exception {
		// Set up the resources for the test.
		destAddress = (Inet4Address) Inet4Address.getByName(IPDevice.BROADCAST_IP);

		RemoteATCommandWifiPacket packet = new RemoteATCommandWifiPacket(frameID, destAddress, transmitOptions, command, parameter);

		// Call the method under test and verify the result.
		assertThat("Packet should be broadcast", packet.isBroadcast(), is(equalTo(true)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandWifiPacket#setParameter(String)}.
	 */
	@Test
	public final void testSetParameterStringNull() {
		// Setup the resources for the test.
		RemoteATCommandWifiPacket packet = new RemoteATCommandWifiPacket(frameID, destAddress, transmitOptions, command, parameter);

		String param = null;

		// Call the method under test.
		packet.setParameter(param);

		// Verify the result.
		assertThat("Returned parameter is not the expected", packet.getParameter(), is(nullValue()));
		assertThat("Returned parameter is not the expected", packet.getParameterAsString(), is(nullValue()));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandWifiPacket#setParameter(String)}.
	 */
	@Test
	public final void testSetParameterStringNotNull() {
		// Setup the resources for the test.
		RemoteATCommandWifiPacket packet = new RemoteATCommandWifiPacket(frameID, destAddress, transmitOptions, command, parameter);

		String param = "new value";

		// Call the method under test.
		packet.setParameter(param);

		// Verify the result.
		assertThat("Returned parameter is not the expected", packet.getParameter(), is(equalTo(param.getBytes())));
		assertThat("Returned parameter is not the expected", packet.getParameterAsString(), is(equalTo(param)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandWifiPacket#setParameter(byte[])}.
	 */
	@Test
	public final void testSetParameterByteArrayNull() {
		// Setup the resources for the test.
		RemoteATCommandWifiPacket packet = new RemoteATCommandWifiPacket(frameID, destAddress, transmitOptions, command, parameter);

		parameter = null;

		// Call the method under test.
		packet.setParameter(parameter);

		// Verify the result.
		assertThat("Returned parameter is not the expected", packet.getParameter(), is(nullValue()));
		assertThat("Returned parameter is not the expected", packet.getParameterAsString(), is(nullValue()));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.RemoteATCommandWifiPacket#setParameter(byte[])}.
	 */
	@Test
	public final void testSetParameterByteArrayNotNull() {
		// Setup the resources for the test.
		RemoteATCommandWifiPacket packet = new RemoteATCommandWifiPacket(frameID, destAddress, transmitOptions, command, parameter);

		parameter = "value".getBytes();

		// Call the method under test.
		packet.setParameter(parameter);

		// Verify the result.
		assertThat("Returned parameter is not the expected", packet.getParameter(), is(equalTo(parameter)));
		assertThat("Returned parameter is not the expected", packet.getParameterAsString(), is(equalTo(new String(parameter))));
	}
}
