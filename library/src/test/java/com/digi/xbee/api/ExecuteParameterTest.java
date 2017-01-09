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
import org.powermock.api.mockito.PowerMockito;

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.ATCommandException;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.ATCommand;
import com.digi.xbee.api.models.ATCommandResponse;
import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.models.OperatingMode;

public class ExecuteParameterTest {

	// Constants
	private static final String PARAM_INVALID = "A";
	private static final String PARAM_AC = "AC";
	
	// Variables.
	private SerialPortRxTx mockedPort;
	private XBeeDevice xbeeDevice;
	
	private ATCommandResponse mocketATCommandResponse;
	
	@Before
	public void setup() {
		// Mock an RxTx IConnectionInterface.
		mockedPort = Mockito.mock(SerialPortRxTx.class);
		Mockito.when(mockedPort.isOpen()).thenReturn(true);
		
		// Instantiate an XBeeDevice object with basic parameters.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(mockedPort));
		Mockito.when(xbeeDevice.getOperatingMode()).thenReturn(OperatingMode.API);
		
		// Mock an ATCommandResponse.
		mocketATCommandResponse = Mockito.mock(ATCommandResponse.class);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.AbstractXBeeDevice#executeParameter(String)}.
	 * 
	 * <p>Verify that if the parameter is null, a {@code NullPointerException} is thrown when 
	 * executing a parameter.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=NullPointerException.class)
	public void testExecuteParameterNullParameter() throws TimeoutException, XBeeException {
		xbeeDevice.executeParameter(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.AbstractXBeeDevice#executeParameter(String)}.
	 * 
	 * <p>Verify that if the parameter is not valid, an {@code IllegalArgumentException} is thrown 
	 * when executing a parameter.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testExecuteParameterInvalidParameter() throws TimeoutException, XBeeException {
		xbeeDevice.executeParameter(PARAM_INVALID);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.AbstractXBeeDevice#executeParameter(String)}.
	 * 
	 * <p>Verify that an {@code InterfaceNotOpenException} is thrown when trying to execute a parameter 
	 * with the connection closed.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testExecuteParameterConnectionClosed() throws TimeoutException, XBeeException {
		// Configure the connection to indicate it is closed when asked.
		Mockito.when(mockedPort.isOpen()).thenReturn(false);
		
		xbeeDevice.executeParameter(PARAM_AC);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.AbstractXBeeDevice#executeParameter(String)}.
	 * 
	 * <p>Verify that an {@code InvalidOperatingModeException} is thrown when trying to execute a 
	 * parameter and the operating mode of the device is UNKNOWN.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testExecuteParameterInvalidOperatingMode() throws TimeoutException, XBeeException {
		// Configure the operating mode of the device to return UNKNOWN when asked.
		Mockito.when(xbeeDevice.getOperatingMode()).thenReturn(OperatingMode.UNKNOWN);
		
		xbeeDevice.executeParameter(PARAM_AC);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.AbstractXBeeDevice#executeParameter(String)}.
	 * 
	 * <p>Verify that an {@code InvalidOperatingModeException} is thrown when trying to execute a 
	 * parameter and the operating mode of the device is AT.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testExecuteParameterATOperatingMode() throws TimeoutException, XBeeException {
		// Configure the operating mode of the device to return AT when asked.
		Mockito.when(xbeeDevice.getOperatingMode()).thenReturn(OperatingMode.AT);
		
		xbeeDevice.executeParameter(PARAM_AC);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.AbstractXBeeDevice#executeParameter(String)}.
	 * 
	 * <p>Verify that a {@code TimeoutException} is thrown when there is a timeout sending the AT 
	 * command to execute the parameter.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testExecuteParameterTimeout() throws TimeoutException, XBeeException, IOException {
		// Configure the sendAtCommand method to throw a timeoutException when called.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).sendATCommand(Mockito.any(ATCommand.class));
		
		xbeeDevice.executeParameter(PARAM_AC);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.AbstractXBeeDevice#executeParameter(String)}.
	 * 
	 * <p>Verify that an {@code ATCommandException} is thrown when the response of the AT command 
	 * execution is null.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testExecuteParameterNullResponse() throws TimeoutException, XBeeException, IOException {
		// Configure the sendAtCommand method to return a null response when called.
		Mockito.doReturn(null).when(xbeeDevice).sendATCommand(Mockito.any(ATCommand.class));
		
		xbeeDevice.executeParameter(PARAM_AC);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.AbstractXBeeDevice#executeParameter(String)}.
	 * 
	 * <p>Verify that an {@code ATCommandException} is thrown when the response status of the AT 
	 * command execution is null.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testExecuteParameterNullResponseStatus() throws TimeoutException, XBeeException, IOException {
		// Configure the mocked ATCommandResponse to return a null response status when asked.
		Mockito.doReturn(null).when(mocketATCommandResponse).getResponseStatus();
		
		// Configure the sendAtCommand method to return the mocked response when called.
		Mockito.doReturn(mocketATCommandResponse).when(xbeeDevice).sendATCommand(Mockito.any(ATCommand.class));
		
		xbeeDevice.executeParameter(PARAM_AC);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.AbstractXBeeDevice#executeParameter(String)}.
	 * 
	 * <p>Verify that an {@code ATCommandException} is thrown when the response status of the AT 
	 * command execution is not OK.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testExecuteParameterInvalidResponseStatus() throws TimeoutException, XBeeException, IOException {
		// Configure the mocked ATCommandResponse to return an ERROR response status when asked.
		Mockito.doReturn(ATCommandStatus.ERROR).when(mocketATCommandResponse).getResponseStatus();
		
		// Configure the sendAtCommand method to return the mocked response when called.
		Mockito.doReturn(mocketATCommandResponse).when(xbeeDevice).sendATCommand(Mockito.any(ATCommand.class));
		
		xbeeDevice.executeParameter(PARAM_AC);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.AbstractXBeeDevice#executeParameter(String)}.
	 * 
	 * <p>Verify that an {@code XBeeException} is thrown when there is an IO exception writing in 
	 * the communication interface.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 */
	@Test(expected=XBeeException.class)
	public void testExecuteParameterIOException() throws TimeoutException, XBeeException, IOException {
		// Configure the sendAtCommand method to throw an IOException when called.
		Mockito.doThrow(new IOException()).when(xbeeDevice).sendATCommand(Mockito.any(ATCommand.class));
		
		xbeeDevice.executeParameter(PARAM_AC);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.AbstractXBeeDevice#executeParameter(String)}.
	 * 
	 * <p>Verify that a parameter can be executed successfully using the {@code executeParameter} method.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 */
	@Test
	public void testExecuteParameterSuccess() throws TimeoutException, XBeeException, IOException {
		// Configure the mocked ATCommandResponse to return an OK response status when asked.
		Mockito.doReturn(ATCommandStatus.OK).when(mocketATCommandResponse).getResponseStatus();
		
		// Configure the sendAtCommand method to return the mocked response when called.
		Mockito.doReturn(mocketATCommandResponse).when(xbeeDevice).sendATCommand(Mockito.any(ATCommand.class));
		
		xbeeDevice.executeParameter(PARAM_AC);
	}
}
