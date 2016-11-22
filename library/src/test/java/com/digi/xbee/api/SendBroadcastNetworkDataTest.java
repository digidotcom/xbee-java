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
package com.digi.xbee.api;

import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.models.IP32BitAddress;
import com.digi.xbee.api.models.NetworkProtocol;

public class SendBroadcastNetworkDataTest {
	
	// Constants.
	private static final String DATA = "data";
	
	private static final NetworkProtocol PROTOCOL = NetworkProtocol.TCP;
	
	private static final int PORT = 12345;
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendBroadcastNetworkData(int, NetworkProtocol, byte[])}.
	 * 
	 * <p>Verify that broadcast network data can be sent successfully.</p>
	 */
	@Test
	public void testSendBroadcastNetworkDataSuccess() throws Exception {
		// Instantiate an IPDevice object with a mocked interface.
		IPDevice ipDevice = PowerMockito.spy(new IPDevice(Mockito.mock(SerialPortRxTx.class)));
		
		// Do nothing when the sendNetowrkData(IP32BitAddress, int, NetworkProtocol, boolean, byte[]) method is called.
		Mockito.doNothing().when(ipDevice).sendNetworkData(Mockito.any(IP32BitAddress.class), Mockito.any(int.class), Mockito.any(NetworkProtocol.class), Mockito.anyBoolean(), Mockito.any(byte[].class));
				
		ipDevice.sendBroadcastNetworkData(PORT, PROTOCOL, DATA.getBytes());
		
		// Verify that the method sendNetworkData(IP32BitAddress, int, NetworkProtocol, boolean, byte[]) was called with a BROADCAST_ADDRESS.
		Mockito.verify(ipDevice, Mockito.times(1)).sendNetworkData(Mockito.eq(IP32BitAddress.BROADCAST_ADDRESS), Mockito.eq(PORT), Mockito.eq(PROTOCOL), Mockito.eq(false), Mockito.eq(DATA.getBytes()));
	}
}