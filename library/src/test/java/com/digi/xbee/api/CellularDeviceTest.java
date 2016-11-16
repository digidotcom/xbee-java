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
		PowerMockito.suppress(PowerMockito.method(WLANDevice.class, "readDeviceInfo"));
		
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
		Mockito.verify((WLANDevice)cellularDevice, Mockito.times(1)).readDeviceInfo();
		
		// Verify that the IMEI address was generated.
		assertEquals(imeiAddress, cellularDevice.getIMEIAddress());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#getADCValue(com.digi.xbee.api.io.IOLine)}, 
	 * {@link com.digi.xbee.api.CellularDevice#getDIOChangeDetection()},
	 * {@link com.digi.xbee.api.CellularDevice#setDIOChangeDetection(java.util.Set)}, 
	 * {@link com.digi.xbee.api.CellularDevice#getDIOValue(com.digi.xbee.api.io.IOLine)}, 
	 * {@link com.digi.xbee.api.CellularDevice#setDIOValue(com.digi.xbee.api.io.IOLine, com.digi.xbee.api.io.IOValue)},
	 * {@link com.digi.xbee.api.CellularDevice#getIOConfiguration(com.digi.xbee.api.io.IOLine)},
	 * {@link com.digi.xbee.api.CellularDevice#setIOConfiguration(com.digi.xbee.api.io.IOLine, com.digi.xbee.api.io.IOMode)},
	 * {@link com.digi.xbee.api.CellularDevice#getIOSamplingRate()},
	 * {@link com.digi.xbee.api.CellularDevice#setIOSamplingRate(int)},
	 * {@link com.digi.xbee.api.CellularDevice#getNodeID()},
	 * {@link com.digi.xbee.api.CellularDevice#setNodeID(String)},
	 * {@link com.digi.xbee.api.CellularDevice#getPWMDutyCycle(com.digi.xbee.api.io.IOLine)},
	 * {@link com.digi.xbee.api.CellularDevice#setPWMDutyCycle(com.digi.xbee.api.io.IOLine, double)} and
	 * {@link com.digi.xbee.api.CellularDevice#readIOSample()}.
	 * 
	 * <p>Verify that the not supported methods of the Cellular protocol throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperations() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in Cellular protocol.")));
		
		// All the following operations should throw an 
		// {@code UnsupportedOperationException} exception 
		cellularDevice.getADCValue(null);
		cellularDevice.getDIOChangeDetection();
		cellularDevice.setDIOChangeDetection(null);
		cellularDevice.getDIOValue(null);
		cellularDevice.setDIOValue(null, null);
		cellularDevice.getIOConfiguration(null);
		cellularDevice.setIOConfiguration(null, null);
		cellularDevice.getIOSamplingRate();
		cellularDevice.setIOSamplingRate(-1);
		cellularDevice.setNodeID(null);
		cellularDevice.getPowerLevel();
		cellularDevice.setPowerLevel(null);
		cellularDevice.getPWMDutyCycle(null);
		cellularDevice.setPWMDutyCycle(null, -1);
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
