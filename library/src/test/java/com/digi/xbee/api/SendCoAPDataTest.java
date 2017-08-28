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

import java.net.Inet6Address;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.OperationNotSupportedException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.TransmitException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.CoAPURI;
import com.digi.xbee.api.models.HTTPMethodEnum;
import com.digi.xbee.api.packet.thread.CoAPTxRequestPacket;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ThreadDevice.class})
public class SendCoAPDataTest {
	
	// Constants.
	private static final String IPV6_ADDRESS = "FDB3:0001:0002:0000:0004:0005:0006:0007";
	private static final byte[] DATA = "This is the CoAP Text".getBytes();
	
	// Variables.
	private ThreadDevice threadDevice;
	
	private CoAPTxRequestPacket coapTxPacket;
	
	private Inet6Address ipv6Address;
	
	@Before
	public void setup() throws Exception {
		ipv6Address = (Inet6Address) Inet6Address.getByName(IPV6_ADDRESS);
		
		// Instantiate a ThreadDevice object with a mocked interface.
		threadDevice = PowerMockito.spy(new ThreadDevice(Mockito.mock(SerialPortRxTx.class)));
		
		// Mock Transmit Request packet.
		coapTxPacket = Mockito.mock(CoAPTxRequestPacket.class);
		
		// Whenever a CoAPTxRequestPacket class is instantiated, the mocked coapTxPacket packet should be returned.
		PowerMockito.whenNew(CoAPTxRequestPacket.class).withAnyArguments().thenReturn(coapTxPacket);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendCoAPData(Inet6Address, String, HTTPMethodEnum, byte[])}.
	 * 
	 * <p>Verify that packet cannot be sent if the IPv6 address is {@code null}.</p>
	 * 
	 * @throws XBeeException
	 * @throws TimeoutException
	 * @throws IllegalArgumentException
	 */
	@Test(expected=NullPointerException.class)
	public void testSendCoAPDataAddressNull() throws TimeoutException, IllegalArgumentException, XBeeException {
		threadDevice.sendCoAPData(null, CoAPURI.URI_DATA_TRANSMISSION, 
				HTTPMethodEnum.PUT, DATA);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendCoAPData(Inet6Address, String, HTTPMethodEnum, byte[])}.
	 * 
	 * <p>Verify that packet cannot be sent if the Uri is {@code null}.</p>
	 * 
	 * @throws XBeeException
	 * @throws TimeoutException
	 * @throws IllegalArgumentException
	 */
	@Test(expected=NullPointerException.class)
	public void testSendCoAPDataUriNull() throws TimeoutException, IllegalArgumentException, XBeeException {
		threadDevice.sendCoAPData(ipv6Address, null, HTTPMethodEnum.PUT, DATA);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendCoAPData(Inet6Address, String, HTTPMethodEnum, byte[])}.
	 * 
	 * <p>Verify that packet cannot be sent if the HTTP Method is {@code null}.</p>
	 * 
	 * @throws XBeeException
	 * @throws TimeoutException
	 * @throws IllegalArgumentException
	 */
	@Test(expected=NullPointerException.class)
	public void testSendCoAPDataMethodNull() throws TimeoutException, IllegalArgumentException, XBeeException {
		threadDevice.sendCoAPData(ipv6Address, CoAPURI.URI_DATA_TRANSMISSION, 
				null, DATA);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendCoAPData(Inet6Address, String, HTTPMethodEnum, boolean, byte[])}.
	 * 
	 * <p>Verify that packet cannot be sent if the IPv6 address is {@code null}.</p>
	 * 
	 * @throws XBeeException
	 * @throws TimeoutException
	 * @throws IllegalArgumentException
	 */
	@Test(expected=NullPointerException.class)
	public void testSendCoAPDataAddressNullWithApplyChanges() 
			throws TimeoutException, IllegalArgumentException, XBeeException {
		threadDevice.sendCoAPData(null, CoAPURI.URI_DATA_TRANSMISSION, 
				HTTPMethodEnum.PUT, false, DATA);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendCoAPData(Inet6Address, String, HTTPMethodEnum, boolean, byte[])}.
	 * 
	 * <p>Verify that packet cannot be sent if the Uri is {@code null}.</p>
	 * 
	 * @throws XBeeException
	 * @throws TimeoutException
	 * @throws IllegalArgumentException
	 */
	@Test(expected=NullPointerException.class)
	public void testSendCoAPDataUriNullWithApplyChanges() 
			throws TimeoutException, IllegalArgumentException, XBeeException {
		threadDevice.sendCoAPData(ipv6Address, null, HTTPMethodEnum.PUT, false, DATA);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendCoAPData(Inet6Address, String, HTTPMethodEnum, boolean, byte[])}.
	 * 
	 * <p>Verify that packet cannot be sent if the HTTP Method is {@code null}.</p>
	 * 
	 * @throws XBeeException
	 * @throws TimeoutException
	 * @throws IllegalArgumentException
	 */
	@Test(expected=NullPointerException.class)
	public void testSendCoAPDataMethodNullWithApplyChanges() 
			throws TimeoutException, IllegalArgumentException, XBeeException {
		threadDevice.sendCoAPData(ipv6Address, CoAPURI.URI_DATA_TRANSMISSION, 
				null, false, DATA);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendCoAPData(Inet6Address, String, HTTPMethodEnum, byte[])}.
	 * 
	 * <p>Verify that packet cannot be sent if the Uri is incorrectly built.</p>
	 * 
	 * @throws XBeeException
	 * @throws TimeoutException
	 * @throws IllegalArgumentException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSendCoAPDataIllegalArgument() 
			throws TimeoutException, IllegalArgumentException, XBeeException {
		threadDevice.sendCoAPData(ipv6Address, CoAPURI.URI_AT_COMMAND + "/G", 
				HTTPMethodEnum.PUT, DATA);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendCoAPData(Inet6Address, String, HTTPMethodEnum, boolean, byte[])}.
	 * 
	 * <p>Verify that packet cannot be sent if the sender is a remote XBee device.</p>
	 * 
	 * @throws XBeeException
	 * @throws TimeoutException
	 * @throws IllegalArgumentException
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testSendCoAPDataFromRemoteDevices() 
			throws TimeoutException, IllegalArgumentException, XBeeException {
		// Return that the XBee device is remote when asked.
		Mockito.when(threadDevice.isRemote()).thenReturn(true);
		
		threadDevice.sendCoAPData(ipv6Address, CoAPURI.URI_DATA_TRANSMISSION, 
				HTTPMethodEnum.PUT, DATA);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendCoAPData(Inet6Address, String, HTTPMethodEnum, boolean, byte[])}.
	 * 
	 * <p>Verify that packet cannot be sent if the device is not open.</p>
	 * 
	 * @throws XBeeException
	 * @throws TimeoutException
	 * @throws IllegalArgumentException
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testSendCoAPDataConnectionClosed() 
			throws TimeoutException, IllegalArgumentException, XBeeException {
		// Throw an Interface not open exception when sending and checking any packet.
		Mockito.doThrow(new InterfaceNotOpenException()).when(threadDevice).sendAndCheckCoAPPacket(Mockito.any(CoAPTxRequestPacket.class), Mockito.eq(false));
		
		threadDevice.sendCoAPData(ipv6Address, CoAPURI.URI_DATA_TRANSMISSION, 
				HTTPMethodEnum.PUT, DATA);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendCoAPData(Inet6Address, String, HTTPMethodEnum, boolean, byte[])}.
	 * 
	 * <p>Verify that packet cannot be sent if the device has an invalid operating mode.</p>
	 * 
	 * @throws XBeeException
	 * @throws TimeoutException
	 * @throws IllegalArgumentException
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSendCoAPDataInvalidOperatingMode() 
			throws TimeoutException, IllegalArgumentException, XBeeException {
		// Throw an invalid operating mode exception when sending and checking any packet.
		Mockito.doThrow(new InvalidOperatingModeException()).when(threadDevice).sendAndCheckCoAPPacket(Mockito.any(CoAPTxRequestPacket.class), Mockito.eq(false));
		
		threadDevice.sendCoAPData(ipv6Address, CoAPURI.URI_DATA_TRANSMISSION, 
				HTTPMethodEnum.PUT, DATA);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendCoAPData(Inet6Address, String, HTTPMethodEnum, boolean, byte[])}.
	 * 
	 * <p>Verify that packet cannot be sent if there is a timeout sending and 
	 * checking the packet.</p>
	 * 
	 * @throws XBeeException
	 * @throws TimeoutException
	 * @throws IllegalArgumentException
	 */
	@Test(expected=TimeoutException.class)
	public void testSendCoAPDataTimeout() 
			throws TimeoutException, IllegalArgumentException, XBeeException {
		// Throw a timeout exception when sending and checking any packet.
		Mockito.doThrow(new TimeoutException()).when(threadDevice).sendAndCheckCoAPPacket(Mockito.any(CoAPTxRequestPacket.class), Mockito.eq(false));
		
		threadDevice.sendCoAPData(ipv6Address, CoAPURI.URI_DATA_TRANSMISSION, 
				HTTPMethodEnum.PUT, DATA);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendCoAPData(Inet6Address, String, HTTPMethodEnum, boolean, byte[])}.
	 * 
	 * <p>Verify that packet cannot be sent if there is a transmit exception 
	 * when sending and checking the packet.</p>
	 * 
	 * @throws XBeeException
	 * @throws TimeoutException
	 * @throws IllegalArgumentException
	 */
	@Test(expected=TransmitException.class)
	public void testSendCoAPDataTransmitException() 
			throws TimeoutException, IllegalArgumentException, XBeeException {
		// Throw a transmit exception when sending and checking any packet.
		Mockito.doThrow(new TransmitException(null)).when(threadDevice).sendAndCheckCoAPPacket(Mockito.any(CoAPTxRequestPacket.class), Mockito.eq(false));
		
		threadDevice.sendCoAPData(ipv6Address, CoAPURI.URI_DATA_TRANSMISSION, 
				HTTPMethodEnum.PUT, DATA);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendCoAPData(Inet6Address, String, HTTPMethodEnum, boolean, byte[])}.
	 * 
	 * <p>Verify that packet cannot be sent if there is an IO error when 
	 * sending and checking the packet.</p>
	 * 
	 * @throws XBeeException
	 * @throws TimeoutException
	 * @throws IllegalArgumentException
	 */
	@Test(expected=XBeeException.class)
	public void testSendCoAPDataIOException() 
			throws TimeoutException, IllegalArgumentException, XBeeException {
		// Throw an XBee exception when sending and checking any packet.
		Mockito.doThrow(new XBeeException()).when(threadDevice).sendAndCheckCoAPPacket(Mockito.any(CoAPTxRequestPacket.class), Mockito.eq(false));
		
		threadDevice.sendCoAPData(ipv6Address, CoAPURI.URI_DATA_TRANSMISSION, 
				HTTPMethodEnum.PUT, DATA);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendCoAPData(Inet6Address, String, HTTPMethodEnum, boolean, byte[])}.
	 * 
	 * <p>Verify that packet is sent successfully if there is not any error 
	 * when sending and checking the packet.</p>
	 * 
	 * @throws XBeeException
	 * @throws TimeoutException
	 * @throws IllegalArgumentException
	 */
	@Test
	public void testSendCoAPDataSuccess() 
			throws TimeoutException, IllegalArgumentException, XBeeException {
		// Do nothing when sending and checking any packet.
		Mockito.doReturn(new byte[]{0x00}).when(threadDevice).sendAndCheckCoAPPacket(Mockito.any(CoAPTxRequestPacket.class), Mockito.eq(false));
		
		threadDevice.sendCoAPData(ipv6Address, CoAPURI.URI_DATA_TRANSMISSION, 
				HTTPMethodEnum.PUT, DATA);
		
		// Verify the sendAndCheckCoAPPacket(CoAPTxRequestPacket, boolean) method was called.
		Mockito.verify(threadDevice, Mockito.times(1)).sendAndCheckCoAPPacket(Mockito.eq(coapTxPacket), Mockito.eq(false));
	}
}
