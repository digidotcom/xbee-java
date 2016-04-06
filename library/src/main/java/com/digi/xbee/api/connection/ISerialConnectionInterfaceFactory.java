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
package com.digi.xbee.api.connection;

import com.digi.xbee.api.connection.serial.SerialPortParameters;

/**
 * This interface represents a {@code IConnectionInterface} factory to create 
 * protocol independent connection with an XBee device.
 * 
 * @see IConnectionInterface
 */
public interface ISerialConnectionInterfaceFactory {

	/**
	 * Retrieves an {@code IConnectionInterface} for the provided port with 
	 * the given baud rate.
	 * 
	 * @param port Serial port name.
	 * @param baudRate Serial port baud rate.
	 * 
	 * @return A {@code IConnectionInterface} to the specified port.
	 * 
	 * @throws IllegalArgumentException if {@code port.length == 0} or
	 *                                  if {@code baudRate < 0}.
	 * @throws NullPointerException if {@code port == null}.
	 * 
	 * @see #createInterface(String, SerialPortParameters)
	 * @see com.digi.xbee.api.connection.IConnectionInterface
	 */
	public IConnectionInterface createInterface(String port, int baudRate);
	
	/**
	 * Retrieves an {@code IConnectionInterface} for the provided port with 
	 * the given serial port parameters.
	 * 
	 * @param port Serial port name.
	 * @param serialPortParameters Serial port parameters.
	 * 
	 * @return A {@code IConnectionInterface} to the specified port.
	 * 
	 * @throws IllegalArgumentException if {@code port.length == 0}.
	 * @throws NullPointerException if {@code port == null} or
	 *                              if {@code serialPortParameters == null}.
	 * 
	 * @see #createInterface(String, int)
	 * @see com.digi.xbee.api.connection.IConnectionInterface
	 * @see com.digi.xbee.api.connection.serial.SerialPortParameters
	 */
	public IConnectionInterface createInterface(String port, SerialPortParameters serialPortParameters);
}
