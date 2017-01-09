/**
 * Copyright 2017, Digi International Inc.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES 
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR 
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES 
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN 
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF 
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package com.digi.xbee.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
import com.digi.xbee.api.models.ATCommand;
import com.digi.xbee.api.models.ATCommandResponse;
import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.models.OperatingMode;

public class SetPWMTest {
	
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
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setPWMDutyCycle(IOLine, int)}.
	 * 
	 * <p>Verify that PWM duty cycle cannot be set if the connection is closed.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testSetDutyCycleConnectionClosed() throws XBeeException {
		// When checking if the connection is open, return false.
		Mockito.when(mockedPort.isOpen()).thenReturn(false);
		
		// Set duty cycle of PWM0 to 75%.
		xbeeDevice.setPWMDutyCycle(IOLine.DIO10_PWM0, DUTY_CYCLE_VALID_VALUE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setPWMDutyCycle(IOLine, int)}.
	 * 
	 * <p>Verify that PWM duty cycle cannot be set if the IO line is null.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSetDutyCycleOfNullIOLine() throws XBeeException {
		// Set the duty cycle of a null IO line.
		xbeeDevice.setPWMDutyCycle(null, DUTY_CYCLE_VALID_VALUE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setPWMDutyCycle(IOLine, int)}.
	 * 
	 * <p>Verify that PWM duty cycle cannot be set if the value is under 0%.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetDutyCycleWithNegativePercentace() throws XBeeException {
		// Set duty cycle of PWM0 with a negative value.
		xbeeDevice.setPWMDutyCycle(IOLine.DIO10_PWM0, DUTY_CYCLE_NEGATIVE_VALUE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setPWMDutyCycle(IOLine, int)}.
	 * 
	 * <p>Verify that PWM duty cycle cannot be set if the value is over 100%.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetDutyCycleWithPercentageOver100() throws XBeeException {
		// Set duty cycle of PWM0 with a value over 100%.
		xbeeDevice.setPWMDutyCycle(IOLine.DIO10_PWM0, DUTY_CYCLE_OVER_100_VALUE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setPWMDutyCycle(IOLine, int)}.
	 * 
	 * <p>Verify that PWM duty cycle cannot be set if the IO line does not have 
	 * PWM capabilities.</p>
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
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setPWMDutyCycle(IOLine, int)}.
	 * 
	 * <p>Verify that PWM duty cycle cannot be set if the operating mode is AT.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSetDutyCycleATOperatingMode() throws XBeeException {
		// Return AT operating mode when asked.
		Mockito.doReturn(OperatingMode.AT).when(xbeeDevice).getOperatingMode();
		
		// Set duty cycle of PWM0 to 75%.
		xbeeDevice.setPWMDutyCycle(IOLine.DIO10_PWM0, DUTY_CYCLE_VALID_VALUE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setPWMDutyCycle(IOLine, int)}.
	 * 
	 * <p>Verify that PWM duty cycle cannot be set if the operating mode is UNKNOWN.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSetDutyCycleUnknownOperatingMode() throws XBeeException {
		// Return UNKNOWN operating mode when asked.
		Mockito.doReturn(OperatingMode.UNKNOWN).when(xbeeDevice).getOperatingMode();
		
		// Set duty cycle of PWM0 to 75%.
		xbeeDevice.setPWMDutyCycle(IOLine.DIO10_PWM0, DUTY_CYCLE_VALID_VALUE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setPWMDutyCycle(IOLine, int)}.
	 * 
	 * <p>Verify that PWM duty cycle cannot be set if the status value after sending the set 
	 * command is INVALID_PARAMETER.</p>
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testSetDutyCycleInvalidParameterStatusResponse() throws XBeeException, IOException {
		// Generate an ATCommandResponse with error status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.INVALID_PARAMETER);
		
		Mockito.doReturn(mockedResponse).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Set duty cycle of PWM0 to 75%.
		xbeeDevice.setPWMDutyCycle(IOLine.DIO10_PWM0, DUTY_CYCLE_VALID_VALUE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setPWMDutyCycle(IOLine, int)}.
	 * 
	 * <p>Verify that PWM duty cycle cannot be set if the response value after sending the set command 
	 * is null.</p>
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testSetDutyCycleNullResponse() throws XBeeException, IOException {
		// Return a null ATCommandResponse when sending any AT Command.
		Mockito.doReturn(null).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Set duty cycle of PWM0 to 75%.
		xbeeDevice.setPWMDutyCycle(IOLine.DIO10_PWM0, DUTY_CYCLE_VALID_VALUE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setPWMDutyCycle(IOLine, int)}.
	 * 
	 * <p>Verify that PWM duty cycle cannot be set if the set command was not processed successfully 
	 * due to a timeout sending the command.</p>
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
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setPWMDutyCycle(IOLine, int)}.
	 * 
	 * <p>Verify that PWM duty cycle can be set successfully.</p>
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
}
