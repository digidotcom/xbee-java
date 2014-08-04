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

import java.io.IOException;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.OperationNotSupportedException;
import com.digi.xbee.api.exceptions.TimeoutException;
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
	
	/**
	 * Verify that DIO cannot be set if the connection is closed.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testSetDIOConnectionClosed() throws XBeeException, IOException {
		// When checking if the connection is open, return false.
		Mockito.when(mockedPort.isOpen()).thenReturn(false);
		
		// Set DIO0 line HIGH
		xbeeDevice.setDIOValue(IOLine.DIO0_AD0, IOValue.HIGH);
	}
	
	/**
	 * Verify that DIO cannot be set if the IO line is null.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSetDIOWithValueNullIOLine() throws XBeeException, IOException {
		// Set a null IO line.
		xbeeDevice.setDIOValue(null, IOValue.HIGH);
	}
	
	/**
	 * Verify that DIO cannot be set if the IO value is null.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSetDIOWithValueNullIOValue() throws XBeeException, IOException {
		// Set DIO0 line with a null value.
		xbeeDevice.setDIOValue(IOLine.DIO0_AD0, null);
	}
	
	/**
	 * Verify that DIO cannot be set if the operating mode is AT.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSetDIOATOperatingMode() throws XBeeException, IOException {
		// Return AT operating mode when asked.
		Mockito.doReturn(OperatingMode.AT).when(xbeeDevice).getOperatingMode();
		
		// Set DIO0 line HIGH
		xbeeDevice.setDIOValue(IOLine.DIO0_AD0, IOValue.HIGH);
	}
	
	/**
	 * Verify that DIO cannot be set if the operating mode is UNKNOWN.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSetDIOUnknownOperatingMode() throws XBeeException, IOException {
		// Return UNKNOWN operating mode when asked.
		Mockito.doReturn(OperatingMode.UNKNOWN).when(xbeeDevice).getOperatingMode();
		
		// Set DIO0 line HIGH
		xbeeDevice.setDIOValue(IOLine.DIO0_AD0, IOValue.HIGH);
	}
	
	/**
	 * Verify that DIO cannot be set if the status value after sending the set 
	 * command is INVALID_PARAMETER.
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testSetDIOValueInvalidParameterStatusResponse() throws XBeeException, IOException {
		// Generate an ATCommandResponse with error status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.INVALID_PARAMETER);
		
		Mockito.doReturn(mockedResponse).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Set DIO0 line HIGH
		xbeeDevice.setDIOValue(IOLine.DIO0_AD0, IOValue.HIGH);
	}
	
	/**
	 * Verify that DIO cannot be set if the response value after sending the set command 
	 * is null.
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testSetDIOValueNullResponse() throws XBeeException, IOException {
		// Return a null ATCommandResponse when sending any AT Command.
		Mockito.doReturn(null).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Set DIO0 line HIGH
		xbeeDevice.setDIOValue(IOLine.DIO0_AD0, IOValue.HIGH);
	}
	
	/**
	 * Verify that DIO cannot be set if the set command was not processed successfully 
	 * due to a timeout sending the command.
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
	 * Verify that DIO can be set successfully.
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
	
	
	/**
	 * Verify that DIO value cannot be read if the connection is closed.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testGetDIOValueConnectionClosed() throws XBeeException, IOException {
		// When checking if the connection is open, return false.
		Mockito.when(mockedPort.isOpen()).thenReturn(false);
		
		// Read the value of the DIO0 line.
		xbeeDevice.getDIOValue(IOLine.DIO0_AD0);
	}
	
	/**
	 * Verify that DIO value cannot be read if the IO line is null.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=NullPointerException.class)
	public void testGetDIOWithNullIOLine() throws XBeeException, IOException {
		// Read the value of a null IO line.
		xbeeDevice.getDIOValue(null);
	}
	
	/**
	 * Verify that DIO value cannot be read if the operating mode is AT.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testGetDIOValueATOperatingMode() throws XBeeException, IOException {
		// Return AT operating mode when asked.
		Mockito.doReturn(OperatingMode.AT).when(xbeeDevice).getOperatingMode();
		
		// Read the value of the DIO0 line.
		xbeeDevice.getDIOValue(IOLine.DIO0_AD0);
	}
	
	/**
	 * Verify that DIO value cannot be read if the operating mode is UNKNOWN.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testGetDIOValueUnknownOperatingMode() throws XBeeException, IOException {
		// Return UNKNOWN operating mode when asked.
		Mockito.doReturn(OperatingMode.UNKNOWN).when(xbeeDevice).getOperatingMode();
		
		// Read the value of the DIO0 line.
		xbeeDevice.getDIOValue(IOLine.DIO0_AD0);
	}
	
	/**
	 * Verify that DIO value cannot be read if the status value after sending the get 
	 * command is INVALID_PARAMETER.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testGetDIOValueInvalidParameterStatusResponse() throws XBeeException, IOException {
		// Generate an ATCommandResponse with error status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.INVALID_PARAMETER);
		
		Mockito.doReturn(mockedResponse).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Read the value of the DIO0 line.
		xbeeDevice.getDIOValue(IOLine.DIO0_AD0);
	}
	
	/**
	 * Verify that DIO value cannot be read if the response value after sending the get command 
	 * is null.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testGetDIOValueNullResponse() throws XBeeException, IOException {
		// Return a null ATCommandResponse when sending any AT Command.
		Mockito.doReturn(null).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Read the value of the DIO0 line.
		xbeeDevice.getDIOValue(IOLine.DIO0_AD0);
	}
	
	/**
	 * Verify that DIO value cannot be read if the response value after sending the get command 
	 * does not contain digital values.
	 * 
	 * @throws Exception 
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testGetDIOValueNoDigitalValuesResponse() throws Exception {
		// Generate an ATCommandResponse with OK status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.OK);
		Mockito.when(mockedResponse.getResponse()).thenReturn(new byte[5]);
		
		Mockito.doReturn(mockedResponse).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Mock an IO sample. It will return false when asked if it contains digital values.
		IOSample mockedIOSample = Mockito.mock(IOSample.class);
		Mockito.when(mockedIOSample.hasDigitalValues()).thenReturn(false);
		// Whenever an IOSample class is instantiated, the mockedIOSample should be returned.
		PowerMockito.whenNew(IOSample.class).withAnyArguments().thenReturn(mockedIOSample);
		
		// Read the value of the DIO0 line.
		xbeeDevice.getDIOValue(IOLine.DIO0_AD0);
	}
	
	/**
	 * Verify that DIO value cannot be read if the get value command was not processed 
	 * successfully due to a timeout sending the get value command.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testGetDIOValueTimeout() throws XBeeException, IOException {
		// Throw a timeout exception when trying to send any AT Command.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Read the value of the DIO0 line.
		xbeeDevice.getDIOValue(IOLine.DIO0_AD0);
	}
	
	/**
	 * Verify that DIO value can be read successfully.
	 * 
	 * @throws Exception
	 */
	@Test
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
		IOValue ioValue = xbeeDevice.getDIOValue(IOLine.DIO0_AD0);
		
		// Verify the read value is correct.
		assertEquals(IOValue.HIGH, ioValue);
	}
}
