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
package com.digi.xbee.api.remotedio;

import java.util.Timer;
import java.util.TimerTask;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOMode;
import com.digi.xbee.api.io.IOValue;
import com.digi.xbee.api.models.XBee64BitAddress;

/**
 * XBee Java Library Get/Set Remote DIO sample application.
 * 
 * <p>This example reads the status of the remote input line periodically and 
 * updates the local output to follow the input.</p>
 * 
 * <p>For a complete description on the example, refer to the 'ReadMe.txt' file
 * included in the root directory.</p>
 */
public class MainApp {
	
	/* Constants */
	
	// TODO Replace with the serial port where your local module is connected to.
	private static final String PORT = "COM1";
	// TODO Replace with the baud rate of your local module.
	private static final int BAUD_RATE = 9600;
	private static final int READ_TIMEOUT = 250;
	
	// TODO Replace with the 64-bit address of your remote module.
	private static final XBee64BitAddress REMOTE_64_BIT_ADDRESS = new XBee64BitAddress("0013A20040XXXXXX");
	
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
		System.out.println(" +----------------------------------------------+");
		System.out.println(" | XBee Java Library Get/Set Remote DIO Sample  |");
		System.out.println(" +----------------------------------------------+\n");
		
		XBeeDevice localDevice = new XBeeDevice(PORT, BAUD_RATE);
		
		Timer readDIOTimer = new Timer();
		
		try {
			localDevice.open();
			
			RemoteXBeeDevice remoteDevice = new RemoteXBeeDevice(localDevice, REMOTE_64_BIT_ADDRESS);
			
			// Configure the IOLINE_IN of the remote device as Digital Input.
			remoteDevice.setIOConfiguration(IOLINE_IN, IOMode.DIGITAL_IN);
			
			// Configure the IOLINE_OUT of the local device as Digital Output Low.
			localDevice.setIOConfiguration(IOLINE_OUT, IOMode.DIGITAL_OUT_LOW);
			
			readDIOTimer.schedule(new UpdateOutputTask(localDevice, remoteDevice), 0, READ_TIMEOUT);
			
		} catch (XBeeException e) {
			e.printStackTrace();
			localDevice.close();
			System.exit(1);
		}
	}
	
	/**
	 * Update output task to be performed every {@value #READ_TIMEOUT} ms.
	 *
	 * <p>The task will read the remote digital input state of {@code IOLINE_IN} 
	 * and set the local {@code IOLINE_OUT} output with the same value.</p>
	 * 
	 * @see TimerTask
	 */
	private static class UpdateOutputTask extends TimerTask {
		private XBeeDevice localDevice;
		private RemoteXBeeDevice remoteDevice;
		
		public UpdateOutputTask(XBeeDevice local, RemoteXBeeDevice remote) {
			this.localDevice = local;
			this.remoteDevice = remote;
		}
		
		@Override
		public void run() {
			try {
				// Read the digital value from the remote input line.
				IOValue value = remoteDevice.getDIOValue(IOLINE_IN);
				System.out.println("Input line value: " + value);
				
				// Set the previous value to the local output line.
				localDevice.setDIOValue(IOLINE_OUT, value);
			} catch (XBeeException e) {
				e.printStackTrace();
			}
		}
	}
}
