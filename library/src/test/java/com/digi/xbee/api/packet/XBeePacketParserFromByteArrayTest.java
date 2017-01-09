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
package com.digi.xbee.api.packet;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.digi.xbee.api.exceptions.InvalidPacketException;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.SpecialByte;
import com.digi.xbee.api.packet.cellular.RXSMSPacket;
import com.digi.xbee.api.packet.cellular.TXSMSPacket;
import com.digi.xbee.api.packet.common.ATCommandPacket;
import com.digi.xbee.api.packet.common.ATCommandQueuePacket;
import com.digi.xbee.api.packet.common.ATCommandResponsePacket;
import com.digi.xbee.api.packet.common.ExplicitAddressingPacket;
import com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket;
import com.digi.xbee.api.packet.common.IODataSampleRxIndicatorPacket;
import com.digi.xbee.api.packet.common.ModemStatusPacket;
import com.digi.xbee.api.packet.common.ReceivePacket;
import com.digi.xbee.api.packet.common.RemoteATCommandPacket;
import com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket;
import com.digi.xbee.api.packet.common.TransmitPacket;
import com.digi.xbee.api.packet.common.TransmitStatusPacket;
import com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket;
import com.digi.xbee.api.packet.devicecloud.DeviceResponsePacket;
import com.digi.xbee.api.packet.devicecloud.DeviceResponseStatusPacket;
import com.digi.xbee.api.packet.devicecloud.FrameErrorPacket;
import com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket;
import com.digi.xbee.api.packet.devicecloud.SendDataResponsePacket;
import com.digi.xbee.api.packet.ip.RXIPv4Packet;
import com.digi.xbee.api.packet.ip.TXIPv4Packet;
import com.digi.xbee.api.packet.raw.RX16IOPacket;
import com.digi.xbee.api.packet.raw.RX16Packet;
import com.digi.xbee.api.packet.raw.RX64IOPacket;
import com.digi.xbee.api.packet.raw.RX64Packet;
import com.digi.xbee.api.packet.raw.TX16Packet;
import com.digi.xbee.api.packet.raw.TX64Packet;
import com.digi.xbee.api.packet.raw.TXStatusPacket;
import com.digi.xbee.api.packet.wifi.IODataSampleRxIndicatorWifiPacket;
import com.digi.xbee.api.packet.wifi.RemoteATCommandResponseWifiPacket;
import com.digi.xbee.api.packet.wifi.RemoteATCommandWifiPacket;
import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;

public class XBeePacketParserFromByteArrayTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	// Class under test
	private XBeePacketParser packetParser;

	public XBeePacketParserFromByteArrayTest() {
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
		packetParser = new XBeePacketParser();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a 
	 * {@code null} byte array.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketNullByteArrayApiMode() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteArray = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Packet byte array cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		packetParser.parsePacket(byteArray, OperatingMode.API);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a 
	 * {@code null} byte array.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketNullByteArrayApiEscapeMode() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteArray = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Packet byte array cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		packetParser.parsePacket(byteArray, OperatingMode.API_ESCAPE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a 
	 * {@code null} byte array.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketNullByteArrayAtMode() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteArray = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Packet byte array cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		packetParser.parsePacket(byteArray, OperatingMode.AT);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a 
	 * {@code null} byte array.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketNullByteArrayUnknownMode() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteArray = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Packet byte array cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		packetParser.parsePacket(byteArray, OperatingMode.UNKNOWN);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a 
	 * {@code null} byte array.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketNullByteArrayNullMode() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteArray = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Packet byte array cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		packetParser.parsePacket(byteArray, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when the 
	 * {@code OperatingMode} is {@code null}.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketValidByteArrayNullMode() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteArray = {0x7E, 0x00, 0x06, 0x08, 0x01, 0x4E, 0x49, 0x41, 0x54, (byte)0xCA};
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Operating mode cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		packetParser.parsePacket(byteArray, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when the 
	 * {@code OperatingMode} is {@code OperatingMode.UNKNOWN}.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketValidByteArrayUnknownMode() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteArray = {0x7E, 0x00, 0x06, 0x08, 0x01, 0x4E, 0x49, 0x41, 0x54, (byte)0xCA};
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Operating mode must be API or API Escaped.")));
		
		// Call the method under test that should throw a IllegalArgumentException.
		packetParser.parsePacket(byteArray, OperatingMode.UNKNOWN);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when the 
	 * {@code OperatingMode} is {@code OperatingMode.AT}.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketValidByteArrayAtMode() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteArray = {0x7E, 0x00, 0x06, 0x08, 0x01, 0x4E, 0x49, 0x41, 0x54, (byte)0xCA};
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Operating mode must be API or API Escaped.")));
		
		// Call the method under test that should throw a IllegalArgumentException.
		packetParser.parsePacket(byteArray, OperatingMode.AT);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>An {@code InvalidPacketException} exception must be thrown when the 
	 * frame to parse is shorter than {@code 4} bytes.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketFrameEmptyApiMode() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteArray = new byte[0];
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Error parsing packet: Incomplete packet.")));
		
		// Call the method under test that should throw a InvalidPacketException.
		packetParser.parsePacket(byteArray, OperatingMode.API);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>An {@code InvalidPacketException} exception must be thrown when the 
	 * frame to parse is shorter than {@code 4} bytes.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketFrameEmptyApiEscapeMode() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteArray = new byte[0];
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Error parsing packet: Incomplete packet.")));
		
		// Call the method under test that should throw a InvalidPacketException.
		packetParser.parsePacket(byteArray, OperatingMode.API_ESCAPE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>An {@code InvalidPacketException} exception must be thrown when the 
	 * frame to parse is shorter than {@code 4} bytes.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketFrameTooShortApiMode() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteArray = {0x7E, 0x00, 0x06};
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Error parsing packet: Incomplete packet.")));
		
		// Call the method under test that should throw a InvalidPacketException.
		packetParser.parsePacket(byteArray, OperatingMode.API);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>An {@code InvalidPacketException} exception must be thrown when the 
	 * frame to parse is shorter than {@code 4} bytes.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketFrameTooShortApiEscapeMode() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteArray = {0x7E, 0x7D, 0x33};
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Error parsing packet: Incomplete packet.")));
		
		// Call the method under test that should throw a InvalidPacketException.
		packetParser.parsePacket(byteArray, OperatingMode.API_ESCAPE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>An {@code InvalidPacketException} exception must be thrown when the 
	 * frame to parse is not starting with the header byte.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketNotHeaderByteApiMode() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteArray = {0x68, 0x00, 0x08, 0x08, 0x01, 0x4E, 0x49, 0x41, 0x54, 0x49, (byte)0x81, (byte)0x7E};
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Invalid start delimiter (expected 0x" 
				+ HexUtils.byteToHexString((byte)SpecialByte.HEADER_BYTE.getValue()) + ").")));
		
		// Call the method under test that should throw a InvalidPacketException.
		packetParser.parsePacket(byteArray, OperatingMode.API);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>An {@code InvalidPacketException} exception must be thrown when the 
	 * frame to parse is not starting with the header byte.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketNotHeaderByteApiEscapeMode() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteArray = {0x68, 0x00, 0x08, 0x08, 0x01, 0x4E, 0x49, 0x41, 0x54, 0x49, (byte)0x81, (byte)0x7E};
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Invalid start delimiter (expected 0x" 
				+ HexUtils.byteToHexString((byte)SpecialByte.HEADER_BYTE.getValue()) + ").")));
		
		// Call the method under test that should throw a InvalidPacketException.
		packetParser.parsePacket(byteArray, OperatingMode.API_ESCAPE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>An {@code InvalidPacketException} exception must be thrown when the 
	 * length in the frame does not match with the payload length.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketFrameShorterThanExpectedApiMode() throws InvalidPacketException {
		// Setup the resources for the test.
		// Real package: {0x7E, 0x00, 0x07, 0x08, 0x01, 0x4E, 0x49, 0x41, 0x54, 0x49, 0x81};
		byte[] byteArray = {0x7E, 0x00, 0x08, 0x08, 0x01, 0x4E, 0x49, 0x41, 0x54, 0x49, (byte)0x81, (byte)0x7E};
		byte expectedChecksum = 0x00;
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Invalid checksum (expected 0x" 
				+ HexUtils.byteToHexString(expectedChecksum) + ").")));
		
		// Call the method under test that should throw a InvalidPacketException.
		packetParser.parsePacket(byteArray, OperatingMode.API);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>An {@code InvalidPacketException} exception must be thrown when the 
	 * length in the frame does not match with the payload length.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketFrameShorterThanExpectedApiEscapeMode() throws InvalidPacketException {
		// Setup the resources for the test.
		// Real package: {0x7E, 0x00, 0x09, (byte)0x88, 0x07, 0x53, 0x48, 0x00, 0x00, 0x7D, 0x33, (byte)0xA2, 0x00, 0x20}
		byte[] byteArray = {0x7E, 0x00, 0x0B, (byte)0x88, 0x07, 0x53, 0x48, 0x00, 0x00, 0x7D, 0x33, (byte)0xA2, 0x00, 0x20, 0x25, (byte)0xB4};
		byte expectedChecksum = (byte)0xDB;
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Invalid checksum (expected 0x" 
				+ HexUtils.byteToHexString(expectedChecksum) + ").")));
		
		// Call the method under test that should throw a InvalidPacketException.
		packetParser.parsePacket(byteArray, OperatingMode.API_ESCAPE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>An {@code InvalidPacketException} exception must be thrown when the 
	 * length in the frame does not match with the payload length.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketFrameShorterThanExpectedApiEscapeMode2() throws InvalidPacketException {
		// Setup the resources for the test.
		byte notEscapedByte = 0x7E;
		// Real package: {0x7E, 0x00, 0x09, (byte)0x88, 0x07, 0x53, 0x48, 0x00, 0x00, 0x7D, 0x33, (byte)0xA2, 0x00, 0x20}
		byte[] byteArray = {0x7E, 0x00, 0x0B, (byte)0x88, 0x07, 0x53, 0x48, 0x00, 0x00, 0x7D, 0x33, (byte)0xA2, 0x00, 0x20, notEscapedByte, (byte)0x00};
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Special byte not escaped: 0x" 
				+ HexUtils.byteToHexString((byte)(notEscapedByte & 0xFF)) + ".")));
		
		// Call the method under test that should throw a InvalidPacketException.
		packetParser.parsePacket(byteArray, OperatingMode.API_ESCAPE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>An {@code InvalidPacketException} exception must be thrown when the 
	 * length in the frame does not match with the payload length.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketFrameLongerThanExpectedApiMode() throws InvalidPacketException {
		// Setup the resources for the test.
		// Real package: {0x7E, 0x00, 0x07, 0x08, 0x01, 0x4E, 0x49, 0x41, 0x54, 0x49, 0x81};
		byte[] byteArray = {0x7E, 0x00, 0x05, 0x08, 0x01, 0x4E, 0x49, 0x41, 0x54, 0x49, (byte)0x81, (byte)0x7E};
		byte expectedChecksum = 0x1E;
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Invalid checksum (expected 0x" 
				+ HexUtils.byteToHexString(expectedChecksum) + ").")));
		
		// Call the method under test that should throw a InvalidPacketException.
		packetParser.parsePacket(byteArray, OperatingMode.API);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>An {@code InvalidPacketException} exception must be thrown when the 
	 * length in the frame does not match with the payload length.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketFrameLongerThanExpectedApiEscapeMode() throws InvalidPacketException {
		// Setup the resources for the test.
		// Real package: {0x7E, 0x00, 0x09, (byte)0x88, 0x07, 0x53, 0x48, 0x00, 0x00, 0x7D, 0x33, (byte)0xA2, 0x00, 0x20}
		byte[] byteArray = {0x7E, 0x00, 0x07, (byte)0x88, 0x07, 0x53, 0x48, 0x00, 0x00, 0x7D, 0x33, (byte)0xA2, 0x00, 0x20, 0x25, (byte)0xB4};
		byte expectedChecksum = (byte)0xC2;
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Invalid checksum (expected 0x" 
				+ HexUtils.byteToHexString(expectedChecksum) + ").")));
		
		// Call the method under test that should throw a InvalidPacketException.
		packetParser.parsePacket(byteArray, OperatingMode.API_ESCAPE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>An {@code InvalidPacketException} exception must be thrown when the 
	 * length in the frame does not match with the payload length.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	//@Test
	public final void testParsePacketFrameLongerThanExpectedApiEscapeMode2() throws InvalidPacketException {
		// Setup the resources for the test.
		byte notEscapedByte = 0x7D;
		// Real package: {0x7E, 0x00, 0x09, (byte)0x88, 0x07, 0x53, 0x48, 0x00, 0x00, 0x7D, 0x33, (byte)0xA2, 0x00, 0x20}
		byte[] byteArray = {0x7E, 0x00, 0x06, (byte)0x88, 0x07, 0x53, 0x48, 0x00, 0x00, notEscapedByte, 0x33, (byte)0xA2, 0x00, 0x20, 0x7E, (byte)0x00};
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Special byte not escaped: 0x" 
				+ HexUtils.byteToHexString((byte)(notEscapedByte & 0xFF)) + ".")));
		
		// Call the method under test that should throw a InvalidPacketException.
		packetParser.parsePacket(byteArray, OperatingMode.API_ESCAPE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>An {@code InvalidPacketException} exception must be thrown when the 
	 * checksum in the frame is not well calculated.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketInvalidChecksumApiMode() throws InvalidPacketException {
		// Setup the resources for the test.
		// Real package: {0x7E, 0x00, 0x06, 0x08, 0x01, 0x4E, 0x49, 0x41, 0x54, (byte)0xCA};
		byte[] byteArray = {0x7E, 0x00, 0x06, 0x08, 0x01, 0x4E, 0x49, 0x41, 0x54, (byte)0xCB};
		byte expectedChecksum = (byte)0xCA;
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Invalid checksum (expected 0x" 
				+ HexUtils.byteToHexString(expectedChecksum) + ").")));
		
		// Call the method under test that should throw a InvalidPacketException.
		packetParser.parsePacket(byteArray, OperatingMode.API);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>An {@code InvalidPacketException} exception must be thrown when the 
	 * checksum in the frame is not well calculated.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketInvalidChecksumApiEscapeMode() throws InvalidPacketException {
		// Setup the resources for the test.
		// Real package: {0x7E, 0x00, 0x09, (byte)0x88, 0x07, 0x53, 0x48, 0x00, 0x00, 0x7D, 0x33, (byte)0xA2, 0x00, 0x20};
		byte[] byteArray = {0x7E, 0x00, 0x09, (byte)0x88, 0x07, 0x53, 0x48, 0x00, 0x00, 0x7D, 0x33, (byte)0xA2, 0x00, 0x50};
		byte expectedChecksum = 0x20;
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Invalid checksum (expected 0x" 
				+ HexUtils.byteToHexString(expectedChecksum) + ").")));
		
		// Call the method under test that should throw a InvalidPacketException.
		packetParser.parsePacket(byteArray, OperatingMode.API_ESCAPE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>An {@code InvalidPacketException} exception must be thrown when a 
	 * special byte in the API Escaped frame is not escaped.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketSpecialByteNotEscapedApiEscapeMode() throws InvalidPacketException {
		// Setup the resources for the test.
		byte specialByte = (byte)SpecialByte.XOFF_BYTE.getValue();
		// Real package: {0x7E, 0x00, 0x09, (byte)0x88, 0x07, 0x53, 0x48, 0x00, 0x00, 0x7D, 0x33, (byte)0xA2, 0x00, 0x20};
		byte[] byteArray = {0x7E, 0x00, 0x09, (byte)0x88, 0x07, 0x53, 0x48, 0x00, 0x00, specialByte, (byte)0xA2, 0x00, 0x20};
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Special byte not escaped: 0x" 
				+ HexUtils.byteToHexString((byte)(specialByte & 0xFF)) + ".")));
		
		// Call the method under test that should throw a InvalidPacketException.
		packetParser.parsePacket(byteArray, OperatingMode.API_ESCAPE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>An {@code InvalidPacketException} exception must be thrown when a 
	 * escaped frame is being parsed as a non-escaped frame.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketApiModeWithApiEscapeFrame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)0x88, 0x07, 0x53, 0x48, 0x00, 0x00, 0x7D, 0x33, (byte)0xA2, 0x00};
		byte[] byteArray = new byte[byteData.length + 4];
		// Length of the byte array.
		byteArray[0] = 0x7E; 
		byteArray[1] = 0x00;
		byteArray[2] = 0x09;
		// Payload.
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = 0x20;
		// Real package: {0x7E, 0x00, 0x09, (byte)0x88, 0x07, 0x53, 0x48, 0x00, 0x00, 0x7D, 0x33, (byte)0xA2, 0x00, 0x20};
		
		byte expectedChecksum = (byte)0x83;
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Invalid checksum (expected 0x" 
				+ HexUtils.byteToHexString(expectedChecksum) + ").")));
		
		// Call the method under test that should throw a InvalidPacketException.
		packetParser.parsePacket(byteArray, OperatingMode.API);
	}
	
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>An {@code InvalidPacketException} exception must be thrown when a non 
	 * escaped frame with special byte is being parsed as a escaped frame.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketApiEscapeModeWithApiFrame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte specialByte = (byte)SpecialByte.ESCAPE_BYTE.getValue();
		byte[] byteData = {(byte)0x88, 0x07, 0x53, 0x48, 0x00, 0x00, specialByte, (byte)0xA2, 0x00};
		byte[] byteArray = new byte[byteData.length + 4];
		// Length of the byte array.
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x09;
		// Payload.
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = (byte)0xB6;
		// Real package: {0x7E, 0x00, 0x09, (byte)0x88, 0x07, 0x53, 0x48, 0x00, 0x00, 0x7D, 0x33, (byte)0xA2, 0x00, (byte)0xB6};
		
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Error parsing packet: Incomplete packet.")));
		
		// Call the method under test that should throw a InvalidPacketException.
		packetParser.parsePacket(byteArray, OperatingMode.API_ESCAPE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>An {@code InvalidPacketException} exception must be thrown when a non 
	 * escaped frame with special byte is being parsed as a escaped frame.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketApiEscapeModeWithApiFrame2() throws InvalidPacketException {
		// Setup the resources for the test.
		byte specialByte = (byte)SpecialByte.XON_BYTE.getValue();
		byte[] byteData = {(byte)0x88, 0x07, 0x53, 0x48, 0x00, 0x00, specialByte, (byte)0xA2, 0x00};
		byte[] byteArray = new byte[byteData.length + 4];
		// Length of the byte array.
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x09;
		// Payload.
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = 0x22;
		// Real package: {0x7E, 0x00, 0x09, (byte)0x88, 0x07, 0x53, 0x48, 0x00, 0x00, 0x11, (byte)0xA2, 0x00, 0x22};
		
		exception.expect(InvalidPacketException.class);
		exception.expectMessage(is(equalTo("Special byte not escaped: 0x" 
				+ HexUtils.byteToHexString((byte)(specialByte & 0xFF)) + ".")));
		
		// Call the method under test that should throw a InvalidPacketException.
		packetParser.parsePacket(byteArray, OperatingMode.API_ESCAPE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid API byte array with unknown frame type must result in a valid 
	 * API packet of {@code UnknownXBeePacket.class} type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketApiModeNonRecognizeFrameTypePayload() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] payload = new byte[] {0x13};
		byte[] byteArray = new byte[payload.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x01;
		System.arraycopy(payload, 0, byteArray, 3, payload.length);
		byteArray[byteArray.length - 1] = (byte)0xEC;
		// Real package: {0x7E, 0x00, 0x01, 0x13, (byte)0xEC};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be an unknown packet", packet, is(instanceOf(UnknownXBeePacket.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid API escaped byte array with unknown frame type must result in 
	 * a valid API packet of {@code UnknownXBeePacket.class} type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketApiEscapeModeNonRecognizeFrameTypePayload() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] payload = new byte[] {0x7D, 0x33};
		byte[] byteArray = new byte[payload.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x01;
		System.arraycopy(payload, 0, byteArray, 3, payload.length);
		byteArray[byteArray.length - 1] = (byte)0xEC;
		// Real package: {0x7E, 0x00, 0x01, 0x7D, 0x33, (byte)0xEC};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API_ESCAPE);
		
		// Verify the result.
		assertThat("Packet must be an unknown packet", packet, is(instanceOf(UnknownXBeePacket.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length - 1)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArrayEscaped(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid API byte array with known frame type must result in a valid 
	 * API packet of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketApiModeValidFrame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {0x17, 0x01, 0x00, 0x00, 0x00, 0x40, (byte)0x9D, 0x5E, 0x49, (byte)0xD5, (byte)0xFF, (byte)0xFE, 0x02, 0x4E, 0x49};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x0F;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		byteArray[byteArray.length - 1] = (byte)0xF8;
		// Real package: {0x7E, 0x00, 0x0F, 0x17, 0x01, 0x00, 0x00, 0x00, 0x40, (byte)0x9D, 0x5E, 0x49, (byte)0xD5, (byte)0xFF, (byte)0xFE, 0x02, 0x4E, 0x49, (byte)0xF8};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet mustn't be an unknown packet", packet, is(not(instanceOf(UnknownXBeePacket.class))));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(ByteUtils.byteArrayToInt(new byte[]{byteArray[1], byteArray[2]}))));
		assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid API escaped byte array (with special chars) with known frame 
	 * type must result in a valid API packet of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketApiEscapeModeValidFrame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)0x88, 0x07, 0x53, 0x48, 0x00, 0x00, 0x7D, 0x33, (byte)0xA2, 0x00};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x09;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		byteArray[byteArray.length - 1] = (byte)0x20;
		// Real package: {0x7E, 0x00, 0x09, (byte)0x88, 0x07, 0x53, 0x48, 0x00, 0x00, 0x7D, 0x33, (byte)0xA2, 0x00, 0x20};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API_ESCAPE);
		
		// Verify the result.
		assertThat("Packet mustn't be an unknown packet", packet, is(not(instanceOf(UnknownXBeePacket.class))));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(ByteUtils.byteArrayToInt(new byte[]{byteArray[1], byteArray[2]}))));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArrayEscaped(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid API escaped byte array (without special chars) with known 
	 * frame type must result in a valid API packet of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketApiEscapeModeValidFrameNoEscapes() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {0x01, (byte)0xFF, 0x00, 0x00, 0x00, 0x40, (byte)0x9D, 0x5E, 0x49, (byte)0xD5, (byte)0xFF, (byte)0xFE, 0x02, 0x4E, 0x49};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x0F;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		byteArray[byteArray.length - 1] = (byte)0x10;
		// Real package: {0x7E, 0x00, 0x0F, 0x01, 0xFF, 0x00, 0x00, 0x00, 0x40, (byte)0x9D, 0x5E, 0x49, (byte)0xD5, (byte)0xFF, (byte)0xFE, 0x02, 0x4E, 0x49, 0x10};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API_ESCAPE);
		
		// Verify the result.
		assertThat("Packet mustn't be an unknown packet", packet, is(not(instanceOf(UnknownXBeePacket.class))));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(ByteUtils.byteArrayToInt(new byte[]{byteArray[1], byteArray[2]}))));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArrayEscaped(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid API escaped byte array (with length escaped) with known 
	 * frame type must result in a valid API packet of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketApiEscapeModeLengthEscaped() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)0x88, (byte)0x83, 0x4D, 0x59, 0x00, 0x31, 0x30, 0x2E, 0x31, 0x30, 0x31, 0x2E, 0x32, 0x2E, 0x31, 0x35, 0x37};
		byte[] byteArray = new byte[byteData.length + 5];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x7D;
		byteArray[3] = 0x31;
		System.arraycopy(byteData, 0, byteArray, 4, byteData.length);
		byteArray[byteArray.length - 1] = 0x02;
		// Real package: {0x7E, 0x00, 0x7D, 0x31, (byte)0x88, (byte)0x83, 0x4D, 0x59, 0x00, 0x31, 0x30, 0x2E, 0x31, 0x30, 0x31, 0x2E, 0x32, 0x2E, 0x31, 0x35, 0x37, 0x02};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API_ESCAPE);
		
		// Verify the result.
		assertThat("Packet mustn't be an unknown packet", packet, is(not(instanceOf(UnknownXBeePacket.class))));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArrayEscaped(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid API escaped byte array (with length escaped) with known 
	 * frame type must result in a valid API packet of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketApiEscapeModeChecksumEscaped() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)0x88, 0x55, 0x4D, 0x54, 0x00, 0x03};
		byte[] byteArray = new byte[byteData.length + 5];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x06;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Escaped checksum.
		byteArray[byteArray.length - 2] = 0x7D;
		byteArray[byteArray.length - 1] = 0x5E;
		// Real package: {0x7E, 0x00, 0x06, (byte)0x88, 0x55, 0x4D, 0x54, 0x00, 0x03, 0x7D, 0x5E};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API_ESCAPE);
		
		// Verify the result.
		assertThat("Packet mustn't be an unknown packet", packet, is(not(instanceOf(UnknownXBeePacket.class))));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArrayEscaped(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid Tx (Transmit) request 64-bits API byte array must result in a 
	 * valid API packet of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketTx64Frame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.TX_64.getValue(), 0x01, 0x00, 
				0x13, (byte)0xA2, 0x00, 0x40, 0x32, 0x16, (byte)0x2E, 0x00, 0x42, 0x79, 0x65};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x0E;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = 0x73;
		// Real package: {7E 00 0E 00 01 00 13 A2 00 40 32 16 2E 00 42 79 65 73};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be a Tx (Transmit) Request 64-bits packet", packet, is(instanceOf(TX64Packet.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid Tx (Transmit) request 16-bits API byte array must result in a 
	 * valid API packet of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketTx16Frame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.TX_16.getValue(), 0x01, 
				0x12, 0x34, 0x00, 0x42, 0x79, 0x65};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x08;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = (byte)0x97;
		// Real package: {7E 00 08 01 01 12 34 00 42 79 65 97};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be a Tx (Transmit) Request 16-bits packet", packet, is(instanceOf(TX16Packet.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid Remote AT Command (Wi-Fi) API byte array must result in a 
	 * valid API packet of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketRemoteATCommandWifiFrame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.REMOTE_AT_COMMAND_REQUEST_WIFI.getValue(), 
				0x01, 0x00, 0x00, 0x00, 0x00, (byte) 0xC0, (byte) 0xA8, 0x01, 0x02, 0x02, 0x4E, 0x49};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x0D;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = (byte)0xF3;
		// Real package: {7E 00 0D 07 01 00 00 00 00 C0 A8 01 02 02 4E 49 F3};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be a Remote AT Command (Wi-Fi) packet", packet, is(instanceOf(RemoteATCommandWifiPacket.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid AT Command API byte array must result in a valid API packet 
	 * of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketATCommandFrame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.AT_COMMAND.getValue(), 0x01, 
				0x4E, 0x49};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x04;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = 0x5F;
		// Real package: {7E 00 04 08 01 4E 49 5F};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be an AT Command packet", packet, is(instanceOf(ATCommandPacket.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid AT Command Queue API byte array must result in a valid API packet 
	 * of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketATCommandQueueFrame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.AT_COMMAND_QUEUE.getValue(), 0x01, 
				0x4E, 0x49};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x04;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = 0x5E;
		// Real package: {7E 00 04 09 01 4E 49 5E};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be an AT Command Queue packet", packet, is(instanceOf(ATCommandQueuePacket.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid Transmit request API byte array must result in a valid API packet 
	 * of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketTransmitRequestFrame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.TRANSMIT_REQUEST.getValue(), 0x01, 
				0x00, 0x13, (byte)0xA2, 0x00, 0x40, 0x32, 0x16, (byte)0x2E, (byte)0xFF, (byte)0xFE, 0x00, 0x00, 0x42, 0x79, 0x65};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x11;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = 0x66;
		// Real package: {7E 00 11 10 01 00 13 A2 00 40 32 16 2E FF FE 00 00 42 79 65 66};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be a Transmit request packet", packet, is(instanceOf(TransmitPacket.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid Explicit Addressing Command API byte array must result in a 
	 * valid API packet of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketExplicitAddressingCommandFrame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.EXPLICIT_ADDRESSING_COMMAND_FRAME.getValue(), 0x01, 
				0x00, 0x13, (byte)0xA2, 0x00, 0x40, (byte)0xAD, 0x15, 0x64, (byte)0xFF, (byte)0xFE, (byte)0xE8, (byte)0xE8, 
				0x00, 0x11, (byte)0xC1, 0x05, 0x00, 0x00, 0x42, 0x79, 0x65};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x17;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = 0x0E;
		// Real package: {7E 00 17 11 01 00 13 A2 00 40 AD 15 64 FF FE E8 E8 00 11 C1 05 00 00 42 79 65 0E};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be a Explicit Addressing packet", packet, is(instanceOf(ExplicitAddressingPacket.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid Remote AT Command API byte array must result in a valid API packet 
	 * of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketRemoteATCommandFrame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.REMOTE_AT_COMMAND_REQUEST.getValue(), 0x01, 
				0x00, 0x13, (byte)0xA2, 0x00, 0x40, 0x32, 0x16, (byte)0x2E, (byte)0xFF, (byte)0xFE, 0x02, 0x4E, 0x49};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x0F;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = (byte)0xE6;
		// Real package: {7E 00 0F 17 01 00 13 A2 00 40 32 16 2E FF FE 02 4E 49 E6};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be a Remote AT Command packet", packet, is(instanceOf(RemoteATCommandPacket.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid TX SMS API byte array must result in a valid API packet 
	 * of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketTXSMSFrame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.TX_SMS.getValue(), 0x01, 
				0x00, 0x2B, 0x33, 0x34, 0x36, 0x35, 0x35, 0x35, 0x35, 0x35, 0x32, 0x32, 
				0x32, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x48, 0x69, 0x21};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x1A;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = (byte)0xA6;
		// Real package: {7E 00 1A 1F 01 00 2B 33 34 36 35 35 35 35 35 32 32 32 00 00 00 00 00 00 00 00 48 69 21 A6};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be a TX SMS packet", packet, is(instanceOf(TXSMSPacket.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid TX IPv4 API byte array must result in a valid API packet 
	 * of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketTXIPv4Frame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.TX_IPV4.getValue(), 0x01, 0x10, 0x60, 0x01, 
				0x01, 0x12, 0x34, (byte) 0x88, (byte) 0xDB, 0x00, 0x00, 0x48, 0x65, 0x79};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x0F;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = (byte)0x9D;
		// Real package: {7E 00 0F 20 01 10 60 01 01 12 34 88 DB 00 00 48 65 79 9D};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be a TX IPv4 packet", packet, is(instanceOf(TXIPv4Packet.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid Send Data Request API byte array must result in a valid API 
	 * packet of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketSendDataRequestFrame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.SEND_DATA_REQUEST.getValue(), 
				0x01, 0x08, 0x74, 0x65, 0x73, 0x74, 0x2E, 0x74, 0x78, 0x74, 0x0A, 0x74, 0x65, 0x78, 
				0x74, 0x2F, 0x70, 0x6C, 0x61, 0x69, 0x6E, 0x00, 0x00, 0x54, 0x65, 0x73, 0x74};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x1C;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = (byte)0xCE;
		// Real package: {7E 00 1C 28 01 08 74 65 73 74 2E 74 78 74 0A 74 65 78 74 2F 70 6C 61 69 6E 00 00 54 65 73 74 CE};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be a Send Data Request packet", packet, is(instanceOf(SendDataRequestPacket.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid Device Response API byte array must result in a valid API 
	 * packet of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketDeviceResponseFrame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.DEVICE_RESPONSE.getValue(), 
				0x01, 0x05, 0x00, 0x4F, 0x4B};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x06;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = (byte)0x35;
		// Real package: {7E 00 06 2A 01 05 00 4F 4B 35};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be a Device Response packet", packet, is(instanceOf(DeviceResponsePacket.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid Rx (Receive) 64-bit API byte array must result in a valid API packet 
	 * of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketRx64Frame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.RX_64.getValue(), 
				0x00, 0x13, (byte)0xA2, 0x00, 0x40, 0x32, 0x16, (byte)0x2E, 0x00, 0x01, 0x42, 0x79, 0x65};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x0E;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = (byte)0xF3;
		// Real package: {7E 00 0E 80 00 13 A2 00 40 32 16 2E 00 01 42 79 65 F3};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be an Rx (Receive) 64-bit packet", packet, is(instanceOf(RX64Packet.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid Rx (Receive) 16-bit API byte array must result in a valid API packet 
	 * of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketRx16Frame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.RX_16.getValue(), 
				0x12, 0x34, 0x00, 0x01, 0x42, 0x79, 0x65};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x08;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = 0x17;
		// Real package: {7E 00 08 81 12 34 00 01 42 79 65 17};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be an Rx (Receive) 16-bit packet", packet, is(instanceOf(RX16Packet.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid IO Data Sample RX 64-bit API byte array must result in a valid 
	 * API packet of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketRxIO64Frame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.RX_IO_64.getValue(), 
				0x00, 0x13, (byte)0xA2, 0x00, 0x40, 0x32, 0x16, (byte)0x2E, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x10;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = 0x11;
		// Real package: {7E 00 10 82 00 13 A2 00 40 32 16 2E 00 01 00 00 00 00 00 11};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be an IO Data Sample RX 64-bit packet", packet, is(instanceOf(RX64IOPacket.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid IO Data Sample Rx (Receive) 16-bit API byte array must result 
	 * in a valid API packet of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketRxIO16Frame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.RX_IO_16.getValue(), 
				0x12, 0x34, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x0A;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = 0x35;
		// Real package: {7E 00 0A 83 12 34 00 01 00 00 00 00 00 35};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be an IO Data Sample Rx 16-bit packet", packet, is(instanceOf(RX16IOPacket.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid Remote AT Command Response (Wi-Fi) API byte array must result 
	 * in a valid API packet of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketRemoteATCommandResponseWifiFrame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.REMOTE_AT_COMMAND_RESPONSE_WIFI.getValue(), 
				0x01, 0x00, 0x00, 0x00, 0x00, (byte) 0xC0, (byte) 0xA8, 0x01, 0x02, 0x4E, 
				0x49, 0x00, 0x58, 0x42, 0x65, 0x65};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x11;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = 0x11;
		// Real package: {7E 00 11 87 01 00 00 00 00 C0 A8 01 02 4E 49 00 58 42 65 65 11};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be a Remote AT Command Response (Wi-Fi) packet", packet, is(instanceOf(RemoteATCommandResponseWifiPacket.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid AT Command Response API byte array must result in a valid API packet 
	 * of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketATCommandResponseFrame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.AT_COMMAND_RESPONSE.getValue(), 0x01, 
				0x4E, 0x49, 0x00, 0x42, 0x79, 0x65};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x08;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = (byte)0xBF;
		// Real package: {7E 00 08 88 01 4E 49 00 42 79 65 BF};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be an AT Command Response packet", packet, is(instanceOf(ATCommandResponsePacket.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid Transmit Status API byte array must result in a valid API packet 
	 * of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketTxStatusFrame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.TX_STATUS.getValue(), 0x01, 
				0x00};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x03;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = 0x75;
		// Real package: {7E 00 03 89 01 00 75};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be a Tx (Transmit) status packet", packet, is(instanceOf(TXStatusPacket.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid Modem Status API byte array must result in a valid API packet 
	 * of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketModemStatusFrame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.MODEM_STATUS.getValue(), 
				0x06};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x02;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = 0x6F;
		// Real package: {7E 00 02 8A 06 6F};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be a Modem status packet", packet, is(instanceOf(ModemStatusPacket.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid Transmit Status API byte array must result in a valid API packet 
	 * of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketTransmitStatusFrame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.TRANSMIT_STATUS.getValue(), 
				0x01, 0x12, 0x34, 0x00, 0x00, 0x00};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x07;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = 0x2D;
		// Real package: {7E 00 07 8B 01 12 34 00 00 00 2D};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be a Transmit status packet", packet, is(instanceOf(TransmitStatusPacket.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid IO Data Sample Rx Indicator (Wi-Fi) API byte array must result
	 * in a valid API packet of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketIODataSampleRxIndicatorWifiFrame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.IO_DATA_SAMPLE_RX_INDICATOR_WIFI.getValue(), 
				0x00, 0x00, 0x00, 0x00, (byte) 0xC0, (byte) 0xA8, 0x01, 0x02, (byte) 0xD1, 
				0x00, 0x01, 0x00, 0x01, 0x02, 0x00, (byte) 0xC0, 0x12, 0x50};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x13;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = 0x0E;
		// Real package: {7E 00 13 8F 00 00 00 00 C0 A8 01 02 D1 00 01 00 01 02 00 C0 12 50 0E};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be an IO Data Sample Rx Indicator (Wi-Fi) packet", packet, is(instanceOf(IODataSampleRxIndicatorWifiPacket.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid Receive Packet API byte array must result in a valid 
	 * API packet of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketReceivePacketFrame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.RECEIVE_PACKET.getValue(), 
				0x00, 0x13, (byte)0xA2, 0x00, 0x40, 0x32, 0x16, (byte)0x2E, (byte)0xFF, (byte)0xFE, 0x01, 0x42, 0x79, 0x65};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x0F;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = (byte)0xE6;
		// Real package: {7E 00 0F 90 00 13 A2 00 40 32 16 2E FF FE 01 42 79 65 E6};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be a Receive packet", packet, is(instanceOf(ReceivePacket.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid Explicit Rx Indicator API byte array must result in a valid 
	 * API packet of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketExplicitRxIndicatorFrame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.EXPLICIT_RX_INDICATOR.getValue(), 
				0x00, 0x13, (byte)0xA2, 0x00, 0x40, (byte)0xAD, 0x15, 0x64, (byte)0xFF, (byte)0xFE, (byte)0xE8, (byte)0xE8, 
				0x00, 0x11, (byte)0xC1, 0x05, 0x01, 0x42, 0x79, 0x65};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x15;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = (byte)0x8E;
		// Real package: {7E 00 15 91 00 13 A2 00 40 AD 15 64 FF FE E8 E8 00 11 C1 05 01 42 79 65 8E};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be a Explicit Rx Indicator packet", packet, is(instanceOf(ExplicitRxIndicatorPacket.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid IO Data Sample Rx Indicator API byte array must result in a valid 
	 * API packet of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketIODataSampleRxFrame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.IO_DATA_SAMPLE_RX_INDICATOR.getValue(), 
				0x00, 0x13, (byte)0xA2, 0x00, 0x40, 0x32, 0x16, (byte)0x2E, (byte)0xFF, (byte)0xFE, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x12;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = 0x03;
		// Real package: {7E 00 12 92 00 13 A2 00 40 32 16 2E FF FE 01 01 00 00 00 00 00 03};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be an IO Data Sample RX Indicator packet", packet, is(instanceOf(IODataSampleRxIndicatorPacket.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid Remote AT Command Response API byte array must result in a valid API packet 
	 * of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketRemoteATCommandResponseFrame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.REMOTE_AT_COMMAND_RESPONSE.getValue(), 0x01, 
				0x00, 0x13, (byte)0xA2, 0x00, 0x40, 0x32, 0x16, (byte)0x2E, (byte)0xFF, (byte)0xFE, 0x4E, 0x49, 0x00, 0x42, 0x79, 0x65};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x12;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = 0x48;
		// Real package: {7E 00 12 97 01 00 13 A2 00 40 32 16 2E FF FE 4E 49 00 42 79 65 48};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be a Remote AT Command Response packet", packet, is(instanceOf(RemoteATCommandResponsePacket.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid RX SMS API byte array must result in a valid API packet 
	 * of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketRXSMSFrame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.RX_SMS.getValue(), 0x35, 0x35, 0x35, 0x32, 0x33, 0x30, 
				0x32, 0x33, 0x36, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
				0x52, 0x65, 0x73, 0x70, 0x6F, 0x6E, 0x73, 0x65};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x1D;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = (byte)0x42;
		// Real package: {7E 00 1D 9F 35 35 35 32 33 30 32 33 36 00 00 00 00 00 00 00 00 00 00 00 52 65 73 70 6F 6E 73 65 42};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be a RX SMS packet", packet, is(instanceOf(RXSMSPacket.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid RX IPv4 API byte array must result in a valid API packet 
	 * of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketRXIPv4Frame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.RX_IPV4.getValue(), 0x10, 0x60, 0x01, 0x01, 0x55, 
				0x00, (byte) 0xAB, (byte) 0xCD, 0x01, 0x00, 0x48, 0x69};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x0D;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = (byte)0x5E;
		// Real package: {7E 00 0D B0 10 60 01 01 55 00 AB CD 01 00 48 69 5E};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be a RX IPv4 packet", packet, is(instanceOf(RXIPv4Packet.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid Send Data Response API byte array must result in a valid API
	 * packet of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketSendDataResponseFrame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.SEND_DATA_RESPONSE.getValue(), 0x02, 0x00};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x03;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = (byte)0x45;
		// Real package: {7E 00 03 B8 02 00 45};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be a Send Data Response packet", packet, is(instanceOf(SendDataResponsePacket.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid Device Request API byte array must result in a valid API
	 * packet of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketDeviceRequestFrame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.DEVICE_REQUEST.getValue(), 
				0x01, 0x00, 0x00, 0x06, 0x74, 0x61, 0x72, 0x67, 0x65, 0x74, 0x48, 0x65, 0x79};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x0E;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = (byte)0x92;
		// Real package: {7E 00 0E B9 01 00 00 06 74 61 72 67 65 74 48 65 79 92};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be a Device Request packet", packet, is(instanceOf(DeviceRequestPacket.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid Device Response Status API byte array must result in a valid
	 * API packet of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketDeviceResponseStatusFrame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.DEVICE_RESPONSE_STATUS.getValue(), 0x02, 0x20};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x03;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = (byte)0x23;
		// Real package: {7E 00 03 BA 02 20 23};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be a Device Response Status packet", packet, is(instanceOf(DeviceResponseStatusPacket.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid Frame Error API byte array must result in a valid API packet
	 * of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketFrameErrorFrame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.FRAME_ERROR.getValue(), 0x02};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x02;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = (byte)0xFF;
		// Real package: {7E 00 02 FE 02 FF};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be a Frame Error packet", packet, is(instanceOf(FrameErrorPacket.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeePacketParser#parsePacket(byte[], OperatingMode)}.
	 * 
	 * <p>A valid Generic API byte array must result in a valid API packet 
	 * of the right type.</p>
	 * 
	 * @throws InvalidPacketException 
	 */
	@Test
	public final void testParsePacketGenericFrame() throws InvalidPacketException {
		// Setup the resources for the test.
		byte[] byteData = {(byte)APIFrameType.GENERIC.getValue(), 
				0x62, 0x79, 0x65};
		byte[] byteArray = new byte[byteData.length + 4];
		byteArray[0] = 0x7E;
		byteArray[1] = 0x00;
		byteArray[2] = 0x04;
		System.arraycopy(byteData, 0, byteArray, 3, byteData.length);
		// Checksum.
		byteArray[byteArray.length - 1] = (byte)0xC0;
		// Real package: {7E 00 04 FF 62 79 65 C0};
		
		// Call the method under test.
		XBeePacket packet = packetParser.parsePacket(byteArray, OperatingMode.API);
		
		// Verify the result.
		assertThat("Packet must be a Generic packet", packet, is(instanceOf(GenericXBeePacket.class)));
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(byteData.length)));
		// Do not use this since the data is always API and never API_ESCAPE.
		//assertThat("Returned data array is not the expected one", packet.getPacketData(), is(equalTo(byteData)));
		
		assertThat("Generated API array from packet is not the expected one", packet.generateByteArray(), is(equalTo(byteArray)));
	}
}
