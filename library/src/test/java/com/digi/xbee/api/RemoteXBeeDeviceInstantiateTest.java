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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.models.XBee64BitAddress;

@RunWith(PowerMockRunner.class)
@PrepareForTest({XBee64BitAddress.class})
public class RemoteXBeeDeviceInstantiateTest {

	/**
	 * Test method for {@link com.digi.xbee.api.RemoteXBeeDevice#RemoteXBeeDevice(XBeeDevice, XBee64BitAddress)}.
	 * 
	 * <p>Verify that a remote XBeeDevice object is not correctly instantiated when the local 
	 * XBee device associated is null or the remote XBee64BitAddress is null.</p>
	 */
	@Test
	public void testInstantiateRemoteXBeeDeviceBadParameters() {
		// Instantiate a remote XBeeDevice object with a null local XBeeDevice.
		try {
			new RemoteXBeeDevice(null, PowerMockito.mock(XBee64BitAddress.class));
			fail("Remote device shouldn't have been instantiated correctly.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
		
		// Instantiate a remote XBeeDevice object with a null XBee64Bitaddress.
		try {
			new RemoteXBeeDevice(Mockito.mock(XBeeDevice.class), null);
			fail("Remote device shouldn't have been instantiated correctly.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.RemoteXBeeDevice#RemoteXBeeDevice(XBeeDevice, XBee64BitAddress)}.
	 * 
	 * <p>Verify that a remote XBeeDevice object can be instantiated correctly.</p>
	 */
	@Test
	public void testInstantiateRemoteXBeeDeviceSuccess() {
		// Mock the necessary objects to instantiate a remote XBee device.
		IConnectionInterface mockedInterface = Mockito.mock(IConnectionInterface.class);
		XBeeDevice mockedLocalDevice = Mockito.mock(XBeeDevice.class);
		Mockito.when(mockedLocalDevice.getConnectionInterface()).thenReturn(mockedInterface);
		
		XBee64BitAddress mockedAddress = PowerMockito.mock(XBee64BitAddress.class);
		
		// Instantiate the remote XBee device.
		RemoteXBeeDevice remoteXBeeDevice = new RemoteXBeeDevice(mockedLocalDevice, mockedAddress);
		
		// Verify the 64-bit address and connection interface are the expected.
		assertEquals(mockedAddress, remoteXBeeDevice.get64BitAddress());
		assertEquals(mockedInterface, remoteXBeeDevice.getConnectionInterface());
		// Verify the device is remote.
		assertTrue(remoteXBeeDevice.isRemote());
	}
}
