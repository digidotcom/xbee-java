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

public class SetNodeIDTest {

	// Constants.
	private static final String PARAMETER_NI = "NI";
	private static final String VALUE_NI_OK = "This name is OK";
	private static final String VALUE_NI_INVALID = "This name is so long that it is not supported";
	
	// Variables.
	private XBeeDevice xbeeDevice;
	
	@Before
	public void setup() throws Exception {
		// Instantiate a local XBeeDevice object.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(Mockito.mock(SerialPortRxTx.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setNodeID(String)}.
	 * 
	 * <p>Verify that the node ID of an XBee device cannot be set if the node ID provided 
	 * is null.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=NullPointerException.class)
	public void testSetNodeIDErrorNullNodeID() throws XBeeException {
		// Set the node ID of the device.
		xbeeDevice.setNodeID(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setNodeID(String)}.
	 * 
	 * <p>Verify that the node ID of an XBee device cannot be set if the node ID provided 
	 * is invalid.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetNodeIDErrorIllegalNodeID() throws XBeeException {
		// Set the node ID of the device.
		xbeeDevice.setNodeID(VALUE_NI_INVALID);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setNodeID(String)}.
	 * 
	 * <p>Verify that the node ID of an XBee device cannot be set if the connection of the 
	 * device is closed.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testSetNodeIDErrorConnectionClosed() throws XBeeException {
		// Throw an interface not open exception when trying to set any parameter.
		Mockito.doThrow(new InterfaceNotOpenException()).when(xbeeDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		// Set the node ID of the device.
		xbeeDevice.setNodeID(VALUE_NI_OK);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setNodeID(String)}.
	 * 
	 * <p>Verify that the node ID of an XBee device cannot be set if the operating mode of the 
	 * device is not valid.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSetNodeIDErrorInvalidOperatingMode() throws XBeeException {
		// Throw an invalid operating mode exception when trying to set any parameter.
		Mockito.doThrow(new InvalidOperatingModeException()).when(xbeeDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		// Set the node ID of the device.
		xbeeDevice.setNodeID(VALUE_NI_OK);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setNodeID(String)}.
	 * 
	 * <p>Verify that the node ID of an XBee device cannot be set if there is a timeout setting 
	 * the node ID of the device.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testSetNodeIDErrorTimeout() throws XBeeException, IOException {
		// Throw a timeout exception when trying to set the NI parameter.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).setParameter(Mockito.eq(PARAMETER_NI), Mockito.any(byte[].class));
		
		// Set the node ID of the device.
		xbeeDevice.setNodeID(VALUE_NI_OK);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setNodeID(String)}.
	 * 
	 * <p>Verify that the node ID of an XBee device cannot be set if the answer when setting 
	 * the node ID is null or the response status is not OK. It is, there is an AT command exception 
	 * setting the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testSetNodeIDErrorInvalidAnswer() throws XBeeException, IOException {
		// Throw an AT command exception when trying to set the NI parameter.
		Mockito.doThrow(new ATCommandException(null)).when(xbeeDevice).setParameter(Mockito.eq(PARAMETER_NI), Mockito.any(byte[].class));
		
		// Set the node ID of the device.
		xbeeDevice.setNodeID(VALUE_NI_OK);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setNodeID(String)}.
	 * 
	 * <p>Verify that the node ID of an XBee device can be set successfully.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testSetNodeIDSuccess() throws XBeeException, IOException {
		// Do nothing when trying to set the NI parameter.
		Mockito.doNothing().when(xbeeDevice).setParameter(Mockito.eq(PARAMETER_NI), Mockito.any(byte[].class));
		
		// Set the node ID of the device.
		xbeeDevice.setNodeID(VALUE_NI_OK);
	}
}
