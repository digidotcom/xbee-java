/**
 * Copyright (c) 2016 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */
package com.digi.xbee.api;

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
import com.digi.xbee.api.models.IP32BitAddress;
import com.digi.xbee.api.models.NetworkProtocol;
import com.digi.xbee.api.packet.network.TXIPv4Packet;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IPDevice.class})
public class SendNetworkDataAsyncTest {
	
	// Constants.
	private static final IP32BitAddress IP_ADDRESS = new IP32BitAddress("10.101.2.123");
	
	private static final NetworkProtocol PROTOCOL = NetworkProtocol.TCP;
	
	private static final int PORT = 12345;
	
	private static final String DATA = "data";
	
	// Variables.
	private IPDevice ipDevice;
	
	private TXIPv4Packet txIPv4Packet;
	
	@Before
	public void setup() throws Exception {
		// Instantiate a IPDevice object with a mocked interface.
		ipDevice = PowerMockito.spy(new IPDevice(Mockito.mock(SerialPortRxTx.class)));
		
		// Mock TX IPv4 packet.
		txIPv4Packet = Mockito.mock(TXIPv4Packet.class);
		
		// Whenever a TXIPv4Packet class is instantiated, the mocked txIPv4Packet packet should be returned.
		PowerMockito.whenNew(TXIPv4Packet.class).withAnyArguments().thenReturn(txIPv4Packet);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendNetworkDataAsync(IP32BitAddress, int, com.digi.xbee.api.models.NetworkProtocol, boolean, byte[])}.
	 * 
	 * <p>Verify that async. network data cannot be sent if the IP address is {@code null}.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSendNetworkDataAsyncIPNull() throws TimeoutException, XBeeException {
		ipDevice.sendNetworkDataAsync(null, PORT, PROTOCOL, false, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendNetworkDataAsync(IP32BitAddress, int, com.digi.xbee.api.models.NetworkProtocol, boolean, byte[])}.
	 * 
	 * <p>Verify that async. network data cannot be sent if the destination port is bigger than 65535.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSendNetworkDataAsyncIllegalPortBig() throws TimeoutException, XBeeException {
		ipDevice.sendNetworkDataAsync(IP_ADDRESS, 100000, PROTOCOL, false, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendNetworkDataAsync(IP32BitAddress, int, com.digi.xbee.api.models.NetworkProtocol, boolean, byte[])}.
	 * 
	 * <p>Verify that async. network data cannot be sent if the destination port is negative.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSendNetworkDataAsyncIllegalPortNegative() throws TimeoutException, XBeeException {
		ipDevice.sendNetworkDataAsync(IP_ADDRESS, -10, PROTOCOL, false, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendNetworkDataAsync(IP32BitAddress, int, com.digi.xbee.api.models.NetworkProtocol, boolean, byte[])}.
	 * 
	 * <p>Verify that async. network data cannot be sent if the network protocol is {@code null}.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSendNetworkDataAsyncProtocolNull() throws TimeoutException, XBeeException {
		ipDevice.sendNetworkDataAsync(IP_ADDRESS, PORT, null, false, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendNetworkDataAsync(IP32BitAddress, int, com.digi.xbee.api.models.NetworkProtocol, boolean, byte[])}.
	 * 
	 * <p>Verify that async. network data cannot be sent if the network data is {@code null}.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSendNetworkDataAsyncDataNull() throws TimeoutException, XBeeException {
		ipDevice.sendNetworkDataAsync(IP_ADDRESS, PORT, PROTOCOL, false, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendNetworkDataAsync(IP32BitAddress, int, com.digi.xbee.api.models.NetworkProtocol, boolean, byte[])}.
	 * 
	 * <p>Verify that async. network data cannot be sent if the sender is a remote XBee device.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testSendNetworkDataAsyncFromRemoteDevices() throws TimeoutException, XBeeException {
		// Return that the IP device is remote when asked.
		Mockito.when(ipDevice.isRemote()).thenReturn(true);
		
		ipDevice.sendNetworkDataAsync(IP_ADDRESS, PORT, PROTOCOL, false, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendNetworkDataAsync(IP32BitAddress, int, com.digi.xbee.api.models.NetworkProtocol, boolean, byte[])}.
	 * 
	 * <p>Verify that async. network data cannot be sent if the device is not open.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testSendNetworkDataAsyncConnectionClosed() throws TimeoutException, XBeeException {
		// Throw an Interface not open exception when sending and checking any Tx IPv4 packet.
		Mockito.doThrow(new InterfaceNotOpenException()).when(ipDevice).sendAndCheckXBeePacket(Mockito.any(TXIPv4Packet.class), Mockito.eq(true));
		
		ipDevice.sendNetworkDataAsync(IP_ADDRESS, PORT, PROTOCOL, false, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendNetworkDataAsync(IP32BitAddress, int, com.digi.xbee.api.models.NetworkProtocol, boolean, byte[])}.
	 * 
	 * <p>Verify that async. network data cannot be sent if the device has an invalid operating mode.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSendNetworkDataAsyncInvalidOperatingMode() throws TimeoutException, XBeeException {
		// Throw an invalid operating mode exception when sending and checking any Tx IPv4 packet.
		Mockito.doThrow(new InvalidOperatingModeException()).when(ipDevice).sendAndCheckXBeePacket(Mockito.any(TXIPv4Packet.class), Mockito.eq(true));
		
		ipDevice.sendNetworkDataAsync(IP_ADDRESS, PORT, PROTOCOL, false, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendNetworkDataAsync(IP32BitAddress, int, com.digi.xbee.api.models.NetworkProtocol, boolean, byte[])}.
	 * 
	 * <p>Verify that async. network data cannot be sent if there is a timeout sending and checking the Tx IPv4 packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=TimeoutException.class)
	public void testSendNetworkDataAsyncTimeout() throws TimeoutException, XBeeException {
		// Throw a timeout exception when sending and checking any Tx IPv4 packet.
		Mockito.doThrow(new TimeoutException()).when(ipDevice).sendAndCheckXBeePacket(Mockito.any(TXIPv4Packet.class), Mockito.eq(true));
		
		ipDevice.sendNetworkDataAsync(IP_ADDRESS, PORT, PROTOCOL, false, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendNetworkDataAsync(IP32BitAddress, int, com.digi.xbee.api.models.NetworkProtocol, boolean, byte[])}.
	 * 
	 * <p>Verify that async. network data cannot be sent if there is a transmit exception when sending and checking the Tx IPv4 packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=TransmitException.class)
	public void testSendNetworkDataAsyncTransmitException() throws TimeoutException, XBeeException {
		// Throw a transmit exception when sending and checking any Tx IPv4 packet.
		Mockito.doThrow(new TransmitException(null)).when(ipDevice).sendAndCheckXBeePacket(Mockito.any(TXIPv4Packet.class), Mockito.eq(true));
		
		ipDevice.sendNetworkDataAsync(IP_ADDRESS, PORT, PROTOCOL, false, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendNetworkDataAsync(IP32BitAddress, int, com.digi.xbee.api.models.NetworkProtocol, boolean, byte[])}.
	 * 
	 * <p>Verify that async. network data cannot be sent if there is an IO error when sending and checking the Tx IPv4 packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=XBeeException.class)
	public void testSendNetworkDataAsyncIOException() throws TimeoutException, XBeeException {
		// Throw an XBee exception when sending and checking any Tx IPv4 packet.
		Mockito.doThrow(new XBeeException()).when(ipDevice).sendAndCheckXBeePacket(Mockito.any(TXIPv4Packet.class), Mockito.eq(true));
		
		ipDevice.sendNetworkDataAsync(IP_ADDRESS, PORT, PROTOCOL, false, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendNetworkDataAsync(IP32BitAddress, int, com.digi.xbee.api.models.NetworkProtocol, boolean, byte[])}.
	 * 
	 * <p>Verify that async. network data is sent successfully if there is not any error when sending and checking the Tx IPv4 packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testSendNetworkDataAsyncSuccess() throws TimeoutException, XBeeException {
		// Do nothing when sending and checking any Tx IPv4 packet.
		Mockito.doNothing().when(ipDevice).sendAndCheckXBeePacket(Mockito.any(TXIPv4Packet.class), Mockito.eq(true));
		
		ipDevice.sendNetworkDataAsync(IP_ADDRESS, PORT, PROTOCOL, false, DATA.getBytes());
		
		// Verify the sendAndCheckXBeePacket(XBeePacket, boolean) method was called.
		Mockito.verify(ipDevice, Mockito.times(1)).sendAndCheckXBeePacket(Mockito.eq(txIPv4Packet), Mockito.eq(true));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendNetworkDataAsync(IP32BitAddress, int, NetworkProtocol, byte[])}.
	 * 
	 * <p>Verify that the reduced method to send network data asynchronously (who calls the 
	 * expanded one) works successfully.</p>
	 * 
	 * @throws TimeoutException
	 * @throws XBeeException
	 */
	@Test
	public void testSendNetworkDataAsyncSimpleSuccess() throws TimeoutException, XBeeException {
		// Do nothing when the send network data async. expanded method is called.
		Mockito.doNothing().when(ipDevice).sendNetworkDataAsync(Mockito.any(IP32BitAddress.class), Mockito.anyInt(), Mockito.any(NetworkProtocol.class), Mockito.anyBoolean(), Mockito.any(byte[].class));
		
		ipDevice.sendNetworkDataAsync(IP_ADDRESS, PORT, PROTOCOL, DATA.getBytes());
	}
}
