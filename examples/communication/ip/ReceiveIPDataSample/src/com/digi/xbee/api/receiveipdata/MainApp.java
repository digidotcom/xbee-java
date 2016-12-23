/**
 * Copyright (c) 2016 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */
package com.digi.xbee.api.receiveipdata;

import com.digi.xbee.api.WiFiDevice;
import com.digi.xbee.api.exceptions.XBeeException;

/**
 * XBee Java Library Receive IP Data sample application.
 *
 * <p>This example registers a listener to manage the received IP data.</p>
 *
 * <p>For a complete description on the example, refer to the 'ReadMe.txt' file
 * included in the root directory.</p>
 */
public class MainApp {

	/* Constants */

	// TODO Replace with the serial port where your receiver module is connected.
	private static final String PORT = "COM1";
	// TODO Replace with the baud rate of you receiver module.
	private static final int BAUD_RATE = 9600;

	/**
	 * Application main method.
	 *
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		System.out.println(" +--------------------------------------------+");
		System.out.println(" |  XBee Java Library Receive IP Data Sample  |");
		System.out.println(" +--------------------------------------------+\n");

		// For XBee Cellular modules, use the CellularDevice class instead.
		WiFiDevice myDevice = new WiFiDevice(PORT, BAUD_RATE);

		try {
			myDevice.open();

			if (!myDevice.isConnected()) {
				System.err.println(">> Error: the device is not connected to the network");
				return;
			}

			myDevice.addIPDataListener(new MyIPDataReceiveListener());

			System.out.println("\n>> Waiting for data...");

		} catch (XBeeException e) {
			e.printStackTrace();
			myDevice.close();
			System.exit(1);
		}
	}
}
