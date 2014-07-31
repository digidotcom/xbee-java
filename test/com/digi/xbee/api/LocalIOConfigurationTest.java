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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOMode;
import com.digi.xbee.api.models.ATCommand;
import com.digi.xbee.api.models.ATCommandResponse;
import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.models.OperatingMode;

public class LocalIOConfigurationTest {
	
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
	
	@Test
	/**
	 * Verify that IO cannot be configured if the connection is closed.
	 */
	public void testConfigureIOConnectionClosed() {
		// When checking if the connection is open, return false.
		Mockito.when(mockedPort.isOpen()).thenReturn(false);
		
		// Configure DIO0 line as digital output.
		try {
			xbeeDevice.setIOConfiguration(IOLine.DIO0_AD0, IOMode.DIGITAL_OUT_HIGH);
			fail("IO shouldn't have been configured.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			assertEquals(XBeeException.CONNECTION_NOT_OPEN, e.getErrorCode());
		}
	}
	
	@Test
	/**
	 * Verify that IO cannot be configured if the IO line or mode are not 
	 * valid.
	 */
	public void testConfigureIOInvalidParameters() {
		// Configure a null IO line.
		try {
			xbeeDevice.setIOConfiguration(null, IOMode.DIGITAL_OUT_HIGH);
			fail("IO shouldn't have been configured.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
		
		// Configure DIO0 line with a null mode.
		try {
			xbeeDevice.setIOConfiguration(IOLine.DIO0_AD0, null);
			fail("IO shouldn't have been configured.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
	}
	
	@Test
	/**
	 * Verify that IO cannot be configured if the operating mode is not API or 
	 * API Escaped.
	 */
	public void testConfigureIOInvalidOperatingMode() {
		// Return AT operating mode when asked.
		Mockito.doReturn(OperatingMode.AT).when(xbeeDevice).getOperatingMode();
		
		// Configure DIO0 line as digital output.
		try {
			xbeeDevice.setIOConfiguration(IOLine.DIO0_AD0, IOMode.DIGITAL_OUT_HIGH);
			fail("IO shouldn't have been configured.");
		} catch (Exception e) {
			assertEquals(InvalidOperatingModeException.class, e.getClass());
		}
		
		// Return UNKNOWN operating mode when asked.
		Mockito.doReturn(OperatingMode.UNKNOWN).when(xbeeDevice).getOperatingMode();
		
		// Configure DIO0 line as digital output.
		try {
			xbeeDevice.setIOConfiguration(IOLine.DIO0_AD0, IOMode.DIGITAL_OUT_HIGH);
			fail("IO shouldn't have been configured.");
		} catch (Exception e) {
			assertEquals(InvalidOperatingModeException.class, e.getClass());
		}
	}
	
	@Test
	/**
	 * Verify that IO cannot be configured if the configuration command was not processed 
	 * successfully (null answer or error in the answer).
	 * 
	 * @throws Exception
	 */
	public void testConfigureIOOperationNotSupported() throws Exception {
		// Generate an ATCommandResponse with error status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.INVALID_PARAMETER);
		
		Mockito.doReturn(mockedResponse).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Configure DIO0 line as PWM.
		try {
			xbeeDevice.setIOConfiguration(IOLine.DIO0_AD0, IOMode.PWM);
			fail("IO shouldn't have been configured.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			assertEquals(XBeeException.INVALID_OPERATION, e.getErrorCode());
		}
		
		// Now try returning a null ATCommandResponse when sending any AT Command.
		Mockito.doReturn(null).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Configure DIO0 line as PWM.
		try {
			xbeeDevice.setIOConfiguration(IOLine.DIO0_AD0, IOMode.PWM);
			fail("IO shouldn't have been configured.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			assertEquals(XBeeException.INVALID_OPERATION, e.getErrorCode());
		}
	}
	
	@Test
	/**
	 * Verify that IO cannot be configured if the configuration command was not processed 
	 * successfully due to a timeout sending the command.
	 * 
	 * @throws Exception
	 */
	public void testConfigureIOTimeout() throws Exception {
		// Throw a timeout exception when trying to send any AT Command.
		Mockito.doThrow(new XBeeException(XBeeException.CONNECTION_TIMEOUT)).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Configure DIO0 line as PWM.
		try {
			xbeeDevice.setIOConfiguration(IOLine.DIO0_AD0, IOMode.PWM);
			fail("IO shouldn't have been configured.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			assertEquals(XBeeException.CONNECTION_TIMEOUT, e.getErrorCode());
		}
	}
	
	@Test
	/**
	 * Verify that IO configuration cannot be read if the connection is closed.
	 */
	public void testGetIOConfigurationConnectionClosed() {
		// When checking if the connection is open, return false.
		Mockito.when(mockedPort.isOpen()).thenReturn(false);
		
		// Read the configuration of the DIO0 line.
		try {
			xbeeDevice.getIOConfiguration(IOLine.DIO0_AD0);
			fail("IO configuration shouldn't have been retrieved.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			assertEquals(XBeeException.CONNECTION_NOT_OPEN, e.getErrorCode());
		}
	}
	
	@Test
	/**
	 * Verify that IO configuration cannot be read if the IO line is not valid.
	 */
	public void testGetIOConfigurationInvalidParameters() {
		// Read the configuration of a null IO line.
		try {
			xbeeDevice.getIOConfiguration(null);
			fail("IO configuration shouldn't have been retrieved.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
	}
	
	@Test
	/**
	 * Verify that IO configuration cannot be read if the operating mode is not API or 
	 * API Escaped.
	 */
	public void testGetIOConfigurationInvalidOperatingMode() {
		// Return AT operating mode when asked.
		Mockito.doReturn(OperatingMode.AT).when(xbeeDevice).getOperatingMode();
		
		// Read the configuration of the DIO0 line.
		try {
			xbeeDevice.getIOConfiguration(IOLine.DIO0_AD0);
			fail("IO configuration shouldn't have been retrieved.");
		} catch (Exception e) {
			assertEquals(InvalidOperatingModeException.class, e.getClass());
		}
		
		// Return UNKNOWN operating mode when asked.
		Mockito.doReturn(OperatingMode.UNKNOWN).when(xbeeDevice).getOperatingMode();
		
		// Read the configuration of the DIO0 line.
		try {
			xbeeDevice.getIOConfiguration(IOLine.DIO0_AD0);
			fail("IO configuration shouldn't have been retrieved.");
		} catch (Exception e) {
			assertEquals(InvalidOperatingModeException.class, e.getClass());
		}
	}
	
	@Test
	/**
	 * Verify that IO configuration cannot be read if the get configuration command was not processed 
	 * successfully (null answer or error in the answer).
	 * 
	 * @throws Exception
	 */
	public void testGetIOConfigurationOperationNotSupported() throws Exception {
		// Generate an ATCommandResponse with error status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.INVALID_PARAMETER);
		
		Mockito.doReturn(mockedResponse).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Read the configuration of the DIO0 line.
		try {
			xbeeDevice.getIOConfiguration(IOLine.DIO0_AD0);
			fail("IO configuration shouldn't have been retrieved.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			assertEquals(XBeeException.INVALID_OPERATION, e.getErrorCode());
		}
		
		// Now try returning a null ATCommandResponse when sending any AT Command.
		Mockito.doReturn(null).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Read the configuration of the DIO0 line.
		try {
			xbeeDevice.getIOConfiguration(IOLine.DIO0_AD0);
			fail("IO configuration shouldn't have been retrieved.");
		} catch (XBeeException e) {
			assertEquals(XBeeException.INVALID_OPERATION, e.getErrorCode());
		}
		
		// Now try with a valid response status (OK) but with a configuration ID not supported 
		// (not contained in the IOMode enumerator).
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.OK);
		Mockito.when(mockedResponse.getResponse()).thenReturn(new byte[]{(byte) 0xFF});
		
		// Read the configuration of the DIO0 line.
		try {
			xbeeDevice.getIOConfiguration(IOLine.DIO0_AD0);
			fail("IO configuration shouldn't have been retrieved.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			assertEquals(XBeeException.INVALID_OPERATION, e.getErrorCode());
		}
	}
	
	@Test
	/**
	 * Verify that IO configuration cannot be read if the get configuration command was not processed 
	 * successfully due to a timeout sending the get configuration command.
	 * 
	 * @throws Exception
	 */
	public void testGetIOConfigurationTimeout() throws Exception {
		// Throw a timeout exception when trying to send any AT Command.
		Mockito.doThrow(new XBeeException(XBeeException.CONNECTION_TIMEOUT)).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Read the configuration of the DIO0 line.
		try {
			xbeeDevice.getIOConfiguration(IOLine.DIO0_AD0);
			fail("IO configuration shouldn't have been retrieved.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			assertEquals(XBeeException.CONNECTION_TIMEOUT, e.getErrorCode());
		}
	}
	
	@Test
	/**
	 * Verify that IO configuration can be read successfully.
	 * @throws Exception
	 */
	public void testGetIOConfigurationSuccess() throws Exception {
		// Generate an ATCommandResponse with OK status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.OK);
		// The value of the AT command will be 5, which is the DIGITAL_OUT_HIGH mode.
		Mockito.when(mockedResponse.getResponse()).thenReturn(new byte[]{5});
		
		Mockito.doReturn(mockedResponse).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Read the configuration of the DIO0 line.
		IOMode ioMode = null;
		try {
			ioMode = xbeeDevice.getIOConfiguration(IOLine.DIO0_AD0);
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			fail("This exception shouldn't be thrown now.");
		}
		
		// Verify the read mode is correct.
		assertEquals(IOMode.DIGITAL_OUT_HIGH, ioMode);
	}
}
