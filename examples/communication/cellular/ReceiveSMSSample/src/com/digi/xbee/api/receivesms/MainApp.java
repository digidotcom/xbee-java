/**
 * Copyright (c) 2016-2017 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */
package com.digi.xbee.api.receivesms;

import com.digi.xbee.api.CellularDevice;
import com.digi.xbee.api.exceptions.XBeeException;

/**
 * XBee Java Library Receive SMS sample application.
 * 
 * <p>This example configures a Cellular device to read SMS (sent from a phone 
 * or Cellular device) using a callback that is executed when new SMS is 
 * received.</p>
 * 
 * <p>For a complete description on the example, refer to the 'ReadMe.txt' file
 * included in the root directory.</p>
 */
public class MainApp {
	
	/* Constants */
	
	// TODO Replace with the serial port where your Cellular module is connected.
	private static final String PORT = "COM1";
	// TODO Replace with the baud rate of your Cellular module.
	private static final int BAUD_RATE = 9600;

	/**
	 * Application main method.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		System.out.println(" +----------------------------------------+");
		System.out.println(" |  XBee Java Library Receive SMS Sample  |");
		System.out.println(" +----------------------------------------+\n");
		
		CellularDevice myDevice = new CellularDevice(PORT, BAUD_RATE);
		
		try {
			myDevice.open();
			
			myDevice.addSMSListener(new MySMSReceiveListener());
			
			System.out.println("\n>> Waiting for SMS...");
			
		} catch (XBeeException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
