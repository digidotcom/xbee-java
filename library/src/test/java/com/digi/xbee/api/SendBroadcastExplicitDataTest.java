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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.OperationNotSupportedException;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeProtocol;

public class SendBroadcastExplicitDataTest {
	
	// Constants.
	private static final int SOURCE_ENDPOINT = 0xA0;
	private static final int DESTINATION_ENDPOINT = 0xA1;
	private static final int CLUSTER_ID = 0x1554;
	private static final int PROFILE_ID = 0xC105;
	
	private static final String DATA = "data";
	
	// Variables.
	private XBeeDevice xbeeDevice;
	
	@Before
	public void setup() throws Exception {
		// Instantiate an XBeeDevice object with a mocked interface.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(Mockito.mock(SerialPortRxTx.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#sendBroadcastExplicitData(byte[])}.
	 * 
	 * <p>Verify that broadcast explicit data can be sent successfully.</p>
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testSendBroadcastExplicitData802() throws Exception {
		// Return that the protocol of the XBee device is 802.15.4 when asked.
		Mockito.doReturn(XBeeProtocol.RAW_802_15_4).when(xbeeDevice).getXBeeProtocol();
				
		xbeeDevice.sendBroadcastExplicitData(SOURCE_ENDPOINT, DESTINATION_ENDPOINT, CLUSTER_ID, PROFILE_ID, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#sendBroadcastExplicitData(byte[])}.
	 * 
	 * <p>Verify that broadcast explicit data can be sent successfully.</p>
	 */
	@Test
	public void testSendBroadcastExplicitDataSuccess() throws Exception {
		// Do nothing when the sendData(XBee64BitAddress, byte[]) method is called.
		Mockito.doNothing().when(xbeeDevice).sendExplicitData(Mockito.any(XBee64BitAddress.class), Mockito.anyInt(), Mockito.anyInt(), 
				Mockito.anyInt(), Mockito.anyInt(), Mockito.any(byte[].class));
				
		xbeeDevice.sendBroadcastExplicitData(SOURCE_ENDPOINT, DESTINATION_ENDPOINT, CLUSTER_ID, PROFILE_ID, DATA.getBytes());
		
		// Verify that the method sendExplicitData(XBee64BitAddress, int, int, int, int, byte[]) was called with a BROADCAST_ADDRESS.
		Mockito.verify(xbeeDevice, Mockito.times(1)).sendExplicitData(Mockito.eq(XBee64BitAddress.BROADCAST_ADDRESS), Mockito.eq(SOURCE_ENDPOINT), 
				Mockito.eq(DESTINATION_ENDPOINT), Mockito.eq(CLUSTER_ID), Mockito.eq(PROFILE_ID), Mockito.eq(DATA.getBytes()));
	}
}
