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

import java.net.Inet6Address;
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

public class GetDestinationIPv6AddressTest {

	// Constants.
	private static final String PARAMETER = "DL";

	private static final byte[] RESPONSE = new byte[]{(byte)0xFD, (byte)0xB3, 
			(byte)0x00, (byte)0x01, (byte)0x00, (byte)0x02, (byte)0x00, (byte)0x00, 
			(byte)0x00, (byte)0x04, (byte)0x00, (byte)0x05, (byte)0x00, (byte)0x06, 
			(byte)0x00, (byte)0x07}; // FDB3:0001:0002:0000:0004:0005:0006:0007

	private static Inet6Address DESTINATION_ADDRESS;

	// Variables.
	private ThreadDevice xbeeDevice;

	@Before
	public void setup() throws Exception {
		DESTINATION_ADDRESS = (Inet6Address) Inet6Address.getByAddress(RESPONSE);

		// Instantiate a local Thread device object.
		xbeeDevice = PowerMockito.spy(new ThreadDevice(Mockito.mock(SerialPortRxTx.class)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.AbstractXBeeDevice#getIPv6DestinationAddress()}.
	 *
	 * <p>Verify that the destination IPv6 address of an XBee device cannot be
	 * gotten if the connection of the device is closed.</p>
	 *
	 * @throws XBeeException
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testGetDestinationIPv6AddressErrorConnectionClosed() throws XBeeException {
		// Throw an interface not open exception when trying to get any parameter.
		Mockito.doThrow(new InterfaceNotOpenException()).when(xbeeDevice).getParameter(Mockito.anyString());

		// Get the destination IPv6 address of the device.
		xbeeDevice.getIPv6DestinationAddress();
	}

	/**
	 * Test method for {@link com.digi.xbee.api.AbstractXBeeDevice#getIPv6DestinationAddress()}.
	 *
	 * <p>Verify that the destination IPv6 address of an XBee device cannot be
	 * gotten if the operating mode of the device is not valid.</p>
	 *
	 * @throws XBeeException
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testGetDestinationIPv6AddressErrorInvalidOperatingMode() throws XBeeException {
		// Throw an invalid operating mode exception when trying to get any parameter.
		Mockito.doThrow(new InvalidOperatingModeException()).when(xbeeDevice).getParameter(Mockito.anyString());

		// Get the destination IPv6 address of the device.
		xbeeDevice.getIPv6DestinationAddress();
	}

	/**
	 * Test method for {@link com.digi.xbee.api.AbstractXBeeDevice#getIPv6DestinationAddress()}.
	 *
	 * <p>Verify that the destination IPv6 address of an XBee device cannot be
	 * gotten if there is a timeout getting the DL parameter.</p>
	 *
	 * @throws XBeeException
	 */
	@Test(expected=TimeoutException.class)
	public void testGetDestinationIPv6AddressErrorTimeout() throws XBeeException {
		// Throw a timeout exception when trying to get the DL parameter.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).getParameter(PARAMETER);

		// Get the destination IPv6 address of the device.
		xbeeDevice.getIPv6DestinationAddress();
	}

	/**
	 * Test method for {@link com.digi.xbee.api.AbstractXBeeDevice#getIPv6DestinationAddress()}.
	 *
	 * <p>Verify that the destination IPv6 address of an XBee device cannot be
	 * gotten if the answer when getting the DL parameter is null or the
	 * response status is not OK. It is, there is an AT command exception
	 * getting the DL parameter.</p>
	 *
	 * @throws XBeeException
	 */
	@Test(expected=ATCommandException.class)
	public void testGetDestinationIPv6AddressErrorInvalidAnswer() throws XBeeException {
		// Throw an AT command exception when trying to get the DL parameter.
		Mockito.doThrow(new ATCommandException(null)).when(xbeeDevice).getParameter(PARAMETER);

		// Get the destination IPv6 address of the device.
		xbeeDevice.getIPv6DestinationAddress();
	}

	/**
	 * Test method for {@link com.digi.xbee.api.AbstractXBeeDevice#getIPv6DestinationAddress()}.
	 *
	 * <p>Verify that the destination IPv6 address of an XBee device cannot be
	 * gotten if the returned IPv6 is illegal. It is, there is an XBee exception
	 * getting the DL parameter.</p>
	 *
	 * @throws XBeeException
	 */
	@Test
	public void testGetDestinationIPv6AddressIllegalIPv6() throws XBeeException {
		// Return an illegal IPv6 address when getting the DL parameter.
		Mockito.doReturn(new byte[]{0x00}).when(xbeeDevice).getParameter(PARAMETER);

		// Get the destination IPv6 address of the device.
		try {
			xbeeDevice.getIPv6DestinationAddress();
			fail();
		} catch (XBeeException e) {
			assertEquals("Exception is not caused by an UnknownHostException", e.getCause().getClass(), UnknownHostException.class);
		}
	}

	/**
	 * Test method for {@link com.digi.xbee.api.AbstractXBeeDevice#getIPv6DestinationAddress()}.
	 *
	 * <p>Verify that the destination IPv6 address of an XBee device can be gotten
	 * successfully.</p>
	 *
	 * @throws XBeeException
	 */
	@Test
	public void testGetDestinationIPv6AddressSuccess() throws XBeeException {
		// Return a valid value when getting the DL parameter.
		Mockito.doReturn(RESPONSE).when(xbeeDevice).getParameter(PARAMETER);

		// Get the destination IPv6 address of the device.
		assertEquals(DESTINATION_ADDRESS, xbeeDevice.getIPv6DestinationAddress());
	}
}
