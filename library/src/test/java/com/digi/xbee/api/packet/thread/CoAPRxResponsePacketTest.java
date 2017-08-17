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
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

import java.net.Inet6Address;
import java.net.UnknownHostException;
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
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.digi.xbee.api.models.IPProtocol;
import com.digi.xbee.api.models.RestFulStatusEnum;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.thread.CoAPRxResponsePacket;
import com.digi.xbee.api.utils.HexUtils;
import com.digi.xbee.api.utils.ByteUtils;

@PrepareForTest({Inet6Address.class, CoAPRxResponsePacket.class})
@RunWith(PowerMockRunner.class)
public class CoAPRxResponsePacketTest {

	// Constants.
	private static final String IPV6_SRC_ADDRESS = "FDB3:0001:0002:0000:0004:0005:0006:0007";
	private static final String IPV6_DST_ADDRESS = "FDB3:0001:0002:0000:0004:0005:0006:0007";

	// Variables.
	private int frameType = APIFrameType.COAP_RX_RESPONSE.getValue();
	private int frameID = 0x01;
	private Inet6Address sourceAddress;
	private Inet6Address destAddress;
	private int destPort = 0x0025;
	private int sourcePort = 0x00B3;
	private IPProtocol protocol = IPProtocol.TCP;
	private RestFulStatusEnum restFulStatus = RestFulStatusEnum.ACCEPTED;
	private byte[] data = "Test".getBytes();
	private byte[] destPortBytes = ByteUtils.shortToByteArray((short)destPort);
	private byte[] sourcePortBytes = ByteUtils.shortToByteArray((short)sourcePort);
	private byte[] restFulStatusBytes = ByteUtils.shortToByteArray((short)restFulStatus.getID());

	@Rule
	public ExpectedException exception = ExpectedException.none();

	public CoAPRxResponsePacketTest() throws Exception {
		sourceAddress = (Inet6Address) Inet6Address.getByName(IPV6_SRC_ADDRESS);
		destAddress = (Inet6Address) Inet6Address.getByName(IPV6_DST_ADDRESS);
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
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#createPacket(byte[])}.
	 *
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Set up the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("CoAP Rx Response packet payload cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		CoAPRxResponsePacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Set up the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete CoAP Rx Response packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		CoAPRxResponsePacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Set up the resources for the test.
		byte[] payload = new byte[40];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(destAddress.getAddress(), 0, payload, 2, destAddress.getAddress().length);
		System.arraycopy(sourceAddress.getAddress(), 0, payload, 18, sourceAddress.getAddress().length);
		System.arraycopy(destPortBytes, 0, payload, 34, destPortBytes.length);
		System.arraycopy(sourcePortBytes, 0, payload, 36, sourcePortBytes.length);
		payload[38] = (byte)protocol.getID();
		// Do not copy the complete RESTFul status
		System.arraycopy(restFulStatusBytes, 0, payload, 39, restFulStatusBytes.length - 1);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete CoAP Rx Response packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		CoAPRxResponsePacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array not including the Frame type.</p>
	 */
	@Test
	public final void testCreatePacketPayloadNotIncludingFrameType() {
		// Set up the resources for the test.
		byte[] payload = new byte[40 + data.length];
		payload[0] = (byte)frameID;
		System.arraycopy(destAddress.getAddress(), 0, payload, 1, destAddress.getAddress().length);
		System.arraycopy(sourceAddress.getAddress(), 0, payload, 17, sourceAddress.getAddress().length);
		System.arraycopy(destPortBytes, 0, payload, 33, destPortBytes.length);
		System.arraycopy(sourcePortBytes, 0, payload, 35, sourcePortBytes.length);
		payload[37] = (byte)protocol.getID();
		System.arraycopy(restFulStatusBytes, 0, payload, 38, restFulStatusBytes.length);
		System.arraycopy(data, 0, payload, 40, data.length);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not a CoAP Rx Response packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		CoAPRxResponsePacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array with an invalid IPv6 address.</p>
	 */
	@Test
	public final void testCreatePacketPayloadInvalidIP() throws Exception {
		// Set up the resources for the test.
		byte[] payload = new byte[41];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(destAddress.getAddress(), 0, payload, 2, destAddress.getAddress().length);
		System.arraycopy(sourceAddress.getAddress(), 0, payload, 18, sourceAddress.getAddress().length);
		System.arraycopy(destPortBytes, 0, payload, 34, destPortBytes.length);
		System.arraycopy(sourcePortBytes, 0, payload, 36, sourcePortBytes.length);
		payload[38] = (byte)protocol.getID();
		System.arraycopy(restFulStatusBytes, 0, payload, 39, restFulStatusBytes.length);

		PowerMockito.mockStatic(Inet6Address.class);
		PowerMockito.when(Inet6Address.getByAddress(Mockito.any(byte[].class))).thenThrow(new UnknownHostException());

		exception.expect(IllegalArgumentException.class);

		// Call the method under test that should throw an IllegalArgumentException.
		CoAPRxResponsePacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#createPacket(byte[])}.
	 *
	 * <p>A valid CoAP Rx Response packet with the provided options and 
	 * without data is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutData() {
		// Set up the resources for the test.
		byte[] payload = new byte[41];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(destAddress.getAddress(), 0, payload, 2, destAddress.getAddress().length);
		System.arraycopy(sourceAddress.getAddress(), 0, payload, 18, sourceAddress.getAddress().length);
		System.arraycopy(destPortBytes, 0, payload, 34, destPortBytes.length);
		System.arraycopy(sourcePortBytes, 0, payload, 36, sourcePortBytes.length);
		payload[38] = (byte)protocol.getID();
		System.arraycopy(restFulStatusBytes, 0, payload, 39, restFulStatusBytes.length);

		// Call the method under test.
		CoAPRxResponsePacket packet = CoAPRxResponsePacket.createPacket(payload);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned dest address is not the expected one", packet.getDestAddress(), is(equalTo(destAddress)));
		assertThat("Returned source address is not the expected one", packet.getSourceAddress(), is(equalTo(sourceAddress)));
		assertThat("Returned dest port is not the expected one", packet.getDestPort(), is(equalTo(destPort)));
		assertThat("Returned source port is not the expected one", packet.getDestPort(), is(equalTo(destPort)));
		assertThat("Returned protocol is not the expected one", packet.getProtocol(), is(equalTo(protocol)));
		assertThat("Returned RESTFul status is not the expected one", packet.getStatus(), is(equalTo(restFulStatus)));
		assertThat("Returned data is not the expected one", packet.getData(), is(nullValue()));

		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#createPacket(byte[])}.
	 *
	 * <p>A valid CoAP Rx Response packet with the provided options and data is
	 * created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithData() {
		// Set up the resources for the test.
		byte[] payload = new byte[41 + data.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(destAddress.getAddress(), 0, payload, 2, destAddress.getAddress().length);
		System.arraycopy(sourceAddress.getAddress(), 0, payload, 18, sourceAddress.getAddress().length);
		System.arraycopy(destPortBytes, 0, payload, 34, destPortBytes.length);
		System.arraycopy(sourcePortBytes, 0, payload, 36, sourcePortBytes.length);
		payload[38] = (byte)protocol.getID();
		System.arraycopy(restFulStatusBytes, 0, payload, 39, restFulStatusBytes.length);
		System.arraycopy(data, 0, payload, 41, data.length);

		// Call the method under test.
		CoAPRxResponsePacket packet = CoAPRxResponsePacket.createPacket(payload);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned dest address is not the expected one", packet.getDestAddress(), is(equalTo(destAddress)));
		assertThat("Returned source address is not the expected one", packet.getSourceAddress(), is(equalTo(sourceAddress)));
		assertThat("Returned dest port is not the expected one", packet.getDestPort(), is(equalTo(destPort)));
		assertThat("Returned source port is not the expected one", packet.getDestPort(), is(equalTo(destPort)));
		assertThat("Returned protocol is not the expected one", packet.getProtocol(), is(equalTo(protocol)));
		assertThat("Returned RESTFul status is not the expected one", packet.getStatus(), is(equalTo(restFulStatus)));
		assertThat("Returned data is not the expected one", packet.getData(), is(equalTo(data)));

		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#CoAPRxResponsePacket(int, Inet6Address, Inet6Address, int, int, IPProtocol, RestFulStatusEnum, byte[])}.
	 *
	 * <p>Construct a new CoAP Rx Response packet with a null source address. 
	 * This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateCoAPRxResponsePacketSrcAddressNull() {
		// Set up the resources for the test.
		sourceAddress = null;

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Source address cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		new CoAPRxResponsePacket(frameID, destAddress, null, destPort, sourcePort, protocol, restFulStatus, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#CoAPRxResponsePacket(int, Inet6Address, Inet6Address, int, int, IPProtocol, RestFulStatusEnum, byte[])}.
	 *
	 * <p>Construct a new CoAP Rx Response packet with a null destination 
	 * address. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateCoAPRxResponsePacketDestAddressNull() {
		// Set up the resources for the test.
		destAddress = null;

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Destination address cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		new CoAPRxResponsePacket(frameID, null, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#CoAPRxResponsePacket(int, Inet6Address, Inet6Address, int, int, IPProtocol, RestFulStatusEnum, byte[])}.
	 *
	 * <p>Construct a new CoAP Rx Response packet with a negative destination 
	 * port. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateCoAPRxResponsePacketDestPortNegative() {
		// Set up the resources for the test.
		int destPort = -6;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Destination port must be between 0 and 65535.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#CoAPRxResponsePacket(int, Inet6Address, Inet6Address, int, int, IPProtocol, RestFulStatusEnum, byte[])}.
	 *
	 * <p>Construct a new CoAP Rx Response packet with a destination port 
	 * bigger than 65535. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateCoAPRxResponsePacketDestPortBigger() {
		// Set up the resources for the test.
		int destPort = 66200;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Destination port must be between 0 and 65535.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#CoAPRxResponsePacket(int, Inet6Address, Inet6Address, int, int, IPProtocol, RestFulStatusEnum, byte[])}.
	 *
	 * <p>Construct a new CoAP Rx Response packet with a negative source port. 
	 * This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateCoAPRxResponsePacketSourcePortNegative() {
		// Set up the resources for the test.
		int sourcePort = -6;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Source port must be between 0 and 65535.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#CoAPRxResponsePacket(int, Inet6Address, Inet6Address, int, int, IPProtocol, RestFulStatusEnum, byte[])}.
	 *
	 * <p>Construct a new CoAP Rx Response packet with a source port bigger 
	 * than 65535. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateCoAPRxResponsePacketSourcePortBigger() {
		// Set up the resources for the test.
		int sourcePort = 66200;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Source port must be between 0 and 65535.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#CoAPRxResponsePacket(int, Inet6Address, Inet6Address, int, int, IPProtocol, RestFulStatusEnum, byte[])}.
	 *
	 * <p>Construct a new CoAP Rx Response packet with a null protocol. This 
	 * must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateCoAPRxResponsePacketProtocolNull() {
		// Set up the resources for the test.
		IPProtocol protocol = null;

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Protocol cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#CoAPRxResponsePacket(int, Inet6Address, Inet6Address, int, int, IPProtocol, RestFulStatusEnum, byte[])}.
	 *
	 * <p>Construct a new CoAP Rx Response packet with a null RESTFul status. 
	 * This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateCoAPRxResponsePacketStatusNull() {
		// Set up the resources for the test.
		RestFulStatusEnum restFulStatus = null;

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("RESTFul status cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#CoAPRxResponsePacket(int, Inet6Address, Inet6Address, int, int, IPProtocol, RestFulStatusEnum, byte[])}.
	 *
	 * <p>Construct a new CoAP Rx Response packet without data ({@code null}).</p>
	 */
	@Test
	public final void testCreateCoAPRxResponsePacketValidDataNull() {
		// Set up the resources for the test.
		byte[] data = null;

		int expectedLength = 41;

		// Call the method under test.
		CoAPRxResponsePacket packet = new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned dest address is not the expected one", packet.getDestAddress(), is(equalTo(destAddress)));
		assertThat("Returned source address is not the expected one", packet.getSourceAddress(), is(equalTo(sourceAddress)));
		assertThat("Returned dest port is not the expected one", packet.getDestPort(), is(equalTo(destPort)));
		assertThat("Returned source port is not the expected one", packet.getDestPort(), is(equalTo(destPort)));
		assertThat("Returned protocol is not the expected one", packet.getProtocol(), is(equalTo(protocol)));
		assertThat("Returned RESTFul status is not the expected one", packet.getStatus(), is(equalTo(restFulStatus)));
		assertThat("Returned data is not the expected one", packet.getData(), is(nullValue()));
		assertThat("CoAP Rx Response packet needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#CoAPRxResponsePacket(int, Inet6Address, Inet6Address, int, int, IPProtocol, RestFulStatusEnum, byte[])}.
	 *
	 * <p>Construct a new CoAP Rx Response packet with data.</p>
	 */
	@Test
	public final void testCreateCoAPRxResponsePacketValidDataNotNull() {
		// Set up the resources for the test.
		int expectedLength = 41 + data.length;

		// Call the method under test.
		CoAPRxResponsePacket packet = new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned dest address is not the expected one", packet.getDestAddress(), is(equalTo(destAddress)));
		assertThat("Returned source address is not the expected one", packet.getSourceAddress(), is(equalTo(sourceAddress)));
		assertThat("Returned dest port is not the expected one", packet.getDestPort(), is(equalTo(destPort)));
		assertThat("Returned source port is not the expected one", packet.getDestPort(), is(equalTo(destPort)));
		assertThat("Returned protocol is not the expected one", packet.getProtocol(), is(equalTo(protocol)));
		assertThat("Returned RESTFul status is not the expected one", packet.getStatus(), is(equalTo(restFulStatus)));
		assertThat("Returned data is not the expected one", packet.getData(), is(data));
		assertThat("CoAP Rx Response packet needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#getAPIData()}.
	 *
	 * <p>Test the get API parameters with a {@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIDataReceivedDataNull() {
		// Set up the resources for the test.
		byte[] data = null;

		CoAPRxResponsePacket packet = new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);

		int expectedLength = 40;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		System.arraycopy(destAddress.getAddress(), 0, expectedData, 1, destAddress.getAddress().length);
		System.arraycopy(sourceAddress.getAddress(), 0, expectedData, 17, sourceAddress.getAddress().length);
		System.arraycopy(destPortBytes, 0, expectedData, 33, destPortBytes.length);
		System.arraycopy(sourcePortBytes, 0, expectedData, 35, sourcePortBytes.length);
		expectedData[37] = (byte)protocol.getID();
		System.arraycopy(restFulStatusBytes, 0, expectedData, 38, restFulStatusBytes.length);

		// Call the method under test.
		byte[] apiData = packet.getAPIData();

		// Verify the result.
		assertThat("API data is not the expected", apiData, is(equalTo(expectedData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#getAPIData()}.
	 *
	 * <p>Test the get API parameters with a not-{@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIDataReceivedDataNotNull() {
		// Set up the resources for the test.
		CoAPRxResponsePacket packet = new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);

		int expectedLength = 40 + data.length;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		System.arraycopy(destAddress.getAddress(), 0, expectedData, 1, destAddress.getAddress().length);
		System.arraycopy(sourceAddress.getAddress(), 0, expectedData, 17, sourceAddress.getAddress().length);
		System.arraycopy(destPortBytes, 0, expectedData, 33, destPortBytes.length);
		System.arraycopy(sourcePortBytes, 0, expectedData, 35, sourcePortBytes.length);
		expectedData[37] = (byte)protocol.getID();
		System.arraycopy(restFulStatusBytes, 0, expectedData, 38, restFulStatusBytes.length);
		System.arraycopy(data, 0, expectedData, 40, data.length);

		// Call the method under test.
		byte[] apiData = packet.getAPIData();

		// Verify the result.
		assertThat("API data is not the expected", apiData, is(equalTo(expectedData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#getAPIPacketParameters()}.
	 *
	 * <p>Test the get API parameters but with a {@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersReceivedDataNull() {
		// Set up the resources for the test.
		byte[] data = null;

		CoAPRxResponsePacket packet = new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);

		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();

		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(6)));
		assertThat("Returned destination address is not the expected one", packetParams.get("Destination address"),
				is(equalTo(HexUtils.prettyHexString(destAddress.getAddress()) + " (" + destAddress.getHostAddress() + ")")));
		assertThat("Returned source address is not the expected one", packetParams.get("Source address"),
				is(equalTo(HexUtils.prettyHexString(sourceAddress.getAddress()) + " (" + sourceAddress.getHostAddress() + ")")));
		assertThat("Returned dest port is not the expected one", packetParams.get("Destination port"),
				is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(destPort, 2)) + " (" + destPort + ")")));
		assertThat("Returned source port is not the expected one", packetParams.get("Source port"),
				is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(sourcePort, 2)) + " (" + sourcePort + ")")));
		assertThat("Returned protocol is not the expected one", packetParams.get("Protocol"),
				is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(protocol.getID(), 1)) + " (" + protocol.getName() + ")")));
		assertThat("RESTful status is not the expected one", packetParams.get("RESTful Response Code"),
				is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(restFulStatus.getID(), 2)) + " (" + restFulStatus.getDescription() + ")")));
		assertThat("RF data is not the expected", packetParams.get("RF data"), is(nullValue(String.class)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#getAPIPacketParameters()}.
	 *
	 * <p>Test the get API parameters but with a not-{@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersReceivedDataNotNull() {
		// Set up the resources for the test.
		CoAPRxResponsePacket packet = new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);

		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();

		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(7)));
		assertThat("Returned destination address is not the expected one", packetParams.get("Destination address"),
				is(equalTo(HexUtils.prettyHexString(destAddress.getAddress()) + " (" + destAddress.getHostAddress() + ")")));
		assertThat("Returned source address is not the expected one", packetParams.get("Source address"),
				is(equalTo(HexUtils.prettyHexString(sourceAddress.getAddress()) + " (" + sourceAddress.getHostAddress() + ")")));
		assertThat("Returned dest port is not the expected one", packetParams.get("Destination port"),
				is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(destPort, 2)) + " (" + destPort + ")")));
		assertThat("Returned source port is not the expected one", packetParams.get("Source port"),
				is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(sourcePort, 2)) + " (" + sourcePort + ")")));
		assertThat("Returned protocol is not the expected one", packetParams.get("Protocol"),
				is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(protocol.getID(), 1)) + " (" + protocol.getName() + ")")));
		assertThat("RESTful status is not the expected one", packetParams.get("RESTful Response Code"),
				is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(restFulStatus.getID(), 2)) + " (" + restFulStatus.getDescription() + ")")));
		assertThat("RF data is not the expected", packetParams.get("Data"),
				is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(data)))));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#setSourceAddress(Inet6Address)}.
	 */
	@Test
	public final void testSetSourceAddressNull() {
		// Set up the resources for the test.
		CoAPRxResponsePacket packet = new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Source address cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		packet.setSourceAddress(null);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#setSourceAddress(Inet6Address)}.
	 *
	 * @throws Exception
	 */
	@Test
	public final void testSetSourceAddressNotNull() throws Exception {
		// Set up the resources for the test.
		CoAPRxResponsePacket packet = new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);

		Inet6Address newAddress = (Inet6Address) Inet6Address.getByName("FDB3:0001:0002:0000:0004:0005:0006:0088");

		// Call the method under test.
		packet.setSourceAddress(newAddress);

		// Verify the result.
		assertThat("Source address is not the expected one", packet.getSourceAddress(), is(equalTo(newAddress)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#setDestAddress(Inet6Address)}.
	 */
	@Test
	public final void testSetDestAddressNull() {
		// Set up the resources for the test.
		CoAPRxResponsePacket packet = new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Destination address cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		packet.setDestAddress(null);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#setDestAddress(Inet6Address)}.
	 *
	 * @throws Exception
	 */
	@Test
	public final void testSetDestAddressNotNull() throws Exception {
		// Set up the resources for the test.
		CoAPRxResponsePacket packet = new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);

		Inet6Address newAddress = (Inet6Address) Inet6Address.getByName("FDB3:0001:0002:0000:0004:0005:0006:0088");

		// Call the method under test.
		packet.setDestAddress(newAddress);

		// Verify the result.
		assertThat("Destination address is not the expected one", packet.getDestAddress(), is(equalTo(newAddress)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#setDestPort(int)}.
	 */
	@Test
	public final void testSetDestPortNegative() {
		// Set up the resources for the test.
		CoAPRxResponsePacket packet = new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Destination port must be between 0 and 65535.")));

		// Call the method under test that should throw an IllegalArgumentException.
		packet.setDestPort(-1);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#setDestPort(int)}.
	 */
	@Test
	public final void testSetDestPortBigger() {
		// Set up the resources for the test.
		CoAPRxResponsePacket packet = new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Destination port must be between 0 and 65535.")));

		// Call the method under test that should throw an IllegalArgumentException.
		packet.setDestPort(65536);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#setDestPort(int)}.
	 */
	@Test
	public final void testSetDestPortValid() {
		// Set up the resources for the test.
		CoAPRxResponsePacket packet = new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);

		int newPort = 0;

		// Call the method under test.
		packet.setDestPort(newPort);

		// Verify the result.
		assertThat("Destination port is not the expected one", packet.getDestPort(), is(equalTo(newPort)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#setSourcePort(int)}.
	 */
	@Test
	public final void testSetSourcePortNegative() {
		// Set up the resources for the test.
		CoAPRxResponsePacket packet = new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Source port must be between 0 and 65535.")));

		// Call the method under test that should throw an IllegalArgumentException.
		packet.setSourcePort(-1);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#setSourcePort(int)}.
	 */
	@Test
	public final void testSetSourcePortBigger() {
		// Set up the resources for the test.
		CoAPRxResponsePacket packet = new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Source port must be between 0 and 65535.")));

		// Call the method under test that should throw an IllegalArgumentException.
		packet.setSourcePort(65536);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#setSourcePort(int)}.
	 */
	@Test
	public final void testSetSourcePortValid() {
		// Set up the resources for the test.
		CoAPRxResponsePacket packet = new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);

		int newPort = 65535;

		// Call the method under test.
		packet.setSourcePort(newPort);

		// Verify the result.
		assertThat("Source port is not the expected one", packet.getSourcePort(), is(equalTo(newPort)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#setProtocol(IPProtocol)}.
	 */
	@Test
	public final void testSetProtocolNull() {
		// Set up the resources for the test.
		CoAPRxResponsePacket packet = new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Protocol cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		packet.setProtocol(null);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#setProtocol(IPProtocol)}.
	 */
	@Test
	public final void testSetProtocolNotNull() {
		// Set up the resources for the test.
		CoAPRxResponsePacket packet = new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);

		IPProtocol newProtocol = IPProtocol.UDP;

		// Call the method under test.
		packet.setProtocol(newProtocol);

		// Verify the result.
		assertThat("Protocol is not the expected one", packet.getProtocol(), is(equalTo(newProtocol)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#setStatus(RestFulStatusEnum)}.
	 */
	@Test
	public final void testSetStatusNull() {
		// Set up the resources for the test.
		CoAPRxResponsePacket packet = new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("RESTFul status cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		packet.setStatus(null);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#setStatus(RestFulStatusEnum)}.
	 */
	@Test
	public final void testSetStatusNotNull() {
		// Set up the resources for the test.
		CoAPRxResponsePacket packet = new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);

		RestFulStatusEnum newStatus = RestFulStatusEnum.SUCCESS;

		// Call the method under test.
		packet.setStatus(newStatus);

		// Verify the result.
		assertThat("RESTFul status is not the expected one", packet.getStatus(), is(equalTo(newStatus)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#getData()}.
	 */
	@Test
	public final void testGetDataNullData() {
		// Set up the resources for the test.
		byte[] data = null;

		CoAPRxResponsePacket packet = new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);

		// Call the method under test.
		byte[] result = packet.getData();

		// Verify the result.
		assertThat("Data must be the same", result, is(equalTo(data)));
		assertThat("Data must be null", result, is(nullValue(byte[].class)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#getData()}.
	 */
	@Test
	public final void testGetDataValidData() {
		// Set up the resources for the test.
		CoAPRxResponsePacket packet = new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);

		// Call the method under test.
		byte[] result = packet.getData();

		// Verify the result.
		assertThat("Data must be the same", result, is(equalTo(data)));
		assertThat("Data must not be the same object", result.hashCode(), is(not(equalTo(data.hashCode()))));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#setData(byte[])}.
	 */
	@Test
	public final void testSetDataNullData() {
		// Set up the resources for the test.
		byte[] newData = null;

		CoAPRxResponsePacket packet = new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);

		// Call the method under test.
		packet.setData(newData);

		byte[] result = packet.getData();

		// Verify the result.
		assertThat("Data must be the same", result, is(equalTo(newData)));
		assertThat("Data must be null", result, is(nullValue(byte[].class)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#setData(byte[])}.
	 */
	@Test
	public final void testSetDataValidData() {
		// Set up the resources for the test.
		byte[] newData = "New data".getBytes();

		CoAPRxResponsePacket packet = new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);

		// Call the method under test.
		packet.setData(newData);

		byte[] result = packet.getData();

		// Verify the result.
		assertThat("Data must be the same", result, is(equalTo(newData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPRxResponsePacket#setData(byte[])}.
	 */
	@Test
	public final void testSetDataAndModifyOriginal() {
		// Set up the resources for the test.
		byte[] newData = "New data".getBytes();

		CoAPRxResponsePacket packet = new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, data);

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
