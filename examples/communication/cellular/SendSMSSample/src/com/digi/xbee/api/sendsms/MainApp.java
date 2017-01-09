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
