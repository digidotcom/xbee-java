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
package com.digi.xbee.api.models;

import static org.junit.Assert.*;

import org.junit.Test;

public class XBeeMessageTest {

	// Constants.
	private final static String DATA = "Data";
	private final static String ADDRESS = "0123456789ABCDEF";
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeMessage#XBeeMessage(String, byte[])}.
	 * 
	 * <p>Verify that the {@code XBeeMessage} cannot be created if the address is null.</p>
	 */
	@Test(expected=NullPointerException.class)
	public void TestCreateNullAddress() {
		new XBeeMessage(null, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeMessage#XBeeMessage(String, byte[])}.
	 * 
	 * <p>Verify that the {@code XBeeMessage} cannot be created if the address is empty.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void TestCreateEmptyAddress() {
		new XBeeMessage("", DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeMessage#XBeeMessage(String, byte[])}.
	 * 
	 * <p>Verify that the {@code XBeeMessage} cannot be created if the data is null.</p>
	 */
	@Test(expected=NullPointerException.class)
	public void TestCreateNullData() {
		new XBeeMessage(ADDRESS, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeMessage#XBeeMessage(String, byte[])}, 
	 * {@link com.digi.xbee.api.models.XBeeMessage#getAddress()}, 
	 * {@link com.digi.xbee.api.models.XBeeMessage#getData()} and 
	 * {@link com.digi.xbee.api.models.XBeeMessage#getDataString()}.
	 * 
	 * <p>Verify that the {@code XBeeMessage} can be created successfully and the getters work 
	 * properly.</p>
	 */
	@Test
	public void TestCreateSuccess() {
		XBeeMessage xbeeMessage = new XBeeMessage(ADDRESS, DATA.getBytes());
		
		assertEquals(ADDRESS, xbeeMessage.getAddress());
		assertArrayEquals(DATA.getBytes(), xbeeMessage.getData());
		assertEquals(DATA, xbeeMessage.getDataString());
	}
}
