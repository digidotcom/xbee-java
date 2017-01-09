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

public class DeviceResponsePacketTest {

	private int frameType = APIFrameType.DEVICE_RESPONSE.getValue();
	private int frameID = 0x01;
	private int requestID = 0x01;
	private byte[] data = "OK".getBytes();

	@Rule
	public ExpectedException exception = ExpectedException.none();

	public DeviceResponsePacketTest() {
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
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceResponsePacket#createPacket(byte[])}.
	 *
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Set up the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Device Response packet payload cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		DeviceResponsePacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceResponsePacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Set up the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Device Response packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		DeviceResponsePacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceResponsePacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Set up the resources for the test.
		byte[] payload = new byte[3];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		payload[2] = (byte)requestID;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Device Response packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		DeviceResponsePacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceResponsePacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array not including the Frame type.</p>
	 */
	@Test
	public final void testCreatePacketPayloadNotIncludingFrameType() {
		// Set up the resources for the test.
		byte[] payload = new byte[3 + data.length];
		payload[0] = (byte)frameID;
		payload[1] = (byte)requestID;
		payload[2] = (byte)0x00;
		System.arraycopy(data, 0, payload, 3, data.length);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not a Device Response packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		DeviceResponsePacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceResponsePacket#createPacket(byte[])}.
	 *
	 * <p>A valid API Device Response packet with the provided options without data
	 * is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutData() {
		// Set up the resources for the test.
		byte[] payload = new byte[4];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		payload[2] = (byte)requestID;
		payload[3] = (byte)0x00;

		// Call the method under test.
		DeviceResponsePacket packet = DeviceResponsePacket.createPacket(payload);

		System.out.println(packet);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned request ID is not the expected one", packet.getRequestID(), is(equalTo(requestID)));
		assertThat("Returned data is not the expected one", packet.getResponseData(), is(nullValue()));

		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceResponsePacket#createPacket(byte[])}.
	 *
	 * <p>A valid API Device Response packet with the provided options and data is
	 * created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithData() {
		// Set up the resources for the test.
		byte[] payload = new byte[4 + data.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		payload[2] = (byte)requestID;
		payload[3] = (byte)0x00;
		System.arraycopy(data, 0, payload, 4, data.length);

		// Call the method under test.
		DeviceResponsePacket packet = DeviceResponsePacket.createPacket(payload);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned request ID is not the expected one", packet.getRequestID(), is(equalTo(requestID)));
		assertThat("Returned data is not the expected one", packet.getResponseData(), is(equalTo(data)));

		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceResponsePacket#DeviceResponsePacket(int, int, byte[])}.
	 *
	 * <p>Construct a new Device Response packet with a frame ID bigger than 255.
	 * This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateDeviceResponsePacketFrameIDBiggerThan255() {
		// Set up the resources for the test.
		frameID = 256;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new DeviceResponsePacket(frameID, requestID, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceResponsePacket#DeviceResponsePacket(int, int, byte[])}.
	 *
	 * <p>Construct a new Device Response packet with a negative frame ID.
	 * This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateDeviceResponsePacketFrameIDNegative() {
		// Set up the resources for the test.
		frameID = -1;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new DeviceResponsePacket(frameID, requestID, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceResponsePacket#DeviceResponsePacket(int, int, byte[])}.
	 *
	 * <p>Construct a new Device Response packet with a request ID bigger than 255.
	 * This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateDeviceResponsePacketRequestIDBiggerThan255() {
		// Set up the resources for the test.
		requestID = 256;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Device request ID must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new DeviceResponsePacket(frameID, requestID, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceResponsePacket#DeviceResponsePacket(int, int, byte[])}.
	 *
	 * <p>Construct a new Device Response packet with a negative request ID. This
	 * must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateDeviceResponsePacketRequestIDNegative() {
		// Set up the resources for the test.
		requestID = -6;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Device request ID must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new DeviceResponsePacket(frameID, requestID, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceResponsePacket#DeviceResponsePacket(int, int, byte[])}.
	 *
	 * <p>Construct a new Device Response packet without data ({@code null}).</p>
	 */
	@Test
	public final void testCreateDeviceResponsePacketValidDataNull() {
		// Set up the resources for the test.
		data = null;

		int expectedLength = 4;

		// Call the method under test.
		DeviceResponsePacket packet = new DeviceResponsePacket(frameID, requestID, data);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned request ID is not the expected one", packet.getRequestID(), is(equalTo(requestID)));
		assertThat("Returned data is not the expected one", packet.getResponseData(), is(nullValue()));
		assertThat("Device Response packet needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceResponsePacket#DeviceResponsePacket(int, int, byte[])}.
	 *
	 * <p>Construct a new Device Response packet with data.</p>
	 */
	@Test
	public final void testCreateDeviceResponsePacketValidDataNotNull() {
		// Set up the resources for the test.
		int expectedLength = 4 + data.length;

		// Call the method under test.
		DeviceResponsePacket packet = new DeviceResponsePacket(frameID, requestID, data);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned request ID is not the expected one", packet.getRequestID(), is(equalTo(requestID)));
		assertThat("Returned data is not the expected one", packet.getResponseData(), is(equalTo(data)));
		assertThat("Device Response packet needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceResponsePacket#getAPIData()}.
	 *
	 * <p>Test the get API parameters with a {@code null} data.</p>
	 */
	@Test
	public final void testGetAPIDataDataNull() {
		// Set up the resources for the test.
		data = null;

		DeviceResponsePacket packet = new DeviceResponsePacket(frameID, requestID, data);

		int expectedLength = 3;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		expectedData[1] = (byte)requestID;
		expectedData[2] = (byte)0x00;

		// Call the method under test.
		byte[] apiData = packet.getAPIData();

		// Verify the result.
		assertThat("API data is not the expected", apiData, is(equalTo(expectedData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceResponsePacket#getAPIData()}.
	 *
	 * <p>Test the get API parameters.</p>
	 */
	@Test
	public final void testGetAPIDataDataNotNull() {
		// Set up the resources for the test.
		DeviceResponsePacket packet = new DeviceResponsePacket(frameID, requestID, data);

		int expectedLength = 3 + data.length;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		expectedData[1] = (byte)requestID;
		expectedData[2] = (byte)0x00;
		System.arraycopy(data, 0, expectedData, 3, data.length);

		// Call the method under test.
		byte[] apiData = packet.getAPIData();

		// Verify the result.
		assertThat("API data is not the expected", apiData, is(equalTo(expectedData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceResponsePacket#getAPIPacketParameters()}.
	 *
	 * <p>Test the get API parameters but with {@code null} data.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersDataNull() {
		// Set up the resources for the test.
		data = null;

		DeviceResponsePacket packet = new DeviceResponsePacket(frameID, requestID, data);

		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();

		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(2)));
		assertThat("Returned request ID is not the expected one", packetParams.get("Device Request ID"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(requestID, 1)) + " (" + requestID + ")")));
		assertThat("Returned reserved is not the expected one", packetParams.get("Reserved"), is("00"));
		assertThat("Returned data is not the expected one", packetParams.get("RF data"), is(nullValue()));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceResponsePacket#getAPIPacketParameters()}.
	 *
	 * <p>Test the get API parameters.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersDataNotNull() {
		// Set up the resources for the test.
		DeviceResponsePacket packet = new DeviceResponsePacket(frameID, requestID, data);

		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();

		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(3)));
		assertThat("Returned request ID is not the expected one", packetParams.get("Device Request ID"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(requestID, 1)) + " (" + requestID + ")")));
		assertThat("Returned reserved is not the expected one", packetParams.get("Reserved"), is("00"));
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
		DeviceResponsePacket packet = new DeviceResponsePacket(frameID, requestID, data);

		// Call the method under test and verify the result.
		assertThat("Packet should not be broadcast", packet.isBroadcast(), is(equalTo(false)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceResponsePacket#setRequestID(int)}.
	 */
	@Test
	public final void testSetRequestIDBiggerThan255() {
		// Set up the resources for the test.
		DeviceResponsePacket packet = new DeviceResponsePacket(frameID, requestID, data);

		requestID = 256;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Device request ID must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		packet.setRequestID(requestID);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceResponsePacket#setRequestID(int)}.
	 */
	@Test
	public final void testSetRequestIDNegative() {
		// Set up the resources for the test.
		DeviceResponsePacket packet = new DeviceResponsePacket(frameID, requestID, data);

		requestID = -1;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Device request ID must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		packet.setRequestID(requestID);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceResponsePacket#setRequestID(int)}.
	 */
	@Test
	public final void testSetRequestIDValid() {
		// Set up the resources for the test.
		DeviceResponsePacket packet = new DeviceResponsePacket(frameID, requestID, data);

		requestID = 20;

		// Call the method under test.
		packet.setRequestID(requestID);

		// Verify the result.
		assertThat("Returned request ID is not the expected", packet.getRequestID(), is(equalTo(requestID)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceResponsePacket#setRequestData(byte[])}.
	 */
	@Test
	public final void testSetDataNull() {
		// Set up the resources for the test.
		DeviceResponsePacket packet = new DeviceResponsePacket(frameID, requestID, data);

		data = null;

		// Call the method under test.
		packet.setResponseData(data);

		// Verify the result.
		assertThat("Returned data is not the expected", packet.getResponseData(), is(nullValue()));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceResponsePacket#setRequestData(byte[])}.
	 */
	@Test
	public final void testSetDataNotNull() {
		// Set up the resources for the test.
		DeviceResponsePacket packet = new DeviceResponsePacket(frameID, requestID, data);

		data = "New data".getBytes();

		// Call the method under test.
		packet.setResponseData(data);

		// Verify the result.
		assertThat("Returned data is not the expected", packet.getResponseData(), is(equalTo(data)));
	}
}
