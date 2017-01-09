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

import com.digi.xbee.api.connection.serial.SerialPortParameters;
import com.digi.xbee.api.connection.serial.SerialPortRxTx;

@RunWith(PowerMockRunner.class)
@PrepareForTest({XBee.class, XBeeDevice.class, SerialPortParameters.class})
public class XBeeDeviceInstantiateTest {
	
	// Variables.
	private SerialPortParameters serialPortParameters;
	private SerialPortRxTx connectionInterface;
	
	@Before
	public void setup() throws Exception {
		// Mock the serial parameters of the port.
		serialPortParameters = PowerMockito.mock(SerialPortParameters.class);
		
		// Mock the connection interface to be returned by the XBee class.
		connectionInterface = PowerMockito.mock(SerialPortRxTx.class);
		
		// Prepare the XBee class to return the mocked connection interface when asking to create a connectionInterface.
		PowerMockito.mockStatic(XBee.class);
		PowerMockito.when(XBee.createConnectiontionInterface("COM1", 9600)).thenReturn(connectionInterface);
		PowerMockito.when(XBee.createConnectiontionInterface("COM1", serialPortParameters)).thenReturn(connectionInterface);
	}
	
	/**
	 * Verify that the xbeeDevice object is correctly instantiated giving the name of the 
	 * serial port and the baud rate as parameters.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInstantiateXBeeDeviceNameBR() throws Exception {
		// Whenever a SerialPortParameters class is instantiated, the mocked serialPortParameters should be returned.
		PowerMockito.whenNew(SerialPortParameters.class).withAnyArguments().thenReturn(serialPortParameters);
		
		// Instantiate an XBeeDevice object with basic parameters.
		XBeeDevice xbeeDevice = null;
		xbeeDevice = new XBeeDevice("COM1", 9600);
		
		// Verify the createConnectiontionInterface static method from the XBee class 
		// was called.
		PowerMockito.verifyStatic(Mockito.times(1));
		XBee.createConnectiontionInterface("COM1", 9600);
		
		// Verify the XBee device returns the expected connection interface.
		assertEquals(connectionInterface, xbeeDevice.getConnectionInterface());
		// Verify the instantiated device is local.
		assertFalse(xbeeDevice.isRemote());
	}
	
	/**
	 * Verify that the xbeeDevice object is not correctly instantiated when the name of the 
	 * serial port or the baud rate parameters are not valid.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInstantiateXBeeDeviceBadNameBR() throws Exception {
		// Real methods must be called so the checkers for bad parameters are executed.
		PowerMockito.when(XBee.class, "createConnectiontionInterface", Mockito.anyString(), Mockito.anyInt()).thenCallRealMethod();
		PowerMockito.when(XBee.class, "createConnectiontionInterface", Mockito.anyString(), Mockito.anyObject()).thenCallRealMethod();
		
		// Instantiate an XBeeDevice object with a null name.
		try {
			new XBeeDevice((String)null, 9600);
			fail("Device shouldn't have been instantiated correctly.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
		
		// Instantiate an XBeeDevice object with an invalid baud rate.
		try {
			new XBeeDevice("COM1", -10);
			fail("Device shouldn't have been instantiated correctly.");
		} catch (Exception e) {
			assertEquals(IllegalArgumentException.class, e.getClass());
		}
	}
	
	/**
	 * Verify that the xbeeDevice object is correctly instantiated giving all the serial 
	 * port settings as parameters.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInstantiateXBeeDeviceAdvancedSettings() throws Exception {
		// Whenever a SerialPortParameters class is instantiated, the mocked serialPortParameters should be returned.
		PowerMockito.whenNew(SerialPortParameters.class).withAnyArguments().thenReturn(serialPortParameters);
		
		// Instantiate an XBeeDevice object with advanced parameters.
		XBeeDevice xbeeDevice = null;
		xbeeDevice = new XBeeDevice("COM1", 9600, 8, 1, 0, 0);
		
		// Verify the createConnectiontionInterface static method from the XBee class 
		// was called.
		PowerMockito.verifyStatic(Mockito.times(1));
		XBee.createConnectiontionInterface("COM1", serialPortParameters);
		
		// Verify the XBee device returns the expected connection interface.
		assertEquals(connectionInterface, xbeeDevice.getConnectionInterface());
		// Verify the instantiated device is local.
		assertFalse(xbeeDevice.isRemote());
	}
	
	/**
	 * Verify that the xbeeDevice object is not correctly instantiated when the serial 
	 * port settings are not valid.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInstantiateXBeeDeviceBadAdvancedSettings() throws Exception {
		// Real methods must be called so the checkers for bad parameters are executed.
		PowerMockito.when(XBee.class, "createConnectiontionInterface", Mockito.anyString(), Mockito.anyInt()).thenCallRealMethod();
		PowerMockito.when(XBee.class, "createConnectiontionInterface", Mockito.anyString(), Mockito.anyObject()).thenCallRealMethod();
		
		// Instantiate an XBeeDevice object with a null name.
		try {
			new XBeeDevice(null, 9600, 8, 1, 0, 0);
			fail("Device shouldn't have been instantiated correctly.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
		
		// Instantiate an XBeeDevice object with an invalid baud rate.
		try {
			new XBeeDevice("COM1", -10, 8, 1, 0, 0);
			fail("Device shouldn't have been instantiated correctly.");
		} catch (Exception e) {
			assertEquals(IllegalArgumentException.class, e.getClass());
		}
		
		// Instantiate an XBeeDevice object with invalid data bits.
		try {
			new XBeeDevice("COM1", 9600, -10, 1, 0, 0);
			fail("Device shouldn't have been instantiated correctly.");
		} catch (Exception e) {
			assertEquals(IllegalArgumentException.class, e.getClass());
		}
		
		// Instantiate an XBeeDevice object with invalid stop bits.
		try {
			new XBeeDevice("COM1", 9600, 8, -10, 0, 0);
			fail("Device shouldn't have been instantiated correctly.");
		} catch (Exception e) {
			assertEquals(IllegalArgumentException.class, e.getClass());
		}
		
		// Instantiate an XBeeDevice object with invalid parity.
		try {
			new XBeeDevice("COM1", 9600, 8, 1, -10, 0);
			fail("Device shouldn't have been instantiated correctly.");
		} catch (Exception e) {
			assertEquals(IllegalArgumentException.class, e.getClass());
		}
		
		// Instantiate an XBeeDevice object with invalid flow control.
		try {
			new XBeeDevice("COM1", 9600, 8, 1, 0, -10);
			fail("Device shouldn't have been instantiated correctly.");
		} catch (Exception e) {
			assertEquals(IllegalArgumentException.class, e.getClass());
		}
	}
	
	/**
	 * Verify that the xbeeDevice object is correctly instantiated giving the name of the 
	 * serial port and a SerialPortParameters object as parameters.
	 */
	@Test
	public void testInstantiateXBeeDeviceSerialPortParams() {
		// Instantiate an XBeeDevice object with a SerialParameters object.
		XBeeDevice xbeeDevice = null;
		xbeeDevice = new XBeeDevice("COM1", serialPortParameters);
		
		// Verify the createConnectiontionInterface static method from the XBee class 
		// was called.
		PowerMockito.verifyStatic(Mockito.times(1));
		XBee.createConnectiontionInterface("COM1", serialPortParameters);
		
		// Verify the XBee device returns the expected connection interface.
		assertEquals(connectionInterface, xbeeDevice.getConnectionInterface());
		// Verify the instantiated device is local.
		assertFalse(xbeeDevice.isRemote());
	}
	
	/**
	 * Verify that the xbeeDevice object is not correctly instantiated when the name of the 
	 * serial port or the SerialPortParameters object are not valid.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInstantiateXBeeDeviceBadSerialPortParams() throws Exception {
		// Real methods must be called so the checkers for bad parameters are executed.
		PowerMockito.when(XBee.class, "createConnectiontionInterface", Mockito.anyString(), Mockito.anyInt()).thenCallRealMethod();
		PowerMockito.when(XBee.class, "createConnectiontionInterface", Mockito.anyString(), Mockito.anyObject()).thenCallRealMethod();
		
		// Instantiate an XBeeDevice object with a null name.
		try {
			new XBeeDevice(null, serialPortParameters);
			fail("Device shouldn't have been instantiated correctly.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
		
		// Instantiate an XBeeDevice object with null parameters.
		try {
			new XBeeDevice("COM1", null);
			fail("Device shouldn't have been instantiated correctly.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
	}
	
	/**
	 * Verify that the xbeeDevice object was correctly instantiated giving an 
	 * IConnectionInterface object as parameter.
	 */
	@Test
	public void testInstantiateXBeeDeviceIConnectionInterface() {
		// Instantiate an XBeeDevice object with a SerialParameters object.
		XBeeDevice xbeeDevice = null;
		xbeeDevice = new XBeeDevice(connectionInterface);
		
		// Verify the XBee device returns the expected connection interface.
		assertEquals(connectionInterface, xbeeDevice.getConnectionInterface());
		// Verify the instantiated device is local.
		assertFalse(xbeeDevice.isRemote());
	}
	
	/**
	 * Verify that the xbeeDevice object was not correctly instantiated when the 
	 * IConnectionInterface object is null.
	 */
	@Test
	public void testInstantiateXBeeDeviceBadIConnectionInterface() {
		// Instantiate an XBeeDevice object with a null IConnectionInterface.
		try {
			new XBeeDevice(null);
			fail("Device shouldn't have been instantiated correctly.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
	}
}
