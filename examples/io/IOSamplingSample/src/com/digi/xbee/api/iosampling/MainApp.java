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
package com.digi.xbee.api.iosampling;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOMode;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.listeners.IIOSampleReceiveListener;
import com.digi.xbee.api.models.XBee64BitAddress;

/**
 * XBee Java Library Handle IO Samples sample application.
 * 
 * <p>This example enables automatic IO sampling in the remote module and 
 * listens for new samples from it.</p>
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
	
	// TODO Replace with the 64-bit address of your remote module.
	private static final XBee64BitAddress REMOTE_64_BIT_ADDRESS = new XBee64BitAddress("0013A20040XXXXXX");
	
	// TODO Comment this line if your are not using the XBIB-U-DEV.
	private static final IOLine IOLINE_IN = IOLine.DIO3_AD3;
	// TODO Uncomment this line if you are using the XBee Development board.
//	private static final IOLine IOLINE_IN = IOLine.DIO4_AD4;
	
	// TODO Comment this line if you are not using the XBIB-U-DEV.
	private static final byte[] CHANGE_DETECTION_MASK = new byte[] {0x08};
	// TODO Uncomment this line if you are using the XBee Development board.
//	private static final byte[] CHANGE_DETECTION_MASK = new byte[] {0x10};
	
	private static final int IO_SAMPLING_RATE = 5000; // 5 seconds.
	
	/**
	 * Application main method.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		System.out.println(" +----------------------------------------------+");
		System.out.println(" |  XBee Java Library Handle IO Samples Sample  |");
		System.out.println(" +----------------------------------------------+\n");
		
		XBeeDevice localDevice = new XBeeDevice(PORT, BAUD_RATE);
		
		try {
			localDevice.open();
			
			RemoteXBeeDevice remoteDevice = new RemoteXBeeDevice(localDevice, REMOTE_64_BIT_ADDRESS);
			
			// Set the local device as destination address of the remote.
			remoteDevice.setDestinationAddress(localDevice.get64BitAddress());
			
			// Set the IOLINE_IN of the remote device as Digital Input.
			remoteDevice.setIOConfiguration(IOLINE_IN, IOMode.DIGITAL_IN);
			
			// Enable DIO change detection over the IOLINE_IN in the remote device.
			remoteDevice.setDIOChangeDetection(CHANGE_DETECTION_MASK);
			
			// Enable periodic sampling every IO_SAMPLING_RATE milliseconds in the remote device.
			remoteDevice.setIOSamplingRate(IO_SAMPLING_RATE);
			
			// Register a listener to handle the samples received by the local device.
			localDevice.startListeningForIOSamples(new IIOSampleReceiveListener() {
				@Override
				public void ioSampleReceived(IOSample ioSample, RemoteXBeeDevice remoteDevice) {
					System.out.println("New sample received from " + remoteDevice.get64BitAddress() +
							" >> " + IOLINE_IN + ": " + ioSample.getDigitalValue(IOLINE_IN));
				}
			});
			
		} catch (XBeeException e) {
			e.printStackTrace();
			localDevice.close();
			System.exit(1);
		}
	}
}
