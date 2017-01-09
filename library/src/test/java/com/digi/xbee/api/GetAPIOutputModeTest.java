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
import com.digi.xbee.api.models.APIOutputMode;

public class GetAPIOutputModeTest {

	// Constants.
	private static final String PARAMETER_AO = "AO";
	
	private static final byte[] RESPONSE_AO_VALID = new byte[]{0x01};        // 0x01 - APIOutputMode.MODE_EXPLICIT
	private static final byte[] RESPONSE_AO_OUT_OF_RANGE = new byte[]{0x15}; // 0x15 - APIOutputMode.LEVEL_UNKNOWN
	
	// Variables.
	private XBeeDevice xbeeDevice;
	
	@Before
	public void setup() throws Exception {
		// Instantiate a local XBeeDevice object.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(Mockito.mock(SerialPortRxTx.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getAPIOutputMode()}.
	 * 
	 * <p>Verify that the API output mode of an XBee device cannot be get if the connection of the 
	 * device is closed.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testGetAPIOutputModeErrorConnectionClosed() throws XBeeException {
		// Throw an interface not open exception when trying to get any parameter.
		Mockito.doThrow(new InterfaceNotOpenException()).when(xbeeDevice).getParameter(Mockito.anyString());
		
		// Get the API output mode.
		xbeeDevice.getAPIOutputMode();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getAPIOutputMode()}.
	 * 
	 * <p>Verify that the API output mode of an XBee device cannot be get if the operating mode of the 
	 * device is not valid.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testGetAPIOutputModeErrorInvalidOperatingMode() throws XBeeException {
		// Throw an invalid operating mode exception when trying to get any parameter.
		Mockito.doThrow(new InvalidOperatingModeException()).when(xbeeDevice).getParameter(Mockito.anyString());
		
		// Get the API output mode.
		xbeeDevice.getAPIOutputMode();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getAPIOutputMode()}.
	 * 
	 * <p>Verify that the API output mode of an XBee device cannot be get if there is a timeout getting 
	 * the API output mode.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testGetAPIOutputModeErrorTimeout() throws XBeeException, IOException {
		// Throw a timeout exception when trying to get the AO parameter.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).getParameter(PARAMETER_AO);
		
		// Get the API output mode.
		xbeeDevice.getAPIOutputMode();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getAPIOutputMode()}.
	 * 
	 * <p>Verify that the API output mode of an XBee device cannot be get if the answer when getting 
	 * the API output mode is null or the response status is not OK. It is, there is an AT command exception 
	 * getting the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testGetAPIOutputModeErrorInvalidAnswer() throws XBeeException, IOException {
		// Throw an AT command exception when trying to get the AO parameter.
		Mockito.doThrow(new ATCommandException(null)).when(xbeeDevice).getParameter(PARAMETER_AO);
		
		// Get the API output mode.
		xbeeDevice.getAPIOutputMode();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getAPIOutputMode()}.
	 * 
	 * <p>Verify that the API output mode of an XBee device can be get successfully.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testGetAPIOutputModeSuccess() throws XBeeException, IOException {
		// Return a valid (Explicit mode) value when getting the AO parameter.
		Mockito.doReturn(RESPONSE_AO_VALID).when(xbeeDevice).getParameter(PARAMETER_AO);
		
		// Get the API output mode.
		assertEquals(APIOutputMode.MODE_EXPLICIT, xbeeDevice.getAPIOutputMode());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getAPIOutputMode()}.
	 * 
	 * <p>Verify if the response to the AO command is out of range, a {@code null} API output 
	 * mode is received.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testGetAPIOutputModeOutOfRange() throws XBeeException, IOException {
		// Return an out of range value when getting the AO parameter.
		Mockito.doReturn(RESPONSE_AO_OUT_OF_RANGE).when(xbeeDevice).getParameter(PARAMETER_AO);
		
		// Get the API output mode.
		assertNull(xbeeDevice.getAPIOutputMode());
	}
}
