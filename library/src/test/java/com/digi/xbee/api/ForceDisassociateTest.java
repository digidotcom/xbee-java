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

public class ForceDisassociateTest {

	// Constants.
	private static final String PARAMETER_DA = "DA";
	
	// Variables.
	private XBeeDevice xbeeDevice;
	
	@Before
	public void setup() throws Exception {
		// Instantiate a local XBeeDevice object.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(Mockito.mock(SerialPortRxTx.class)));
		
		Mockito.when(xbeeDevice.isOpen()).thenReturn(true);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#forceDisassociate()}.
	 * 
	 * <p>Verify that the force disassociate command cannot be executed successfully on an XBee 
	 * device if the connection of the device is closed.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testForceDisassociateErrorConnectionClosed() throws XBeeException {
		// Throw an interface not open exception when trying to set any parameter.
		Mockito.doThrow(new InterfaceNotOpenException()).when(xbeeDevice).executeParameter(PARAMETER_DA);
		
		// Execute the force disassociate command.
		xbeeDevice.forceDisassociate();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#forceDisassociate()}.
	 * 
	 * <p>Verify that the force disassociate command cannot be executed successfully on an XBee 
	 * device if the operating mode of the device is not valid.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testForceDisassociateErrorInvalidOperatingMode() throws XBeeException {
		// Throw an invalid operating mode exception when trying to set any parameter.
		Mockito.doThrow(new InvalidOperatingModeException()).when(xbeeDevice).executeParameter(PARAMETER_DA);
		
		// Execute the force disassociate command.
		xbeeDevice.forceDisassociate();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#forceDisassociate()}.
	 * 
	 * <p>Verify that the force disassociate command cannot be executed successfully on an XBee 
	 * device if there is a timeout executing the command.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testForceDisassociateErrorTimeout() throws XBeeException, IOException {
		// Throw a timeout exception when trying to set the DA parameter.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).executeParameter(PARAMETER_DA);
		
		// Execute the force disassociate command.
		xbeeDevice.forceDisassociate();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#forceDisassociate()}.
	 * 
	 * <p>Verify that the force disassociate command cannot be executed successfully on an XBee 
	 * device if the answer when executing the command is null or the response status is not OK. 
	 * It is, there is an AT command exception executing the command.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testForceDisassociateErrorInvalidAnswer() throws XBeeException, IOException {
		// Throw an AT command exception when trying to set the DA parameter.
		Mockito.doThrow(new ATCommandException(null)).when(xbeeDevice).executeParameter(PARAMETER_DA);
		
		// Execute the force disassociate command.
		xbeeDevice.forceDisassociate();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#forceDisassociate()}.
	 * 
	 * <p>Verify that the force disassociate command can be executed successfully.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testForceDisassociateSuccess() throws XBeeException, IOException {
		// Do nothing when trying to set the DA parameter.
		Mockito.doNothing().when(xbeeDevice).executeParameter(PARAMETER_DA);
		
		// Execute the force disassociate command.
		xbeeDevice.forceDisassociate();
	}
}
