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
package com.digi.xbee.api.receivedatapolling;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.XBeeMessage;
import com.digi.xbee.api.utils.HexUtils;

/**
 * XBee Java Library Receive Data polling sample application.
 * 
 * <p>This example reads data from the local device using the polling mechanism.</p>
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
		System.out.println(" +-----------------------------------------+");
		System.out.println(" |  XBee Java Library Data Polling Sample  |");
		System.out.println(" +-----------------------------------------+\n");
		
		XBeeDevice myDevice = new XBeeDevice(PORT, BAUD_RATE);
		
		try {
			myDevice.open();
		} catch (XBeeException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		while (true) {
			XBeeMessage xbeeMessage = myDevice.readData();
			if (xbeeMessage != null)
				System.out.format("From %s >> %s | %s%n", xbeeMessage.getDevice().get64BitAddress(), 
						HexUtils.prettyHexString(HexUtils.byteArrayToHexString(xbeeMessage.getData())), 
						new String(xbeeMessage.getData()));
		}
	}
}
