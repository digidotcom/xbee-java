/**
 * Copyright (c) 2014-2016 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */
package com.digi.xbee.api;

import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.models.XBee64BitAddress;

public class SendBroadcastDataTest {
	
	// Constants.
	private static final String DATA = "data";
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#sendBroadcastData(byte[])}.
	 * 
	 * <p>Verify that broadcast data can be sent successfully.</p>
	 */
	@Test
	public void testSendBroadcastDataSuccess() throws Exception {
		// Instantiate an XBeeDevice object with a mocked interface.
		XBeeDevice xbeeDevice = PowerMockito.spy(new XBeeDevice(Mockito.mock(SerialPortRxTx.class)));
		
		// Do nothing when the sendData(XBee64BitAddress, byte[]) method is called.
		Mockito.doNothing().when(xbeeDevice).sendData(Mockito.any(XBee64BitAddress.class), Mockito.any(byte[].class));
				
		xbeeDevice.sendBroadcastData(DATA.getBytes());
		
		// Verify that the method sendData(XBee64BitAddress, byte[]) was called with a BROADCAST_ADDRESS.
		Mockito.verify(xbeeDevice, Mockito.times(1)).sendData(Mockito.eq(XBee64BitAddress.BROADCAST_ADDRESS), Mockito.eq(DATA.getBytes()));
	}
}