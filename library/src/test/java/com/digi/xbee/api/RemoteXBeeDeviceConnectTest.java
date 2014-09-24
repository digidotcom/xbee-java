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
import static org.junit.Assert.fail;

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
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.XBee64BitAddress;

@RunWith(PowerMockRunner.class)
@PrepareForTest({XBee64BitAddress.class})
public class RemoteXBeeDeviceConnectTest {

	// Variables.
	private RemoteXBeeDevice remoteXBeeDevice;
	private XBeeDevice localXBeeDevice;
	
	@Before
	public void setup() throws Exception {
		// Mock the connection interface to be returned by the XBee class.
		SerialPortRxTx connectionInterface = Mockito.mock(SerialPortRxTx.class);
		
		// Mock the local XBee device and 64-bit address objects necessary to instantiate a remote 
		// XBee device.
		localXBeeDevice = Mockito.mock(XBeeDevice.class);
		Mockito.when(localXBeeDevice.getConnectionInterface()).thenReturn(connectionInterface);
		
		XBee64BitAddress mockedAddress = Mockito.mock(XBee64BitAddress.class);
		
		// Stub the 'open' method of the localXBeeDevice mock so when checking if the 
		// interface is open next time it returns true.
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Mockito.when(localXBeeDevice.isOpen()).thenReturn(true);
				return null;
			}
		}).when(localXBeeDevice).open();
		// Stub the 'close' method of the localXBeeDevice mock so when checking if the 
		// interface is open next time it returns false.
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Mockito.when(localXBeeDevice.isOpen()).thenReturn(false);
				return null;
			}
		}).when(localXBeeDevice).close();
		
		// Instantiate the remote XBee device.
		remoteXBeeDevice = PowerMockito.spy(new RemoteXBeeDevice(localXBeeDevice, mockedAddress));
		// Stub the initializeDevice method to do nothing (it has its own test file).
		Mockito.doNothing().when(remoteXBeeDevice).initializeDevice();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.RemoteXBeeDevice#open()}.
	 * 
	 * <p>Verify that when connecting a remote XBee device, the process opens the local XBee device 
	 * interface, but does not try to determine the operating mode of the remote one.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test
	public void testOpenRemoteXBeeSuccess() throws XBeeException {
		// Execute the connect method.
		remoteXBeeDevice.open();
		
		// Verify that the device was opened correctly.
		assertTrue(remoteXBeeDevice.isOpen());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.RemoteXBeeDevice#open()}.
	 * 
	 * <p>Verify that when there is a problem opening the remote device (indeed the problem is 
	 * raised opening the local one), an exception is thrown, and the remote device is not open.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public void testOpenRemoteXBeeError() throws XBeeException {
		// Configure the connection interface of the local XBee to indicate that it is
		// open when asked.
		Mockito.doThrow(new XBeeException()).when(localXBeeDevice).open();
		
		// Execute the open method.
		try {
			remoteXBeeDevice.open();
			fail("Device shouldn't have connected");
		} catch (Exception e) {
			assertEquals(XBeeException.class, e.getClass());
		}
		
		// Verify that the device is not open.
		assertFalse(remoteXBeeDevice.isOpen());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.RemoteXBeeDevice#open()}.
	 * 
	 * <p>Verify that if the local device is already open and the {@code open()} method of the 
	 * remote one is called, there is not any error and the remote device will be open.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public void testOpenRemoteAlreadyOpen() throws XBeeException {
		// Open the local device.
		localXBeeDevice.open();
		
		// Execute the open method of the remote one.
		remoteXBeeDevice.open();
		
		// Verify that the device is open.
		assertTrue(remoteXBeeDevice.isOpen());
		
		// Close the connection.
		remoteXBeeDevice.close();
		
		// Verify the device is not connected.
		assertFalse(remoteXBeeDevice.isOpen());
	}
}
