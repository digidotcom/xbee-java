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
import com.digi.xbee.api.models.XBeeProtocol;

public class XBeeDeviceReadInfoTest {

	// Constants.
	private static final String PARAMETER_SH = "SH";
	private static final String PARAMETER_SL = "SL";
	private static final String PARAMETER_NI = "NI";
	private static final String PARAMETER_HV = "HV";
	private static final String PARAMETER_VR = "VR";
	private static final String PARAMETER_MY = "MY";
	
	private static final byte[] RESPONSE_SH = new byte[]{0x01, 0x23, 0x45, 0x67};                             // 0x01234567
	private static final byte[] RESPONSE_SL = new byte[]{(byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF}; // 0x89ABCDEF
	private static final byte[] RESPONSE_NI = new byte[]{0x58, 0x42, 0x45, 0x45};                             // XBEE
	private static final byte[] RESPONSE_HV = new byte[]{0x01, 0x23};                                         // 0x0123
	private static final byte[] RESPONSE_VR = new byte[]{0x45, 0x67};                                         // 0x4567
	private static final byte[] RESPONSE_MY = new byte[]{0x76, 0x54};                                         // 0x7654
	
	// Variables.
	private XBeeDevice xbeeDevice;
	
	@Before
	public void setup() throws Exception {
		// Instantiate an XBeeDevice object with basic parameters.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(Mockito.mock(SerialPortRxTx.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a local device cannot be read if the connection is closed.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testReadDeviceInfoErrorConnectionClosed() throws XBeeException {
		// Throw an interface not open exception when trying to read any parameter.
		Mockito.doThrow(new InterfaceNotOpenException()).when(xbeeDevice).getParameter(Mockito.anyString());
		
		// Initialize the device.
		xbeeDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a local device cannot be read if the operating mode is not valid.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testReadDeviceInfoErrorInvalidOperatingMode() throws XBeeException {
		// Throw an invalid operating mode exception when trying to read any parameter.
		Mockito.doThrow(new InvalidOperatingModeException()).when(xbeeDevice).getParameter(Mockito.anyString());
		
		// Initialize the device.
		xbeeDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a local device cannot be read if there is a timeout reading the 
	 * SH parameter of the device.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testReadDeviceInfoErrorSHTimeout() throws XBeeException, IOException {
		// Throw a timeout exception when requesting the SH parameter value.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).getParameter(PARAMETER_SH);
		
		// Initialize the device.
		xbeeDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a local device cannot be read if the answer when requesting the 
	 * SH parameter is null or the response status is not OK. It is, there is an AT command exception 
	 * reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testReadDeviceInfoErrorSHInvalidAnswer() throws XBeeException, IOException {
		// Throw an AT command exception when requesting the SH parameter value.
		Mockito.doThrow(new ATCommandException(null)).when(xbeeDevice).getParameter(PARAMETER_SH);
		
		// Initialize the device.
		xbeeDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a local device cannot be read if there is a timeout reading the 
	 * SL parameter of the device.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testReadDeviceInfoErrorSLTimeout() throws XBeeException, IOException {
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(xbeeDevice).getParameter(PARAMETER_SH);
		
		// Throw a timeout exception when requesting the SL parameter value.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).getParameter(PARAMETER_SL);
				
		// Initialize the device.
		xbeeDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a local device cannot be read if the answer when requesting the 
	 * SL parameter is null or the response status is not OK. It is, there is an AT command exception 
	 * reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testReadDeviceInfoErrorSLInvalidAnswer() throws XBeeException, IOException {
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(xbeeDevice).getParameter(PARAMETER_SH);
		
		// Throw an AT command exception when requesting the SL parameter value.
		Mockito.doThrow(new ATCommandException(null)).when(xbeeDevice).getParameter(PARAMETER_SL);
		
		// Initialize the device.
		xbeeDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a local device cannot be read if there is a timeout reading the 
	 * NI parameter of the device.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testReadDeviceInfoErrorNITimeout() throws XBeeException, IOException {
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(xbeeDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(xbeeDevice).getParameter(PARAMETER_SL);
		
		// Throw a timeout exception when requesting the NI parameter value.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).getParameter(PARAMETER_NI);
		
		// Initialize the device.
		xbeeDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a local device cannot be read if the answer when requesting the 
	 * NI parameter is null or the response status is not OK. It is, there is an AT command exception 
	 * reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testReadDeviceInfoErrorNIInvalidAnswer() throws XBeeException, IOException {
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(xbeeDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(xbeeDevice).getParameter(PARAMETER_SL);
		
		// Throw an AT command exception when requesting the NI parameter value.
		Mockito.doThrow(new ATCommandException(null)).when(xbeeDevice).getParameter(PARAMETER_NI);
		
		// Initialize the device.
		xbeeDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a local device cannot be read if there is a timeout reading the 
	 * HV parameter of the device.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testReadDeviceInfoErrorHVTimeout() throws XBeeException, IOException {
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(xbeeDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(xbeeDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(xbeeDevice).getParameter(PARAMETER_NI);
		
		// Throw a timeout exception when requesting the HV parameter value.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).getParameter(PARAMETER_HV);
		
		// Initialize the device.
		xbeeDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a local device cannot be read if the answer when requesting the 
	 * HV parameter is null or the response status is not OK. It is, there is an AT command exception 
	 * reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testReadDeviceInfoErrorHVInvalidAnswer() throws XBeeException, IOException {
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(xbeeDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(xbeeDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(xbeeDevice).getParameter(PARAMETER_NI);
		
		// Throw an AT command exception when requesting the HV parameter value.
		Mockito.doThrow(new ATCommandException(null)).when(xbeeDevice).getParameter(PARAMETER_HV);
		
		// Initialize the device.
		xbeeDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a local device cannot be read if there is a timeout reading the 
	 * VR parameter of the device.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testReadDeviceInfoErrorVRTimeout() throws XBeeException, IOException {
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(xbeeDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(xbeeDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(xbeeDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(xbeeDevice).getParameter(PARAMETER_HV);
		
		// Throw a timeout exception when requesting the VR parameter value.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).getParameter(PARAMETER_VR);
		
		// Initialize the device.
		xbeeDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a local device cannot be read if the answer when requesting the 
	 * VR parameter is null or the response status is not OK. It is, there is an AT command exception 
	 * reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testReadDeviceInfoErrorVRInvalidAnswer() throws XBeeException, IOException {
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(xbeeDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(xbeeDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(xbeeDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(xbeeDevice).getParameter(PARAMETER_HV);
		
		// Throw an AT command exception when requesting the VR parameter value.
		Mockito.doThrow(new ATCommandException(null)).when(xbeeDevice).getParameter(PARAMETER_VR);
		
		// Initialize the device.
		xbeeDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a local device cannot be read if there is a timeout reading the 
	 * MY parameter of the device.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testReadDeviceInfoErrorMYTimeout() throws XBeeException, IOException {
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(xbeeDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(xbeeDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(xbeeDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(xbeeDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(xbeeDevice).getParameter(PARAMETER_VR);
		
		// Throw a timeout exception when requesting the MY parameter value.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).getParameter(PARAMETER_MY);
		
		// Initialize the device.
		xbeeDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a local device cannot be read if the answer when requesting the 
	 * MY parameter is null or the response status is not OK. It is, there is an AT command exception 
	 * reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testReadDeviceInfoErrorMYInvalidAnswer() throws XBeeException, IOException {
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(xbeeDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(xbeeDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(xbeeDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(xbeeDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(xbeeDevice).getParameter(PARAMETER_VR);
		
		// Throw an AT command exception when requesting the MY parameter value.
		Mockito.doThrow(new ATCommandException(null)).when(xbeeDevice).getParameter(PARAMETER_MY);
		
		// Initialize the device.
		xbeeDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a local device can be read successfully.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testReadDeviceInfoSuccess() throws XBeeException, IOException {
		// Return that the protocol of the device is ZigBee when asked. This way the MY setting will be read.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(xbeeDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(xbeeDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(xbeeDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(xbeeDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(xbeeDevice).getParameter(PARAMETER_VR);
		
		// Return a valid response when requesting the MY parameter value.
		Mockito.doReturn(RESPONSE_MY).when(xbeeDevice).getParameter(PARAMETER_MY);
		
		// Initialize the device.
		xbeeDevice.readDeviceInfo();
		
		// Verify that all parameters were read successfully.
		assertEquals("7654", xbeeDevice.get16BitAddress().toString());
		assertEquals("XBEE", xbeeDevice.getNodeID());
		assertEquals(HardwareVersion.get(0x01), xbeeDevice.getHardwareVersion());
		assertEquals("4567", xbeeDevice.getFirmwareVersion());
		assertEquals(XBeeProtocol.ZIGBEE, xbeeDevice.getXBeeProtocol());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a local device can be read successfully when the 
	 * protocol of the device is DigiMesh. In this case the method won't read the network 
	 * address, so its value should remain as {@code XBee16BitAddress#UNKNOWN_ADDRESS}.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testReadDeviceInfoSuccessDigiMesh() throws XBeeException, IOException {
		// Return that the protocol of the device is DigiMesh when asked. This way the MY setting won't be read.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.DIGI_MESH);
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(xbeeDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(xbeeDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(xbeeDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(xbeeDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(xbeeDevice).getParameter(PARAMETER_VR);
		
		// Return a valid response when requesting the MY parameter value.
		Mockito.doReturn(RESPONSE_MY).when(xbeeDevice).getParameter(PARAMETER_MY);
		
		// Initialize the device.
		xbeeDevice.readDeviceInfo();
		
		// Verify that all parameters but the network address were read successfully.
		assertEquals(XBee16BitAddress.UNKNOWN_ADDRESS, xbeeDevice.get16BitAddress());
		assertEquals("XBEE", xbeeDevice.getNodeID());
		assertEquals(HardwareVersion.get(0x01), xbeeDevice.getHardwareVersion());
		assertEquals("4567", xbeeDevice.getFirmwareVersion());
		assertEquals(XBeeProtocol.DIGI_MESH, xbeeDevice.getXBeeProtocol());
	}
}
