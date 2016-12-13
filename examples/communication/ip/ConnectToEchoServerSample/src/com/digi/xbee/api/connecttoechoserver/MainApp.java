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
package com.digi.xbee.api.connecttoechoserver;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import com.digi.xbee.api.CellularDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.IPMessage;
import com.digi.xbee.api.models.IPProtocol;

/**
 * XBee Java Library Connect to Echo Server sample application.
 * 
 * <p>This example connects to an echo server, sends data to it and reads the 
 * echoed data.</p>
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
	// TODO Optionally, replace with the text you want to send to the server.
	private static final String TEXT = "Hello XBee!";
	
	private static final String ECHO_SERVER = "52.43.121.77";
	
	private static final int ECHO_SERVER_PORT = 11001;
	
	private static final IPProtocol PROTOCOL_TCP = IPProtocol.TCP;
	
	/**
	 * Application main method.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		System.out.println(" +---------------------------------------------------+");
		System.out.println(" |  XBee Java Library Connect to Echo Server Sample  |");
		System.out.println(" +---------------------------------------------------+\n");
		
		// For XBee Wi-Fi modules, use the WiFiDevice class instead.
		CellularDevice myDevice = new CellularDevice(PORT, BAUD_RATE);
		byte[] dataToSend = TEXT.getBytes();
		
		try {
			myDevice.open();
			
			System.out.format("Sending text to %s:%s >> '%s'... ", ECHO_SERVER, ECHO_SERVER_PORT,
					new String(dataToSend));
			myDevice.sendIPData((Inet4Address) Inet4Address.getByName(ECHO_SERVER), 
					ECHO_SERVER_PORT, PROTOCOL_TCP, TEXT.getBytes());
			
			System.out.println("Success");
			
			// Read the echoed data.
			IPMessage response = myDevice.readIPData();
			if (response == null) {
				System.out.format("Echo response was not received from the server."); 
				System.exit(1);
			}
			System.out.format("Echo response received from %s:%s >> '%s'\n", response.getIPAddress(), 
					response.getSourcePort(),
					response.getDataString());
		} catch (XBeeException | UnknownHostException e) {
			System.out.println("Error sending data to the echo server");
			e.printStackTrace();
			System.exit(1);
		} finally {
			myDevice.close();
		}
	}
}
