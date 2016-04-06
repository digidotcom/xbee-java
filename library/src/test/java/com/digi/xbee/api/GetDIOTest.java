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

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.OperationNotSupportedException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.io.IOValue;
import com.digi.xbee.api.models.ATCommand;
import com.digi.xbee.api.models.ATCommandResponse;
import com.digi.xbee.api.models.ATCommandStatus;

@RunWith(PowerMockRunner.class)
@PrepareForTest({XBeeDevice.class})
public class GetDIOTest {

	// Constants.
	private final static String METHOD_READ_IO_SAMPLE = "readIOSample";
	
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
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getDIOValue(IOLine)}.
	 * 
	 * <p>Verify that DIO value cannot be read if the IO line is null.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=NullPointerException.class)
	public void testGetDIOWithNullIOLine() throws XBeeException {
		// Read the value of a null IO line.
		xbeeDevice.getDIOValue(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getDIOValue(IOLine)}.
	 * 
	 * <p>Verify that DIO value cannot be read if the IOSample retrieved from the XBee device 
	 * does not contain digital values.</p>
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
		
		// When the 'readIOSample()' method of the XBeeDevice is called, return the mocked IOSample.
		PowerMockito.doReturn(mockedIOSample).when(xbeeDevice, METHOD_READ_IO_SAMPLE);
		
		// Read the value of the DIO0 line.
		xbeeDevice.getDIOValue(IOLine.DIO0_AD0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getDIOValue(IOLine)}.
	 * 
	 * <p>Verify that DIO value cannot be read if the IOSample retrieved from the XBee device 
	 * does not contain a digital value for the specified IOLine.</p>
	 * 
	 * @throws Exception 
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testGetDIOValueNoDigitalValueForIOLineResponse() throws Exception {
		// Generate an ATCommandResponse with OK status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.OK);
		Mockito.when(mockedResponse.getResponse()).thenReturn(new byte[5]);
		
		Mockito.doReturn(mockedResponse).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Mock an IO sample. It will not contain a digital value for the requested IOLine.
		IOSample mockedIOSample = Mockito.mock(IOSample.class);
		Mockito.when(mockedIOSample.hasDigitalValues()).thenReturn(true);
		Mockito.when(mockedIOSample.getDigitalValues()).thenReturn(new HashMap<IOLine, IOValue>());
		
		// When the 'readIOSample()' method of the XBeeDevice is called, return the mocked IOSample.
		PowerMockito.doReturn(mockedIOSample).when(xbeeDevice, METHOD_READ_IO_SAMPLE);
		
		// Read the value of the DIO0 line.
		xbeeDevice.getDIOValue(IOLine.DIO0_AD0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getDIOValue(IOLine)}.
	 * 
	 * <p>Verify that DIO value can be read successfully.</p>
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
		
		// When the 'readIOSample()' method of the XBeeDevice is called, return the mocked IOSample.
		PowerMockito.doReturn(mockedIOSample).when(xbeeDevice, METHOD_READ_IO_SAMPLE);
		
		// Read the value of the DIO0 line.
		IOValue ioValue = xbeeDevice.getDIOValue(IOLine.DIO0_AD0);
		
		// Verify the read value is correct.
		assertEquals(IOValue.HIGH, ioValue);
	}
}
