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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.models.ATCommand;
import com.digi.xbee.api.models.ATCommandResponse;
import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.models.OperatingMode;

public class LocalPWMHandlingTest {
	
	// Constants.
	private static final int DUTY_CYCLE_VALID_VALUE = 75;
	private static final int DUTY_CYCLE_INVALID_VALUE_LOW = -10;
	private static final int DUTY_CYCLE_INVALID_VALUE_HIGH = 150;
	
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
	 * Verify that PWM duty cycle cannot be set if the connection is closed.
	 */
	public void testSetDutyCycleConnectionClosed() {
		// When checking if the connection is open, return false.
		Mockito.when(mockedPort.isOpen()).thenReturn(false);
		
		// Set duty cycle of PWM0 to 75%.
		try {
			xbeeDevice.setPWMDutyCycle(IOLine.DIO10_PWM0, DUTY_CYCLE_VALID_VALUE);
			fail("Duty cycle shouldn't have been set.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			assertEquals(XBeeException.CONNECTION_NOT_OPEN, e.getErrorCode());
		}
	}
	
	@Test
	/**
	 * Verify that PWM duty cycle cannot be set if the IO line or value are not 
	 * valid.
	 */
	public void testSetDutyCycleInvalidParameters() {
		// Set the duty cycle of a null IO line.
		try {
			xbeeDevice.setPWMDutyCycle(null, DUTY_CYCLE_VALID_VALUE);
			fail("Duty cycle shouldn't have been set.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
		
		// Set duty cycle of PWM0 to -10%.
		try {
			xbeeDevice.setPWMDutyCycle(IOLine.DIO10_PWM0, DUTY_CYCLE_INVALID_VALUE_LOW);
			fail("Duty cycle shouldn't have been set.");
		} catch (Exception e) {
			assertEquals(IllegalArgumentException.class, e.getClass());
		}
		
		// Set duty cycle of PWM0 to 150%.
		try {
			xbeeDevice.setPWMDutyCycle(IOLine.DIO10_PWM0, DUTY_CYCLE_INVALID_VALUE_HIGH);
			fail("Duty cycle shouldn't have been set.");
		} catch (Exception e) {
			assertEquals(IllegalArgumentException.class, e.getClass());
		}
		
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
	
	@Test
	/**
	 * Verify that PWM duty cycle cannot be set if the operating mode is not API or 
	 * API Escaped.
	 */
	public void testSetDutyCycleInvalidOperatingMode() {
		// Return AT operating mode when asked.
		Mockito.doReturn(OperatingMode.AT).when(xbeeDevice).getOperatingMode();
		
		// Set duty cycle of PWM0 to 75%.
		try {
			xbeeDevice.setPWMDutyCycle(IOLine.DIO10_PWM0, DUTY_CYCLE_VALID_VALUE);
			fail("Duty cycle shouldn't have been set.");
		} catch (Exception e) {
			assertEquals(InvalidOperatingModeException.class, e.getClass());
		}
		
		// Return UNKNOWN operating mode when asked.
		Mockito.doReturn(OperatingMode.UNKNOWN).when(xbeeDevice).getOperatingMode();
		
		// Set duty cycle of PWM0 to 75%.
		try {
			xbeeDevice.setPWMDutyCycle(IOLine.DIO10_PWM0, DUTY_CYCLE_VALID_VALUE);
			fail("Duty cycle shouldn't have been set.");
		} catch (Exception e) {
			assertEquals(InvalidOperatingModeException.class, e.getClass());
		}
	}
	
	@Test
	/**
	 * Verify that PWM duty cycle cannot be set if the set command was not processed successfully 
	 * (null answer or error in the answer).
	 * 
	 * @throws Exception
	 */
	public void testSetDutyCycleOperationNotSupported() throws Exception {
		// Generate an ATCommandResponse with error status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.INVALID_PARAMETER);
		
		Mockito.doReturn(mockedResponse).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Set duty cycle of PWM0 to 75%.
		try {
			xbeeDevice.setPWMDutyCycle(IOLine.DIO10_PWM0, DUTY_CYCLE_VALID_VALUE);
			fail("Duty cycle shouldn't have been set.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			assertEquals(XBeeException.INVALID_OPERATION, e.getErrorCode());
		}
		
		// Now try returning a null ATCommandResponse when sending any AT Command.
		Mockito.doReturn(null).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Set duty cycle of PWM0 to 75%.
		try {
			xbeeDevice.setPWMDutyCycle(IOLine.DIO10_PWM0, DUTY_CYCLE_VALID_VALUE);
			fail("Duty cycle shouldn't have been set.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			assertEquals(XBeeException.INVALID_OPERATION, e.getErrorCode());
		}
	}
	
	@Test
	/**
	 * Verify that PWM duty cycle cannot be set if the set command was not processed successfully 
	 * due to a timeout sending the command.
	 * 
	 * @throws Exception
	 */
	public void testSetDutyCycleTimeout() throws Exception {
		// Throw a timeout exception when trying to send any AT Command.
		Mockito.doThrow(new XBeeException(XBeeException.CONNECTION_TIMEOUT)).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Set duty cycle of PWM0 to 75%.
		try {
			xbeeDevice.setPWMDutyCycle(IOLine.DIO10_PWM0, DUTY_CYCLE_VALID_VALUE);
			fail("Duty cycle shouldn't have been set.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			assertEquals(XBeeException.CONNECTION_TIMEOUT, e.getErrorCode());
		}
	}
	
	@Test
	/**
	 * Verify that PWM duty cycle can be set successfully.
	 * 
	 * @throws Exception
	 */
	public void testSetDutyCycleSuccess() throws Exception {
		// Generate an ATCommandResponse with OK status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.OK);
		
		Mockito.doReturn(mockedResponse).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Set duty cycle of PWM0 to 75%.
		try {
			xbeeDevice.setPWMDutyCycle(IOLine.DIO10_PWM0, DUTY_CYCLE_VALID_VALUE);
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			fail("This exception shouldn't be thrown now.");
		}
	}
	
	@Test
	/**
	 * Verify that PWM duty cycle value cannot be read if the connection is closed.
	 */
	public void testGetDutyCycleValueConnectionClosed() {
		// When checking if the connection is open, return false.
		Mockito.when(mockedPort.isOpen()).thenReturn(false);
		
		// Read the duty cycle value of the PWM0 line.
		try {
			xbeeDevice.getPWMDutyCycle(IOLine.DIO10_PWM0);
			fail("Duty cycle value shouldn't have been retrieved.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			assertEquals(XBeeException.CONNECTION_NOT_OPEN, e.getErrorCode());
		}
	}
	
	@Test
	/**
	 * Verify that PWM duty cycle value cannot be read if the IO line is not valid.
	 */
	public void testGetDutyCycleValueInvalidParameters() {
		// Read the duty cycle value of a null IO line.
		try {
			xbeeDevice.getPWMDutyCycle(null);
			fail("Duty cycle value shouldn't have been retrieved.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
	}
	
	@Test
	/**
	 * Verify that PWM duty cycle value cannot be read if the operating mode is not 
	 * API or API Escaped.
	 */
	public void testGetDutyCycleValueInvalidOperatingMode() {
		// Return AT operating mode when asked.
		Mockito.doReturn(OperatingMode.AT).when(xbeeDevice).getOperatingMode();
		
		// Read the duty cycle value of the PWM0 line.
		try {
			xbeeDevice.getPWMDutyCycle(IOLine.DIO10_PWM0);
			fail("Duty cycle value shouldn't have been retrieved.");
		} catch (Exception e) {
			assertEquals(InvalidOperatingModeException.class, e.getClass());
		}
		
		// Return UNKNOWN operating mode when asked.
		Mockito.doReturn(OperatingMode.UNKNOWN).when(xbeeDevice).getOperatingMode();
		
		// Read the duty cycle value of the PWM0 line.
		try {
			xbeeDevice.getPWMDutyCycle(IOLine.DIO10_PWM0);
			fail("Duty cycle value shouldn't have been retrieved.");
		} catch (Exception e) {
			assertEquals(InvalidOperatingModeException.class, e.getClass());
		}
	}
	
	@Test
	/**
	 * Verify that PWM duty cycle value cannot be read if the get value command was not processed 
	 * successfully (null answer or error in the answer).
	 * 
	 * @throws Exception
	 */
	public void testGetDutyCycleValueOperationNotSupported() throws Exception {
		// Generate an ATCommandResponse with error status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.INVALID_PARAMETER);
		
		Mockito.doReturn(mockedResponse).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Read the duty cycle value of the PWM0 line.
		try {
			xbeeDevice.getPWMDutyCycle(IOLine.DIO10_PWM0);
			fail("Duty cycle value shouldn't have been retrieved.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			assertEquals(XBeeException.INVALID_OPERATION, e.getErrorCode());
		}
		
		// Now try returning a null ATCommandResponse when sending any AT Command.
		Mockito.doReturn(null).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Read the duty cycle value of the PWM0 line.
		try {
			xbeeDevice.getPWMDutyCycle(IOLine.DIO10_PWM0);
			fail("Duty cycle value shouldn't have been retrieved.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			assertEquals(XBeeException.INVALID_OPERATION, e.getErrorCode());
		}
	}
	
	@Test
	/**
	 * Verify that PWM duty cycle value cannot be read if the get value command was not processed 
	 * successfully due to a timeout sending the get value command.
	 * 
	 * @throws Exception
	 */
	public void testGetDutyCycleValueTimeout() throws Exception {
		// Throw a timeout exception when trying to send any AT Command.
		Mockito.doThrow(new XBeeException(XBeeException.CONNECTION_TIMEOUT)).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Read the duty cycle value of the PWM0 line.
		try {
			xbeeDevice.getPWMDutyCycle(IOLine.DIO10_PWM0);
			fail("Duty cycle value shouldn't have been retrieved.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			assertEquals(XBeeException.CONNECTION_TIMEOUT, e.getErrorCode());
		}
	}
	
	@Test
	/**
	 * Verify that PWM duty cycle value can be retrieved successfully.
	 * 
	 * @throws Exception
	 */
	public void testGetDutyCycleValueSuccess() throws Exception {
		// Generate an ATCommandResponse with OK status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.OK);
		Mockito.when(mockedResponse.getResponse()).thenReturn(new byte[]{0x03, 0x52});
		
		Mockito.doReturn(mockedResponse).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Read the duty cycle value of the PWM0 line.
		double dutyCycle = -1;
		try {
			dutyCycle = xbeeDevice.getPWMDutyCycle(IOLine.DIO10_PWM0);
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			fail("This exception shouldn't be thrown now.");
		}
		
		// Verify that the value retrieved is approximately 83.1%. 
		assertEquals(83.1, dutyCycle, 0.1);
	}
}
