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
package com.digi.xbee.api.packet.wifi;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

import java.net.Inet4Address;
import java.net.UnknownHostException;
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

import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.models.XBeeReceiveOptions;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

@PrepareForTest({Inet4Address.class, IODataSampleRxIndicatorWifiPacket.class})
@RunWith(PowerMockRunner.class)
public class IODataSampleRxIndicatorWifiPacketTest {

	// Constants.
	private static final String IP_ADDRESS = "10.10.11.12";

	// Variables.
	private int frameType = APIFrameType.IO_DATA_SAMPLE_RX_INDICATOR_WIFI.getValue();
	private Inet4Address sourceAddress;
	private int rssi = 0x2D;
	private int receiveOptions = XBeeReceiveOptions.NONE;
	private int nSamples = 0x01;
	private int digitalMask = 0x0100;
	private int analogMask = 0x81;
	private int digitalSamples = 0x0000;
	private int analogSamples = 0x03B5;
	private byte[] data = new byte[]{(byte)nSamples, (byte)(digitalMask >> 8), (byte)digitalMask,
			(byte)analogMask, (byte)(digitalSamples), (byte)digitalSamples, (byte)(analogSamples >> 8), (byte)analogSamples};
	private IOSample sample = new IOSample(data);

	@Rule
	public ExpectedException exception = ExpectedException.none();

	public IODataSampleRxIndicatorWifiPacketTest() throws Exception {
		sourceAddress = (Inet4Address) Inet4Address.getByName(IP_ADDRESS);
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
	 * Test method for {@link com.digi.xbee.api.packet.wifi.IODataSampleRxIndicatorWifiPacket#createPacket(byte[])}.
	 *
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Set up the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("IO Data Sample Rx Indicator (Wi-Fi) packet payload cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		IODataSampleRxIndicatorWifiPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.IODataSampleRxIndicatorWifiPacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Set up the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete IO Data Sample Rx Indicator (Wi-Fi) packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		IODataSampleRxIndicatorWifiPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.IODataSampleRxIndicatorWifiPacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Set up the resources for the test.
		byte[] payload = new byte[10];
		payload[0] = (byte)frameType;
		System.arraycopy(new byte[]{0x00, 0x00, 0x00, 0x00}, 0, payload, 1, 4);
		System.arraycopy(sourceAddress.getAddress(), 0, payload, 5, sourceAddress.getAddress().length);
		payload[9] = (byte)rssi;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete IO Data Sample Rx Indicator (Wi-Fi) packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		IODataSampleRxIndicatorWifiPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.IODataSampleRxIndicatorWifiPacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array not including the Frame type.</p>
	 */
	@Test
	public final void testCreatePacketPayloadNotIncludingFrameType() {
		// Set up the resources for the test.
		byte[] payload = new byte[11];
		System.arraycopy(new byte[]{0x00, 0x00, 0x00, 0x00}, 0, payload, 0, 4);
		System.arraycopy(sourceAddress.getAddress(), 0, payload, 4, sourceAddress.getAddress().length);
		payload[8] = (byte)rssi;
		payload[9] = (byte)receiveOptions;
		payload[10] = (byte)nSamples;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not a IO Data Sample Rx Indicator (Wi-Fi) packet.")));

		// Call the method under test that should throw an IllegalArgumentException.
		IODataSampleRxIndicatorWifiPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.network.IODataSampleRxIndicatorWifiPacket#createPacket(byte[])}.
	 *
	 * <p>An {@code IllegalArgumentException} exception must be thrown when
	 * parsing a byte array with an invalid IP address.</p>
	 */
	@Test
	public final void testCreatePacketPayloadInvalidIP() throws Exception {
		// Set up the resources for the test.
		byte[] payload = new byte[11];
		payload[0] = (byte)frameType;
		System.arraycopy(new byte[]{0x00, 0x00, 0x00, 0x00}, 0, payload, 1, 4);
		System.arraycopy(sourceAddress.getAddress(), 0, payload, 5, sourceAddress.getAddress().length);
		payload[9] = (byte)rssi;
		payload[10] = (byte)receiveOptions;

		PowerMockito.mockStatic(Inet4Address.class);
		PowerMockito.when(Inet4Address.getByAddress(Mockito.any(byte[].class))).thenThrow(new UnknownHostException());

		exception.expect(IllegalArgumentException.class);

		// Call the method under test that should throw an IllegalArgumentException.
		IODataSampleRxIndicatorWifiPacket.createPacket(payload);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.IODataSampleRxIndicatorWifiPacket#createPacket(byte[])}.
	 *
	 * <p>A valid API IO Data Sample Rx Indicator (Wi-Fi) packet with the provided options without data
	 * is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutData() {
		// Set up the resources for the test.
		byte[] payload = new byte[11];
		payload[0] = (byte)frameType;
		System.arraycopy(new byte[]{0x00, 0x00, 0x00, 0x00}, 0, payload, 1, 4);
		System.arraycopy(sourceAddress.getAddress(), 0, payload, 5, sourceAddress.getAddress().length);
		payload[9] = (byte)rssi;
		payload[10] = (byte)receiveOptions;

		// Call the method under test.
		IODataSampleRxIndicatorWifiPacket packet = IODataSampleRxIndicatorWifiPacket.createPacket(payload);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned source address is not the expected one", packet.getSourceAddress(), is(equalTo(sourceAddress)));
		assertThat("Returned RSSI is not the expected one", packet.getRSSI(), is(equalTo(rssi)));
		assertThat("Returned options are not the expected one", packet.getReceiveOptions(), is(equalTo(receiveOptions)));
		assertThat("Returned data is not the expected one", packet.getRFData(), is(nullValue()));
		assertThat("Returned IO sample is not the expected one", packet.getIOSample(), is(nullValue()));
		assertThat("Packet is broadcast", packet.isBroadcast(), is(equalTo(false)));

		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.IODataSampleRxIndicatorWifiPacket#createPacket(byte[])}.
	 *
	 * <p>A valid API IO Data Sample Rx Indicator (Wi-Fi) packet with the provided options and data is
	 * created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithData() {
		// Set up the resources for the test.
		byte[] payload = new byte[19];
		payload[0] = (byte)frameType;
		System.arraycopy(new byte[]{0x00, 0x00, 0x00, 0x00}, 0, payload, 1, 4);
		System.arraycopy(sourceAddress.getAddress(), 0, payload, 5, sourceAddress.getAddress().length);
		payload[9] = (byte)rssi;
		payload[10] = (byte)receiveOptions;
		payload[11] = (byte)nSamples;
		payload[12] = (byte)(digitalMask >> 8);
		payload[13] = (byte)digitalMask;
		payload[14] = (byte)analogMask;
		payload[15] = (byte)(digitalSamples >> 8);
		payload[16] = (byte)digitalSamples;
		payload[17] = (byte)(analogSamples >> 8);
		payload[18] = (byte)analogSamples;

		// Call the method under test.
		IODataSampleRxIndicatorWifiPacket packet = IODataSampleRxIndicatorWifiPacket.createPacket(payload);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned source address is not the expected one", packet.getSourceAddress(), is(equalTo(sourceAddress)));
		assertThat("Returned RSSI is not the expected one", packet.getRSSI(), is(equalTo(rssi)));
		assertThat("Returned options are not the expected one", packet.getReceiveOptions(), is(equalTo(receiveOptions)));
		assertThat("Returned data is not the expected one", packet.getRFData(), is(equalTo(data)));
		assertThat("Returned IO sample is not the expected one", packet.getIOSample(), is(equalTo(sample)));

		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.IODataSampleRxIndicatorWifiPacket#IODataSampleRxIndicatorWifiPacket(Inet4Address, int, int, byte[])}.
	 *
	 * <p>Construct a new IO Data Sample Rx Indicator (Wi-Fi) packet with a
	 * {@code null} source address.This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateIODataSampleRxIndicatorWifiPacketSourceAddressNull() {
		// Set up the resources for the test.
		sourceAddress = null;

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Source address cannot be null.")));

		// Call the method under test that should throw an NullPointerException.
		new IODataSampleRxIndicatorWifiPacket(sourceAddress, rssi, receiveOptions, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.IODataSampleRxIndicatorWifiPacket#IODataSampleRxIndicatorWifiPacket(Inet4Address, int, int, byte[])}.
	 *
	 * <p>Construct a new IO Data Sample Rx Indicator (Wi-Fi) packet with an
	 * RSSI bigger than 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateIODataSampleRxIndicatorWifiPacketRSSIBiggerThan255() {
		// Set up the resources for the test.
		rssi = 256;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("RSSI must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new IODataSampleRxIndicatorWifiPacket(sourceAddress, rssi, receiveOptions, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.IODataSampleRxIndicatorWifiPacket#IODataSampleRxIndicatorWifiPacket(Inet4Address, int, int, byte[])}.
	 *
	 * <p>Construct a new IO Data Sample Rx Indicator (Wi-Fi) packet with a
	 * negative RSSI. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateIODataSampleRxIndicatorWifiPacketRSSINegative() {
		// Set up the resources for the test.
		rssi = -1;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("RSSI must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new IODataSampleRxIndicatorWifiPacket(sourceAddress, rssi, receiveOptions, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.IODataSampleRxIndicatorWifiPacket#IODataSampleRxIndicatorWifiPacket(Inet4Address, int, int, byte[])}.
	 *
	 * <p>Construct a new IO Data Sample Rx Indicator (Wi-Fi) packet with
	 * options bigger than 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateIODataSampleRxIndicatorWifiPacketOptionsBiggerThan255() {
		// Set up the resources for the test.
		receiveOptions = 256;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Receive options must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new IODataSampleRxIndicatorWifiPacket(sourceAddress, rssi, receiveOptions, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.IODataSampleRxIndicatorWifiPacket#IODataSampleRxIndicatorWifiPacket(Inet4Address, int, int, byte[])}.
	 *
	 * <p>Construct a new IO Data Sample Rx Indicator (Wi-Fi) packet with
	 * negative options. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateIODataSampleRxIndicatorWifiPacketOptionsNegative() {
		// Set up the resources for the test.
		receiveOptions = -1;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Receive options must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		new IODataSampleRxIndicatorWifiPacket(sourceAddress, rssi, receiveOptions, data);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.IODataSampleRxIndicatorWifiPacket#IODataSampleRxIndicatorWifiPacket(Inet4Address, int, int, byte[])}.
	 *
	 * <p>Construct a new IO Data Sample Rx Indicator (Wi-Fi) packet without data ({@code null}).</p>
	 */
	@Test
	public final void testCreateIODataSampleRxIndicatorWifiPacketDataNull() {
		// Set up the resources for the test.
		data = null;

		int expectedLength = 11;

		// Call the method under test.
		IODataSampleRxIndicatorWifiPacket packet = new IODataSampleRxIndicatorWifiPacket(sourceAddress, rssi, receiveOptions, data);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned source address is not the expected one", packet.getSourceAddress(), is(equalTo(sourceAddress)));
		assertThat("Returned RSSI is not the expected one", packet.getRSSI(), is(equalTo(rssi)));
		assertThat("Returned options are not the expected one", packet.getReceiveOptions(), is(equalTo(receiveOptions)));
		assertThat("Returned data is not the expected one", packet.getRFData(), is(nullValue()));
		assertThat("Returned IO sample is not the expected one", packet.getIOSample(), is(nullValue()));
		assertThat("IO Data Sample Rx Indicator (Wi-Fi) packet does not need API Frame ID", packet.needsAPIFrameID(), is(equalTo(false)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.IODataSampleRxIndicatorWifiPacket#IODataSampleRxIndicatorWifiPacket(Inet4Address, int, int, byte[])}.
	 *
	 * <p>Construct a new IO Data Sample Rx Indicator (Wi-Fi) packet with data.</p>
	 */
	@Test
	public final void testCreateIODataSampleRxIndicatorWifiPacketValidDataNotNull() {
		// Set up the resources for the test.
		int expectedLength = 11 + data.length;

		// Call the method under test.
		IODataSampleRxIndicatorWifiPacket packet = new IODataSampleRxIndicatorWifiPacket(sourceAddress, rssi, receiveOptions, data);

		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned source address is not the expected one", packet.getSourceAddress(), is(equalTo(sourceAddress)));
		assertThat("Returned RSSI is not the expected one", packet.getRSSI(), is(equalTo(rssi)));
		assertThat("Returned options are not the expected one", packet.getReceiveOptions(), is(equalTo(receiveOptions)));
		assertThat("Returned data is not the expected one", packet.getRFData(), is(equalTo(data)));
		assertThat("Returned IO sample is not the expected one", packet.getIOSample(), is(equalTo(sample)));
		assertThat("IO Data Sample Rx Indicator (Wi-Fi) packet does not need API Frame ID", packet.needsAPIFrameID(), is(equalTo(false)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.IODataSampleRxIndicatorWifiPacket#getAPIData()}.
	 *
	 * <p>Test the get API parameters with a {@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIDataReceivedDataNull() {
		// Set up the resources for the test.
		data = null;

		IODataSampleRxIndicatorWifiPacket packet = new IODataSampleRxIndicatorWifiPacket(sourceAddress, rssi, receiveOptions, data);

		int expectedLength = 10;
		byte[] expectedData = new byte[expectedLength];
		System.arraycopy(new byte[]{0x00, 0x00, 0x00, 0x00}, 0, expectedData, 0, 4);
		System.arraycopy(sourceAddress.getAddress(), 0, expectedData, 4, sourceAddress.getAddress().length);
		expectedData[8] = (byte)rssi;
		expectedData[9] = (byte)receiveOptions;

		// Call the method under test.
		byte[] apiData = packet.getAPIData();

		// Verify the result.
		assertThat("API data is not the expected", apiData, is(equalTo(expectedData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.IODataSampleRxIndicatorWifiPacket#getAPIData()}.
	 *
	 * <p>Test the get API parameters with a not-{@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIDataReceivedDataNotNull() {
		// Set up the resources for the test.
		IODataSampleRxIndicatorWifiPacket packet = new IODataSampleRxIndicatorWifiPacket(sourceAddress, rssi, receiveOptions, data);

		int expectedLength = 10 + data.length;
		byte[] expectedData = new byte[expectedLength];
		System.arraycopy(new byte[]{0x00, 0x00, 0x00, 0x00}, 0, expectedData, 0, 4);
		System.arraycopy(sourceAddress.getAddress(), 0, expectedData, 4, sourceAddress.getAddress().length);
		expectedData[8] = (byte)rssi;
		expectedData[9] = (byte)receiveOptions;
		expectedData[10] = (byte)nSamples;
		expectedData[11] = (byte)(digitalMask >> 8);
		expectedData[12] = (byte)digitalMask;
		expectedData[13] = (byte)analogMask;
		expectedData[14] = (byte)(digitalSamples >> 8);
		expectedData[15] = (byte)digitalSamples;
		expectedData[16] = (byte)(analogSamples >> 8);
		expectedData[17] = (byte)analogSamples;

		// Call the method under test.
		byte[] apiData = packet.getAPIData();

		// Verify the result.
		assertThat("API data is not the expected", apiData, is(equalTo(expectedData)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.IODataSampleRxIndicatorWifiPacket#getAPIPacketParameters()}.
	 *
	 * <p>Test the get API parameters but with a {@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersReceivedDataNull() {
		// Set up the resources for the test.
		data = null;

		IODataSampleRxIndicatorWifiPacket packet = new IODataSampleRxIndicatorWifiPacket(sourceAddress, rssi, receiveOptions, data);

		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();

		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(3)));
		assertThat("Returned source address is not the expected one", packetParams.get("Source address"), is(equalTo("00 00 00 00 " + HexUtils.prettyHexString(HexUtils.byteArrayToHexString(sourceAddress.getAddress())) + " (" + sourceAddress.getHostAddress() + ")")));
		assertThat("Returned RSSI is not the expected one", packetParams.get("RSSI"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(rssi, 1)))));
		assertThat("Returned options are not the expected one", packetParams.get("Receive options"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(receiveOptions, 1)))));
		assertThat("Returned number of samples are not the expected", packetParams.get("Number of samples"), is(nullValue()));
		assertThat("Returned digital mask is not the expected", packetParams.get("Digital channel mask"), is(nullValue()));
		assertThat("Returned analog mask is not the expected", packetParams.get("Analog channel mask"), is(nullValue()));
		assertThat("Packet is broadcast", packet.isBroadcast(), is(equalTo(false)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.IODataSampleRxIndicatorWifiPacket#getAPIPacketParameters()}.
	 *
	 * <p>Test the get API parameters.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersReceivedDataNotNull() {
		// Set up the resources for the test.
		IODataSampleRxIndicatorWifiPacket packet = new IODataSampleRxIndicatorWifiPacket(sourceAddress, rssi, receiveOptions, data);

		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();

		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(8)));
		assertThat("Returned source address is not the expected one", packetParams.get("Source address"), is(equalTo("00 00 00 00 " + HexUtils.prettyHexString(HexUtils.byteArrayToHexString(sourceAddress.getAddress())) + " (" + sourceAddress.getHostAddress() + ")")));
		assertThat("Returned RSSI is not the expected one", packetParams.get("RSSI"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(rssi, 1)))));
		assertThat("Returned options are not the expected one", packetParams.get("Receive options"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(receiveOptions, 1)))));
		assertThat("Returned number of samples are not the expected", packetParams.get("Number of samples"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(1, 1)))));
		assertThat("Returned digital mask is not the expected", packetParams.get("Digital channel mask"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(sample.getDigitalMask(), 2)))));
		assertThat("Returned analog mask is not the expected", packetParams.get("Analog channel mask"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(sample.getAnalogMask(), 2)))));
		for (int i = 0; i < 16; i++) {
			if (sample.hasDigitalValue(IOLine.getDIO(i)))
				assertThat("Returned digital value is not the expected", packetParams.get(IOLine.getDIO(i).getName() + " digital value"), is(equalTo(sample.getDigitalValue(IOLine.getDIO(i)).getName())));
		}
		for (int i = 0; i < 6; i++) {
			if (sample.hasAnalogValue(IOLine.getDIO(i)))
				assertThat("Returned analog value is not the expected", packetParams.get(IOLine.getDIO(i).getName() + " analog value"), is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(sample.getAnalogValue(IOLine.getDIO(i)), 2)))));
		}
		assertThat("Packet is broadcast", packet.isBroadcast(), is(equalTo(false)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.IODataSampleRxIndicatorWifiPacket#isBroadcast()}.
	 *
	 * <p>Test the is broadcast method.</p>
	 */
	@Test
	public final void testIsBroadcast() {
		// Set up the resources for the test.
		IODataSampleRxIndicatorWifiPacket packet = new IODataSampleRxIndicatorWifiPacket(sourceAddress, rssi, receiveOptions, data);

		// Call the method under test and verify the result.
		assertThat("Packet should not be broadcast", packet.isBroadcast(), is(equalTo(false)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.IODataSampleRxIndicatorWifiPacket#setSourceAddress(Inet4Address)}.
	 */
	@Test
	public final void testSetSourceAddressNull() {
		// Set up the resources for the test.
		IODataSampleRxIndicatorWifiPacket packet = new IODataSampleRxIndicatorWifiPacket(sourceAddress, rssi, receiveOptions, data);

		sourceAddress = null;

		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Source address cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		packet.setSourceAddress(sourceAddress);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.IODataSampleRxIndicatorWifiPacket#setSourceAddress(Inet4Address)}.
	 *
	 * @throws Exception
	 */
	@Test
	public final void testSetSourceAddressNotNull() throws Exception {
		// Set up the resources for the test.
		IODataSampleRxIndicatorWifiPacket packet = new IODataSampleRxIndicatorWifiPacket(sourceAddress, rssi, receiveOptions, data);

		sourceAddress = (Inet4Address) Inet4Address.getByName("192.168.1.30");

		// Call the method under test.
		packet.setSourceAddress(sourceAddress);

		// Verify the result.
		assertThat("Source address is not the expected one", packet.getSourceAddress(), is(equalTo(sourceAddress)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.IODataSampleRxIndicatorWifiPacket#setRSSI(int)}.
	 */
	@Test
	public final void testSetRSSIBiggerThan255() {
		// Set up the resources for the test.
		IODataSampleRxIndicatorWifiPacket packet = new IODataSampleRxIndicatorWifiPacket(sourceAddress, rssi, receiveOptions, data);

		rssi = 256;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("RSSI must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		packet.setRSSI(rssi);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.IODataSampleRxIndicatorWifiPacket#setRSSI(int)}.
	 */
	@Test
	public final void testSetRSSINegative() {
		// Set up the resources for the test.
		IODataSampleRxIndicatorWifiPacket packet = new IODataSampleRxIndicatorWifiPacket(sourceAddress, rssi, receiveOptions, data);

		rssi = -1;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("RSSI must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		packet.setRSSI(rssi);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.IODataSampleRxIndicatorWifiPacket#setRSSI(int)}.
	 */
	@Test
	public final void testSetRSSIValid() {
		// Set up the resources for the test.
		IODataSampleRxIndicatorWifiPacket packet = new IODataSampleRxIndicatorWifiPacket(sourceAddress, rssi, receiveOptions, data);

		rssi = 0x42;

		// Call the method under test.
		packet.setRSSI(rssi);

		// Verify the result.
		assertThat("RSSI is not the expected one", packet.getRSSI(), is(equalTo(rssi)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.IODataSampleRxIndicatorWifiPacket#setReceiveOptions(int)}.
	 */
	@Test
	public final void testSetOptionsBiggerThan255() {
		// Set up the resources for the test.
		IODataSampleRxIndicatorWifiPacket packet = new IODataSampleRxIndicatorWifiPacket(sourceAddress, rssi, receiveOptions, data);

		receiveOptions = 256;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Receive options must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		packet.setReceiveOptions(receiveOptions);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.IODataSampleRxIndicatorWifiPacket#setReceiveOptions(int)}.
	 */
	@Test
	public final void testSetOptionsNegative() {
		// Set up the resources for the test.
		IODataSampleRxIndicatorWifiPacket packet = new IODataSampleRxIndicatorWifiPacket(sourceAddress, rssi, receiveOptions, data);

		receiveOptions = -1;

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Receive options must be between 0 and 255.")));

		// Call the method under test that should throw an IllegalArgumentException.
		packet.setReceiveOptions(receiveOptions);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.wifi.IODataSampleRxIndicatorWifiPacket#setReceiveOptions(int)}.
	 */
	@Test
	public final void testSetOptionsValid() {
		// Set up the resources for the test.
		IODataSampleRxIndicatorWifiPacket packet = new IODataSampleRxIndicatorWifiPacket(sourceAddress, rssi, receiveOptions, data);

		receiveOptions = 0x00;

		// Call the method under test.
		packet.setReceiveOptions(receiveOptions);

		// Verify the result.
		assertThat("Receive options are not the expected one", packet.getReceiveOptions(), is(equalTo(receiveOptions)));
	}
}
