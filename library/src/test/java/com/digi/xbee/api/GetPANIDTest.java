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
import com.digi.xbee.api.models.XBeeProtocol;

public class GetPANIDTest {

	// Constants.
	private static final String PARAMETER_ID = "ID";
	private static final String PARAMETER_OP = "OP";
	
	private static final byte[] RESPONSE_ID = new byte[]{0x01, 0x23, 0x45, 0x67, 0x76, 0x54, 0x32, 0x10}; // 0x0123456776543210
	private static final byte[] RESPONSE_OP = new byte[]{0x76, 0x54, 0x32, 0x10, 0x01, 0x23, 0x45, 0x67}; // 0x7654321001234567
	
	// Variables.
	private XBeeDevice xbeeDevice;
	
	@Before
	public void setup() throws Exception {
		// Instantiate a local XBeeDevice object.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(Mockito.mock(SerialPortRxTx.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getPANID()}.
	 * 
	 * <p>Verify that the PAN ID of an XBee device cannot be get if the connection of the 
	 * device is closed.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testGetPANIDErrorConnectionClosed() throws XBeeException {
		// Throw an interface not open exception when trying to get any parameter.
		Mockito.doThrow(new InterfaceNotOpenException()).when(xbeeDevice).getParameter(Mockito.anyString());
		
		// Get the PAN ID.
		xbeeDevice.getPANID();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getPANID()}.
	 * 
	 * <p>Verify that the PAN ID of an XBee device cannot be get if the operating mode of the 
	 * device is not valid.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testGetPANIDErrorInvalidOperatingMode() throws XBeeException {
		// Throw an invalid operating mode exception when trying to get any parameter.
		Mockito.doThrow(new InvalidOperatingModeException()).when(xbeeDevice).getParameter(Mockito.anyString());
		
		// Get the PAN ID.
		xbeeDevice.getPANID();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getPANID()}.
	 * 
	 * <p>Verify that the PAN ID of an XBee device cannot be get if there is a timeout getting 
	 * the PAN ID.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testGetPANIDErrorTimeout() throws XBeeException, IOException {
		// Throw a timeout exception when trying to get the ID parameter.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).getParameter(PARAMETER_ID);
		
		// Get the PAN ID.
		xbeeDevice.getPANID();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getPANID()}.
	 * 
	 * <p>Verify that the PAN ID of an XBee device cannot be get if the answer when getting 
	 * the PAN ID is null or the response status is not OK. It is, there is an AT command exception 
	 * getting the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testGetPANIDErrorInvalidAnswer() throws XBeeException, IOException {
		// Throw an AT command exception when trying to get the ID parameter.
		Mockito.doThrow(new ATCommandException(null)).when(xbeeDevice).getParameter(PARAMETER_ID);
		
		// Get the PAN ID.
		xbeeDevice.getPANID();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getPANID()}.
	 * 
	 * <p>Verify that the PAN ID of an XBee device can be get successfully.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testGetPANIDSuccess() throws XBeeException, IOException {
		// Return a valid ID value when getting the ID parameter.
		Mockito.doReturn(RESPONSE_ID).when(xbeeDevice).getParameter(PARAMETER_ID);
		
		// Get the PAN ID.
		assertArrayEquals(RESPONSE_ID, xbeeDevice.getPANID());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getPANID()}.
	 * 
	 * <p>Verify that the PAN ID of a ZigBee device cannot be get if there is a timeout getting 
	 * the PAN ID.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testGetPANIDZigBeeErrorTimeout() throws XBeeException, IOException {
		// Return that the device protocol is ZigBee when asked.
		Mockito.doReturn(XBeeProtocol.ZIGBEE).when(xbeeDevice).getXBeeProtocol();
		
		// Throw a timeout exception when trying to get the OP parameter.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).getParameter(PARAMETER_OP);
		
		// Get the PAN ID.
		xbeeDevice.getPANID();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getPANID()}.
	 * 
	 * <p>Verify that the PAN ID of a ZigBee device cannot be get if the answer when getting 
	 * the PAN ID is null or the response status is not OK. It is, there is an AT command exception 
	 * getting the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testGetPANIDZigBeeErrorInvalidAnswer() throws XBeeException, IOException {
		// Return that the device protocol is ZigBee when asked.
		Mockito.doReturn(XBeeProtocol.ZIGBEE).when(xbeeDevice).getXBeeProtocol();
		
		// Throw an AT command exception when trying to get the OP parameter.
		Mockito.doThrow(new ATCommandException(null)).when(xbeeDevice).getParameter(PARAMETER_OP);
		
		// Get the PAN ID.
		xbeeDevice.getPANID();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getPANID()}.
	 * 
	 * <p>Verify that the PAN ID of a ZigBee device can be get successfully.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testGetPANIDZigBeeSuccess() throws XBeeException, IOException {
		// Return that the device protocol is ZigBee when asked.
		Mockito.doReturn(XBeeProtocol.ZIGBEE).when(xbeeDevice).getXBeeProtocol();
		
		// Return a valid OP value when getting the OP parameter.
		Mockito.doReturn(RESPONSE_OP).when(xbeeDevice).getParameter(PARAMETER_OP);
		
		// Get the PAN ID.
		assertArrayEquals(RESPONSE_OP, xbeeDevice.getPANID());
	}
}
