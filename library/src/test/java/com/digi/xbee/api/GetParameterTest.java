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

public class GetParameterTest {

	// Constants
	private static final String PARAM_INVALID = "N";
	private static final String PARAM_NI = "NI";
	private static final String VALUE_NI = "Yoda";
	
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
	 * Test method for {@link com.digi.xbee.api.AbstractXBeeDevice#getParameter(String)}.
	 * 
	 * <p>Verify that if the parameter is null, a {@code NullPointerException} is thrown when
	 * getting a parameter.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=NullPointerException.class)
	public void testGetParameterNullParameter() throws TimeoutException, XBeeException {
		xbeeDevice.getParameter(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.AbstractXBeeDevice#getParameter(String)}.
	 * 
	 * <p>Verify that if the parameter is not valid, an {@code IllegalArgumentException} is thrown 
	 * when getting a parameter.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetParameterInvalidParameter() throws TimeoutException, XBeeException {
		xbeeDevice.getParameter(PARAM_INVALID);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.AbstractXBeeDevice#getParameter(String)}.
	 * 
	 * <p>Verify that an {@code InterfaceNotOpenException} is thrown when trying to get a parameter 
	 * with the connection closed.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testGetParameterConnectionClosed() throws TimeoutException, XBeeException {
		// Configure the connection to indicate it is closed when asked.
		Mockito.when(mockedPort.isOpen()).thenReturn(false);
		
		xbeeDevice.getParameter(PARAM_NI);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.AbstractXBeeDevice#getParameter(String)}.
	 * 
	 * <p>Verify that an {@code InvalidOperatingModeException} is thrown when trying to get a parameter 
	 * and the operating mode of the device is UNKNOWN.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testGetParameterInvalidOperatingMode() throws TimeoutException, XBeeException {
		// Configure the operating mode of the device to return UNKNOWN when asked.
		Mockito.when(xbeeDevice.getOperatingMode()).thenReturn(OperatingMode.UNKNOWN);
		
		xbeeDevice.getParameter(PARAM_NI);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.AbstractXBeeDevice#getParameter(String)}.
	 * 
	 * <p>Verify that an {@code InvalidOperatingModeException} is thrown when trying to get a parameter 
	 * and the operating mode of the device is AT.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testGetParameterATOperatingMode() throws TimeoutException, XBeeException {
		// Configure the operating mode of the device to return AT when asked.
		Mockito.when(xbeeDevice.getOperatingMode()).thenReturn(OperatingMode.AT);
		
		xbeeDevice.getParameter(PARAM_NI);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.AbstractXBeeDevice#getParameter(String)}.
	 * 
	 * <p>Verify that a {@code TimeoutException} is thrown when there is a timeout sending the AT 
	 * command to get the parameter.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testGetParameterTimeout() throws TimeoutException, XBeeException, IOException {
		// Configure the sendAtCommand method to throw a timeoutException when called.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).sendATCommand(Mockito.any(ATCommand.class));
		
		xbeeDevice.getParameter(PARAM_NI);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.AbstractXBeeDevice#getParameter(String)}.
	 * 
	 * <p>Verify that an {@code ATCommandException} is thrown when the response of the AT command 
	 * get is null.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testGetParameterNullResponse() throws TimeoutException, XBeeException, IOException {
		// Configure the sendAtCommand method to return a null response when called.
		Mockito.doReturn(null).when(xbeeDevice).sendATCommand(Mockito.any(ATCommand.class));
		
		xbeeDevice.getParameter(PARAM_NI);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.AbstractXBeeDevice#getParameter(String)}.
	 * 
	 * <p>Verify that an {@code ATCommandException} is thrown when the response status of the AT 
	 * command get is null.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testGetParameterNullResponseStatus() throws TimeoutException, XBeeException, IOException {
		// Configure the mocked ATCommandResponse to return a null response status when asked.
		Mockito.doReturn(null).when(mocketATCommandResponse).getResponseStatus();
		
		// Configure the sendAtCommand method to return the mocked response when called.
		Mockito.doReturn(mocketATCommandResponse).when(xbeeDevice).sendATCommand(Mockito.any(ATCommand.class));
		
		xbeeDevice.getParameter(PARAM_NI);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.AbstractXBeeDevice#getParameter(String)}.
	 * 
	 * <p>Verify that an {@code ATCommandException} is thrown when the response status of the AT 
	 * command get is not OK.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testGetParameterInvalidResponseStatus() throws TimeoutException, XBeeException, IOException {
		// Configure the mocked ATCommandResponse to return an ERROR response status when asked.
		Mockito.doReturn(ATCommandStatus.ERROR).when(mocketATCommandResponse).getResponseStatus();
		
		// Configure the sendAtCommand method to return the mocked response when called.
		Mockito.doReturn(mocketATCommandResponse).when(xbeeDevice).sendATCommand(Mockito.any(ATCommand.class));
		
		xbeeDevice.getParameter(PARAM_NI);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.AbstractXBeeDevice#getParameter(String)}.
	 * 
	 * <p>Verify that an {@code XBeeException} is thrown when there is an IO exception writing in 
	 * the communication interface.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 */
	@Test(expected=XBeeException.class)
	public void testGetParameterIOException() throws TimeoutException, XBeeException, IOException {
		// Configure the sendAtCommand method to throw an IOException when called.
		Mockito.doThrow(new IOException()).when(xbeeDevice).sendATCommand(Mockito.any(ATCommand.class));
		
		xbeeDevice.getParameter(PARAM_NI);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.AbstractXBeeDevice#getParameter(String)}.
	 * 
	 * <p>Verify that an {@code OperationNotSupportedException} is thrown when the value of the read 
	 * parameter is null.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 */
	@Test(expected=XBeeException.class)
	public void testGetParameterNullParameterValue() throws TimeoutException, XBeeException, IOException {
		// Configure the mocked ATCommandResponse to return an OK response status when asked.
		Mockito.doReturn(ATCommandStatus.OK).when(mocketATCommandResponse).getResponseStatus();
		
		// Configure the mocked ATCommandResponse to return a null response when asked.
		Mockito.doReturn(null).when(mocketATCommandResponse).getResponse();
		
		// Configure the sendAtCommand method to return the mocked response when called.
		Mockito.doReturn(mocketATCommandResponse).when(xbeeDevice).sendATCommand(Mockito.any(ATCommand.class));
		
		xbeeDevice.getParameter(PARAM_NI);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.AbstractXBeeDevice#getParameter(String)}.
	 * 
	 * <p>Verify that a parameter can be get successfully using the {@code getParameter} method.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 * @throws IOException 
	 */
	@Test
	public void testGetParameterSuccess() throws TimeoutException, XBeeException, IOException {
		// Configure the mocked ATCommandResponse to return an OK response status when asked.
		Mockito.doReturn(ATCommandStatus.OK).when(mocketATCommandResponse).getResponseStatus();
		
		// Configure the mocked ATCommandResponse to return the NI value as response when asked.
		Mockito.doReturn(VALUE_NI.getBytes()).when(mocketATCommandResponse).getResponse();
		
		// Configure the sendAtCommand method to return the mocked response when called.
		Mockito.doReturn(mocketATCommandResponse).when(xbeeDevice).sendATCommand(Mockito.any(ATCommand.class));
		
		// Get the parameter value.
		byte[] paramValue = xbeeDevice.getParameter(PARAM_NI);
		
		// Verify the value is correct.
		assertArrayEquals(VALUE_NI.getBytes(), paramValue);
	}
}
