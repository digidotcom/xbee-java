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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.IP32BitAddress;
import com.digi.xbee.api.models.NetworkProtocol;
import com.digi.xbee.api.utils.ByteUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WLANDevice.class})
public class WLANDeviceTest {
	
	// Constants.
	private static final String PARAMETER_MY = "MY";
	private static final String PARAMETER_C0 = "C0";
	private static final byte[] RESPONSE_MY = new byte[]{0x00, 0x00, 0x00, 0x00};
	private static final byte[] RESPONSE_C0 = new byte[]{0x12, 0x34};
	
	private static short EXPECTED_SOURCE_PORT = ByteUtils.byteArrayToShort(RESPONSE_C0);
	
	private static NetworkProtocol PROTOCOL = NetworkProtocol.UDP;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	// Variables.
	private IP32BitAddress ipAddress;
	
	private WLANDevice wlanDevice;
	
	@Before
	public void setup() throws Exception {
		// Suppress the 'readDeviceInfo' method of the parent class so that it is not
		// called from the child (WLANDevice) class.
		PowerMockito.suppress(PowerMockito.method(AbstractXBeeDevice.class, "readDeviceInfo"));
		
		// Spy the WLANDevice class.
		SerialPortRxTx mockPort = Mockito.mock(SerialPortRxTx.class);
		wlanDevice = PowerMockito.spy(new WLANDevice(mockPort));
		
		// Mock an IP address object.
		ipAddress = PowerMockito.mock(IP32BitAddress.class);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WLANDevice#getIPAddress()} and
	 * {@link com.digi.xbee.api.WLANDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that the {@code readDeviceInfo()} method of the WLAN device generates 
	 * the IP address correctly.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testReadDeviceInfoWLAN() throws Exception {
		// Whenever an IP32BitAddress class is instantiated, the mocked IP address should be returned.
		PowerMockito.whenNew(IP32BitAddress.class).withAnyArguments().thenReturn(ipAddress);
		
		// Return a valid response when requesting the MY parameter value.
		Mockito.doReturn(RESPONSE_MY).when(wlanDevice).getParameter(PARAMETER_MY);
		// Return a valid response when requesting the C0 parameter value.
		Mockito.doReturn(RESPONSE_C0).when(wlanDevice).getParameter(PARAMETER_C0);
		
		// Fist, check that the IP is null (it has not been created yet)
		assertNull(wlanDevice.getIPAddress());
		// Check that the source port is the default one.
		assertEquals(WLANDevice.DEFAULT_SOURCE_PORT, wlanDevice.sourcePort);
		
		// Call the readDeviceInfo method.
		wlanDevice.readDeviceInfo();
		Mockito.verify((AbstractXBeeDevice)wlanDevice, Mockito.times(1)).readDeviceInfo();
		
		// Verify that the IP address was generated.
		assertEquals(ipAddress, wlanDevice.getIPAddress());
		// Verify that the source port is the expected one.
		assertEquals(EXPECTED_SOURCE_PORT, wlanDevice.sourcePort);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WLANDevice#setNetworkProtocol(com.digi.xbee.api.models.NetworkProtocol)}.
	 * 
	 * Verify that network protocol cannot be set if it is {@code null}.
	 */
	@Test(expected=NullPointerException.class)
	public void testSetNetworkProtocolNullProtocol() {
		wlanDevice.setNetworkProtocol(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WLANDevice#setNetworkProtocol(com.digi.xbee.api.models.NetworkProtocol)}.
	 * 
	 * Verify that network protocol can be set successfully {@code null}.
	 */
	@Test
	public void testSetNetworkProtocolSuccess() {
		// Verify the device has the default network protocol.
		assertEquals(WLANDevice.DEFAULT_PROTOCOL, wlanDevice.protocol);
		
		// Set the new protocol and verify it was set correctly.
		wlanDevice.setNetworkProtocol(PROTOCOL);
		assertEquals(PROTOCOL, wlanDevice.getNetworkProtocol());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WLANDevice#startListening(int)}.
	 * 
	 * <p>Verify that device cannot start listening for network data if the 
	 * source port is bigger than 65535.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testStartListeningInvalidPortBig() throws TimeoutException, XBeeException {
		wlanDevice.startListening(100000);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WLANDevice#startListening(int)}.
	 * 
	 * <p>Verify that device cannot start listening for network data if the 
	 * source port is negative.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testStartListeningInvalidPortNegative() throws TimeoutException, XBeeException {
		wlanDevice.startListening(-10);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WLANDevice#startListening(int)}.
	 * 
	 * <p>Verify that device cannot start listening for network data if the 
	 * device is not open.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testStartListeningConnectionClosed() throws TimeoutException, XBeeException {
		// Throw an Interface not open exception when setting any parameter.
		Mockito.doThrow(new InterfaceNotOpenException()).when(wlanDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		wlanDevice.startListening(123);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WLANDevice#startListening(int)}.
	 * 
	 * <p>Verify that device cannot start listening for network data if device 
	 * has an invalid operating mode.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testStartListeningInvalidOperatingMode() throws TimeoutException, XBeeException {
		// Throw an Invalid operating mode exception when setting any parameter.
		Mockito.doThrow(new InvalidOperatingModeException()).when(wlanDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		wlanDevice.startListening(123);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WLANDevice#startListening(int)}.
	 * 
	 * <p>Verify that device cannot start listening for network data if there 
	 * is a timeout setting the 'C0' parameter.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=TimeoutException.class)
	public void testStartListeningTimeout() throws TimeoutException, XBeeException {
		// Throw an timeout exception when setting any parameter.
		Mockito.doThrow(new TimeoutException()).when(wlanDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		wlanDevice.startListening(123);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WLANDevice#startListening(int)}.
	 * 
	 * <p>Verify that device starts listening for network data successfully.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testStartListeningSuccess() throws TimeoutException, XBeeException {
		int newSourcePort = 123;
		// Do nothing when setting any parameter.
		Mockito.doNothing().when(wlanDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		wlanDevice.startListening(newSourcePort);
		
		// Verify that source port has changed to the provided one.
		assertEquals(newSourcePort, wlanDevice.sourcePort);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WLANDevice#stopListening()}.
	 * 
	 * <p>Verify that device cannot stop listening for network data if the 
	 * device is not open.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testStopListeningConnectionClosed() throws TimeoutException, XBeeException {
		// Throw an Interface not open exception when setting any parameter.
		Mockito.doThrow(new InterfaceNotOpenException()).when(wlanDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		wlanDevice.stopListening();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WLANDevice#stopListening()}.
	 * 
	 * <p>Verify that device cannot stop listening for network data if device 
	 * has an invalid operating mode.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testStopListeningInvalidOperatingMode() throws TimeoutException, XBeeException {
		// Throw an Invalid operating mode exception when setting any parameter.
		Mockito.doThrow(new InvalidOperatingModeException()).when(wlanDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		wlanDevice.stopListening();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WLANDevice#stopListening()}.
	 * 
	 * <p>Verify that device cannot stop listening for network data if there 
	 * is a timeout setting the 'C0' parameter.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=TimeoutException.class)
	public void testStoptListeningTimeout() throws TimeoutException, XBeeException {
		// Throw an timeout exception when setting any parameter.
		Mockito.doThrow(new TimeoutException()).when(wlanDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		wlanDevice.stopListening();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WLANDevice#stopListening()}.
	 * 
	 * <p>Verify that device stops listening for network data successfully.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testStopListeningSuccess() throws TimeoutException, XBeeException {
		// Do nothing when setting tany parameter.
		Mockito.doNothing().when(wlanDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		wlanDevice.stopListening();
		
		// Verify that source port has changed to 0.
		assertEquals(0, wlanDevice.sourcePort);
		
		// Verify the setParameter(String, byte[]) method was called once.
		Mockito.verify(wlanDevice, Mockito.times(1)).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WLANDevice#stopListening()}.
	 * 
	 * <p>Verify that device stops listening for network data successfully.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testStopListeningUDPSuccess() throws TimeoutException, XBeeException {
		// Do nothing when setting tany parameter.
		Mockito.doNothing().when(wlanDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		// Return UDP when asked for the network protocol.
		Mockito.doReturn(NetworkProtocol.UDP).when(wlanDevice).getNetworkProtocol();
		
		wlanDevice.stopListening();
		
		// Verify that source port has changed to 0.
		assertEquals(0, wlanDevice.sourcePort);
		
		// Verify the setParameter(String, byte[]) method was called twice (one for 'C0' and other for 'DE').
		Mockito.verify(wlanDevice, Mockito.times(1)).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WLANDevice#getDestinationAddress()},
	 * {@link com.digi.xbee.api.WLANDevice#setDestinationAddress(com.digi.xbee.api.models.XBee64BitAddress)},
	 * {@link com.digi.xbee.api.WLANDevice#getPANID()},
	 * {@link com.digi.xbee.api.WLANDevice#setPANID(byte[])},
	 * {@link com.digi.xbee.api.WLANDevice#addDataListener(com.digi.xbee.api.listeners.IDataReceiveListener)},
	 * {@link com.digi.xbee.api.WLANDevice#removeDataListener(com.digi.xbee.api.listeners.IDataReceiveListener)},
	 * {@link com.digi.xbee.api.WLANDevice#addIOSampleListener(com.digi.xbee.api.listeners.IIOSampleReceiveListener)},
	 * {@link com.digi.xbee.api.WLANDevice#removeIOSampleListener(com.digi.xbee.api.listeners.IIOSampleReceiveListener)},
	 * {@link com.digi.xbee.api.WLANDevice#readData()},
	 * {@link com.digi.xbee.api.WLANDevice#readData(int)},
	 * {@link com.digi.xbee.api.WLANDevice#readDataFrom(RemoteXBeeDevice)},
	 * {@link com.digi.xbee.api.WLANDevice#readDataFrom(RemoteXBeeDevice, int)},
	 * {@link com.digi.xbee.api.WLANDevice#sendBroadcastData(byte[])},
	 * {@link com.digi.xbee.api.WLANDevice#sendData(RemoteXBeeDevice, byte[])} and
	 * {@link com.digi.xbee.api.WLANDevice#sendDataAsync(RemoteXBeeDevice, byte[])}.
	 * 
	 * <p>Verify that the not supported methods of the WLAN device throw an
	 * {@code UnsupportedOperationException}</p>.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperations() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// All the following operations should throw an 
		// {@code UnsupportedOperationException} exception.
		wlanDevice.getDestinationAddress();
		wlanDevice.setDestinationAddress(null);
		wlanDevice.getPANID();
		wlanDevice.setPANID(null);
		wlanDevice.addDataListener(null);
		wlanDevice.removeDataListener(null);
		wlanDevice.addIOSampleListener(null);
		wlanDevice.removeIOSampleListener(null);
		wlanDevice.readData();
		wlanDevice.readData(-1);
		wlanDevice.readDataFrom(null);
		wlanDevice.readDataFrom(null, -1);
		wlanDevice.sendBroadcastData(null);
		wlanDevice.sendData((RemoteXBeeDevice)null, null);
		wlanDevice.sendDataAsync((RemoteXBeeDevice)null, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WLANDevice#get16BitAddress()} and
	 * {@link com.digi.xbee.api.WLANDevice#getNetwork()}.
	 * 
	 * <p>Verify that parameters not supported by the WLAN device are returned 
	 * as {@code null}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedParameters() throws Exception {
		// WLAN devices do not have 16-bit address and do not support the 
		// network feature.
		assertNull(wlanDevice.get16BitAddress());
		assertNull(wlanDevice.getNetwork());
	}
}
