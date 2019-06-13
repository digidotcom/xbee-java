/*
 * Copyright 2017-2019, Digi International Inc.
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

import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.connection.serial.SerialPortParameters;
import com.digi.xbee.api.connection.serial.SerialPortRxTx;

/**
 * Helper class used to create a serial port connection interface.
 */
public class XBee {
	
	/**
	 * Returns a serial port connection interface for the provided port with 
	 * the given baud rate.
	 * 
	 * @param port Serial port name.
	 * @param baudRate Serial port baud rate.
	 * 
	 * @return The serial port connection interface.
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 0}.
	 * @throws NullPointerException if {@code port == null}.
	 * 
	 * @see #createConnectiontionInterface(String, SerialPortParameters)
	 * @see com.digi.xbee.api.connection.IConnectionInterface
	 */
	public static IConnectionInterface createConnectiontionInterface(String port, int baudRate) {
		return new SerialPortRxTx(port, baudRate);
	}
	
	/**
	 * Returns a serial port connection interface for the provided port with 
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
		return new SerialPortRxTx(port, serialPortParameters);
	}
}
