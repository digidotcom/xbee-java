/**
 * Copyright (c) 2014 Digi International Inc.,
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
import com.digi.xbee.api.models.HardwareVersion;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeProtocol;

public class RemoteXBeeDeviceReadInfo64BitTest {

	// Constants.
	private static final String MAC_ADDRESS = "0123456789ABCDEF";
	
	private static final String PARAMETER_SH = "SH";
	private static final String PARAMETER_SL = "SL";
	private static final String PARAMETER_NI = "NI";
	private static final String PARAMETER_HV = "HV";
	private static final String PARAMETER_VR = "VR";
	private static final String PARAMETER_MY = "MY";
	
	private static final byte[] RESPONSE_NI = new byte[]{0x58, 0x42, 0x45, 0x45};                             // XBEE
	private static final byte[] RESPONSE_HV = new byte[]{0x01, 0x23};                                         // 0x0123
	private static final byte[] RESPONSE_VR = new byte[]{0x45, 0x67};                                         // 0x4567
	private static final byte[] RESPONSE_MY = new byte[]{0x76, 0x54};                                         // 0x7654
	
	// Variables.
	private RemoteXBeeDevice remoteXBeeDevice;
	
	@Before
	public void setup() throws Exception {
		// Mock the local XBee device necessary to instantiate a remote one.
		XBeeDevice localXBeeDevice = Mockito.mock(XBeeDevice.class);
		Mockito.when(localXBeeDevice.getConnectionInterface()).thenReturn(Mockito.mock(SerialPortRxTx.class));
		Mockito.when(localXBeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
		
		XBee64BitAddress remoteAddress = new XBee64BitAddress(MAC_ADDRESS);
		
		// Instantiate a RemoteXBeeDevice object with basic parameters.
		remoteXBeeDevice = PowerMockito.spy(new RemoteXBeeDevice(localXBeeDevice, remoteAddress));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.RemoteXBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a remote device cannot be read if the connection of the local 
	 * device is closed.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testReadDeviceInfoErrorConnectionClosed() throws XBeeException {
		// Throw an interface not open exception when trying to read any parameter.
		Mockito.doThrow(new InterfaceNotOpenException()).when(remoteXBeeDevice).getParameter(Mockito.anyString());
		
		// Initialize the device.
		remoteXBeeDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.RemoteXBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a remote device cannot be read if the operating mode of the 
	 * local device is not valid.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testReadDeviceInfoErrorInvalidOperatingMode() throws XBeeException {
		// Throw an invalid operating mode exception when trying to read any parameter.
		Mockito.doThrow(new InvalidOperatingModeException()).when(remoteXBeeDevice).getParameter(Mockito.anyString());
		
		// Initialize the device.
		remoteXBeeDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.RemoteXBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a remote device cannot be read if there is a timeout reading 
	 * the NI parameter of the device.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testReadDeviceInfoErrorNITimeout() throws XBeeException, IOException {
		// Throw a timeout exception when requesting the NI parameter value.
		Mockito.doThrow(new TimeoutException()).when(remoteXBeeDevice).getParameter(PARAMETER_NI);
		
		// Initialize the device.
		remoteXBeeDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.RemoteXBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a remote device cannot be read if the answer when requesting the 
	 * NI parameter is null or the response status is not OK. It is, there is an AT command exception 
	 * reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testReadDeviceInfoErrorNIInvalidAnswer() throws XBeeException, IOException {
		// Throw an AT command exception when requesting the NI parameter value.
		Mockito.doThrow(new ATCommandException(null)).when(remoteXBeeDevice).getParameter(PARAMETER_NI);
		
		// Initialize the device.
		remoteXBeeDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.RemoteXBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a remote device cannot be read if there is a timeout reading the 
	 * HV parameter of the device.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testReadDeviceInfoErrorHVTimeout() throws XBeeException, IOException {
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(remoteXBeeDevice).getParameter(PARAMETER_NI);
		
		// Throw a timeout exception when requesting the HV parameter value.
		Mockito.doThrow(new TimeoutException()).when(remoteXBeeDevice).getParameter(PARAMETER_HV);
		
		// Initialize the device.
		remoteXBeeDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.RemoteXBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a remote device cannot be read if the answer when requesting 
	 * the HV parameter is null or the response status is not OK. It is, there is an AT command exception 
	 * reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testReadDeviceInfoErrorHVInvalidAnswer() throws XBeeException, IOException {
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(remoteXBeeDevice).getParameter(PARAMETER_NI);
		
		// Throw an AT command exception when requesting the HV parameter value.
		Mockito.doThrow(new ATCommandException(null)).when(remoteXBeeDevice).getParameter(PARAMETER_HV);
		
		// Initialize the device.
		remoteXBeeDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.RemoteXBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a remote device cannot be read if there is a timeout reading 
	 * the VR parameter of the device.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testReadDeviceInfoErrorVRTimeout() throws XBeeException, IOException {
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(remoteXBeeDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(remoteXBeeDevice).getParameter(PARAMETER_HV);
		
		// Throw a timeout exception when requesting the VR parameter value.
		Mockito.doThrow(new TimeoutException()).when(remoteXBeeDevice).getParameter(PARAMETER_VR);
		
		// Initialize the device.
		remoteXBeeDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.RemoteXBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a remote device cannot be read if the answer when requesting 
	 * the VR parameter is null or the response status is not OK. It is, there is an AT command exception 
	 * reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testReadDeviceInfoErrorVRInvalidAnswer() throws XBeeException, IOException {
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(remoteXBeeDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(remoteXBeeDevice).getParameter(PARAMETER_HV);
		
		// Throw an AT command exception when requesting the HV parameter value.
		Mockito.doThrow(new ATCommandException(null)).when(remoteXBeeDevice).getParameter(PARAMETER_VR);
		
		// Initialize the device.
		remoteXBeeDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.RemoteXBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a remote device cannot be read if there is a timeout reading 
	 * the MY parameter of the device.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testReadDeviceInfoErrorMYTimeout() throws XBeeException, IOException {
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(remoteXBeeDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(remoteXBeeDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(remoteXBeeDevice).getParameter(PARAMETER_VR);
		
		// Throw a timeout exception when requesting the MY parameter value.
		Mockito.doThrow(new TimeoutException()).when(remoteXBeeDevice).getParameter(PARAMETER_MY);
		
		// Initialize the device.
		remoteXBeeDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.RemoteXBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a remote device cannot be read if the answer when requesting 
	 * the MY parameter is null or the response status is not OK. It is, there is an AT command exception 
	 * reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testReadDeviceInfoErrorMYInvalidAnswer() throws XBeeException, IOException {
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(remoteXBeeDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(remoteXBeeDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(remoteXBeeDevice).getParameter(PARAMETER_VR);
		
		// Throw an AT command exception when requesting the MY parameter value.
		Mockito.doThrow(new ATCommandException(null)).when(remoteXBeeDevice).getParameter(PARAMETER_MY);
		
		// Initialize the device.
		remoteXBeeDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.RemoteXBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a remote device can be read successfully.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testReadDeviceInfoSuccess() throws XBeeException, IOException {
		// Return that the protocol of the device is ZigBee when asked. This way the MY setting will be read.
		Mockito.when(remoteXBeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(remoteXBeeDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(remoteXBeeDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(remoteXBeeDevice).getParameter(PARAMETER_VR);
		
		// Return a valid response when requesting the MY parameter value.
		Mockito.doReturn(RESPONSE_MY).when(remoteXBeeDevice).getParameter(PARAMETER_MY);
		
		// Initialize the device.
		remoteXBeeDevice.readDeviceInfo();
		
		// 64-bit address shouldn't have been read.
		Mockito.verify(remoteXBeeDevice, Mockito.never()).getParameter(PARAMETER_SH);
		Mockito.verify(remoteXBeeDevice, Mockito.never()).getParameter(PARAMETER_SL);
		assertEquals(MAC_ADDRESS, remoteXBeeDevice.get64BitAddress().toString());
		
		// Verify rest of parameters were read successfully.
		assertEquals("7654", remoteXBeeDevice.get16BitAddress().toString());
		assertEquals("XBEE", remoteXBeeDevice.getNodeID());
		assertEquals(HardwareVersion.get(0x01), remoteXBeeDevice.getHardwareVersion());
		assertEquals("4567", remoteXBeeDevice.getFirmwareVersion());
		assertEquals(XBeeProtocol.ZIGBEE, remoteXBeeDevice.getXBeeProtocol());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.RemoteXBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a remote device can be read successfully when the 
	 * protocol of the device is DigiMesh. In this case the method won't read the network 
	 * address, so its value should remain as {@code XBee16BitAddress#UNKNOWN_ADDRESS}.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testReadDeviceInfoSuccessDigiMesh() throws XBeeException, IOException {
		// Return that the protocol of the device is DigiMesh when asked. This way the MY setting won't be read.
		Mockito.when(remoteXBeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.DIGI_MESH);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(remoteXBeeDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(remoteXBeeDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(remoteXBeeDevice).getParameter(PARAMETER_VR);
		
		// Return a valid response when requesting the MY parameter value.
		Mockito.doReturn(RESPONSE_MY).when(remoteXBeeDevice).getParameter(PARAMETER_MY);
		
		// Initialize the device.
		remoteXBeeDevice.readDeviceInfo();
		
		// 64-bit address shouldn't have been read.
		Mockito.verify(remoteXBeeDevice, Mockito.never()).getParameter(PARAMETER_SH);
		Mockito.verify(remoteXBeeDevice, Mockito.never()).getParameter(PARAMETER_SL);
		assertEquals(MAC_ADDRESS, remoteXBeeDevice.get64BitAddress().toString());
		
		// Verify rest of parameters but the network address were read successfully.
		assertEquals(XBee16BitAddress.UNKNOWN_ADDRESS, remoteXBeeDevice.get16BitAddress());
		assertEquals("XBEE", remoteXBeeDevice.getNodeID());
		assertEquals(HardwareVersion.get(0x01), remoteXBeeDevice.getHardwareVersion());
		assertEquals("4567", remoteXBeeDevice.getFirmwareVersion());
		assertEquals(XBeeProtocol.DIGI_MESH, remoteXBeeDevice.getXBeeProtocol());
	}
}
