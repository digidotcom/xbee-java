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
import com.digi.xbee.api.packet.network.TXIPv4Packet;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WLANDevice.class})
public class SendNetworkDataAsyncTest {
	
	// Constants.
	private static final IP32BitAddress IP_ADDRESS = new IP32BitAddress("10.101.2.123");
	
	private static final int PORT = 12345;
	
	private static final String DATA = "data";
	
	// Variables.
	private WLANDevice wlanDevice;
	
	private TXIPv4Packet txIPv4Packet;
	
	@Before
	public void setup() throws Exception {
		// Instantiate a WLANDevice object with a mocked interface.
		wlanDevice = PowerMockito.spy(new WLANDevice(Mockito.mock(SerialPortRxTx.class)));
		
		// Mock TX IPv4 packet.
		txIPv4Packet = Mockito.mock(TXIPv4Packet.class);
		
		// Whenever a TXIPv4Packet class is instantiated, the mocked txIPv4Packet packet should be returned.
		PowerMockito.whenNew(TXIPv4Packet.class).withAnyArguments().thenReturn(txIPv4Packet);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WLANDevice#sendNetworkData(IP32BitAddress, int, byte[])}.
	 * 
	 * <p>Verify that async. network data cannot be sent if the IP address is {@code null}.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSendNetworkDataAsyncIPNull() throws TimeoutException, XBeeException {
		wlanDevice.sendNetworkDataAsync(null, PORT, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WLANDevice#sendNetworkData(IP32BitAddress, int, byte[])}.
	 * 
	 * <p>Verify that async. network data cannot be sent if the destination port is bigger than 65535.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSendNetworkDataAsyncIllegalPortBig() throws TimeoutException, XBeeException {
		wlanDevice.sendNetworkDataAsync(IP_ADDRESS, 100000, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WLANDevice#sendNetworkData(IP32BitAddress, int, byte[])}.
	 * 
	 * <p>Verify that async. network data cannot be sent if the destination port is negative.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSendNetworkDataAsyncIllegalPortNegative() throws TimeoutException, XBeeException {
		wlanDevice.sendNetworkDataAsync(IP_ADDRESS, -10, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WLANDevice#sendNetworkData(IP32BitAddress, int, byte[])}.
	 * 
	 * <p>Verify that async. network data cannot be sent if the network data is {@code null}.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSendNetworkDataAsyncDataNull() throws TimeoutException, XBeeException {
		wlanDevice.sendNetworkDataAsync(IP_ADDRESS, PORT, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WLANDevice#sendNetworkData(IP32BitAddress, int, byte[])}.
	 * 
	 * <p>Verify that async. network data cannot be sent if the sender is a remote XBee device.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testSendNetworkDataAsyncFromRemoteDevices() throws TimeoutException, XBeeException {
		// Return that the WLAN device is remote when asked.
		Mockito.when(wlanDevice.isRemote()).thenReturn(true);
		
		wlanDevice.sendNetworkDataAsync(IP_ADDRESS, PORT, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WLANDevice#sendNetworkData(IP32BitAddress, int, byte[])}.
	 * 
	 * <p>Verify that async. network data cannot be sent if the device is not open.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testSendNetworkDataAsyncConnectionClosed() throws TimeoutException, XBeeException {
		// Throw an Interface not open exception when sending and checking any Tx IPv4 packet.
		Mockito.doThrow(new InterfaceNotOpenException()).when(wlanDevice).sendAndCheckXBeePacket(Mockito.any(TXIPv4Packet.class), Mockito.eq(true));
		
		wlanDevice.sendNetworkDataAsync(IP_ADDRESS, PORT, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WLANDevice#sendNetworkData(IP32BitAddress, int, byte[])}.
	 * 
	 * <p>Verify that async. network data cannot be sent if the device has an invalid operating mode.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSendNetworkDataAsyncInvalidOperatingMode() throws TimeoutException, XBeeException {
		// Throw an invalid operating mode exception when sending and checking any Tx IPv4 packet.
		Mockito.doThrow(new InvalidOperatingModeException()).when(wlanDevice).sendAndCheckXBeePacket(Mockito.any(TXIPv4Packet.class), Mockito.eq(true));
		
		wlanDevice.sendNetworkDataAsync(IP_ADDRESS, PORT, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WLANDevice#sendNetworkData(IP32BitAddress, int, byte[])}.
	 * 
	 * <p>Verify that async. network data cannot be sent if there is a timeout sending and checking the Tx IPv4 packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=TimeoutException.class)
	public void testSendNetworkDataAsyncTimeout() throws TimeoutException, XBeeException {
		// Throw a timeout exception when sending and checking any Tx IPv4 packet.
		Mockito.doThrow(new TimeoutException()).when(wlanDevice).sendAndCheckXBeePacket(Mockito.any(TXIPv4Packet.class), Mockito.eq(true));
		
		wlanDevice.sendNetworkDataAsync(IP_ADDRESS, PORT, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WLANDevice#sendNetworkData(IP32BitAddress, int, byte[])}.
	 * 
	 * <p>Verify that async. network data cannot be sent if there is a transmit exception when sending and checking the Tx IPv4 packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=TransmitException.class)
	public void testSendNetworkDataAsyncTransmitException() throws TimeoutException, XBeeException {
		// Throw a transmit exception when sending and checking any Tx IPv4 packet.
		Mockito.doThrow(new TransmitException(null)).when(wlanDevice).sendAndCheckXBeePacket(Mockito.any(TXIPv4Packet.class), Mockito.eq(true));
		
		wlanDevice.sendNetworkDataAsync(IP_ADDRESS, PORT, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WLANDevice#sendNetworkData(IP32BitAddress, int, byte[])}.
	 * 
	 * <p>Verify that async. network data cannot be sent if there is an IO error when sending and checking the Tx IPv4 packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=XBeeException.class)
	public void testSendNetworkDataAsyncIOException() throws TimeoutException, XBeeException {
		// Throw an XBee exception when sending and checking any Tx IPv4 packet.
		Mockito.doThrow(new XBeeException()).when(wlanDevice).sendAndCheckXBeePacket(Mockito.any(TXIPv4Packet.class), Mockito.eq(true));
		
		wlanDevice.sendNetworkDataAsync(IP_ADDRESS, PORT, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WLANDevice#sendNetworkData(IP32BitAddress, int, byte[])}.
	 * 
	 * <p>Verify that async. network data is sent successfully if there is not any error when sending and checking the Tx IPv4 packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testSendNetworkDataAsyncSuccess() throws TimeoutException, XBeeException {
		// Do nothing when sending and checking any Tx IPv4 packet.
		Mockito.doNothing().when(wlanDevice).sendAndCheckXBeePacket(Mockito.any(TXIPv4Packet.class), Mockito.eq(true));
		
		wlanDevice.sendNetworkDataAsync(IP_ADDRESS, PORT, DATA.getBytes());
		
		// Verify the sendAndCheckXBeePacket(XBeePacket, boolean) method was called.
		Mockito.verify(wlanDevice, Mockito.times(1)).sendAndCheckXBeePacket(Mockito.eq(txIPv4Packet), Mockito.eq(true));
	}
}
