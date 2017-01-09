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
import com.digi.xbee.api.models.XBee64BitAddress;

public class GetDestinationAddressTest {

	// Constants.
	private static final String PARAMETER_DH = "DH";
	private static final String PARAMETER_DL = "DL";
	
	private static final byte[] RESPONSE_DH = new byte[]{0x01, 0x23, 0x45, 0x67};                         // 0x01234567
	private static final byte[] RESPONSE_DL = new byte[]{(byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF}; // 0x89ABCDEF
	
	private static final XBee64BitAddress DESTINATION_ADDRESS = new XBee64BitAddress("0123456789ABCDEF");
	
	// Variables.
	private XBeeDevice xbeeDevice;
	
	@Before
	public void setup() throws Exception {
		// Instantiate a local XBeeDevice object.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(Mockito.mock(SerialPortRxTx.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getDestinationAddress()}.
	 * 
	 * <p>Verify that the destination address of an XBee device cannot be get if the connection of the 
	 * device is closed.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testGetDestinationAddressErrorConnectionClosed() throws XBeeException {
		// Throw an interface not open exception when trying to get any parameter.
		Mockito.doThrow(new InterfaceNotOpenException()).when(xbeeDevice).getParameter(Mockito.anyString());
		
		// Get the destination address of the device.
		xbeeDevice.getDestinationAddress();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getDestinationAddress()}.
	 * 
	 * <p>Verify that the destination address of an XBee device cannot be get if the operating mode of the 
	 * device is not valid.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testGetDestinationAddressErrorInvalidOperatingMode() throws XBeeException {
		// Throw an invalid operating mode exception when trying to get any parameter.
		Mockito.doThrow(new InvalidOperatingModeException()).when(xbeeDevice).getParameter(Mockito.anyString());
		
		// Get the destination address of the device.
		xbeeDevice.getDestinationAddress();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getDestinationAddress()}.
	 * 
	 * <p>Verify that the destination address of an XBee device cannot be get if there is a timeout getting 
	 * the DH parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testGetDestinationAddressDHErrorTimeout() throws XBeeException, IOException {
		// Throw a timeout exception when trying to get the DH parameter.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).getParameter(PARAMETER_DH);
		
		// Get the destination address of the device.
		xbeeDevice.getDestinationAddress();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getDestinationAddress()}.
	 * 
	 * <p>Verify that the destination address of an XBee device cannot be get if there is a timeout getting 
	 * the DL parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testGetDestinationAddressDLErrorTimeout() throws XBeeException, IOException {
		// Return a valid value when getting the DH parameter.
		Mockito.doReturn(RESPONSE_DH).when(xbeeDevice).getParameter(PARAMETER_DH);
		
		// Throw a timeout exception when trying to get the DL parameter.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).getParameter(PARAMETER_DL);
		
		// Get the destination address of the device.
		xbeeDevice.getDestinationAddress();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getDestinationAddress()}.
	 * 
	 * <p>Verify that the destination address of an XBee device cannot be get if the answer when getting 
	 * the DH parameter is null or the response status is not OK. It is, there is an AT command exception 
	 * getting the DH parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testGetDestinationAddressDHErrorInvalidAnswer() throws XBeeException, IOException {
		// Throw an AT command exception when trying to get the DH parameter.
		Mockito.doThrow(new ATCommandException(null)).when(xbeeDevice).getParameter(PARAMETER_DH);
		
		// Get the destination address of the device.
		xbeeDevice.getDestinationAddress();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getDestinationAddress()}.
	 * 
	 * <p>Verify that the destination address of an XBee device cannot be get if the answer when getting 
	 * the DL parameter is null or the response status is not OK. It is, there is an AT command exception 
	 * getting the DL parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testGetDestinationAddressDLErrorInvalidAnswer() throws XBeeException, IOException {
		// Return a valid value when getting the DH parameter.
		Mockito.doReturn(RESPONSE_DH).when(xbeeDevice).getParameter(PARAMETER_DH);
		
		// Throw an AT command exception when trying to get the DL parameter.
		Mockito.doThrow(new ATCommandException(null)).when(xbeeDevice).getParameter(PARAMETER_DL);
		
		// Get the destination address of the device.
		xbeeDevice.getDestinationAddress();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getDestinationAddress()}.
	 * 
	 * <p>Verify that the destination address of an XBee device can be get successfully.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testGetDestinationAddressSuccess() throws XBeeException, IOException {
		// Return a valid value when getting the DH parameter.
		Mockito.doReturn(RESPONSE_DH).when(xbeeDevice).getParameter(PARAMETER_DH);
		
		// Return a valid value when getting the DL parameter.
		Mockito.doReturn(RESPONSE_DL).when(xbeeDevice).getParameter(PARAMETER_DL);
		
		// Get the destination address of the device.
		assertEquals(DESTINATION_ADDRESS, xbeeDevice.getDestinationAddress());
	}
}
