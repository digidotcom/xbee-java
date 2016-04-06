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

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.ATCommandException;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;

public class ApplyChangesTest {

	// Constants.
	private static final String PARAMETER_AC = "AC";
	
	// Variables.
	private XBeeDevice xbeeDevice;
	
	@Before
	public void setup() throws Exception {
		// Instantiate a local XBeeDevice object.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(Mockito.mock(SerialPortRxTx.class)));
		
		Mockito.when(xbeeDevice.isOpen()).thenReturn(true);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#applyChanges()}.
	 * 
	 * <p>Verify that changes on an XBee device cannot be applied if the connection of the 
	 * device is closed.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testApplyChangesErrorConnectionClosed() throws XBeeException {
		// Throw an interface not open exception when trying to set any parameter.
		Mockito.doThrow(new InterfaceNotOpenException()).when(xbeeDevice).executeParameter(PARAMETER_AC);
		
		// Apply changes in the device.
		xbeeDevice.applyChanges();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#applyChanges()}.
	 * 
	 * <p>Verify that changes on an XBee device cannot be applied if the operating mode of the 
	 * device is not valid.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testApplyChangesErrorInvalidOperatingMode() throws XBeeException {
		// Throw an invalid operating mode exception when trying to set any parameter.
		Mockito.doThrow(new InvalidOperatingModeException()).when(xbeeDevice).executeParameter(PARAMETER_AC);
		
		// Apply changes in the device.
		xbeeDevice.applyChanges();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#applyChanges()}.
	 * 
	 * <p>Verify that changes on an XBee device cannot be applied if there is a timeout applying 
	 * those changes.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testApplyChangesErrorTimeout() throws XBeeException, IOException {
		// Throw a timeout exception when trying to set the AC parameter.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).executeParameter(PARAMETER_AC);
		
		// Apply changes in the device.
		xbeeDevice.applyChanges();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#applyChanges()}.
	 * 
	 * <p>Verify that changes on an XBee device cannot be written if the answer when applying 
	 * the changes is null or the response status is not OK. It is, there is an AT command exception 
	 * applying the changes.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testApplyChangesErrorInvalidAnswer() throws XBeeException, IOException {
		// Throw an AT command exception when trying to set the AC parameter.
		Mockito.doThrow(new ATCommandException(null)).when(xbeeDevice).executeParameter(PARAMETER_AC);
		
		// Apply changes in the device.
		xbeeDevice.applyChanges();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#applyChanges()}.
	 * 
	 * <p>Verify that changes on an XBee device can be applied successfully.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testApplyChangesSuccess() throws XBeeException, IOException {
		// Do nothing when trying to set the AC parameter.
		Mockito.doNothing().when(xbeeDevice).executeParameter(PARAMETER_AC);
		
		// Apply changes in the device.
		xbeeDevice.applyChanges();
	}
}
