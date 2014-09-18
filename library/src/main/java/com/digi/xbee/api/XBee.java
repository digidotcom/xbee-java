/**
* Copyright (c) 2014 Digi International Inc.,
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

import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.connection.serial.SerialPortParameters;
import com.digi.xbee.api.connection.serial.SerialPortRxTx;

public class XBee {
	
	/**
	 * Retrieves a serial port connection interface with the given serial port 
	 * name and baud rate.
	 * 
	 * @param port Serial port name.
	 * @param baudRate Serial port baud rate.
	 * @return The serial port connection interface.
	 * 
	 * @throws NullPointerException if {@code port == null}.
	 * 
	 * @see IConnectionInterface
	 */
	public static IConnectionInterface createConnectiontionInterface(String port, int baudRate) {
		IConnectionInterface connectionInterface = new SerialPortRxTx(port, baudRate);
		return connectionInterface;
	}
	
	/**
	 * Retrieves a serial port connection interface with the given serial port 
	 * parameters.
	 * 
	 * @param port Serial port name.
	 * @param serialPortParameters Serial port parameters.
	 * @return The serial port connection interface.
	 * 
	 * @throws NullPointerException if {@code port == null} or
	 *                              if {@code serialPortParameters == null}.
	 * 
	 * @see SerialPortParameters
	 * @see IConnectionInterface
	 */
	public static IConnectionInterface createConnectiontionInterface(String port, SerialPortParameters serialPortParameters) {
		IConnectionInterface connectionInterface = new SerialPortRxTx(port, serialPortParameters);
		return connectionInterface;
	}
}
