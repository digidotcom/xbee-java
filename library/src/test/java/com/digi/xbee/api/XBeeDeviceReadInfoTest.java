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
import com.digi.xbee.api.models.XBeeProtocol;

@PrepareForTest({XBeeProtocol.class})
@RunWith(PowerMockRunner.class)
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
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	// Variables.
	private XBeeDevice xbeeDevice;
	private DigiMeshDevice dmDevice;
	private DigiPointDevice dpDevice;
	private ZigBeeDevice zbDevice;
	private Raw802Device r802Device;
	private CellularDevice cellularDevice;
	private WiFiDevice wifiDevice;
	
	@Before
	public void setup() throws Exception {
		SerialPortRxTx mockPort = Mockito.mock(SerialPortRxTx.class);
		
		// Instantiate an XBeeDevice object with basic parameters.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(mockPort));
		dmDevice = PowerMockito.spy(new DigiMeshDevice(mockPort));
		dpDevice = PowerMockito.spy(new DigiPointDevice(mockPort));
		zbDevice = PowerMockito.spy(new ZigBeeDevice(mockPort));
		r802Device = PowerMockito.spy(new Raw802Device(mockPort));
		cellularDevice = PowerMockito.spy(new CellularDevice(mockPort));
		wifiDevice = PowerMockito.spy(new WiFiDevice(mockPort));
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
		// Return that the protocol of the device is ZigBee.
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
		
		// Throw a timeout exception when requesting the MY parameter value.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).getParameter(PARAMETER_MY);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(XBeeProtocol.ZIGBEE);
		
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
		// Return that the protocol of the device is ZigBee.
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
		
		// Throw an AT command exception when requesting the MY parameter value.
		Mockito.doThrow(new ATCommandException(null)).when(xbeeDevice).getParameter(PARAMETER_MY);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(XBeeProtocol.ZIGBEE);
		
		// Initialize the device.
		xbeeDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a ZigBee local device cannot be read if 
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
				+ " and NOT " + dmDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(dmDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(dmDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(dmDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(dmDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(dmDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		dmDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a ZigBee local device cannot be read if 
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
				+ " and NOT " + dpDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(dpDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(dpDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(dpDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(dpDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(dpDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		dpDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a ZigBee local device cannot be read if 
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
				+ " and NOT " + r802Device.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(r802Device).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(r802Device).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(r802Device).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(r802Device).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(r802Device).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		r802Device.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a ZigBee local device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalidCellularClassForZBDevice() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.ZIGBEE;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + cellularDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(cellularDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(cellularDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(cellularDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(cellularDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(cellularDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		cellularDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a ZigBee local device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalidWiFiClassForZBDevice() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.ZIGBEE;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + wifiDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(wifiDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(wifiDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(wifiDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(wifiDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(wifiDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		wifiDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a DigiPoint local device cannot be read if 
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
				+ " and NOT " + dmDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(dmDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(dmDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(dmDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(dmDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(dmDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		dmDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a DigiPoint local device cannot be read if 
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
				+ " and NOT " + zbDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(zbDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(zbDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(zbDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(zbDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(zbDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		zbDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a DigiPoint local device cannot be read if 
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
				+ " and NOT " + r802Device.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(r802Device).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(r802Device).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(r802Device).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(r802Device).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(r802Device).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		r802Device.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a DigiPoint local device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalidCellularClassForDPDevice() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.DIGI_POINT;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + cellularDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(cellularDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(cellularDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(cellularDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(cellularDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(cellularDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		cellularDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a DigiPoint local device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalidWiFiClassForDPDevice() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.DIGI_POINT;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + wifiDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(wifiDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(wifiDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(wifiDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(wifiDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(wifiDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		wifiDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
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
				+ " and NOT " + dmDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(dmDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(dmDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(dmDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(dmDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(dmDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		dmDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
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
				+ " and NOT " + zbDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(zbDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(zbDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(zbDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(zbDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(zbDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		zbDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
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
				+ " and NOT " + dpDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(dpDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(dpDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(dpDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(dpDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(dpDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		dpDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a 802.15.4 local device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalidCellularClassFor802Device() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.RAW_802_15_4;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + cellularDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(cellularDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(cellularDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(cellularDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(cellularDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(cellularDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		cellularDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a 802.15.4 local device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalidWiFiClassFor802Device() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.RAW_802_15_4;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + wifiDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(wifiDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(wifiDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(wifiDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(wifiDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(wifiDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		wifiDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a DigiMesh local device cannot be read if 
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
				+ " and NOT " + dpDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(dpDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(dpDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(dpDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(dpDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(dpDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		dpDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a DigiMesh local device cannot be read if 
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
				+ " and NOT " + zbDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(zbDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(zbDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(zbDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(zbDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(zbDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		zbDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a DigiMesh local device cannot be read if 
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
				+ " and NOT " + r802Device.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(r802Device).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(r802Device).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(r802Device).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(r802Device).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(r802Device).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		r802Device.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a DigiMesh local device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalidCellularClassForDMDevice() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.DIGI_MESH;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + cellularDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(cellularDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(cellularDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(cellularDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(cellularDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(cellularDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		cellularDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a DigiMesh local device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalidWiFiClassForDMDevice() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.DIGI_MESH;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + wifiDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(wifiDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(wifiDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(wifiDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(wifiDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(wifiDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		wifiDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a Cellular local device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalidDigiMeshClassForCellularDevice() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.CELLULAR;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + dmDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(dmDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(dmDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(dmDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(dmDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(dmDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		dmDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a Cellular local device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalidDigiPointClassForCellularDevice() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.CELLULAR;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + dpDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(dpDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(dpDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(dpDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(dpDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(dpDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		dpDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a Cellular local device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalidZigBeeClassForCellularDevice() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.CELLULAR;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + zbDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(zbDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(zbDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(zbDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(zbDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(zbDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		zbDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a Cellular local device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalid802ClassForCellularDevice() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.CELLULAR;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + r802Device.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(r802Device).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(r802Device).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(r802Device).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(r802Device).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(r802Device).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		r802Device.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a Cellular local device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalidWiFiClassForCellularDevice() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.CELLULAR;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + wifiDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(wifiDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(wifiDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(wifiDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(wifiDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(wifiDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		wifiDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a Wi-Fi local device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalidDigiMeshClassForWiFiDevice() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.XBEE_WIFI;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + dmDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(dmDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(dmDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(dmDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(dmDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(dmDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		dmDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a Wi-Fi local device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalidDigiPointClassForWiFiDevice() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.XBEE_WIFI;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + dpDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(dpDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(dpDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(dpDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(dpDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(dpDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		dpDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a Wi-Fi local device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalidZigBeeClassForWiFiDevice() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.XBEE_WIFI;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + zbDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(zbDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(zbDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(zbDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(zbDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(zbDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		zbDevice.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a Wi-Fi local device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalid802ClassForWiFiDevice() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.XBEE_WIFI;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + r802Device.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(r802Device).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(r802Device).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(r802Device).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(r802Device).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(r802Device).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		r802Device.readDeviceInfo();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a Wi-Fi local device cannot be read if 
	 * the class for the specified device is not the right one. It is, there is 
	 * an AT command exception reading the parameter.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test
	public void testReadDeviceInfoErrorInvalidCellularClassForWiFiDevice() throws XBeeException, IOException {
		// Setup the resources for the test.
		XBeeProtocol realProtocol = XBeeProtocol.XBEE_WIFI;
		
		exception.expect(XBeeException.class);
		exception.expectMessage(is(equalTo("Error reading device information: "
				+ "Your module seems to be " + realProtocol 
				+ " and NOT " + cellularDevice.getXBeeProtocol() + ". Check if you are using" 
				+ " the appropriate device class.")));
		
		// Return a valid response when requesting the SH parameter value.
		Mockito.doReturn(RESPONSE_SH).when(cellularDevice).getParameter(PARAMETER_SH);
		
		// Return a valid response when requesting the SL parameter value.
		Mockito.doReturn(RESPONSE_SL).when(cellularDevice).getParameter(PARAMETER_SL);
		
		// Return a valid response when requesting the NI parameter value.
		Mockito.doReturn(RESPONSE_NI).when(cellularDevice).getParameter(PARAMETER_NI);
		
		// Return a valid response when requesting the HV parameter value.
		Mockito.doReturn(RESPONSE_HV).when(cellularDevice).getParameter(PARAMETER_HV);
		
		// Return a valid response when requesting the VR parameter value.
		Mockito.doReturn(RESPONSE_VR).when(cellularDevice).getParameter(PARAMETER_VR);
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(realProtocol);
		
		// Execute the method under test.
		cellularDevice.readDeviceInfo();
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
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.UNKNOWN, XBeeProtocol.ZIGBEE);
		
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
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(XBeeProtocol.ZIGBEE);
		
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
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.UNKNOWN, XBeeProtocol.DIGI_MESH);
		
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
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(XBeeProtocol.DIGI_MESH);
		
		// Initialize the device.
		xbeeDevice.readDeviceInfo();
		
		// Verify that all parameters but the network address were read successfully.
		Mockito.verify(xbeeDevice, Mockito.times(2)).getXBeeProtocol();
		Mockito.verify(xbeeDevice, Mockito.never()).getParameter(PARAMETER_MY);
		assertEquals(XBee16BitAddress.UNKNOWN_ADDRESS, xbeeDevice.get16BitAddress());
		assertEquals("XBEE", xbeeDevice.getNodeID());
		assertEquals(HardwareVersion.get(0x01), xbeeDevice.getHardwareVersion());
		assertEquals("4567", xbeeDevice.getFirmwareVersion());
		assertEquals(XBeeProtocol.DIGI_MESH, xbeeDevice.getXBeeProtocol());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a local device can be read successfully when the 
	 * protocol of the device is DigiPoint. In this case the method won't read the network 
	 * address, so its value should remain as {@code XBee16BitAddress#UNKNOWN_ADDRESS}.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testReadDeviceInfoSuccessDigiPoint() throws XBeeException, IOException {
		// Return that the protocol of the device is first unknown and then DigiPoint when asked.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.UNKNOWN, XBeeProtocol.DIGI_POINT);
		
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
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(XBeeProtocol.DIGI_POINT);
		
		// Initialize the device.
		xbeeDevice.readDeviceInfo();
		
		// Verify that all parameters but the network address were read successfully.
		Mockito.verify(xbeeDevice, Mockito.times(2)).getXBeeProtocol();
		Mockito.verify(xbeeDevice, Mockito.never()).getParameter(PARAMETER_MY);
		assertEquals(XBee16BitAddress.UNKNOWN_ADDRESS, xbeeDevice.get16BitAddress());
		assertEquals("XBEE", xbeeDevice.getNodeID());
		assertEquals(HardwareVersion.get(0x01), xbeeDevice.getHardwareVersion());
		assertEquals("4567", xbeeDevice.getFirmwareVersion());
		assertEquals(XBeeProtocol.DIGI_POINT, xbeeDevice.getXBeeProtocol());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a local device can be read successfully when the 
	 * protocol of the device is Cellular. In this case the 64-bit and 16-bit addresses 
	 * should be null, but the method should read valid values for the IP and IMEI
	 * addresses.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testReadDeviceInfoSuccessCellular() throws XBeeException, IOException {
		// Return that the protocol of the device is first unknown and then DigiPoint when asked.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.UNKNOWN, XBeeProtocol.CELLULAR);
		
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
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(XBeeProtocol.DIGI_POINT);
		
		// Initialize the device.
		xbeeDevice.readDeviceInfo();
		
		// Verify that all parameters but the network address were read successfully.
		Mockito.verify(xbeeDevice, Mockito.times(2)).getXBeeProtocol();
		Mockito.verify(xbeeDevice, Mockito.never()).getParameter(PARAMETER_MY);
		assertEquals(XBee16BitAddress.UNKNOWN_ADDRESS, xbeeDevice.get16BitAddress());
		assertEquals("XBEE", xbeeDevice.getNodeID());
		assertEquals(HardwareVersion.get(0x01), xbeeDevice.getHardwareVersion());
		assertEquals("4567", xbeeDevice.getFirmwareVersion());
		assertEquals(XBeeProtocol.CELLULAR, xbeeDevice.getXBeeProtocol());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a local device can be read successfully when the 
	 * protocol of the device is Wi-Fi. In this case the16-bit address should be null, 
	 * but the method should read valid values for the IP address.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testReadDeviceInfoSuccessWiFi() throws XBeeException, IOException {
		// Return that the protocol of the device is first unknown and then DigiPoint when asked.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.UNKNOWN, XBeeProtocol.CELLULAR);
		
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
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(XBeeProtocol.DIGI_POINT);
		
		// Initialize the device.
		xbeeDevice.readDeviceInfo();
		
		// Verify that all parameters but the network address were read successfully.
		Mockito.verify(xbeeDevice, Mockito.times(2)).getXBeeProtocol();
		Mockito.verify(xbeeDevice, Mockito.never()).getParameter(PARAMETER_MY);
		assertEquals(XBee16BitAddress.UNKNOWN_ADDRESS, xbeeDevice.get16BitAddress());
		assertEquals("XBEE", xbeeDevice.getNodeID());
		assertEquals(HardwareVersion.get(0x01), xbeeDevice.getHardwareVersion());
		assertEquals("4567", xbeeDevice.getFirmwareVersion());
		assertEquals(XBeeProtocol.CELLULAR, xbeeDevice.getXBeeProtocol());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that device info of a local device can be read successfully when the 
	 * protocol of the device is DigiPoint. In this case the method won't read the network 
	 * address, so its value should remain as {@code XBee16BitAddress#UNKNOWN_ADDRESS}.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testReadDeviceInfoSuccessUnknownProtocol() throws XBeeException, IOException {
		// Return that the protocol of the device is Unknown when asked.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.UNKNOWN);
		
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
		
		// Return the "real" value of the module protocol.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol(Mockito.any(HardwareVersion.class), Mockito.anyString())).thenReturn(XBeeProtocol.UNKNOWN);
		
		// Initialize the device.
		xbeeDevice.readDeviceInfo();
		
		// Verify that all parameters but the network address were read successfully.
		Mockito.verify(xbeeDevice, Mockito.times(2)).getXBeeProtocol();
		Mockito.verify(xbeeDevice, Mockito.never()).getParameter(PARAMETER_MY);
		assertEquals(XBee16BitAddress.UNKNOWN_ADDRESS, xbeeDevice.get16BitAddress());
		assertEquals("XBEE", xbeeDevice.getNodeID());
		assertEquals(HardwareVersion.get(0x01), xbeeDevice.getHardwareVersion());
		assertEquals("4567", xbeeDevice.getFirmwareVersion());
		assertEquals(XBeeProtocol.UNKNOWN, xbeeDevice.getXBeeProtocol());
	}
}
