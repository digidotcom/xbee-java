/**
 * Copyright (c) 2014 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
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
	public final void testParsePacketNullInputStreamApiMode() throws InvalidPacketException {
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
	public final void testParsePacketNullInputStreamApiEscapeMode() throws InvalidPacketException {
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
	public final void testParsePacketNullInputStreamAtMode() throws InvalidPacketException {
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
	public final void testParsePacketNullInputStreamUnknownMode() throws InvalidPacketException {
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
	public final void testParsePacketNullInputStreamNullMode() throws InvalidPacketException {
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
}
