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
package com.digi.xbee.api.sendsms;

import com.digi.xbee.api.CellularDevice;
import com.digi.xbee.api.exceptions.XBeeException;

/**
 * XBee Java Library Send SMS sample application.
 * 
 * <p>This example sends an SMS to a phone or Cellular device.</p>
 * 
 * <p>For a complete description on the example, refer to the 'ReadMe.txt' file
 * included in the root directory.</p>
 */
public class MainApp {
	
	/* Constants */
	
	// TODO Replace with the serial port where your Cellular module is connected.
	private static final String PORT = "COM1";
	// TODO Replace with the baud rate of you Cellular module.
	private static final int BAUD_RATE = 9600;
	// TODO Replace with the phone number of the device to send the SMS to.
	private static final String PHONE = "";
	// TODO Optionally, replace with the text of the SMS.
	private static final String SMS_TEXT = "Hello from XBee Cellular!";

	/**
	 * Application main method.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		System.out.println(" +-------------------------------------+");
		System.out.println(" |  XBee Java Library Send SMS Sample  |");
		System.out.println(" +-------------------------------------+\n");
		
		CellularDevice myDevice = new CellularDevice(PORT, BAUD_RATE);
		
		try {
			myDevice.open();
			
			System.out.format("Sending SMS to %s >> '%s'... ", 
					PHONE, 
					SMS_TEXT);
			
			myDevice.sendSMS(PHONE, SMS_TEXT);
			
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
