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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.*;

import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedHashMap;

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

import com.digi.xbee.api.models.IPProtocol;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.thread.TXIPv6Packet;
import com.digi.xbee.api.utils.HexUtils;

@PrepareForTest({Inet6Address.class, TXIPv6Packet.class})
@RunWith(PowerMockRunner.class)
public class TXIPv6PacketTest {

	// Constants.
	private static final String IPV6_ADDRESS = "FDB3:0001:0002:0000:0004:0005:0006:0007";

	// Variables.
	private int frameType = APIFrameType.TX_IPV6.getValue();
	private int frameID = 0x01;
	private Inet6Address destAddress;
	private int destPort = 0x0025;
	private int sourcePort = 0x00B3;
	private IPProtocol protocol = IPProtocol.TCP;
	private int transmitOptions = 0x00;
	private byte[] data = "Test".getBytes();

	@Rule
	public ExpectedException exception = ExpectedException.none();

	public TXIPv6PacketTest() throws Exception {
		destAddress = (Inet6Address) Inet6Address.getByName(IPV6_ADDRESS);
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
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#createPacket(byte[])}.
	 *
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Set up the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("TX IPv6 packet payload cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		TXIPv6Packet.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Set up the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete TX IPv6 packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		TXIPv6Packet.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Set up the resources for the test.
		byte[] payload = new byte[23];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(destAddress.getAddress(), 0, payload, 2, destAddress.getAddress().length);
		payload[18] = (byte)(destPort >> 8);
		payload[19] = (byte)destPort;
		payload[20] = (byte)(sourcePort >> 8);
		payload[21] = (byte)sourcePort;
		payload[22] = (byte)protocol.getID();

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete TX IPv6 packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		TXIPv6Packet.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array not including the Frame type.</p>
	 */
	@Test
	public final void testCreatePacketPayloadNotIncludingFrameType() {
		// Set up the resources for the test.
		byte[] payload = new byte[22 + data.length];
		payload[0] = (byte)frameID;
		System.arraycopy(destAddress.getAddress(), 0, payload, 1, destAddress.getAddress().length);
		payload[17] = (byte)(destPort >> 8);
		payload[18] = (byte)destPort;
		payload[19] = (byte)(sourcePort >> 8);
		payload[20] = (byte)sourcePort;
		payload[21] = (byte)protocol.getID();
		System.arraycopy(data, 0, payload, 22, data.length);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not a TX IPv6 packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		TXIPv6Packet.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array with an invalid IP address.</p>
	 */
	@Test
	public final void testCreatePacketPayloadInvalidIP() throws Exception {
		// Set up the resources for the test.
		byte[] payload = new byte[24];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(destAddress.getAddress(), 0, payload, 2, destAddress.getAddress().length);
		payload[18] = (byte)(destPort >> 8);
		payload[19] = (byte)destPort;
		payload[20] = (byte)(sourcePort >> 8);
		payload[21] = (byte)sourcePort;
		payload[22] = (byte)protocol.getID();
		payload[23] = (byte)transmitOptions;

		PowerMockito.mockStatic(Inet6Address.class);
		PowerMockito.when(Inet6Address.getByAddress(Mockito.any(byte[].class))).thenThrow(new UnknownHostException());

		exception.expect(IllegalArgumentException.class);

		// Call the method under test that should throw an IllegalArgumentException.
		TXIPv6Packet.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#createPacket(byte[])}.
	 *
	 * <p>A valid API TX IPv6 packet with the provided options without data
	 * is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutData() {
		// Set up the resources for the test.
		byte[] payload = new byte[24];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(destAddress.getAddress(), 0, payload, 2, destAddress.getAddress().length);
		payload[18] = (byte)(destPort >> 8);
		payload[19] = (byte)destPort;
		payload[20] = (byte)(sourcePort >> 8);
		payload[21] = (byte)sourcePort;
		payload[22] = (byte)protocol.getID();
		payload[23] = (byte)transmitOptions;

		// Call the method under test.
		TXIPv6Packet packet = TXIPv6Packet.createPacket(payload);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned dest address is not the expected one", packet.getDestAddress(), is(equalTo(destAddress)));
		assertThat("Returned dest port is not the expected one", packet.getDestPort(), is(equalTo(destPort)));
		assertThat("Returned source port is not the expected one", packet.getDestPort(), is(equalTo(destPort)));
		assertThat("Returned protocol is not the expected one", packet.getProtocol(), is(equalTo(protocol)));
		assertThat("Returned options is not the expected one", packet.getTransmitOptions(), is(equalTo(transmitOptions)));
		assertThat("Returned data is not the expected one", packet.getData(), is(nullValue()));

		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#createPacket(byte[])}.
	 *
	 * <p>A valid API TX IPv6 packet with the provided options and data is
	 * created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithData() {
		// Set up the resources for the test.
		byte[] payload = new byte[24 + data.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(destAddress.getAddress(), 0, payload, 2, destAddress.getAddress().length);
		payload[18] = (byte)(destPort >> 8);
		payload[19] = (byte)destPort;
		payload[20] = (byte)(sourcePort >> 8);
		payload[21] = (byte)sourcePort;
		payload[22] = (byte)protocol.getID();
		payload[23] = (byte)transmitOptions;
		System.arraycopy(data, 0, payload, 24, data.length);

		// Call the method under test.
		TXIPv6Packet packet = TXIPv6Packet.createPacket(payload);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned dest address is not the expected one", packet.getDestAddress(), is(equalTo(destAddress)));
		assertThat("Returned dest port is not the expected one", packet.getDestPort(), is(equalTo(destPort)));
		assertThat("Returned source port is not the expected one", packet.getDestPort(), is(equalTo(destPort)));
		assertThat("Returned protocol is not the expected one", packet.getProtocol(), is(equalTo(protocol)));
		assertThat("Returned options is not the expected one", packet.getTransmitOptions(), is(equalTo(transmitOptions)));
		assertThat("Returned data is not the expected one", packet.getData(), is(equalTo(data)));

		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#TXIPv6Packet(int, Inet6Address, int, int, IPProtocol, int, byte[])}.
	 *
	 * <p>Construct a new TX IPv6 packet with a frame ID bigger than 255.
	 * This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateTXIPv6PacketFrameIDBiggerThan255() {
		// Set up the resources for the test.
		frameID = 524;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#TXIPv6Packet(int, Inet6Address, int, int, IPProtocol, int, byte[])}.
	 *
	 * <p>Construct a new TX IPv6 packet with a negative frame ID. This
	 * must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateTXIPv6PacketFrameIDNegative() {
		// Set up the resources for the test.
		frameID = -6;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#TXIPv6Packet(int, Inet6Address, int, int, IPProtocol, int, byte[])}.
	 *
	 * <p>Construct a new TX IPv6 packet with a null destination address. This
	 * must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateTXIPv6PacketDestAddressNull() {
		// Set up the resources for the test.
		destAddress = null;

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Destination address cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#TXIPv6Packet(int, Inet6Address, int, int, IPProtocol, int, byte[])}.
	 *
	 * <p>Construct a new TX IPv6 packet with a negative destination port. This
	 * must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateTXIPv6PacketDestPortNegative() {
		// Set up the resources for the test.
		destPort = -6;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Port must be between 0 and 65535.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#TXIPv6Packet(int, Inet6Address, int, int, IPProtocol, int, byte[])}.
	 *
	 * <p>Construct a new TX IPv6 packet with a destination port bigger than
	 * 65535. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateTXIPv6PacketDestPortBigger() {
		// Set up the resources for the test.
		destPort = 66200;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Port must be between 0 and 65535.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#TXIPv6Packet(int, Inet6Address, int, int, IPProtocol, int, byte[])}.
	 *
	 * <p>Construct a new TX IPv6 packet with a negative source port. This
	 * must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateTXIPv6PacketSourcePortNegative() {
		// Set up the resources for the test.
		sourcePort = -6;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Port must be between 0 and 65535.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#TXIPv6Packet(int, Inet6Address, int, int, IPProtocol, int, byte[])}.
	 *
	 * <p>Construct a new TX IPv6 packet with a source port bigger than
	 * 65535. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateTXIPv6PacketSourcePortBigger() {
		// Set up the resources for the test.
		sourcePort = 66200;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Port must be between 0 and 65535.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#TXIPv6Packet(int, Inet6Address, int, int, IPProtocol, int, byte[])}.
	 *
	 * <p>Construct a new TX IPv6 packet with a null protocol. This must throw
	 * a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateTXIPv6PacketProtocolNull() {
		// Set up the resources for the test.
		protocol = null;

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Protocol cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#TXIPv6Packet(int, Inet6Address, int, int, IPProtocol, int, byte[])}.
	 *
	 * <p>Construct a new TX IPv6 packet without data ({@code null}).</p>
	 */
	@Test
	public final void testCreateTXIPv6PacketValidDataNull() {
		// Set up the resources for the test.
		data = null;

		int expectedLength = 24;

		// Call the method under test.
		TXIPv6Packet packet = new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned dest address is not the expected one", packet.getDestAddress(), is(equalTo(destAddress)));
		assertThat("Returned dest port is not the expected one", packet.getDestPort(), is(equalTo(destPort)));
		assertThat("Returned source port is not the expected one", packet.getDestPort(), is(equalTo(destPort)));
		assertThat("Returned protocol is not the expected one", packet.getProtocol(), is(equalTo(protocol)));
		assertThat("Returned options is not the expected one", packet.getTransmitOptions(), is(equalTo(transmitOptions)));
		assertThat("Returned data is not the expected one", packet.getData(), is(nullValue()));
		assertThat("TX IPv6 packet needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#TXIPv6Packet(int, Inet6Address, int, int, IPProtocol, int, byte[])}.
	 *
	 * <p>Construct a new TX IPv6 packet with data.</p>
	 */
	@Test
	public final void testCreateTXIPv6PacketValidDataNotNull() {
		// Set up the resources for the test.
		int expectedLength = 24 + data.length;

		// Call the method under test.
		TXIPv6Packet packet = new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned dest address is not the expected one", packet.getDestAddress(), is(equalTo(destAddress)));
		assertThat("Returned dest port is not the expected one", packet.getDestPort(), is(equalTo(destPort)));
		assertThat("Returned source port is not the expected one", packet.getDestPort(), is(equalTo(destPort)));
		assertThat("Returned protocol is not the expected one", packet.getProtocol(), is(equalTo(protocol)));
		assertThat("Returned options is not the expected one", packet.getTransmitOptions(), is(equalTo(transmitOptions)));
		assertThat("Returned data is not the expected one", packet.getData(), is(data));
		assertThat("TX IPv6 packet needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#getAPIData()}.
	 *
	 * <p>Test the get API parameters with a {@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIDataReceivedDataNull() {
		// Set up the resources for the test.
		data = null;

		TXIPv6Packet packet = new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);

		int expectedLength = 23;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		System.arraycopy(destAddress.getAddress(), 0, expectedData, 1, destAddress.getAddress().length);
		expectedData[17] = (byte)(destPort >> 8);
		expectedData[18] = (byte)destPort;
		expectedData[19] = (byte)(sourcePort >> 8);
		expectedData[20] = (byte)sourcePort;
		expectedData[21] = (byte)protocol.getID();
		expectedData[22] = (byte)transmitOptions;

		// Call the method under test.
		byte[] apiData = packet.getAPIData();

		// Verify the result.
		assertThat("API data is not the expected", apiData, is(equalTo(expectedData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#getAPIData()}.
	 *
	 * <p>Test the get API parameters with a not-{@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIDataReceivedDataNotNull() {
		// Set up the resources for the test.
		TXIPv6Packet packet = new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);

		int expectedLength = 23 + data.length;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		System.arraycopy(destAddress.getAddress(), 0, expectedData, 1, destAddress.getAddress().length);
		expectedData[17] = (byte)(destPort >> 8);
		expectedData[18] = (byte)destPort;
		expectedData[19] = (byte)(sourcePort >> 8);
		expectedData[20] = (byte)sourcePort;
		expectedData[21] = (byte)protocol.getID();
		expectedData[22] = (byte)transmitOptions;
		System.arraycopy(data, 0, expectedData, 23, data.length);

		// Call the method under test.
		byte[] apiData = packet.getAPIData();

		// Verify the result.
		assertThat("API data is not the expected", apiData, is(equalTo(expectedData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#getAPIPacketParameters()}.
	 *
	 * <p>Test the get API parameters but with a {@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersReceivedDataNull() {
		// Set up the resources for the test.
		data = null;

		TXIPv6Packet packet = new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);

		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();

		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(5)));
		assertThat("Returned dest address is not the expected one", packetParams.get("Destination address"), 
				is(equalTo(HexUtils.prettyHexString(destAddress.getAddress()) + " (" + destAddress.getHostAddress() + ")")));
		assertThat("Returned dest port is not the expected one", packetParams.get("Destination port"), 
				is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(destPort, 2)) + " (" + destPort + ")")));
		assertThat("Returned source port is not the expected one", packetParams.get("Source port"), 
				is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(sourcePort, 2)) + " (" + sourcePort + ")")));
		assertThat("Returned protocol is not the expected one", packetParams.get("Protocol"), 
				is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(protocol.getID(), 1)) + " (" + protocol.getName() + ")")));
		assertThat("Returned options is not the expected one", packetParams.get("Transmit options"), 
				is(equalTo(new String(HexUtils.prettyHexString(HexUtils.integerToHexString(transmitOptions, 1))) + " (Reserved)")));
		assertThat("Data is not the expected", packetParams.get("Data"), is(nullValue(String.class)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#getAPIPacketParameters()}.
	 *
	 * <p>Test the get API parameters but with a not-{@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersReceivedDataNotNull() {
		// Set up the resources for the test.
		TXIPv6Packet packet = new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);

		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();

		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(6)));
		assertThat("Returned dest address is not the expected one", packetParams.get("Destination address"), 
				is(equalTo(HexUtils.prettyHexString(destAddress.getAddress()) + " (" + destAddress.getHostAddress() + ")")));
		assertThat("Returned dest port is not the expected one", packetParams.get("Destination port"), 
				is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(destPort, 2)) + " (" + destPort + ")")));
		assertThat("Returned source port is not the expected one", packetParams.get("Source port"), 
				is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(sourcePort, 2)) + " (" + sourcePort + ")")));
		assertThat("Returned protocol is not the expected one", packetParams.get("Protocol"), 
				is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(protocol.getID(), 1)) + " (" + protocol.getName() + ")")));
		assertThat("Returned options is not the expected one", packetParams.get("Transmit options"), 
				is(equalTo(new String(HexUtils.prettyHexString(HexUtils.integerToHexString(transmitOptions, 1))) + " (Reserved)")));
		assertThat("Data is not the expected", packetParams.get("Data"), 
				is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(data)))));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#setDestAddress(Inet6Address)}.
	 */
	@Test
	public final void testSetDestAddressNull() {
		// Set up the resources for the test.
		TXIPv6Packet packet = new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Destination address cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		packet.setDestAddress(null);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#setDestAddress(Inet6Address)}.
	 *
	 * @throws Exception
	 */
	@Test
	public final void testSetDestAddressNotNull() throws Exception {
		// Set up the resources for the test.
		TXIPv6Packet packet = new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);

		Inet6Address newAddress = (Inet6Address) Inet6Address.getByName("fd8a:cb11:ad71:0000:7662:c401:5efe:dc41");

		// Call the method under test.
		packet.setDestAddress(newAddress);

		// Verify the result.
		assertThat("Dest address is not the expected one", packet.getDestAddress(), is(equalTo(newAddress)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#setDestPort(int)}.
	 */
	@Test
	public final void testSetDestPortNegative() {
		// Set up the resources for the test.
		TXIPv6Packet packet = new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Port must be between 0 and 65535.")));

		// Call the method under test that should throw an IllegalArgumentException.
		packet.setDestPort(-1);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#setDestPort(int)}.
	 */
	@Test
	public final void testSetDestPortBigger() {
		// Set up the resources for the test.
		TXIPv6Packet packet = new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Port must be between 0 and 65535.")));

		// Call the method under test that should throw an IllegalArgumentException.
		packet.setDestPort(65536);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#setDestPort(int)}.
	 */
	@Test
	public final void testSetDestPortValid() {
		// Set up the resources for the test.
		TXIPv6Packet packet = new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);

		int newPort = 0;

		// Call the method under test.
		packet.setDestPort(newPort);

		// Verify the result.
		assertThat("Dest port is not the expected one", packet.getDestPort(), is(equalTo(newPort)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#setSourcePort(int)}.
	 */
	@Test
	public final void testSetSourcePortNegative() {
		// Set up the resources for the test.
		TXIPv6Packet packet = new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Port must be between 0 and 65535.")));

		// Call the method under test that should throw an IllegalArgumentException.
		packet.setSourcePort(-1);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#setSourcePort(int)}.
	 */
	@Test
	public final void testSetSourcePortBigger() {
		// Set up the resources for the test.
		TXIPv6Packet packet = new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Port must be between 0 and 65535.")));

		// Call the method under test that should throw an IllegalArgumentException.
		packet.setSourcePort(65536);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#setSourcePort(int)}.
	 */
	@Test
	public final void testSetSourcePortValid() {
		// Set up the resources for the test.
		TXIPv6Packet packet = new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);

		int newPort = 65535;

		// Call the method under test.
		packet.setSourcePort(newPort);

		// Verify the result.
		assertThat("Source port is not the expected one", packet.getSourcePort(), is(equalTo(newPort)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#setProtocol(IPProtocol)}.
	 */
	@Test
	public final void testSetProtocolNull() {
		// Set up the resources for the test.
		TXIPv6Packet packet = new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Protocol cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		packet.setProtocol(null);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#setProtocol(IPProtocol)}.
	 */
	@Test
	public final void testSetProtocolNotNull() {
		// Set up the resources for the test.
		TXIPv6Packet packet = new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);

		IPProtocol newProtocol = IPProtocol.TCP;

		// Call the method under test.
		packet.setProtocol(newProtocol);

		// Verify the result.
		assertThat("Protocol is not the expected one", packet.getProtocol(), is(equalTo(newProtocol)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#getData()}.
	 */
	@Test
	public final void testGetDataNullData() {
		// Set up the resources for the test.
		data = null;

		TXIPv6Packet packet = new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);

		// Call the method under test.
		byte[] result = packet.getData();

		// Verify the result.
		assertThat("Data must be the same", result, is(equalTo(data)));
		assertThat("Data must be null", result, is(nullValue(byte[].class)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#getData()}.
	 */
	@Test
	public final void testGetDataValidData() {
		// Set up the resources for the test.
		TXIPv6Packet packet = new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);

		// Call the method under test.
		byte[] result = packet.getData();

		// Verify the result.
		assertThat("Data must be the same", result, is(equalTo(data)));
		assertThat("Data must not be the same object", result.hashCode(), is(not(equalTo(data.hashCode()))));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#setData(byte[])}.
	 */
	@Test
	public final void testSetDataNullData() {
		// Set up the resources for the test.
		byte[] newData = null;

		TXIPv6Packet packet = new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);

		// Call the method under test.
		packet.setData(newData);

		byte[] result = packet.getData();

		// Verify the result.
		assertThat("Data must be the same", result, is(equalTo(newData)));
		assertThat("Data must be null", result, is(nullValue(byte[].class)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#setData(byte[])}.
	 */
	@Test
	public final void testSetDataValidData() {
		// Set up the resources for the test.
		byte[] newData = "New data".getBytes();

		TXIPv6Packet packet = new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);

		// Call the method under test.
		packet.setData(newData);

		byte[] result = packet.getData();

		// Verify the result.
		assertThat("Data must be the same", result, is(equalTo(newData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.TXIPv6Packet#setData(byte[])}.
	 */
	@Test
	public final void testSetDataAndModifyOriginal() {
		// Set up the resources for the test.
		byte[] newData = "New data".getBytes();

		TXIPv6Packet packet = new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);

		// Call the method under test.
		packet.setData(newData);

		byte[] backup = Arrays.copyOf(newData, newData.length);
		newData[0] = 0x00;

		byte[] result = packet.getData();

		// Verify the result.
		assertThat("Data must be the same as the setted data", result, is(equalTo(backup)));
		assertThat("Data must not be the current value of received data", result, is(not(equalTo(data))));
		assertThat("Data must not be the same object", result.hashCode(), is(not(equalTo(backup.hashCode()))));
		assertThat("Data must not be the same object", result.hashCode(), is(not(equalTo(data.hashCode()))));
	}

}
