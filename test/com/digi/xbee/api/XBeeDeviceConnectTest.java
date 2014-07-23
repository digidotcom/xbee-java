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
import com.digi.xbee.api.connection.serial.SerialPortParameters;
import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.OperatingMode;

@RunWith(PowerMockRunner.class)
@PrepareForTest({XBeeDevice.class})
public class XBeeDeviceConnectTest {
	
	// Variables.
	private SerialPortRxTx connectionInterface;
	private XBeeDevice xbeeDevice;
	private DataReader dataReader;
	
	@Before
	public void createMocks() throws Exception {
		// Mock the connection interface to be returned by the XBee class.
		connectionInterface = Mockito.mock(SerialPortRxTx.class);
		// Stub the 'connect' method of the connectionInterface mock so when checking if the 
		// interface is connected next time it returns true.
		PowerMockito.when(connectionInterface, "connect").thenAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				PowerMockito.when(connectionInterface, "isConnected").thenReturn(true);
				return null;
			}
		});
		// Stub the 'disconnect' method of the connectionInterface mock so when checking if the 
		// interface is connected next time it returns false.
		PowerMockito.when(connectionInterface, "disconnect").thenAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				PowerMockito.when(connectionInterface, "isConnected").thenReturn(false);
				return null;
			}
		});
		
		// Instantiate an XBeeDevice object with basic parameters.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(connectionInterface));
		
		// Mock a DataReader object (used in the XBeeDevice connect process).
		dataReader = PowerMockito.mock(DataReader.class);
		// Stub the 'start' method of the dataReader mock so when checking if the 
		// dataReader is running next time it returns true.
		PowerMockito.when(dataReader, "start").thenAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				PowerMockito.when(dataReader, "isRunning").thenReturn(true);
				return null;
			}
		});
		// Stub the 'stopReader' method of the dataReader mock so when checking if the 
		// dataReader is running next time it returns false.
		PowerMockito.when(dataReader, "stopReader").thenAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				PowerMockito.when(dataReader, "isRunning").thenReturn(false);
				return null;
			}
		});
		// Whenever a DataReader class is instantiated, the mocked dataReader should be returned.
		PowerMockito.whenNew(DataReader.class).withAnyArguments().thenReturn(dataReader);
	}
	
	@Test
	/**
	 * Verify that the device connects when it is in API mode.
	 * @throws Exception
	 */
	public void testConnectAPIMode() throws Exception {
		// Configure the determineConnectionMode method to return 'API'.
		PowerMockito.doReturn(OperatingMode.API).when(xbeeDevice, "determineConnectionMode");
		
		// Execute the connect method.
		try {
			xbeeDevice.connect();
		} catch (XBeeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		}
		
		// Verify the device is connected.
		assertTrue(xbeeDevice.isConnected());
		// Verify the dataReader is running.
		assertTrue(dataReader.isRunning());
		// Verify the operating mode is API.
		assertEquals(xbeeDevice.getOperatingMode(), OperatingMode.API);
	}
	
	@Test
	/**
	 * Verify that the device connects when it is in API Escaped mode.
	 * @throws Exception
	 */
	public void testConnectAPIEscapedMode() throws Exception {
		// Configure the determineConnectionMode method to return 'API'.
		PowerMockito.doReturn(OperatingMode.API_ESCAPE).when(xbeeDevice, "determineConnectionMode");
		
		// Execute the connect method.
		try {
			xbeeDevice.connect();
		} catch (XBeeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		}
		
		// Verify the device is connected.
		assertTrue(xbeeDevice.isConnected());
		// Verify the dataReader is running.
		assertTrue(dataReader.isRunning());
		// Verify the operating mode is API Escaped.
		assertEquals(xbeeDevice.getOperatingMode(), OperatingMode.API_ESCAPE);
	}
	
	@Test
	/**
	 * Verify that the device does not connect (exception is thrown) when it is in AT mode.
	 * @throws Exception
	 */
	public void testConnectATMode() throws Exception {
		// Configure the determineConnectionMode method to return 'API'.
		PowerMockito.doReturn(OperatingMode.AT).when(xbeeDevice, "determineConnectionMode");
		
		// Execute the connect method.
		try {
			xbeeDevice.connect();
		} catch (XBeeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (InvalidOperatingModeException e) {
			// This is the exception we should have received.
		}
		
		// Verify the device is disconnected.
		assertFalse(xbeeDevice.isConnected());
		// Verify the dataReader is not running.
		assertFalse(dataReader.isRunning());
		// Verify the operating mode is AT.
		assertEquals(xbeeDevice.getOperatingMode(), OperatingMode.AT);
	}
	
	@Test
	/**
	 * Verify that the device does not connect (exception is thrown) when the operating mode 
	 * could not be retrieved.
	 * @throws Exception
	 */
	public void testConnectUnknownMode() throws Exception {
		// Configure the determineConnectionMode method to return 'API'.
		PowerMockito.doReturn(OperatingMode.UNKNOWN).when(xbeeDevice, "determineConnectionMode");
		
		// Execute the connect method.
		try {
			xbeeDevice.connect();
		} catch (XBeeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (InvalidOperatingModeException e) {
			// This is the exception we should have received.
		}
		
		// Verify the device is disconnected.
		assertFalse(xbeeDevice.isConnected());
		// Verify the dataReader is not running.
		assertFalse(dataReader.isRunning());
		// Verify the operating mode is Unknown.
		assertEquals(xbeeDevice.getOperatingMode(), OperatingMode.UNKNOWN);
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
		
		// Verify the device is disconnected.
		assertFalse(xbeeDevice.isConnected());
		// Verify the dataReader is not running.
		assertFalse(dataReader.isRunning());
		// Verify the operating mode is Unknown.
		assertEquals(xbeeDevice.getOperatingMode(), OperatingMode.UNKNOWN);
	}
	
	// TODO: Complete with more tests:
	//  - Connect with port name is null
	//  - Connect with port name is invalid
	//  - Connect with baud rate is invalid
	//  - Connect with serial params are null
	//  - etc.
}
