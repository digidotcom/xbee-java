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

import static org.junit.Assert.assertThat;

import java.util.LinkedHashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.digi.xbee.api.models.ModemStatusEvent;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

public class ModemStatusPacketTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	public ModemStatusPacketTest() {
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
	 * Test method for {@link com.digi.xbee.api.packet.common.ModemStatusPacket#createPacket(byte[])}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a 
	 * {@code null} byte array.</p>
	 */
	@Test
	public final void testCreatePacketNullPayload() {
		// Setup the resources for the test.
		byte[] payload = null;
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Modem Status packet payload cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		ModemStatusPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ModemStatusPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing an empty byte array.</p>
	 */
	@Test
	public final void testCreatePacketEmptyPayload() {
		// Setup the resources for the test.
		byte[] payload = new byte[0];
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Modem Status packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		ModemStatusPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ModemStatusPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * parsing a byte array shorter than the needed one.</p>
	 */
	@Test
	public final void testCreatePacketPayloadShorterThanNeeded() {
		// Setup the resources for the test.
		int frameType = APIFrameType.MODEM_STATUS.getValue();
		
		// Create a payload without modem status.
		byte[] payload = new byte[1];
		payload[0] = (byte)frameType;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Incomplete Modem Status packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		ModemStatusPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ModemStatusPacket#createPacket(byte[])}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when parsing a byte 
	 * array with an invalid Frame type.</p>
	 */
	@Test
	public final void testCreatePacketPayloadInvalidFrameType() {
		// Setup the resources for the test.
		int frameType = APIFrameType.TRANSMIT_REQUEST.getValue();
		ModemStatusEvent modemStatusEvent = ModemStatusEvent.STATUS_JOINED_NETWORK;
		
		byte[] payload = new byte[2];
		payload[0] = (byte)frameType;
		payload[1] = (byte)modemStatusEvent.getId();
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Payload is not a Modem Status packet.")));
		
		// Call the method under test that should throw an IllegalArgumentException.
		ModemStatusPacket.createPacket(payload);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ModemStatusPacket#createPacket(byte[])}.
	 * 
	 * <p>A valid Modem Status packet is created.</p>
	 */
	@Test
	public final void testCreatePacketValidPayload() {
		// Setup the resources for the test.
		int frameType = APIFrameType.MODEM_STATUS.getValue();
		ModemStatusEvent modemStatusEvent = ModemStatusEvent.STATUS_JOINED_NETWORK;
		
		byte[] payload = new byte[2];
		payload[0] = (byte)frameType;
		payload[1] = (byte)modemStatusEvent.getId();
		
		// Call the method under test.
		ModemStatusPacket packet = ModemStatusPacket.createPacket(payload);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(payload.length)));
		assertThat("Returned Modem Status is not the expected one", packet.getStatus(), is(equalTo(modemStatusEvent)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ModemStatusPacket#ModemStatusPacket(ModemStatusEvent)}.
	 * 
	 * <p>Construct a new Modem Status packet but with a {@code null} modem status. 
	 * This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateModemStatusPacketStatusNull() {
		// Setup the resources for the test.
		ModemStatusEvent modemStatusEvent = null;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Modem Status event cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new ModemStatusPacket(modemStatusEvent);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ModemStatusPacket#ModemStatusPacket(ModemStatusEvent)}.
	 * 
	 * <p>Construct a new Modem Status packet but with valid parameters.</p>
	 */
	@Test
	public final void testCreateModemStatusPacketValid() {
		// Setup the resources for the test.
		ModemStatusEvent modemStatusEvent = ModemStatusEvent.STATUS_JOINED_NETWORK;
		
		int expectedLength = 1 /* Frame type */ + 1 /* Modem status */;
		
		// Call the method under test.
		ModemStatusPacket packet = new ModemStatusPacket(modemStatusEvent);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned Modem Status is not the expected one", packet.getStatus(), is(equalTo(modemStatusEvent)));
		assertThat("Modem Status needs API Frame ID", packet.needsAPIFrameID(), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ModemStatusPacket#getAPIData()}.
	 * 
	 * <p>Test the get API data.</p>
	 */
	@Test
	public final void testGetAPIData() {
		// Setup the resources for the test.
		ModemStatusEvent modemStatusEvent = ModemStatusEvent.STATUS_JOINED_NETWORK;
		ModemStatusPacket packet = new ModemStatusPacket(modemStatusEvent);
		
		int expectedLength = 1 /* Modem status */;
		byte[] expectedData = new byte[expectedLength];
		expectedData[0] = (byte)modemStatusEvent.getId();
		
		// Call the method under test.
		byte[] data = packet.getAPIData();
		
		// Verify the result.
		assertThat("API data is not the expected", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ModemStatusPacket#getAPIPacketParameters()}.
	 * 
	 * <p>Test the get API parameters.</p>
	 */
	@Test
	public final void testGetAPIPacketParameters() {
		// Setup the resources for the test.
		ModemStatusEvent modemStatusEvent = ModemStatusEvent.STATUS_JOINED_NETWORK;
		ModemStatusPacket packet = new ModemStatusPacket(modemStatusEvent);
		
		String expectedModemStatus = HexUtils.integerToHexString(modemStatusEvent.getId(), 1) + " (" + modemStatusEvent.getDescription() + ")";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getAPIPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(1)));
		assertThat("Modem Status is not the expected one", packetParams.get("Status"), is(equalTo(expectedModemStatus)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.common.ModemStatusPacket#isBroadcast()}.
	 * 
	 * <p>Test if a Modem status packet is a broadcast packet, never should be.</p>
	 */
	@Test
	public final void testIsBroadcast() {
		// Setup the resources for the test.
		ModemStatusEvent modemStatusEvent = ModemStatusEvent.STATUS_JOINED_NETWORK;
		ModemStatusPacket packet = new ModemStatusPacket(modemStatusEvent);
		
		// Call the method under test and verify the result.
		assertThat("Packet should not be broadcast", packet.isBroadcast(), is(equalTo(false)));
	}
}
