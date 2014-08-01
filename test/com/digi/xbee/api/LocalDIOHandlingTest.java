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
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.io.IOValue;
import com.digi.xbee.api.models.ATCommand;
import com.digi.xbee.api.models.ATCommandResponse;
import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.models.OperatingMode;

@RunWith(PowerMockRunner.class)
@PrepareForTest({XBeeDevice.class, IOSample.class})
public class LocalDIOHandlingTest {
	
	// Variables.
	private SerialPortRxTx mockedPort;
	
	private XBeeDevice xbeeDevice;
	
	@Before
	public void setup() {
		// Mock an RxTx IConnectionInterface.
		mockedPort = Mockito.mock(SerialPortRxTx.class);
		Mockito.when(mockedPort.isOpen()).thenReturn(true);
		
		// Instantiate an XBeeDevice object with basic parameters.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(mockedPort));
	}
	
	@Test
	/**
	 * Verify that DIO cannot be set if the connection is closed.
	 */
	public void testSetDIOConnectionClosed() {
		// When checking if the connection is open, return false.
		Mockito.when(mockedPort.isOpen()).thenReturn(false);
		
		// Set DIO0 line HIGH
		try {
			xbeeDevice.setDIOValue(IOLine.DIO0_AD0, IOValue.HIGH);
			fail("DIO shouldn't have been set.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			assertEquals(XBeeException.CONNECTION_NOT_OPEN, e.getErrorCode());
		}
	}
	
	@Test
	/**
	 * Verify that DIO cannot be set if the IO line or value are not 
	 * valid.
	 */
	public void testSetDIOInvalidParameters() {
		// Set a null IO line.
		try {
			xbeeDevice.setDIOValue(null, IOValue.HIGH);
			fail("DIO shouldn't have been set.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
		
		// Set DIO0 line with a null value.
		try {
			xbeeDevice.setDIOValue(IOLine.DIO0_AD0, null);
			fail("DIO shouldn't have been set.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
	}
	
	@Test
	/**
	 * Verify that DIO cannot be set if the operating mode is not API or 
	 * API Escaped.
	 */
	public void testSetDIOInvalidOperatingMode() {
		// Return AT operating mode when asked.
		Mockito.doReturn(OperatingMode.AT).when(xbeeDevice).getOperatingMode();
		
		// Set DIO0 line HIGH
		try {
			xbeeDevice.setDIOValue(IOLine.DIO0_AD0, IOValue.HIGH);
			fail("DIO shouldn't have been set.");
		} catch (Exception e) {
			assertEquals(InvalidOperatingModeException.class, e.getClass());
		}
		
		// Return UNKNOWN operating mode when asked.
		Mockito.doReturn(OperatingMode.UNKNOWN).when(xbeeDevice).getOperatingMode();
		
		// Set DIO0 line HIGH
		try {
			xbeeDevice.setDIOValue(IOLine.DIO0_AD0, IOValue.HIGH);
			fail("DIO shouldn't have been set.");
		} catch (Exception e) {
			assertEquals(InvalidOperatingModeException.class, e.getClass());
		}
	}
	
	@Test
	/**
	 * Verify that DIO cannot be set if the set command was not processed successfully 
	 * (null answer or error in the answer).
	 * 
	 * @throws Exception
	 */
	public void testSetDIOOperationNotSupported() throws Exception {
		// Generate an ATCommandResponse with error status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.INVALID_PARAMETER);
		
		Mockito.doReturn(mockedResponse).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Set DIO0 line HIGH
		try {
			xbeeDevice.setDIOValue(IOLine.DIO0_AD0, IOValue.HIGH);
			fail("DIO shouldn't have been set.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			assertEquals(XBeeException.INVALID_OPERATION, e.getErrorCode());
		}
		
		// Now try returning a null ATCommandResponse when sending any AT Command.
		Mockito.doReturn(null).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Set DIO0 line HIGH
		try {
			xbeeDevice.setDIOValue(IOLine.DIO0_AD0, IOValue.HIGH);
			fail("DIO shouldn't have been set.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			assertEquals(XBeeException.INVALID_OPERATION, e.getErrorCode());
		}
	}
	
	@Test
	/**
	 * Verify that DIO cannot be set if the set command was not processed successfully 
	 * due to a timeout sending the command.
	 * 
	 * @throws Exception
	 */
	public void testSetDIOTimeout() throws Exception {
		// Throw a timeout exception when trying to send any AT Command.
		Mockito.doThrow(new XBeeException(XBeeException.CONNECTION_TIMEOUT)).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Set DIO0 line HIGH
		try {
			xbeeDevice.setDIOValue(IOLine.DIO0_AD0, IOValue.HIGH);
			fail("DIO shouldn't have been set.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			assertEquals(XBeeException.CONNECTION_TIMEOUT, e.getErrorCode());
		}
	}
	
	@Test
	/**
	 * Verify that DIO can be set successfully.
	 * 
	 * @throws Exception
	 */
	public void testSetDIOSuccess() throws Exception {
		// Generate an ATCommandResponse with OK status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.OK);
		
		Mockito.doReturn(mockedResponse).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Set DIO0 line HIGH
		try {
			xbeeDevice.setDIOValue(IOLine.DIO0_AD0, IOValue.HIGH);
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			fail("This exception shouldn't be thrown now.");
		}
	}
	
	@Test
	/**
	 * Verify that DIO value cannot be read if the connection is closed.
	 */
	public void testGetDIOValueConnectionClosed() {
		// When checking if the connection is open, return false.
		Mockito.when(mockedPort.isOpen()).thenReturn(false);
		
		// Read the value of the DIO0 line.
		try {
			xbeeDevice.getDIOValue(IOLine.DIO0_AD0);
			fail("DIO value shouldn't have been retrieved.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			assertEquals(XBeeException.CONNECTION_NOT_OPEN, e.getErrorCode());
		}
	}
	
	@Test
	/**
	 * Verify that DIO value cannot be read if the IO line is not valid.
	 */
	public void testGetDIOValueInvalidParameters() {
		// Read the value of a null IO line.
		try {
			xbeeDevice.getDIOValue(null);
			fail("DIO value shouldn't have been retrieved.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
	}
	
	@Test
	/**
	 * Verify that DIO value cannot be read if the operating mode is not API or 
	 * API Escaped.
	 */
	public void testGetDIOValueInvalidOperatingMode() {
		// Return AT operating mode when asked.
		Mockito.doReturn(OperatingMode.AT).when(xbeeDevice).getOperatingMode();
		
		// Read the value of the DIO0 line.
		try {
			xbeeDevice.getDIOValue(IOLine.DIO0_AD0);
			fail("DIO value shouldn't have been retrieved.");
		} catch (Exception e) {
			assertEquals(InvalidOperatingModeException.class, e.getClass());
		}
		
		// Return UNKNOWN operating mode when asked.
		Mockito.doReturn(OperatingMode.UNKNOWN).when(xbeeDevice).getOperatingMode();
		
		// Read the value of the DIO0 line.
		try {
			xbeeDevice.getDIOValue(IOLine.DIO0_AD0);
			fail("DIO value shouldn't have been retrieved.");
		} catch (Exception e) {
			assertEquals(InvalidOperatingModeException.class, e.getClass());
		}
	}
	
	@Test
	/**
	 * Verify that DIO value cannot be read if the get value command was not processed successfully 
	 * (null answer or error in the answer).
	 * 
	 * @throws Exception
	 */
	public void testGetDIOValueOperationNotSupported() throws Exception {
		// Generate an ATCommandResponse with error status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.INVALID_PARAMETER);
		
		Mockito.doReturn(mockedResponse).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Read the value of the DIO0 line.
		try {
			xbeeDevice.getDIOValue(IOLine.DIO0_AD0);
			fail("DIO value shouldn't have been retrieved.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			assertEquals(XBeeException.INVALID_OPERATION, e.getErrorCode());
		}
		
		// Now try returning a null ATCommandResponse when sending any AT Command.
		Mockito.doReturn(null).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Read the value of the DIO0 line.
		try {
			xbeeDevice.getDIOValue(IOLine.DIO0_AD0);
			fail("DIO value shouldn't have been retrieved.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			assertEquals(XBeeException.INVALID_OPERATION, e.getErrorCode());
		}
		
		// Now try with a valid response status (OK).
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.OK);
		Mockito.when(mockedResponse.getResponse()).thenReturn(new byte[5]);
		
		// Mock an IO sample. It will return false when asked if it contains digital values.
		IOSample mockedIOSample = Mockito.mock(IOSample.class);
		Mockito.when(mockedIOSample.hasDigitalValues()).thenReturn(false);
		// Whenever an IOSample class is instantiated, the mockedIOSample should be returned.
		PowerMockito.whenNew(IOSample.class).withAnyArguments().thenReturn(mockedIOSample);
		
		// Read the value of the DIO0 line.
		try {
			xbeeDevice.getDIOValue(IOLine.DIO0_AD0);
			fail("DIO value shouldn't have been retrieved.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			assertEquals(XBeeException.INVALID_OPERATION, e.getErrorCode());
		}
	}
	
	@Test
	/**
	 * Verify that DIO value cannot be read if the get value command was not processed 
	 * successfully due to a timeout sending the get value command.
	 * 
	 * @throws Exception
	 */
	public void testGetDIOValueTimeout() throws Exception {
		// Throw a timeout exception when trying to send any AT Command.
		Mockito.doThrow(new XBeeException(XBeeException.CONNECTION_TIMEOUT)).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Read the value of the DIO0 line.
		try {
			xbeeDevice.getDIOValue(IOLine.DIO0_AD0);
			fail("DIO value shouldn't have been retrieved.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			assertEquals(XBeeException.CONNECTION_TIMEOUT, e.getErrorCode());
		}
	}
	
	@Test
	/**
	 * Verify that DIO value can be read successfully.
	 * 
	 * @throws Exception
	 */
	public void testGetDIOValueSuccess() throws Exception {
		// Generate an ATCommandResponse with OK status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.OK);
		Mockito.when(mockedResponse.getResponse()).thenReturn(new byte[1]);
		
		Mockito.doReturn(mockedResponse).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Mock the map of digital values that will be returned by the mocked IO sample. The map will 
		// just contain a HIGH for the DIO0.
		HashMap<IOLine, IOValue> dioValues = new HashMap<IOLine, IOValue>();
		dioValues.put(IOLine.DIO0_AD0, IOValue.HIGH);
		// Mock an IO sample. It will return the mocked map of digital values when asked.
		IOSample mockedIOSample = Mockito.mock(IOSample.class);
		Mockito.when(mockedIOSample.hasDigitalValues()).thenReturn(true);
		Mockito.when(mockedIOSample.getDigitalValues()).thenReturn(dioValues);
		// Whenever an IOSample class is instantiated, the mockedIOSample should be returned.
		PowerMockito.whenNew(IOSample.class).withAnyArguments().thenReturn(mockedIOSample);
		
		// Read the value of the DIO0 line.
		IOValue ioValue = null;
		try {
			ioValue = xbeeDevice.getDIOValue(IOLine.DIO0_AD0);
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			fail("This exception shouldn't be thrown now.");
		}
		
		// Verify the read value is correct.
		assertEquals(IOValue.HIGH, ioValue);
	}
}
