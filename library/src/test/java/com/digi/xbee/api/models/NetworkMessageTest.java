/**
 * Copyright (c) 2016 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */
package com.digi.xbee.api.models;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class NetworkMessageTest {

	// Constants.
	private final static String DATA = "Data";
	
	// Variables.
	private static IP32BitAddress ipAddress;
	
	private static int sourcePort = 123;
	private static int destPort = 456;
	
	private static NetworkProtocol protocol = NetworkProtocol.TCP;
	
	@BeforeClass
	public static void setupOnce() {
		ipAddress = Mockito.mock(IP32BitAddress.class);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.NetworkMessage#NetworkMessage(IP32BitAddress, int, int, NetworkProtocol byte[])}.
	 * 
	 * <p>Verify that the {@code NetworkMessage} cannot be created if the IP address is null.</p>
	 */
	@Test(expected=NullPointerException.class)
	public void testCreateNullIP() {
		new NetworkMessage(null, sourcePort, destPort, protocol, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.NetworkMessage#NetworkMessage(IP32BitAddress, int, int, NetworkProtocol, byte[])}.
	 * 
	 * <p>Verify that the {@code NetworkMessage} cannot be created if the protocol is null.</p>
	 */
	@Test(expected=NullPointerException.class)
	public void testCreateNullProtocol() {
		new NetworkMessage(ipAddress, sourcePort, destPort, null, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.NetworkMessage#NetworkMessage(IP32BitAddress, int, int, NetworkProtocol, byte[])}.
	 * 
	 * <p>Verify that the {@code NetworkMessage} cannot be created if the destination port is 
	 * greater than 65535.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateIllegalSourcePortBig() {
		new NetworkMessage(ipAddress, 80000, destPort, protocol, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.NetworkMessage#NetworkMessage(IP32BitAddress, int, int, NetworkProtocol, byte[])}.
	 * 
	 * <p>Verify that the {@code NetworkMessage} cannot be created if the source port is negative.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateIllegalSourcePortNegative() {
		new NetworkMessage(ipAddress, -5, destPort, protocol, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.NetworkMessage#NetworkMessage(IP32BitAddress, int, int, NetworkProtocol, byte[])}.
	 * 
	 * <p>Verify that the {@code NetworkMessage} cannot be created if the destination port is 
	 * greater than 65535.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateIllegalDestPortBig() {
		new NetworkMessage(ipAddress, sourcePort, 80000, protocol, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.NetworkMessage#NetworkMessage(IP32BitAddress, int, int, NetworkProtocol, byte[])}.
	 * 
	 * <p>Verify that the {@code NetworkMessage} cannot be created if the destination port is 
	 * negative.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateIllegalDestPortNegative() {
		new NetworkMessage(ipAddress, sourcePort, -5, protocol, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.NetworkMessage#NetworkMessage(IP32BitAddress, int, int, NetworkProtocol, byte[])}.
	 * 
	 * <p>Verify that the {@code NetworkMessage} cannot be created if the data is null.</p>
	 */
	@Test(expected=NullPointerException.class)
	public void testCreateNullData() {
		new NetworkMessage(ipAddress, sourcePort, destPort, protocol, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.NetworkMessage#NetworkMessage(IP32BitAddress, int, int, NetworkProtocol, byte[])}, 
	 * {@link com.digi.xbee.api.models.NetworkMessage#getIPAddress()},
	 * {@link com.digi.xbee.api.models.NetworkMessage#getSourcePort()}, 
	 * {@link com.digi.xbee.api.models.NetworkMessage#getDestPort()}, 
	 * {@link com.digi.xbee.api.models.NetworkMessage#getProtocol()},  
	 * {@link com.digi.xbee.api.models.NetworkMessage#getData()} and 
	 * {@link com.digi.xbee.api.models.NetworkMessage#getDataString()}.
	 * 
	 * <p>Verify that the {@code NetworkMessage} can be created successfully and the getters work 
	 * properly.</p>
	 */
	@Test
	public void testCreateSuccess() {
		NetworkMessage networkMessage = new NetworkMessage(ipAddress, sourcePort, destPort, protocol, DATA.getBytes());
		
		assertEquals(ipAddress, networkMessage.getIPAddress());
		assertEquals(sourcePort, networkMessage.getSourcePort());
		assertEquals(destPort, networkMessage.getDestPort());
		assertEquals(protocol, networkMessage.getProtocol());
		assertArrayEquals(DATA.getBytes(), networkMessage.getData());
		assertEquals(DATA, networkMessage.getDataString());
	}
}
