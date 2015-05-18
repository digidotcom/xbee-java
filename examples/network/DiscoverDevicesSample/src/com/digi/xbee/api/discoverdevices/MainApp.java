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
package com.digi.xbee.api.discoverdevices;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.XBeeException;

/**
 * XBee Java Library Discover Devices sample application.
 * 
 * <p>This example retrieves the XBee network from the local XBee device and 
 * performs a remote device discovery.</p>
 * 
 * <p>For a complete description on the example, refer to the 'ReadMe.txt' file
 * included in the root directory.</p>
 */
public class MainApp {

	/* Constants */
	
	// TODO Replace with the serial port where your module is connected to.
	private static final String PORT = "COM1";
	// TODO Replace with the baud rate of your module.
	private static final int BAUD_RATE = 9600;
	
	/**
	 * Application main method.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		System.out.println(" +-------------------------------------------+");
		System.out.println(" | XBee Java Library Discover Devices Sample |");
		System.out.println(" +-------------------------------------------+\n");
		
		XBeeDevice myDevice = new XBeeDevice(PORT, BAUD_RATE);
		
		try {
			myDevice.open();
			
			XBeeNetwork myXBeeNetwork = myDevice.getNetwork();
			
			myXBeeNetwork.setDiscoveryTimeout(15000);
			
			myXBeeNetwork.addDiscoveryListener(new MyDiscoveryListener());
			
			myXBeeNetwork.startDiscoveryProcess();
			
			System.out.println("\n>> Discovering remote XBee devices...");
			
		} catch (XBeeException e) {
			e.printStackTrace();
			myDevice.close();
			System.exit(1);
		}
	}
}
