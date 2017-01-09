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

public class WriteChangesTest {

	// Constants.
	private static final String PARAMETER_WR = "WR";
	
	// Variables.
	private XBeeDevice xbeeDevice;
	
	@Before
	public void setup() throws Exception {
		// Instantiate a local XBeeDevice object.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(Mockito.mock(SerialPortRxTx.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#writeChanges()}.
	 * 
	 * <p>Verify that changes on an XBee device cannot be written if the connection of the 
	 * device is closed.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testWriteChangesErrorConnectionClosed() throws XBeeException {
		// Throw an interface not open exception when trying to set any parameter.
		Mockito.doThrow(new InterfaceNotOpenException()).when(xbeeDevice).executeParameter(PARAMETER_WR);
		
		// Write changes in the device.
		xbeeDevice.writeChanges();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#writeChanges()}.
	 * 
	 * <p>Verify that changes on an XBee device cannot be written if the operating mode of the 
	 * device is not valid.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testWriteChangesErrorInvalidOperatingMode() throws XBeeException {
		// Throw an invalid operating mode exception when trying to set any parameter.
		Mockito.doThrow(new InvalidOperatingModeException()).when(xbeeDevice).executeParameter(PARAMETER_WR);
		
		// Write changes in the device.
		xbeeDevice.writeChanges();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#writeChanges()}.
	 * 
	 * <p>Verify that changes on an XBee device cannot be written if there is a timeout writing 
	 * those changes.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testWriteChangesErrorTimeout() throws XBeeException, IOException {
		// Throw a timeout exception when trying to set the WR parameter.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).executeParameter(PARAMETER_WR);
		
		// Write changes in the device.
		xbeeDevice.writeChanges();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#writeChanges()}.
	 * 
	 * <p>Verify that changes on an XBee device cannot be written if the answer when writing 
	 * the changes is null or the response status is not OK. It is, there is an AT command exception 
	 * writing the changes.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testWriteChangesErrorInvalidAnswer() throws XBeeException, IOException {
		// Throw an AT command exception when trying to set the WR parameter.
		Mockito.doThrow(new ATCommandException(null)).when(xbeeDevice).executeParameter(PARAMETER_WR);
		
		// Write changes in the device.
		xbeeDevice.writeChanges();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#writeChanges()}.
	 * 
	 * <p>Verify that changes on an XBee device can be written successfully.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testWriteChangesSuccess() throws XBeeException, IOException {
		// Do nothing when trying to set the WR parameter.
		Mockito.doNothing().when(xbeeDevice).executeParameter(PARAMETER_WR);
		
		// Write changes in the device.
		xbeeDevice.writeChanges();
	}
}
