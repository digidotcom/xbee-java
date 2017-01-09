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
import com.digi.xbee.api.models.APIOutputMode;

public class SetAPIOutputMode {

	// Constants.
	private static final String PARAMETER_AO = "AO";
	
	private static final APIOutputMode API_OUTPUT_MODE = APIOutputMode.MODE_EXPLICIT;
	
	// Variables.
	private XBeeDevice xbeeDevice;
	
	@Before
	public void setup() throws Exception {
		// Instantiate a local XBeeDevice object.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(Mockito.mock(SerialPortRxTx.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setAPIOutputMode(APIOutputMode)}.
	 * 
	 * <p>Verify that the API output mode of an XBee device cannot be set if the API output mode provided 
	 * is null.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=NullPointerException.class)
	public void testSetAPIOutputModeErrorNullAPIOutputMode() throws XBeeException {
		// Set the API output mode.
		xbeeDevice.setAPIOutputMode(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setAPIOutputMode(APIOutputMode)}.
	 * 
	 * <p>Verify that the API output mode of an XBee device cannot be set if the connection of the 
	 * device is closed.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testSetAPIOutputModeErrorConnectionClosed() throws XBeeException {
		// Throw an interface not open exception when trying to set any parameter.
		Mockito.doThrow(new InterfaceNotOpenException()).when(xbeeDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		// Set the API output mode.
		xbeeDevice.setAPIOutputMode(API_OUTPUT_MODE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setAPIOutputMode(APIOutputMode)}.
	 * 
	 * <p>Verify that the API output mode of an XBee device cannot be set if the operating mode of the 
	 * device is not valid.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSetAPIOutputModeErrorInvalidOperatingMode() throws XBeeException {
		// Throw an invalid operating mode exception when trying to set any parameter.
		Mockito.doThrow(new InvalidOperatingModeException()).when(xbeeDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		// Set the API output mode.
		xbeeDevice.setAPIOutputMode(API_OUTPUT_MODE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setAPIOutputMode(APIOutputMode)}.
	 * 
	 * <p>Verify that the API output mode of an XBee device cannot be set if there is a timeout setting 
	 * the API output mode of the device.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testSetAPIOutputModeErrorTimeout() throws XBeeException, IOException {
		// Throw a timeout exception when trying to set the AO parameter.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).setParameter(Mockito.eq(PARAMETER_AO), Mockito.any(byte[].class));
		
		// Set the API output mode.
		xbeeDevice.setAPIOutputMode(API_OUTPUT_MODE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setAPIOutputMode(APIOutputMode)}.
	 * 
	 * <p>Verify that the API output mode of an XBee device cannot be set if the answer when setting 
	 * the API output mode is null or the response status is not OK. It is, there is an AT command exception 
	 * setting the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testSetAPIOutputModeErrorInvalidAnswer() throws XBeeException, IOException {
		// Throw an AT command exception when trying to set the AO parameter.
		Mockito.doThrow(new ATCommandException(null)).when(xbeeDevice).setParameter(Mockito.eq(PARAMETER_AO), Mockito.any(byte[].class));
		
		// Set the API output mode.
		xbeeDevice.setAPIOutputMode(API_OUTPUT_MODE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setAPIOutputMode(APIOutputMode)}.
	 * 
	 * <p>Verify that the API output mode of an XBee device can be set successfully.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testSetAPIOutputModeSuccess() throws XBeeException, IOException {
		// Do nothing when trying to set the AO parameter.
		Mockito.doNothing().when(xbeeDevice).setParameter(Mockito.eq(PARAMETER_AO), Mockito.any(byte[].class));
		
		// Set the API output mode.
		xbeeDevice.setAPIOutputMode(API_OUTPUT_MODE);
	}
}
