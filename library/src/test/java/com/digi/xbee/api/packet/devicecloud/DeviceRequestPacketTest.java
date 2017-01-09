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
package com.digi.xbee.api.packet.devicecloud;

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

import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

public class DeviceRequestPacketTest {

	private int frameType = APIFrameType.DEVICE_REQUEST.getValue();
	private int requestID = 0x01;
	private int transport = 0x00;
	private int flags = 0x00;
	private String target = "target";
	private byte[] data = "Test".getBytes();

	@Rule
	public ExpectedException exception = ExpectedException.none();

	public DeviceRequestPacketTest() {
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
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket#createPacket(byte[])}.
	 *
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Set up the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Device Request packet payload cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		DeviceRequestPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Set up the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Device Request packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		DeviceRequestPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Set up the resources for the test.
		byte[] payload = new byte[4];
		payload[0] = (byte)frameType;
		payload[1] = (byte)requestID;
		payload[2] = (byte)transport;
		payload[3] = (byte)flags;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Device Request packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		DeviceRequestPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array not including the Frame type.</p>
	 */
	@Test
	public final void testCreatePacketPayloadNotIncludingFrameType() {
		// Set up the resources for the test.
		byte[] payload = new byte[4 + target.length() + data.length];
		payload[0] = (byte)requestID;
		payload[1] = (byte)transport;
		payload[2] = (byte)flags;
		payload[3] = (byte)target.length();
		System.arraycopy(target.getBytes(), 0, payload, 4, target.length());
		System.arraycopy(data, 0, payload, 4 + target.length(), data.length);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not a Device Request packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		DeviceRequestPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket#createPacket(byte[])}.
	 *
	 * <p>A valid API Device Request packet with the provided options and data is
	 * created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutTarget() {
		// Set up the resources for the test.
		byte[] payload = new byte[5 + data.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)requestID;
		payload[2] = (byte)transport;
		payload[3] = (byte)flags;
		payload[4] = (byte)0x00;
		System.arraycopy(data, 0, payload, 5, data.length);

		// Call the method under test.
		DeviceRequestPacket packet = DeviceRequestPacket.createPacket(payload);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned request ID is not the expected one", packet.getRequestID(), is(equalTo(requestID)));
		assertThat("Returned transport is not the expected one", packet.getTransport(), is(equalTo(transport)));
		assertThat("Returned flags is not the expected one", packet.getFlags(), is(equalTo(flags)));
		assertThat("Returned target is not the expected one", packet.getRequestTarget(), is(nullValue()));
		assertThat("Returned data is not the expected one", packet.getRequestData(), is(equalTo(data)));

		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket#createPacket(byte[])}.
	 *
	 * <p>A valid API Device Request packet with the provided options without data
	 * is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutData() {
		// Set up the resources for the test.
		byte[] payload = new byte[5 + target.length()];
		payload[0] = (byte)frameType;
		payload[1] = (byte)requestID;
		payload[2] = (byte)transport;
		payload[3] = (byte)flags;
		payload[4] = (byte)target.length();
		System.arraycopy(target.getBytes(), 0, payload, 5, target.length());

		// Call the method under test.
		DeviceRequestPacket packet = DeviceRequestPacket.createPacket(payload);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned request ID is not the expected one", packet.getRequestID(), is(equalTo(requestID)));
		assertThat("Returned transport is not the expected one", packet.getTransport(), is(equalTo(transport)));
		assertThat("Returned flags is not the expected one", packet.getFlags(), is(equalTo(flags)));
		assertThat("Returned target is not the expected one", packet.getRequestTarget(), is(equalTo(target)));
		assertThat("Returned data is not the expected one", packet.getRequestData(), is(nullValue()));

		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket#createPacket(byte[])}.
	 *
	 * <p>A valid API Device Request packet with the provided options and data is
	 * created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithData() {
		// Set up the resources for the test.
		byte[] payload = new byte[5 + target.length() + data.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)requestID;
		payload[2] = (byte)transport;
		payload[3] = (byte)flags;
		payload[4] = (byte)target.length();
		System.arraycopy(target.getBytes(), 0, payload, 5, target.length());
		System.arraycopy(data, 0, payload, 5 + target.length(), data.length);

		// Call the method under test.
		DeviceRequestPacket packet = DeviceRequestPacket.createPacket(payload);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned request ID is not the expected one", packet.getRequestID(), is(equalTo(requestID)));
		assertThat("Returned transport is not the expected one", packet.getTransport(), is(equalTo(transport)));
		assertThat("Returned flags is not the expected one", packet.getFlags(), is(equalTo(flags)));
		assertThat("Returned target is not the expected one", packet.getRequestTarget(), is(equalTo(target)));
		assertThat("Returned data is not the expected one", packet.getRequestData(), is(equalTo(data)));

		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket#DeviceRequestPacket(int, String, byte[])}.
	 *
	 * <p>Construct a new Device Request packet with a request ID bigger than 255.
	 * This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateDeviceRequestPacketRequestIDBiggerThan255() {
		// Set up the resources for the test.
		requestID = 524;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Device request ID must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new DeviceRequestPacket(requestID, target, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket#DeviceRequestPacket(int, String, byte[])}.
	 *
	 * <p>Construct a new Device Request packet with a negative request ID. This
	 * must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateDeviceRequestPacketRequestIDNegative() {
		// Set up the resources for the test.
		requestID = -6;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Device request ID must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new DeviceRequestPacket(requestID, target, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket#DeviceRequestPacket(int, String, byte[])}.
	 *
	 * <p>Construct a new Device Request packet with a target bigger than 255. This
	 * must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateDeviceRequestTargetBiggerThan255() {
		// Set up the resources for the test.
		target = new String(new char[256]).replace('\0', '-');

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Target lenght cannot exceed 255 bytes.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new DeviceRequestPacket(requestID, target, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket#DeviceRequestPacket(int, String, byte[])}.
	 *
	 * <p>Construct a new Device Request packet without target ({@code null}).</p>
	 */
	@Test
	public final void testCreateDeviceRequestPacketTargetNullValidData() {
		// Set up the resources for the test.
		target = null;

		int expectedLength = 5 + data.length;

		// Call the method under test.
		DeviceRequestPacket packet = new DeviceRequestPacket(requestID, target, data);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned request ID is not the expected one", packet.getRequestID(), is(equalTo(requestID)));
		assertThat("Returned transport is not the expected one", packet.getTransport(), is(equalTo(transport)));
		assertThat("Returned flags is not the expected one", packet.getFlags(), is(equalTo(flags)));
		assertThat("Returned target is not the expected one", packet.getRequestTarget(), is(nullValue()));
		assertThat("Returned data is not the expected one", packet.getRequestData(), is(equalTo(data)));
		assertThat("Device Request packet does not need API Frame ID", packet.needsAPIFrameID(), is(equalTo(false)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket#DeviceRequestPacket(int, String, byte[])}.
	 *
	 * <p>Construct a new Device Request packet without data ({@code null}).</p>
	 */
	@Test
	public final void testCreateDeviceRequestPacketValidDataNull() {
		// Set up the resources for the test.
		data = null;

		int expectedLength = 5 + target.length();

		// Call the method under test.
		DeviceRequestPacket packet = new DeviceRequestPacket(requestID, target, data);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned request ID is not the expected one", packet.getRequestID(), is(equalTo(requestID)));
		assertThat("Returned transport is not the expected one", packet.getTransport(), is(equalTo(transport)));
		assertThat("Returned flags is not the expected one", packet.getFlags(), is(equalTo(flags)));
		assertThat("Returned target is not the expected one", packet.getRequestTarget(), is(equalTo(target)));
		assertThat("Returned data is not the expected one", packet.getRequestData(), is(nullValue()));
		assertThat("Device Request packet does not need API Frame ID", packet.needsAPIFrameID(), is(equalTo(false)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket#DeviceRequestPacket(int, String, byte[])}.
	 *
	 * <p>Construct a new Device Request packet with data.</p>
	 */
	@Test
	public final void testCreateDeviceRequestPacketValidDataNotNull() {
		// Set up the resources for the test.
		int expectedLength = 5 + target.length() + data.length;

		// Call the method under test.
		DeviceRequestPacket packet = new DeviceRequestPacket(requestID, target, data);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned request ID is not the expected one", packet.getRequestID(), is(equalTo(requestID)));
		assertThat("Returned transport is not the expected one", packet.getTransport(), is(equalTo(transport)));
		assertThat("Returned flags is not the expected one", packet.getFlags(), is(equalTo(flags)));
		assertThat("Returned target is not the expected one", packet.getRequestTarget(), is(equalTo(target)));
		assertThat("Returned data is not the expected one", packet.getRequestData(), is(equalTo(data)));
		assertThat("Device Request packet does not need API Frame ID", packet.needsAPIFrameID(), is(equalTo(false)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket#getAPIData()}.
	 *
	 * <p>Test the get API parameters with a {@code null} target.</p>
	 */
	@Test
	public final void testGetAPIDataTargetNull() {
		// Set up the resources for the test.
		target = null;

		DeviceRequestPacket packet = new DeviceRequestPacket(requestID, target, data);

		int expectedLength = 4 + data.length;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)requestID;
		expectedData[1] = (byte)transport;
		expectedData[2] = (byte) flags;
		expectedData[3] = 0x00;
		System.arraycopy(data, 0, expectedData, 4, data.length);

		// Call the method under test.
		byte[] apiData = packet.getAPIData();

		// Verify the result.
		assertThat("API data is not the expected", apiData, is(equalTo(expectedData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket#getAPIData()}.
	 *
	 * <p>Test the get API parameters with a {@code null} data.</p>
	 */
	@Test
	public final void testGetAPIDataDataNull() {
		// Set up the resources for the test.
		data = null;

		DeviceRequestPacket packet = new DeviceRequestPacket(requestID, target, data);

		int expectedLength = 4 + target.length();
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)requestID;
		expectedData[1] = (byte)transport;
		expectedData[2] = (byte) flags;
		expectedData[3] = (byte)target.length();
		System.arraycopy(target.getBytes(), 0, expectedData, 4, target.length());

		// Call the method under test.
		byte[] apiData = packet.getAPIData();

		// Verify the result.
		assertThat("API data is not the expected", apiData, is(equalTo(expectedData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket#getAPIData()}.
	 *
	 * <p>Test the get API parameters.</p>
	 */
	@Test
	public final void testGetAPIDataTargetAndDataNotNull() {
		// Set up the resources for the test.
		DeviceRequestPacket packet = new DeviceRequestPacket(requestID, target, data);

		int expectedLength = 4 + target.length() + data.length;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)requestID;
		expectedData[1] = (byte)transport;
		expectedData[2] = (byte) flags;
		expectedData[3] = (byte)target.length();
		System.arraycopy(target.getBytes(), 0, expectedData, 4, target.length());
		System.arraycopy(data, 0, expectedData, 4 + target.length(), data.length);

		// Call the method under test.
		byte[] apiData = packet.getAPIData();

		// Verify the result.
		assertThat("API data is not the expected", apiData, is(equalTo(expectedData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket#getAPIPacketParameters()}.
	 *
	 * <p>Test the get API parameters but with a {@code null} target.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersTargetNull() {
		// Set up the resources for the test.
		target = null;

		DeviceRequestPacket packet = new DeviceRequestPacket(requestID, target, data);

		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();

		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(5)));
		assertThat("Returned request ID is not the expected one", packetParams.get("Device Request ID"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(requestID, 1)) + " (" + requestID + ")")));
		assertThat("Returned transport is not the expected one", packetParams.get("Transport"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(transport, 1)))));
		assertThat("Returned flags is not the expected one", packetParams.get("Flags"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(flags, 1)))));
		assertThat("Returned target length is not the expected one", packetParams.get("Target length"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(0, 1)) + " (0)")));
		assertThat("Returned data is not the expected one", packetParams.get("Data"), is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(data)))));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket#getAPIPacketParameters()}.
	 *
	 * <p>Test the get API parameters but with {@code null} data.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersDataNull() {
		// Set up the resources for the test.
		data = null;

		DeviceRequestPacket packet = new DeviceRequestPacket(requestID, target, data);

		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();

		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(5)));
		assertThat("Returned request ID is not the expected one", packetParams.get("Device Request ID"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(requestID, 1)) + " (" + requestID + ")")));
		assertThat("Returned transport is not the expected one", packetParams.get("Transport"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(transport, 1)))));
		assertThat("Returned flags is not the expected one", packetParams.get("Flags"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(flags, 1)))));
		assertThat("Returned target length is not the expected one", packetParams.get("Target length"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(target.length(), 1)) + " (" + target.length() + ")")));
		assertThat("Returned target length is not the expected one", packetParams.get("Target"), is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(target.getBytes())) + " (" + target + ")")));
		assertThat("Returned data is not the expected one", packetParams.get("Data"), is(nullValue()));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket#getAPIPacketParameters()}.
	 *
	 * <p>Test the get API parameters.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersTargetAndDataNotNull() {
		// Set up the resources for the test.
		DeviceRequestPacket packet = new DeviceRequestPacket(requestID, target, data);

		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();

		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(6)));
		assertThat("Returned request ID is not the expected one", packetParams.get("Device Request ID"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(requestID, 1)) + " (" + requestID + ")")));
		assertThat("Returned transport is not the expected one", packetParams.get("Transport"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(transport, 1)))));
		assertThat("Returned flags is not the expected one", packetParams.get("Flags"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(flags, 1)))));
		assertThat("Returned target length is not the expected one", packetParams.get("Target length"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(target.length(), 1)) + " (" + target.length() + ")")));
		assertThat("Returned target length is not the expected one", packetParams.get("Target"), is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(target.getBytes())) + " (" + target + ")")));
		assertThat("Returned data is not the expected one", packetParams.get("Data"), is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(data)))));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequstPacket#isBroadcast()}.
	 *
	 * <p>Test the is broadcast method.</p>
	 */
	@Test
	public final void testIsBroadcast() {
		// Set up the resources for the test.
		DeviceRequestPacket packet = new DeviceRequestPacket(requestID, target, data);

		// Call the method under test and verify the result.
		assertThat("Packet should not be broadcast", packet.isBroadcast(), is(equalTo(false)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket#setRequestID(int)}.
	 */
	@Test
	public final void testSetRequestIDBiggerThan255() {
		// Set up the resources for the test.
		DeviceRequestPacket packet = new DeviceRequestPacket(requestID, target, data);

		requestID = 256;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Device request ID must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		packet.setRequestID(requestID);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket#setRequestID(int)}.
	 */
	@Test
	public final void testSetRequestIDNegative() {
		// Set up the resources for the test.
		DeviceRequestPacket packet = new DeviceRequestPacket(requestID, target, data);

		requestID = -1;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Device request ID must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		packet.setRequestID(requestID);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket#setRequestID(int)}.
	 */
	@Test
	public final void testSetRequestIDValid() {
		// Set up the resources for the test.
		DeviceRequestPacket packet = new DeviceRequestPacket(requestID, target, data);

		requestID = 20;

		// Call the method under test.
		packet.setRequestID(requestID);

		// Verify the result.
		assertThat("Returned request ID is not the expected", packet.getRequestID(), is(equalTo(requestID)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket#getTransport()}.
	 */
	@Test
	public final void testGetTransport() {
		// Set up the resources for the test.
		DeviceRequestPacket packet = new DeviceRequestPacket(requestID, target, data);

		// Verify the result.
		assertThat("Returned transport is not the expected", packet.getTransport(), is(equalTo(0)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket#getFlags()}.
	 */
	@Test
	public final void testGetFlags() {
		// Set up the resources for the test.
		DeviceRequestPacket packet = new DeviceRequestPacket(requestID, target, data);

		// Verify the result.
		assertThat("Returned flags is not the expected", packet.getFlags(), is(equalTo(0)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket#setRequestID(int)}.
	 */
	@Test
	public final void testSetTargetBiggerThan255() {
		// Set up the resources for the test.
		DeviceRequestPacket packet = new DeviceRequestPacket(requestID, target, data);

		target = new String(new char[256]).replace('\0', '-');

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Target lenght cannot exceed 255 bytes.")));

		// Call the method under test that should throw an IllegalArgumentException.
		packet.setRequestTarget(target);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket#setRequestID(int)}.
	 */
	@Test
	public final void testSetTargetNull() {
		// Set up the resources for the test.
		DeviceRequestPacket packet = new DeviceRequestPacket(requestID, target, data);

		target = null;

		// Call the method under test.
		packet.setRequestTarget(target);

		// Verify the result.
		assertThat("Returned target is not the expected", packet.getRequestTarget(), is(nullValue()));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket#setRequestID(int)}.
	 */
	@Test
	public final void testSetTargetNotNull() {
		// Set up the resources for the test.
		DeviceRequestPacket packet = new DeviceRequestPacket(requestID, target, data);

		target = "myTarget";

		// Call the method under test.
		packet.setRequestTarget(target);

		// Verify the result.
		assertThat("Returned target is not the expected", packet.getRequestTarget(), is(equalTo(target)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket#setRequestData(byte[])}.
	 */
	@Test
	public final void testSetDataNull() {
		// Set up the resources for the test.
		DeviceRequestPacket packet = new DeviceRequestPacket(requestID, target, data);

		data = null;

		// Call the method under test.
		packet.setRequestData(data);

		// Verify the result.
		assertThat("Returned data is not the expected", packet.getRequestData(), is(nullValue()));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket#setRequestData(byte[])}.
	 */
	@Test
	public final void testSetDataNotNull() {
		// Set up the resources for the test.
		DeviceRequestPacket packet = new DeviceRequestPacket(requestID, target, data);

		data = "New data".getBytes();

		// Call the method under test.
		packet.setRequestData(data);

		// Verify the result.
		assertThat("Returned data is not the expected", packet.getRequestData(), is(equalTo(data)));
	}
}
