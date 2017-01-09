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
import com.digi.xbee.api.models.XBee16BitAddress;

public class Set16BitAddressTest {

	// Constants.
	private static final String PARAMETER_MY = "MY";
	
	private static final XBee16BitAddress ADDRESS = new XBee16BitAddress(new byte[]{0x01, 0x23}); // 0x0123
	
	// Variables.
	private XBeeDevice xbeeDevice;
	
	@Before
	public void setup() throws Exception {
		// Instantiate a local XBee device object.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(Mockito.mock(SerialPortRxTx.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#set16BitAddress(XBee16BitAddress)}.
	 * 
	 * <p>Verify that the 16-bit address of an XBee device cannot be set if the 16-bit address 
	 * is null.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=NullPointerException.class)
	public void testSetAddressErrorNullAddress() throws XBeeException {
		// Set the address.
		xbeeDevice.set16BitAddress(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#set16BitAddress(XBee16BitAddress)}.
	 * 
	 * <p>Verify that the 16-bit address of an XBee device cannot be set if the connection of the 
	 * device is closed.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testSetAddressErrorConnectionClosed() throws XBeeException {
		// Throw an interface not open exception when trying to set any parameter.
		Mockito.doThrow(new InterfaceNotOpenException()).when(xbeeDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		// Set the address.
		xbeeDevice.set16BitAddress(ADDRESS);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#set16BitAddress(XBee16BitAddress)}.
	 * 
	 * <p>Verify that the 16-bit address of an XBee device cannot be set if the operating mode of the 
	 * device is not valid.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSetAddressErrorInvalidOperatingMode() throws XBeeException {
		// Throw an invalid operating mode exception when trying to set any parameter.
		Mockito.doThrow(new InvalidOperatingModeException()).when(xbeeDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		// Set the address.
		xbeeDevice.set16BitAddress(ADDRESS);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#set16BitAddress(XBee16BitAddress)}.
	 * 
	 * <p>Verify that the 16-bit address of an XBee device cannot be set if there is a timeout setting 
	 * the 16-bit address of the device.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testSetAddressErrorTimeout() throws XBeeException, IOException {
		// Throw a timeout exception when trying to set the MY parameter.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).setParameter(Mockito.eq(PARAMETER_MY), Mockito.any(byte[].class));
		
		// Set the address.
		xbeeDevice.set16BitAddress(ADDRESS);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#set16BitAddress(XBee16BitAddress)}.
	 * 
	 * <p>Verify that the 16-bit address of an XBee device cannot be set if the answer when setting 
	 * the address is null or the response status is not OK. It is, there is an AT command exception 
	 * setting the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testSetAddressErrorInvalidAnswer() throws XBeeException, IOException {
		// Throw an AT command exception when trying to set the MY parameter.
		Mockito.doThrow(new ATCommandException(null)).when(xbeeDevice).setParameter(Mockito.eq(PARAMETER_MY), Mockito.any(byte[].class));
		
		// Set the address.
		xbeeDevice.set16BitAddress(ADDRESS);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#set16BitAddress(XBee16BitAddress)}.
	 * 
	 * <p>Verify that the 16-bit address of an XBee device can be set successfully.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testSetAddressSuccess() throws XBeeException, IOException {
		// Do nothing when trying to set the MY parameter.
		Mockito.doNothing().when(xbeeDevice).setParameter(Mockito.eq(PARAMETER_MY), Mockito.any(byte[].class));
		
		// Set the address.
		xbeeDevice.set16BitAddress(ADDRESS);
		
		assertEquals(ADDRESS, xbeeDevice.get16BitAddress());
	}
}
