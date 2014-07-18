package com.digi.xbee.api;

import org.junit.Assert;
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
	public void createMocks() throws Exception {
		// Mock the serial parameters of the port.
		serialPortParameters = PowerMockito.mock(SerialPortParameters.class);
		
		// Whenever a SerialPortParameters class is instantiated, the mocked serialPortParameters should be returned.
		PowerMockito.whenNew(SerialPortParameters.class).withAnyArguments().thenReturn(serialPortParameters);
		
		// Mock the connection interface to be returned by the XBee class.
		connectionInterface = PowerMockito.mock(SerialPortRxTx.class);
		
		// Prepare the XBee class to return the mocked connection interface when asking to create a connectionInterface.
		PowerMockito.mockStatic(XBee.class);
		PowerMockito.when(XBee.createConnectiontionInterface("COM1", 9600)).thenReturn(connectionInterface);
		PowerMockito.when(XBee.createConnectiontionInterface("COM1", serialPortParameters)).thenReturn(connectionInterface);
	}
	
	@Test
	/**
	 * Verify that the xbeeDevice object was correctly instantiated giving the name of the 
	 * serial port and the baud rate as parameters.
	 */
	public void testInstantiateXBeeDeviceNameBR() {
		// Instantiate an XBeeDevice object with basic parameters.
		XBeeDevice xbeeDevice = new XBeeDevice("COM1", 9600);
		
		// Verify the createConnectiontionInterface static method from the XBee class 
		// was called.
		PowerMockito.verifyStatic(Mockito.times(1));
		XBee.createConnectiontionInterface("COM1", 9600);
		
		// Verify the XBee device returns the expected connection interface.
		Assert.assertEquals(xbeeDevice.getConnectionInterface(), connectionInterface);
	}
	
	@Test
	/**
	 * Verify that the xbeeDevice object was correctly instantiated giving all the serial 
	 * port settings as parameters.
	 */
	public void testInstantiateXBeeDeviceAdvancedSettings() {
		// Instantiate an XBeeDevice object with advanced parameters.
		XBeeDevice xbeeDevice = new XBeeDevice("COM1", 9600, 8, 1, 0, 0);
		
		// Verify the createConnectiontionInterface static method from the XBee class 
		// was called.
		PowerMockito.verifyStatic(Mockito.times(1));
		XBee.createConnectiontionInterface("COM1", serialPortParameters);
		
		// Verify the XBee device returns the expected connection interface.
		Assert.assertEquals(xbeeDevice.getConnectionInterface(), connectionInterface);
	}
	
	@Test
	/**
	 * Verify that the xbeeDevice object was correctly instantiated giving the name of the 
	 * serial port and a SerialPortParameters object as parameters.
	 */
	public void testInstantiateXBeeDeviceSerialPortParams() {
		// Instantiate an XBeeDevice object with a SerialParameters object.
		XBeeDevice xbeeDevice = new XBeeDevice("COM1", serialPortParameters);
		
		// Verify the createConnectiontionInterface static method from the XBee class 
		// was called.
		PowerMockito.verifyStatic(Mockito.times(1));
		XBee.createConnectiontionInterface("COM1", serialPortParameters);
		
		// Verify the XBee device returns the expected connection interface.
		Assert.assertEquals(xbeeDevice.getConnectionInterface(), connectionInterface);
	}
}
