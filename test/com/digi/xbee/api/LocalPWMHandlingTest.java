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

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.OperationNotSupportedException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.models.ATCommand;
import com.digi.xbee.api.models.ATCommandResponse;
import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.models.OperatingMode;

public class LocalPWMHandlingTest {
	
	// Constants.
	private static final int DUTY_CYCLE_VALID_VALUE = 75;
	private static final int DUTY_CYCLE_NEGATIVE_VALUE = -10;
	private static final int DUTY_CYCLE_OVER_100_VALUE = 150;
	
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
	 * Verify that PWM duty cycle cannot be set if the connection is closed.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testSetDutyCycleConnectionClosed() throws XBeeException, IOException {
		// When checking if the connection is open, return false.
		Mockito.when(mockedPort.isOpen()).thenReturn(false);
		
		// Set duty cycle of PWM0 to 75%.
		xbeeDevice.setPWMDutyCycle(IOLine.DIO10_PWM0, DUTY_CYCLE_VALID_VALUE);
	}
	
	/**
	 * Verify that PWM duty cycle cannot be set if the IO line is null.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSetDutyCycleOfNullIOLine() throws XBeeException, IOException {
		// Set the duty cycle of a null IO line.
		xbeeDevice.setPWMDutyCycle(null, DUTY_CYCLE_VALID_VALUE);
	}
	
	/**
	 * Verify that PWM duty cycle cannot be set if the value is under 0%.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetDutyCycleWithNegativePercentace() throws XBeeException, IOException {
		// Set duty cycle of PWM0 with a negative value.
		xbeeDevice.setPWMDutyCycle(IOLine.DIO10_PWM0, DUTY_CYCLE_NEGATIVE_VALUE);
	}
	
	/**
	 * Verify that PWM duty cycle cannot be set if the value is over 100%.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetDutyCycleWithPercentageOver100() throws XBeeException, IOException {
		// Set duty cycle of PWM0 with a value over 100%.
		xbeeDevice.setPWMDutyCycle(IOLine.DIO10_PWM0, DUTY_CYCLE_OVER_100_VALUE);
	}
	
	/**
	 * Verify that PWM duty cycle cannot be set if the IO line does not have 
	 * PWM capabilities.
	 */
	@Test
	public void testSetDutyCycleOfIOLineWithoutPWMCapabilities() {
		// Set the duty cycle of IO lines without PWM capabilities.
		for (IOLine ioLine:IOLine.values()) {
			if (ioLine == IOLine.DIO10_PWM0 || ioLine == IOLine.DIO11_PWM1)
				continue;
			// Set duty cycle of ioLine to 75%.
			try {
				xbeeDevice.setPWMDutyCycle(ioLine, DUTY_CYCLE_VALID_VALUE);
				fail("Duty cycle shouldn't have been set.");
			} catch (Exception e) {
				assertEquals(IllegalArgumentException.class, e.getClass());
			}
		}
	}
	
	/**
	 * Verify that PWM duty cycle cannot be set if the operating mode is AT.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSetDutyCycleATOperatingMode() throws XBeeException, IOException {
		// Return AT operating mode when asked.
		Mockito.doReturn(OperatingMode.AT).when(xbeeDevice).getOperatingMode();
		
		// Set duty cycle of PWM0 to 75%.
		xbeeDevice.setPWMDutyCycle(IOLine.DIO10_PWM0, DUTY_CYCLE_VALID_VALUE);
	}
	
	/**
	 * Verify that PWM duty cycle cannot be set if the operating mode is UNKNOWN.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSetDutyCycleUnknownOperatingMode() throws XBeeException, IOException {
		// Return UNKNOWN operating mode when asked.
		Mockito.doReturn(OperatingMode.UNKNOWN).when(xbeeDevice).getOperatingMode();
		
		// Set duty cycle of PWM0 to 75%.
		xbeeDevice.setPWMDutyCycle(IOLine.DIO10_PWM0, DUTY_CYCLE_VALID_VALUE);
	}
	
	/**
	 * Verify that PWM duty cycle cannot be set if the status value after sending the set 
	 * command is INVALID_PARAMETER.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testSetDutyCycleInvalidParameterStatusResponse() throws XBeeException, IOException {
		// Generate an ATCommandResponse with error status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.INVALID_PARAMETER);
		
		Mockito.doReturn(mockedResponse).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Set duty cycle of PWM0 to 75%.
		xbeeDevice.setPWMDutyCycle(IOLine.DIO10_PWM0, DUTY_CYCLE_VALID_VALUE);
	}
	
	/**
	 * Verify that PWM duty cycle cannot be set if the response value after sending the set command 
	 * is null.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testSetDutyCycleNullResponse() throws XBeeException, IOException {
		// Return a null ATCommandResponse when sending any AT Command.
		Mockito.doReturn(null).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Set duty cycle of PWM0 to 75%.
		xbeeDevice.setPWMDutyCycle(IOLine.DIO10_PWM0, DUTY_CYCLE_VALID_VALUE);
	}
	
	/**
	 * Verify that PWM duty cycle cannot be set if the set command was not processed successfully 
	 * due to a timeout sending the command.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=XBeeException.class)
	public void testSetDutyCycleTimeout() throws XBeeException, IOException {
		// Throw a timeout exception when trying to send any AT Command.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Set duty cycle of PWM0 to 75%.
		xbeeDevice.setPWMDutyCycle(IOLine.DIO10_PWM0, DUTY_CYCLE_VALID_VALUE);
	}
	
	/**
	 * Verify that PWM duty cycle can be set successfully.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test
	public void testSetDutyCycleSuccess() throws XBeeException, IOException {
		// Generate an ATCommandResponse with OK status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.OK);
		
		Mockito.doReturn(mockedResponse).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Set duty cycle of PWM0 to 75%.
		xbeeDevice.setPWMDutyCycle(IOLine.DIO10_PWM0, DUTY_CYCLE_VALID_VALUE);
	}
	
	/**
	 * Verify that PWM duty cycle value cannot be read if the connection is closed.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testGetDutyCycleValueConnectionClosed() throws XBeeException, IOException {
		// When checking if the connection is open, return false.
		Mockito.when(mockedPort.isOpen()).thenReturn(false);
		
		// Read the duty cycle value of the PWM0 line.
		xbeeDevice.getPWMDutyCycle(IOLine.DIO10_PWM0);
	}
	
	/**
	 * Verify that PWM duty cycle value cannot be read if the IO line is null.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=NullPointerException.class)
	public void testGetDutyCycleValueOfNullIOLine() throws XBeeException, IOException {
		// Read the duty cycle value of a null IO line.
		xbeeDevice.getPWMDutyCycle(null);
	}
	
	/**
	 * Verify that PWM duty cycle value cannot be read if the operating mode is AT.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testGetDutyCycleValueATOperatingMode() throws XBeeException, IOException {
		// Return AT operating mode when asked.
		Mockito.doReturn(OperatingMode.AT).when(xbeeDevice).getOperatingMode();
		
		// Read the duty cycle value of the PWM0 line.
		xbeeDevice.getPWMDutyCycle(IOLine.DIO10_PWM0);
	}
	
	/**
	 * Verify that PWM duty cycle value cannot be read if the operating mode is UNKNOWN.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testGetDutyCycleValueUnknownOperatingMode() throws XBeeException, IOException {
		// Return UNKNOWN operating mode when asked.
		Mockito.doReturn(OperatingMode.UNKNOWN).when(xbeeDevice).getOperatingMode();
		
		// Read the duty cycle value of the PWM0 line.
		xbeeDevice.getPWMDutyCycle(IOLine.DIO10_PWM0);
	}
	
	/**
	 * Verify that PWM duty cycle value cannot be read if the status value after sending the get 
	 * command is INVALID_PARAMETER.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testGetDutyCycleValueInvalidParameterStatusResponse() throws XBeeException, IOException {
		// Generate an ATCommandResponse with error status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.INVALID_PARAMETER);
		
		Mockito.doReturn(mockedResponse).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Read the duty cycle value of the PWM0 line.
		xbeeDevice.getPWMDutyCycle(IOLine.DIO10_PWM0);
	}
	
	/**
	 * Verify that PWM duty cycle value cannot be read if the response value after sending the get command 
	 * is null.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testGetDutyCycleValueNullResponse() throws XBeeException, IOException {
		// Return a null ATCommandResponse when sending any AT Command.
		Mockito.doReturn(null).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Read the duty cycle value of the PWM0 line.
		xbeeDevice.getPWMDutyCycle(IOLine.DIO10_PWM0);
	}
	
	/**
	 * Verify that PWM duty cycle value cannot be read if the get value command was not processed 
	 * successfully due to a timeout sending the get value command.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testGetDutyCycleValueTimeout() throws XBeeException, IOException {
		// Throw a timeout exception when trying to send any AT Command.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Read the duty cycle value of the PWM0 line.
		xbeeDevice.getPWMDutyCycle(IOLine.DIO10_PWM0);
	}
	
	/**
	 * Verify that PWM duty cycle value can be retrieved successfully.
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test
	public void testGetDutyCycleValueSuccess() throws XBeeException, IOException {
		// Generate an ATCommandResponse with OK status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.OK);
		Mockito.when(mockedResponse.getResponse()).thenReturn(new byte[]{0x03, 0x52});
		
		Mockito.doReturn(mockedResponse).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Read the duty cycle value of the PWM0 line.
		double dutyCycle = xbeeDevice.getPWMDutyCycle(IOLine.DIO10_PWM0);
		
		// Verify that the value retrieved is approximately 83.1%. 
		assertEquals(83.1, dutyCycle, 0.1);
	}
}
