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
import com.digi.xbee.api.io.IOMode;
import com.digi.xbee.api.models.ATCommand;
import com.digi.xbee.api.models.ATCommandResponse;
import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.models.OperatingMode;

public class SetIOConfigurationTest {
	
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
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setIOConfiguration(IOLine, IOMode)}.
	 * 
	 * <p>Verify that IO cannot be configured if the connection is closed.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testSetIOConfigurationConnectionClosed() throws XBeeException {
		// When checking if the connection is open, return false.
		Mockito.when(mockedPort.isOpen()).thenReturn(false);
		
		// Configure DIO0 line as digital output.
		xbeeDevice.setIOConfiguration(IOLine.DIO0_AD0, IOMode.DIGITAL_OUT_HIGH);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setIOConfiguration(IOLine, IOMode)}.
	 * 
	 * <p>Verify that IO cannot be configured if the IO line is null.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSetIOConfigurationWithNullIOLine() throws XBeeException {
		// Configure a null IO line.
		xbeeDevice.setIOConfiguration(null, IOMode.DIGITAL_OUT_HIGH);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setIOConfiguration(IOLine, IOMode)}.
	 * 
	 * <p>Verify that IO cannot be configured if the IO line is null.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSetIOConfigurationWithNullIOMode() throws XBeeException {
		// Configure DIO0 line with a null mode.
		xbeeDevice.setIOConfiguration(IOLine.DIO0_AD0, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setIOConfiguration(IOLine, IOMode)}.
	 * 
	 * <p>Verify that IO cannot be configured if the operating mode is AT.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSetIOConfigurationATOperatingMode() throws XBeeException {
		// Return AT operating mode when asked.
		Mockito.doReturn(OperatingMode.AT).when(xbeeDevice).getOperatingMode();
		
		// Configure DIO0 line as digital output.
		xbeeDevice.setIOConfiguration(IOLine.DIO0_AD0, IOMode.DIGITAL_OUT_HIGH);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setIOConfiguration(IOLine, IOMode)}.
	 * 
	 * <p>Verify that IO cannot be configured if the operating mode is UNKNOWN.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSetIOConfigurationUnknownOperatingMode() throws XBeeException {
		// Return UNKNOWN operating mode when asked.
		Mockito.doReturn(OperatingMode.UNKNOWN).when(xbeeDevice).getOperatingMode();
		
		// Configure DIO0 line as digital output.
		xbeeDevice.setIOConfiguration(IOLine.DIO0_AD0, IOMode.DIGITAL_OUT_HIGH);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setIOConfiguration(IOLine, IOMode)}.
	 * 
	 * <p>Verify that IO cannot be configured if the status value after sending the 
	 * configure command is INVALID_PARAMETER.</p>
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testSetIOConfigurationInvalidParameterStatusResponse() throws XBeeException, IOException {
		// Generate an ATCommandResponse with error status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.INVALID_PARAMETER);
		
		Mockito.doReturn(mockedResponse).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Configure DIO0 line as PWM.
		xbeeDevice.setIOConfiguration(IOLine.DIO0_AD0, IOMode.PWM);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setIOConfiguration(IOLine, IOMode)}.
	 * 
	 * <p>Verify that IO cannot be configured if the response value after sending the 
	 * configure command is null.</p>
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testSetIOConfigurationNullResponse() throws XBeeException, IOException {
		// Now try returning a null ATCommandResponse when sending any AT Command.
		Mockito.doReturn(null).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Configure DIO0 line as PWM.
		xbeeDevice.setIOConfiguration(IOLine.DIO0_AD0, IOMode.PWM);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setIOConfiguration(IOLine, IOMode)}.
	 * 
	 * <p>Verify that IO cannot be configured if the configuration command was not processed 
	 * successfully due to a timeout sending the command.</p>
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testSetIOConfigurationTimeout() throws XBeeException, IOException {
		// Throw a timeout exception when trying to send any AT Command.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Configure DIO0 line as PWM.
		xbeeDevice.setIOConfiguration(IOLine.DIO0_AD0, IOMode.PWM);
	}
}
