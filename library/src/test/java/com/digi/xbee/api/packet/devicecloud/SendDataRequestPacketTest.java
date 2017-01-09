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

import com.digi.xbee.api.models.SendDataRequestOptions;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

public class SendDataRequestPacketTest {

	private int frameType = APIFrameType.SEND_DATA_REQUEST.getValue();
	private int frameID = 0x01;
	private String path = "test.txt";
	private String contentType = "text/plain";
	private int transport = 0x00;
	private SendDataRequestOptions options = SendDataRequestOptions.APPEND;
	private byte[] data = "Test".getBytes();

	@Rule
	public ExpectedException exception = ExpectedException.none();

	public SendDataRequestPacketTest() {
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
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#createPacket(byte[])}.
	 *
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Set up the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Send Data Request packet payload cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		SendDataRequestPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Set up the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Send Data Request packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		SendDataRequestPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Set up the resources for the test.
		byte[] payload = new byte[5];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		payload[2] = (byte)0x00;
		payload[3] = (byte)0x00;
		payload[4] = (byte)transport;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Send Data Request packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		SendDataRequestPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array not including the Frame type.</p>
	 */
	@Test
	public final void testCreatePacketPayloadNotIncludingFrameType() {
		// Set up the resources for the test.
		byte[] payload = new byte[5 + path.length() + contentType.length() + data.length];
		payload[0] = (byte)frameID;
		payload[1] = (byte)path.length();
		System.arraycopy(path.getBytes(), 0, payload, 2, path.length());
		payload[2 + path.length()] = (byte)contentType.length();
		System.arraycopy(contentType.getBytes(), 0, payload, 3 + path.length(), contentType.length());
		payload[3 + path.length() + contentType.length()] = (byte)transport;
		payload[4 + path.length() + contentType.length()] = (byte)options.getID();
		System.arraycopy(data, 0, payload, 5 + path.length() + contentType.length(), data.length);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not a Send Data Request packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		SendDataRequestPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#createPacket(byte[])}.
	 *
	 * <p>A valid API Send Data Request packet with the provided options
	 * but without path is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutPath() {
		// Set up the resources for the test.
		byte[] payload = new byte[6 + contentType.length() + data.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		payload[2] = (byte)0x00;
		payload[3] = (byte)contentType.length();
		System.arraycopy(contentType.getBytes(), 0, payload, 4, contentType.length());
		payload[4 + contentType.length()] = (byte)transport;
		payload[5 + contentType.length()] = (byte)options.getID();
		System.arraycopy(data, 0, payload, 6 + contentType.length(), data.length);

		// Call the method under test.
		SendDataRequestPacket packet = SendDataRequestPacket.createPacket(payload);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned path is not the expected one", packet.getPath(), is(nullValue()));
		assertThat("Returned content type is not the expected one", packet.getContentType(), is(equalTo(contentType)));
		assertThat("Returned transport is not the expected one", packet.getTransport(), is(equalTo(transport)));
		assertThat("Returned options is not the expected one", packet.getOptions(), is(equalTo(options)));
		assertThat("Returned data is not the expected one", packet.getFileData(), is(equalTo(data)));

		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#createPacket(byte[])}.
	 *
	 * <p>A valid API Send Data Request packet with the provided options
	 * but without content type is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutContentType() {
		// Set up the resources for the test.
		byte[] payload = new byte[6 + path.length() + data.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		payload[2] = (byte)path.length();
		System.arraycopy(path.getBytes(), 0, payload, 3, path.length());
		payload[3 + path.length()] = (byte)0x00;
		payload[4 + path.length()] = (byte)transport;
		payload[5 + path.length()] = (byte)options.getID();
		System.arraycopy(data, 0, payload, 6 + path.length(), data.length);

		// Call the method under test.
		SendDataRequestPacket packet = SendDataRequestPacket.createPacket(payload);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned path is not the expected one", packet.getPath(), is(equalTo(path)));
		assertThat("Returned content type is not the expected one", packet.getContentType(), is(nullValue()));
		assertThat("Returned transport is not the expected one", packet.getTransport(), is(equalTo(transport)));
		assertThat("Returned options is not the expected one", packet.getOptions(), is(equalTo(options)));
		assertThat("Returned data is not the expected one", packet.getFileData(), is(equalTo(data)));

		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#createPacket(byte[])}.
	 *
	 * <p>A valid API Send Data Request packet with the provided options without data
	 * is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutData() {
		// Set up the resources for the test.
		byte[] payload = new byte[6 + path.length() + contentType.length()];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		payload[2] = (byte)path.length();
		System.arraycopy(path.getBytes(), 0, payload, 3, path.length());
		payload[3 + path.length()] = (byte)contentType.length();
		System.arraycopy(contentType.getBytes(), 0, payload, 4 + path.length(), contentType.length());
		payload[4 + path.length() + contentType.length()] = (byte)transport;
		payload[5 + path.length() + contentType.length()] = (byte)options.getID();

		// Call the method under test.
		SendDataRequestPacket packet = SendDataRequestPacket.createPacket(payload);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned path is not the expected one", packet.getPath(), is(equalTo(path)));
		assertThat("Returned content type is not the expected one", packet.getContentType(), is(equalTo(contentType)));
		assertThat("Returned transport is not the expected one", packet.getTransport(), is(equalTo(transport)));
		assertThat("Returned options is not the expected one", packet.getOptions(), is(equalTo(options)));
		assertThat("Returned data is not the expected one", packet.getFileData(), is(nullValue()));

		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#createPacket(byte[])}.
	 *
	 * <p>A valid API Send Data Request packet with the provided options and data is
	 * created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithData() {
		// Set up the resources for the test.
		byte[] payload = new byte[6 + path.length() + contentType.length() + data.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		payload[2] = (byte)path.length();
		System.arraycopy(path.getBytes(), 0, payload, 3, path.length());
		payload[3 + path.length()] = (byte)contentType.length();
		System.arraycopy(contentType.getBytes(), 0, payload, 4 + path.length(), contentType.length());
		payload[4 + path.length() + contentType.length()] = (byte)transport;
		payload[5 + path.length() + contentType.length()] = (byte)options.getID();
		System.arraycopy(data, 0, payload, 6 + path.length() + contentType.length(), data.length);

		// Call the method under test.
		SendDataRequestPacket packet = SendDataRequestPacket.createPacket(payload);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned path is not the expected one", packet.getPath(), is(equalTo(path)));
		assertThat("Returned content type is not the expected one", packet.getContentType(), is(equalTo(contentType)));
		assertThat("Returned transport is not the expected one", packet.getTransport(), is(equalTo(transport)));
		assertThat("Returned options is not the expected one", packet.getOptions(), is(equalTo(options)));
		assertThat("Returned data is not the expected one", packet.getFileData(), is(equalTo(data)));

		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#SendDataRequestPacket(int, String, String, SendDataRequestOptions, byte[])}.
	 *
	 * <p>Construct a new Send Data Request packet with a frame ID bigger than 255.
	 * This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateSendDataRequestPacketFrameIDBiggerThan255() {
		// Set up the resources for the test.
		frameID = 256;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new SendDataRequestPacket(frameID, path, contentType, options, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#SendDataRequestPacket(int, String, String, SendDataRequestOptions, byte[])}.
	 *
	 * <p>Construct a new Send Data Request packet with a negative frame ID. This
	 * must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateSendDataRequestPacketFrameIDNegative() {
		// Set up the resources for the test.
		frameID = -1;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new SendDataRequestPacket(frameID, path, contentType, options, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#SendDataRequestPacket(int, String, String, SendDataRequestOptions, byte[])}.
	 *
	 * <p>Construct a new Send Data Request packet with {@code null} options. This
	 * must throw an {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateSendDataRequestPacketOptionsNull() {
		// Set up the resources for the test.
		options = null;

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Options cannot be null.")));

		// Call the method under test that should throw an NullPointerException.
		new SendDataRequestPacket(frameID, path, contentType, options, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#SendDataRequestPacket(int, String, String, SendDataRequestOptions, byte[])}.
	 *
	 * <p>Construct a new Send Data Request packet without path ({@code null}).</p>
	 */
	@Test
	public final void testCreateSendDataRequestPacketPathNull() {
		// Set up the resources for the test.
		path = null;

		int expectedLength = 6 + contentType.length() + data.length;

		// Call the method under test.
		SendDataRequestPacket packet = new SendDataRequestPacket(frameID, path, contentType, options, data);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned path is not the expected one", packet.getPath(), is(nullValue()));
		assertThat("Returned content type is not the expected one", packet.getContentType(), is(equalTo(contentType)));
		assertThat("Returned transport is not the expected one", packet.getTransport(), is(equalTo(transport)));
		assertThat("Returned options is not the expected one", packet.getOptions(), is(equalTo(options)));
		assertThat("Returned data is not the expected one", packet.getFileData(), is(equalTo(data)));
		assertThat("Send Data Request packet needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#SendDataRequestPacket(int, String, String, SendDataRequestOptions, byte[])}.
	 *
	 * <p>Construct a new Send Data Request packet without content type ({@code null}).</p>
	 */
	@Test
	public final void testCreateSendDataRequestPacketContentTypeNull() {
		// Set up the resources for the test.
		contentType = null;

		int expectedLength = 6 + path.length() + data.length;

		// Call the method under test.
		SendDataRequestPacket packet = new SendDataRequestPacket(frameID, path, contentType, options, data);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned path is not the expected one", packet.getPath(), is(equalTo(path)));
		assertThat("Returned content type is not the expected one", packet.getContentType(), is(nullValue()));
		assertThat("Returned transport is not the expected one", packet.getTransport(), is(equalTo(transport)));
		assertThat("Returned options is not the expected one", packet.getOptions(), is(equalTo(options)));
		assertThat("Returned data is not the expected one", packet.getFileData(), is(equalTo(data)));
		assertThat("Send Data Request packet needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#SendDataRequestPacket(int, String, String, SendDataRequestOptions, byte[])}.
	 *
	 * <p>Construct a new Send Data Request packet without data ({@code null}).</p>
	 */
	@Test
	public final void testCreateSendDataRequestPacketDataNull() {
		// Set up the resources for the test.
		data = null;

		int expectedLength = 6 + contentType.length() + path.length();

		// Call the method under test.
		SendDataRequestPacket packet = new SendDataRequestPacket(frameID, path, contentType, options, data);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned path is not the expected one", packet.getPath(), is(equalTo(path)));
		assertThat("Returned content type is not the expected one", packet.getContentType(), is(equalTo(contentType)));
		assertThat("Returned transport is not the expected one", packet.getTransport(), is(equalTo(transport)));
		assertThat("Returned options is not the expected one", packet.getOptions(), is(equalTo(options)));
		assertThat("Returned data is not the expected one", packet.getFileData(), is(nullValue()));
		assertThat("Send Data Request packet needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#SendDataRequestPacket(int, String, String, SendDataRequestOptions, byte[])}.
	 *
	 * <p>Construct a new Send Data Request packet.</p>
	 */
	@Test
	public final void testCreateSendDataRequestPacket() {
		// Set up the resources for the test.
		int expectedLength = 6 + contentType.length() + path.length() + data.length;

		// Call the method under test.
		SendDataRequestPacket packet = new SendDataRequestPacket(frameID, path, contentType, options, data);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned path is not the expected one", packet.getPath(), is(equalTo(path)));
		assertThat("Returned content type is not the expected one", packet.getContentType(), is(equalTo(contentType)));
		assertThat("Returned transport is not the expected one", packet.getTransport(), is(equalTo(transport)));
		assertThat("Returned options is not the expected one", packet.getOptions(), is(equalTo(options)));
		assertThat("Returned data is not the expected one", packet.getFileData(), is(equalTo(data)));
		assertThat("Send Data Request packet needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#getAPIData()}.
	 *
	 * <p>Test the get API parameters with a {@code null} target.</p>
	 */
	@Test
	public final void testGetAPIDataPathNull() {
		// Set up the resources for the test.
		path = null;

		SendDataRequestPacket packet = new SendDataRequestPacket(frameID, path, contentType, options, data);

		int expectedLength = 5 + contentType.length() + data.length;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		expectedData[1] = (byte)0x00;
		expectedData[2] = (byte)contentType.length();
		System.arraycopy(contentType.getBytes(), 0, expectedData, 3, contentType.length());
		expectedData[3 + contentType.length()] = (byte)transport;
		expectedData[4 + contentType.length()] = (byte)options.getID();
		System.arraycopy(data, 0, expectedData, 5 + contentType.length(), data.length);

		// Call the method under test.
		byte[] apiData = packet.getAPIData();

		// Verify the result.
		assertThat("API data is not the expected", apiData, is(equalTo(expectedData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#getAPIData()}.
	 *
	 * <p>Test the get API parameters with a {@code null} content type.</p>
	 */
	@Test
	public final void testGetAPIDataContentTypeNull() {
		// Set up the resources for the test.
		contentType = null;

		SendDataRequestPacket packet = new SendDataRequestPacket(frameID, path, contentType, options, data);

		int expectedLength = 5 + path.length() + data.length;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		expectedData[1] = (byte)path.length();
		System.arraycopy(path.getBytes(), 0, expectedData, 2, path.length());
		expectedData[2 + path.length()] = (byte)0x00;
		expectedData[3 + path.length()] = (byte)transport;
		expectedData[4 + path.length()] = (byte)options.getID();
		System.arraycopy(data, 0, expectedData, 5 + path.length(), data.length);

		// Call the method under test.
		byte[] apiData = packet.getAPIData();

		// Verify the result.
		assertThat("API data is not the expected", apiData, is(equalTo(expectedData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#getAPIData()}.
	 *
	 * <p>Test the get API parameters with a {@code null} data.</p>
	 */
	@Test
	public final void testGetAPIDataDataNull() {
		// Set up the resources for the test.
		data = null;

		SendDataRequestPacket packet = new SendDataRequestPacket(frameID, path, contentType, options, data);

		int expectedLength = 5 + path.length() + contentType.length();
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		expectedData[1] = (byte)path.length();
		System.arraycopy(path.getBytes(), 0, expectedData, 2, path.length());
		expectedData[2 + path.length()] = (byte)contentType.length();
		System.arraycopy(contentType.getBytes(), 0, expectedData, 3 + path.length(), contentType.length());
		expectedData[3 + path.length() + contentType.length()] = (byte)transport;
		expectedData[4 + path.length() + contentType.length()] = (byte)options.getID();

		// Call the method under test.
		byte[] apiData = packet.getAPIData();

		// Verify the result.
		assertThat("API data is not the expected", apiData, is(equalTo(expectedData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#getAPIData()}.
	 *
	 * <p>Test the get API parameters.</p>
	 */
	@Test
	public final void testGetAPIData() {
		// Set up the resources for the test.
		SendDataRequestPacket packet = new SendDataRequestPacket(frameID, path, contentType, options, data);

		int expectedLength = 5 + path.length() + contentType.length() + data.length;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		expectedData[1] = (byte)path.length();
		System.arraycopy(path.getBytes(), 0, expectedData, 2, path.length());
		expectedData[2 + path.length()] = (byte)contentType.length();
		System.arraycopy(contentType.getBytes(), 0, expectedData, 3 + path.length(), contentType.length());
		expectedData[3 + path.length() + contentType.length()] = (byte)transport;
		expectedData[4 + path.length() + contentType.length()] = (byte)options.getID();
		System.arraycopy(data, 0, expectedData, 5 + path.length() + contentType.length(), data.length);

		// Call the method under test.
		byte[] apiData = packet.getAPIData();

		// Verify the result.
		assertThat("API data is not the expected", apiData, is(equalTo(expectedData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#getAPIPacketParameters()}.
	 *
	 * <p>Test the get API parameters but with a {@code null} path.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersPathNull() {
		// Set up the resources for the test.
		path = null;

		SendDataRequestPacket packet = new SendDataRequestPacket(frameID, path, contentType, options, data);

		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();

		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(6)));
		assertThat("Returned path length is not the expected one", packetParams.get("Path length"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(0x00, 1)) + " (0)")));
		assertThat("Returned content type length is not the expected one", packetParams.get("Content Type length"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(contentType.length(), 1)) + " (" + contentType.length() + ")")));
		assertThat("Returned content type is not the expected one", packetParams.get("Content Type"), is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(contentType.getBytes())) + " (" + contentType + ")")));
		assertThat("Returned transport is not the expected one", packetParams.get("Transport"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(transport, 1)))));
		assertThat("Returned options are not the expected one", packetParams.get("Options"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(options.getID(), 1)) + " (" + options.getName() + ")")));
		assertThat("Returned data is not the expected one", packetParams.get("Data"), is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(data)))));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#getAPIPacketParameters()}.
	 *
	 * <p>Test the get API parameters but with a {@code null} content type.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersContentTypeNull() {
		// Set up the resources for the test.
		contentType = null;

		SendDataRequestPacket packet = new SendDataRequestPacket(frameID, path, contentType, options, data);

		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();

		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(6)));
		assertThat("Returned path length is not the expected one", packetParams.get("Path length"), is(HexUtils.prettyHexString(HexUtils.integerToHexString(path.length(), 1)) + " (" + path.length() + ")"));
		assertThat("Returned path is not the expected one", packetParams.get("Path"), is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(path.getBytes())) + " (" + path + ")")));
		assertThat("Returned content type length is not the expected one", packetParams.get("Content Type length"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(0x00, 1)) + " (0)")));
		assertThat("Returned transport is not the expected one", packetParams.get("Transport"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(transport, 1)))));
		assertThat("Returned options are not the expected one", packetParams.get("Options"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(options.getID(), 1)) + " (" + options.getName() + ")")));
		assertThat("Returned data is not the expected one", packetParams.get("Data"), is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(data)))));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#getAPIPacketParameters()}.
	 *
	 * <p>Test the get API parameters but with {@code null} data.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersDataNull() {
		// Set up the resources for the test.
		data = null;

		SendDataRequestPacket packet = new SendDataRequestPacket(frameID, path, contentType, options, data);

		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();

		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(6)));
		assertThat("Returned path length is not the expected one", packetParams.get("Path length"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(path.length(), 1)) + " (" + path.length() + ")")));
		assertThat("Returned path is not the expected one", packetParams.get("Path"), is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(path.getBytes())) + " (" + path + ")")));
		assertThat("Returned content type length is not the expected one", packetParams.get("Content Type length"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(contentType.length(), 1)) + " (" + contentType.length() + ")")));
		assertThat("Returned content type is not the expected one", packetParams.get("Content Type"), is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(contentType.getBytes())) + " (" + contentType + ")")));
		assertThat("Returned transport is not the expected one", packetParams.get("Transport"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(transport, 1)))));
		assertThat("Returned options are not the expected one", packetParams.get("Options"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(options.getID(), 1)) + " (" + options.getName() + ")")));
		assertThat("Returned data is not the expected one", packetParams.get("Data"), is(nullValue()));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#getAPIPacketParameters()}.
	 *
	 * <p>Test the get API parameters.</p>
	 */
	@Test
	public final void testGetAPIPacketParameters() {
		// Set up the resources for the test.
		SendDataRequestPacket packet = new SendDataRequestPacket(frameID, path, contentType, options, data);

		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();

		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(7)));
		assertThat("Returned path length is not the expected one", packetParams.get("Path length"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(path.length(), 1)) + " (" + path.length() + ")")));
		assertThat("Returned path is not the expected one", packetParams.get("Path"), is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(path.getBytes())) + " (" + path + ")")));
		assertThat("Returned content type length is not the expected one", packetParams.get("Content Type length"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(contentType.length(), 1)) + " (" + contentType.length() + ")")));
		assertThat("Returned content type is not the expected one", packetParams.get("Content Type"), is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(contentType.getBytes())) + " (" + contentType + ")")));
		assertThat("Returned transport is not the expected one", packetParams.get("Transport"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(transport, 1)))));
		assertThat("Returned options are not the expected one", packetParams.get("Options"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(options.getID(), 1)) + " (" + options.getName() + ")")));
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
		SendDataRequestPacket packet = new SendDataRequestPacket(frameID, path, contentType, options, data);

		// Call the method under test and verify the result.
		assertThat("Packet should not be broadcast", packet.isBroadcast(), is(equalTo(false)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#setPath(String)}.
	 */
	@Test
	public final void testSetPathNull() {
		// Set up the resources for the test.
		SendDataRequestPacket packet = new SendDataRequestPacket(frameID, path, contentType, options, data);

		path = null;

		// Call the method under test.
		packet.setPath(path);

		// Verify the result.
		assertThat("Returned path is not the expected", packet.getPath(), is(nullValue()));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#setPath(String)}.
	 */
	@Test
	public final void testSetPathNotNull() {
		// Set up the resources for the test.
		SendDataRequestPacket packet = new SendDataRequestPacket(frameID, path, contentType, options, data);

		path = "path/a";

		// Call the method under test.
		packet.setPath(path);

		// Verify the result.
		assertThat("Returned path is not the expected", packet.getPath(), is(equalTo(path)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#setContentType(String)}.
	 */
	@Test
	public final void testSetContentTypeNull() {
		// Set up the resources for the test.
		SendDataRequestPacket packet = new SendDataRequestPacket(frameID, path, contentType, options, data);

		contentType = null;

		// Call the method under test.
		packet.setContentType(contentType);

		// Verify the result.
		assertThat("Returned content type is not the expected", packet.getContentType(), is(nullValue()));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#setContentType(String)}.
	 */
	@Test
	public final void testSetContentTypeNotNull() {
		// Set up the resources for the test.
		SendDataRequestPacket packet = new SendDataRequestPacket(frameID, path, contentType, options, data);

		contentType = "text/javascript";

		// Call the method under test.
		packet.setContentType(contentType);

		// Verify the result.
		assertThat("Returned content type is not the expected", packet.getContentType(), is(equalTo(contentType)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#getTransport()}.
	 */
	@Test
	public final void testGetTransport() {
		// Set up the resources for the test.
		SendDataRequestPacket packet = new SendDataRequestPacket(frameID, path, contentType, options, data);

		// Verify the result.
		assertThat("Returned transport is not the expected", packet.getTransport(), is(equalTo(0)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#setOptions(SendDataRequestOptions)}.
	 */
	@Test
	public final void testSetOptionsNull() {
		// Set up the resources for the test.
		SendDataRequestPacket packet = new SendDataRequestPacket(frameID, path, contentType, options, data);

		options = null;

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Options cannot be null.")));

		// Call the method under test that should throw an NullPointerException.
		packet.setOptions(options);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#setOptions(SendDataRequestOptions)}.
	 */
	@Test
	public final void testSetOptionsNotNull() {
		// Set up the resources for the test.
		SendDataRequestPacket packet = new SendDataRequestPacket(frameID, path, contentType, options, data);

		options = SendDataRequestOptions.TRANSIENT;

		// Call the method under test.
		packet.setOptions(options);

		// Verify the result.
		assertThat("Returned options are not the expected", packet.getOptions(), is(equalTo(options)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#setFileData(byte[])}.
	 */
	@Test
	public final void testSetDataNull() {
		// Set up the resources for the test.
		SendDataRequestPacket packet = new SendDataRequestPacket(frameID, path, contentType, options, data);

		data = null;

		// Call the method under test.
		packet.setFileData(data);

		// Verify the result.
		assertThat("Returned data is not the expected", packet.getFileData(), is(nullValue()));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket#setFileData(byte[])}.
	 */
	@Test
	public final void testSetDataNotNull() {
		// Set up the resources for the test.
		SendDataRequestPacket packet = new SendDataRequestPacket(frameID, path, contentType, options, data);

		data = "New data".getBytes();

		// Call the method under test.
		packet.setFileData(data);

		// Verify the result.
		assertThat("Returned data is not the expected", packet.getFileData(), is(equalTo(data)));
	}
}
