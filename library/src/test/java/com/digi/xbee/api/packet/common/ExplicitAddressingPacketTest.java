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
package com.digi.xbee.api.packet.common;

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

import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

public class ExplicitAddressingPacketTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	public ExplicitAddressingPacketTest() {
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
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitAddressingPacket#createPacket(byte[])}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a 
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Setup the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Explicit Addressing packet payload cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		ExplicitAddressingPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitAddressingPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Setup the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Explicit Addressing packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		ExplicitAddressingPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitAddressingPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Setup the resources for the test.
		int frameType = APIFrameType.EXPLICIT_ADDRESSING_COMMAND_FRAME.getValue();
		int frameID = 0x01;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		byte[] clusterID = new byte[]{0x15}; // Invalid Cluster ID.
		byte[] profileID = new byte[]{(byte) 0xC1, 0x05};
		int broadcastRadius = 0;
		int options = 0x06;
		
		byte[] payload = new byte[19];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(dest64Addr.getValue(), 0, payload, 2, dest64Addr.getValue().length);
		System.arraycopy(dest16Addr.getValue(), 0, payload, 10, dest16Addr.getValue().length);
		payload[12] = (byte)sourceEndpoint;
		payload[13] = (byte)destEndpoint;
		System.arraycopy(clusterID, 0, payload, 14, clusterID.length);
		System.arraycopy(profileID, 0, payload, 15, profileID.length);
		payload[17] = (byte)broadcastRadius;
		payload[18] = (byte)options;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Explicit Addressing packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		ExplicitAddressingPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitAddressingPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array not including the Frame type.</p>
	 */
	@Test
	public final void testCreatePacketPayloadNotIncludingFrameType() {
		// Setup the resources for the test.
		int frameID = 0x01;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		byte[] clusterID = new byte[]{0x15, 0x54};
		byte[] profileID = new byte[]{(byte) 0xC1, 0x05};
		int broadcastRadius = 0;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		byte[] payload = new byte[19 + data.length];
		payload[0] = (byte)frameID;
		System.arraycopy(dest64Addr.getValue(), 0, payload, 1, dest64Addr.getValue().length);
		System.arraycopy(dest16Addr.getValue(), 0, payload, 9, dest16Addr.getValue().length);
		payload[11] = (byte)sourceEndpoint;
		payload[12] = (byte)destEndpoint;
		System.arraycopy(clusterID, 0, payload, 13, clusterID.length);
		System.arraycopy(profileID, 0, payload, 15, profileID.length);
		payload[17] = (byte)broadcastRadius;
		payload[18] = (byte)options;
		System.arraycopy(data, 0, payload, 19, data.length);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not an Explicit Addressing packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		ExplicitAddressingPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitAddressingPacket#createPacket(byte[])}.
	 * 
	 * <p>A valid Explicit Addressing packet with the provided options without 
	 * data is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutData() {
		// Setup the resources for the test.
		int frameType = APIFrameType.EXPLICIT_ADDRESSING_COMMAND_FRAME.getValue();
		int frameID = 0x01;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		byte[] clusterID = new byte[]{0x15, 0x54};
		byte[] profileID = new byte[]{(byte) 0xC1, 0x05};
		int broadcastRadius = 0;
		int options = 0x06;
		
		byte[] payload = new byte[20];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(dest64Addr.getValue(), 0, payload, 2, dest64Addr.getValue().length);
		System.arraycopy(dest16Addr.getValue(), 0, payload, 10, dest16Addr.getValue().length);
		payload[12] = (byte)sourceEndpoint;
		payload[13] = (byte)destEndpoint;
		System.arraycopy(clusterID, 0, payload, 14, clusterID.length);
		System.arraycopy(profileID, 0, payload, 16, profileID.length);
		payload[18] = (byte)broadcastRadius;
		payload[19] = (byte)options;
		
		// Call the method under test.
		ExplicitAddressingPacket packet = ExplicitAddressingPacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination 64-bit address is not the expected one", packet.get64bitDestinationAddress(), is(equalTo(dest64Addr)));
		assertThat("Returned destination 16-bit address is not the expected one", packet.get16bitDestinationAddress(), is(equalTo(dest16Addr)));
		assertThat("Returned source endpoint is not the expected one", packet.getSourceEndpoint(), is(equalTo(sourceEndpoint)));
		assertThat("Returned destination endpoint is not the expected one", packet.getDestinationEndpoint(), is(equalTo(destEndpoint)));
		assertThat("Returned cluster ID is not the expected one", packet.getClusterID(), is(equalTo(clusterID)));
		assertThat("Returned profile ID is not the expected one", packet.getProfileID(), is(equalTo(profileID)));
		assertThat("Returned broadcast radius is not the expected one", packet.getBroadcastRadius(), is(equalTo(broadcastRadius)));
		assertThat("Returned transmit options is not the expected one", packet.getTransmitOptions(), is(equalTo(options)));
		assertThat("Returned RF Data is not the expected one", packet.getRFData(), is(nullValue()));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitAddressingPacket#createPacket(byte[])}.
	 * 
	 * <p>A valid Explicit Addressing packet with the provided options and data is 
	 * created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithData() {
		// Setup the resources for the test.
		int frameType = APIFrameType.EXPLICIT_ADDRESSING_COMMAND_FRAME.getValue();
		int frameID = 0x01;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		byte[] clusterID = new byte[]{0x15, 0x54};
		byte[] profileID = new byte[]{(byte) 0xC1, 0x05};
		int broadcastRadius = 0;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		byte[] payload = new byte[20 + data.length];
		payload[0] = (byte)frameType;
		payload[1] = (byte)frameID;
		System.arraycopy(dest64Addr.getValue(), 0, payload, 2, dest64Addr.getValue().length);
		System.arraycopy(dest16Addr.getValue(), 0, payload, 10, dest16Addr.getValue().length);
		payload[12] = (byte)sourceEndpoint;
		payload[13] = (byte)destEndpoint;
		System.arraycopy(clusterID, 0, payload, 14, clusterID.length);
		System.arraycopy(profileID, 0, payload, 16, profileID.length);
		payload[18] = (byte)broadcastRadius;
		payload[19] = (byte)options;
		System.arraycopy(data, 0, payload, 20, data.length);
		
		// Call the method under test.
		ExplicitAddressingPacket packet = ExplicitAddressingPacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination 64-bit address is not the expected one", packet.get64bitDestinationAddress(), is(equalTo(dest64Addr)));
		assertThat("Returned destination 16-bit address is not the expected one", packet.get16bitDestinationAddress(), is(equalTo(dest16Addr)));
		assertThat("Returned source endpoint is not the expected one", packet.getSourceEndpoint(), is(equalTo(sourceEndpoint)));
		assertThat("Returned destination endpoint is not the expected one", packet.getDestinationEndpoint(), is(equalTo(destEndpoint)));
		assertThat("Returned cluster ID is not the expected one", packet.getClusterID(), is(equalTo(clusterID)));
		assertThat("Returned profile ID is not the expected one", packet.getProfileID(), is(equalTo(profileID)));
		assertThat("Returned broadcast radius is not the expected one", packet.getBroadcastRadius(), is(equalTo(broadcastRadius)));
		assertThat("Returned transmit options is not the expected one", packet.getTransmitOptions(), is(equalTo(options)));
		assertThat("Returned RF Data is not the expected one", packet.getRFData(), is(equalTo(data)));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitAddressingPacket#ExplicitAddressingPacket(int, XBee64BitAddress, XBee16BitAddress, int, int, byte[], byte[], int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Addressing packet but with a {@code null} 
	 * 64-bit destination address. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateExplicitAddressingPacket64BitAddressNull() {
		// Setup the resources for the test.
		int frameID = 0x01;
		XBee64BitAddress dest64Addr = null;
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		byte[] clusterID = new byte[]{0x15, 0x54};
		byte[] profileID = new byte[]{(byte) 0xC1, 0x05};
		int broadcastRadius = 0;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("64-bit destination address cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new ExplicitAddressingPacket(frameID, dest64Addr, dest16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, broadcastRadius, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitAddressingPacket#ExplicitAddressingPacket(int, XBee64BitAddress, XBee16BitAddress, int, int, byte[], byte[], int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Addressing packet but with a {@code null} 
	 * 16-bit destination address. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateExplicitAddressingPacket16BitAddressNull() {
		// Setup the resources for the test.
		int frameID = 0x01;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = null;
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		byte[] clusterID = new byte[]{0x15, 0x54};
		byte[] profileID = new byte[]{(byte) 0xC1, 0x05};
		int broadcastRadius = 0;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("16-bit destination address cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new ExplicitAddressingPacket(frameID, dest64Addr, dest16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, broadcastRadius, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitAddressingPacket#ExplicitAddressingPacket(int, XBee64BitAddress, XBee16BitAddress, int, int, byte[], byte[], int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Addressing packet but with a {@code null} 
	 * Cluster ID. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateExplicitAddressingPacketClusterIDNull() {
		// Setup the resources for the test.
		int frameID = 0x01;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		byte[] clusterID = null;
		byte[] profileID = new byte[]{(byte) 0xC1, 0x05};
		int broadcastRadius = 0;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Cluster ID cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new ExplicitAddressingPacket(frameID, dest64Addr, dest16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, broadcastRadius, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitAddressingPacket#ExplicitAddressingPacket(int, XBee64BitAddress, XBee16BitAddress, int, int, byte[], byte[], int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Addressing packet but with a {@code null} 
	 * Profile ID. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateExplicitAddressingPacketProfileIDNull() {
		// Setup the resources for the test.
		int frameID = 0x01;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		byte[] clusterID = new byte[]{0x15, 0x54};
		byte[] profileID = null;
		int broadcastRadius = 0;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Profile ID cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new ExplicitAddressingPacket(frameID, dest64Addr, dest16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, broadcastRadius, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitAddressingPacket#ExplicitAddressingPacket(int, XBee64BitAddress, XBee16BitAddress, int, int, byte[], byte[], int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Addressing packet but with a source endpoint 
	 * bigger than 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateExplicitAddressingPacketSourceEndpointBiggerThan255() {
		// Setup the resources for the test.
		int frameID = 0x01;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 748;
		int destEndpoint = 0xA1;
		byte[] clusterID = new byte[]{0x15, 0x54};
		byte[] profileID =  new byte[]{(byte) 0xC1, 0x05};
		int broadcastRadius = 0;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Source endpoint must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ExplicitAddressingPacket(frameID, dest64Addr, dest16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, broadcastRadius, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitAddressingPacket#ExplicitAddressingPacket(int, XBee64BitAddress, XBee16BitAddress, int, int, byte[], byte[], int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Addressing packet but with a negative source 
	 * endpoint bigger than 255. This must throw an 
	 * {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateExplicitAddressingPacketSourceEndpointNegative() {
		// Setup the resources for the test.
		int frameID = 0x01;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = -149;
		int destEndpoint = 0xA1;
		byte[] clusterID = new byte[]{0x15, 0x54};
		byte[] profileID =  new byte[]{(byte) 0xC1, 0x05};
		int broadcastRadius = 0;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Source endpoint must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ExplicitAddressingPacket(frameID, dest64Addr, dest16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, broadcastRadius, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitAddressingPacket#ExplicitAddressingPacket(int, XBee64BitAddress, XBee16BitAddress, int, int, byte[], byte[], int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Addressing packet but with a dest. endpoint 
	 * bigger than 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateExplicitAddressingPacketDestEndpointBiggerThan255() {
		// Setup the resources for the test.
		int frameID = 0x01;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 748;
		byte[] clusterID = new byte[]{0x15, 0x54};
		byte[] profileID =  new byte[]{(byte) 0xC1, 0x05};
		int broadcastRadius = 0;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Destination endpoint must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ExplicitAddressingPacket(frameID, dest64Addr, dest16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, broadcastRadius, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitAddressingPacket#ExplicitAddressingPacket(int, XBee64BitAddress, XBee16BitAddress, int, int, byte[], byte[], int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Addressing packet but with a negative dest. 
	 * endpoint. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateExplicitAddressingPacketDestEndpointNegative() {
		// Setup the resources for the test.
		int frameID = 0x01;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = -149;
		byte[] clusterID = new byte[]{0x15, 0x54};
		byte[] profileID =  new byte[]{(byte) 0xC1, 0x05};
		int broadcastRadius = 0;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Destination endpoint must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ExplicitAddressingPacket(frameID, dest64Addr, dest16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, broadcastRadius, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitAddressingPacket#ExplicitAddressingPacket(int, XBee64BitAddress, XBee16BitAddress, int, int, byte[], byte[], int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Addressing packet but with a broadcast 
	 * radius bigger than 255. This must throw an 
	 * {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateExplicitAddressingPacketBroadcastRadiusBiggerThan255() {
		// Setup the resources for the test.
		int frameID = 0x01;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		byte[] clusterID = new byte[]{0x15, 0x54};
		byte[] profileID =  new byte[]{(byte) 0xC1, 0x05};
		int broadcastRadius = 815;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Broadcast radius must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ExplicitAddressingPacket(frameID, dest64Addr, dest16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, broadcastRadius, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitAddressingPacket#ExplicitAddressingPacket(int, XBee64BitAddress, XBee16BitAddress, int, int, byte[], byte[], int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Addressing packet but with a negative 
	 * broadcast radius. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateExplicitAddressingPacketBroadcastRadiusNegative() {
		// Setup the resources for the test.
		int frameID = 0x01;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		byte[] clusterID = new byte[]{0x15, 0x54};
		byte[] profileID =  new byte[]{(byte) 0xC1, 0x05};
		int broadcastRadius = -238;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Broadcast radius must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ExplicitAddressingPacket(frameID, dest64Addr, dest16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, broadcastRadius, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitAddressingPacket#ExplicitAddressingPacket(int, XBee64BitAddress, XBee16BitAddress, int, int, byte[], byte[], int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Addressing packet but with transmit options 
	 * bigger than 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateExplicitAddressingPackettransmitOptionsBiggerThan255() {
		// Setup the resources for the test.
		int frameID = 0x01;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		byte[] clusterID = new byte[]{0x15, 0x54};
		byte[] profileID =  new byte[]{(byte) 0xC1, 0x05};
		int broadcastRadius = 0;
		int options = 324;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Transmit options must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ExplicitAddressingPacket(frameID, dest64Addr, dest16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, broadcastRadius, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitAddressingPacket#ExplicitAddressingPacket(int, XBee64BitAddress, XBee16BitAddress, int, int, byte[], byte[], int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Addressing packet but with negative 
	 * transmit options. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateExplicitAddressingPacketReceiveOptionsNegative() {
		// Setup the resources for the test.
		int frameID = 0x01;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		byte[] clusterID = new byte[]{0x15, 0x54};
		byte[] profileID =  new byte[]{(byte) 0xC1, 0x05};
		int broadcastRadius = 0;
		int options = -153;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Transmit options must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ExplicitAddressingPacket(frameID, dest64Addr, dest16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, broadcastRadius, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitAddressingPacket#ExplicitAddressingPacket(int, XBee64BitAddress, XBee16BitAddress, int, int, byte[], byte[], int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Addressing packet but with a cluster ID 
	 * with invalid length. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateExplicitAddressingPacketClusterIDInvalidLength() {
		// Setup the resources for the test.
		int frameID = 0x01;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		byte[] clusterID = new byte[]{0x15, 0x54, 0x34};
		byte[] profileID =  new byte[]{(byte) 0xC1, 0x05};
		int broadcastRadius = 0;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Cluster ID length must be 2 bytes.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ExplicitAddressingPacket(frameID, dest64Addr, dest16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, broadcastRadius, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitAddressingPacket#ExplicitAddressingPacket(int, XBee64BitAddress, XBee16BitAddress, int, int, byte[], byte[], int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Addressing packet but with a profile ID 
	 * with invalid length. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateExplicitAddressingPacketProfileIDInvalidLength() {
		// Setup the resources for the test.
		int frameID = 0x01;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		byte[] clusterID = new byte[]{0x15, 0x54};
		byte[] profileID =  new byte[]{(byte) 0xC1};
		int broadcastRadius = 0;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Profile ID length must be 2 bytes.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ExplicitAddressingPacket(frameID, dest64Addr, dest16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, broadcastRadius, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitAddressingPacket#ExplicitAddressingPacket(int, XBee64BitAddress, XBee16BitAddress, int, int, byte[], byte[], int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Addressing packet but with an invalid frame 
	 * ID, bigger than 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateExplicitAddressingPacketInvalidFrameIDBiggerThan255() {
		// Setup the resources for the test.
		int frameID = 2000;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		byte[] clusterID = new byte[]{0x15, 0x54};
		byte[] profileID =  new byte[]{(byte) 0xC1, 0x05};
		int broadcastRadius = 0;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ExplicitAddressingPacket(frameID, dest64Addr, dest16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, broadcastRadius, options, data);
	}
	
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitAddressingPacket#ExplicitAddressingPacket(int, XBee64BitAddress, XBee16BitAddress, int, int, byte[], byte[], int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Addressing packet but with a negative frame 
	 * ID. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateExplicitAddressingPacketInvalidFrameIDNegative() {
		// Setup the resources for the test.
		int frameID = -4;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		byte[] clusterID = new byte[]{0x15, 0x54};
		byte[] profileID =  new byte[]{(byte) 0xC1, 0x05};
		int broadcastRadius = 0;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ExplicitAddressingPacket(frameID, dest64Addr, dest16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, broadcastRadius, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitAddressingPacket#ExplicitAddressingPacket(int, XBee64BitAddress, XBee16BitAddress, int, int, byte[], byte[], int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Addressing packet with an valid parameters 
	 * and RF data.</p>
	 */
	@Test
	public final void testCreateExplicitAddressingPacketValidwithData() {
		// Setup the resources for the test.
		int frameID = 0;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		byte[] clusterID = new byte[]{0x15, 0x54};
		byte[] profileID =  new byte[]{(byte) 0xC1, 0x05};
		int broadcastRadius = 0;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		int expectedLength = 1 /* Frame type */ + 1 /* Frame ID */ + 8 /* 64-bit address */ + 2 /* 16-bit address */ + 1 /* Source endpoint */ + 1 /* Destination endpoint */ + 2 /* Cluster ID */ + 2 /* Profile ID */ + 1 /* Broadcast radius */ + 1 /* Transmit Options */ + data.length /* Data */;
		
		// Call the method under test.
		ExplicitAddressingPacket packet = new ExplicitAddressingPacket(frameID, dest64Addr, dest16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, broadcastRadius, options, data);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination 64-bit address is not the expected one", packet.get64bitDestinationAddress(), is(equalTo(dest64Addr)));
		assertThat("Returned destination 16-bit address is not the expected one", packet.get16bitDestinationAddress(), is(equalTo(dest16Addr)));
		assertThat("Returned source endpoint is not the expected one", packet.getSourceEndpoint(), is(equalTo(sourceEndpoint)));
		assertThat("Returned destination endpoint is not the expected one", packet.getDestinationEndpoint(), is(equalTo(destEndpoint)));
		assertThat("Returned cluster ID is not the expected one", packet.getClusterID(), is(equalTo(clusterID)));
		assertThat("Returned profile ID is not the expected one", packet.getProfileID(), is(equalTo(profileID)));
		assertThat("Returned broadcast radius is not the expected one", packet.getBroadcastRadius(), is(equalTo(broadcastRadius)));
		assertThat("Returned transmit options is not the expected one", packet.getTransmitOptions(), is(equalTo(options)));
		assertThat("Returned RF Data is not the expected one", packet.getRFData(), is(equalTo(data)));
		assertThat("Explicit Addressing needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitAddressingPacket#ExplicitAddressingPacket(int, XBee64BitAddress, XBee16BitAddress, int, int, byte[], byte[], int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Addressing packet with an valid parameters 
	 * without data.</p>
	 */
	@Test
	public final void testCreateExplicitAddressingPacketValidWithoutData() {
		// Setup the resources for the test.
		int frameID = 0;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		byte[] clusterID = new byte[]{0x15, 0x54};
		byte[] profileID =  new byte[]{(byte) 0xC1, 0x05};
		int broadcastRadius = 0;
		int options = 0x06;
		byte[] data = null;
		
		int expectedLength = 1 /* Frame type */ + 1 /* Frame ID */ + 8 /* 64-bit address */ + 2 /* 16-bit address */ + 1 /* Source endpoint */ + 1 /* Destination endpoint */ + 2 /* Cluster ID */ + 2 /* Profile ID */ + 1 /* Broadcast radius */ + 1 /* Transmit Options */;
		
		// Call the method under test.
		ExplicitAddressingPacket packet = new ExplicitAddressingPacket(frameID, dest64Addr, dest16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, broadcastRadius, options, data);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
		assertThat("Returned destination 64-bit address is not the expected one", packet.get64bitDestinationAddress(), is(equalTo(dest64Addr)));
		assertThat("Returned destination 16-bit address is not the expected one", packet.get16bitDestinationAddress(), is(equalTo(dest16Addr)));
		assertThat("Returned source endpoint is not the expected one", packet.getSourceEndpoint(), is(equalTo(sourceEndpoint)));
		assertThat("Returned destination endpoint is not the expected one", packet.getDestinationEndpoint(), is(equalTo(destEndpoint)));
		assertThat("Returned cluster ID is not the expected one", packet.getClusterID(), is(equalTo(clusterID)));
		assertThat("Returned profile ID is not the expected one", packet.getProfileID(), is(equalTo(profileID)));
		assertThat("Returned broadcast radius is not the expected one", packet.getBroadcastRadius(), is(equalTo(broadcastRadius)));
		assertThat("Returned transmit options is not the expected one", packet.getTransmitOptions(), is(equalTo(options)));
		assertThat("Returned RF Data is not the expected one", packet.getRFData(), is(nullValue()));
		assertThat("Explicit Addressing needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitAddressingPacket#getAPIData()}.
	 * 
	 * <p>Test the get API data with valid RF data.</p>
	 */
	@Test
	public final void testGetAPIData() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		byte[] clusterID = new byte[]{0x15, 0x54};
		byte[] profileID =  new byte[]{(byte) 0xC1, 0x05};
		int broadcastRadius = 0;
		int options = 0x06;
		byte[] rfData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		ExplicitAddressingPacket packet = new ExplicitAddressingPacket(frameID, dest64Addr, dest16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, broadcastRadius, options, rfData);
		
		int expectedLength = 1 /* Frame ID */ + 8 /* 64-bit address */ + 2 /* 16-bit address */ + 1 /* Source endpoint */ + 1 /* Destination endpoint */ + 2 /* Cluster ID */ + 2 /* Profile ID */ + 1 /* Broadcast radius */ + 1 /* Transmit Options */ + rfData.length /* RF Data */;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		System.arraycopy(dest64Addr.getValue(), 0, expectedData, 1, dest64Addr.getValue().length);
		System.arraycopy(dest16Addr.getValue(), 0, expectedData, 9, dest16Addr.getValue().length);
		expectedData[11] = (byte)sourceEndpoint;
		expectedData[12] = (byte)destEndpoint;
		System.arraycopy(clusterID, 0, expectedData, 13, clusterID.length);
		System.arraycopy(profileID, 0, expectedData, 15, profileID.length);
		expectedData[17] = (byte)broadcastRadius;
		expectedData[18] = (byte)options;
		System.arraycopy(rfData, 0, expectedData, 19, rfData.length);
		
		// Call the method under test.
		byte[] data = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitAddressingPacket#getAPIData()}.
	 * 
	 * <p>Test the get API data but with {@code null} RF data.</p>
	 */
	@Test
	public final void testGetAPIDataRFDataNull() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		byte[] clusterID = new byte[]{0x15, 0x54};
		byte[] profileID =  new byte[]{(byte) 0xC1, 0x05};
		int broadcastRadius = 0;
		int options = 0x06;
		byte[] rfData = null;
		ExplicitAddressingPacket packet = new ExplicitAddressingPacket(frameID, dest64Addr, dest16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, broadcastRadius, options, rfData);
		
		int expectedLength = 1 /* Frame ID */ + 8 /* 64-bit address */ + 2 /* 16-bit address */ + 1 /* Source endpoint */ + 1 /* Destination endpoint */ + 2 /* Cluster ID */ + 2 /* Profile ID */ + 1 /* Broadcast radius */ + 1 /* Transmit Options */;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)frameID;
		System.arraycopy(dest64Addr.getValue(), 0, expectedData, 1, dest64Addr.getValue().length);
		System.arraycopy(dest16Addr.getValue(), 0, expectedData, 9, dest16Addr.getValue().length);
		expectedData[11] = (byte)sourceEndpoint;
		expectedData[12] = (byte)destEndpoint;
		System.arraycopy(clusterID, 0, expectedData, 13, clusterID.length);
		System.arraycopy(profileID, 0, expectedData, 15, profileID.length);
		expectedData[17] = (byte)broadcastRadius;
		expectedData[18] = (byte)options;
		
		// Call the method under test.
		byte[] data = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitAddressingPacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters.</p>
	 */
	@Test
	public final void testGetAPIPacketParameters() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		byte[] clusterID = new byte[]{0x15, 0x54};
		byte[] profileID =  new byte[]{(byte) 0xC1, 0x05};
		int broadcastRadius = 0;
		int options = 0x06;
		byte[] rfData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		ExplicitAddressingPacket packet = new ExplicitAddressingPacket(frameID, dest64Addr, dest16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, broadcastRadius, options, rfData);
		
		String expectedFrameID = HexUtils.prettyHexString(HexUtils.integerToHexString(frameID, 1)) + " (" + frameID + ")";
		String expectedDest64Addr = HexUtils.prettyHexString(dest64Addr.getValue());
		String expectedDest16Addr = HexUtils.prettyHexString(dest16Addr.getValue());
		String expectedSourceEndpoint = HexUtils.prettyHexString(HexUtils.integerToHexString(sourceEndpoint, 1));
		String expectedDestEndpoint = HexUtils.prettyHexString(HexUtils.integerToHexString(destEndpoint, 1));
		String expectedClusterID = HexUtils.prettyHexString(HexUtils.byteArrayToHexString(clusterID));
		String expectedProfileID = HexUtils.prettyHexString(HexUtils.byteArrayToHexString(profileID));
		String expectedBroadcastRadius = HexUtils.prettyHexString(HexUtils.integerToHexString(broadcastRadius, 1)) + " (" + broadcastRadius + ")";;
		String expectedOptions = HexUtils.prettyHexString(HexUtils.integerToHexString(options, 1));
		String expectedRFData = HexUtils.prettyHexString(HexUtils.byteArrayToHexString(rfData));
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(10)));
		assertThat("Frame ID is not the expected one", packetParams.get("Frame ID"), is(equalTo(expectedFrameID)));
		assertThat("Destination 64-bit Address is not the expected one", packetParams.get("64-bit dest. address"), is(equalTo(expectedDest64Addr)));
		assertThat("Destination 16-bit Address is not the expected one", packetParams.get("16-bit dest. address"), is(equalTo(expectedDest16Addr)));
		assertThat("Source endpoint is not the expected one", packetParams.get("Source endpoint"), is(equalTo(expectedSourceEndpoint)));
		assertThat("Dest. endpoint is not the expected one", packetParams.get("Dest. endpoint"), is(equalTo(expectedDestEndpoint)));
		assertThat("Cluster ID is not the expected one", packetParams.get("Cluster ID"), is(equalTo(expectedClusterID)));
		assertThat("Profile ID is not the expected one", packetParams.get("Profile ID"), is(equalTo(expectedProfileID)));
		assertThat("Broadcast radius is not the expected one", packetParams.get("Broadcast radius"), is(equalTo(expectedBroadcastRadius)));
		assertThat("Transmit options are not the expected one", packetParams.get("Transmit options"), is(equalTo(expectedOptions)));
		assertThat("RF Data is not the expected one", packetParams.get("RF data"), is(equalTo(expectedRFData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitAddressingPacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters with {@code null} RF data.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersRFDataNull() {
		// Setup the resources for the test.
		int frameID = 0x10;
		XBee64BitAddress dest64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress dest16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		byte[] clusterID = new byte[]{0x15, 0x54};
		byte[] profileID =  new byte[]{(byte) 0xC1, 0x05};
		int broadcastRadius = 0;
		int options = 0x06;
		byte[] rfData = null;
		ExplicitAddressingPacket packet = new ExplicitAddressingPacket(frameID, dest64Addr, dest16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, broadcastRadius, options, rfData);
		
		String expectedFrameID = HexUtils.prettyHexString(HexUtils.integerToHexString(frameID, 1)) + " (" + frameID + ")";
		String expectedDest64Addr = HexUtils.prettyHexString(dest64Addr.getValue());
		String expectedDest16Addr = HexUtils.prettyHexString(dest16Addr.getValue());
		String expectedSourceEndpoint = HexUtils.prettyHexString(HexUtils.integerToHexString(sourceEndpoint, 1));
		String expectedDestEndpoint = HexUtils.prettyHexString(HexUtils.integerToHexString(destEndpoint, 1));
		String expectedClusterID = HexUtils.prettyHexString(HexUtils.byteArrayToHexString(clusterID));
		String expectedProfileID = HexUtils.prettyHexString(HexUtils.byteArrayToHexString(profileID));
		String expectedBroadcastRadius = HexUtils.prettyHexString(HexUtils.integerToHexString(broadcastRadius, 1)) + " (" + broadcastRadius + ")";;
		String expectedOptions = HexUtils.prettyHexString(HexUtils.integerToHexString(options, 1));
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(9)));
		assertThat("Frame ID is not the expected one", packetParams.get("Frame ID"), is(equalTo(expectedFrameID)));
		assertThat("Destination 64-bit Address is not the expected one", packetParams.get("64-bit dest. address"), is(equalTo(expectedDest64Addr)));
		assertThat("Destination 16-bit Address is not the expected one", packetParams.get("16-bit dest. address"), is(equalTo(expectedDest16Addr)));
		assertThat("Source endpoint is not the expected one", packetParams.get("Source endpoint"), is(equalTo(expectedSourceEndpoint)));
		assertThat("Dest. endpoint is not the expected one", packetParams.get("Dest. endpoint"), is(equalTo(expectedDestEndpoint)));
		assertThat("Cluster ID is not the expected one", packetParams.get("Cluster ID"), is(equalTo(expectedClusterID)));
		assertThat("Profile ID is not the expected one", packetParams.get("Profile ID"), is(equalTo(expectedProfileID)));
		assertThat("Broadcast radius is not the expected one", packetParams.get("Broadcast radius"), is(equalTo(expectedBroadcastRadius)));
		assertThat("Transmit options are not the expected one", packetParams.get("Transmit options"), is(equalTo(expectedOptions)));
		assertThat("RF Data is not the expected one", packetParams.get("RF data"), is(nullValue(String.class)));
	}
}
