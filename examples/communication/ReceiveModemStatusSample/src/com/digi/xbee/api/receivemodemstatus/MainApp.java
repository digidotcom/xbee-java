/**
 * Copyright (c) 2014-2015 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */
package com.digi.xbee.api.receivemodemstatus;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;

/**
 * XBee Java Library Receive Modem Status sample application.
 * 
 * <p>This example registers a listener to manage the received Modem Status 
 * events.</p>
 * 
 * <p>For a complete description on the example, refer to the 'ReadMe.txt' file
 * included in the root directory.</p>
 */
public class MainApp {
	
	/* Constants */
	
	// TODO Replace with the serial port where your module is connected to.
	private static final String PORT = "COM1";
	// TODO Replace with the baud rate of you receiver module.
	private static final int BAUD_RATE = 9600;

	/**
	 * Application main method.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		System.out.println(" +-----------------------------------------------+");
		System.out.println(" | XBee Java Library Receive Modem Status Sample |");
		System.out.println(" +-----------------------------------------------+\n");
		
		XBeeDevice myDevice = new XBeeDevice(PORT, BAUD_RATE);
		
		try {
			myDevice.open();
			
			myDevice.addModemStatusListener(new MyModemStatusReceiveListener());
			
			System.out.println("\n>> Waiting for Modem Status events...");
			
		} catch (XBeeException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
