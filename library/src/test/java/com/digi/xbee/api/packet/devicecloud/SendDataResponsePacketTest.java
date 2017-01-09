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

import java.util.LinkedHashMap;

import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.digi.xbee.api.models.DeviceCloudStatus;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

public class SendDataResponsePacketTest {

	private int frameType = APIFrameType.SEND_DATA_RESPONSE.getValue();
	private int frameID = 0x01;
	private DeviceCloudStatus status = DeviceCloudStatus.DEVICE_CLOUD_ERROR;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	public SendDataResponsePacketTest() {
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
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataResponsePacket#createPacket(byte[])}.
	 *
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Set up the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Send Data Response packet payload cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		SendDataResponsePacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataResponsePacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Set up the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Send Data Response packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		SendDataResponsePacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataResponsePacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Set up the resources for the test.
		byte[] payload = new byte[2];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Send Data Response packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		SendDataResponsePacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataResponsePacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array not including the Frame type.</p>
	 */
	@Test
	public final void testCreatePacketPayloadNotIncludingFrameType() {
		// Set up the resources for the test.
		byte[] payload = new byte[3];
		payload[0] = (byte)frameID;
		payload[1] = (byte)status.getID();
		payload[2] = (byte)status.getID();

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not a Send Data Response packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		SendDataResponsePacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataResponsePacket#createPacket(byte[])}.
	 *
	 * <p>A valid API Send Data Response packet with the provided options.</p>
	 */
	@Test
	public final void testCreatePacketValidPayload() {
		// Set up the resources for the test.
		byte[] payload = new byte[3];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		payload[2] = (byte)status.getID();

		// Call the method under test.
		SendDataResponsePacket packet = SendDataResponsePacket.createPacket(payload);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned status is not the expected one", packet.getStatus(), is(equalTo(status)));

		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataResponsePacket#SendDataResponsePacket(int, DeviceCloudStatus)}.
	 *
	 * <p>Construct a new Send Data Response packet with a frame ID bigger than 255.
	 * This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateSendDataResponsePacketFrameIDBiggerThan255() {
		// Set up the resources for the test.
		frameID = 256;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new SendDataResponsePacket(frameID, status);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataResponsePacket#SendDataResponsePacket(int, DeviceCloudStatus)}.
	 *
	 * <p>Construct a new Send Data Response packet with a negative frame ID. This
	 * must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateSendDataResponsePacketFrameIDNegative() {
		// Set up the resources for the test.
		frameID = -1;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new SendDataResponsePacket(frameID, status);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataResponsePacket#SendDataResponsePacket(int, DeviceCloudStatus)}.
	 *
	 * <p>Construct a new Send Data Response packet with {@code null} status. This
	 * must throw an {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateSendDataResponsePacketStatusNull() {
		// Set up the resources for the test.
		status = null;

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Status cannot be null.")));

		// Call the method under test that should throw an NullPointerException.
		new SendDataResponsePacket(frameID, status);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataResponsePacket#SendDataResponsePacket(int, DeviceCloudStatus)}.
	 *
	 * <p>Construct a new Send Data Response packet.</p>
	 */
	@Test
	public final void testCreateSendDataResponsePacket() {
		// Set up the resources for the test.
		int expectedLength = 3;

		// Call the method under test.
		SendDataResponsePacket packet = new SendDataResponsePacket(frameID, status);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned status is not the expected one", packet.getStatus(), is(equalTo(status)));
		assertThat("Send Data Response packet needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataResponsePacket#getAPIData()}.
	 *
	 * <p>Test the get API parameters.</p>
	 */
	@Test
	public final void testGetAPIData() {
		// Set up the resources for the test.
		SendDataResponsePacket packet = new SendDataResponsePacket(frameID, status);

		int expectedLength = 2;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		expectedData[1] = (byte)status.getID();

		// Call the method under test.
		byte[] apiData = packet.getAPIData();

		// Verify the result.
		assertThat("API data is not the expected", apiData, is(equalTo(expectedData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataResponsePacket#getAPIPacketParameters()}.
	 *
	 * <p>Test the get API parameters.</p>
	 */
	@Test
	public final void testGetAPIPacketParameters() {
		// Set up the resources for the test.
		SendDataResponsePacket packet = new SendDataResponsePacket(frameID, status);

		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();

		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(1)));
		assertThat("Returned status is not the expected one", packetParams.get("Status"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(status.getID(), 1)) + " (" + status.getName() + ")")));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequstPacket#isBroadcast()}.
	 *
	 * <p>Test the is broadcast method.</p>
	 */
	@Test
	public final void testIsBroadcast() {
		// Set up the resources for the test.
		SendDataResponsePacket packet = new SendDataResponsePacket(frameID, status);

		// Call the method under test and verify the result.
		assertThat("Packet should not be broadcast", packet.isBroadcast(), is(equalTo(false)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataResponsePacket#setStatus(DeviceCloudStatus)}.
	 */
	@Test
	public final void testSetStatusNull() {
		// Set up the resources for the test.
		SendDataResponsePacket packet = new SendDataResponsePacket(frameID, status);

		status = null;

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Status cannot be null.")));

		// Call the method under test that should throw an NullPointerException.
		packet.setStatus(status);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataResponsePacket#setStatus(DeviceCloudStatus)}.
	 */
	@Test
	public final void testSetStatusNotNull() {
		// Set up the resources for the test.
		SendDataResponsePacket packet = new SendDataResponsePacket(frameID, status);

		status = DeviceCloudStatus.TIME_OUT;

		// Call the method under test.
		packet.setStatus(status);

		// Verify the result.
		assertThat("Returned status is not the expected", packet.getStatus(), is(equalTo(status)));
	}
}
