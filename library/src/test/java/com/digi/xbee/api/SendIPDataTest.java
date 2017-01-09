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

import java.net.Inet4Address;

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
import com.digi.xbee.api.packet.ip.TXIPv4Packet;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IPDevice.class})
public class SendIPDataTest {
	
	// Constants.
	private static final String IP_ADDRESS = "10.101.2.123";
	
	private static final IPProtocol PROTOCOL = IPProtocol.TCP;
	
	private static final int PORT = 12345;
	
	private static final String DATA = "data";
	
	// Variables.
	private IPDevice ipDevice;
	
	private TXIPv4Packet txIPv4Packet;
	
	private Inet4Address ipAddress;
	
	@Before
	public void setup() throws Exception {
		ipAddress = (Inet4Address) Inet4Address.getByName(IP_ADDRESS);
		
		// Instantiate a IPDevice object with a mocked interface.
		ipDevice = PowerMockito.spy(new IPDevice(Mockito.mock(SerialPortRxTx.class)));
		
		// Mock TX IPv4 packet.
		txIPv4Packet = Mockito.mock(TXIPv4Packet.class);
		
		// Whenever a TXIPv4Packet class is instantiated, the mocked txIPv4Packet packet should be returned.
		PowerMockito.whenNew(TXIPv4Packet.class).withAnyArguments().thenReturn(txIPv4Packet);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendIPData(Inet4Address, int, com.digi.xbee.api.models.IPProtocol, boolean, byte[])}.
	 * 
	 * <p>Verify that IP data cannot be sent if the IP address is {@code null}.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSendIPDataIPNull() throws TimeoutException, XBeeException {
		ipDevice.sendIPData(null, PORT, PROTOCOL, false, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendIPData(Inet4Address, int, com.digi.xbee.api.models.IPProtocol, boolean, byte[])}.
	 * 
	 * <p>Verify that IP data cannot be sent if the destination port is bigger than 65535.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSendIPDataIllegalPortBig() throws TimeoutException, XBeeException {
		ipDevice.sendIPData(ipAddress, 100000, PROTOCOL, false, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendIPData(Inet4Address, int, com.digi.xbee.api.models.IPProtocol, boolean, byte[])}.
	 * 
	 * <p>Verify that IP data cannot be sent if the destination port is negative.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSendIPDataIllegalPortNegative() throws TimeoutException, XBeeException {
		ipDevice.sendIPData(ipAddress, -10, PROTOCOL, false, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendIPData(Inet4Address, int, com.digi.xbee.api.models.IPProtocol, boolean, byte[])}.
	 * 
	 * <p>Verify that IP data cannot be sent if the IP protocol is {@code null}.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSendIPDataProtocolNull() throws TimeoutException, XBeeException {
		ipDevice.sendIPData(ipAddress, PORT, null, false, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendIPData(Inet4Address, int, com.digi.xbee.api.models.IPProtocol, boolean, byte[])}.
	 * 
	 * <p>Verify that IP data cannot be sent if the IP data is {@code null}.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSendIPDataDataNull() throws TimeoutException, XBeeException {
		ipDevice.sendIPData(ipAddress, PORT, PROTOCOL, false, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendIPData(Inet4Address, int, com.digi.xbee.api.models.IPProtocol, boolean, byte[])}.
	 * 
	 * <p>Verify that IP data cannot be sent if the sender is a remote XBee device.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testSendIPDataFromRemoteDevices() throws TimeoutException, XBeeException {
		// Return that the IP device is remote when asked.
		Mockito.when(ipDevice.isRemote()).thenReturn(true);
		
		ipDevice.sendIPData(ipAddress, PORT, PROTOCOL, false, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendIPData(Inet4Address, int, com.digi.xbee.api.models.IPProtocol, boolean, byte[])}.
	 * 
	 * <p>Verify that IP data cannot be sent if the device is not open.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testSendIPDataConnectionClosed() throws TimeoutException, XBeeException {
		// Throw an Interface not open exception when sending and checking any Tx IPv4 packet.
		Mockito.doThrow(new InterfaceNotOpenException()).when(ipDevice).sendAndCheckXBeePacket(Mockito.any(TXIPv4Packet.class), Mockito.eq(false));
		
		ipDevice.sendIPData(ipAddress, PORT, PROTOCOL, false, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendIPData(Inet4Address, int, com.digi.xbee.api.models.IPProtocol, boolean, byte[])}.
	 * 
	 * <p>Verify that IP data cannot be sent if the device has an invalid operating mode.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSendIPDataInvalidOperatingMode() throws TimeoutException, XBeeException {
		// Throw an invalid operating mode exception when sending and checking any Tx IPv4 packet.
		Mockito.doThrow(new InvalidOperatingModeException()).when(ipDevice).sendAndCheckXBeePacket(Mockito.any(TXIPv4Packet.class), Mockito.eq(false));
		
		ipDevice.sendIPData(ipAddress, PORT, PROTOCOL, false, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendIPData(Inet4Address, int, com.digi.xbee.api.models.IPProtocol, boolean, byte[])}.
	 * 
	 * <p>Verify that IP data cannot be sent if there is a timeout sending and checking the Tx IPv4 packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=TimeoutException.class)
	public void testSendIPDataTimeout() throws TimeoutException, XBeeException {
		// Throw a timeout exception when sending and checking any Tx IPv4 packet.
		Mockito.doThrow(new TimeoutException()).when(ipDevice).sendAndCheckXBeePacket(Mockito.any(TXIPv4Packet.class), Mockito.eq(false));
		
		ipDevice.sendIPData(ipAddress, PORT, PROTOCOL, false, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendIPData(Inet4Address, int, com.digi.xbee.api.models.IPProtocol, boolean, byte[])}.
	 * 
	 * <p>Verify that IP data cannot be sent if there is a transmit exception when sending and checking the Tx IPv4 packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=TransmitException.class)
	public void testSendIPDataTransmitException() throws TimeoutException, XBeeException {
		// Throw a transmit exception when sending and checking any Tx IPv4 packet.
		Mockito.doThrow(new TransmitException(null)).when(ipDevice).sendAndCheckXBeePacket(Mockito.any(TXIPv4Packet.class), Mockito.eq(false));
		
		ipDevice.sendIPData(ipAddress, PORT, PROTOCOL, false, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendIPData(Inet4Address, int, com.digi.xbee.api.models.IPProtocol, boolean, byte[])}.
	 * 
	 * <p>Verify that IP data cannot be sent if there is an IO error when sending and checking the Tx IPv4 packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=XBeeException.class)
	public void testSendIPDataIOException() throws TimeoutException, XBeeException {
		// Throw an XBee exception when sending and checking any Tx IPv4 packet.
		Mockito.doThrow(new XBeeException()).when(ipDevice).sendAndCheckXBeePacket(Mockito.any(TXIPv4Packet.class), Mockito.eq(false));
		
		ipDevice.sendIPData(ipAddress, PORT, PROTOCOL, false, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendIPData(Inet4Address, int, com.digi.xbee.api.models.IPProtocol, boolean, byte[])}.
	 * 
	 * <p>Verify that IP data is sent successfully if there is not any error when sending and checking the Tx IPv4 packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testSendIPDataSuccess() throws TimeoutException, XBeeException {
		// Do nothing when sending and checking any Tx IPv4 packet.
		Mockito.doNothing().when(ipDevice).sendAndCheckXBeePacket(Mockito.any(TXIPv4Packet.class), Mockito.eq(false));
		
		ipDevice.sendIPData(ipAddress, PORT, PROTOCOL, false, DATA.getBytes());
		
		// Verify the sendAndCheckXBeePacket(XBeePacket, boolean) method was called.
		Mockito.verify(ipDevice, Mockito.times(1)).sendAndCheckXBeePacket(Mockito.eq(txIPv4Packet), Mockito.eq(false));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendIPData(Inet4Address, int, IPProtocol, byte[])}.
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
		Mockito.doNothing().when(ipDevice).sendIPData(Mockito.any(Inet4Address.class), Mockito.anyInt(), Mockito.any(IPProtocol.class), Mockito.anyBoolean(), Mockito.any(byte[].class));
		
		ipDevice.sendIPData(ipAddress, PORT, PROTOCOL, DATA.getBytes());
	}
}
