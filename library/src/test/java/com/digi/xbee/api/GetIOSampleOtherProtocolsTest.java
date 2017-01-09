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

import static org.junit.Assert.*;

import org.junit.Before;
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
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.models.ATCommand;
import com.digi.xbee.api.models.ATCommandResponse;
import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.models.OperatingMode;

@RunWith(PowerMockRunner.class)
@PrepareForTest({XBeeDevice.class})
public class GetIOSampleOtherProtocolsTest {
		
	// Variables.
	private SerialPortRxTx mockedPort;
	
	private XBeeDevice xbeeDevice;
	
	@Before
	public void setup() {
		// Mock an RxTx IConnectionInterface.
		mockedPort = Mockito.mock(SerialPortRxTx.class);
		Mockito.when(mockedPort.isOpen()).thenReturn(true);
		
		// Instantiate an XBeeDevice object with basic parameters.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(mockedPort));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getIOSample(IOLine)}.
	 * 
	 * <p>Verify that IOSample cannot be retrieved from the device if the connection is closed.</p>
	 * 
	 * @throws Exception 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testGetIOSampleOtherProtocolsConnectionClosed() throws Exception {
		// When checking if the connection is open, return false.
		Mockito.when(mockedPort.isOpen()).thenReturn(false);
		
		// Get an IOSample from the XBee device.
		xbeeDevice.readIOSample();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getIOSample(IOLine)}.
	 * 
	 * <p>Verify that IOSample cannot be retrieved from the device if the operating mode is AT.</p>
	 * 
	 * @throws Exception 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testGetIOSampleOtherProtocolsATOperatingMode() throws Exception {
		// Return AT operating mode when asked.
		Mockito.doReturn(OperatingMode.AT).when(xbeeDevice).getOperatingMode();
		
		// Get an IOSample from the XBee device.
		xbeeDevice.readIOSample();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getIOSample(IOLine)}.
	 * 
	 * <p>Verify that IOSample cannot be retrieved from the device if the operating mode is UNKNOWN.</p>
	 * 
	 * @throws Exception 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testGetIOSampleOtherProtocolsUnknownOperatingMode() throws Exception {
		// Return UNKNOWN operating mode when asked.
		Mockito.doReturn(OperatingMode.UNKNOWN).when(xbeeDevice).getOperatingMode();
		
		// Get an IOSample from the XBee device.
		xbeeDevice.readIOSample();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getIOSample(IOLine)}.
	 * 
	 * <p>Verify that IOSample cannot be retrieved from the device if the status value after sending the get 
	 * command is INVALID_PARAMETER.</p>
	 * 
	 * @throws Exception 
	 */
	@Test(expected=ATCommandException.class)
	public void testGetIOSampleOtherProtocolsInvalidParameterStatusResponse() throws Exception {
		// Generate an ATCommandResponse with error status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.INVALID_PARAMETER);
		
		Mockito.doReturn(mockedResponse).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Get an IOSample from the XBee device.
		xbeeDevice.readIOSample();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getIOSample(IOLine)}.
	 * 
	 * <p>Verify that IOSample cannot be retrieved if the answer received after sending the get 
	 * command is null.</p>
	 * 
	 * @throws Exception 
	 */
	@Test(expected=ATCommandException.class)
	public void testGetIOSampleOtherProtocolsNullResponse() throws Exception {
		// Return a null ATCommandResponse when sending any AT Command.
		Mockito.doReturn(null).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Get an IOSample from the XBee device.
		xbeeDevice.readIOSample();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getIOSample(IOLine)}.
	 * 
	 * <p>Verify that IOSample cannot be retrieved if there is a timeout exception sending the get 
	 * command.</p>
	 * 
	 * @throws Exception 
	 */
	@Test(expected=TimeoutException.class)
	public void testGetIOSampleOtherProtocolsTimeout() throws Exception {
		// Throw a timeout exception when trying to send any AT Command.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Get an IOSample from the XBee device.
		xbeeDevice.readIOSample();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getIOSample(IOLine)}.
	 * 
	 * <p>Verify that IOSample can be retrieved successfully.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testGetIOSampleOtherProtocolsSuccess() throws Exception {
		// Generate an ATCommandResponse with OK status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.OK);
		Mockito.when(mockedResponse.getResponse()).thenReturn(new byte[]{});
		
		Mockito.doReturn(mockedResponse).when(xbeeDevice).sendATCommand((ATCommand)Mockito.any());
		
		// Mock an IO sample.
		IOSample mockedIOSample = Mockito.mock(IOSample.class);
		
		// Whenever an IOSample class is instantiated, the mockedIOSample should be returned.
		PowerMockito.whenNew(IOSample.class).withAnyArguments().thenReturn(mockedIOSample);
		
		// Get an IOSample from the XBee device.
		IOSample receivedSample = xbeeDevice.readIOSample();
		
		// Verify the sample is the expected one.
		assertEquals(mockedIOSample, receivedSample);
	}
}
