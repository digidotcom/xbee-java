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

import com.digi.xbee.api.models.CoAPURI;
import com.digi.xbee.api.models.HTTPMethodEnum;
import com.digi.xbee.api.models.RemoteATCommandOptions;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.thread.CoAPTxRequestPacket;
import com.digi.xbee.api.utils.HexUtils;

@PrepareForTest({Inet6Address.class, CoAPTxRequestPacket.class})
@RunWith(PowerMockRunner.class)
public class CoAPTxRequestPacketTest {

	// Constants.
	private static final String IPV6_ADDRESS = "FDB3:0001:0002:0000:0004:0005:0006:0007";

	// Variables.
	private int frameType = APIFrameType.COAP_TX_REQUEST.getValue();
	private int frameID = 0x01;
	private int options = RemoteATCommandOptions.OPTION_NONE;
	private Inet6Address destAddress;
	private HTTPMethodEnum method = HTTPMethodEnum.GET;
	private String uriData = CoAPURI.URI_DATA_TRANSMISSION;
	private byte[] data = "Test".getBytes();

	@Rule
	public ExpectedException exception = ExpectedException.none();

	public CoAPTxRequestPacketTest() throws Exception {
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
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#createPacket(byte[])}.
	 *
	 * <p>A {@code NullPointerException} exception must be thrown when parsing 
	 * a {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Set up the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("CoAP Tx Request packet payload cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		CoAPTxRequestPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Set up the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete CoAP Tx Request packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		CoAPTxRequestPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Set up the resources for the test.
		byte[] payload = new byte[25];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		payload[2] = (byte)options;
		payload[3] = (byte)method.getValue();
		System.arraycopy(destAddress.getAddress(), 0, payload, 4, destAddress.getAddress().length);
		payload[20] = (byte)(uriData.length());
		System.arraycopy(uriData.getBytes(), 0, payload, 21, uriData.getBytes().length - 1);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete CoAP Tx Request packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		CoAPTxRequestPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array not including the Frame type.</p>
	 */
	@Test
	public final void testCreatePacketPayloadNotIncludingFrameType() {
		// Set up the resources for the test.
		byte[] payload = new byte[25 + data.length];
		payload[0] = (byte)frameID;
		payload[1] = (byte)options;
		payload[2] = (byte)method.getValue();
		System.arraycopy(destAddress.getAddress(), 0, payload, 3, destAddress.getAddress().length);
		payload[20] = (byte)(uriData.length());
		System.arraycopy(uriData.getBytes(), 0, payload, 20, uriData.getBytes().length);
		System.arraycopy(data, 0, payload, 20 + uriData.getBytes().length, data.length);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not a CoAP Tx Request packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		CoAPTxRequestPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array with an invalid IPv6 address.</p>
	 */
	@Test
	public final void testCreatePacketPayloadInvalidIP() throws Exception {
		// Set up the resources for the test.
		byte[] payload = new byte[26];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		payload[2] = (byte)options;
		payload[3] = (byte)method.getValue();
		System.arraycopy(destAddress.getAddress(), 0, payload, 4, destAddress.getAddress().length);
		payload[20] = (byte)(uriData.length());
		System.arraycopy(uriData.getBytes(), 0, payload, 21, uriData.getBytes().length);

		PowerMockito.mockStatic(Inet6Address.class);
		PowerMockito.when(Inet6Address.getByAddress(Mockito.any(byte[].class))).thenThrow(new UnknownHostException());

		exception.expect(IllegalArgumentException.class);

		// Call the method under test that should throw an IllegalArgumentException.
		CoAPTxRequestPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#createPacket(byte[])}.
	 *
	 * <p>A valid CoAP TX Request packet with the provided options and without 
	 * data is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutData() {
		// Set up the resources for the test.
		byte[] payload = new byte[26];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		payload[2] = (byte)options;
		payload[3] = (byte)method.getValue();
		System.arraycopy(destAddress.getAddress(), 0, payload, 4, destAddress.getAddress().length);
		payload[20] = (byte)(uriData.length());
		System.arraycopy(uriData.getBytes(), 0, payload, 21, uriData.getBytes().length);

		// Call the method under test.
		CoAPTxRequestPacket packet = CoAPTxRequestPacket.createPacket(payload);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned options are not the expected ones", packet.getTransmitOptions(), is(equalTo(options)));
		assertThat("Returned RESTul method is not the expected one", packet.getMethod(), is(equalTo(method)));
		assertThat("Returned dest address is not the expected one", packet.getDestAddress(), is(equalTo(destAddress)));
		assertThat("Returned URI is not the expected one", packet.getURI(), is(equalTo(uriData)));
		assertThat("Returned data is not the expected one", packet.getPayload(), is(nullValue()));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#createPacket(byte[])}.
	 *
	 * <p>A valid CoAP TX Request packet with the provided options and data is
	 * created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithData() {
		// Set up the resources for the test.
		byte[] payload = new byte[26 + data.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		payload[2] = (byte)options;
		payload[3] = (byte)method.getValue();
		System.arraycopy(destAddress.getAddress(), 0, payload, 4, destAddress.getAddress().length);
		payload[20] = (byte)(uriData.length());
		System.arraycopy(uriData.getBytes(), 0, payload, 21, uriData.getBytes().length);
		System.arraycopy(data, 0, payload, 21 + uriData.getBytes().length, data.length);

		// Call the method under test.
		CoAPTxRequestPacket packet = CoAPTxRequestPacket.createPacket(payload);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned options are not the expected ones", packet.getTransmitOptions(), is(equalTo(options)));
		assertThat("Returned RESTul method is not the expected one", packet.getMethod(), is(equalTo(method)));
		assertThat("Returned dest address is not the expected one", packet.getDestAddress(), is(equalTo(destAddress)));
		assertThat("Returned URI is not the expected one", packet.getURI(), is(equalTo(uriData)));
		assertThat("Returned data is not the expected one", packet.getPayload(), is(equalTo(data)));

		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#CoAPTxRequestPacket(int, int, HTTPMethodEnum, Inet6Address, String, byte[])}.
	 *
	 * <p>Construct a new CoAP TX Request packet with a frame ID bigger than 
	 * 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateCoAPTxRequestPacketFrameIDBiggerThan255() {
		// Set up the resources for the test.
		int frameID = 524;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new CoAPTxRequestPacket(frameID, options, method, destAddress, uriData, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#CoAPTxRequestPacket(int, int, HTTPMethodEnum, Inet6Address, String, byte[])}.
	 *
	 * <p>Construct a new CoAP TX Request packet with a negative frame ID. This
	 * must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateCoAPTxRequestPacketFrameIDNegative() {
		// Set up the resources for the test.
		int frameID = -6;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new CoAPTxRequestPacket(frameID, options, method, destAddress, uriData, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#CoAPTxRequestPacket(int, int, HTTPMethodEnum, Inet6Address, String, byte[])}.
	 *
	 * <p>Construct a new CoAP TX Request packet with invalid transmit options. 
	 * This must throw a {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateCoAPTxRequestPacketTransmitOptionsInvalid() {
		// Set up the resources for the test.
		int options = -1;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Transmit options can only be " +
				RemoteATCommandOptions.OPTION_NONE +
				" or " + RemoteATCommandOptions.OPTION_APPLY_CHANGES + ".")));

		// Call the method under test that should throw a NullPointerException.
		new CoAPTxRequestPacket(frameID, options, method, destAddress, uriData, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#CoAPTxRequestPacket(int, int, HTTPMethodEnum, Inet6Address, String, byte[])}.
	 *
	 * <p>Construct a new CoAP TX Request packet with a null RESTful method. 
	 * This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateCoAPTxRequestPacketMethodNull() {
		// Set up the resources for the test.
		HTTPMethodEnum method = null;

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("HTTP Method cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		new CoAPTxRequestPacket(frameID, options, method, destAddress, uriData, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#CoAPTxRequestPacket(int, int, HTTPMethodEnum, Inet6Address, String, byte[])}.
	 *
	 * <p>Construct a new CoAP TX Request packet with a null destination 
	 * address. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateCoAPTxRequestPacketDestAddressNull() {
		// Set up the resources for the test.
		Inet6Address destAddress = null;

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Destination address cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		new CoAPTxRequestPacket(frameID, options, method, destAddress, uriData, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#CoAPTxRequestPacket(int, int, HTTPMethodEnum, Inet6Address, String, byte[])}.
	 *
	 * <p>Construct a new CoAP TX Request packet with a null URI. This must 
	 * throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateCoAPTxRequestPacketURINull() {
		// Set up the resources for the test.
		String uriData = null;

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("URI cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		new CoAPTxRequestPacket(frameID, options, method, destAddress, uriData, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#CoAPTxRequestPacket(int, int, HTTPMethodEnum, Inet6Address, String, byte[])}.
	 *
	 * <p>Construct a new CoAP TX Request packet without data ({@code null}).</p>
	 */
	@Test
	public final void testCreateCoAPTxRequestPacketValidDataNull() {
		// Set up the resources for the test.
		data = null;

		int expectedLength = 26;

		// Call the method under test.
		CoAPTxRequestPacket packet = new CoAPTxRequestPacket(frameID, options, method, destAddress, uriData, data);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned options are not the expected ones", packet.getTransmitOptions(), is(equalTo(options)));
		assertThat("Returned RESTul method is not the expected one", packet.getMethod(), is(equalTo(method)));
		assertThat("Returned dest address is not the expected one", packet.getDestAddress(), is(equalTo(destAddress)));
		assertThat("Returned URI is not the expected one", packet.getURI(), is(equalTo(uriData)));
		assertThat("Returned data is not the expected one", packet.getPayload(), is(nullValue()));
		assertThat("CoAP TX Request packet needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#CoAPTxRequestPacket(int, int, HTTPMethodEnum, Inet6Address, String, byte[])}.
	 *
	 * <p>Construct a new CoAP TX Request packet with data.</p>
	 */
	@Test
	public final void testCreateCoAPTxRequestPacketValidDataNotNull() {
		// Set up the resources for the test.
		int expectedLength = 26 + data.length;

		// Call the method under test.
		CoAPTxRequestPacket packet = new CoAPTxRequestPacket(frameID, options, method, destAddress, uriData, data);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned options are not the expected ones", packet.getTransmitOptions(), is(equalTo(options)));
		assertThat("Returned RESTul method is not the expected one", packet.getMethod(), is(equalTo(method)));
		assertThat("Returned dest address is not the expected one", packet.getDestAddress(), is(equalTo(destAddress)));
		assertThat("Returned URI is not the expected one", packet.getURI(), is(equalTo(uriData)));
		assertThat("Returned data is not the expected one", packet.getPayload(), is(data));
		assertThat("CoAP TX Request packet needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#getAPIData()}.
	 *
	 * <p>Test the get API parameters with a {@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIDataReceivedDataNull() {
		// Set up the resources for the test.
		byte[] data = null;

		CoAPTxRequestPacket packet = new CoAPTxRequestPacket(frameID, options, method, destAddress, uriData, data);

		int expectedLength = 25;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		expectedData[1] = (byte)options;
		expectedData[2] = (byte)method.getValue();
		System.arraycopy(destAddress.getAddress(), 0, expectedData, 3, destAddress.getAddress().length);
		expectedData[19] = (byte)(uriData.length());
		System.arraycopy(uriData.getBytes(), 0, expectedData, 20, uriData.getBytes().length);

		// Call the method under test.
		byte[] apiData = packet.getAPIData();

		// Verify the result.
		assertThat("API data is not the expected", apiData, is(equalTo(expectedData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#getAPIData()}.
	 *
	 * <p>Test the get API parameters with a not-{@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIDataReceivedDataNotNull() {
		// Set up the resources for the test.
		CoAPTxRequestPacket packet = new CoAPTxRequestPacket(frameID, options, method, destAddress, uriData, data);

		int expectedLength = 25 + data.length;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		expectedData[1] = (byte)options;
		expectedData[2] = (byte)method.getValue();
		System.arraycopy(destAddress.getAddress(), 0, expectedData, 3, destAddress.getAddress().length);
		expectedData[19] = (byte)(uriData.length());
		System.arraycopy(uriData.getBytes(), 0, expectedData, 20, uriData.getBytes().length);
		System.arraycopy(data, 0, expectedData, 20 + uriData.getBytes().length, data.length);

		// Call the method under test.
		byte[] apiData = packet.getAPIData();

		// Verify the result.
		assertThat("API data is not the expected", apiData, is(equalTo(expectedData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#getAPIPacketParameters()}.
	 *
	 * <p>Test the get API parameters with a {@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersReceivedDataNull() {
		// Set up the resources for the test.
		byte[] data = null;

		CoAPTxRequestPacket packet = new CoAPTxRequestPacket(frameID, options, method, destAddress, uriData, data);

		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();

		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(6)));
		assertThat("Returned transmit options are not the expected ones", packetParams.get("Options"), 
				is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(options, 1)))));
		assertThat("Returned HTTP method is not the expected one", packetParams.get("Method"), 
				is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(method.getValue(), 1)) + " (" + method.getName() + ")")));
		assertThat("Returned dest address is not the expected one", packetParams.get("Destination address"), 
				is(equalTo(HexUtils.prettyHexString(destAddress.getAddress()) + " (" + destAddress.getHostAddress() + ")")));
		assertThat("Returned URI length is not the expected one", packetParams.get("URI length"), 
				is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(uriData.length(), 1)) + " (" + uriData.length() + ")")));
		assertThat("Returned URI is not the expected one", packetParams.get("URI"), 
				is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(uriData.getBytes())) + " (" + uriData + ")")));
		assertThat("RF data is not the expected", packetParams.get("RF data"), is(nullValue(String.class)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#getAPIPacketParameters()}.
	 *
	 * <p>Test the get API parameters with a not-{@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersReceivedDataNotNull() {
		// Set up the resources for the test.
		CoAPTxRequestPacket packet = new CoAPTxRequestPacket(frameID, options, method, destAddress, uriData, data);

		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();

		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(7)));
		assertThat("Returned transmit options are not the expected ones", packetParams.get("Options"), 
				is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(options, 1)))));
		assertThat("Returned HTTP method is not the expected one", packetParams.get("Method"), 
				is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(method.getValue(), 1)) + " (" + method.getName() + ")")));
		assertThat("Returned dest address is not the expected one", packetParams.get("Destination address"), 
				is(equalTo(HexUtils.prettyHexString(destAddress.getAddress()) + " (" + destAddress.getHostAddress() + ")")));
		assertThat("Returned URI length is not the expected one", packetParams.get("URI length"), 
				is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(uriData.length(), 1)) + " (" + uriData.length() + ")")));
		assertThat("Returned URI is not the expected one", packetParams.get("URI"), 
				is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(uriData.getBytes())) + " (" + uriData + ")")));
		assertThat("RF data is not the expected", packetParams.get("Payload"), 
				is(equalTo(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(data)))));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#setDestAddress(Inet6Address)}.
	 */
	@Test
	public final void testSetDestAddressNull() {
		// Set up the resources for the test.
		CoAPTxRequestPacket packet = new CoAPTxRequestPacket(frameID, options, method, destAddress, uriData, data);

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Destination address cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		packet.setDestAddress(null);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#setDestAddress(Inet6Address)}.
	 *
	 * @throws Exception
	 */
	@Test
	public final void testSetDestAddressNotNull() throws Exception {
		// Set up the resources for the test.
		CoAPTxRequestPacket packet = new CoAPTxRequestPacket(frameID, options, method, destAddress, uriData, data);

		Inet6Address newAddress = (Inet6Address) Inet6Address.getByName("fd8a:cb11:ad71:0000:7662:c401:5efe:dc41");

		// Call the method under test.
		packet.setDestAddress(newAddress);

		// Verify the result.
		assertThat("Dest address is not the expected one", packet.getDestAddress(), is(equalTo(newAddress)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#setTransmitOptions(int)}.
	 */
	@Test
	public final void testSetTransmitOptionsATURIOptionsIllegal() {
		// Set up the resources for the test.
		CoAPTxRequestPacket packet = new CoAPTxRequestPacket(frameID, options, method, destAddress, CoAPURI.URI_AT_COMMAND, data);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Transmit options can only be " +
				RemoteATCommandOptions.OPTION_NONE +
				" or " + RemoteATCommandOptions.OPTION_APPLY_CHANGES + ".")));

		// Call the method under test that should throw an IllegalArgumentException.
		packet.setTransmitOptions(0x03);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#setTransmitOptions(int)}.
	 */
	@Test
	public final void testSetTransmitOptionsTXURIOptionsIllegal() {
		// Set up the resources for the test.
		CoAPTxRequestPacket packet = new CoAPTxRequestPacket(frameID, options, method, destAddress, CoAPURI.URI_DATA_TRANSMISSION, data);

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Transmit options can only be " +
				RemoteATCommandOptions.OPTION_NONE +
				" or " + RemoteATCommandOptions.OPTION_APPLY_CHANGES + ".")));

		// Call the method under test that should throw an IllegalArgumentException.
		packet.setTransmitOptions(0x02);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#setTransmitOptions(int)}.
	 */
	@Test
	public final void testTransmitOptionsValid() {
		// Set up the resources for the test.
		CoAPTxRequestPacket packet = new CoAPTxRequestPacket(frameID, options, method, destAddress, uriData, data);

		int newOptions = 0x00;

		// Call the method under test.
		packet.setTransmitOptions(newOptions);

		// Verify the result.
		assertThat("Transmit options are not the expected ones", packet.getTransmitOptions(), is(equalTo(newOptions)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#setMethod(HTTPMethodEnum)}.
	 */
	@Test
	public final void testSetMEthodNull() {
		// Set up the resources for the test.
		CoAPTxRequestPacket packet = new CoAPTxRequestPacket(frameID, options, method, destAddress, uriData, data);

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("HTTP Method cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		packet.setMethod(null);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#setMethod(HTTPMethodEnum)}.
	 */
	@Test
	public final void testSetMethodNotNull() {
		// Set up the resources for the test.
		CoAPTxRequestPacket packet = new CoAPTxRequestPacket(frameID, options, method, destAddress, uriData, data);

		HTTPMethodEnum newMethod = HTTPMethodEnum.PUT;

		// Call the method under test.
		packet.setMethod(newMethod);

		// Verify the result.
		assertThat("HTTP method is not the expected one", packet.getMethod(), is(equalTo(newMethod)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#getPayload()}.
	 */
	@Test
	public final void testGetDataNullData() {
		// Set up the resources for the test.
		byte[] data = null;

		CoAPTxRequestPacket packet = new CoAPTxRequestPacket(frameID, options, method, destAddress, uriData, data);

		// Call the method under test.
		byte[] result = packet.getPayload();

		// Verify the result.
		assertThat("RF data must be the same", result, is(equalTo(data)));
		assertThat("RF data must be null", result, is(nullValue(byte[].class)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#getPayload()}.
	 */
	@Test
	public final void testGetDataValidData() {
		// Set up the resources for the test.
		CoAPTxRequestPacket packet = new CoAPTxRequestPacket(frameID, options, method, destAddress, uriData, data);

		// Call the method under test.
		byte[] result = packet.getPayload();

		// Verify the result.
		assertThat("Data must be the same", result, is(equalTo(data)));
		assertThat("Data must not be the same object", result.hashCode(), is(not(equalTo(data.hashCode()))));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#setPayload(byte[])}.
	 */
	@Test
	public final void testSetDataNullData() {
		// Set up the resources for the test.
		byte[] newData = null;

		CoAPTxRequestPacket packet = new CoAPTxRequestPacket(frameID, options, method, destAddress, uriData, data);

		// Call the method under test.
		packet.setPayload(newData);

		byte[] result = packet.getPayload();

		// Verify the result.
		assertThat("Data must be the same", result, is(equalTo(newData)));
		assertThat("Data must be null", result, is(nullValue(byte[].class)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#setPayload(byte[])}.
	 */
	@Test
	public final void testSetDataValidData() {
		// Set up the resources for the test.
		byte[] newData = "New data".getBytes();

		CoAPTxRequestPacket packet = new CoAPTxRequestPacket(frameID, options, method, destAddress, uriData, data);

		// Call the method under test.
		packet.setPayload(newData);

		byte[] result = packet.getPayload();

		// Verify the result.
		assertThat("Data must be the same", result, is(equalTo(newData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket#setPayload(byte[])}.
	 */
	@Test
	public final void testSetDataAndModifyOriginal() {
		// Set up the resources for the test.
		byte[] newData = "New data".getBytes();

		CoAPTxRequestPacket packet = new CoAPTxRequestPacket(frameID, options, method, destAddress, uriData, data);

		// Call the method under test.
		packet.setPayload(newData);

		byte[] backup = Arrays.copyOf(newData, newData.length);
		newData[0] = 0x00;

		byte[] result = packet.getPayload();

		// Verify the result.
		assertThat("Data must be the same as the setted data", result, is(equalTo(backup)));
		assertThat("Data must not be the current value of received data", result, is(not(equalTo(data))));
		assertThat("Data must not be the same object", result.hashCode(), is(not(equalTo(backup.hashCode()))));
		assertThat("Data must not be the same object", result.hashCode(), is(not(equalTo(data.hashCode()))));
	}

}
