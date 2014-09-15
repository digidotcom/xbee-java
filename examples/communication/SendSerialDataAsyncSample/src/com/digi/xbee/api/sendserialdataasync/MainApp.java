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
package com.digi.xbee.api.sendserialdataasync;

import com.digi.xbee.api.ZigBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
//TODO Uncomment this import if you are using a 16-bit destination address.
//import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.utils.HexUtils;

/**
 * XBee Java Library Send Data Asynchronously sample application.
 * 
 * <p>This example sends data asynchronously to a remote device with the 
 * provided 64-bit or 16-bit address.</p>
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
	
	// TODO Replace with the 64-bit address of your receiver module.
	private static final XBee64BitAddress DESTINATION_64_BIT_ADDRESS = new XBee64BitAddress("0013A20040XXXXXX");
	// TODO Replace with the 16-bit address of your receiver module.
	//private static final XBee16BitAddress DESTINATION_16_BIT_ADDRESS = new XBee16BitAddress("XXXX");
	
	// TODO Replace with the data to send.
	private static final String DATA_TO_SEND = "Hello XBee!";
	
	/**
	 * Application main method.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		System.out.println(" +---------------------------------------------------+");
		System.out.println(" |  XBee Java Library Send Data Asynchrously Sample  |");
		System.out.println(" +---------------------------------------------------+\n");
		
		ZigBeeDevice myDevice = new ZigBeeDevice(PORT, BAUD_RATE);
		byte[] dataToSend = DATA_TO_SEND.getBytes();
		
		// Use an XBee64BitAddress object when using a 64-bit destination address.
		XBee64BitAddress destinationAddress = DESTINATION_64_BIT_ADDRESS;
		// Use an XBee16BitAddress object when using a 16-bit destination address.
		//XBee16BitAddress destinationAddress = DESTINATION_16_BIT_ADDRESS;
		
		try {
			myDevice.open();
			
			System.out.format("Sending data to %s >> %s | %s... ", destinationAddress, 
					HexUtils.prettyHexString(HexUtils.byteArrayToHexString(dataToSend)), 
					new String(dataToSend));
			
			myDevice.sendSerialDataAsync(destinationAddress, dataToSend);
			
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
