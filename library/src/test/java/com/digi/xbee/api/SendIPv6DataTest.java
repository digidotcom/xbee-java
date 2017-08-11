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
import com.digi.xbee.api.models.IPProtocol;
import com.digi.xbee.api.packet.thread.TXIPv6Packet;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IPv6Device.class})
public class SendIPv6DataTest {
	
	// Constants.
	private static final String IPV6_ADDRESS = "FDB3:0001:0002:0000:0004:0005:0006:0007";
	
	private static final IPProtocol PROTOCOL = IPProtocol.TCP;
	
	private static final int PORT = 12345;
	
	private static final String DATA = "data";
	
	// Variables.
	private IPv6Device ipv6Device;
	
	private TXIPv6Packet txIPv6Packet;
	
	private Inet6Address ipv6Address;
	
	@Before
	public void setup() throws Exception {
		ipv6Address = (Inet6Address) Inet6Address.getByName(IPV6_ADDRESS);
		
		// Instantiate a IPv6Device object with a mocked interface.
		ipv6Device = PowerMockito.spy(new IPv6Device(Mockito.mock(SerialPortRxTx.class)));
		
		// Mock TX IPv6 packet.
		txIPv6Packet = Mockito.mock(TXIPv6Packet.class);
		
		// Whenever a TXIPv6Packet class is instantiated, the mocked txIPv6Packet packet should be returned.
		PowerMockito.whenNew(TXIPv6Packet.class).withAnyArguments().thenReturn(txIPv6Packet);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#sendIPData(Inet6Address, int, com.digi.xbee.api.models.IPProtocol, byte[])}.
	 * 
	 * <p>Verify that IP data cannot be sent if the IP address is {@code null}.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSendIPDataIPNull() throws TimeoutException, XBeeException {
		ipv6Device.sendIPData(null, PORT, PROTOCOL, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#sendIPData(Inet6Address, int, com.digi.xbee.api.models.IPProtocol, byte[])}.
	 * 
	 * <p>Verify that IP data cannot be sent if the destination port is bigger than 65535.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSendIPDataIllegalPortBig() throws TimeoutException, XBeeException {
		ipv6Device.sendIPData(ipv6Address, 100000, PROTOCOL, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#sendIPData(Inet6Address, int, com.digi.xbee.api.models.IPProtocol, byte[])}.
	 * 
	 * <p>Verify that IP data cannot be sent if the destination port is negative.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSendIPDataIllegalPortNegative() throws TimeoutException, XBeeException {
		ipv6Device.sendIPData(ipv6Address, -10, PROTOCOL, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#sendIPData(Inet6Address, int, com.digi.xbee.api.models.IPProtocol, byte[])}.
	 * 
	 * <p>Verify that IP data cannot be sent if the IP protocol is {@code null}.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSendIPDataProtocolNull() throws TimeoutException, XBeeException {
		ipv6Device.sendIPData(ipv6Address, PORT, null, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#sendIPData(Inet6Address, int, com.digi.xbee.api.models.IPProtocol, byte[])}.
	 * 
	 * <p>Verify that IP data cannot be sent if the IP data is {@code null}.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSendIPDataDataNull() throws TimeoutException, XBeeException {
		ipv6Device.sendIPData(ipv6Address, PORT, PROTOCOL, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#sendIPData(Inet6Address, int, com.digi.xbee.api.models.IPProtocol, byte[])}.
	 * 
	 * <p>Verify that IP data cannot be sent if the sender is a remote XBee device.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testSendIPDataFromRemoteDevices() throws TimeoutException, XBeeException {
		// Return that the IP device is remote when asked.
		Mockito.when(ipv6Device.isRemote()).thenReturn(true);
		
		ipv6Device.sendIPData(ipv6Address, PORT, PROTOCOL, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#sendIPData(Inet6Address, int, com.digi.xbee.api.models.IPProtocol, byte[])}.
	 * 
	 * <p>Verify that IP data cannot be sent if the device is not open.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testSendIPDataConnectionClosed() throws TimeoutException, XBeeException {
		// Throw an Interface not open exception when sending and checking any Tx IPv6 packet.
		Mockito.doThrow(new InterfaceNotOpenException()).when(ipv6Device).sendAndCheckXBeePacket(Mockito.any(TXIPv6Packet.class), Mockito.eq(false));
		
		ipv6Device.sendIPData(ipv6Address, PORT, PROTOCOL, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#sendIPData(Inet6Address, int, com.digi.xbee.api.models.IPProtocol, byte[])}.
	 * 
	 * <p>Verify that IP data cannot be sent if the device has an invalid operating mode.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSendIPDataInvalidOperatingMode() throws TimeoutException, XBeeException {
		// Throw an invalid operating mode exception when sending and checking any Tx IPv6 packet.
		Mockito.doThrow(new InvalidOperatingModeException()).when(ipv6Device).sendAndCheckXBeePacket(Mockito.any(TXIPv6Packet.class), Mockito.eq(false));
		
		ipv6Device.sendIPData(ipv6Address, PORT, PROTOCOL, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#sendIPData(Inet6Address, int, com.digi.xbee.api.models.IPProtocol, byte[])}.
	 * 
	 * <p>Verify that IP data cannot be sent if there is a timeout sending and checking the Tx IPv6 packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=TimeoutException.class)
	public void testSendIPDataTimeout() throws TimeoutException, XBeeException {
		// Throw a timeout exception when sending and checking any Tx IPv6 packet.
		Mockito.doThrow(new TimeoutException()).when(ipv6Device).sendAndCheckXBeePacket(Mockito.any(TXIPv6Packet.class), Mockito.eq(false));
		
		ipv6Device.sendIPData(ipv6Address, PORT, PROTOCOL, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#sendIPData(Inet6Address, int, com.digi.xbee.api.models.IPProtocol, byte[])}.
	 * 
	 * <p>Verify that IP data cannot be sent if there is a transmit exception when sending and checking the Tx IPv6 packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=TransmitException.class)
	public void testSendIPDataTransmitException() throws TimeoutException, XBeeException {
		// Throw a transmit exception when sending and checking any Tx IPv6 packet.
		Mockito.doThrow(new TransmitException(null)).when(ipv6Device).sendAndCheckXBeePacket(Mockito.any(TXIPv6Packet.class), Mockito.eq(false));
		
		ipv6Device.sendIPData(ipv6Address, PORT, PROTOCOL, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#sendIPData(Inet6Address, int, com.digi.xbee.api.models.IPProtocol, byte[])}.
	 * 
	 * <p>Verify that IP data cannot be sent if there is an IO error when sending and checking the Tx IPv6 packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=XBeeException.class)
	public void testSendIPDataIOException() throws TimeoutException, XBeeException {
		// Throw an XBee exception when sending and checking any Tx IPv6 packet.
		Mockito.doThrow(new XBeeException()).when(ipv6Device).sendAndCheckXBeePacket(Mockito.any(TXIPv6Packet.class), Mockito.eq(false));
		
		ipv6Device.sendIPData(ipv6Address, PORT, PROTOCOL, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#sendIPData(Inet6Address, int, com.digi.xbee.api.models.IPProtocol, byte[])}.
	 * 
	 * <p>Verify that IP data is sent successfully if there is not any error when sending and checking the Tx IPv6 packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testSendIPDataSuccess() throws TimeoutException, XBeeException {
		// Do nothing when sending and checking any Tx IPv6 packet.
		Mockito.doNothing().when(ipv6Device).sendAndCheckXBeePacket(Mockito.any(TXIPv6Packet.class), Mockito.eq(false));
		
		ipv6Device.sendIPData(ipv6Address, PORT, PROTOCOL, DATA.getBytes());
		
		// Verify the sendAndCheckXBeePacket(XBeePacket, boolean) method was called.
		Mockito.verify(ipv6Device, Mockito.times(1)).sendAndCheckXBeePacket(Mockito.eq(txIPv6Packet), Mockito.eq(false));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#sendIPData(Inet6Address, int, IPProtocol, byte[])}.
	 * 
	 * <p>Verify that the reduced method to send IP data (who calls the expanded one) 
	 * works successfully.</p>
	 * 
	 * @throws TimeoutException
	 * @throws XBeeException
	 */
	@Test
	public void testSendIPDataSimpleSuccess() throws TimeoutException, XBeeException {
		// Do nothing when the send IP data expanded method is called.
		Mockito.doNothing().when(ipv6Device).sendIPData(Mockito.any(Inet6Address.class), Mockito.anyInt(), 
				Mockito.any(IPProtocol.class), Mockito.any(byte[].class));
		
		ipv6Device.sendIPData(ipv6Address, PORT, PROTOCOL, DATA.getBytes());
	}
}
