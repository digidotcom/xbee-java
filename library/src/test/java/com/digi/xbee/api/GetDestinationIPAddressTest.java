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

import java.net.Inet4Address;
import java.net.UnknownHostException;

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

public class GetDestinationIPAddressTest {

	// Constants.
	private static final String PARAMETER = "DL";

	private static final byte[] RESPONSE = new byte[]{(byte)0xC0, (byte)0xA8, (byte)0x01, (byte)0x02}; // 192 168 1 2

	private static Inet4Address DESTINATION_ADDRESS;

	// Variables.
	private WiFiDevice xbeeDevice;

	@Before
	public void setup() throws Exception {
		DESTINATION_ADDRESS = (Inet4Address) Inet4Address.getByAddress(RESPONSE);

		// Instantiate a local Wi-Fi device object.
		xbeeDevice = PowerMockito.spy(new WiFiDevice(Mockito.mock(SerialPortRxTx.class)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#getDestinationIPAddress()}.
	 *
	 * <p>Verify that the destination IP address of an XBee device cannot be
	 * gotten if the connection of the device is closed.</p>
	 *
	 * @throws XBeeException
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testGetDestinationIPAddressErrorConnectionClosed() throws XBeeException {
		// Throw an interface not open exception when trying to get any parameter.
		Mockito.doThrow(new InterfaceNotOpenException()).when(xbeeDevice).getParameter(Mockito.anyString());

		// Get the destination IP address of the device.
		xbeeDevice.getDestinationIPAddress();
	}

	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#getDestinationIPAddress()}.
	 *
	 * <p>Verify that the destination IP address of an XBee device cannot be
	 * gotten if the operating mode of the device is not valid.</p>
	 *
	 * @throws XBeeException
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testGetDestinationIPAddressErrorInvalidOperatingMode() throws XBeeException {
		// Throw an invalid operating mode exception when trying to get any parameter.
		Mockito.doThrow(new InvalidOperatingModeException()).when(xbeeDevice).getParameter(Mockito.anyString());

		// Get the destination IP address of the device.
		xbeeDevice.getDestinationIPAddress();
	}

	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#getDestinationIPAddress()}.
	 *
	 * <p>Verify that the destination IP address of an XBee device cannot be
	 * gotten if there is a timeout getting the DL parameter.</p>
	 *
	 * @throws XBeeException
	 */
	@Test(expected=TimeoutException.class)
	public void testGetDestinationIPAddressErrorTimeout() throws XBeeException {
		// Throw a timeout exception when trying to get the DL parameter.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).getParameter(PARAMETER);

		// Get the destination IP address of the device.
		xbeeDevice.getDestinationIPAddress();
	}

	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#getDestinationIPAddress()}.
	 *
	 * <p>Verify that the destination IP address of an XBee device cannot be
	 * gotten if the answer when getting the DL parameter is null or the
	 * response status is not OK. It is, there is an AT command exception
	 * getting the DL parameter.</p>
	 *
	 * @throws XBeeException
	 */
	@Test(expected=ATCommandException.class)
	public void testGetDestinationIPAddressErrorInvalidAnswer() throws XBeeException {
		// Throw an AT command exception when trying to get the DL parameter.
		Mockito.doThrow(new ATCommandException(null)).when(xbeeDevice).getParameter(PARAMETER);

		// Get the destination IP address of the device.
		xbeeDevice.getDestinationIPAddress();
	}

	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#getDestinationIPAddress()}.
	 *
	 * <p>Verify that the destination IP address of an XBee device cannot be
	 * gotten if the returned IP is illegal. It is, there is an XBee exception
	 * getting the DL parameter.</p>
	 *
	 * @throws XBeeException
	 */
	@Test
	public void testGetDestinationIPAddressIllegalIP() throws XBeeException {
		// Return an illegal IP address when getting the DL parameter.
		Mockito.doReturn(new byte[]{0x00}).when(xbeeDevice).getParameter(PARAMETER);

		// Get the destination IP address of the device.
		try {
			xbeeDevice.getDestinationIPAddress();
			fail();
		} catch (XBeeException e) {
			assertEquals("Exception is not caused by an UnknownHostException", e.getCause().getClass(), UnknownHostException.class);
		}
	}

	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#getDestinationIPAddress()}.
	 *
	 * <p>Verify that the destination IP address of an XBee device can be gotten
	 * successfully.</p>
	 *
	 * @throws XBeeException
	 */
	@Test
	public void testGetDestinationIPAddressSuccess() throws XBeeException {
		// Return a valid value when getting the DL parameter.
		Mockito.doReturn(RESPONSE).when(xbeeDevice).getParameter(PARAMETER);

		// Get the destination IP address of the device.
		assertEquals(DESTINATION_ADDRESS, xbeeDevice.getDestinationIPAddress());
	}
}
