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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

import java.io.IOException;

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
import com.digi.xbee.api.exceptions.ATCommandException;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.HardwareVersion;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeProtocol;

@PrepareForTest({XBeeProtocol.class})
@RunWith(PowerMockRunner.class)
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
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	// Variables.
	private RemoteXBeeDevice remoteXBeeDevice;
	
	private RemoteRaw802Device remote802Device;
	private RemoteDigiMeshDevice remoteDmDevice;
	private RemoteDigiPointDevice remoteDpDevice;
	private RemoteZigBeeDevice remoteZbDevice;
	
	@Before
	public void setup() throws Exception {
		SerialPortRxTx mockPort = Mockito.mock(SerialPortRxTx.class);
		
		// Mock the local XBee device necessary to instantiate a remote one.
		XBeeDevice localXBeeDevice = Mockito.mock(XBeeDevice.class);
		Mockito.when(localXBeeDevice.getConnectionInterface()).thenReturn(mockPort);
		Mockito.when(localXBeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
		
		Raw802Device local802Device = Mockito.mock(Raw802Device.class);
		Mockito.when(local802Device.getConnectionInterface()).thenReturn(mockPort);
		
		DigiMeshDevice localDMDevice = Mockito.mock(DigiMeshDevice.class);
		Mockito.when(localDMDevice.getConnectionInterface()).thenReturn(mockPort);
		
		ZigBeeDevice localZBDevice = Mockito.mock(ZigBeeDevice.class);
		Mockito.when(localZBDevice.getConnectionInterface()).thenReturn(mockPort);
		
		DigiPointDevice localDPDevice = Mockito.mock(DigiPointDevice.class);
		Mockito.when(localDPDevice.getConnectionInterface()).thenReturn(mockPort);
		
		XBee64BitAddress remoteAddress = new XBee64BitAddress(MAC_ADDRESS);
		
		// Instantiate a RemoteXBeeDevice object with basic parameters.
		remoteXBeeDevice = PowerMockito.spy(new RemoteXBeeDevice(localXBeeDevice, remoteAddress));
		
		remote802Device = PowerMockito.spy(new RemoteRaw802Device(local802Device, remoteAddress));
		remoteDmDevice = PowerMockito.spy(new RemoteDigiMeshDevice(localDMDevice, remoteAddress));
		remoteDpDevice = PowerMockito.spy(new RemoteDigiPointDevice(localDPDevice, remoteAddress));
		remoteZbDevice = PowerMockito.spy(new RemoteZigBeeDevice(localZBDevice, remoteAddress));
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
		XBeeProtocol protocol = XBeeProtocol.ZIGBEE;
		
		// Return that the protocol of the device is ZigBee.
		Mockito.when(remoteXBeeDevice.getXBeeProtocol()).thenReturn(protocol);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(remoteXBeeDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(remoteXBeeDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(remoteXBeeDevice).getParameter(PARAMETER_VR);
		
		// Throw a timeout exception when requesting the MY parameter value.
		Mockito.doThrow(new TimeoutException()).when(remoteXBeeDevice).getParameter(PARAMETER_MY);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(protocol);
		
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
		XBeeProtocol protocol = XBeeProtocol.ZIGBEE;
		
		// Return that the protocol of the device is ZigBee.
		Mockito.when(remoteXBeeDevice.getXBeeProtocol()).thenReturn(protocol);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(remoteXBeeDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(remoteXBeeDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(remoteXBeeDevice).getParameter(PARAMETER_VR);
		
		// Throw an AT command exception when requesting the MY parameter value.
		Mockito.doThrow(new ATCommandException(null)).when(remoteXBeeDevice).getParameter(PARAMETER_MY);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(protocol);
		
		// Initialize the device.
		remoteXBeeDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.RemoteDigiMeshDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a ZigBee remote device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalidDigiMeshClassForZBDevice() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.ZIGBEE;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + remoteDmDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(remoteDmDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(remoteDmDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(remoteDmDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		remoteDmDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.RemoteDigiPointDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a ZigBee remote device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalidDigiPointClassForZBDevice() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.ZIGBEE;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + remoteDpDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(remoteDpDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(remoteDpDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(remoteDpDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		remoteDpDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.RemoteRaw802Device#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a ZigBee remote device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalid802ClassForZBDevice() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.ZIGBEE;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + remote802Device.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(remote802Device).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(remote802Device).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(remote802Device).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		remote802Device.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.RemoteDigiMeshDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a DigiPoint remote device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalidDigiMeshClassForDPDevice() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.DIGI_POINT;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + remoteDmDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(remoteDmDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(remoteDmDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(remoteDmDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		remoteDmDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.RemoteZigBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a DigiPoint remote device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalidZigBeeClassForDPDevice() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.DIGI_POINT;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + remoteZbDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(remoteZbDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(remoteZbDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(remoteZbDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		remoteZbDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.RemoteRaw802Device#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a DigiPoint remote device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalid802ClassForDPDevice() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.DIGI_POINT;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + remote802Device.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(remote802Device).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(remote802Device).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(remote802Device).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		remote802Device.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.RemoteDigiPointDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a DigiMesh remote device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalidDigiPointClassForDMDevice() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.DIGI_MESH;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + remoteDpDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(remoteDpDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(remoteDpDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(remoteDpDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		remoteDpDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.RemoteZigBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a DigiMesh remote device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalidZigBeeClassForDMDevice() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.DIGI_MESH;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + remoteZbDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(remoteZbDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(remoteZbDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(remoteZbDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		remoteZbDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.RemoteRaw802Device#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a DigiMesh remote device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalid802ClassForDMDevice() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.DIGI_MESH;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + remote802Device.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(remote802Device).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(remote802Device).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(remote802Device).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		remote802Device.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.RemoteDigiMeshDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a 802.15.4 local device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalidDigiMeshClassFor802Device() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.RAW_802_15_4;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + remoteDmDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(remoteDmDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(remoteDmDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(remoteDmDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		remoteDmDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.RemoteZigBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a 802.15.4 local device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalidZigBeeClassFor802Device() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.RAW_802_15_4;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + remoteZbDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(remoteZbDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(remoteZbDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(remoteZbDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		remoteZbDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.RemoteRaw802Device#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a 802.15.4 local device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalidDigiPointClassFor802Device() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.RAW_802_15_4;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + remoteDpDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(remoteDpDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(remoteDpDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(remoteDpDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		remoteDpDevice.readDeviceInfo();
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
		XBeeProtocol protocol = XBeeProtocol.ZIGBEE;
		
		// Return that the protocol of the device is ZigBee when asked. This way the MY setting will be read.
		Mockito.when(remoteXBeeDevice.getXBeeProtocol()).thenReturn(protocol);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(remoteXBeeDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(remoteXBeeDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(remoteXBeeDevice).getParameter(PARAMETER_VR);
		
		// Return a valid response when requesting the MY parameter value.
		Mockito.doReturn(RESPONSE_MY).when(remoteXBeeDevice).getParameter(PARAMETER_MY);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(protocol);
		
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
		XBeeProtocol protocol = XBeeProtocol.DIGI_MESH;
		
		// Return that the protocol of the device is DigiMesh when asked. This way the MY setting won't be read.
		Mockito.when(remoteXBeeDevice.getXBeeProtocol()).thenReturn(protocol);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(remoteXBeeDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(remoteXBeeDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(remoteXBeeDevice).getParameter(PARAMETER_VR);
		
		// Return a valid response when requesting the MY parameter value.
		Mockito.doReturn(RESPONSE_MY).when(remoteXBeeDevice).getParameter(PARAMETER_MY);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(protocol);
		
		// Initialize the device.
		remoteXBeeDevice.readDeviceInfo();
		
		// 64-bit address shouldn't have been read.
		Mockito.verify(remoteXBeeDevice, Mockito.never()).getParameter(PARAMETER_SH);
		Mockito.verify(remoteXBeeDevice, Mockito.never()).getParameter(PARAMETER_SL);
		assertEquals(MAC_ADDRESS, remoteXBeeDevice.get64BitAddress().toString());
		
		// Verify rest of parameters but the network address were read successfully.
		Mockito.verify(remoteXBeeDevice, Mockito.never()).getParameter(PARAMETER_MY);
		assertEquals(XBee16BitAddress.UNKNOWN_ADDRESS, remoteXBeeDevice.get16BitAddress());
		assertEquals("XBEE", remoteXBeeDevice.getNodeID());
		assertEquals(HardwareVersion.get(0x01), remoteXBeeDevice.getHardwareVersion());
		assertEquals("4567", remoteXBeeDevice.getFirmwareVersion());
		assertEquals(XBeeProtocol.DIGI_MESH, remoteXBeeDevice.getXBeeProtocol());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.RemoteXBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a remote device can be read successfully when the 
	 * protocol of the device is DigiPoint. In this case the method won't read the network 
	 * address, so its value should remain as {@code XBee16BitAddress#UNKNOWN_ADDRESS}.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testReadDeviceInfoSuccessDigiPoint() throws XBeeException, IOException {
		XBeeProtocol protocol = XBeeProtocol.DIGI_POINT;
		
		// Return that the protocol of the device is DigiMesh when asked. This way the MY setting won't be read.
		Mockito.when(remoteXBeeDevice.getXBeeProtocol()).thenReturn(protocol);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(remoteXBeeDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(remoteXBeeDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(remoteXBeeDevice).getParameter(PARAMETER_VR);
		
		// Return a valid response when requesting the MY parameter value.
		Mockito.doReturn(RESPONSE_MY).when(remoteXBeeDevice).getParameter(PARAMETER_MY);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(protocol);
		
		// Initialize the device.
		remoteXBeeDevice.readDeviceInfo();
		
		// 64-bit address shouldn't have been read.
		Mockito.verify(remoteXBeeDevice, Mockito.never()).getParameter(PARAMETER_SH);
		Mockito.verify(remoteXBeeDevice, Mockito.never()).getParameter(PARAMETER_SL);
		assertEquals(MAC_ADDRESS, remoteXBeeDevice.get64BitAddress().toString());
		
		// Verify rest of parameters but the network address were read successfully.
		Mockito.verify(remoteXBeeDevice, Mockito.never()).getParameter(PARAMETER_MY);
		assertEquals(XBee16BitAddress.UNKNOWN_ADDRESS, remoteXBeeDevice.get16BitAddress());
		assertEquals("XBEE", remoteXBeeDevice.getNodeID());
		assertEquals(HardwareVersion.get(0x01), remoteXBeeDevice.getHardwareVersion());
		assertEquals("4567", remoteXBeeDevice.getFirmwareVersion());
		assertEquals(XBeeProtocol.DIGI_POINT, remoteXBeeDevice.getXBeeProtocol());
	}
}
