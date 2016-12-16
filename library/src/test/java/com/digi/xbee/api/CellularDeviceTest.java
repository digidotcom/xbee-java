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
import com.digi.xbee.api.models.CellularAssociationIndicationStatus;
import com.digi.xbee.api.models.XBeeIMEIAddress;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CellularDevice.class, CellularAssociationIndicationStatus.class})
public class CellularDeviceTest {
	
	// Constants.
	private static final String PARAMETER_AI = "AI";
	private static final byte[] RESPONSE_AI = new byte[]{0x00};
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	// Variables.
	private XBeeIMEIAddress imeiAddress;
	
	private CellularDevice cellularDevice;
	
	private CellularAssociationIndicationStatus validCellularAIStatus = CellularAssociationIndicationStatus.SUCCESSFULLY_CONNECTED;
	
	@Before
	public void setup() throws Exception {
		// Suppress the 'readDeviceInfo' method of the parent class so that it is not
		// called from the child (CellularDevice) class.
		PowerMockito.suppress(PowerMockito.method(IPDevice.class, "readDeviceInfo"));
		
		// Spy the CellularDevice class.
		SerialPortRxTx mockPort = Mockito.mock(SerialPortRxTx.class);
		cellularDevice = PowerMockito.spy(new CellularDevice(mockPort));
		
		// Mock an IMEI address object.
		imeiAddress = PowerMockito.mock(XBeeIMEIAddress.class);
		
		// Always return a valid Cellular association indication when requested.
		PowerMockito.mockStatic(CellularAssociationIndicationStatus.class);
		PowerMockito.when(CellularAssociationIndicationStatus.get(Mockito.anyInt())).thenReturn(validCellularAIStatus);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#getIMEIAddress()} and
	 * {@link com.digi.xbee.api.CellularDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that the {@code readDeviceInfo()} method of the Cellular device generates 
	 * the IMEI address correctly.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testReadDeviceInfoCellular() throws Exception {
		// Whenever an XBeeIMEIAddress class is instantiated, the mocked IMEI address should be returned.
		PowerMockito.whenNew(XBeeIMEIAddress.class).withAnyArguments().thenReturn(imeiAddress);
		
		// Fist, check that the IMEI is null (it has not been created yet)
		assertNull(cellularDevice.getIMEIAddress());
		
		// Call the readDeviceInfo method.
		cellularDevice.readDeviceInfo();
		Mockito.verify((IPDevice)cellularDevice, Mockito.times(1)).readDeviceInfo();
		
		// Verify that the IMEI address was generated.
		assertEquals(imeiAddress, cellularDevice.getIMEIAddress());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#getADCValue(com.digi.xbee.api.io.IOLine)}.
	 * 
	 * <p>Verify that the not supported methods of the Cellular protocol throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationGetADCValue() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in Cellular protocol.")));
		
		// Call the method that should throw the exception.
		cellularDevice.getADCValue(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#getDIOChangeDetection()}.
	 * 
	 * <p>Verify that the not supported methods of the Cellular protocol throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationGetDIOChangeDetection() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in Cellular protocol.")));
		
		// Call the method that should throw the exception.
		cellularDevice.getDIOChangeDetection();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#setDIOChangeDetection(java.util.Set)}.
	 * 
	 * <p>Verify that the not supported methods of the Cellular protocol throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationSetDIOChangeDetection() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in Cellular protocol.")));
		
		// Call the method that should throw the exception.
		cellularDevice.setDIOChangeDetection(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#getDIOValue(com.digi.xbee.api.io.IOLine)}.
	 * 
	 * <p>Verify that the not supported methods of the Cellular protocol throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationGetDIOValue() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in Cellular protocol.")));
		
		// Call the method that should throw the exception.
		cellularDevice.getDIOValue(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#setDIOValue(com.digi.xbee.api.io.IOLine, com.digi.xbee.api.io.IOValue)}.
	 * 
	 * <p>Verify that the not supported methods of the Cellular protocol throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationSetDIOValue() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in Cellular protocol.")));
		
		// Call the method that should throw the exception.
		cellularDevice.setDIOValue(null, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#getIOConfiguration(com.digi.xbee.api.io.IOLine)}.
	 * 
	 * <p>Verify that the not supported methods of the Cellular protocol throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationGetIOConfiguration() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in Cellular protocol.")));
		
		// Call the method that should throw the exception.
		cellularDevice.getIOConfiguration(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#setIOConfiguration(com.digi.xbee.api.io.IOLine, com.digi.xbee.api.io.IOMode)}.
	 * 
	 * <p>Verify that the not supported methods of the Cellular protocol throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationSetIOConfiguration() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in Cellular protocol.")));
		
		// Call the method that should throw the exception.
		cellularDevice.setIOConfiguration(null, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#getIOSamplingRate()}.
	 * 
	 * <p>Verify that the not supported methods of the Cellular protocol throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationGetIOSamplingRate() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in Cellular protocol.")));
		
		// Call the method that should throw the exception.
		cellularDevice.getIOSamplingRate();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#setIOSamplingRate(int)}.
	 * 
	 * <p>Verify that the not supported methods of the Cellular protocol throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationSetIOSamplingRate() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in Cellular protocol.")));
		
		// Call the method that should throw the exception.
		cellularDevice.setIOSamplingRate(-1);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#setNodeID(String)}.
	 * 
	 * <p>Verify that the not supported methods of the Cellular protocol throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationSetNodeID() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in Cellular protocol.")));
		
		// Call the method that should throw the exception.
		cellularDevice.setNodeID(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#getPowerLevel()}.
	 * 
	 * <p>Verify that the not supported methods of the Cellular protocol throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationGetPowerLevel() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in Cellular protocol.")));
		
		// Call the method that should throw the exception.
		cellularDevice.getPowerLevel();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#setPowerLevel(com.digi.xbee.api.models.PowerLevel)}.
	 * 
	 * <p>Verify that the not supported methods of the Cellular protocol throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationSetPowerLevel() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in Cellular protocol.")));
		
		// Call the method that should throw the exception.
		cellularDevice.setPowerLevel(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#getPWMDutyCycle(com.digi.xbee.api.io.IOLine)}.
	 * 
	 * <p>Verify that the not supported methods of the Cellular protocol throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationGetPWMDutyCycle() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in Cellular protocol.")));
		
		// Call the method that should throw the exception.
		cellularDevice.getPWMDutyCycle(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#setPWMDutyCycle(com.digi.xbee.api.io.IOLine, double)}.
	 * 
	 * <p>Verify that the not supported methods of the Cellular protocol throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationSetPWMDutyCycle() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in Cellular protocol.")));
		
		// Call the method that should throw the exception.
		cellularDevice.setPWMDutyCycle(null, -1);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#readIOSample()}.
	 * 
	 * <p>Verify that the not supported methods of the Cellular protocol throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationReadIOSample() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in Cellular protocol.")));
		
		// Call the method that should throw the exception.
		cellularDevice.readIOSample();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#get64BitAddress()} and
	 * {@link com.digi.xbee.api.CellularDevice#getNodeID()},
	 * 
	 * <p>Verify that parameters not supported by the Cellular protocol are returned 
	 * as {@code null}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedParameters() throws Exception {
		// Cellular devices do not have 64-bit address and Node ID.
		assertNull(cellularDevice.get64BitAddress());
		assertNull(cellularDevice.getNodeID());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#getCellularAssociationIndicationStatus()}.
	 * 
	 * <p>Check that the Cellular association indication method returns values 
	 * successfully.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAssociationIndicationStatus() throws Exception {
		// Return a valid response when requesting the AI parameter value.
		Mockito.doReturn(RESPONSE_AI).when(cellularDevice).getParameter(PARAMETER_AI);
		
		assertEquals(validCellularAIStatus, cellularDevice.getCellularAssociationIndicationStatus());
	}
}
