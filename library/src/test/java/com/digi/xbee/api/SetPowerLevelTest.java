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
import com.digi.xbee.api.models.PowerLevel;

public class SetPowerLevelTest {

	// Constants.
	private static final String PARAMETER_MY = "PL";
	
	private static final PowerLevel POWER_LEVEL = PowerLevel.LEVEL_LOW;
	
	// Variables.
	private XBeeDevice xbeeDevice;
	
	@Before
	public void setup() throws Exception {
		// Instantiate a local XBeeDevice object.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(Mockito.mock(SerialPortRxTx.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setPowerLevel(PowerLevel)}.
	 * 
	 * <p>Verify that the power level of an XBee device cannot be set if the power level provided 
	 * is null.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=NullPointerException.class)
	public void testSetPowerLevelErrorNullPowerLevel() throws XBeeException {
		// Set the power level.
		xbeeDevice.setPowerLevel(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setPowerLevel(PowerLevel)}.
	 * 
	 * <p>Verify that the power level of an XBee device cannot be set if the connection of the 
	 * device is closed.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testSetPowerLevelErrorConnectionClosed() throws XBeeException {
		// Throw an interface not open exception when trying to set any parameter.
		Mockito.doThrow(new InterfaceNotOpenException()).when(xbeeDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		// Set the power level.
		xbeeDevice.setPowerLevel(POWER_LEVEL);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setPowerLevel(PowerLevel)}.
	 * 
	 * <p>Verify that the power level of an XBee device cannot be set if the operating mode of the 
	 * device is not valid.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSetPowerLevelErrorInvalidOperatingMode() throws XBeeException {
		// Throw an invalid operating mode exception when trying to set any parameter.
		Mockito.doThrow(new InvalidOperatingModeException()).when(xbeeDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		// Set the power level.
		xbeeDevice.setPowerLevel(POWER_LEVEL);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setPowerLevel(PowerLevel)}.
	 * 
	 * <p>Verify that the power level of an XBee device cannot be set if there is a timeout setting 
	 * the power level of the device.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testSetPowerLevelErrorTimeout() throws XBeeException, IOException {
		// Throw a timeout exception when trying to set the PL parameter.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).setParameter(Mockito.eq(PARAMETER_MY), Mockito.any(byte[].class));
		
		// Set the power level.
		xbeeDevice.setPowerLevel(POWER_LEVEL);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setPowerLevel(PowerLevel)}.
	 * 
	 * <p>Verify that the power level of an XBee device cannot be set if the answer when setting 
	 * the power level is null or the response status is not OK. It is, there is an AT command exception 
	 * setting the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testSetPowerLevelErrorInvalidAnswer() throws XBeeException, IOException {
		// Throw an AT command exception when trying to set the PL parameter.
		Mockito.doThrow(new ATCommandException(null)).when(xbeeDevice).setParameter(Mockito.eq(PARAMETER_MY), Mockito.any(byte[].class));
		
		// Set the power level.
		xbeeDevice.setPowerLevel(POWER_LEVEL);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setPowerLevel(PowerLevel)}.
	 * 
	 * <p>Verify that the power level of an XBee device can be set successfully.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testSetPowerLevelSuccess() throws XBeeException, IOException {
		// Do nothing when trying to set the PL parameter.
		Mockito.doNothing().when(xbeeDevice).setParameter(Mockito.eq(PARAMETER_MY), Mockito.any(byte[].class));
		
		// Set the power level.
		xbeeDevice.setPowerLevel(POWER_LEVEL);
	}
}
