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

import com.digi.xbee.api.models.FrameError;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

public class FrameErrorPacketTest {

	private int frameType = APIFrameType.FRAME_ERROR.getValue();
	private FrameError error = FrameError.INVALID_CHECKSUM;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	public FrameErrorPacketTest() {
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
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.FrameErrorPacket#createPacket(byte[])}.
	 *
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Set up the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Frame Error packet payload cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		FrameErrorPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.FrameErrorPacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Set up the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Frame Error packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		FrameErrorPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.FrameErrorPacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Set up the resources for the test.
		byte[] payload = new byte[1];
		payload[0] = (byte)error.getID();

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Frame Error packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		FrameErrorPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.FrameErrorPacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array not including the Frame type.</p>
	 */
	@Test
	public final void testCreatePacketPayloadNotIncludingFrameType() {
		// Set up the resources for the test.
		byte[] payload = new byte[2];
		payload[0] = (byte)error.getID();
		payload[1] = (byte)error.getID();

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not a Frame Error packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		FrameErrorPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.FrameErrorPacket#createPacket(byte[])}.
	 *
	 * <p>A valid API Device Response packet with the provided options and data is
	 * created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayload() {
		// Set up the resources for the test.
		byte[] payload = new byte[2];
		payload[0] = (byte)frameType;
		payload[1] = (byte)error.getID();

		// Call the method under test.
		FrameErrorPacket packet = FrameErrorPacket.createPacket(payload);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned error is not the expected one", packet.getError(), is(equalTo(error)));

		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.FrameErrorPacket#FrameErrorPacket(FrameError)}.
	 *
	 * <p>Construct a new Frame Error packet with a {@code null} error.
	 * This must throw an {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateFrameErrorPacketErrorNull() {
		// Set up the resources for the test.
		error = null;

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Frame error cannot be null.")));

		// Call the method under test that should throw an NullPointerException.
		new FrameErrorPacket(error);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.FrameErrorPacket#FrameErrorPacket(FrameError)}.
	 *
	 * <p>Construct a new Frame Error packet.</p>
	 */
	@Test
	public final void testCreateFrameErrorPacketValid() {
		// Set up the resources for the test.
		int expectedLength = 2;

		// Call the method under test.
		FrameErrorPacket packet = new FrameErrorPacket(error);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned error is not the expected one", packet.getError(), is(equalTo(error)));
		assertThat("Frame Error packet does not need API Frame ID", packet.needsAPIFrameID(), is(equalTo(false)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.FrameErrorPacket#getAPIPacketParameters()}.
	 *
	 * <p>Test the get API parameters.</p>
	 */
	@Test
	public final void testGetAPIPacketParameters() {
		// Set up the resources for the test.
		FrameErrorPacket packet = new FrameErrorPacket(error);

		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();

		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(1)));
		assertThat("Returned error is not the expected one", packetParams.get("Error"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(error.getID(), 1)) + " (" + error.getName() + ")")));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.DeviceRequstPacket#isBroadcast()}.
	 *
	 * <p>Test the is broadcast method.</p>
	 */
	@Test
	public final void testIsBroadcast() {
		// Set up the resources for the test.
		FrameErrorPacket packet = new FrameErrorPacket(error);

		// Call the method under test and verify the result.
		assertThat("Packet should not be broadcast", packet.isBroadcast(), is(equalTo(false)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.FrameErrorPacket#setError(FrameError)}.
	 */
	@Test
	public final void testSetErrorNull() {
		// Set up the resources for the test.
		FrameErrorPacket packet = new FrameErrorPacket(error);

		error = null;

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Frame error cannot be null.")));

		// Call the method under test that should throw an NullPointerException.
		packet.setError(error);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.FrameErrorPacket#setError(FrameError)}.
	 */
	@Test
	public final void testSetErrorValid() {
		// Set up the resources for the test.
		FrameErrorPacket packet = new FrameErrorPacket(error);

		error = FrameError.PAYLOAD_TOO_BIG;

		// Call the method under test.
		packet.setError(error);

		// Verify the result.
		assertThat("Returned error is not the expected", packet.getError(), is(equalTo(error)));
	}
}
