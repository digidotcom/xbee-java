/**
 * Copyright (c) 2014-2016 Digi International Inc.,
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.connection.ISerialConnectionInterfaceFactory;
import com.digi.xbee.api.connection.serial.SerialPortParameters;
import com.digi.xbee.api.exceptions.SerialConnectionInterfaceNotCreatedException;

/**
 * Helper class used to create a serial port connection interface.
 * 
 * <p>This class determines which serial connection class is going to be used.
 * By default, {@code com.digi.xbee.api.connection.serial.SerialPortRxTx}.</p>
 * 
 * <p>To change to a new one:</p>
 * <ul>
 * <li>Method 1:
 *    <ol>
 *      <li>Assign a new class name to the system property called 
 *      {@code com.digi.xbee.api.serialInterface} (see 
 *      {@link #PROPERTY_SERIAL_CONNECTION_INTERFACE}.
 *      <p>By default:</p>
 *      <p>{@code com.digi.xbee.api.serialInterface = com.digi.xbee.api.connection.serial.SerialPortRxTx}</p>
 *      </li>
 *      
 *      <li>This class must implement the interface {@link com.digi.xbee.api.connection.serial.IConnectionInterface}.</li>
 *      
 *      <li>Add a static initialization block to this class in order to define 
 *      the serial connection interface factory: 
 *      <p>Example:</p>
 *      
 *      <p><pre>
 *      static {
 *          XBee.setSerialConnectionInterfaceFactory(new SerialPortRxTxFactory());
 *      }
 *      </pre>
 *      
 *      </li>
 *      
 *      <li>Create the factory class ({@code SerialPortRxTxFactory} in the 
 *      example above) by implementing the interface 
 *      {@link com.digi.xbee.api.connection.serial.SerialPortParameters.ISerialConnectionInterfaceFactory}.
 *      </li>
 *    </ol>
 * </li>
 * <li>Method 2:</li>
 *    <ol>
 *      <li>Before instantiating any {@code XBeeDevice} or any of its subclasses
 *      set the serial connection interface factory by calling:
 *      <pre>
 *      XBee.setSerialConnectionInterfaceFactory(new SerialPortRxTxFactory());
 *      </pre>
 *      </li>
 *      
 *      <li>Create the factory class ({@code SerialPortRxTxFactory} in the 
 *      example above) by implementing the interface 
 *      {@link com.digi.xbee.api.connection.serial.SerialPortParameters.ISerialConnectionInterfaceFactory}.
 *      </li>
 *    </ol>
 * </ul>
 */
public class XBee {
	
	public static String PROPERTY_SERIAL_CONNECTION_INTERFACE = "com.digi.xbee.api.serialInterface";
	
	private static String RXTX_SERIAL_CONNECTION_INTERFACE_NAME = "com.digi.xbee.api.connection.serial.SerialPortRxTx";
	private static String JSSC_SERIAL_CONNECTION_INTERFACE_NAME = "com.digi.xbee.api.connection.serial.SerialPortJSSC";
	private static String DEFAULT_SERIAL_CONNECTION_INTERFACE_NAME = RXTX_SERIAL_CONNECTION_INTERFACE_NAME;
	
	private static ISerialConnectionInterfaceFactory loadedFactory;
	
	private static Logger logger;
	
	/**
	 * Retrieves a serial port connection interface for the provided port with 
	 * the given baud rate.
	 * 
	 * @param port Serial port name.
	 * @param baudRate Serial port baud rate.
	 * 
	 * @return The serial port connection interface.
	 * 
	 * @throws IllegalArgumentException if {@code port.length == 0} or
	 *                                  if {@code baudRate < 0}.
	 * @throws NullPointerException if {@code port == null}.
	 * @throws SerialConnectionInterfaceNotCreatedException if the connection 
	 *                                                      interface cannot be created.
	 * 
	 * @see #createConnectiontionInterface(String, SerialPortParameters)
	 * @see com.digi.xbee.api.connection.IConnectionInterface
	 */
	public static IConnectionInterface createConnectiontionInterface(String port, int baudRate) {
		if (port == null)
			throw new NullPointerException("Serial port name cannot be null.");
		
		if (port.length() == 0)
			throw new IllegalArgumentException("Serial port name cannot be an empty string.");
		if (baudRate < 0)
			throw new IllegalArgumentException("Baudrate cannot be less than 0.");
		
		ISerialConnectionInterfaceFactory f = getSerialConnectionInterfaceFactory();
		return f.createInterface(port, baudRate);
	}
	
	/**
	 * Retrieves a serial port connection interface for the provided port with 
	 * the given serial port parameters.
	 * 
	 * @param port Serial port name.
	 * @param serialPortParameters Serial port parameters.
	 * 
	 * @return The serial port connection interface.
	 * 
	 * @throws IllegalArgumentException if {@code port.length == 0}.
	 * @throws NullPointerException if {@code port == null} or
	 *                              if {@code serialPortParameters == null}.
	 * @throws SerialConnectionInterfaceNotCreatedException if the connection 
	 *                                                      interface cannot be created.
	 * 
	 * @see #createConnectiontionInterface(String, int)
	 * @see com.digi.xbee.api.connection.IConnectionInterface
	 * @see com.digi.xbee.api.connection.serial.SerialPortParameters
	 */
	public static IConnectionInterface createConnectiontionInterface(String port, SerialPortParameters serialPortParameters) {
		if (port == null)
			throw new NullPointerException("Serial port name cannot be null.");
		if (serialPortParameters == null)
			throw new NullPointerException("Serial port parameters cannot be null.");
		
		if (port.length() == 0)
			throw new IllegalArgumentException("Serial port name cannot be an empty string.");
		
		ISerialConnectionInterfaceFactory f = getSerialConnectionInterfaceFactory();
		return f.createInterface(port, serialPortParameters);
	}
	
	/**
	 * Configures the serial connection interface factory to be used.
	 * 
	 * <p>If a serial connection interface factory is already loaded, this does 
	 * nothing.</p>
	 * 
	 * @throws NullPointerException if {@code factory == null}.
	 * 
	 * @param factory The serial connection interface factory to be used.
	 */
	public static void setSerialConnectionInterfaceFactory (ISerialConnectionInterfaceFactory factory) {
		if (factory == null)
			throw new NullPointerException("ISerialConnectionInterfaceFactory cannot be null.");
		if (loadedFactory != null)
			return; //FIXME Maybe we should throw an exception to be sure this cannot be done.
		
		loadedFactory = factory;
	}
	
	/**
	 * Returns the configured connection interface factory.
	 * 
	 * @return The configured connection interface factory.
	 * 
	 * @throws SerialConnectionInterfaceNotCreatedException if the serial connection
	 *                                                      interface cannot be created.
	 */
	private static ISerialConnectionInterfaceFactory getSerialConnectionInterfaceFactory() throws SerialConnectionInterfaceNotCreatedException {
		
		if (loadedFactory != null) // Interface already loaded.
			return loadedFactory;
		
		logger = LoggerFactory.getLogger(XBee.class);
		
		// Get the name of the class from a configuration option.
		String name = System.getProperty(PROPERTY_SERIAL_CONNECTION_INTERFACE);
		
		// If not defined, let's try with the default one: RxTx.
		if (name == null || name.length() == 0) {
			logger.info("Serial connnection interface not specified, using default.");
			name = DEFAULT_SERIAL_CONNECTION_INTERFACE_NAME;
		}
		
		// Interface not loaded.
		try {
			Class.forName(name);
			
			if (loadedFactory == null)
				throw new SerialConnectionInterfaceNotCreatedException(
						String.format("Error loading serial connection interface factory: %s.", name));
		} catch (ClassNotFoundException e) {
			throw new SerialConnectionInterfaceNotCreatedException(
					String.format("Error loading serial connection interface factory: %s.", name), e);
		}
		return loadedFactory;
	}
}
