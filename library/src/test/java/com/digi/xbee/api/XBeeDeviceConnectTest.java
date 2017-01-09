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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.digi.xbee.api.connection.DataReader;
import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.InterfaceAlreadyOpenException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.OperationNotSupportedException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.XBee64BitAddress;

@RunWith(PowerMockRunner.class)
@PrepareForTest({XBeeDevice.class, XBee64BitAddress.class})
public class XBeeDeviceConnectTest {
	
	// Variables.
	private SerialPortRxTx connectionInterface;
	private XBeeDevice xbeeDevice;
	private DataReader dataReader;
	
	@Before
	public void setup() throws Exception {
		// Mock the connection interface to be returned by the XBee class.
		connectionInterface = Mockito.mock(SerialPortRxTx.class);
		// Stub the 'open' method of the connectionInterface mock so when checking if the 
		// interface is open next time it returns true.
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Mockito.when(connectionInterface.isOpen()).thenReturn(true);
				return null;
			}
		}).when(connectionInterface).open();
		// Stub the 'close' method of the connectionInterface mock so when checking if the 
		// interface is open next time it returns false.
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Mockito.when(connectionInterface.isOpen()).thenReturn(false);
				return null;
			}
		}).when(connectionInterface).close();
		
		// Instantiate an XBeeDevice object with basic parameters.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(connectionInterface));
		// Stub the readDeviceInfo method to do nothing (it has its own test file).
		Mockito.doNothing().when(xbeeDevice).readDeviceInfo();
		
		// Mock a DataReader object (used in the XBeeDevice connect process).
		dataReader = Mockito.mock(DataReader.class);
		// Stub the 'start' method of the dataReader mock so when checking if the 
		// dataReader is running next time it returns true.
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Mockito.when(dataReader.isRunning()).thenReturn(true);
				return null;
			}
		}).when(dataReader).start();
		// Stub the 'stopReader' method of the dataReader mock so when checking if the 
		// dataReader is running next time it returns false.
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Mockito.when(dataReader.isRunning()).thenReturn(false);
				return null;
			}
		}).when(dataReader).stopReader();
		
		// Whenever a DataReader class is instantiated, the mocked dataReader should be returned.
		PowerMockito.whenNew(DataReader.class).withAnyArguments().thenReturn(dataReader);
	}
	
	/**
	 * Verify that the device connects when it is in API mode.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public void testConnectAPIMode() throws XBeeException {
		// Configure the determineOperatingMode method to return 'API'.
		Mockito.doReturn(OperatingMode.API).when(xbeeDevice).determineOperatingMode();
		
		// Execute the connect method.
		xbeeDevice.open();
		
		// Verify the device is connected.
		assertTrue(xbeeDevice.isOpen());
		// Verify the dataReader is running.
		assertTrue(dataReader.isRunning());
		// Verify the operating mode is API.
		assertEquals(OperatingMode.API, xbeeDevice.getOperatingMode());
		
		// Close the connection.
		xbeeDevice.close();
		
		// Verify the device is not connected.
		assertFalse(xbeeDevice.isOpen());
		// Verify the dataReader is not running.
		assertFalse(dataReader.isRunning());
	}
	
	/**
	 * Verify that the device connects when it is in API Escaped mode.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public void testConnectAPIEscapedMode() throws XBeeException {
		// Configure the determineOperatingMode method to return 'API'.
		Mockito.doReturn(OperatingMode.API_ESCAPE).when(xbeeDevice).determineOperatingMode();
		
		// Execute the connect method.
		xbeeDevice.open();
		
		// Verify the device is connected.
		assertTrue(xbeeDevice.isOpen());
		// Verify the dataReader is running.
		assertTrue(dataReader.isRunning());
		// Verify the operating mode is API Escaped.
		assertEquals(OperatingMode.API_ESCAPE, xbeeDevice.getOperatingMode());
		
		// Close the connection.
		xbeeDevice.close();
		
		// Verify the device is not connected.
		assertFalse(xbeeDevice.isOpen());
		// Verify the dataReader is not running.
		assertFalse(dataReader.isRunning());
	}
	
	/**
	 * Verify that the device does not connect (exception is thrown) when it is in AT mode.
	 * 
	 * @throws OperationNotSupportedException 
	 */
	@Test
	public void testConnectATMode() throws OperationNotSupportedException {
		// Configure the determineOperatingMode method to return 'API'.
		Mockito.doReturn(OperatingMode.AT).when(xbeeDevice).determineOperatingMode();
		
		// Execute the connect method.
		try {
			xbeeDevice.open();
		} catch (Exception e) {
			assertEquals(InvalidOperatingModeException.class, e.getClass());
		}
		
		// Verify the device is disconnected.
		assertFalse(xbeeDevice.isOpen());
		// Verify the dataReader is not running.
		assertFalse(dataReader.isRunning());
		// Verify the operating mode is AT.
		assertEquals(OperatingMode.AT, xbeeDevice.getOperatingMode());
	}
	
	/**
	 * Verify that the device does not connect (exception is thrown) when the operating mode 
	 * could not be retrieved.
	 * 
	 * @throws OperationNotSupportedException 
	 */
	@Test
	public void testConnectUnknownMode() throws OperationNotSupportedException {
		// Configure the determineOperatingMode method to return 'API'.
		Mockito.doReturn(OperatingMode.UNKNOWN).when(xbeeDevice).determineOperatingMode();
		
		// Execute the connect method.
		try {
			xbeeDevice.open();
		} catch (Exception e) {
			assertEquals(InvalidOperatingModeException.class, e.getClass());
		}
		
		// Verify the device is disconnected.
		assertFalse(xbeeDevice.isOpen());
		// Verify the dataReader is not running.
		assertFalse(dataReader.isRunning());
		// Verify the operating mode is Unknown.
		assertEquals(OperatingMode.UNKNOWN, xbeeDevice.getOperatingMode());
	}
	
	/**
	 * Verify that the device does not connect (exception is thrown) when a timeout happens 
	 * while retrieving the operating mode.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public void testConnectTimeout() throws XBeeException {
		// Configure the determineOperatingMode method to return 'API'.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).open();
		
		// Execute the connect method.
		try {
			xbeeDevice.open();
			fail("Device shouldn't have connected");
		} catch (Exception e) {
			assertEquals(TimeoutException.class, e.getClass());
		}
		
		// Verify the device is disconnected.
		assertFalse(xbeeDevice.isOpen());
		// Verify the dataReader is not running.
		assertFalse(dataReader.isRunning());
		// Verify the operating mode is Unknown.
		assertEquals(OperatingMode.UNKNOWN, xbeeDevice.getOperatingMode());
	}
	
	/**
	 * Verify that when the connection is already open and the connect method is called an 
	 * exception is thrown.
	 */
	@Test
	public void testConnectAlreadyConnected() {
		// Configure the isOpen method of the XBee device to return true when asked.
		Mockito.when(connectionInterface.isOpen()).thenReturn(true);
		
		// Execute the connect method.
		try {
			xbeeDevice.open();
			fail("Device shouldn't have connected");
		} catch (Exception e) {
			assertEquals(InterfaceAlreadyOpenException.class, e.getClass());
		}
		
		// Verify that the device is still open.
		assertTrue(xbeeDevice.isOpen());
		
		// Close the connection.
		xbeeDevice.close();
		
		// Verify the device is not connected.
		assertFalse(xbeeDevice.isOpen());
		// Verify the dataReader is not running.
		assertFalse(dataReader.isRunning());
	}
}
