/**
 * Copyright (c) 2016 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */
package com.digi.xbee.api.packet.network;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

import java.util.Arrays;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.digi.xbee.api.models.IP32BitAddress;
import com.digi.xbee.api.models.NetworkProtocol;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.network.TXIPv4Packet;
import com.digi.xbee.api.utils.HexUtils;

public class TXIPv4PacketTest {

	private int frameType = APIFrameType.TX_IPV4.getValue();
	private int frameID = 0x01;
	private IP32BitAddress destAddress = new IP32BitAddress("10.10.11.12");
	private IP32BitAddress destAddressBroadcast = IP32BitAddress.BROADCAST_ADDRESS;
	private int destPort = 0x0025;
	private int sourcePort = 0x00B3;
	private NetworkProtocol protocol = NetworkProtocol.TCP;
	private int transmitOptions = TXIPv4Packet.OPTIONS_LEAVE_SOCKET_OPEN;
	private byte[] data = "Test".getBytes();

	@Rule
	public ExpectedException exception = ExpectedException.none();

	public TXIPv4PacketTest() {
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
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#createPacket(byte[])}.
	 *
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Set up the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("TX IPv4 packet payload cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		TXIPv4Packet.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Set up the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete TX IPv4 packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		TXIPv4Packet.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Set up the resources for the test.
		byte[] payload = new byte[11];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(destAddress.getValue(), 0, payload, 2, destAddress.getValue().length);
		payload[6] = (byte)(destPort >> 8);
		payload[7] = (byte)destPort;
		payload[8] = (byte)(sourcePort >> 8);
		payload[9] = (byte)sourcePort;
		payload[10] = (byte)protocol.getID();

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete TX IPv4 packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		TXIPv4Packet.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array not including the Frame type.</p>
	 */
	@Test
	public final void testCreatePacketPayloadNotIncludingFrameType() {
		// Set up the resources for the test.
		byte[] payload = new byte[10 + data.length];
		payload[0] = (byte)frameID;
		System.arraycopy(destAddress.getValue(), 0, payload, 1, destAddress.getValue().length);
		payload[5] = (byte)(destPort >> 8);
		payload[6] = (byte)destPort;
		payload[7] = (byte)(sourcePort >> 8);
		payload[8] = (byte)sourcePort;
		payload[9] = (byte)protocol.getID();
		System.arraycopy(data, 0, payload, 9, data.length);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not a TX IPv4 packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		TXIPv4Packet.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#createPacket(byte[])}.
	 *
	 * <p>A valid API TX IPv4 packet with the provided options without data
	 * is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutData() {
		// Set up the resources for the test.
		byte[] payload = new byte[12];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(destAddress.getValue(), 0, payload, 2, destAddress.getValue().length);
		payload[6] = (byte)(destPort >> 8);
		payload[7] = (byte)destPort;
		payload[8] = (byte)(sourcePort >> 8);
		payload[9] = (byte)sourcePort;
		payload[10] = (byte)protocol.getID();
		payload[11] = (byte)transmitOptions;

		// Call the method under test.
		TXIPv4Packet packet = TXIPv4Packet.createPacket(payload);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned dest address is not the expected one", packet.getDestAddress(), is(equalTo(destAddress)));
		assertThat("Returned dest port is not the expected one", packet.getDestPort(), is(equalTo(destPort)));
		assertThat("Returned source port is not the expected one", packet.getDestPort(), is(equalTo(destPort)));
		assertThat("Returned protocol is not the expected one", packet.getProtocol(), is(equalTo(protocol)));
		assertThat("Returned options is not the expected one", packet.getTransmitOptions(), is(equalTo(transmitOptions)));
		assertThat("Returned data is not the expected one", packet.getData(), is(nullValue()));
		assertThat("Packet is broadcast", packet.isBroadcast(), is(equalTo(false)));

		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#createPacket(byte[])}.
	 *
	 * <p>A valid API TX IPv4 packet with the provided options and data is
	 * created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithData() {
		// Set up the resources for the test.
		byte[] payload = new byte[12 + data.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(destAddress.getValue(), 0, payload, 2, destAddress.getValue().length);
		payload[6] = (byte)(destPort >> 8);
		payload[7] = (byte)destPort;
		payload[8] = (byte)(sourcePort >> 8);
		payload[9] = (byte)sourcePort;
		payload[10] = (byte)protocol.getID();
		payload[11] = (byte)transmitOptions;
		System.arraycopy(data, 0, payload, 12, data.length);

		// Call the method under test.
		TXIPv4Packet packet = TXIPv4Packet.createPacket(payload);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned dest address is not the expected one", packet.getDestAddress(), is(equalTo(destAddress)));
		assertThat("Returned dest port is not the expected one", packet.getDestPort(), is(equalTo(destPort)));
		assertThat("Returned source port is not the expected one", packet.getDestPort(), is(equalTo(destPort)));
		assertThat("Returned protocol is not the expected one", packet.getProtocol(), is(equalTo(protocol)));
		assertThat("Returned options is not the expected one", packet.getTransmitOptions(), is(equalTo(transmitOptions)));
		assertThat("Returned data is not the expected one", packet.getData(), is(equalTo(data)));
		assertThat("Packet is broadcast", packet.isBroadcast(), is(equalTo(false)));

		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#createPacket(byte[])}.
	 *
	 * <p>A valid API TX IPv4 packet with the provided options and data is
	 * created (broadcast transmission).</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithDataBroadcast() {
		// Set up the resources for the test.
		byte[] payload = new byte[12 + data.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(destAddressBroadcast.getValue(), 0, payload, 2, destAddressBroadcast.getValue().length);
		payload[6] = (byte)(destPort >> 8);
		payload[7] = (byte)destPort;
		payload[8] = (byte)(sourcePort >> 8);
		payload[9] = (byte)sourcePort;
		payload[10] = (byte)protocol.getID();
		payload[11] = (byte)transmitOptions;
		System.arraycopy(data, 0, payload, 12, data.length);

		// Call the method under test.
		TXIPv4Packet packet = TXIPv4Packet.createPacket(payload);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned dest address is not the expected one", packet.getDestAddress(), is(equalTo(destAddressBroadcast)));
		assertThat("Returned dest port is not the expected one", packet.getDestPort(), is(equalTo(destPort)));
		assertThat("Returned source port is not the expected one", packet.getDestPort(), is(equalTo(destPort)));
		assertThat("Returned protocol is not the expected one", packet.getProtocol(), is(equalTo(protocol)));
		assertThat("Returned options is not the expected one", packet.getTransmitOptions(), is(equalTo(transmitOptions)));
		assertThat("Returned data is not the expected one", packet.getData(), is(equalTo(data)));
		assertThat("Packet is not broadcast", packet.isBroadcast(), is(equalTo(true)));

		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#TXIPv4Packet(int, IP32BitAddress, int, int, NetworkProtocol, int, byte[])}.
	 *
	 * <p>Construct a new TX IPv4 packet with a frame ID bigger than 255.
	 * This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateTXIPv4PacketFrameIDBiggerThan255() {
		// Set up the resources for the test.
		frameID = 524;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#TXIPv4Packet(int, IP32BitAddress, int, int, NetworkProtocol, int, byte[])}.
	 *
	 * <p>Construct a new TX IPv4 packet with a negative frame ID. This
	 * must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateTXIPv4PacketFrameIDNegative() {
		// Set up the resources for the test.
		frameID = -6;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#TXIPv4Packet(int, IP32BitAddress, int, int, NetworkProtocol, int, byte[])}.
	 *
	 * <p>Construct a new TX IPv4 packet with a null destination address. This
	 * must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateTXIPv4PacketDestAddressNull() {
		// Set up the resources for the test.
		destAddress = null;

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Destination address cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#TXIPv4Packet(int, IP32BitAddress, int, int, NetworkProtocol, int, byte[])}.
	 *
	 * <p>Construct a new TX IPv4 packet with a negative destination port. This
	 * must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateTXIPv4PacketDestPortNegative() {
		// Set up the resources for the test.
		destPort = -6;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Port must be between 0 and 65535.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#TXIPv4Packet(int, IP32BitAddress, int, int, NetworkProtocol, int, byte[])}.
	 *
	 * <p>Construct a new TX IPv4 packet with a destination port bigger than
	 * 65535. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateTXIPv4PacketDestPortBigger() {
		// Set up the resources for the test.
		destPort = 66200;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Port must be between 0 and 65535.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#TXIPv4Packet(int, IP32BitAddress, int, int, NetworkProtocol, int, byte[])}.
	 *
	 * <p>Construct a new TX IPv4 packet with a negative source port. This
	 * must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateTXIPv4PacketSourcePortNegative() {
		// Set up the resources for the test.
		sourcePort = -6;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Port must be between 0 and 65535.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#TXIPv4Packet(int, IP32BitAddress, int, int, NetworkProtocol, int, byte[])}.
	 *
	 * <p>Construct a new TX IPv4 packet with a source port bigger than
	 * 65535. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateTXIPv4PacketSourcePortBigger() {
		// Set up the resources for the test.
		sourcePort = 66200;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Port must be between 0 and 65535.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#TXIPv4Packet(int, IP32BitAddress, int, int, NetworkProtocol, int, byte[])}.
	 *
	 * <p>Construct a new TX IPv4 packet with illegal transmit options. This
	 * must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateTXIPv4PacketTransmitOptionsIllegal() {
		// Set up the resources for the test.
		transmitOptions = 20;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Transmit options can only be 1 or 0.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#TXIPv4Packet(int, IP32BitAddress, int, int, NetworkProtocol, int, byte[])}.
	 *
	 * <p>Construct a new TX IPv4 packet with a null protocol. This must throw
	 * a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateTXIPv4PacketProtocolNull() {
		// Set up the resources for the test.
		protocol = null;

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Protocol cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#TXIPv4Packet(int, IP32BitAddress, int, int, NetworkProtocol, int, byte[])}.
	 *
	 * <p>Construct a new TX IPv4 packet with an illegal options. This must
	 * throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateTXIPv4PacketOptionsIllegal() {
		// Set up the resources for the test.
		transmitOptions = 4;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Transmit options can only be 1 or 0.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#TXIPv4Packet(int, IP32BitAddress, int, int, NetworkProtocol, int, byte[])}.
	 *
	 * <p>Construct a new TX IPv4 packet without data ({@code null}).</p>
	 */
	@Test
	public final void testCreateTXIPv4PacketValidDataNull() {
		// Set up the resources for the test.
		data = null;

		int expectedLength = 12;

		// Call the method under test.
		TXIPv4Packet packet = new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned dest address is not the expected one", packet.getDestAddress(), is(equalTo(destAddress)));
		assertThat("Returned dest port is not the expected one", packet.getDestPort(), is(equalTo(destPort)));
		assertThat("Returned source port is not the expected one", packet.getDestPort(), is(equalTo(destPort)));
		assertThat("Returned protocol is not the expected one", packet.getProtocol(), is(equalTo(protocol)));
		assertThat("Returned options is not the expected one", packet.getTransmitOptions(), is(equalTo(transmitOptions)));
		assertThat("Returned data is not the expected one", packet.getData(), is(nullValue()));
		assertThat("TX IPv4 packet needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
		assertThat("Packet is broadcast", packet.isBroadcast(), is(equalTo(false)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#TXIPv4Packet(int, IP32BitAddress, int, int, NetworkProtocol, int, byte[])}.
	 *
	 * <p>Construct a new TX IPv4 packet with data.</p>
	 */
	@Test
	public final void testCreateTXIPv4PacketValidDataNotNull() {
		// Set up the resources for the test.
		int expectedLength = 12 + data.length;
		transmitOptions = TXIPv4Packet.OPTIONS_CLOSE_SOCKET;

		// Call the method under test.
		TXIPv4Packet packet = new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned dest address is not the expected one", packet.getDestAddress(), is(equalTo(destAddress)));
		assertThat("Returned dest port is not the expected one", packet.getDestPort(), is(equalTo(destPort)));
		assertThat("Returned source port is not the expected one", packet.getDestPort(), is(equalTo(destPort)));
		assertThat("Returned protocol is not the expected one", packet.getProtocol(), is(equalTo(protocol)));
		assertThat("Returned options is not the expected one", packet.getTransmitOptions(), is(equalTo(transmitOptions)));
		assertThat("Returned data is not the expected one", packet.getData(), is(data));
		assertThat("TX IPv4 packet needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
		assertThat("Packet is broadcast", packet.isBroadcast(), is(equalTo(false)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#TXIPv4Packet(int, IP32BitAddress, int, int, NetworkProtocol, int, byte[])}.
	 *
	 * <p>Construct a new TX IPv4 packet with data (broadcast transmission).</p>
	 */
	@Test
	public final void testCreateTXIPv4PacketValidDataNotNullBroadcast() {
		// Set up the resources for the test.
		int expectedLength = 12 + data.length;

		// Call the method under test.
		TXIPv4Packet packet = new TXIPv4Packet(frameID, destAddressBroadcast, destPort, sourcePort, protocol, transmitOptions, data);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned dest address is not the expected one", packet.getDestAddress(), is(equalTo(destAddressBroadcast)));
		assertThat("Returned dest port is not the expected one", packet.getDestPort(), is(equalTo(destPort)));
		assertThat("Returned source port is not the expected one", packet.getDestPort(), is(equalTo(destPort)));
		assertThat("Returned protocol is not the expected one", packet.getProtocol(), is(equalTo(protocol)));
		assertThat("Returned options is not the expected one", packet.getTransmitOptions(), is(equalTo(transmitOptions)));
		assertThat("Returned data is not the expected one", packet.getData(), is(data));
		assertThat("TX IPv4 packet needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
		assertThat("Packet is not broadcast", packet.isBroadcast(), is(equalTo(true)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#getAPIData()}.
	 *
	 * <p>Test the get API parameters with a {@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIDataReceivedDataNull() {
		// Set up the resources for the test.
		data = null;

		TXIPv4Packet packet = new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);

		int expectedLength = 11;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		System.arraycopy(destAddress.getValue(), 0, expectedData, 1, destAddress.getValue().length);
		expectedData[5] = (byte)(destPort >> 8);
		expectedData[6] = (byte)destPort;
		expectedData[7] = (byte)(sourcePort >> 8);
		expectedData[8] = (byte)sourcePort;
		expectedData[9] = (byte)protocol.getID();
		expectedData[10] = (byte)transmitOptions;

		// Call the method under test.
		byte[] apiData = packet.getAPIData();

		// Verify the result.
		assertThat("API data is not the expected", apiData, is(equalTo(expectedData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#getAPIData()}.
	 *
	 * <p>Test the get API parameters with a not-{@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIDataReceivedDataNotNull() {
		// Set up the resources for the test.
		TXIPv4Packet packet = new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);

		int expectedLength = 11 + data.length;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		System.arraycopy(destAddress.getValue(), 0, expectedData, 1, destAddress.getValue().length);
		expectedData[5] = (byte)(destPort >> 8);
		expectedData[6] = (byte)destPort;
		expectedData[7] = (byte)(sourcePort >> 8);
		expectedData[8] = (byte)sourcePort;
		expectedData[9] = (byte)protocol.getID();
		expectedData[10] = (byte)transmitOptions;
		System.arraycopy(data, 0, expectedData, 11, data.length);

		// Call the method under test.
		byte[] apiData = packet.getAPIData();

		// Verify the result.
		assertThat("API data is not the expected", apiData, is(equalTo(expectedData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#getAPIPacketParameters()}.
	 *
	 * <p>Test the get API parameters but with a {@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersReceivedDataNull() {
		// Set up the resources for the test.
		data = null;

		TXIPv4Packet packet = new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);

		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();

		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(5)));
		assertThat("Returned dest address is not the expected one", packetParams.get("Destination address"), is(equalTo(HexUtils.prettyHexString(destAddress.getValue()) + " (" + destAddress.toString() + ")")));
		assertThat("Returned dest port is not the expected one", packetParams.get("Destination port"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(destPort, 2)) + " (" + destPort + ")")));
		assertThat("Returned source port is not the expected one", packetParams.get("Source port"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(sourcePort, 2)) + " (" + sourcePort + ")")));
		assertThat("Returned protocol is not the expected one", packetParams.get("Protocol"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(protocol.getID(), 1)) + " (" + protocol.getName() + ")")));
		assertThat("Returned options is not the expected one", packetParams.get("Transmit options"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(transmitOptions, 1)))));
		assertThat("Data is not the expected", packetParams.get("Data"), is(nullValue(String.class)));
		assertThat("Packet is broadcast", packet.isBroadcast(), is(equalTo(false)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#getAPIPacketParameters()}.
	 *
	 * <p>Test the get API parameters but with a not-{@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersReceivedDataNotNull() {
		// Set up the resources for the test.
		TXIPv4Packet packet = new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);

		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();

		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(6)));
		assertThat("Returned dest address is not the expected one", packetParams.get("Destination address"), is(equalTo(HexUtils.prettyHexString(destAddress.getValue()) + " (" + destAddress.toString() + ")")));
		assertThat("Returned dest port is not the expected one", packetParams.get("Destination port"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(destPort, 2)) + " (" + destPort + ")")));
		assertThat("Returned source port is not the expected one", packetParams.get("Source port"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(sourcePort, 2)) + " (" + sourcePort + ")")));
		assertThat("Returned protocol is not the expected one", packetParams.get("Protocol"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(protocol.getID(), 1)) + " (" + protocol.getName() + ")")));
		assertThat("Returned options is not the expected one", packetParams.get("Transmit options"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(transmitOptions, 1)))));
		assertThat("Data is not the expected", packetParams.get("Data"), is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(data)))));
		assertThat("Packet is broadcast", packet.isBroadcast(), is(equalTo(false)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#setDestAddress(IP32BitAddress))}.
	 */
	@Test
	public final void testSetDestAddressNull() {
		// Set up the resources for the test.
		TXIPv4Packet packet = new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Destination address cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		packet.setDestAddress(null);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#setDestAddress(IP32BitAddress))}.
	 */
	@Test
	public final void testSetDestAddressNotNull() {
		// Set up the resources for the test.
		TXIPv4Packet packet = new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);

		IP32BitAddress newAddress = new IP32BitAddress("192.168.1.30");

		// Call the method under test.
		packet.setDestAddress(newAddress);

		// Verify the result.
		assertThat("Dest address is not the expected one", packet.getDestAddress(), is(equalTo(newAddress)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#setDestPort(int))}.
	 */
	@Test
	public final void testSetDestPortNegative() {
		// Set up the resources for the test.
		TXIPv4Packet packet = new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Port must be between 0 and 65535.")));

		// Call the method under test that should throw an IllegalArgumentException.
		packet.setDestPort(-1);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#setDestPort(int))}.
	 */
	@Test
	public final void testSetDestPortBigger() {
		// Set up the resources for the test.
		TXIPv4Packet packet = new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Port must be between 0 and 65535.")));

		// Call the method under test that should throw an IllegalArgumentException.
		packet.setDestPort(65536);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#setDestPort(int))}.
	 */
	@Test
	public final void testSetDestPortValid() {
		// Set up the resources for the test.
		TXIPv4Packet packet = new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);

		int newPort = 0;

		// Call the method under test.
		packet.setDestPort(newPort);

		// Verify the result.
		assertThat("Dest port is not the expected one", packet.getDestPort(), is(equalTo(newPort)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#setSourcePort(int))}.
	 */
	@Test
	public final void testSetSourcePortNegative() {
		// Set up the resources for the test.
		TXIPv4Packet packet = new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Port must be between 0 and 65535.")));

		// Call the method under test that should throw an IllegalArgumentException.
		packet.setSourcePort(-1);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#setSourcePort(int))}.
	 */
	@Test
	public final void testSetSourcePortBigger() {
		// Set up the resources for the test.
		TXIPv4Packet packet = new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Port must be between 0 and 65535.")));

		// Call the method under test that should throw an IllegalArgumentException.
		packet.setSourcePort(65536);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#setSourcePort(int))}.
	 */
	@Test
	public final void testSetSourcePortValid() {
		// Set up the resources for the test.
		TXIPv4Packet packet = new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);

		int newPort = 65535;

		// Call the method under test.
		packet.setSourcePort(newPort);

		// Verify the result.
		assertThat("Source port is not the expected one", packet.getSourcePort(), is(equalTo(newPort)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#setProtocol(NetworkProtocol))}.
	 */
	@Test
	public final void testSetProtocolNull() {
		// Set up the resources for the test.
		TXIPv4Packet packet = new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Protocol cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		packet.setProtocol(null);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#setProtocol(NetworkProtocol))}.
	 */
	@Test
	public final void testSetProtocolNotNull() {
		// Set up the resources for the test.
		TXIPv4Packet packet = new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);

		NetworkProtocol newProtocol = NetworkProtocol.TCP_SSL;

		// Call the method under test.
		packet.setProtocol(newProtocol);

		// Verify the result.
		assertThat("Protocol is not the expected one", packet.getProtocol(), is(equalTo(newProtocol)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#setTransmitOptions(int))}.
	 */
	@Test
	public final void testSetTransmitOptionsIllegal() {
		// Set up the resources for the test.
		TXIPv4Packet packet = new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Transmit options can only be 1 or 0.")));

		// Call the method under test that should throw an IllegalArgumentException.
		packet.setTransmitOptions(5);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#setTransmitOptions(int))}.
	 */
	@Test
	public final void testSetTransmitOptionsValid() {
		// Set up the resources for the test.
		TXIPv4Packet packet = new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);

		int newTransmitOptions = TXIPv4Packet.OPTIONS_CLOSE_SOCKET;

		// Call the method under test.
		packet.setTransmitOptions(newTransmitOptions);

		// Verify the result.
		assertThat("Transmit options are not the expected one", packet.getTransmitOptions(), is(equalTo(newTransmitOptions)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#setTransmitOptions(int))}.
	 */
	@Test
	public final void testSetTransmitOptionsValid2() {
		// Set up the resources for the test.
		TXIPv4Packet packet = new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);

		int newTransmitOptions = TXIPv4Packet.OPTIONS_LEAVE_SOCKET_OPEN;

		// Call the method under test.
		packet.setTransmitOptions(newTransmitOptions);

		// Verify the result.
		assertThat("Transmit options are not the expected one", packet.getTransmitOptions(), is(equalTo(newTransmitOptions)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#getData())}.
	 */
	@Test
	public final void testGetDataNullData() {
		// Set up the resources for the test.
		data = null;

		TXIPv4Packet packet = new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);

		// Call the method under test.
		byte[] result = packet.getData();

		// Verify the result.
		assertThat("Data must be the same", result, is(equalTo(data)));
		assertThat("Data must be null", result, is(nullValue(byte[].class)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#getData())}.
	 */
	@Test
	public final void testGetDataValidData() {
		// Set up the resources for the test.
		TXIPv4Packet packet = new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);

		// Call the method under test.
		byte[] result = packet.getData();

		// Verify the result.
		assertThat("Data must be the same", result, is(equalTo(data)));
		assertThat("Data must not be the same object", result.hashCode(), is(not(equalTo(data.hashCode()))));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#setData(byte[])}.
	 */
	@Test
	public final void testSetDataNullData() {
		// Set up the resources for the test.
		byte[] newData = null;

		TXIPv4Packet packet = new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);

		// Call the method under test.
		packet.setData(newData);

		byte[] result = packet.getData();

		// Verify the result.
		assertThat("Data must be the same", result, is(equalTo(newData)));
		assertThat("Data must be null", result, is(nullValue(byte[].class)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#setData(byte[])}.
	 */
	@Test
	public final void testSetDataValidData() {
		// Set up the resources for the test.
		byte[] newData = "New data".getBytes();

		TXIPv4Packet packet = new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);

		// Call the method under test.
		packet.setData(newData);

		byte[] result = packet.getData();

		// Verify the result.
		assertThat("Data must be the same", result, is(equalTo(newData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.TXIPv4Packet#setData(byte[])}.
	 */
	@Test
	public final void testSetDataAndModifyOriginal() {
		// Set up the resources for the test.
		byte[] newData = "New data".getBytes();

		TXIPv4Packet packet = new TXIPv4Packet(frameID, destAddress, destPort, sourcePort, protocol, transmitOptions, data);

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
