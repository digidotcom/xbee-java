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
package com.digi.xbee.api.senddataasync;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.utils.HexUtils;

/**
 * XBee Java Library Send Data Asynchronously sample application.
 * 
 * <p>This example sends data asynchronously to a remote device whose Node 
 * Identifier is 'REMOTE'.</p>
 * 
 * <p>For a complete description on the example, refer to the 'ReadMe.txt' file
 * included in the root directory.</p>
 */
public class MainApp {
	
	/* Constants */
	
	// TODO Replace with the serial port where your sender module is connected to.
	private static final String PORT = "COM1";
	// TODO Replace with the baud rate of your sender module.
	private static final int BAUD_RATE = 9600;
	
	private static final String DATA_TO_SEND = "Hello XBee!";
	private static final String REMOTE_NODE_IDENTIFIER = "REMOTE";
	
	/**
	 * Application main method.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		System.out.println(" +---------------------------------------------------+");
		System.out.println(" |  XBee Java Library Send Data Asynchrously Sample  |");
		System.out.println(" +---------------------------------------------------+\n");
		
		XBeeDevice myDevice = new XBeeDevice(PORT, BAUD_RATE);
		byte[] dataToSend = DATA_TO_SEND.getBytes();
		
		try {
			myDevice.open();
			
			// Obtain the remote XBee device from the XBee network.
			XBeeNetwork xbeeNetwork = myDevice.getNetwork();
			RemoteXBeeDevice remoteDevice = xbeeNetwork.discoverDevice(REMOTE_NODE_IDENTIFIER);
			if (remoteDevice == null) {
				System.out.println("Couldn't find the remote XBee device with '" + REMOTE_NODE_IDENTIFIER + "' Node Identifier.");
				System.exit(1);
			}
			
			System.out.format("Sending data to %s >> %s | %s... ", remoteDevice.get64BitAddress(), 
					HexUtils.prettyHexString(HexUtils.byteArrayToHexString(dataToSend)), 
					new String(dataToSend));
			
			myDevice.sendDataAsync(remoteDevice, dataToSend);
			
			System.out.println("Success");
			
		} catch (XBeeException e) {
			System.out.println("Error");
			e.printStackTrace();
			System.exit(1);
		} finally {
			myDevice.close();
		}
	}
}
