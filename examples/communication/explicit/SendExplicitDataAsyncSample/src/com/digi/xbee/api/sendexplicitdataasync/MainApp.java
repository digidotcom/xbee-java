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
package com.digi.xbee.api.sendexplicitdataasync;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.ZigBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.utils.HexUtils;

/**
 * XBee Java Library Send Explicit Data Asynchronously sample application.
 * 
 * <p>This example sends data in application layer (explicit) format 
 * asynchronously to a remote device whose Node Identifier is 'REMOTE'.</p>
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
	
	private static final int SOURCE_ENDPOINT = 0xA0;
	private static final int DESTINATION_ENDPOINT = 0xA1;
	
	private static final byte[] CLUSTER_ID = new byte[]{(byte)0x15, (byte)0x54};
	private static final byte[] PROFILE_ID = new byte[]{(byte)0xC1, (byte)0x05};
	
	/**
	 * Application main method.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		System.out.println(" +------------------------------------------------------------+");
		System.out.println(" |  XBee Java Library Send Explicit Data Asynchrously Sample  |");
		System.out.println(" +------------------------------------------------------------+\n");
		
		ZigBeeDevice myDevice = new ZigBeeDevice(PORT, BAUD_RATE);
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
			
			System.out.format("Sending explicit data asynchronously to %s [%s - %s - %s - %s] >> %s | %s... ", 
					remoteDevice.get64BitAddress(), 
					HexUtils.integerToHexString(SOURCE_ENDPOINT, 1), 
					HexUtils.integerToHexString(DESTINATION_ENDPOINT, 1), 
					HexUtils.byteArrayToHexString(CLUSTER_ID), 
					HexUtils.byteArrayToHexString(PROFILE_ID), 
					HexUtils.prettyHexString(HexUtils.byteArrayToHexString(dataToSend)), 
					new String(dataToSend));
			
			myDevice.sendExplicitDataAsync(remoteDevice, SOURCE_ENDPOINT, DESTINATION_ENDPOINT, CLUSTER_ID, PROFILE_ID, dataToSend);
			
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
