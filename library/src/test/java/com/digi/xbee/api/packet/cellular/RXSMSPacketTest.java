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
package com.digi.xbee.api.packet.cellular;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.Is.is;
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

import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.cellular.RXSMSPacket;
import com.digi.xbee.api.utils.HexUtils;

public class RXSMSPacketTest {

	private int frameType = APIFrameType.RX_SMS.getValue();
	private String phoneNumber = "555203203";
	private String data = "Test";

	@Rule
	public ExpectedException exception = ExpectedException.none();

	public RXSMSPacketTest() {
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
	 * Test method for {@link com.digi.xbee.api.packet.cellular.RXSMSPacket#createPacket(byte[])}.
	 *
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Set up the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("RX SMS packet payload cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		RXSMSPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.cellular.RXSMSPacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Set up the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete RX SMS packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		RXSMSPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.cellular.RXSMSPacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Set up the resources for the test.
		byte[] phoneNumber = Arrays.copyOf(this.phoneNumber.getBytes(), 20);

		byte[] payload = phoneNumber;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete RX SMS packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		RXSMSPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.cellular.RXSMSPacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array not including the Frame type.</p>
	 */
	@Test
	public final void testCreatePacketPayloadNotIncludingFrameType() {
		// Set up the resources for the test.
		byte[] phoneNumber = Arrays.copyOf(this.phoneNumber.getBytes(), 20);

		byte[] payload = new byte[20 + data.length()];
		System.arraycopy(phoneNumber, 0, payload, 0, phoneNumber.length);
		System.arraycopy(data.getBytes(), 0, payload, 20, data.length());

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not a RX SMS packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		RXSMSPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.cellular.RXSMSPacket#createPacket(byte[])}.
	 *
	 * <p>A valid API RX SMS packet with the provided options without data is
	 * created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutData() {
		// Set up the resources for the test.
		byte[] phoneNumber = Arrays.copyOf(this.phoneNumber.getBytes(), 20);

		byte[] payload = new byte[21];
		payload[0] = (byte)frameType;
		System.arraycopy(phoneNumber, 0, payload, 1, phoneNumber.length);

		// Call the method under test.
		RXSMSPacket packet = RXSMSPacket.createPacket(payload);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned phone number is not the expected one", packet.getPhoneNumberByteArray(), is(equalTo(phoneNumber)));
		assertThat("Returned data is not the expected one", packet.getData(), is(nullValue()));

		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.cellular.RXSMSPacket#createPacket(byte[])}.
	 *
	 * <p>A valid API RX SMS packet with the provided options and data is
	 * created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithData() {
		// Set up the resources for the test.
		byte[] phoneNumber = Arrays.copyOf(this.phoneNumber.getBytes(), 20);

		byte[] payload = new byte[21 + data.length()];
		payload[0] = (byte)frameType;
		System.arraycopy(phoneNumber, 0, payload, 1, phoneNumber.length);
		System.arraycopy(data.getBytes(), 0, payload, 21, data.length());

		// Call the method under test.
		RXSMSPacket packet = RXSMSPacket.createPacket(payload);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned phone number is not the expected one", packet.getPhoneNumberByteArray(), is(equalTo(phoneNumber)));
		assertThat("Returned data is not the expected one", packet.getData(), is(equalTo(data)));

		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.cellular.RXSMSPacket#RXSMSPacket(int, int, String, byte[])}.
	 *
	 * <p>Construct a new RX SMS packet with a {@code null} phone number.
	 * This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateRXSMSPacketPhoneNumberNull() {
		// Set up the resources for the test.
		phoneNumber = null;

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Phone number cannot be null.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new RXSMSPacket(phoneNumber, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.cellular.RXSMSPacket#RXSMSPacket(int, int, String, byte[])}.
	 *
	 * <p>Construct a new RX SMS packet with an invalid phone number length.
	 * This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateRXSMSPacketPhoneNumberInvalidLength() {
		// Set up the resources for the test.
		phoneNumber = "555555555555555555555";

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Phone number length cannot be greater than 20 bytes.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new RXSMSPacket(phoneNumber, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.cellular.RXSMSPacket#RXSMSPacket(int, int, String, byte[])}.
	 *
	 * <p>Construct a new RX SMS packet with an invalid phone number.
	 * This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateRXSMSPacketInvalidPhoneNumber() {
		// Set up the resources for the test.
		phoneNumber = "44fj342ljp3";

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Phone number invalid, only numbers and '+' prefix allowed.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new RXSMSPacket(phoneNumber, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.cellular.RXSMSPacket#RXSMSPacket(int, int, String, byte[])}.
	 *
	 * <p>Construct a new RX SMS packet with a phone number that starts with
	 * the plus prefix.</p>
	 */
	@Test
	public final void testCreateRXSMSPacketValidPhoneNumberPlus() {
		// Set up the resources for the test.
		phoneNumber = "+34652023202";

		int expectedLength = 1 /* Frame type */ + 20 /* phone number */ + data.length();

		// Call the method under test.
		RXSMSPacket packet = new RXSMSPacket(phoneNumber, data);

		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned phone number is not the expected one", packet.getPhoneNumberByteArray(), is(equalTo(Arrays.copyOf(phoneNumber.getBytes(), 20))));
		assertThat("Returned phone number is not the expected one", packet.getPhoneNumber(), is(equalTo(phoneNumber)));
		assertThat("Returned data is not the expected one", packet.getData(), is(equalTo(data)));
		assertThat("RX SMS packet needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(false)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.cellular.RXSMSPacket#RXSMSPacket(int, int, String, byte[])}.
	 *
	 * <p>Construct a new RX SMS packet without data ({@code null}).</p>
	 */
	@Test
	public final void testCreateRXSMSPacketValidDataNull() {
		// Set up the resources for the test.
		data = null;

		int expectedLength = 1 /* Frame type */ + 20 /* phone number */;

		// Call the method under test.
		RXSMSPacket packet = new RXSMSPacket(phoneNumber, data);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned phone number is not the expected one", packet.getPhoneNumberByteArray(), is(equalTo(Arrays.copyOf(phoneNumber.getBytes(), 20))));
		assertThat("Returned phone number is not the expected one", packet.getPhoneNumber(), is(equalTo(phoneNumber)));
		assertThat("Returned data is not the expected one", packet.getData(), is(nullValue(String.class)));
		assertThat("RX SMS packet needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(false)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.cellular.RXSMSPacket#RXSMSPacket(int, int, String, byte[])}.
	 *
	 * <p>Construct a new RX SMS packet with data.</p>
	 */
	@Test
	public final void testCreateRXSMSPacketValidDataNotNull() {
		// Set up the resources for the test.
		int expectedLength = 1 /* Frame type */ + 20 /* phone number */ + data.length();

		// Call the method under test.
		RXSMSPacket packet = new RXSMSPacket(phoneNumber, data);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned phone number is not the expected one", packet.getPhoneNumberByteArray(), is(equalTo(Arrays.copyOf(phoneNumber.getBytes(), 20))));
		assertThat("Returned phone number is not the expected one", packet.getPhoneNumber(), is(equalTo(phoneNumber)));
		assertThat("Returned data is not the expected one", packet.getData(), is(data));
		assertThat("RX SMS packet needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(false)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.cellular.RXSMSPacket#getAPIData()}.
	 *
	 * <p>Test the get API parameters with a {@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIDataReceivedDataNull() {
		// Set up the resources for the test.
		data = null;

		RXSMSPacket packet = new RXSMSPacket(phoneNumber, data);

		int expectedLength = 20 /* phone number */;
		byte[] expectedData = new byte[expectedLength];
		System.arraycopy(Arrays.copyOf(phoneNumber.getBytes(), 20), 0, expectedData, 0, 20);

		// Call the method under test.
		byte[] apiData = packet.getAPIData();

		// Verify the result.
		assertThat("API data is not the expected", apiData, is(equalTo(expectedData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.cellular.RXSMSPacket#getAPIData()}.
	 *
	 * <p>Test the get API parameters with a not-{@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIDataReceivedDataNotNull() {
		// Set up the resources for the test.
		RXSMSPacket packet = new RXSMSPacket(phoneNumber, data);

		int expectedLength = 20 /* phone number */ + data.length();
		byte[] expectedData = new byte[expectedLength];
		System.arraycopy(Arrays.copyOf(phoneNumber.getBytes(), 20), 0, expectedData, 0, 20);
		System.arraycopy(data.getBytes(), 0, expectedData, 20, data.length());

		// Call the method under test.
		byte[] apiData = packet.getAPIData();

		// Verify the result.
		assertThat("API data is not the expected", apiData, is(equalTo(expectedData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.cellular.RXSMSPacket#getAPIPacketParameters()}.
	 *
	 * <p>Test the get API parameters but with a {@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersReceivedDataNull() {
		// Set up the resources for the test.
		data = null;

		RXSMSPacket packet = new RXSMSPacket(phoneNumber, data);

		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();

		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(1)));
		assertThat("Phone number is not the expected", packetParams.get("Phone number"), is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(Arrays.copyOf(phoneNumber.getBytes(), 20))) + " (" + new String(phoneNumber).replaceAll("\0", "") + ")")));
		assertThat("Data is not the expected", packetParams.get("Data"), is(nullValue(String.class)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.cellular.RXSMSPacket#getAPIPacketParameters()}.
	 *
	 * <p>Test the get API parameters but with a not-{@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersReceivedDataNotNull() {
		// Set up the resources for the test.
		RXSMSPacket packet = new RXSMSPacket(phoneNumber, data);

		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();

		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(2)));
		assertThat("Phone number is not the expected", packetParams.get("Phone number"), is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(Arrays.copyOf(phoneNumber.getBytes(), 20))) + " (" + new String(phoneNumber).replaceAll("\0", "") + ")")));
		assertThat("Data is not the expected", packetParams.get("Data"), is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(data.getBytes())) + " (" + data + ")")));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.cellular.RXSMSPacket#isBroadcast()}.
	 *
	 * <p>Test the is broadcast method.</p>
	 */
	@Test
	public final void testIsBroadcast() {
		// Set up the resources for the test.
		RXSMSPacket packet = new RXSMSPacket(phoneNumber, data);

		// Call the method under test and verify the result.
		assertThat("Packet should not be broadcast", packet.isBroadcast(), is(equalTo(false)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.cellular.RXSMSPacket#setPhoneNumber(String))}.
	 *
	 * <p>The call should throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testSetPhoneNumberNull() {
		// Set up the resources for the test.
		String newPhoneNumber = null;

		RXSMSPacket packet = new RXSMSPacket(phoneNumber, data);

		assertThat("Phone number is not the expected one", packet.getPhoneNumberByteArray(), is(equalTo(Arrays.copyOf(phoneNumber.getBytes(), 20))));
		assertThat("Phone number is not the expected one", packet.getPhoneNumber(), is(equalTo(phoneNumber)));

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Phone number cannot be null.")));

		// Call the method under test.
		packet.setPhoneNumber(newPhoneNumber);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.cellular.RXSMSPacket#setPhoneNumber(String))}.
	 *
	 * <p>The call should throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testSetPhoneNumberInvalidLength() {
		// Set up the resources for the test.
		String newPhoneNumber = "555555555555555555555";

		RXSMSPacket packet = new RXSMSPacket(phoneNumber, data);

		assertThat("Phone number is not the expected one", packet.getPhoneNumberByteArray(), is(equalTo(Arrays.copyOf(phoneNumber.getBytes(), 20))));
		assertThat("Phone number is not the expected one", packet.getPhoneNumber(), is(equalTo(phoneNumber)));

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Phone number length cannot be greater than 20 bytes.")));

		// Call the method under test.
		packet.setPhoneNumber(newPhoneNumber);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.cellular.RXSMSPacket#setPhoneNumber(String))}.
	 *
	 * <p>The call should throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testSetPhoneNumberInvalid() {
		// Set up the resources for the test.
		String newPhoneNumber = "5aw34";

		RXSMSPacket packet = new RXSMSPacket(phoneNumber, data);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Phone number invalid, only numbers and '+' prefix allowed.")));

		// Call the method under test.
		packet.setPhoneNumber(newPhoneNumber);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.cellular.RXSMSPacket#setPhoneNumber(String))}.
	 */
	@Test
	public final void testSetPhoneNumberValid() {
		// Set up the resources for the test.
		String newPhoneNumber = "+34622000349";

		RXSMSPacket packet = new RXSMSPacket(phoneNumber, data);

		// Call the method under test.
		packet.setPhoneNumber(newPhoneNumber);

		// Verify the result.
		assertThat("New phone number is not the expected one", packet.getPhoneNumberByteArray(), is(equalTo(Arrays.copyOf(newPhoneNumber.getBytes(), 20))));
		assertThat("New phone number is not the expected one", packet.getPhoneNumber(), is(equalTo(newPhoneNumber)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.cellular.RXSMSPacket#getData())}.
	 */
	@Test
	public final void testGetDataNullData() {
		// Set up the resources for the test.
		data = null;

		RXSMSPacket packet = new RXSMSPacket(phoneNumber, data);

		// Call the method under test.
		String result = packet.getData();

		// Verify the result.
		assertThat("Data must be the same", result, is(equalTo(data)));
		assertThat("Data must be null", result, is(nullValue(String.class)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.cellular.RXSMSPacket#getData())}.
	 */
	@Test
	public final void testGetDataValidData() {
		// Set up the resources for the test.
		RXSMSPacket packet = new RXSMSPacket(phoneNumber, data);

		// Call the method under test.
		String result = packet.getData();

		// Verify the result.
		assertThat("Data must be the same", result, is(equalTo(data)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.cellular.RXSMSPacket#setData(byte[])}.
	 */
	@Test
	public final void testSetDataNullData() {
		// Set up the resources for the test.
		String newData = null;

		RXSMSPacket packet = new RXSMSPacket(phoneNumber, data);

		// Call the method under test.
		packet.setData(newData);

		String result = packet.getData();

		// Verify the result.
		assertThat("Data must be the same", result, is(equalTo(newData)));
		assertThat("Data must be null", result, is(nullValue(String.class)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.cellular.RXSMSPacket#setData(byte[])}.
	 */
	@Test
	public final void testSetDataValidData() {
		// Set up the resources for the test.
		String newData = "New data";

		RXSMSPacket packet = new RXSMSPacket(phoneNumber, data);

		// Call the method under test.
		packet.setData(newData);

		String result = packet.getData();

		// Verify the result.
		assertThat("Data must be the same", result, is(equalTo(newData)));
	}
}
