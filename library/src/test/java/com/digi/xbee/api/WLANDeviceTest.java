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
import com.digi.xbee.api.models.IP32BitAddress;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WLANDevice.class})
public class WLANDeviceTest {
	
	// Constants.
	private static final String PARAMETER_MY = "MY";
	private static final byte[] RESPONSE_MY = new byte[]{0x00, 0x00, 0x00, 0x00};
	
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
		
		// Fist, check that the IP is null (it has not been created yet)
		assertNull(wlanDevice.getIPAddress());
		
		// Call the readDeviceInfo method.
		wlanDevice.readDeviceInfo();
		Mockito.verify((AbstractXBeeDevice)wlanDevice, Mockito.times(1)).readDeviceInfo();
		
		// Verify that the IP address was generated.
		assertEquals(ipAddress, wlanDevice.getIPAddress());
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
