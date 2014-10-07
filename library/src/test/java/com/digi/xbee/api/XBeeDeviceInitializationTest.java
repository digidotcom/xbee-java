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
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.ATCommandException;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.OperationNotSupportedException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.ATCommand;
import com.digi.xbee.api.models.ATCommandResponse;
import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.models.HardwareVersion;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeProtocol;

@RunWith(PowerMockRunner.class)
@PrepareForTest({XBeeDevice.class, XBeeProtocol.class})
public class XBeeDeviceInitializationTest {

	// Constants.
	private static final byte[] RESPONSE_SH = new byte[]{0x01, 0x23, 0x45, 0x67};                             // 0x01234567
	private static final byte[] RESPONSE_SL = new byte[]{(byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF}; // 0x89ABCDEF
	private static final byte[] RESPONSE_NI = new byte[]{0x58, 0x42, 0x45, 0x45};                             // XBEE
	private static final byte[] RESPONSE_HV = new byte[]{0x01, 0x23};                                         // 0x0123
	private static final byte[] RESPONSE_VR = new byte[]{0x01, 0x23};                                         // 0x0123
	private static final byte[] RESPONSE_DUMMY = new byte[0];
	
	// Variables.
	private SerialPortRxTx mockedPort;
	private XBeeDevice xbeeDevice;
	
	private static ATCommand atCommandSH;
	private static ATCommand atCommandSL;
	private static ATCommand atCommandNI;
	private static ATCommand atCommandHV;
	private static ATCommand atCommandVR;
	
	private static ATCommandResponse atCommandResponseSH;
	private static ATCommandResponse atCommandResponseSL;
	private static ATCommandResponse atCommandResponseNI;
	private static ATCommandResponse atCommandResponseHV;
	private static ATCommandResponse atCommandResponseVR;
	private static ATCommandResponse atCommandResponseInvalid;
	
	@BeforeClass
	public static void setupOnce() {
		// Declare immutable mocks.
		atCommandSH = Mockito.mock(ATCommand.class);
		atCommandSL = Mockito.mock(ATCommand.class);
		atCommandNI = Mockito.mock(ATCommand.class);
		atCommandHV = Mockito.mock(ATCommand.class);
		atCommandVR = Mockito.mock(ATCommand.class);
		
		atCommandResponseSH = Mockito.mock(ATCommandResponse.class);
		Mockito.when(atCommandResponseSH.getResponseStatus()).thenReturn(ATCommandStatus.OK);
		Mockito.when(atCommandResponseSH.getResponse()).thenReturn(RESPONSE_SH);
		atCommandResponseSL = Mockito.mock(ATCommandResponse.class);
		Mockito.when(atCommandResponseSL.getResponseStatus()).thenReturn(ATCommandStatus.OK);
		Mockito.when(atCommandResponseSL.getResponse()).thenReturn(RESPONSE_SL);
		atCommandResponseNI = Mockito.mock(ATCommandResponse.class);
		Mockito.when(atCommandResponseNI.getResponseStatus()).thenReturn(ATCommandStatus.OK);
		Mockito.when(atCommandResponseNI.getResponse()).thenReturn(RESPONSE_NI);
		atCommandResponseHV = Mockito.mock(ATCommandResponse.class);
		Mockito.when(atCommandResponseHV.getResponseStatus()).thenReturn(ATCommandStatus.OK);
		Mockito.when(atCommandResponseHV.getResponse()).thenReturn(RESPONSE_HV);
		atCommandResponseVR = Mockito.mock(ATCommandResponse.class);
		Mockito.when(atCommandResponseVR.getResponseStatus()).thenReturn(ATCommandStatus.OK);
		Mockito.when(atCommandResponseVR.getResponse()).thenReturn(RESPONSE_VR);
		atCommandResponseInvalid = Mockito.mock(ATCommandResponse.class);
		Mockito.when(atCommandResponseInvalid.getResponseStatus()).thenReturn(ATCommandStatus.INVALID_PARAMETER);
		Mockito.when(atCommandResponseInvalid.getResponse()).thenReturn(RESPONSE_DUMMY);
	}
	
	@Before
	public void setup() throws Exception {
		// Mock an RxTx IConnectionInterface.
		mockedPort = Mockito.mock(SerialPortRxTx.class);
		Mockito.when(mockedPort.isOpen()).thenReturn(true);
		
		// Instantiate an XBeeDevice object with basic parameters.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(mockedPort));
		Mockito.when(xbeeDevice.getOperatingMode()).thenReturn(OperatingMode.API);
		
		PowerMockito.whenNew(ATCommand.class).withArguments("SH").thenReturn(atCommandSH);
		PowerMockito.whenNew(ATCommand.class).withArguments("SL").thenReturn(atCommandSL);
		PowerMockito.whenNew(ATCommand.class).withArguments("NI").thenReturn(atCommandNI);
		PowerMockito.whenNew(ATCommand.class).withArguments("HV").thenReturn(atCommandHV);
		PowerMockito.whenNew(ATCommand.class).withArguments("VR").thenReturn(atCommandVR);
		
		// Prepare the XBee class to return the mocked connection interface when asking to create a connectionInterface.
		PowerMockito.mockStatic(XBeeProtocol.class);
		PowerMockito.when(XBeeProtocol.determineProtocol((HardwareVersion)Mockito.any(), Mockito.anyString())).thenReturn(XBeeProtocol.ZIGBEE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#initializeDevice()}.
	 * 
	 * <p>Verify that device cannot be initialized if the connection is closed.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testInitializeDeviceErrorConnectionClosed() throws XBeeException {
		// When checking if the connection is open, return false.
		Mockito.when(mockedPort.isOpen()).thenReturn(false);
		
		// Initialize the device.
		xbeeDevice.initializeDevice();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#initializeDevice()}.
	 * 
	 * <p>Verify that device cannot be initialized if the operating mode is not valid.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testInitializeDeviceErrorInvalidOperatingMode() throws XBeeException {
		// Return AT operating mode when asked.
		Mockito.doReturn(OperatingMode.AT).when(xbeeDevice).getOperatingMode();
		
		// Initialize the device.
		xbeeDevice.initializeDevice();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#initializeDevice()}.
	 * 
	 * <p>Verify that device cannot be initialized if the operating mode is UNKNOWN.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testInitializeDeviceErrorUnknownOperatingMode() throws XBeeException {
		// Return UNKKNOWN operating mode when asked.
		Mockito.doReturn(OperatingMode.UNKNOWN).when(xbeeDevice).getOperatingMode();
		
		// Initialize the device.
		xbeeDevice.initializeDevice();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#initializeDevice()}.
	 * 
	 * <p>Verify that device cannot be initialized if there is a timeout reading the 
	 * SH parameter of the device.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testInitializeDeviceErrorSHTimeout() throws XBeeException, IOException {
		// Throw a timeout exception when requesting the SH parameter value.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).sendATCommand(atCommandSH);
		
		// Initialize the device.
		xbeeDevice.initializeDevice();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#initializeDevice()}.
	 * 
	 * <p>Verify that device cannot be initialized if the answer when requesting the SH 
	 * parameter is null.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testInitializeDeviceErrorSHInvalidAnswer() throws XBeeException, IOException {
		// Return a null response when requesting the SH parameter value.
		Mockito.doReturn(null).when(xbeeDevice).sendATCommand(atCommandSH);
		
		// Initialize the device.
		xbeeDevice.initializeDevice();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#initializeDevice()}.
	 * 
	 * <p>Verify that device cannot be initialized if the status value when retrieving the 
	 * SH value is INVALID_PARAMETER.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testInitializeDeviceErrorSHStatus() throws XBeeException, IOException {
		// Return an invalid AT response when requesting the SH parameter value.
		Mockito.doReturn(atCommandResponseInvalid).when(xbeeDevice).sendATCommand(atCommandSH);
		
		// Initialize the device.
		xbeeDevice.initializeDevice();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#initializeDevice()}.
	 * 
	 * <p>Verify that device cannot be initialized if there is a timeout reading the 
	 * SL parameter of the device.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testInitializeDeviceErrorSLTimeout() throws XBeeException, IOException {
		// Return a valid AT response when requesting the SH parameter value.
		Mockito.doReturn(atCommandResponseSH).when(xbeeDevice).sendATCommand(atCommandSH);
		
		// Throw a timeout exception when requesting the SL parameter value.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).sendATCommand(atCommandSL);
				
		// Initialize the device.
		xbeeDevice.initializeDevice();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#initializeDevice()}.
	 * 
	 * <p>Verify that device cannot be initialized if the answer when requesting the SL 
	 * parameter is null.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testInitializeDeviceErrorSLInvalidAnswer() throws XBeeException, IOException {
		// Return a valid AT response when requesting the SH parameter value.
		Mockito.doReturn(atCommandResponseSH).when(xbeeDevice).sendATCommand(atCommandSH);
		
		// Return a null response when requesting the SL parameter value.
		Mockito.doReturn(null).when(xbeeDevice).sendATCommand(atCommandSL);
		
		// Initialize the device.
		xbeeDevice.initializeDevice();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#initializeDevice()}.
	 * 
	 * <p>Verify that device cannot be initialized if the status value when retrieving the 
	 * SL value is INVALID_PARAMETER.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testInitializeDeviceErrorSLStatus() throws XBeeException, IOException {
		// Return a valid AT response when requesting the SH parameter value.
		Mockito.doReturn(atCommandResponseSH).when(xbeeDevice).sendATCommand(atCommandSH);
		
		// Return an invalid AT response when requesting the SL parameter value.
		Mockito.doReturn(atCommandResponseInvalid).when(xbeeDevice).sendATCommand(atCommandSL);
		
		// Initialize the device.
		xbeeDevice.initializeDevice();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#initializeDevice()}.
	 * 
	 * <p>Verify that device cannot be initialized if there is a timeout reading the 
	 * NI parameter of the device.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testInitializeDeviceErrorNITimeout() throws XBeeException, IOException {
		// Return a valid AT response when requesting the SH parameter value.
		Mockito.doReturn(atCommandResponseSH).when(xbeeDevice).sendATCommand(atCommandSH);
		
		// Return a valid AT response when requesting the SL parameter value.
		Mockito.doReturn(atCommandResponseSL).when(xbeeDevice).sendATCommand(atCommandSL);
		
		// Throw a timeout exception when requesting the NI parameter value.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).sendATCommand(atCommandNI);
		
		// Initialize the device.
		xbeeDevice.initializeDevice();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#initializeDevice()}.
	 * 
	 * <p>Verify that device cannot be initialized if the answer when requesting the NI 
	 * parameter is null.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testInitializeDeviceErrorNIInvalidAnswer() throws XBeeException, IOException {
		// Return a valid AT response when requesting the SH parameter value.
		Mockito.doReturn(atCommandResponseSH).when(xbeeDevice).sendATCommand(atCommandSH);
		
		// Return a valid AT response when requesting the SL parameter value.
		Mockito.doReturn(atCommandResponseSL).when(xbeeDevice).sendATCommand(atCommandSL);
		
		// Return a null response when requesting the NI parameter value.
		Mockito.doReturn(null).when(xbeeDevice).sendATCommand(atCommandNI);
		
		// Initialize the device.
		xbeeDevice.initializeDevice();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#initializeDevice()}.
	 * 
	 * <p>Verify that device cannot be initialized if the status value when retrieving the 
	 * NI value is INVALID_PARAMETER.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testInitializeDeviceErrorNIStatus() throws XBeeException, IOException {
		// Return a valid AT response when requesting the SH parameter value.
		Mockito.doReturn(atCommandResponseSH).when(xbeeDevice).sendATCommand(atCommandSH);
		
		// Return a valid AT response when requesting the SL parameter value.
		Mockito.doReturn(atCommandResponseSL).when(xbeeDevice).sendATCommand(atCommandSL);
		
		// Return an invalid AT response when requesting the NI parameter value.
		Mockito.doReturn(atCommandResponseInvalid).when(xbeeDevice).sendATCommand(atCommandNI);
		
		// Initialize the device.
		xbeeDevice.initializeDevice();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#initializeDevice()}.
	 * 
	 * <p>Verify that device cannot be initialized if there is a timeout reading the 
	 * HV parameter of the device.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testInitializeDeviceErrorHVTimeout() throws XBeeException, IOException {
		// Return a valid AT response when requesting the SH parameter value.
		Mockito.doReturn(atCommandResponseSH).when(xbeeDevice).sendATCommand(atCommandSH);
		
		// Return a valid AT response when requesting the SL parameter value.
		Mockito.doReturn(atCommandResponseSL).when(xbeeDevice).sendATCommand(atCommandSL);
		
		// Return a valid AT response when requesting the NI parameter value.
		Mockito.doReturn(atCommandResponseNI).when(xbeeDevice).sendATCommand(atCommandNI);
		
		// Throw a timeout exception when requesting the HV parameter value.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).sendATCommand(atCommandHV);
		
		// Initialize the device.
		xbeeDevice.initializeDevice();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#initializeDevice()}.
	 * 
	 * <p>Verify that device cannot be initialized if the answer when requesting the HV 
	 * parameter is null.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testInitializeDeviceErrorHVInvalidAnswer() throws XBeeException, IOException {
		// Return a valid AT response when requesting the SH parameter value.
		Mockito.doReturn(atCommandResponseSH).when(xbeeDevice).sendATCommand(atCommandSH);
		
		// Return a valid AT response when requesting the SL parameter value.
		Mockito.doReturn(atCommandResponseSL).when(xbeeDevice).sendATCommand(atCommandSL);
		
		// Return a valid AT response when requesting the NI parameter value.
		Mockito.doReturn(atCommandResponseNI).when(xbeeDevice).sendATCommand(atCommandNI);
		
		// Return a null response when requesting the HV parameter value.
		Mockito.doReturn(null).when(xbeeDevice).sendATCommand(atCommandHV);
		
		// Initialize the device.
		xbeeDevice.initializeDevice();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#initializeDevice()}.
	 * 
	 * <p>Verify that device cannot be initialized if the status value when retrieving the 
	 * HV value is INVALID_PARAMETER.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testInitializeDeviceErrorHVStatus() throws XBeeException, IOException {
		// Return a valid AT response when requesting the SH parameter value.
		Mockito.doReturn(atCommandResponseSH).when(xbeeDevice).sendATCommand(atCommandSH);
		
		// Return a valid AT response when requesting the SL parameter value.
		Mockito.doReturn(atCommandResponseSL).when(xbeeDevice).sendATCommand(atCommandSL);
		
		// Return a valid AT response when requesting the NI parameter value.
		Mockito.doReturn(atCommandResponseNI).when(xbeeDevice).sendATCommand(atCommandNI);
		
		// Return an invalid AT response when requesting the HV parameter value.
		Mockito.doReturn(atCommandResponseInvalid).when(xbeeDevice).sendATCommand(atCommandHV);
		
		// Initialize the device.
		xbeeDevice.initializeDevice();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#initializeDevice()}.
	 * 
	 * <p>Verify that device cannot be initialized if there is a timeout reading the 
	 * VR parameter of the device.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testInitializeDeviceErrorVRTimeout() throws XBeeException, IOException {
		// Return a valid AT response when requesting the SH parameter value.
		Mockito.doReturn(atCommandResponseSH).when(xbeeDevice).sendATCommand(atCommandSH);
		
		// Return a valid AT response when requesting the SL parameter value.
		Mockito.doReturn(atCommandResponseSL).when(xbeeDevice).sendATCommand(atCommandSL);
		
		// Return a valid AT response when requesting the NI parameter value.
		Mockito.doReturn(atCommandResponseNI).when(xbeeDevice).sendATCommand(atCommandNI);
		
		// Return a valid AT response when requesting the HV parameter value.
		Mockito.doReturn(atCommandResponseHV).when(xbeeDevice).sendATCommand(atCommandHV);
		
		// Throw a timeout exception when requesting the VR parameter value.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).sendATCommand(atCommandVR);
		
		// Initialize the device.
		xbeeDevice.initializeDevice();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#initializeDevice()}.
	 * 
	 * <p>Verify that device cannot be initialized if the answer when requesting the VR 
	 * parameter is null.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testInitializeDeviceErrorVRInvalidAnswer() throws XBeeException, IOException {
		// Return a valid AT response when requesting the SH parameter value.
		Mockito.doReturn(atCommandResponseSH).when(xbeeDevice).sendATCommand(atCommandSH);
		
		// Return a valid AT response when requesting the SL parameter value.
		Mockito.doReturn(atCommandResponseSL).when(xbeeDevice).sendATCommand(atCommandSL);
		
		// Return a valid AT response when requesting the NI parameter value.
		Mockito.doReturn(atCommandResponseNI).when(xbeeDevice).sendATCommand(atCommandNI);
		
		// Return a valid AT response when requesting the HV parameter value.
		Mockito.doReturn(atCommandResponseHV).when(xbeeDevice).sendATCommand(atCommandHV);
		
		// Return a null response when requesting the VR parameter value.
		Mockito.doReturn(null).when(xbeeDevice).sendATCommand(atCommandVR);
		
		// Initialize the device.
		xbeeDevice.initializeDevice();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#initializeDevice()}.
	 * 
	 * <p>Verify that device cannot be initialized if the status value when retrieving the 
	 * VR value is INVALID_PARAMETER.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testInitializeDeviceErrorVRStatus() throws XBeeException, IOException {
		// Return a valid AT response when requesting the SH parameter value.
		Mockito.doReturn(atCommandResponseSH).when(xbeeDevice).sendATCommand(atCommandSH);
		
		// Return a valid AT response when requesting the SL parameter value.
		Mockito.doReturn(atCommandResponseSL).when(xbeeDevice).sendATCommand(atCommandSL);
		
		// Return a valid AT response when requesting the NI parameter value.
		Mockito.doReturn(atCommandResponseNI).when(xbeeDevice).sendATCommand(atCommandNI);
		
		// Return a valid AT response when requesting the HV parameter value.
		Mockito.doReturn(atCommandResponseHV).when(xbeeDevice).sendATCommand(atCommandHV);
		
		// Return an invalid AT response when requesting the VR parameter value.
		Mockito.doReturn(atCommandResponseInvalid).when(xbeeDevice).sendATCommand(atCommandVR);
		
		// Initialize the device.
		xbeeDevice.initializeDevice();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#initializeDevice()}.
	 * 
	 * <p>Verify that device can be initialized successfully.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testInitializeDeviceSuccess() throws XBeeException, IOException {
		// Return a valid AT response when requesting the SH parameter value.
		Mockito.doReturn(atCommandResponseSH).when(xbeeDevice).sendATCommand(atCommandSH);
		
		// Return a valid AT response when requesting the SL parameter value.
		Mockito.doReturn(atCommandResponseSL).when(xbeeDevice).sendATCommand(atCommandSL);
		
		// Return a valid AT response when requesting the NI parameter value.
		Mockito.doReturn(atCommandResponseNI).when(xbeeDevice).sendATCommand(atCommandNI);
		
		// Return a valid AT response when requesting the HV parameter value.
		Mockito.doReturn(atCommandResponseHV).when(xbeeDevice).sendATCommand(atCommandHV);
		
		// Return a valid AT response when requesting the VR parameter value.
		Mockito.doReturn(atCommandResponseVR).when(xbeeDevice).sendATCommand(atCommandVR);
		
		// Initialize the device.
		xbeeDevice.initializeDevice();
		
		assertEquals(new XBee64BitAddress("0123456789ABCDEF"), xbeeDevice.get64BitAddress());
		assertEquals("XBEE", xbeeDevice.getNodeID());
		assertEquals(HardwareVersion.get(0x01), xbeeDevice.getHardwareVersion());
		assertEquals("0123", xbeeDevice.getFirmwareVersion());
		assertEquals(XBeeProtocol.ZIGBEE, xbeeDevice.getXBeeProtocol());
	}
}
