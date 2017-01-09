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
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#isConnected()}.
	 * 
	 * <p>Verify that it is not possible to determine if the device is connected if 
	 * the connection of the device is closed.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testIsConnectedErrorConnectionClosed() throws XBeeException {
		// Throw an interface not open exception when getting the CellularAssociationIndicationStatus.
		Mockito.doThrow(new InterfaceNotOpenException()).when(cellularDevice).getCellularAssociationIndicationStatus();
		
		// Check the connection.
		cellularDevice.isConnected();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#isConnected()}.
	 * 
	 * <p>Verify that it is not possible to determine if the device is connected if 
	 * the operating mode of the device is not valid.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testIsConnectedErrorInvalidOperatingMode() throws XBeeException {
		// Throw an invalid operating mode exception when getting the CellularAssociationIndicationStatus.
		Mockito.doThrow(new InvalidOperatingModeException()).when(cellularDevice).getCellularAssociationIndicationStatus();
		
		// Check the connection.
		cellularDevice.isConnected();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#isConnected()}.
	 * 
	 * <p>Verify that it is not possible to determine if the device is connected 
	 * when there is an Timeout exception getting the CellularAssociationIndicationStatus.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testIsConnectedErrorTimeout() throws XBeeException, IOException {
		// Throw a timeout exception when getting the CellularAssociationIndicationStatus.
		Mockito.doThrow(new TimeoutException()).when(cellularDevice).getCellularAssociationIndicationStatus();
		
		// Check the connection.
		cellularDevice.isConnected();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#isConnected()}.
	 * 
	 * <p>Verify that it is not possible to determine if the device is connected 
	 * when there is an AT command exception getting the CellularAssociationIndicationStatus.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testIsConnectedErrorInvalidAnswer() throws XBeeException, IOException {
		// Throw an AT command exception when when getting the CellularAssociationIndicationStatus.
		Mockito.doThrow(new ATCommandException(null)).when(cellularDevice).getCellularAssociationIndicationStatus();
		
		// Check the connection.
		cellularDevice.isConnected();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#isConnected()}.
	 * 
	 * <p>Verify that it is possible to determine if the device is connected or not 
	 * when it is connected.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testIsConnectedSuccessConnected() throws XBeeException, IOException {
		// Return the connected value when when getting the CellularAssociationIndicationStatus.
		Mockito.doReturn(CellularAssociationIndicationStatus.SUCCESSFULLY_CONNECTED).when(cellularDevice).getCellularAssociationIndicationStatus();
		
		// Check the connection.
		assertTrue(cellularDevice.isConnected());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#isConnected()}.
	 * 
	 * <p>Verify that it is possible to determine if the device is connected or not 
	 * when it is disconnected.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testIsConnectedSuccessDisconnected() throws XBeeException, IOException {
		// Return a valid disconnected value when when getting the CellularAssociationIndicationStatus.
		Mockito.doReturn(CellularAssociationIndicationStatus.INITIALIZING).when(cellularDevice).getCellularAssociationIndicationStatus();
		
		// Check the connection.
		assertFalse(cellularDevice.isConnected());
	}
}
