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
package com.digi.xbee.api.packet.common;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
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

import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

public class ExplicitRxIndicatorPacketTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	public ExplicitRxIndicatorPacketTest() {
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
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#createPacket(byte[])}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a 
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Setup the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Explicit Rx Indicator packet payload cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		ExplicitRxIndicatorPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Setup the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Explicit Rx Indicator packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		ExplicitRxIndicatorPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array shorter than the needed one is provided.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Setup the resources for the test.
		int frameType = APIFrameType.EXPLICIT_RX_INDICATOR.getValue();
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		int clusterID = 0x15; // Invalid Cluster ID.
		int profileID = 0xC105;
		int options = 0x06;
		
		byte[] payload = new byte[17];
		payload[0] = (byte)frameType;
		System.arraycopy(source64Addr.getValue(), 0, payload, 1, source64Addr.getValue().length);
		System.arraycopy(source16Addr.getValue(), 0, payload, 9, source16Addr.getValue().length);
		payload[11] = (byte)sourceEndpoint;
		payload[12] = (byte)destEndpoint;
		payload[13] = (byte)clusterID;
		payload[14] = (byte)(profileID >> 8);
		payload[15] = (byte)profileID;
		payload[16] = (byte)options;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Explicit Rx Indicator packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		ExplicitRxIndicatorPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array not including the Frame type.</p>
	 */
	@Test
	public final void testCreatePacketPayloadNotIncludingFrameType() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		int clusterID = 0x1554;
		int profileID = 0xC105;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		byte[] payload = new byte[17 + data.length];
		System.arraycopy(source64Addr.getValue(), 0, payload, 0, source64Addr.getValue().length);
		System.arraycopy(source16Addr.getValue(), 0, payload, 8, source16Addr.getValue().length);
		payload[10] = (byte)sourceEndpoint;
		payload[11] = (byte)destEndpoint;
		payload[12] = (byte)(clusterID >> 8);
		payload[13] = (byte)clusterID;
		payload[14] = (byte)(profileID >> 8);
		payload[15] = (byte)profileID;
		payload[16] = (byte)options;
		System.arraycopy(data, 0, payload, 17, data.length);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not an Explicit Rx Indicator packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		ExplicitRxIndicatorPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#createPacket(byte[])}.
	 * 
	 * <p>A valid Explicit Rx Indicator packet with the provided options
	 *  without data is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithoutData() {
		// Setup the resources for the test.
		int frameType = APIFrameType.EXPLICIT_RX_INDICATOR.getValue();
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		int clusterID = 0x1554;
		int profileID = 0xC105;
		int options = 0x06;
		
		byte[] payload = new byte[18];
		payload[0] = (byte)frameType;
		System.arraycopy(source64Addr.getValue(), 0, payload, 1, source64Addr.getValue().length);
		System.arraycopy(source16Addr.getValue(), 0, payload, 9, source16Addr.getValue().length);
		payload[11] = (byte)sourceEndpoint;
		payload[12] = (byte)destEndpoint;
		payload[13] = (byte)(clusterID >> 8);
		payload[14] = (byte)clusterID;
		payload[15] = (byte)(profileID >> 8);
		payload[16] = (byte)profileID;
		payload[17] = (byte)options;
		
		// Call the method under test.
		ExplicitRxIndicatorPacket packet = ExplicitRxIndicatorPacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned source 64-bit address is not the expected one", packet.get64BitSourceAddress(), is(equalTo(source64Addr)));
		assertThat("Returned source 16-bit address is not the expected one", packet.get16BitSourceAddress(), is(equalTo(source16Addr)));
		assertThat("Returned source endpoint is not the expected one", packet.getSourceEndpoint(), is(equalTo(sourceEndpoint)));
		assertThat("Returned destination endpoint is not the expected one", packet.getDestinationEndpoint(), is(equalTo(destEndpoint)));
		assertThat("Returned cluster ID is not the expected one", packet.getClusterID(), is(equalTo(clusterID)));
		assertThat("Returned profile ID is not the expected one", packet.getProfileID(), is(equalTo(profileID)));
		assertThat("Returned receive options is not the expected one", packet.getReceiveOptions(), is(equalTo(options)));
		assertThat("Returned RF Data is not the expected one", packet.getRFData(), is(nullValue()));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#createPacket(byte[])}.
	 * 
	 * <p>A valid Explicit Rx Indicator packet with the provided options and data is 
	 * created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayloadWithData() {
		// Setup the resources for the test.
		int frameType = APIFrameType.EXPLICIT_RX_INDICATOR.getValue();
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		int clusterID = 0x1554;
		int profileID = 0xC105;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		byte[] payload = new byte[18 + data.length];
		payload[0] = (byte)frameType;
		System.arraycopy(source64Addr.getValue(), 0, payload, 1, source64Addr.getValue().length);
		System.arraycopy(source16Addr.getValue(), 0, payload, 9, source16Addr.getValue().length);
		payload[11] = (byte)sourceEndpoint;
		payload[12] = (byte)destEndpoint;
		payload[13] = (byte)(clusterID >> 8);
		payload[14] = (byte)clusterID;
		payload[15] = (byte)(profileID >> 8);
		payload[16] = (byte)profileID;
		payload[17] = (byte)options;
		System.arraycopy(data, 0, payload, 18, data.length);
		
		// Call the method under test.
		ExplicitRxIndicatorPacket packet = ExplicitRxIndicatorPacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned source 64-bit address is not the expected one", packet.get64BitSourceAddress(), is(equalTo(source64Addr)));
		assertThat("Returned source 16-bit address is not the expected one", packet.get16BitSourceAddress(), is(equalTo(source16Addr)));
		assertThat("Returned source endpoint is not the expected one", packet.getSourceEndpoint(), is(equalTo(sourceEndpoint)));
		assertThat("Returned destination endpoint is not the expected one", packet.getDestinationEndpoint(), is(equalTo(destEndpoint)));
		assertThat("Returned cluster ID is not the expected one", packet.getClusterID(), is(equalTo(clusterID)));
		assertThat("Returned profile ID is not the expected one", packet.getProfileID(), is(equalTo(profileID)));
		assertThat("Returned receive options is not the expected one", packet.getReceiveOptions(), is(equalTo(options)));
		assertThat("Returned RF Data is not the expected one", packet.getRFData(), is(equalTo(data)));
		
		assertThat("Returned payload array is not the expected one", packet.getPacketData(), is(equalTo(payload)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#ExplicitRxIndicatorPacket(XBee64BitAddress, XBee16BitAddress, int, int, int, int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Rx Indicator packet but with a {@code null} 
	 * 64-bit source address. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateExplicitRxIndicatorPacket64BitAddressNull() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = null;
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		int clusterID = 0x1554;
		int profileID = 0xC105;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("64-bit source address cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new ExplicitRxIndicatorPacket(source64Addr, source16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#ExplicitRxIndicatorPacket(XBee64BitAddress, XBee16BitAddress, int, int, int, int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Rx Indicator packet but with a {@code null} 
	 * 16-bit source address. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateExplicitRxIndicatorPacket16BitAddressNull() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = null;
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		int clusterID = 0x1554;
		int profileID = 0xC105;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("16-bit source address cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new ExplicitRxIndicatorPacket(source64Addr, source16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#ExplicitRxIndicatorPacket(XBee64BitAddress, XBee16BitAddress, int, int, int, int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Rx Indicator packet but with a source endpoint 
	 * bigger than 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateExplicitRxIndicatorPacketSourceEndpointBiggerThan255() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 748;
		int destEndpoint = 0xA1;
		int clusterID = 0x1554;
		int profileID = 0xC105;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Source endpoint must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ExplicitRxIndicatorPacket(source64Addr, source16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#ExplicitRxIndicatorPacket(XBee64BitAddress, XBee16BitAddress, int, int, int, int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Rx Indicator packet but with a negative source 
	 * endpoint bigger than 255. This must throw an 
	 * {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateExplicitRxIndicatorPacketSourceEndpointNegative() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = -149;
		int destEndpoint = 0xA1;
		int clusterID = 0x1554;
		int profileID = 0xC105;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Source endpoint must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ExplicitRxIndicatorPacket(source64Addr, source16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#ExplicitRxIndicatorPacket(XBee64BitAddress, XBee16BitAddress, int, int, int, int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Rx Indicator packet but with a dest. endpoint 
	 * bigger than 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateExplicitRxIndicatorPacketDestEndpointBiggerThan255() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 748;
		int clusterID = 0x1554;
		int profileID = 0xC105;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Destination endpoint must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ExplicitRxIndicatorPacket(source64Addr, source16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#ExplicitRxIndicatorPacket(XBee64BitAddress, XBee16BitAddress, int, int, int, int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Rx Indicator packet but with a negative dest. 
	 * endpoint. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateExplicitRxIndicatorPacketDestEndpointNegative() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = -149;
		int clusterID = 0x1554;
		int profileID = 0xC105;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Destination endpoint must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ExplicitRxIndicatorPacket(source64Addr, source16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#ExplicitRxIndicatorPacket(XBee64BitAddress, XBee16BitAddress, int, int, int, int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Rx Indicator packet but with a cluster ID 
	 * bigger than 65535. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateExplicitRxIndicatorPacketClusterIDBiggerThan65535() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA0;
		int clusterID = 65536;
		int profileID = 0xC105;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Cluster ID must be between 0 and 65535.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ExplicitRxIndicatorPacket(source64Addr, source16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#ExplicitRxIndicatorPacket(XBee64BitAddress, XBee16BitAddress, int, int, int, int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Rx Indicator packet but with a negative 
	 * cluster ID. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateExplicitRxIndicatorPacketClusterIDNegative() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA0;
		int clusterID = -2;
		int profileID = 0xC105;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Cluster ID must be between 0 and 65535.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ExplicitRxIndicatorPacket(source64Addr, source16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#ExplicitRxIndicatorPacket(XBee64BitAddress, XBee16BitAddress, int, int, int, int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Rx Indicator packet but with a profile ID 
	 * bigger than 65535. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateExplicitRxIndicatorPacketProfileIDBiggerThan65535() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA0;
		int clusterID = 0x1554;
		int profileID = 65536;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Profile ID must be between 0 and 65535.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ExplicitRxIndicatorPacket(source64Addr, source16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#ExplicitRxIndicatorPacket(XBee64BitAddress, XBee16BitAddress, int, int, int, int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Rx Indicator packet but with a negative 
	 * profile ID. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateExplicitRxIndicatorPacketProfileIDNegative() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA0;
		int clusterID = 0x1554;
		int profileID = -1;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Profile ID must be between 0 and 65535.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ExplicitRxIndicatorPacket(source64Addr, source16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#ExplicitRxIndicatorPacket(XBee64BitAddress, XBee16BitAddress, int, int, int, int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Rx Indicator packet but with receive options 
	 * bigger than 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateExplicitRxIndicatorPacketReceiveOptionsBiggerThan255() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		int clusterID = 0x1554;
		int profileID = 0xC105;
		int options = 324;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Receive options must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ExplicitRxIndicatorPacket(source64Addr, source16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#ExplicitRxIndicatorPacket(XBee64BitAddress, XBee16BitAddress, int, int, int, int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Rx Indicator packet but with negative 
	 * receive options. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateExplicitRxIndicatorPacketReceiveOptionsNegative() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		int clusterID = 0x1554;
		int profileID = 0xC105;
		int options = -153;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Receive options must be between 0 and 255.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		new ExplicitRxIndicatorPacket(source64Addr, source16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, options, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#ExplicitRxIndicatorPacket(XBee64BitAddress, XBee16BitAddress, int, int, int, int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Rx Indicator packet with valid parameters 
	 * and RF data.</p>
	 */
	@Test
	public final void testCreateExplicitRxIndicatorPacketValidWithData() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		int clusterID = 0x1554;
		int profileID = 0xC105;
		int options = 0x06;
		byte[] data = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		
		int expectedLength = 1 /* Frame type */ + 8 /* 64-bit address */ + 2 /* 16-bit address */ + 1 /* Source endpoint */ + 1 /* Destination endpoint */ + 2 /* Cluster ID */ + 2 /* Profile ID */ + 1 /* Receive Options */ + data.length /* Data */;
		
		// Call the method under test.
		ExplicitRxIndicatorPacket packet = new ExplicitRxIndicatorPacket(source64Addr, source16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, options, data);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned source 64-bit address is not the expected one", packet.get64BitSourceAddress(), is(equalTo(source64Addr)));
		assertThat("Returned source 16-bit address is not the expected one", packet.get16BitSourceAddress(), is(equalTo(source16Addr)));
		assertThat("Returned source endpoint is not the expected one", packet.getSourceEndpoint(), is(equalTo(sourceEndpoint)));
		assertThat("Returned destination endpoint is not the expected one", packet.getDestinationEndpoint(), is(equalTo(destEndpoint)));
		assertThat("Returned cluster ID is not the expected one", packet.getClusterID(), is(equalTo(clusterID)));
		assertThat("Returned profile ID is not the expected one", packet.getProfileID(), is(equalTo(profileID)));
		assertThat("Returned receive options is not the expected one", packet.getReceiveOptions(), is(equalTo(options)));
		assertThat("Returned RF Data is not the expected one", packet.getRFData(), is(equalTo(data)));
		assertThat("Explicit Rx Indicator needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#ExplicitRxIndicatorPacket(XBee64BitAddress, XBee16BitAddress, int, int, int, int, int, byte[])}.
	 * 
	 * <p>Construct a new Explicit Rx Indicator packet with valid parameters 
	 * without data.</p>
	 */
	@Test
	public final void testCreateExplicitRxIndicatorPacketValidWithoutData() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		int clusterID = 0x1554;
		int profileID = 0xC105;
		int options = 0x06;
		byte[] data = null;
		
		int expectedLength = 1 /* Frame type */ + 8 /* 64-bit address */ + 2 /* 16-bit address */ + 1 /* Source endpoint */ + 1 /* Destination endpoint */ + 2 /* Cluster ID */ + 2 /* Profile ID */ + 1 /* Receive Options */;
		
		// Call the method under test.
		ExplicitRxIndicatorPacket packet = new ExplicitRxIndicatorPacket(source64Addr, source16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, options, data);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned source 64-bit address is not the expected one", packet.get64BitSourceAddress(), is(equalTo(source64Addr)));
		assertThat("Returned source 16-bit address is not the expected one", packet.get16BitSourceAddress(), is(equalTo(source16Addr)));
		assertThat("Returned source endpoint is not the expected one", packet.getSourceEndpoint(), is(equalTo(sourceEndpoint)));
		assertThat("Returned destination endpoint is not the expected one", packet.getDestinationEndpoint(), is(equalTo(destEndpoint)));
		assertThat("Returned cluster ID is not the expected one", packet.getClusterID(), is(equalTo(clusterID)));
		assertThat("Returned profile ID is not the expected one", packet.getProfileID(), is(equalTo(profileID)));
		assertThat("Returned receive options is not the expected one", packet.getReceiveOptions(), is(equalTo(options)));
		assertThat("Returned RF Data is not the expected one", packet.getRFData(), is(nullValue()));
		assertThat("Explicit Rx Indicator needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#getAPIData()}.
	 * 
	 * <p>Test the get API data with valid RF data.</p>
	 */
	@Test
	public final void testGetAPIData() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		int clusterID = 0x1554;
		int profileID = 0xC105;
		int options = 0x06;
		byte[] rfData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		ExplicitRxIndicatorPacket packet = new ExplicitRxIndicatorPacket(source64Addr, source16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, options, rfData);
		
		int expectedLength = 8 /* 64-bit address */ + 2 /* 16-bit address */ + 1 /* Source endpoint */ + 1 /* Destination endpoint */ + 2 /* Cluster ID */ + 2 /* Profile ID */ + 1 /* Receive Options */ + rfData.length /* RF Data */;
		byte[] expectedData = new byte[expectedLength];
		System.arraycopy(source64Addr.getValue(), 0, expectedData, 0, source64Addr.getValue().length);
		System.arraycopy(source16Addr.getValue(), 0, expectedData, 8, source16Addr.getValue().length);
		expectedData[10] = (byte)sourceEndpoint;
		expectedData[11] = (byte)destEndpoint;
		expectedData[12] = (byte)(clusterID >> 8);
		expectedData[13] = (byte)clusterID;
		expectedData[14] = (byte)(profileID >> 8);
		expectedData[15] = (byte)profileID;
		expectedData[16] = (byte)options;
		System.arraycopy(rfData, 0, expectedData, 17, rfData.length);
		
		// Call the method under test.
		byte[] data = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#getAPIData()}.
	 * 
	 * <p>Test the get API data but with {@code null} RF data.</p>
	 */
	@Test
	public final void testGetAPIDataRFDataNull() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		int clusterID = 0x1554;
		int profileID = 0xC105;
		int options = 0x06;
		byte[] rfData = null;
		ExplicitRxIndicatorPacket packet = new ExplicitRxIndicatorPacket(source64Addr, source16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, options, rfData);
		
		int expectedLength = 8 /* 64-bit address */ + 2 /* 16-bit address */ + 1 /* Source endpoint */ + 1 /* Destination endpoint */ + 2 /* Cluster ID */ + 2 /* Profile ID */ + 1 /* Receive Options */;
		byte[] expectedData = new byte[expectedLength];
		System.arraycopy(source64Addr.getValue(), 0, expectedData, 0, source64Addr.getValue().length);
		System.arraycopy(source16Addr.getValue(), 0, expectedData, 8, source16Addr.getValue().length);
		expectedData[10] = (byte)sourceEndpoint;
		expectedData[11] = (byte)destEndpoint;
		expectedData[12] = (byte)(clusterID >> 8);
		expectedData[13] = (byte)clusterID;
		expectedData[14] = (byte)(profileID >> 8);
		expectedData[15] = (byte)profileID;
		expectedData[16] = (byte)options;
		
		// Call the method under test.
		byte[] data = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters.</p>
	 */
	@Test
	public final void testGetAPIPacketParameters() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		int clusterID = 0x1554;
		int profileID = 0xC105;
		int options = 0x06;
		byte[] rfData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		ExplicitRxIndicatorPacket packet = new ExplicitRxIndicatorPacket(source64Addr, source16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, options, rfData);
		
		String expectedsource64Addr = HexUtils.prettyHexString(source64Addr.getValue());
		String expectedsource16Addr = HexUtils.prettyHexString(source16Addr.getValue());
		String expectedSourceEndpoint = HexUtils.prettyHexString(HexUtils.integerToHexString(sourceEndpoint, 1));
		String expectedDestEndpoint = HexUtils.prettyHexString(HexUtils.integerToHexString(destEndpoint, 1));
		String expectedClusterID = HexUtils.prettyHexString(HexUtils.integerToHexString(clusterID, 2));
		String expectedProfileID = HexUtils.prettyHexString(HexUtils.integerToHexString(profileID, 2));
		String expectedOptions = HexUtils.prettyHexString(HexUtils.integerToHexString(options, 1));
		String expectedRFData = HexUtils.prettyHexString(HexUtils.byteArrayToHexString(rfData));
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(8)));
		assertThat("source 64-bit Address is not the expected one", packetParams.get("64-bit source address"), is(equalTo(expectedsource64Addr)));
		assertThat("source 16-bit Address is not the expected one", packetParams.get("16-bit source address"), is(equalTo(expectedsource16Addr)));
		assertThat("Source endpoint is not the expected one", packetParams.get("Source endpoint"), is(equalTo(expectedSourceEndpoint)));
		assertThat("Dest. endpoint is not the expected one", packetParams.get("Dest. endpoint"), is(equalTo(expectedDestEndpoint)));
		assertThat("Cluster ID is not the expected one", packetParams.get("Cluster ID"), is(equalTo(expectedClusterID)));
		assertThat("Profile ID is not the expected one", packetParams.get("Profile ID"), is(equalTo(expectedProfileID)));
		assertThat("Receive options are not the expected one", packetParams.get("Receive options"), is(equalTo(expectedOptions)));
		assertThat("RF Data is not the expected one", packetParams.get("RF data"), is(equalTo(expectedRFData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters with {@code null} RF data.</p>
	 */
	@Test
	public final void testGetAPIPacketParametersRFDataNull() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		int clusterID = 0x1554;
		int profileID = 0xC105;
		int options = 0x06;
		byte[] rfData = null;
		ExplicitRxIndicatorPacket packet = new ExplicitRxIndicatorPacket(source64Addr, source16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, options, rfData);
		
		String expectedsource64Addr = HexUtils.prettyHexString(source64Addr.getValue());
		String expectedsource16Addr = HexUtils.prettyHexString(source16Addr.getValue());
		String expectedSourceEndpoint = HexUtils.prettyHexString(HexUtils.integerToHexString(sourceEndpoint, 1));
		String expectedDestEndpoint = HexUtils.prettyHexString(HexUtils.integerToHexString(destEndpoint, 1));
		String expectedClusterID = HexUtils.prettyHexString(HexUtils.integerToHexString(clusterID, 2));
		String expectedProfileID = HexUtils.prettyHexString(HexUtils.integerToHexString(profileID, 2));
		String expectedOptions = HexUtils.prettyHexString(HexUtils.integerToHexString(options, 1));
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(7)));
		assertThat("source 64-bit Address is not the expected one", packetParams.get("64-bit source address"), is(equalTo(expectedsource64Addr)));
		assertThat("source 16-bit Address is not the expected one", packetParams.get("16-bit source address"), is(equalTo(expectedsource16Addr)));
		assertThat("Source endpoint is not the expected one", packetParams.get("Source endpoint"), is(equalTo(expectedSourceEndpoint)));
		assertThat("Dest. endpoint is not the expected one", packetParams.get("Dest. endpoint"), is(equalTo(expectedDestEndpoint)));
		assertThat("Cluster ID is not the expected one", packetParams.get("Cluster ID"), is(equalTo(expectedClusterID)));
		assertThat("Profile ID is not the expected one", packetParams.get("Profile ID"), is(equalTo(expectedProfileID)));
		assertThat("Receive options are not the expected one", packetParams.get("Receive options"), is(equalTo(expectedOptions)));
		assertThat("RF Data is not the expected one", packetParams.get("RF data"), is(nullValue(String.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#isBroadcast()}.
	 * 
	 * <p>Test if an Explicit Rx Indicator packet is a broadcast packet when 
	 * broadcast options is not enabled.</p>
	 */
	@Test
	public final void testIsBroadcastWithNonBroadcastOption() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		int clusterID = 0x1554;
		int profileID =  0xC105;
		int options = 0x19;
		byte[] receivedData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		ExplicitRxIndicatorPacket packet = new ExplicitRxIndicatorPacket(source64Addr, source16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, options, receivedData);
		
		// Call the method under test and verify the result.
		assertThat("Packet should not be broadcast", packet.isBroadcast(), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#isBroadcast()}.
	 * 
	 * <p>Test if an Explicit Rx Indicator packet is a broadcast packet when 
	 * broadcast is enabled in the options.</p>
	 */
	@Test
	public final void testIsBroadcastWithBroadcastOptionEnabled() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("D817");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		int clusterID = 0x1554;
		int profileID =  0xC105;
		int options = 0x8A; /* bit 1 */
		byte[] receivedData = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		ExplicitRxIndicatorPacket packet = new ExplicitRxIndicatorPacket(source64Addr, source16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, options, receivedData);
		
		// Call the method under test and verify the result.
		assertThat("Packet should be broadcast", packet.isBroadcast(), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#getRFData())}.
	 */
	@Test
	public final void testGetRFDataNullData() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		int clusterID = 0x1554;
		int profileID =  0xC105;
		int options = 0x06;
		byte[] rfData = null;
		ExplicitRxIndicatorPacket packet = new ExplicitRxIndicatorPacket(source64Addr, source16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, options, rfData);
		
		// Call the method under test.
		byte[] result = packet.getRFData();
		
		// Verify the result.
		assertThat("RF Data must be the same", result, is(equalTo(rfData)));
		assertThat("RF Data must be null", result, is(nullValue(byte[].class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#getRFData())}.
	 */
	@Test
	public final void testGetRFDataInvalidData() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		int clusterID = 0x1554;
		int profileID =  0xC105;
		int options = 0x06;
		byte[] rfData = new byte[]{0x68, 0x6F, 0x6C, 0x61};
		ExplicitRxIndicatorPacket packet = new ExplicitRxIndicatorPacket(source64Addr, source16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, options, rfData);
		
		// Call the method under test.
		byte[] result = packet.getRFData();
		
		// Verify the result.
		assertThat("RF Data must be the same", result, is(equalTo(rfData)));
		assertThat("RF Data must not be the same object", result.hashCode(), is(not(equalTo(rfData.hashCode()))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#getRFData())}.
	 */
	@Test
	public final void testGetRFDataValidData() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		int clusterID = 0x1554;
		int profileID =  0xC105;
		int options = 0x06;
		byte[] rfData = new byte[]{0x68, 0x6F, 0x6C, 0x61, (byte)0x98, 0x11, 0x32};
		ExplicitRxIndicatorPacket packet = new ExplicitRxIndicatorPacket(source64Addr, source16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, options, rfData);
		
		// Call the method under test.
		byte[] result = packet.getRFData();
		
		// Verify the result.
		assertThat("RF Data must be the same", result, is(equalTo(rfData)));
		assertThat("RF Data must not be the same object", result.hashCode(), is(not(equalTo(rfData.hashCode()))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#setRFData(byte[])}.
	 */
	@Test
	public final void testSetRFDataNullData() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		int clusterID = 0x1554;
		int profileID =  0xC105;
		int options = 0x06;
		byte[] origData = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		byte[] rfData = null;
		ExplicitRxIndicatorPacket packet = new ExplicitRxIndicatorPacket(source64Addr, source16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, options, origData);
		
		// Call the method under test.
		packet.setRFData(rfData);
		
		byte[] result = packet.getRFData();
		
		// Verify the result.
		assertThat("RF Data must be the same", result, is(equalTo(rfData)));
		assertThat("RF Data must be null", result, is(nullValue(byte[].class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#setRFData(byte[])}.
	 */
	@Test
	public final void testSetRFDataValidData() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		int clusterID = 0x1554;
		int profileID =  0xC105;
		int options = 0x06;
		byte[] origData = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		byte[] rfData = new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
		ExplicitRxIndicatorPacket packet = new ExplicitRxIndicatorPacket(source64Addr, source16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, options, origData);
		
		// Call the method under test.
		packet.setRFData(rfData);
		
		byte[] result = packet.getRFData();
		
		// Verify the result.
		assertThat("RF Data must be the same", result, is(equalTo(rfData)));
		assertThat("RF Data must not be the same object", result.hashCode(), is(not(equalTo(rfData.hashCode()))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket#setRFData(byte[])}.
	 */
	@Test
	public final void testSetRFDataAndModifyOriginal() {
		// Setup the resources for the test.
		XBee64BitAddress source64Addr = new XBee64BitAddress("0013A2004032D9AB");
		XBee16BitAddress source16Addr = new XBee16BitAddress("B45C");
		int sourceEndpoint = 0xA0;
		int destEndpoint = 0xA1;
		int clusterID = 0x1554;
		int profileID =  0xC105;
		int options = 0x06;
		byte[] origData = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		byte[] rfData = new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
		ExplicitRxIndicatorPacket packet = new ExplicitRxIndicatorPacket(source64Addr, source16Addr, sourceEndpoint, destEndpoint, clusterID, profileID, options, origData);
		
		// Call the method under test.
		packet.setRFData(rfData);
		byte[] backup = Arrays.copyOf(rfData, rfData.length);
		rfData[0] = 0x11;
		rfData[1] = 0x12;
		
		byte[] result = packet.getRFData();
		
		// Verify the result.
		assertThat("RF Data must be the same as the setted data", result, is(equalTo(backup)));
		assertThat("RF Data must not be the current value of received data", result, is(not(equalTo(rfData))));
		assertThat("RF Data must not be the same object", result.hashCode(), is(not(equalTo(backup.hashCode()))));
		assertThat("RF Data must not be the same object", result.hashCode(), is(not(equalTo(rfData.hashCode()))));
	}
}
