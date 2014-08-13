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
package com.digi.xbee.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.InterfaceAlreadyOpenException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.XBee64BitAddress;

@RunWith(PowerMockRunner.class)
@PrepareForTest({XBee64BitAddress.class})
public class RemoteXBeeDeviceConnectTest {

	// Variables.
	private SerialPortRxTx connectionInterface;
	private XBeeDevice remoteXBeeDevice;
	
	@Before
	public void setup() throws Exception {
		// Mock the connection interface to be returned by the XBee class.
		connectionInterface = Mockito.mock(SerialPortRxTx.class);
		// Stub the 'open' method of the connectionInterface mock so when checking if the 
		// interface is open next time it returns true.
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Mockito.when(connectionInterface.isOpen()).thenReturn(true);
				return null;
			}
		}).when(connectionInterface).open();
		// Stub the 'close' method of the connectionInterface mock so when checking if the 
		// interface is open next time it returns false.
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Mockito.when(connectionInterface.isOpen()).thenReturn(false);
				return null;
			}
		}).when(connectionInterface).close();
		
		// Mock the local XBee device and 64-bit address objects necessary to instantiate a remote 
		// XBee device.
		XBeeDevice mockedLocalXBeeDevice = Mockito.mock(XBeeDevice.class);
		Mockito.when(mockedLocalXBeeDevice.getConnectionInterface()).thenReturn(connectionInterface);
		
		XBee64BitAddress mockedAddress = PowerMockito.mock(XBee64BitAddress.class);
		
		// Instantiate the remote XBee device.
		remoteXBeeDevice = PowerMockito.spy(new XBeeDevice(mockedLocalXBeeDevice, mockedAddress));
	}
	
	/**
	 * Verify that when connecting a remote XBee device, the process opens the local XBee device 
	 * interface, but does not try to determine the operating mode of the remote one.
	 * 
	 * @throws XBeeException
	 */
	@Test
	public void testOpemRemoteXBeeSuccess() throws XBeeException {
		// Execute the connect method.
		remoteXBeeDevice.open();
		
		// As the device is remote, the determineOperatingMode method should be never called when opening 
		// it. So, the operating mode will be unknown.
		Mockito.verify(remoteXBeeDevice, Mockito.never()).determineOperatingMode();
		assertEquals(OperatingMode.UNKNOWN, remoteXBeeDevice.getOperatingMode());
		// Verify that the device was opened correctly.
		assertTrue(remoteXBeeDevice.isOpen());
	}
	
	/**
	 * Verify that when the connection is already open and the connect method is called an 
	 * exception is thrown.
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=InterfaceAlreadyOpenException.class)
	public void testOpenRemoteXBeeAlreadyOpen() throws XBeeException {
		// Configure the connection interface of the local XBee to indicate that it is
		// open when asked.
		Mockito.when(connectionInterface.isOpen()).thenReturn(true);
		
		// Execute the connect method.
		remoteXBeeDevice.open();
		
		// Verify that the device is still open.
		assertTrue(remoteXBeeDevice.isOpen());
		
		// Close the connection.
		remoteXBeeDevice.close();
		
		// Verify the device is not connected.
		assertFalse(remoteXBeeDevice.isOpen());
	}
}
