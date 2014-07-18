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
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.XBeeMode;

@RunWith(PowerMockRunner.class)
@PrepareForTest({XBeeDevice.class})
public class XBeeDeviceConnectTest {
	
	// Variables.
	private SerialPortRxTx connectionInterface;
	
	private XBeeDevice xbeeDevice;
	
	@Before
	public void createMocks() throws Exception {
		// Mock the connection interface to be returned by the XBee class.
		connectionInterface = Mockito.mock(SerialPortRxTx.class);
		PowerMockito.doNothing().when(connectionInterface).connect();
		
		// Instantiate an XBeeDevice object with basic parameters.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(connectionInterface));
	}
	
	@Test
	/**
	 * Verify that the device connects when it is in API mode.
	 * @throws Exception
	 */
	public void testConnectAPIMode() throws Exception {
		// Configure the determineConnectionMode method to return 'API'.
		PowerMockito.doReturn(XBeeMode.API).when(xbeeDevice, "determineConnectionMode");
		
		// Execute the connect method.
		try {
			xbeeDevice.connect();
		} catch (XBeeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		}
		
		// Verify the operating mode is API.
		assertEquals(xbeeDevice.getOperatingMode(), XBeeMode.API);
	}
	
	@Test
	/**
	 * Verify that the device connects when it is in API Escaped mode.
	 * @throws Exception
	 */
	public void testConnectAPIEscapedMode() throws Exception {
		// Configure the determineConnectionMode method to return 'API'.
		PowerMockito.doReturn(XBeeMode.API_ESCAPE).when(xbeeDevice, "determineConnectionMode");
		
		// Execute the connect method.
		try {
			xbeeDevice.connect();
		} catch (XBeeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		}
		
		// Verify the operating mode is API Escaped.
		assertEquals(xbeeDevice.getOperatingMode(), XBeeMode.API_ESCAPE);
	}
	
	@Test
	/**
	 * Verify that the device does not connect (exception is thrown) when it is in AT mode.
	 * @throws Exception
	 */
	public void testConnectATMode() throws Exception {
		// Configure the determineConnectionMode method to return 'API'.
		PowerMockito.doReturn(XBeeMode.AT).when(xbeeDevice, "determineConnectionMode");
		
		// Execute the connect method.
		try {
			xbeeDevice.connect();
		} catch (XBeeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (InvalidOperatingModeException e) {
			// This is the exception we should have received.
		}
		
		// Verify the operating mode is AT.
		assertEquals(xbeeDevice.getOperatingMode(), XBeeMode.AT);
	}
	
	@Test
	/**
	 * Verify that the device does not connect (exception is thrown) when the operating mode 
	 * could not be retrieved.
	 * @throws Exception
	 */
	public void testConnectUnknownMode() throws Exception {
		// Configure the determineConnectionMode method to return 'API'.
		PowerMockito.doReturn(XBeeMode.UNKNOWN).when(xbeeDevice, "determineConnectionMode");
		
		// Execute the connect method.
		try {
			xbeeDevice.connect();
		} catch (XBeeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (InvalidOperatingModeException e) {
			// This is the exception we should have received.
		}
		
		// Verify the operating mode is Unknown.
		assertEquals(xbeeDevice.getOperatingMode(), XBeeMode.UNKNOWN);
	}
	
	@Test
	/**
	 * Verify that the device does not connect (exception is thrown) when a timeout happens 
	 * while retrieving the operating mode.
	 * @throws Exception
	 */
	public void testConnectTimeout() throws Exception {
		// Configure the determineConnectionMode method to return 'API'.
		PowerMockito.doThrow(new XBeeException(XBeeException.CONNECTION_TIMEOUT)).when(xbeeDevice, "connect");
		
		// Execute the connect method.
		try {
			xbeeDevice.connect();
			fail("Device shouldn't have connected");
		} catch (XBeeException e) {
			// This is the exception we should have received.
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		}
		
		// Verify the operating mode is Unknown.
		assertEquals(xbeeDevice.getOperatingMode(), XBeeMode.UNKNOWN);
	}
	
	// TODO: Complete with more tests:
	//  - Connect with port name is null
	//  - Connect with port name is invalid
	//  - Connect with baud rate is invalid
	//  - Connect with serial params are null
	//  - etc.
}
