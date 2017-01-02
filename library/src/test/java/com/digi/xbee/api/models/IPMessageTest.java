/**
 * Copyright (c) 2016-2017 Digi International Inc.,
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

import java.net.Inet4Address;

import org.junit.BeforeClass;
import org.junit.Test;

public class IPMessageTest {

	// Constants.
	private final static String DATA = "Data";
	
	private final static String IP_ADDRESS = "10.11.12.13";
	
	// Variables.
	private static Inet4Address ipAddress;
	
	private static int sourcePort = 123;
	private static int destPort = 456;
	
	private static IPProtocol protocol = IPProtocol.TCP;
	
	@BeforeClass
	public static void setupOnce() throws Exception {
		ipAddress = (Inet4Address) Inet4Address.getByName(IP_ADDRESS);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.IPMessage#IPMessage(Inet4Address, int, int, IPProtocol byte[])}.
	 * 
	 * <p>Verify that the {@code IPMessage} cannot be created if the IP address is null.</p>
	 */
	@Test(expected=NullPointerException.class)
	public void testCreateNullIP() {
		new IPMessage(null, sourcePort, destPort, protocol, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.IPMessage#IPMessage(Inet4Address, int, int, IPProtocol, byte[])}.
	 * 
	 * <p>Verify that the {@code IPMessage} cannot be created if the protocol is null.</p>
	 */
	@Test(expected=NullPointerException.class)
	public void testCreateNullProtocol() {
		new IPMessage(ipAddress, sourcePort, destPort, null, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.IPMessage#IPMessage(Inet4Address, int, int, IPProtocol, byte[])}.
	 * 
	 * <p>Verify that the {@code IPMessage} cannot be created if the destination port is 
	 * greater than 65535.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateIllegalSourcePortBig() {
		new IPMessage(ipAddress, 80000, destPort, protocol, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.IPMessage#IPMessage(Inet4Address, int, int, IPProtocol, byte[])}.
	 * 
	 * <p>Verify that the {@code IPMessage} cannot be created if the source port is negative.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateIllegalSourcePortNegative() {
		new IPMessage(ipAddress, -5, destPort, protocol, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.IPMessage#IPMessage(Inet4Address, int, int, IPProtocol, byte[])}.
	 * 
	 * <p>Verify that the {@code IPMessage} cannot be created if the destination port is 
	 * greater than 65535.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateIllegalDestPortBig() {
		new IPMessage(ipAddress, sourcePort, 80000, protocol, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.IPMessage#IPMessage(Inet4Address, int, int, IPProtocol, byte[])}.
	 * 
	 * <p>Verify that the {@code IPMessage} cannot be created if the destination port is 
	 * negative.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateIllegalDestPortNegative() {
		new IPMessage(ipAddress, sourcePort, -5, protocol, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.IPMessage#IPMessage(Inet4Address, int, int, IPProtocol, byte[])}.
	 * 
	 * <p>Verify that the {@code IPMessage} cannot be created if the data is null.</p>
	 */
	@Test(expected=NullPointerException.class)
	public void testCreateNullData() {
		new IPMessage(ipAddress, sourcePort, destPort, protocol, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.IPMessage#IPMessage(Inet4Address, int, int, IPProtocol, byte[])}, 
	 * {@link com.digi.xbee.api.models.IPMessage#getIPAddress()},
	 * {@link com.digi.xbee.api.models.IPMessage#getSourcePort()}, 
	 * {@link com.digi.xbee.api.models.IPMessage#getDestPort()}, 
	 * {@link com.digi.xbee.api.models.IPMessage#getProtocol()},  
	 * {@link com.digi.xbee.api.models.IPMessage#getData()} and 
	 * {@link com.digi.xbee.api.models.IPMessage#getDataString()}.
	 * 
	 * <p>Verify that the {@code IPMessage} can be created successfully and the getters work 
	 * properly.</p>
	 */
	@Test
	public void testCreateSuccess() {
		IPMessage ipMessage = new IPMessage(ipAddress, sourcePort, destPort, protocol, DATA.getBytes());
		
		assertEquals(ipAddress, ipMessage.getIPAddress());
		assertEquals(sourcePort, ipMessage.getSourcePort());
		assertEquals(destPort, ipMessage.getDestPort());
		assertEquals(protocol, ipMessage.getProtocol());
		assertArrayEquals(DATA.getBytes(), ipMessage.getData());
		assertEquals(DATA, ipMessage.getDataString());
	}
}
