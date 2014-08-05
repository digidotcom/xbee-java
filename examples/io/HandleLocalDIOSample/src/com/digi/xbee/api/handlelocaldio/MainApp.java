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
package com.digi.xbee.api.handlelocaldio;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOMode;
import com.digi.xbee.api.io.IOValue;

/**
 * XBee Java Library Handle Local DIO sample application.
 * 
 * <p>This example periodically reads the digital input value of the selected
 * IO line in order to change the status of the output line.</p>
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
	
	// TODO Comment this line if your are not using the XBIB-U-DEV.
	private static final IOLine IOLINE_IN = IOLine.DIO3_AD3;
	// TODO Uncomment this line if you are using the XBee Development board.
//	private static final IOLine IOLINE_IN = IOLine.DIO4_AD4;
	
	private static final IOLine IOLINE_OUT = IOLine.DIO12;
	
	/**
	 * Application main method.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		System.out.println(" +---------------------------------------------+");
		System.out.println(" |  XBee Java Library Handle Local DIO Sample  |");
		System.out.println(" +---------------------------------------------+\n");
		
		final XBeeDevice myDevice = new XBeeDevice(PORT, BAUD_RATE);
		
		try {
			// Open the local device.
			myDevice.open();
			
			// Configure the IO lines.
			myDevice.setIOConfiguration(IOLINE_IN, IOMode.DIGITAL_IN);
			myDevice.setIOConfiguration(IOLINE_OUT, IOMode.DIGITAL_OUT_LOW);
			
			// Create a thread that reads the digital input and sets the digital output every 250ms.
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						while (true) {
							// Read the digital value from the input line.
							IOValue value = myDevice.getDIOValue(IOLINE_IN);
							System.out.println("Input line value: " + value);
							// Set the previous value to the output line.
							myDevice.setDIOValue(IOLINE_OUT, value);
							// Sleep 250ms.
							Thread.sleep(250);
						}
					} catch (XBeeException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			thread.start();
		} catch (XBeeException e) {
			e.printStackTrace();
		}
	}

}
