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
package com.digi.xbee.api.iosampling;

import java.util.EnumSet;
import java.util.Set;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOMode;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.listeners.IIOSampleReceiveListener;

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
	
	private static final String REMOTE_NODE_IDENTIFIER = "REMOTE";
	
	private static final IOLine DIGITAL_LINE = IOLine.DIO3_AD3;
	private static final IOLine ANALOG_LINE = IOLine.DIO2_AD2;
	
	private static final Set<IOLine> MONITORED_LINES = EnumSet.of(DIGITAL_LINE);
	
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
			
			// Obtain the remote XBee device from the XBee network.
			XBeeNetwork xbeeNetwork = localDevice.getNetwork();
			RemoteXBeeDevice remoteDevice = xbeeNetwork.discoverDevice(REMOTE_NODE_IDENTIFIER);
			if (remoteDevice == null) {
				System.out.println("Couldn't find the remote XBee device with '" + REMOTE_NODE_IDENTIFIER + "' Node Identifier.");
				localDevice.close();
				System.exit(1);
			}
			
			// Set the local device as destination address of the remote.
			remoteDevice.setDestinationAddress(localDevice.get64BitAddress());
			
			remoteDevice.setIOConfiguration(DIGITAL_LINE, IOMode.DIGITAL_IN);
			remoteDevice.setIOConfiguration(ANALOG_LINE, IOMode.ADC);
			
			// Enable DIO change detection in the remote device.
			remoteDevice.setDIOChangeDetection(MONITORED_LINES);
			
			// Enable periodic sampling every IO_SAMPLING_RATE milliseconds in the remote device.
			remoteDevice.setIOSamplingRate(IO_SAMPLING_RATE);
			
			// Register a listener to handle the samples received by the local device.
			localDevice.addIOSampleListener(new IIOSampleReceiveListener() {
				@Override
				public void ioSampleReceived(RemoteXBeeDevice remoteDevice, IOSample ioSample) {
					System.out.println("New sample received from " + remoteDevice.get64BitAddress() +
							" - " + ioSample);
				}
			});
			
		} catch (XBeeException e) {
			e.printStackTrace();
			localDevice.close();
			System.exit(1);
		}
	}
}
