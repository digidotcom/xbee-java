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
package com.digi.xbee.api.models;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.digi.xbee.api.RemoteXBeeDevice;

public class ExplicitXBeeMessageTest {

	// Constants.
	private final static String DATA = "Data";
	
	private static final int SOURCE_ENDPOINT = 0xA0;
	private static final int DESTINATION_ENDPOINT = 0xA1;
	private static final int CLUSTER_ID = 0x1554;
	private static final int PROFILE_ID = 0xC105;
	
	// Variables.
	private static RemoteXBeeDevice remoteXBeeDevice;
	
	@BeforeClass
	public static void setupOnce() {
		remoteXBeeDevice = Mockito.mock(RemoteXBeeDevice.class);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.ExplicitXBeeMessage#ExplicitXBeeMessage(RemoteXBeeDevice, int, int, int, int, byte[])}.
	 * 
	 * <p>Verify that the {@code ExplicitXBeeMessage} cannot be created if the remote XBee device is null.</p>
	 */
	@Test(expected=NullPointerException.class)
	public void testCreateNullDevice() {
		new ExplicitXBeeMessage(null, SOURCE_ENDPOINT, DESTINATION_ENDPOINT, CLUSTER_ID, PROFILE_ID, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.ExplicitXBeeMessage#ExplicitXBeeMessage(RemoteXBeeDevice, int, int, int, int, byte[])}.
	 * 
	 * <p>Verify that the {@code ExplicitXBeeMessage} cannot be created if the source endpoint is negative.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateSourceEndpointNegative() {
		new ExplicitXBeeMessage(remoteXBeeDevice, -44, DESTINATION_ENDPOINT, CLUSTER_ID, PROFILE_ID, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.ExplicitXBeeMessage#ExplicitXBeeMessage(RemoteXBeeDevice, int, int, int, int, byte[])}.
	 * 
	 * <p>Verify that the {@code ExplicitXBeeMessage} cannot be created if the source endpoint is greater than 255.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateSourceEndpointGreater() {
		new ExplicitXBeeMessage(remoteXBeeDevice, 256, DESTINATION_ENDPOINT, CLUSTER_ID, PROFILE_ID, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.ExplicitXBeeMessage#ExplicitXBeeMessage(RemoteXBeeDevice, int, int, int, int, byte[])}.
	 * 
	 * <p>Verify that the {@code ExplicitXBeeMessage} cannot be created if the source endpoint is negative.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateDestinationEndpointNegative() {
		new ExplicitXBeeMessage(remoteXBeeDevice, SOURCE_ENDPOINT, -59, CLUSTER_ID, PROFILE_ID, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.ExplicitXBeeMessage#ExplicitXBeeMessage(RemoteXBeeDevice, int, int, int, int, byte[])}.
	 * 
	 * <p>Verify that the {@code ExplicitXBeeMessage} cannot be created if the source endpoint is greater than 255.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateDestinationEndpointGreater() {
		new ExplicitXBeeMessage(remoteXBeeDevice, SOURCE_ENDPOINT, 256, CLUSTER_ID, PROFILE_ID, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.ExplicitXBeeMessage#ExplicitXBeeMessage(RemoteXBeeDevice, int, int, int, int, byte[])}.
	 * 
	 * <p>Verify that the {@code ExplicitXBeeMessage} cannot be created if the cluster ID is negative.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateClusterIDNegative() {
		new ExplicitXBeeMessage(remoteXBeeDevice, SOURCE_ENDPOINT, DESTINATION_ENDPOINT, -20, PROFILE_ID, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.ExplicitXBeeMessage#ExplicitXBeeMessage(RemoteXBeeDevice, int, int, int, int, byte[])}.
	 * 
	 * <p>Verify that the {@code ExplicitXBeeMessage} cannot be created if the cluster ID is greater than 65535.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateClusterIDGreater() {
		new ExplicitXBeeMessage(remoteXBeeDevice, SOURCE_ENDPOINT, DESTINATION_ENDPOINT, 65536, PROFILE_ID, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.ExplicitXBeeMessage#ExplicitXBeeMessage(RemoteXBeeDevice, int, int, int, int, byte[])}.
	 * 
	 * <p>Verify that the {@code ExplicitXBeeMessage} cannot be created if the profile ID is negative.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateProfileIDNegative() {
		new ExplicitXBeeMessage(remoteXBeeDevice, SOURCE_ENDPOINT, DESTINATION_ENDPOINT, CLUSTER_ID, -15, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.ExplicitXBeeMessage#ExplicitXBeeMessage(RemoteXBeeDevice, int, int, int, int, byte[])}.
	 * 
	 * <p>Verify that the {@code ExplicitXBeeMessage} cannot be created if the cluster ID is greater than 65535.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateProfileIDGreater() {
		new ExplicitXBeeMessage(remoteXBeeDevice, SOURCE_ENDPOINT, DESTINATION_ENDPOINT, CLUSTER_ID, 65536, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.ExplicitXBeeMessage#ExplicitXBeeMessage(RemoteXBeeDevice, int, int, int, int, byte[])}.
	 * 
	 * <p>Verify that the {@code ExplicitXBeeMessage} cannot be created if the data is null.</p>
	 */
	@Test(expected=NullPointerException.class)
	public void testCreateDataNull() {
		new ExplicitXBeeMessage(remoteXBeeDevice, SOURCE_ENDPOINT, DESTINATION_ENDPOINT, CLUSTER_ID, PROFILE_ID, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.ExplicitXBeeMessage#ExplicitXBeeMessage(RemoteXBeeDevice, int, int, int, int, byte[], boolean)}, 
	 * {@link com.digi.xbee.api.models.ExplicitXBeeMessage#getDevice()},
	 * {@link com.digi.xbee.api.models.ExplicitXBeeMessage#getSourceEndpoint()},
	 * {@link com.digi.xbee.api.models.ExplicitXBeeMessage#getDestinationEndpoint()},
	 * {@link com.digi.xbee.api.models.ExplicitXBeeMessage#getClusterID()},
	 * {@link com.digi.xbee.api.models.ExplicitXBeeMessage#getProfileID()},
	 * {@link com.digi.xbee.api.models.ExplicitXBeeMessage#getData()},
	 * {@link com.digi.xbee.api.models.ExplicitXBeeMessage#getDataString()}, 
	 * {@link com.digi.xbee.api.models.ExplicitXBeeMessage#isBroadcast()}.
	 * 
	 * <p>Verify that the {@code ExplicitXBeeMessage} can be created successfully and the getters work 
	 * properly when the message is unicast.</p>
	 */
	@Test
	public void testCreateSuccessNotBroadcast() {
		ExplicitXBeeMessage explicitXBeeMessage = new ExplicitXBeeMessage(remoteXBeeDevice, SOURCE_ENDPOINT, DESTINATION_ENDPOINT, CLUSTER_ID, PROFILE_ID, DATA.getBytes(), false);
		
		assertEquals(remoteXBeeDevice, explicitXBeeMessage.getDevice());
		assertEquals(SOURCE_ENDPOINT, explicitXBeeMessage.getSourceEndpoint());
		assertEquals(DESTINATION_ENDPOINT, explicitXBeeMessage.getDestinationEndpoint());
		assertEquals(CLUSTER_ID, explicitXBeeMessage.getClusterID());
		assertEquals(PROFILE_ID, explicitXBeeMessage.getProfileID());
		assertArrayEquals(DATA.getBytes(), explicitXBeeMessage.getData());
		assertEquals(DATA, explicitXBeeMessage.getDataString());
		assertFalse(explicitXBeeMessage.isBroadcast());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.ExplicitXBeeMessage#ExplicitXBeeMessage(RemoteXBeeDevice, int, int, int, int, byte[], boolean)}, 
	 * {@link com.digi.xbee.api.models.ExplicitXBeeMessage#getDevice()},
	 * {@link com.digi.xbee.api.models.ExplicitXBeeMessage#getSourceEndpoint()},
	 * {@link com.digi.xbee.api.models.ExplicitXBeeMessage#getDestinationEndpoint()},
	 * {@link com.digi.xbee.api.models.ExplicitXBeeMessage#getClusterID()},
	 * {@link com.digi.xbee.api.models.ExplicitXBeeMessage#getProfileID()},
	 * {@link com.digi.xbee.api.models.ExplicitXBeeMessage#getData()},
	 * {@link com.digi.xbee.api.models.ExplicitXBeeMessage#getDataString()}, 
	 * {@link com.digi.xbee.api.models.ExplicitXBeeMessage#isBroadcast()}.
	 * 
	 * <p>Verify that the {@code ExplicitXBeeMessage} can be created successfully and the getters work 
	 * properly when the message is broadcast.</p>
	 */
	@Test
	public void testCreateSuccessBroadcast() {
		ExplicitXBeeMessage explicitXBeeMessage = new ExplicitXBeeMessage(remoteXBeeDevice, SOURCE_ENDPOINT, DESTINATION_ENDPOINT, CLUSTER_ID, PROFILE_ID, DATA.getBytes(), true);
		
		assertEquals(remoteXBeeDevice, explicitXBeeMessage.getDevice());
		assertEquals(SOURCE_ENDPOINT, explicitXBeeMessage.getSourceEndpoint());
		assertEquals(DESTINATION_ENDPOINT, explicitXBeeMessage.getDestinationEndpoint());
		assertEquals(CLUSTER_ID, explicitXBeeMessage.getClusterID());
		assertEquals(PROFILE_ID, explicitXBeeMessage.getProfileID());
		assertArrayEquals(DATA.getBytes(), explicitXBeeMessage.getData());
		assertEquals(DATA, explicitXBeeMessage.getDataString());
		assertTrue(explicitXBeeMessage.isBroadcast());
	}
}
