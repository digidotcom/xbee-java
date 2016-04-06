/**
 * Copyright (c) 2015 Digi International Inc.,
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
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.digi.xbee.api.TestConnectionInterface.TestSerialFactory;
import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.connection.ISerialConnectionInterfaceFactory;
import com.digi.xbee.api.connection.serial.AbstractSerialPort;
import com.digi.xbee.api.connection.serial.SerialPortParameters;
import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.SerialConnectionInterfaceNotCreatedException;

@RunWith(PowerMockRunner.class)
@PrepareForTest({XBee.class, TestConnectionInterface.class, Class.class})
public class XBeeTest {
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		PowerMockito.spy(XBee.class);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		// Remove the loaded serial connection interface factory if any.
		Whitebox.setInternalState(XBee.class, "loadedFactory", (ISerialConnectionInterfaceFactory)null);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.XBee#createConnectiontionInterface(String, int)}.
	 */
	@Test
	public final void testCreateConnectiontionInterfaceNullPort() {
		// Setup the resources for the test.
		String port = null;
		int baudrate = 9600;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Serial port name cannot be null.")));
		
		// Call the method under test.
		XBee.createConnectiontionInterface(port, baudrate);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBee#createConnectiontionInterface(String, int)}.
	 */
	@Test
	public final void testCreateConnectiontionInterfaceEmptyPort() {
		// Setup the resources for the test.
		String port = "";
		int baudrate = 9600;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Serial port name cannot be an empty string.")));
		
		// Call the method under test.
		XBee.createConnectiontionInterface(port, baudrate);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBee#createConnectiontionInterface(String, int)}.
	 */
	@Test
	public final void testCreateConnectiontionInterfaceNegativeBaudrate() {
		// Setup the resources for the test.
		String port = "COM1";
		int baudrate = -9600;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Baudrate cannot be less than 0.")));
		
		// Call the method under test.
		XBee.createConnectiontionInterface(port, baudrate);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBee#createConnectiontionInterface(String, int)}.
	 * @throws Exception 
	 */
	@Test
	public final void testCreateConnectiontionInterfaceNotDefinedSerialInterface() throws Exception {
		// Setup the resources for the test.
		String port = "COM1";
		int baudrate = 9600;
		String serialInterfaceClassName = "";
		
		System.setProperty(XBee.PROPERTY_SERIAL_CONNECTION_INTERFACE, serialInterfaceClassName);
		
		// Call the method under test.
		IConnectionInterface result = null;
		try {
			result = XBee.createConnectiontionInterface(port, baudrate);
		} catch (SerialConnectionInterfaceNotCreatedException e) {
			// If the class was already loaded during the execution of previous test cases.
			
			String msg = e.getMessage();
			if (msg != null) {
				String[] p = msg.split(":");
				String name = p[1].substring(1, p[1].length() - 1);
				
				// Verify the result.
				assertThat(name, is(equalTo(SerialPortRxTx.class.getName())));
				PowerMockito.verifyPrivate(XBee.class).invoke("getSerialConnectionInterfaceFactory");
				
				return;
			}
			throw e;
		}
		
		// Verify the result.
		assertThat(result, is(instanceOf(SerialPortRxTx.class)));
		PowerMockito.verifyPrivate(XBee.class).invoke("getSerialConnectionInterfaceFactory");
		
		PowerMockito.verifyStatic(Mockito.times(1));
		XBee.setSerialConnectionInterfaceFactory((ISerialConnectionInterfaceFactory)Mockito.any(Whitebox.getInnerClassType(SerialPortRxTx.class, "SerialPortRxTxFactory")));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBee#createConnectiontionInterface(String, int)}.
	 * @throws Exception 
	 */
	@Test
	public final void testCreateConnectiontionInterfaceNonExistingSerialInterface() throws Exception {
		// Setup the resources for the test.
		String port = "COM1";
		int baudrate = 9600;
		String serialInterfaceClassName = "non.existing.class";
		
		exception.expect(SerialConnectionInterfaceNotCreatedException.class);
		exception.expectMessage(is(equalTo(String.format("Error loading serial connection interface factory: %s.", serialInterfaceClassName))));
		
		System.setProperty(XBee.PROPERTY_SERIAL_CONNECTION_INTERFACE, serialInterfaceClassName);
		
		// Call the method under test.
		XBee.createConnectiontionInterface(port, baudrate);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBee#createConnectiontionInterface(String, int)}.
	 * @throws Exception 
	 */
	@Test
	public final void testCreateConnectiontionInterfaceNotLoadedSerialInterface() throws Exception {
		// Setup the resources for the test.
		String port = "COM2";
		int baudrate = 9600;
		String serialInterfaceClassName = "com.digi.xbee.api.TestConnectionInterface";
		
		exception.expect(SerialConnectionInterfaceNotCreatedException.class);
		exception.expectMessage(is(equalTo(String.format("Error loading serial connection interface factory: %s.", serialInterfaceClassName))));
		
		System.setProperty(XBee.PROPERTY_SERIAL_CONNECTION_INTERFACE, serialInterfaceClassName);
		
		PowerMockito.doNothing().when(XBee.class, "setSerialConnectionInterfaceFactory", Mockito.any(ISerialConnectionInterfaceFactory.class));
		
		// Call the method under test.
		XBee.createConnectiontionInterface(port, baudrate);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBee#createConnectiontionInterface(String, int)}.
	 * @throws Exception 
	 */
	@Test
	public final void testCreateConnectiontionInterface() throws Exception {
		// Setup the resources for the test.
		String port = "COM3";
		int baudrate = 9600;
		String serialInterfaceClassName = "com.digi.xbee.api.TestConnectionInterface";
		
		System.setProperty(XBee.PROPERTY_SERIAL_CONNECTION_INTERFACE, serialInterfaceClassName);
		
		// Call the method under test.
		IConnectionInterface result = null;
		try {
			result = XBee.createConnectiontionInterface(port, baudrate);
		} catch (SerialConnectionInterfaceNotCreatedException e) {
			// If the class was already loaded during the execution of previous test cases.
			
			String msg = e.getMessage();
			if (msg != null) {
				String[] p = msg.split(":");
				String name = p[1].substring(1, p[1].length() - 1);
				
				// Verify the result.
				assertThat(name, is(equalTo(serialInterfaceClassName)));
				PowerMockito.verifyPrivate(XBee.class).invoke("getSerialConnectionInterfaceFactory");
				
				return;
			}
			throw e;
		}
		
		// Verify the result.
		assertThat(result, is(equalTo((IConnectionInterface)TestConnectionInterface.getInstance())));
		PowerMockito.verifyPrivate(XBee.class).invoke("getSerialConnectionInterfaceFactory");
		
		PowerMockito.verifyStatic(Mockito.times(1));
		XBee.setSerialConnectionInterfaceFactory(Mockito.any(TestSerialFactory.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBee#createConnectiontionInterface(String, int)}.
	 * @throws Exception 
	 */
	@Test
	public final void testCreateConnectiontionInterfaceWhenFactoryAlreadyLoaded() throws Exception {
		// Setup the resources for the test.
		String port = "COM10";
		int baudrate = 9600;
		String serialInterfaceClassName = "com.digi.xbee.api.XBeeTest.VoidSerialFactory";
		
		TestSerialFactory testSerialFactory = TestConnectionInterface.getTestSerialFactoryInstance();
		XBee.setSerialConnectionInterfaceFactory(testSerialFactory);
		
		System.setProperty(XBee.PROPERTY_SERIAL_CONNECTION_INTERFACE, serialInterfaceClassName);
		
		// Call the method under test.
		IConnectionInterface result = XBee.createConnectiontionInterface(port, baudrate);
		
		// Verify the result.
		assertThat(result, is(equalTo((IConnectionInterface)TestConnectionInterface.getInstance())));
		assertThat(result, is(not(instanceOf(VoidSerialFactory.class))));
		PowerMockito.verifyPrivate(XBee.class).invoke("getSerialConnectionInterfaceFactory");
		
		PowerMockito.verifyStatic(Mockito.times(1));
		XBee.setSerialConnectionInterfaceFactory(Mockito.any(TestSerialFactory.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBee#createConnectiontionInterface(String, com.digi.xbee.api.connection.serial.SerialPortParameters)}.
	 */
	@Test
	public final void testCreateConnectiontionInterfaceSerialPortParametersNullPort() {
		// Setup the resources for the test.
		String port = null;
		SerialPortParameters parameters = new SerialPortParameters(9600, AbstractSerialPort.DEFAULT_DATA_BITS, 
				AbstractSerialPort.DEFAULT_STOP_BITS, AbstractSerialPort.DEFAULT_PARITY, AbstractSerialPort.DEFAULT_FLOW_CONTROL);
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Serial port name cannot be null.")));
		
		// Call the method under test.
		XBee.createConnectiontionInterface(port, parameters);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBee#createConnectiontionInterface(String, com.digi.xbee.api.connection.serial.SerialPortParameters)}.
	 */
	@Test
	public final void testCreateConnectiontionInterfaceSerialPortParametersEmptyPort() {
		// Setup the resources for the test.
		String port = "";
		SerialPortParameters parameters = new SerialPortParameters(9600, AbstractSerialPort.DEFAULT_DATA_BITS, 
				AbstractSerialPort.DEFAULT_STOP_BITS, AbstractSerialPort.DEFAULT_PARITY, AbstractSerialPort.DEFAULT_FLOW_CONTROL);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Serial port name cannot be an empty string.")));
		
		// Call the method under test.
		XBee.createConnectiontionInterface(port, parameters);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBee#createConnectiontionInterface(String, com.digi.xbee.api.connection.serial.SerialPortParameters)}.
	 */
	@Test
	public final void testCreateConnectiontionInterfaceSerialPortParametersNullParameters() {
		// Setup the resources for the test.
		String port = "COM1";
		SerialPortParameters parameters = null;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Serial port parameters cannot be null.")));
		
		// Call the method under test.
		XBee.createConnectiontionInterface(port, parameters);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBee#createConnectiontionInterface(String, com.digi.xbee.api.connection.serial.SerialPortParameters)}.
	 * @throws Exception 
	 */
	@Test
	public final void testCreateConnectiontionInterfaceSerialPortParametersNotDefinedSerialInterface() throws Exception {
		// Setup the resources for the test.
		String port = "COM1";
		SerialPortParameters parameters = new SerialPortParameters(9600, AbstractSerialPort.DEFAULT_DATA_BITS, 
				AbstractSerialPort.DEFAULT_STOP_BITS, AbstractSerialPort.DEFAULT_PARITY, AbstractSerialPort.DEFAULT_FLOW_CONTROL);
		String serialInterfaceClassName = "";
		
		System.setProperty(XBee.PROPERTY_SERIAL_CONNECTION_INTERFACE, serialInterfaceClassName);
		
		// Call the method under test.
		IConnectionInterface result = null;
		try {
			result = XBee.createConnectiontionInterface(port, parameters);
		} catch (SerialConnectionInterfaceNotCreatedException e) {
			// If the class was already loaded during the execution of previous test cases.
			
			String msg = e.getMessage();
			if (msg != null) {
				String[] p = msg.split(":");
				String name = p[1].substring(1, p[1].length() - 1);
				
				// Verify the result.
				assertThat(name, is(equalTo(SerialPortRxTx.class.getName())));
				PowerMockito.verifyPrivate(XBee.class).invoke("getSerialConnectionInterfaceFactory");
				
				return;
			}
			throw e;
		}
		
		// Verify the result.
		assertThat(result, is(instanceOf(SerialPortRxTx.class)));
		PowerMockito.verifyPrivate(XBee.class).invoke("getSerialConnectionInterfaceFactory");
		
		PowerMockito.verifyStatic(Mockito.times(1));
		XBee.setSerialConnectionInterfaceFactory(
				(ISerialConnectionInterfaceFactory)Mockito.any(Whitebox.getInnerClassType(SerialPortRxTx.class, "SerialPortRxTxFactory")));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBee#createConnectiontionInterface(String, com.digi.xbee.api.connection.serial.SerialPortParameters)}.
	 * @throws Exception 
	 */
	@Test
	public final void testCreateConnectiontionInterfaceSerialPortParametersNonExistingSerialInterface() throws Exception {
		// Setup the resources for the test.
		String port = "COM4";
		SerialPortParameters parameters = new SerialPortParameters(9600, AbstractSerialPort.DEFAULT_DATA_BITS, 
				AbstractSerialPort.DEFAULT_STOP_BITS, AbstractSerialPort.DEFAULT_PARITY, AbstractSerialPort.DEFAULT_FLOW_CONTROL);
		String serialInterfaceClassName = "non.existing.class";
		
		exception.expect(SerialConnectionInterfaceNotCreatedException.class);
		exception.expectMessage(is(equalTo(String.format("Error loading serial connection interface factory: %s.", serialInterfaceClassName))));
		
		System.setProperty(XBee.PROPERTY_SERIAL_CONNECTION_INTERFACE, serialInterfaceClassName);
		
		// Call the method under test.
		XBee.createConnectiontionInterface(port, parameters);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBee#createConnectiontionInterface(String, com.digi.xbee.api.connection.serial.SerialPortParameters)}.
	 * @throws Exception 
	 */
	@Test
	public final void testCreateConnectiontionInterfaceSerialPortParametersNotLoadedSerialInterface() throws Exception {
		// Setup the resources for the test.
		String port = "COM5";
		SerialPortParameters parameters = new SerialPortParameters(9600, AbstractSerialPort.DEFAULT_DATA_BITS, 
				AbstractSerialPort.DEFAULT_STOP_BITS, AbstractSerialPort.DEFAULT_PARITY, AbstractSerialPort.DEFAULT_FLOW_CONTROL);
		String serialInterfaceClassName = "com.digi.xbee.api.TestConnectionInterface";
		
		exception.expect(SerialConnectionInterfaceNotCreatedException.class);
		exception.expectMessage(is(equalTo(String.format("Error loading serial connection interface factory: %s.", serialInterfaceClassName))));
		
		System.setProperty(XBee.PROPERTY_SERIAL_CONNECTION_INTERFACE, serialInterfaceClassName);
		
		PowerMockito.doNothing().when(XBee.class, "setSerialConnectionInterfaceFactory", Mockito.any(ISerialConnectionInterfaceFactory.class));
		
		// Call the method under test.
		XBee.createConnectiontionInterface(port, parameters);
		
		PowerMockito.doCallRealMethod().when(XBee.class, "setSerialConnectionInterfaceFactory", Mockito.any(ISerialConnectionInterfaceFactory.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBee#createConnectiontionInterface(String, com.digi.xbee.api.connection.serial.SerialPortParameters)}.
	 * @throws Exception 
	 */
	@Test
	public final void testCreateConnectiontionInterfaceSerialPortParameters() throws Exception {
		// Setup the resources for the test.
		String port = "COM6";
		SerialPortParameters parameters = new SerialPortParameters(9600, AbstractSerialPort.DEFAULT_DATA_BITS, 
				AbstractSerialPort.DEFAULT_STOP_BITS, AbstractSerialPort.DEFAULT_PARITY, AbstractSerialPort.DEFAULT_FLOW_CONTROL);
		String serialInterfaceClassName = "com.digi.xbee.api.TestConnectionInterface";
		
		System.setProperty(XBee.PROPERTY_SERIAL_CONNECTION_INTERFACE, serialInterfaceClassName);
		
		// Call the method under test.
		IConnectionInterface result = null;
		try {
			result = XBee.createConnectiontionInterface(port, parameters);
		} catch (SerialConnectionInterfaceNotCreatedException e) {
			// If the class was already loaded during the execution of previous test cases.
			
			String msg = e.getMessage();
			if (msg != null) {
				String[] p = msg.split(":");
				String name = p[1].substring(1, p[1].length() - 1);
				
				// Verify the result.
				assertThat(name, is(equalTo(serialInterfaceClassName)));
				PowerMockito.verifyPrivate(XBee.class).invoke("getSerialConnectionInterfaceFactory");
				
				return;
			}
			throw e;
		}
		
		// Verify the result.
		assertThat(result, is(equalTo((IConnectionInterface)TestConnectionInterface.getInstance())));
		PowerMockito.verifyPrivate(XBee.class).invoke("getSerialConnectionInterfaceFactory");
		
		PowerMockito.verifyStatic(Mockito.times(1));
		XBee.setSerialConnectionInterfaceFactory(Mockito.any(TestSerialFactory.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBee#setSerialConnectionInterfaceFactory(ISerialConnectionInterfaceFactory)}.
	 * @throws Exception 
	 */
	@Test
	public final void testSetSerialConnectionInterfaceFactoryNullFactory() throws Exception {
		// Setup the resources for the test.
		TestSerialFactory testSerialFactory = null;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("ISerialConnectionInterfaceFactory cannot be null.")));
		
		// Call the method under test.
		XBee.setSerialConnectionInterfaceFactory(testSerialFactory);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBee#setSerialConnectionInterfaceFactory(ISerialConnectionInterfaceFactory)}.
	 * @throws Exception 
	 */
	@Test
	public final void testSetSerialConnectionInterfaceFactory() throws Exception {
		// Setup the resources for the test.
		TestSerialFactory testSerialFactory = TestConnectionInterface.getTestSerialFactoryInstance();
		
		// Call the method under test.
		XBee.setSerialConnectionInterfaceFactory(testSerialFactory);
		
		// Verify the result.
		TestSerialFactory result = (TestSerialFactory) Whitebox.getInternalState(XBee.class, "loadedFactory");
		assertThat(result, is(equalTo(testSerialFactory)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBee#setSerialConnectionInterfaceFactory(ISerialConnectionInterfaceFactory)}.
	 * @throws Exception 
	 */
	@Test
	public final void testSetSerialConnectionInterfaceFactoryWhenAFactoryIsAlreadyLoaded() throws Exception {
		// Setup the resources for the test.
		TestSerialFactory testSerialFactory = TestConnectionInterface.getTestSerialFactoryInstance();
		XBee.setSerialConnectionInterfaceFactory(testSerialFactory);
		
		// Call the method under test.
		XBee.setSerialConnectionInterfaceFactory(new ISerialConnectionInterfaceFactory() {
			
			@Override
			public IConnectionInterface createInterface(String port,
					SerialPortParameters serialPortParameters) {
				return null;
			}
			
			@Override
			public IConnectionInterface createInterface(String port, int baudRate) {
				return null;
			}
		});
		
		// Verify the result.
		TestSerialFactory result = (TestSerialFactory) Whitebox.getInternalState(XBee.class, "loadedFactory");
		assertThat(result, is(equalTo(testSerialFactory)));
	}
	
	private static class VoidSerialFactory implements ISerialConnectionInterfaceFactory {
		
		@Override
		public IConnectionInterface createInterface(String port, int baudRate) {
			return null;
		}
		
		@Override
		public IConnectionInterface createInterface(String port,
				SerialPortParameters serialPortParameters) {
			return null;
		}
	}
}
