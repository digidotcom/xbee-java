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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.models.WiFiAssociationIndicationStatus;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WiFiDevice.class, WiFiAssociationIndicationStatus.class})
public class WiFiDeviceTest {
	
	// Constants.
	private static final String PARAMETER_AI = "AI";
	private static final byte[] RESPONSE_AI = new byte[]{0x00};
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	private WiFiDevice wifiDevice;
	
	private WiFiAssociationIndicationStatus validWiFiAIStatus = WiFiAssociationIndicationStatus.SUCCESSFULLY_JOINED;
	
	@Before
	public void setup() throws Exception {
		// Spy the WiFiDevice class.
		SerialPortRxTx mockPort = Mockito.mock(SerialPortRxTx.class);
		wifiDevice = PowerMockito.spy(new WiFiDevice(mockPort));
		
		// Always return a valid Wi-Fi association indication when requested.
		PowerMockito.mockStatic(WiFiAssociationIndicationStatus.class);
		PowerMockito.when(WiFiAssociationIndicationStatus.get(Mockito.anyInt())).thenReturn(validWiFiAIStatus);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#getWiFiAssociationIndicationStatus()}.
	 * 
	 * <p>Check that the Wi-Fi association indication method returns values 
	 * successfully.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAssociationIndicationStatus() throws Exception {
		// Return a valid response when requesting the AI parameter value.
		Mockito.doReturn(RESPONSE_AI).when(wifiDevice).getParameter(PARAMETER_AI);
		
		assertEquals(validWiFiAIStatus, wifiDevice.getWiFiAssociationIndicationStatus());
	}
}
