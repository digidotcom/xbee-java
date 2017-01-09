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

public class XBeeMessageTest {

	// Constants.
	private final static String DATA = "Data";
	
	// Variables.
	private static RemoteXBeeDevice remoteXBeeDevice;
	
	@BeforeClass
	public static void setupOnce() {
		remoteXBeeDevice = Mockito.mock(RemoteXBeeDevice.class);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeMessage#XBeeMessage(RemoteXBeeDevice, byte[])}.
	 * 
	 * <p>Verify that the {@code XBeeMessage} cannot be created if the remote XBee device is null.</p>
	 */
	@Test(expected=NullPointerException.class)
	public void testCreateNullDevice() {
		new XBeeMessage(null, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeMessage#XBeeMessage(RemoteXBeeDevice, byte[])}.
	 * 
	 * <p>Verify that the {@code XBeeMessage} cannot be created if the data is null.</p>
	 */
	@Test(expected=NullPointerException.class)
	public void testCreateNullData() {
		new XBeeMessage(remoteXBeeDevice, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeMessage#XBeeMessage(RemoteXBeeDevice, byte[])}, 
	 * {@link com.digi.xbee.api.models.XBeeMessage#getDevice()}, 
	 * {@link com.digi.xbee.api.models.XBeeMessage#getData()} and 
	 * {@link com.digi.xbee.api.models.XBeeMessage#getDataString()}, 
	 * {@link com.digi.xbee.api.models.XBeeMessage#isBroadcast()}.
	 * 
	 * <p>Verify that the {@code XBeeMessage} can be created successfully and the getters work 
	 * properly when the message is unicast.</p>
	 */
	@Test
	public void testCreateSuccessNotBroadcast() {
		XBeeMessage xbeeMessage = new XBeeMessage(remoteXBeeDevice, DATA.getBytes(), false);
		
		assertEquals(remoteXBeeDevice, xbeeMessage.getDevice());
		assertArrayEquals(DATA.getBytes(), xbeeMessage.getData());
		assertEquals(DATA, xbeeMessage.getDataString());
		assertFalse(xbeeMessage.isBroadcast());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeMessage#XBeeMessage(RemoteXBeeDevice, byte[], boolean)}, 
	 * {@link com.digi.xbee.api.models.XBeeMessage#getDevice()}, 
	 * {@link com.digi.xbee.api.models.XBeeMessage#getData()} and 
	 * {@link com.digi.xbee.api.models.XBeeMessage#getDataString()}, 
	 * {@link com.digi.xbee.api.models.XBeeMessage#isBroadcast()}.
	 * 
	 * <p>Verify that the {@code XBeeMessage} can be created successfully and the getters work 
	 * properly when the message is broadcast.</p>
	 */
	@Test
	public void testCreateSuccessBroadcast() {
		XBeeMessage xbeeMessage = new XBeeMessage(remoteXBeeDevice, DATA.getBytes(), true);
		
		assertEquals(remoteXBeeDevice, xbeeMessage.getDevice());
		assertArrayEquals(DATA.getBytes(), xbeeMessage.getData());
		assertEquals(DATA, xbeeMessage.getDataString());
		assertTrue(xbeeMessage.isBroadcast());
	}
}
