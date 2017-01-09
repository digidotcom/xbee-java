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

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.ATCommandException;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.OperationNotSupportedException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOMode;
import com.digi.xbee.api.models.ATCommand;
import com.digi.xbee.api.models.ATCommandResponse;
import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.models.OperatingMode;

public class GetIOConfigurationTest {
	
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
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getIOConfiguration(IOLine)}.
	 * 
	 * <p>Verify that IO configuration cannot be read if the connection is closed.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testGetIOConfigurationConnectionClosed() throws XBeeException {
		// When checking if the connection is open, return false.
		Mockito.when(mockedPort.isOpen()).thenReturn(false);
		
		// Read the configuration of the DIO0 line.
		xbeeDevice.getIOConfiguration(IOLine.DIO0_AD0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getIOConfiguration(IOLine)}.
	 * 
	 * <p>Verify that IO configuration cannot be read if the IO line is null.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=NullPointerException.class)
	public void testGetIOConfigurationWithNullIOLine() throws XBeeException {
		// Read the configuration of a null IO line.
		xbeeDevice.getIOConfiguration(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getIOConfiguration(IOLine)}.
	 * 
	 * <p>Verify that IO configuration cannot be read if the operating mode is AT.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testGetIOConfigurationATOperatingMode() throws XBeeException {
		// Return AT operating mode when asked.
		Mockito.doReturn(OperatingMode.AT).when(xbeeDevice).getOperatingMode();
		
		// Read the configuration of the DIO0 line.
		xbeeDevice.getIOConfiguration(IOLine.DIO0_AD0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getIOConfiguration(IOLine)}.
	 * 
	 * <p>Verify that IO configuration cannot be read if the operating mode is UNKNOWN.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testGetIOConfigurationUnknownOperatingMode() throws XBeeException {
		// Return UNKNOWN operating mode when asked.
		Mockito.doReturn(OperatingMode.UNKNOWN).when(xbeeDevice).getOperatingMode();
		
		// Read the configuration of the DIO0 line.
		xbeeDevice.getIOConfiguration(IOLine.DIO0_AD0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getIOConfiguration(IOLine)}.
	 * 
	 * <p>Verify that IO configuration cannot be read if the status value after sending the 
	 * configuration command is INVALID_PARAMETER.</p>
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testGetIOConfigurationInvalidParameterStatusResponse() throws XBeeException, IOException {
		// Generate an ATCommandResponse with error status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.INVALID_PARAMETER);
		
		Mockito.doReturn(mockedResponse).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Read the configuration of the DIO0 line.
		xbeeDevice.getIOConfiguration(IOLine.DIO0_AD0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getIOConfiguration(IOLine)}.
	 * 
	 * <p>Verify that IO configuration cannot be read if the response value after sending the 
	 * configuration command is null.</p>
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testGetIOConfigurationNullResponse() throws XBeeException, IOException {
		// Return a null ATCommandResponse when sending any AT Command.
		Mockito.doReturn(null).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Read the configuration of the DIO0 line.
		xbeeDevice.getIOConfiguration(IOLine.DIO0_AD0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getIOConfiguration(IOLine)}.
	 * 
	 * <p>Verify that IO configuration cannot be read if the response value contains an invalid 
	 * configuration mode.</p>
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testGetIOConfigurationInvalidIOModeResponse() throws XBeeException, IOException {
		// Generate an ATCommandResponse with OK status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.OK);
		// The value of the AT command will be 0xFF, which is a configuration ID not contained in the IOMode enumerator.
		Mockito.when(mockedResponse.getResponse()).thenReturn(new byte[]{(byte) 0xFF});
		
		Mockito.doReturn(mockedResponse).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Read the configuration of the DIO0 line.
		xbeeDevice.getIOConfiguration(IOLine.DIO0_AD0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getIOConfiguration(IOLine)}.
	 * 
	 * <p>Verify that IO configuration cannot be read if the get configuration command was not processed 
	 * successfully due to a timeout sending the get configuration command.</p>
	 * 
	 * @throws XBeeException 
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testGetIOConfigurationTimeout() throws XBeeException, IOException {
		// Throw a timeout exception when trying to send any AT Command.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Read the configuration of the DIO0 line.
		xbeeDevice.getIOConfiguration(IOLine.DIO0_AD0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getIOConfiguration(IOLine)}.
	 * 
	 * <p>Verify that IO configuration can be read successfully.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetIOConfigurationSuccess() throws Exception {
		// Generate an ATCommandResponse with OK status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.OK);
		// The value of the AT command will be 5, which is the DIGITAL_OUT_HIGH mode.
		Mockito.when(mockedResponse.getResponse()).thenReturn(new byte[]{(byte)IOMode.DIGITAL_OUT_HIGH.getID()});
		
		Mockito.doReturn(mockedResponse).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Read the configuration of the DIO0 line.
		IOMode ioMode = xbeeDevice.getIOConfiguration(IOLine.DIO0_AD0);
		
		// Verify the read mode is correct.
		assertEquals(IOMode.DIGITAL_OUT_HIGH, ioMode);
	}
}
