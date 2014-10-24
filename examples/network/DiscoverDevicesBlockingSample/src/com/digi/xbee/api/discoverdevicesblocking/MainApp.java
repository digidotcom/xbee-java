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
package com.digi.xbee.api.discoverdevicesblocking;

import java.util.List;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.XBeeException;

/**
 * XBee Java Library Discover Devices Blocking sample application.
 * 
 * <p>This example retrieves the XBee network from the local XBee device and 
 * performs a remote device discovery process using a blocking mechanism 
 * (application is blocked until the discovery process finishes).</p>
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
		System.out.println(" +----------------------------------------------------+");
		System.out.println(" | XBee Java Library Discover Devices Blocking Sample |");
		System.out.println(" +----------------------------------------------------+\n");
		
		XBeeDevice myDevice = new XBeeDevice(PORT, BAUD_RATE);
		XBeeNetwork myXBeeNetwork = null;
		List<RemoteXBeeDevice> discoveredDevices = null;
		
		try {
			myDevice.open();
			
			myXBeeNetwork = myDevice.getNetwork();
			
		} catch (XBeeException e) {
			e.printStackTrace();
			myDevice.close();
			System.exit(1);
		}
		
		try {
			myXBeeNetwork.setDiscoveryTimeout(15000);
			
			System.out.println("\n>> Discovering remote XBee devices...");
			
			discoveredDevices = myXBeeNetwork.discoverDevices();
			
			System.out.println(">> Discovery process finished successfully.");
			
		} catch (XBeeException e) {
			System.out.println(">> Discovery process finished due to the following error: " + e.getMessage());
			myDevice.close();
			System.exit(1);
		}
		
		for (RemoteXBeeDevice discoveredDevice:discoveredDevices) 
			System.out.format(">> Device discovered: %s%n", discoveredDevice.toString());
		
		System.exit(0);
	}
}
