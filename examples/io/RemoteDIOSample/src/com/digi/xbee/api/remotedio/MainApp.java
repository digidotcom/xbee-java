/**
 * Copyright 2017, Digi International Inc.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES 
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR 
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES 
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN 
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF 
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package com.digi.xbee.api.remotedio;

import java.util.Timer;
import java.util.TimerTask;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOMode;
import com.digi.xbee.api.io.IOValue;

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
	
	private static final String REMOTE_NODE_IDENTIFIER = "REMOTE";
	
	private static final IOLine IOLINE_IN = IOLine.DIO3_AD3;
	private static final IOLine IOLINE_OUT = IOLine.DIO4_AD4;
	
	/**
	 * Application main method.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		System.out.println(" +---------------------------------------------+");
		System.out.println(" | XBee Java Library Get/Set Remote DIO Sample |");
		System.out.println(" +---------------------------------------------+\n");
		
		XBeeDevice localDevice = new XBeeDevice(PORT, BAUD_RATE);
		
		Timer readDIOTimer = new Timer();
		
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
			
			remoteDevice.setIOConfiguration(IOLINE_IN, IOMode.DIGITAL_IN);
			
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
				System.out.println(IOLINE_IN + ": " + value);
				
				// Set the previous value to the local output line.
				localDevice.setDIOValue(IOLINE_OUT, value);
			} catch (XBeeException e) {
				e.printStackTrace();
			}
		}
	}
}
