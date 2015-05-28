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
package com.digi.xbee.api.receiveexplicitdatapolling;

import com.digi.xbee.api.ZigBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.APIOutputMode;
import com.digi.xbee.api.models.ExplicitXBeeMessage;
import com.digi.xbee.api.utils.HexUtils;

/**
 * XBee Java Library Receive Explicit Data polling sample application.
 * 
 * <p>This example configures a ZigBee device to read data in application layer 
 * (explicit) mode from the ZigBee device using the polling mechanism.</p>
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
		System.out.println(" |  XBee Java Library Explicit Data Polling Sample  |");
		System.out.println(" +--------------------------------------------------+\n");
		
		ZigBeeDevice myZigBeeDevice = new ZigBeeDevice(PORT, BAUD_RATE);
		
		try {
			myZigBeeDevice.open();
			myZigBeeDevice.setAPIOutputMode(APIOutputMode.MODE_EXPLICIT);
		} catch (XBeeException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		while (true) {
			ExplicitXBeeMessage explicitXBeeMessage = myZigBeeDevice.readExplicitData();
			if (explicitXBeeMessage != null) {
				System.out.format("From %s >> %s | %s%n", explicitXBeeMessage.getDevice().get64BitAddress(), 
						HexUtils.prettyHexString(HexUtils.byteArrayToHexString(explicitXBeeMessage.getData())), 
						new String(explicitXBeeMessage.getData()));
				System.out.format(" - Source endpoint: 0x%02X%n", explicitXBeeMessage.getSourceEndpoint());
				System.out.format(" - Destination endpoint: 0x%02X%n", explicitXBeeMessage.getDestinationEndpoint());
				System.out.format(" - Cluster ID: 0x%04X%n", explicitXBeeMessage.getClusterID());
				System.out.format(" - Profile ID: 0x%04X%n%n",explicitXBeeMessage.getProfileID());
			}
		}
	}
}
