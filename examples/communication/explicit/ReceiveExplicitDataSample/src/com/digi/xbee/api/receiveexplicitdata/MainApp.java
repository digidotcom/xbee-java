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
package com.digi.xbee.api.receiveexplicitdata;

import com.digi.xbee.api.ZigBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.APIOutputMode;

/**
 * XBee Java Library Receive Explicit Data sample application.
 * 
 * <p>This example configures a ZigBee device to read data in application layer 
 * (explicit) mode from the ZigBee device using a callback that is executed when
 * new data in explicit format is received.</p>
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
		System.out.println(" +--------------------------------------------------+");
		System.out.println(" |  XBee Java Library Receive Explicit Data Sample  |");
		System.out.println(" +--------------------------------------------------+\n");
		
		ZigBeeDevice myDevice = new ZigBeeDevice(PORT, BAUD_RATE);
		
		try {
			myDevice.open();
			myDevice.setAPIOutputMode(APIOutputMode.MODE_EXPLICIT);
			
			myDevice.addExplicitDataListener(new MyExplicitDataReceiveListener());
			
			System.out.println("\n>> Waiting for data in explicit format...");
			
		} catch (XBeeException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
