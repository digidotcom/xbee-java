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

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.ATCommandException;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOValue;
import com.digi.xbee.api.models.ATCommand;
import com.digi.xbee.api.models.ATCommandResponse;
import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.models.OperatingMode;

public class SetDIOTest {
	
	// Variables.
	private SerialPortRxTx mockedPort;
	
	private XBeeDevice xbeeDevice;
	
	@Before
	public void setup() {
		// Mock an RxTx IConnectionInterface.
		mockedPort = Mockito.mock(SerialPortRxTx.class);
		Mockito.when(mockedPort.isOpen()).thenReturn(true);
		
		// Instantiate an XBeeDevice object with basic parameters.
		xbeeDevice = Mockito.spy(new XBeeDevice(mockedPort));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setDIOValue(IOLine, IOValue)}.
	 * 
	 * <p>Verify that DIO cannot be set if the connection is closed.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testSetDIOConnectionClosed() throws XBeeException {
		// When checking if the connection is open, return false.
		Mockito.when(mockedPort.isOpen()).thenReturn(false);
		
		// Set DIO0 line HIGH
		xbeeDevice.setDIOValue(IOLine.DIO0_AD0, IOValue.HIGH);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setDIOValue(IOLine, IOValue)}.
	 * 
	 * <p>Verify that DIO cannot be set if the IO line is null.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSetDIOWithValueNullIOLine() throws XBeeException {
		// Set a null IO line.
		xbeeDevice.setDIOValue(null, IOValue.HIGH);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setDIOValue(IOLine, IOValue)}.
	 * 
	 * <p>Verify that DIO cannot be set if the IO value is null.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSetDIOWithValueNullIOValue() throws XBeeException {
		// Set DIO0 line with a null value.
		xbeeDevice.setDIOValue(IOLine.DIO0_AD0, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setDIOValue(IOLine, IOValue)}.
	 * 
	 * <p>Verify that DIO cannot be set if the operating mode is AT.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSetDIOATOperatingMode() throws XBeeException {
		// Return AT operating mode when asked.
		Mockito.doReturn(OperatingMode.AT).when(xbeeDevice).getOperatingMode();
		
		// Set DIO0 line HIGH
		xbeeDevice.setDIOValue(IOLine.DIO0_AD0, IOValue.HIGH);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setDIOValue(IOLine, IOValue)}.
	 * 
	 * <p>Verify that DIO cannot be set if the operating mode is UNKNOWN.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSetDIOUnknownOperatingMode() throws XBeeException {
		// Return UNKNOWN operating mode when asked.
		Mockito.doReturn(OperatingMode.UNKNOWN).when(xbeeDevice).getOperatingMode();
		
		// Set DIO0 line HIGH
		xbeeDevice.setDIOValue(IOLine.DIO0_AD0, IOValue.HIGH);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setDIOValue(IOLine, IOValue)}.
	 * 
	 * <p>Verify that DIO cannot be set if the status value after sending the set 
	 * command is INVALID_PARAMETER.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testSetDIOValueInvalidParameterStatusResponse() throws XBeeException, IOException {
		// Generate an ATCommandResponse with error status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.INVALID_PARAMETER);
		
		Mockito.doReturn(mockedResponse).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Set DIO0 line HIGH
		xbeeDevice.setDIOValue(IOLine.DIO0_AD0, IOValue.HIGH);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setDIOValue(IOLine, IOValue)}.
	 * 
	 * <p>Verify that DIO cannot be set if the response value after sending the set command 
	 * is null.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testSetDIOValueNullResponse() throws XBeeException, IOException {
		// Return a null ATCommandResponse when sending any AT Command.
		Mockito.doReturn(null).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Set DIO0 line HIGH
		xbeeDevice.setDIOValue(IOLine.DIO0_AD0, IOValue.HIGH);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setDIOValue(IOLine, IOValue)}.
	 * 
	 * <p>Verify that DIO cannot be set if the set command was not processed successfully 
	 * due to a timeout sending the command.</p>
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testSetDIOTimeout() throws XBeeException, IOException {
		// Throw a timeout exception when trying to send any AT Command.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Set DIO0 line HIGH
		xbeeDevice.setDIOValue(IOLine.DIO0_AD0, IOValue.HIGH);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setDIOValue(IOLine, IOValue)}.
	 * 
	 * <p>Verify that DIO can be set successfully.</p>
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test
	public void testSetDIOSuccess() throws XBeeException, IOException {
		// Generate an ATCommandResponse with OK status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.OK);
		
		Mockito.doReturn(mockedResponse).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Set DIO0 line HIGH
		xbeeDevice.setDIOValue(IOLine.DIO0_AD0, IOValue.HIGH);
	}
}
