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
import com.digi.xbee.api.models.XBee64BitAddress;

public class SetDestinationAddressTest {

	// Constants.
	private static final String PARAMETER_DH = "DH";
	private static final String PARAMETER_DL = "DL";
	
	private static final XBee64BitAddress ADDRESS = new XBee64BitAddress("013456789ABCDEF"); // 0x0123456789ABCDEF
	
	// Variables.
	private XBeeDevice xbeeDevice;
	
	@Before
	public void setup() throws Exception {
		// Instantiate a local XBee device object.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(Mockito.mock(SerialPortRxTx.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setDestinationAddress(XBee64BitAddress)}.
	 * 
	 * <p>Verify that the destination address of an XBee device cannot be set if the destination address 
	 * is null.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=NullPointerException.class)
	public void testSetDestinationAddressErrorNullAddress() throws XBeeException {
		// Set the destination address.
		xbeeDevice.setDestinationAddress(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setDestinationAddress(XBee64BitAddress)}.
	 * 
	 * <p>Verify that the destination address of an XBee device cannot be set if the connection of the 
	 * device is closed.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testSetDestinationAddressErrorConnectionClosed() throws XBeeException {
		// Throw an interface not open exception when trying to set any parameter.
		Mockito.doThrow(new InterfaceNotOpenException()).when(xbeeDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		// Set the destination address.
		xbeeDevice.setDestinationAddress(ADDRESS);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setDestinationAddress(XBee64BitAddress)}.
	 * 
	 * <p>Verify that the destination address of an XBee device cannot be set if the operating mode of the 
	 * device is not valid.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSetDestinationAddressErrorInvalidOperatingMode() throws XBeeException {
		// Throw an invalid operating mode exception when trying to set any parameter.
		Mockito.doThrow(new InvalidOperatingModeException()).when(xbeeDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		// Set the destination address.
		xbeeDevice.setDestinationAddress(ADDRESS);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setDestinationAddress(XBee64BitAddress)}.
	 * 
	 * <p>Verify that the destination address of an XBee device cannot be set if there is a timeout setting 
	 * the DH parameter of the address.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testSetDestinationAddressDHErrorTimeout() throws XBeeException, IOException {
		// Throw a timeout exception when trying to set the DH parameter.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).setParameter(Mockito.eq(PARAMETER_DH), Mockito.any(byte[].class));
		
		// Set the destination address.
		xbeeDevice.setDestinationAddress(ADDRESS);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setDestinationAddress(XBee64BitAddress)}.
	 * 
	 * <p>Verify that the destination address of an XBee device cannot be set if there is a timeout setting 
	 * the DL parameter of the address.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testSetDestinationAddressDLErrorTimeout() throws XBeeException, IOException {
		// Do nothing when trying to set the DH parameter.
		Mockito.doNothing().when(xbeeDevice).setParameter(Mockito.eq(PARAMETER_DH), Mockito.any(byte[].class));
		
		// Throw a timeout exception when trying to set the DL parameter.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).setParameter(Mockito.eq(PARAMETER_DL), Mockito.any(byte[].class));
		
		// Set the destination address.
		xbeeDevice.setDestinationAddress(ADDRESS);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setDestinationAddress(XBee64BitAddress)}.
	 * 
	 * <p>Verify that the destination address of an XBee device cannot be set if the answer when setting 
	 * the DH parameter is null or the response status is not OK. It is, there is an AT command exception 
	 * setting the DH parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testSetDestinationAddressDHErrorInvalidAnswer() throws XBeeException, IOException {
		// Throw an AT command exception when trying to set the DH parameter.
		Mockito.doThrow(new ATCommandException(null)).when(xbeeDevice).setParameter(Mockito.eq(PARAMETER_DH), Mockito.any(byte[].class));
		
		// Set the destination address.
		xbeeDevice.setDestinationAddress(ADDRESS);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setDestinationAddress(XBee64BitAddress)}.
	 * 
	 * <p>Verify that the destination address of an XBee device cannot be set if the answer when setting 
	 * the DL parameter is null or the response status is not OK. It is, there is an AT command exception 
	 * setting the DL parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testSetDestinationAddressDLErrorInvalidAnswer() throws XBeeException, IOException {
		// Do nothing when trying to set the DH parameter.
		Mockito.doNothing().when(xbeeDevice).setParameter(Mockito.eq(PARAMETER_DH), Mockito.any(byte[].class));
		
		// Throw an AT command exception when trying to set the DL parameter.
		Mockito.doThrow(new ATCommandException(null)).when(xbeeDevice).setParameter(Mockito.eq(PARAMETER_DL), Mockito.any(byte[].class));
		
		// Set the destination address.
		xbeeDevice.setDestinationAddress(ADDRESS);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setDestinationAddress(XBee64BitAddress)}.
	 * 
	 * <p>Verify that the destination address of an XBee device cannot be set if there is a timeout 
	 * exception when applying changes.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testSetDestinationAddressACErrorTimeout() throws XBeeException, IOException {
		// Do nothing when trying to set the DH parameter.
		Mockito.doNothing().when(xbeeDevice).setParameter(Mockito.eq(PARAMETER_DH), Mockito.any(byte[].class));
		
		// Do nothing when trying to set the DL parameter.
		Mockito.doNothing().when(xbeeDevice).setParameter(Mockito.eq(PARAMETER_DL), Mockito.any(byte[].class));
		
		// Throw a timeout exception when applying changes.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).applyChanges();
		
		// Set the destination address.
		xbeeDevice.setDestinationAddress(ADDRESS);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setDestinationAddress(XBee64BitAddress)}.
	 * 
	 * <p>Verify that the destination address of an XBee device cannot be set if there is an AT Command 
	 * exception applying the changes.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testSetDestinationAddressACErrorInvalidAnswer() throws XBeeException, IOException {
		// Do nothing when trying to set the DH parameter.
		Mockito.doNothing().when(xbeeDevice).setParameter(Mockito.eq(PARAMETER_DH), Mockito.any(byte[].class));
		
		// Do nothing when trying to set the DL parameter.
		Mockito.doNothing().when(xbeeDevice).setParameter(Mockito.eq(PARAMETER_DL), Mockito.any(byte[].class));
		
		// Throw an AT command exception when applying changes.
		Mockito.doThrow(new ATCommandException(null)).when(xbeeDevice).applyChanges();
		
		// Set the destination address.
		xbeeDevice.setDestinationAddress(ADDRESS);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#setDestinationAddress(XBee64BitAddress)}.
	 * 
	 * <p>Verify that the destination address of an XBee device can be set successfully.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testSetDestinationAddressSuccess() throws XBeeException, IOException {
		// Do nothing when trying to set the DH parameter.
		Mockito.doNothing().when(xbeeDevice).setParameter(Mockito.eq(PARAMETER_DH), Mockito.any(byte[].class));
		
		// Do nothing when trying to set the DL parameter.
		Mockito.doNothing().when(xbeeDevice).setParameter(Mockito.eq(PARAMETER_DL), Mockito.any(byte[].class));
		
		// Do nothing when applying changes.
		Mockito.doNothing().when(xbeeDevice).applyChanges();
		
		// Set the destination address.
		xbeeDevice.setDestinationAddress(ADDRESS);
	}
}
