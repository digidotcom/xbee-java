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
@PrepareForTest({Raw802Device.class})
public class GetIOSample802Test {

	// Constants.
	private final static String METHOD_RECEIVE_RAW_IO_PACKET = "receiveRaw802IOPacket";
		
	// Variables.
	private SerialPortRxTx mockedPort;
	
	private Raw802Device raw802Device;
	
	@Before
	public void setup() {
		// Mock an RxTx IConnectionInterface.
		mockedPort = Mockito.mock(SerialPortRxTx.class);
		Mockito.when(mockedPort.isOpen()).thenReturn(true);
		
		// Instantiate an Raw802Device object with basic parameters.
		raw802Device = PowerMockito.spy(new Raw802Device(mockedPort));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.Raw802Device#getIOSample(IOLine)}.
	 * 
	 * <p>Verify that IOSample cannot be retrieved from the device if the connection is closed.</p>
	 * 
	 * @throws Exception 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testGetIOSample802ConnectionClosed() throws Exception {
		// When checking if the connection is open, return false.
		Mockito.when(mockedPort.isOpen()).thenReturn(false);
		
		// Get an IOSample from the 802.15.4 device.
		raw802Device.readIOSample();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.Raw802Device#getIOSample(IOLine)}.
	 * 
	 * <p>Verify that IOSample cannot be retrieved from the device if the operating mode is AT.</p>
	 * 
	 * @throws Exception 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testGetIOSample802ATOperatingMode() throws Exception {
		// Return AT operating mode when asked.
		Mockito.doReturn(OperatingMode.AT).when(raw802Device).getOperatingMode();
		
		// Get an IOSample from the 802.15.4 device.
		raw802Device.readIOSample();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.Raw802Device#getIOSample(IOLine)}.
	 * 
	 * <p>Verify that IOSample cannot be retrieved from the device if the operating mode is UNKNOWN.</p>
	 * 
	 * @throws Exception 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testGetIOSample802UnknownOperatingMode() throws Exception {
		// Return UNKNOWN operating mode when asked.
		Mockito.doReturn(OperatingMode.UNKNOWN).when(raw802Device).getOperatingMode();
		
		// Get an IOSample from the 802.15.4 device.
		raw802Device.readIOSample();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.Raw802Device#getIOSample(IOLine)}.
	 * 
	 * <p>Verify that IOSample cannot be retrieved from the device if the status value after sending the get 
	 * command is INVALID_PARAMETER.</p>
	 * 
	 * @throws Exception 
	 */
	@Test(expected=ATCommandException.class)
	public void testGetIOSample802InvalidParameterStatusResponse() throws Exception {
		// Generate an ATCommandResponse with error status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.INVALID_PARAMETER);
		
		Mockito.doReturn(mockedResponse).when(raw802Device).sendATCommand((ATCommand)Mockito.any());
		
		// Get an IOSample from the 802.15.4 device.
		raw802Device.readIOSample();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.Raw802Device#getIOSample(IOLine)}.
	 * 
	 * <p>Verify that IOSample cannot be retrieved if the answer received after sending the get 
	 * command is null.</p>
	 * 
	 * @throws Exception 
	 */
	@Test(expected=ATCommandException.class)
	public void testGetIOSample802NullResponse() throws Exception {
		// Return a null ATCommandResponse when sending any AT Command.
		Mockito.doReturn(null).when(raw802Device).sendATCommand((ATCommand)Mockito.any());
		
		// Get an IOSample from the 802.15.4 device.
		raw802Device.readIOSample();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.Raw802Device#getIOSample(IOLine)}.
	 * 
	 * <p>Verify that IOSample cannot be retrieved if there is a timeout exception sending the get 
	 * command.</p>
	 * 
	 * @throws Exception 
	 */
	@Test(expected=TimeoutException.class)
	public void testGetIOSample802Timeout() throws Exception {
		// Throw a timeout exception when trying to send any AT Command.
		Mockito.doThrow(new TimeoutException()).when(raw802Device).sendATCommand((ATCommand)Mockito.any());
		
		// Get an IOSample from the 802.15.4 device.
		raw802Device.readIOSample();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.Raw802Device#getIOSample(IOLine)}.
	 * 
	 * <p>Verify that IOSample cannot be retrieved if there is a timeout exception (the payload read is null) 
	 * waiting for the 802.15.4 IO packet.</p>
	 * 
	 * @throws Exception 
	 */
	@Test(expected=TimeoutException.class)
	public void testGetIOSample802IOPacketTimeout() throws Exception {
		// Generate an ATCommandResponse with OK status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.OK);
		Mockito.when(mockedResponse.getResponse()).thenReturn(new byte[]{});
		
		Mockito.doReturn(mockedResponse).when(raw802Device).sendATCommand((ATCommand)Mockito.any());
				
		// Throw a timeout exception (return a null payload) when trying to get an IO sample packet.
		PowerMockito.doReturn(null).when(raw802Device, METHOD_RECEIVE_RAW_IO_PACKET);
		
		// Get an IOSample from the 802.15.4 device.
		raw802Device.readIOSample();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.Raw802Device#getIOSample(IOLine)}.
	 * 
	 * <p>Verify that IOSample can be retrieved successfully.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testGetIOSample802Success() throws Exception {
		// Generate an ATCommandResponse with OK status to be returned when sending any AT Command.
		ATCommandResponse mockedResponse = Mockito.mock(ATCommandResponse.class);
		Mockito.when(mockedResponse.getResponseStatus()).thenReturn(ATCommandStatus.OK);
		Mockito.when(mockedResponse.getResponse()).thenReturn(new byte[]{});
		
		Mockito.doReturn(mockedResponse).when(raw802Device).sendATCommand((ATCommand)Mockito.any());
				
		// Return a dummy payload (won't be used) when trying to get an IO sample packet.
		PowerMockito.doReturn(new byte[0]).when(raw802Device, METHOD_RECEIVE_RAW_IO_PACKET);
		
		// Mock an IO sample.
		IOSample mockedIOSample = Mockito.mock(IOSample.class);
		
		// Whenever an IOSample class is instantiated, the mockedIOSample should be returned.
		PowerMockito.whenNew(IOSample.class).withAnyArguments().thenReturn(mockedIOSample);
		
		// Get an IOSample from the 802.15.4 device.
		IOSample receivedSample = raw802Device.readIOSample();
		
		// Verify the sample is the expected one.
		assertEquals(mockedIOSample, receivedSample);
	}
}
