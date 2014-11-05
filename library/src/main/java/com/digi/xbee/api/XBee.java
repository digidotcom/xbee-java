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

import android.content.Context;

import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.connection.android.AndroidUSBPermissionListener;
import com.digi.xbee.api.connection.android.AndroidXBeeInterface;
import com.digi.xbee.api.connection.serial.SerialPortParameters;
import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.connection.serial.SerialPortRxTxAndroid;

/**
 * Helper class used to create a serial port connection interface.
 */
public class XBee {
	
	/**
	 * Retrieves a serial port connection interface for the provided port with 
	 * the given baud rate.
	 * 
	 * @param port Serial port name.
	 * @param baudRate Serial port baud rate.
	 * 
	 * @return The serial port connection interface.
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 1}.
	 * @throws NullPointerException if {@code port == null}.
	 * 
	 * @see #createConnectiontionInterface(String, SerialPortParameters)
	 * @see com.digi.xbee.api.connection.IConnectionInterface
	 */
	public static IConnectionInterface createConnectiontionInterface(String port, int baudRate) {
		IConnectionInterface connectionInterface;
		if (isAndroid())
			connectionInterface = new SerialPortRxTxAndroid(port, baudRate);
		else
			connectionInterface = new SerialPortRxTx(port, baudRate);
		return connectionInterface;
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
	 * @throws NullPointerException if {@code port == null} or
	 *                              if {@code serialPortParameters == null}.
	 * 
	 * @see #createConnectiontionInterface(String, int)
	 * @see com.digi.xbee.api.connection.IConnectionInterface
	 * @see com.digi.xbee.api.connection.serial.SerialPortParameters
	 */
	public static IConnectionInterface createConnectiontionInterface(String port, SerialPortParameters serialPortParameters) {
		IConnectionInterface connectionInterface;
		if (isAndroid())
			connectionInterface = new SerialPortRxTxAndroid(port, serialPortParameters);
		else
			connectionInterface = new SerialPortRxTx(port, serialPortParameters);
		return connectionInterface;
	}
	
	/**
	 * Retrieves an XBee Android connection interface for the given context and
	 * baud rate.
	 * 
	 * @param context The Android context.
	 * @param baudRate The USB connection baud rate.
	 * 
	 * @return The XBee Android connection interface.
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 1}.
	 * @throws NullPointerException if {@code context == null}.
	 * 
	 * @see #createConnectiontionInterface(String, int)
	 * @see com.digi.xbee.api.connection.IConnectionInterface
	 * @see com.digi.xbee.api.connection.serial.SerialPortParameters
	 */
	public static IConnectionInterface createConnectiontionInterface(Context context, int baudRate) {
		return createConnectiontionInterface(context, baudRate, null);
	}
	
	/**
	 * Retrieves an XBee Android connection interface for the given context and
	 * baud rate.
	 * 
	 * @param context The Android context.
	 * @param baudRate The USB connection baud rate.
	 * @param permissionListener The USB permission listener that will be 
	 *                           notified when user grants USB permissions.
	 * 
	 * @return The XBee Android connection interface.
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 1}.
	 * @throws NullPointerException if {@code context == null}.
	 * 
	 * @see #createConnectiontionInterface(String, int)
	 * @see com.digi.xbee.api.connection.IConnectionInterface
	 * @see com.digi.xbee.api.connection.serial.SerialPortParameters
	 * @see com.digi.xbee.api.connection.android.AndroidUSBPermissionListener
	 */
	public static IConnectionInterface createConnectiontionInterface(Context context, int baudRate, AndroidUSBPermissionListener permissionListener) {
		if (context == null)
			throw new NullPointerException("Android context cannot be null");
		
		return new AndroidXBeeInterface(context, baudRate, permissionListener);
	}
	
	/**
	 * Retrieves whether the API is running in Android or not.
	 * 
	 * @return {@code true} if the API is running in an Android system, {@code false} otherwise.
	 */
	private static boolean isAndroid() {
		String property = System.getProperty("java.runtime.name");
		if (property != null && property.equalsIgnoreCase("Android Runtime"))
			return true;
		return false;
	}
}
