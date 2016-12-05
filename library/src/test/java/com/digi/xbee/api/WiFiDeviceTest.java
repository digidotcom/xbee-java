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

import java.io.IOException;
import java.net.Inet4Address;

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
import com.digi.xbee.api.exceptions.ATCommandException;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
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
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#isConnected()}.
	 * 
	 * <p>Verify that it is not possible to determine if the device is connected if 
	 * the connection of the device is closed.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testIsConnectedErrorConnectionClosed() throws XBeeException {
		// Throw an interface not open exception when getting the WiFiAssociationIndicationStatus.
		Mockito.doThrow(new InterfaceNotOpenException()).when(wifiDevice).getWiFiAssociationIndicationStatus();
		
		// Check the connection.
		wifiDevice.isConnected();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#isConnected()}.
	 * 
	 * <p>Verify that it is not possible to determine if the device is connected if 
	 * the operating mode of the device is not valid.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testIsConnectedErrorInvalidOperatingMode() throws XBeeException {
		// Throw an invalid operating mode exception when getting the WiFiAssociationIndicationStatus.
		Mockito.doThrow(new InvalidOperatingModeException()).when(wifiDevice).getWiFiAssociationIndicationStatus();
		
		// Check the connection.
		wifiDevice.isConnected();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#isConnected()}.
	 * 
	 * <p>Verify that it is not possible to determine if the device is connected 
	 * when there is an Timeout exception getting the WiFiAssociationIndicationStatus.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testIsConnectedErrorTimeout() throws XBeeException, IOException {
		// Throw a timeout exception when getting the WiFiAssociationIndicationStatus.
		Mockito.doThrow(new TimeoutException()).when(wifiDevice).getWiFiAssociationIndicationStatus();
		
		// Check the connection.
		wifiDevice.isConnected();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#isConnected()}.
	 * 
	 * <p>Verify that it is not possible to determine if the device is connected 
	 * when there is an AT command exception getting the WiFiAssociationIndicationStatus.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testIsConnectedErrorInvalidAnswer() throws XBeeException, IOException {
		// Throw an AT command exception when when getting the WiFiAssociationIndicationStatus.
		Mockito.doThrow(new ATCommandException(null)).when(wifiDevice).getWiFiAssociationIndicationStatus();
		
		// Check the connection.
		wifiDevice.isConnected();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#isConnected()}.
	 * 
	 * <p>Verify that it is possible to determine if the device is connected or not 
	 * when it is connected.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testIsConnectedSuccessConnected() throws XBeeException, IOException {
		// Return the connected value when when getting the WiFiAssociationIndicationStatus.
		Mockito.doReturn(WiFiAssociationIndicationStatus.SUCCESSFULLY_JOINED).when(wifiDevice).getWiFiAssociationIndicationStatus();
		
		// Check the connection.
		assertTrue(wifiDevice.isConnected());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#isConnected()}.
	 * 
	 * <p>Verify that it is possible to determine if the device is connected or not 
	 * when it is disconnected.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testIsConnectedSuccessDisconnected() throws XBeeException, IOException {
		// Return a valid disconnected value when when getting the WiFiAssociationIndicationStatus.
		Mockito.doReturn(WiFiAssociationIndicationStatus.WAITING_FOR_IP).when(wifiDevice).getWiFiAssociationIndicationStatus();
		
		// Check the connection.
		assertFalse(wifiDevice.isConnected());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#setWiFiReceiveTimeout(int)}.
	 * 
	 * <p>Check that the Wi-Fi receive timeout cannot be set if it is negative throwing an 
	 * {@code IllegalArgumentException}.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetWiFiReceiveTimeoutNegative() {
		// Call the method under test.
		wifiDevice.setWiFiReceiveTimeout(-50);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#setWiFiReceiveTimeout(int)} and 
	 * {@link com.digi.xbee.api.WiFiDevice#getWiFiReceiveTimeout()}.
	 * 
	 * <p>Check that the Wi-Fi receive timeout can be set and get successfully.</p>
	 */
	@Test
	public void testSetWiFiReceiveTimeoutSuccess() {
		// First, verify that the timeout has the default value before changing it. 
		assertEquals(15000, wifiDevice.getWiFiReceiveTimeout());
		
		// Call the method under test (change the timeout).
		wifiDevice.setWiFiReceiveTimeout(5000);
		
		// First, verify that the new valu was set. 
		assertEquals(5000, wifiDevice.getWiFiReceiveTimeout());
	}
}
