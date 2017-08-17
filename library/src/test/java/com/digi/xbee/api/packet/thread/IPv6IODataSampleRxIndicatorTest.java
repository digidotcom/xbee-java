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
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.IsNot.not;

import static org.junit.Assert.assertThat;

import java.net.Inet6Address;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.digi.xbee.api.exceptions.OperationNotSupportedException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

public class IPv6IODataSampleRxIndicatorTest {
	
	// Constants.
	private static final String IPV6_ADDR = "FDB3:0001:0002:0000:0004:0005:0006:0007";
	
	// Variables.
	int frameType = APIFrameType.IPV6_IO_DATA_SAMPLE_RX_INDICATOR.getValue();
	private Inet6Address ipv6address;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();

	public IPv6IODataSampleRxIndicatorTest() throws Exception {
		ipv6address = (Inet6Address) Inet6Address.getByName(IPV6_ADDR);
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
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6IODataSampleRxIndicator#createPacket(byte[])}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when parsing 
	 * a {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Setup the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("IPv6 IO Data Sample RX Indicator packet payload cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		IPv6IODataSampleRxIndicator.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6IODataSampleRxIndicator#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Setup the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete IPv6 IO Data Sample RX Indicator packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		IPv6IODataSampleRxIndicator.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6IODataSampleRxIndicator#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Setup the resources for the test.
		byte[] payload = new byte[15];
		payload[0] = (byte)frameType;
		// Do not copy the complete IPv6 address.
		System.arraycopy(ipv6address.getAddress(), 0, payload, 1, ipv6address.getAddress().length - 2);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete IPv6 IO Data Sample RX Indicator packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		IPv6IODataSampleRxIndicator.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6IODataSampleRxIndicator#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array not including the Frame type.</p>
	 */
	@Test
	public final void testCreatePacketPayloadNotIncludingFrameType() {
		// Setup the resources for the test.
		byte[] data = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		
		byte[] payload = new byte[ipv6address.getAddress().length + data.length];
		System.arraycopy(ipv6address.getAddress(), 0, payload, 0, ipv6address.getAddress().length);
		System.arraycopy(data, 0, payload, 16, data.length);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not an IPv6 IO Data Sample RX Indicator packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		IPv6IODataSampleRxIndicator.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6IODataSampleRxIndicator#createPacket(byte[])}.
	 * 
	 * <p>A valid IPv6 IO data sample RX indicator with the minimum payload 
	 * size is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadMinimum() {
		// Setup the resources for the test.
		byte[] payload = new byte[17];
		payload[0] = (byte)frameType;
		System.arraycopy(ipv6address.getAddress(), 0, payload, 1, ipv6address.getAddress().length);
		
		// Call the method under test.
		IPv6IODataSampleRxIndicator packet = IPv6IODataSampleRxIndicator.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned source IPv6 address is not the expected one", packet.getSourceAddress(), is(equalTo(ipv6address)));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6IODataSampleRxIndicator#createPacket(byte[])}.
	 * 
	 * <p>A valid API IPv6 IO Data Sample RX Indicator packet with the provided 
	 * options without IO sample data is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutIOSamples() {
		// Setup the resources for the test.
		byte[] data = new byte[]{(byte)0x01};
		
		byte[] payload = new byte[17 + data.length];
		payload[0] = (byte)frameType;
		System.arraycopy(ipv6address.getAddress(), 0, payload, 1, ipv6address.getAddress().length);
		System.arraycopy(data, 0, payload, 17, data.length);
		
		// Call the method under test.
		IPv6IODataSampleRxIndicator packet = IPv6IODataSampleRxIndicator.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned source IPv6 address is not the expected one", packet.getSourceAddress(), is(equalTo(ipv6address)));
		assertThat("Returned Received Data is not the expected one", packet.getRFData(), is(equalTo(data)));
		assertThat("Returned IO sample is not the expected one", packet.getIOSample(), is(nullValue(IOSample.class)));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6IODataSampleRxIndicator#createPacket(byte[])}.
	 * 
	 * <p>A valid API IPv6 IO Data Sample RX Indicator packet with the provided 
	 * options and RF data is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithData() {
		// Setup the resources for the test.
		byte[] data = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		
		byte[] payload = new byte[17 + data.length];
		payload[0] = (byte)frameType;
		System.arraycopy(ipv6address.getAddress(), 0, payload, 1, ipv6address.getAddress().length);
		System.arraycopy(data, 0, payload, 17, data.length);
		
		// Call the method under test.
		IPv6IODataSampleRxIndicator packet = IPv6IODataSampleRxIndicator.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned source IPv6 address is not the expected one", packet.getSourceAddress(), is(equalTo(ipv6address)));
		assertThat("Returned Received Data is not the expected one", packet.getRFData(), is(equalTo(data)));
		assertThat("Returned IO Sample must not be null", packet.getIOSample(), is(not(nullValue(IOSample.class))));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6IODataSampleRxIndicator#IPv6IODataSampleRxIndicator(Inet6Address, byte[])}.
	 * 
	 * <p>Construct a new IPv6 IO Data Sample RX Indicator packet with a 
	 * {@code null} source IPv6 address. This must throw a 
	 * {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateIPv6IODataSampleRxIndicator64BitAddressNull() {
		// Setup the resources for the test.
		byte[] data = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Source address cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new IPv6IODataSampleRxIndicator(null, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6IODataSampleRxIndicator#IPv6IODataSampleRxIndicator(Inet6Address, byte[])}.
	 * 
	 * <p>Construct a new IPv6 IO Data Sample RX Indicator packet with 
	 * {@code null} data.</p>
	 */
	@Test
	public final void testCreateIPv6IODataSampleRxIndicatorDataNull() {
		// Setup the resources for the test.
		int expectedLength = 1 /* Frame type */ + 16 /* IPv6 address */;
		
		// Call the method under test.
		IPv6IODataSampleRxIndicator packet = new IPv6IODataSampleRxIndicator(ipv6address, null);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned source IPv6 address is not the expected one", packet.getSourceAddress(), is(equalTo(ipv6address)));
		assertThat("Returned Received Data is not the expected one", packet.getRFData(), is(nullValue(byte[].class)));
		assertThat("Frame ID is not the expected one", packet.needsAPIFrameID(), is(equalTo(false)));
		assertThat("IO Sample must be null", packet.getIOSample(), is(nullValue(IOSample.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6IODataSampleRxIndicator#IPv6IODataSampleRxIndicator(Inet6Address, byte[])}.
	 * 
	 * <p>Construct a new IPv6 IO Data Sample RX Indicator packet but with data 
	 * length less than 5. This must throw an 
	 * {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateIPv6IODataSampleRxIndicatorDataLengthLessThan5() {
		// Setup the resources for the test.
		byte[] data = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF};
		
		int expectedLength = 1 /* Frame type */ + 16 /* IPv6 address */ + data.length /* Data */;
		
		// Call the method under test.
		IPv6IODataSampleRxIndicator packet = new IPv6IODataSampleRxIndicator(ipv6address, data);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned source IPv6 address is not the expected one", packet.getSourceAddress(), is(equalTo(ipv6address)));
		assertThat("Returned Received Data is not the expected one", packet.getRFData(), is(equalTo(data)));
		assertThat("Returned IO sample is not the expected one", packet.getIOSample(), is(nullValue(IOSample.class)));
		assertThat("IPv6  IO Data Sample RX Indicator packet does NOT need API Frame ID", packet.needsAPIFrameID(), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6IODataSampleRxIndicator#IPv6IODataSampleRxIndicator(Inet6Address, byte[])}.
	 * 
	 * <p>Construct a new IPv6 IO Data Sample RX Indicator packet.</p>
	 */
	@Test
	public final void testCreateIPv6IODataSampleRxIndicator() {
		// Setup the resources for the test.
		byte[] data = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		
		int expectedLength = 1 /* Frame type */ + 16 /* IPv6 address */ + data.length /* Data */;
		
		// Call the method under test.
		IPv6IODataSampleRxIndicator packet = new IPv6IODataSampleRxIndicator(ipv6address, data);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned source IPv6 address is not the expected one", packet.getSourceAddress(), is(equalTo(ipv6address)));
		assertThat("Returned Data is not the expected one", packet.getRFData(), is(equalTo(data)));
		assertThat("IPv6 IO Data Sample RX Indicator packet does NOT need API Frame ID", packet.needsAPIFrameID(), is(equalTo(false)));
		assertThat("IO Sample must NOT be null", packet.getIOSample(), is(not(nullValue(IOSample.class))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6IODataSampleRxIndicator#getAPIData()}.
	 * 
	 * <p>Test the get API parameters with a {@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIDataReceivedDataNull() {
		// Setup the resources for the test.
		IPv6IODataSampleRxIndicator packet = new IPv6IODataSampleRxIndicator(ipv6address, null);
		
		byte[] expectedData = ipv6address.getAddress();
		
		// Call the method under test.
		byte[] data = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6IODataSampleRxIndicator#getAPIData()}.
	 * 
	 * <p>Test the get API parameters but a non-{@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIDataReceivedDataNonNull() {
		// Setup the resources for the test.
		byte[] receivedData = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		IPv6IODataSampleRxIndicator packet = new IPv6IODataSampleRxIndicator(ipv6address, receivedData);
		
		int expectedLength = 16 /* IPv6 address */ + receivedData.length /* Data */;
		byte[] expectedData = new byte[expectedLength];
		System.arraycopy(ipv6address.getAddress(), 0, expectedData, 0, ipv6address.getAddress().length);
		System.arraycopy(receivedData, 0, expectedData, 16, receivedData.length);
		
		// Call the method under test.
		byte[] data = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6IODataSampleRxIndicator#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters but with a {@code null} received data.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersReceivedDataNull() {
		// Setup the resources for the test.
		IPv6IODataSampleRxIndicator packet = new IPv6IODataSampleRxIndicator(ipv6address, null);
		
		String expectedSourceIPv6Addr = HexUtils.prettyHexString(ipv6address.getAddress()) + " (" + ipv6address.getHostAddress() + ")";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(1)));
		assertThat("Source IPv6 Address is not the expected one", packetParams.get("IPv6 source address"), is(equalTo(expectedSourceIPv6Addr)));
		assertThat("Number of samples is not the expected", packetParams.get("Number of samples"), is(nullValue(String.class)));
		assertThat("Digital channel mask is not the expected", packetParams.get("Digital channel mask"), is(nullValue(String.class)));
		assertThat("Analog channel mask is not the expected", packetParams.get("Analog channel mask"), is(nullValue(String.class)));
		assertThat("Power supply value is not the expected", packetParams.get("Power supply value"), is(nullValue(String.class)));
		assertThat("RF data is not the expected", packetParams.get("RF data"), is(nullValue(String.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6IODataSampleRxIndicator#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters with invalid received data.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersInvalidReceivedData() {
		// Setup the resources for the test.
		byte[] receivedData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		IPv6IODataSampleRxIndicator packet = new IPv6IODataSampleRxIndicator(ipv6address, receivedData);
		
		String expectedSourceIPv6Addr = HexUtils.prettyHexString(ipv6address.getAddress()) + " (" + ipv6address.getHostAddress() + ")";
		String expectedRFData = HexUtils.prettyHexString(HexUtils.byteArrayToHexString(receivedData));
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(2)));
		assertThat("Source IPv6 Address is not the expected one", packetParams.get("IPv6 source address"), is(equalTo(expectedSourceIPv6Addr)));
		assertThat("Number of samples is not the expected", packetParams.get("Number of samples"), is(nullValue(String.class)));
		assertThat("Digital channel mask is not the expected", packetParams.get("Digital channel mask"), is(nullValue(String.class)));
		assertThat("Analog channel mask is not the expected", packetParams.get("Analog channel mask"), is(nullValue(String.class)));
		assertThat("Power supply value is not the expected", packetParams.get("Power supply value"), is(nullValue(String.class)));
		assertThat("RF data is not the expected", packetParams.get("RF data"), is(equalTo(expectedRFData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6IODataSampleRxIndicator#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters with a non-{@code null} received data.</p>
	 * 
	 * @throws OperationNotSupportedException 
	 */
	@Test
	public final void testGetAPIPacketParametersReceivedDataNotNull() throws OperationNotSupportedException {
		// Setup the resources for the test.
		byte[] receivedData = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, 0x02, 0x0C, 0x00, (byte)0xFA, 0x04, (byte)0xE2};
		IPv6IODataSampleRxIndicator packet = new IPv6IODataSampleRxIndicator(ipv6address, receivedData);
		
		String expectedSourceIPv6Addr = HexUtils.prettyHexString(ipv6address.getAddress()) + " (" + ipv6address.getHostAddress() + ")";
		IOSample expectedIoSample = new IOSample(receivedData);
		String expectedDigitalMask = HexUtils.prettyHexString(HexUtils.integerToHexString(expectedIoSample.getDigitalMask(), 2));
		String expectedAnalogMask = HexUtils.prettyHexString(HexUtils.integerToHexString(expectedIoSample.getAnalogMask(), 1));
		String expectedPowerSupplyValue = HexUtils.prettyHexString(HexUtils.integerToHexString(expectedIoSample.getPowerSupplyValue(), 2));
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(23)));
		assertThat("Source IPv6 Address is not the expected one", packetParams.get("IPv6 source address"), is(equalTo(expectedSourceIPv6Addr)));
		assertThat("Number of samples is not the expected", packetParams.get("Number of samples"), is(equalTo("01"))); // Always 1.
		assertThat("Digital channel mask is not the expected", packetParams.get("Digital channel mask"), is(equalTo(expectedDigitalMask)));
		assertThat("Analog channel mask is not the expected", packetParams.get("Analog channel mask"), is(equalTo(expectedAnalogMask)));
		for (int i = 0; i < 16; i++) {
			if (expectedIoSample.hasDigitalValue(IOLine.getDIO(i)))
				assertThat(packetParams.get(IOLine.getDIO(i).getName() + " digital value"), 
						is(equalTo(expectedIoSample.getDigitalValue(IOLine.getDIO(i)).getName())));
		}
		for (int i = 0; i < 6; i++)
			if (expectedIoSample.hasAnalogValue(IOLine.getDIO(i)))
				assertThat(packetParams.get(IOLine.getDIO(i).getName() + " analog value"), 
						is(equalTo(HexUtils.prettyHexString(HexUtils.integerToHexString(expectedIoSample.getAnalogValue(IOLine.getDIO(i)), 2)) + 
								" (" + expectedIoSample.getAnalogValue(IOLine.getDIO(i)) + ")")));
		
		assertThat("Power supply value is not the expected", packetParams.get("Power supply value"), is(equalTo(expectedPowerSupplyValue)));
		assertThat("RF data is not the expected", packetParams.get("RF data"), is(nullValue(String.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6IODataSampleRxIndicator#isBroadcast()}.
	 * 
	 * <p>Test if an IPv6 IO Data Sample RX Indicator packet is a broadcast 
	 * packet. This must throw a {@code UnsupportedOperationException}.</p>
	 */
	@SuppressWarnings("deprecation")
	@Test
	public final void testIsBroadcastWithNonBroadcastOption() {
		// Setup the resources for the test.
		byte[] receivedData = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		IPv6IODataSampleRxIndicator packet = new IPv6IODataSampleRxIndicator(ipv6address, receivedData);
		
		exception.expect(UnsupportedOperationException.class);
		
		// Call the method under test and verify the result.
		packet.isBroadcast();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6IODataSampleRxIndicator#getRFData())}.
	 */
	@Test
	public final void testGetRFDataNullData() {
		// Setup the resources for the test.
		IPv6IODataSampleRxIndicator packet = new IPv6IODataSampleRxIndicator(ipv6address, null);
		
		// Call the method under test.
		byte[] result = packet.getRFData();
		
		// Verify the result.
		assertThat("RF Data must be the same", result, is(equalTo(null)));
		assertThat("RF Data must be null", result, is(nullValue(byte[].class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6IODataSampleRxIndicator#getRFData())}.
	 */
	@Test
	public final void testGetRFDataInvalidData() {
		// Setup the resources for the test.
		byte[] receivedData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		IPv6IODataSampleRxIndicator packet = new IPv6IODataSampleRxIndicator(ipv6address, receivedData);
		
		// Call the method under test.
		byte[] result = packet.getRFData();
		
		// Verify the result.
		assertThat("RF Data must be the same", result, is(equalTo(receivedData)));
		assertThat("RF Data must not be the same object", result.hashCode(), is(not(equalTo(receivedData.hashCode()))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6IODataSampleRxIndicator#getRFData())}.
	 */
	@Test
	public final void testGetRFDataValidData() {
		// Setup the resources for the test.
		byte[] receivedData = new byte[]{0x68, 0x6F, 0x6C, 0x61, (byte)0x98, 0x11, 0x32};
		IPv6IODataSampleRxIndicator packet = new IPv6IODataSampleRxIndicator(ipv6address, receivedData);
		
		// Call the method under test.
		byte[] result = packet.getRFData();
		
		// Verify the result.
		assertThat("RF Data must be the same", result, is(equalTo(receivedData)));
		assertThat("RF Data must not be the same object", result.hashCode(), is(not(equalTo(receivedData.hashCode()))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6IODataSampleRxIndicator#setRFData(byte[])}.
	 */
	@Test
	public final void testSetRFDataNullData() {
		// Setup the resources for the test.
		byte[] origData = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		byte[] receivedData = null;
		IPv6IODataSampleRxIndicator packet = new IPv6IODataSampleRxIndicator(ipv6address, origData);
		
		IOSample origSample = packet.getIOSample();
		
		// Call the method under test.
		packet.setRFData(receivedData);
		
		byte[] result = packet.getRFData();
		IOSample sample = packet.getIOSample();
		
		// Verify the result.
		assertThat("RF Data must be the same", result, is(equalTo(receivedData)));
		assertThat("RF Data must be null", result, is(nullValue(byte[].class)));
		
		assertThat("IO sample must be null", sample, is(nullValue(IOSample.class)));
		assertThat("IO sample orig must not be null", origSample, is(not(nullValue(IOSample.class))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6IODataSampleRxIndicator#setRFData(byte[])}.
	 */
	@Test
	public final void testSetRFDataInvalidData() {
		// Setup the resources for the test.
		byte[] origData = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		byte[] receivedData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		IPv6IODataSampleRxIndicator packet = new IPv6IODataSampleRxIndicator(ipv6address, origData);
		
		IOSample origSample = packet.getIOSample();
		
		// Call the method under test.
		packet.setRFData(receivedData);
		
		byte[] result = packet.getRFData();
		IOSample sample = packet.getIOSample();
		
		// Verify the result.
		assertThat("RF Data must be the same", result, is(equalTo(receivedData)));
		assertThat("RF Data must not be the same object", result.hashCode(), is(not(equalTo(receivedData.hashCode()))));
		
		assertThat("IO sample is not the expected", sample, is(nullValue(IOSample.class)));
		assertThat("IO sample orig must not be null", origSample, is(not(nullValue(IOSample.class))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6IODataSampleRxIndicator#setRFData(byte[])}.
	 */
	@Test
	public final void testSetRFDataValidData() {
		// Setup the resources for the test.
		byte[] origData = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		byte[] receivedData = new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
		IPv6IODataSampleRxIndicator packet = new IPv6IODataSampleRxIndicator(ipv6address, origData);
		
		IOSample origSample = packet.getIOSample();
		
		// Call the method under test.
		packet.setRFData(receivedData);
		
		byte[] result = packet.getRFData();
		IOSample sample = packet.getIOSample();
		
		// Verify the result.
		assertThat("RF Data must be the same", result, is(equalTo(receivedData)));
		assertThat("RF Data must not be the same object", result.hashCode(), is(not(equalTo(receivedData.hashCode()))));
		
		assertThat("IO sample is not the expected", sample.toString(), is(equalTo(new IOSample(receivedData).toString())));
		assertThat("IO sample must not be equal to the original", sample.toString(), is(not(equalTo(origSample.toString()))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6IODataSampleRxIndicator#setRFData(byte[])}.
	 */
	@Test
	public final void testSetRFDataAndModifyOriginal() {
		// Setup the resources for the test.
		byte[] origData = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		byte[] receivedData = new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
		IPv6IODataSampleRxIndicator packet = new IPv6IODataSampleRxIndicator(ipv6address, origData);
		
		IOSample origSample = packet.getIOSample();
		
		// Call the method under test.
		packet.setRFData(receivedData);
		byte[] backup = Arrays.copyOf(receivedData, receivedData.length);
		receivedData[0] = 0x11;
		receivedData[1] = 0x12;
		
		byte[] result = packet.getRFData();
		IOSample sample = packet.getIOSample();
		
		// Verify the result.
		assertThat("RF Data must be the same as the setted data", result, is(equalTo(backup)));
		assertThat("RF Data must not be the current value of received data", result, is(not(equalTo(receivedData))));
		assertThat("RF Data must not be the same object", result.hashCode(), is(not(equalTo(backup.hashCode()))));
		assertThat("RF Data must not be the same object", result.hashCode(), is(not(equalTo(receivedData.hashCode()))));
		
		assertThat("IO sample is not the expected", sample.toString(), is(equalTo(new IOSample(backup).toString())));
		assertThat("IO sample must not be equal to the original", sample.toString(), is(not(equalTo(origSample.toString()))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6IODataSampleRxIndicator#getIOSample()}.
	 */
	@Test
	public final void testGetIOSampleNullData() {
		// Setup the resources for the test.
		byte[] receivedData = null;
		IPv6IODataSampleRxIndicator packet = new IPv6IODataSampleRxIndicator(ipv6address, receivedData);
		
		// Call the method under test.
		IOSample result = packet.getIOSample();
		
		// Verify the result.
		assertThat("IO sample is not the expected", result, is(nullValue(IOSample.class)));
		assertThat("RF data must be null", packet.getRFData(), is(nullValue(byte[].class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6IODataSampleRxIndicator#getIOSample()}.
	 */
	@Test
	public final void testGetIOSampleInvalidData() {
		// Setup the resources for the test.
		byte[] receivedData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		IPv6IODataSampleRxIndicator packet = new IPv6IODataSampleRxIndicator(ipv6address, receivedData);
		
		// Call the method under test.
		IOSample result = packet.getIOSample();
		
		// Verify the result.
		assertThat("IO sample is not the expected", result, is(nullValue(IOSample.class)));
		assertThat("RF data is not the expected", packet.getRFData(), is(equalTo(receivedData)));
		assertThat("RF Data must not be the same object", packet.getRFData().hashCode(), is(not(equalTo(receivedData.hashCode()))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6IODataSampleRxIndicator#getIOSample()}.
	 */
	@Test
	public final void testGetIOSampleValidData() {
		// Setup the resources for the test.
		byte[] receivedData = new byte[]{0x68, 0x6F, 0x6C, 0x61, (byte)0x98, 0x11};
		IPv6IODataSampleRxIndicator packet = new IPv6IODataSampleRxIndicator(ipv6address, receivedData);
		
		// Call the method under test.
		IOSample result = packet.getIOSample();
		
		// Verify the result.
		assertThat("IO sample must not be null", result, is(not(nullValue(IOSample.class))));
		assertThat("IO sample is not the expected", result.toString(), is(equalTo(new IOSample(receivedData).toString())));
		assertThat("RF data is not the expected", packet.getRFData(), is(equalTo(receivedData)));
		assertThat("RF Data must not be the same object", packet.getRFData().hashCode(), is(not(equalTo(receivedData.hashCode()))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.thread.IPv6IODataSampleRxIndicator#getIOSample()}.
	 */
	@Test
	public final void testGetIOSampleModify() {
		// Setup the resources for the test.
		byte[] receivedData = new byte[]{0x68, 0x6F, 0x6C, 0x61, (byte)0x98, 0x11};
		IPv6IODataSampleRxIndicator packet = new IPv6IODataSampleRxIndicator(ipv6address, receivedData);
		
		// Call the method under test.
		IOSample result = packet.getIOSample();
		IOSample backup = new IOSample(receivedData);
		
		result.getAnalogValues().clear();
		result.getDigitalValues().clear();
		
		// Verify the result.
		assertThat("IO sample must not be null", result, is(not(nullValue(IOSample.class))));
		assertThat("IO sample is not the expected", result.toString(), is(equalTo(backup.toString())));
		assertThat("RF data is not the expected", packet.getRFData(), is(equalTo(receivedData)));
		assertThat("RF Data must not be the same object", packet.getRFData().hashCode(), is(not(equalTo(receivedData.hashCode()))));
	}
}
