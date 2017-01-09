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

public class SetPANIDTest {

	// Constants.
	private static final String PARAMETER_ID = "ID";
	
	private static final byte[] PAN_ID = new byte[]{0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF};
	
	// Variables.
	private XBeeDevice xbeeDevice;
	
	@Before
	public void setup() throws Exception {
		// Instantiate a local XBeeDevice object.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(Mockito.mock(SerialPortRxTx.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setPANID(byte[])}.
	 * 
	 * <p>Verify that the PAN ID of an XBee device cannot be set if the PAN ID provided 
	 * is null.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=NullPointerException.class)
	public void testSetPANIDErrorNullPANID() throws XBeeException {
		// Set the PAN ID.
		xbeeDevice.setPANID(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setPANID(byte[])}.
	 * 
	 * <p>Verify that the PAN ID of an XBee device cannot be set if the length of the 
	 * PAN ID provided is 0.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetPANIDErrorPANIDLengthZero() throws XBeeException {
		// Set the PAN ID.
		xbeeDevice.setPANID(new byte[0]);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setPANID(byte[])}.
	 * 
	 * <p>Verify that the PAN ID of an XBee device cannot be set if the length of the 
	 * PAN ID provided is longer than 8 bytes.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetPANIDErrorPANIDLengthLonger() throws XBeeException {
		// Set the PAN ID.
		xbeeDevice.setPANID(new byte[9]);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setPANID(byte[])}.
	 * 
	 * <p>Verify that the PAN ID of an XBee device cannot be set if the connection of the 
	 * device is closed.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testSetPANIDErrorConnectionClosed() throws XBeeException {
		// Throw an interface not open exception when trying to set any parameter.
		Mockito.doThrow(new InterfaceNotOpenException()).when(xbeeDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		// Set the PAN ID.
		xbeeDevice.setPANID(PAN_ID);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setPANID(byte[])}.
	 * 
	 * <p>Verify that the PAN ID of an XBee device cannot be set if the operating mode of the 
	 * device is not valid.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSetPANIDErrorInvalidOperatingMode() throws XBeeException {
		// Throw an invalid operating mode exception when trying to set any parameter.
		Mockito.doThrow(new InvalidOperatingModeException()).when(xbeeDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		// Set the PAN ID.
		xbeeDevice.setPANID(PAN_ID);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setPANID(byte[])}.
	 * 
	 * <p>Verify that the PAN ID of an XBee device cannot be set if there is a timeout setting 
	 * the PAN ID of the device.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testSetPANIDErrorTimeout() throws XBeeException, IOException {
		// Throw a timeout exception when trying to set the ID parameter.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).setParameter(Mockito.eq(PARAMETER_ID), Mockito.any(byte[].class));
		
		// Set the PAN ID.
		xbeeDevice.setPANID(PAN_ID);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setPANID(byte[])}.
	 * 
	 * <p>Verify that the PAN ID of an XBee device cannot be set if the answer when setting 
	 * the PAN ID is null or the response status is not OK. It is, there is an AT command exception 
	 * setting the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testSetPANIDErrorInvalidAnswer() throws XBeeException, IOException {
		// Throw an AT command exception when trying to set the ID parameter.
		Mockito.doThrow(new ATCommandException(null)).when(xbeeDevice).setParameter(Mockito.eq(PARAMETER_ID), Mockito.any(byte[].class));
		
		// Set the PAN ID.
		xbeeDevice.setPANID(PAN_ID);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setPANID(byte[])}.
	 * 
	 * <p>Verify that the PAN ID of an XBee device can be set successfully.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testSetPANIDSuccess() throws XBeeException, IOException {
		// Do nothing when trying to set the ID parameter.
		Mockito.doNothing().when(xbeeDevice).setParameter(Mockito.eq(PARAMETER_ID), Mockito.any(byte[].class));
		
		// Set the PAN ID.
		xbeeDevice.setPANID(PAN_ID);
	}
}
