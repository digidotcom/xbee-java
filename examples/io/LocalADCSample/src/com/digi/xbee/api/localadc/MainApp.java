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
package com.digi.xbee.api.localadc;

import java.util.Timer;
import java.util.TimerTask;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOMode;

/**
 * XBee Java Library Read Local ADC sample application.
 * 
 * <p>This example reads the potentiometer value and prints it in the output
 * console every 250ms.</p>
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
	private static final int READ_TIMEOUT = 250;
	
	private static final IOLine IOLINE_IN = IOLine.DIO1_AD1;
	
	/**
	 * Application main method.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		System.out.println(" +-----------------------------------------+");
		System.out.println(" | XBee Java Library Read Local ADC Sample |");
		System.out.println(" +-----------------------------------------+\n");
		
		final XBeeDevice myDevice = new XBeeDevice(PORT, BAUD_RATE);
		
		Timer readADCTimer = new Timer();
		
		try {
			myDevice.open();
			
			myDevice.setIOConfiguration(IOLINE_IN, IOMode.ADC);
			
			readADCTimer.schedule(new ReadADCTask(myDevice), 0, READ_TIMEOUT);
			
		} catch (XBeeException e) {
			e.printStackTrace();
			myDevice.close();
			System.exit(1);
		}
	}
	
	/**
	 * Read ADC task to be performed every {@value #READ_TIMEOUT} ms.
	 *
	 * <p>The task will read the ADC value of {@value #IOLINE_IN} and print its 
	 * value to the standard output.</p>
	 * 
	 * @see TimerTask
	 */
	private static class ReadADCTask extends TimerTask {
		private XBeeDevice xbeeDevice;
		
		public ReadADCTask(XBeeDevice xbeeDevice) {
			this.xbeeDevice = xbeeDevice;
		}
		
		@Override
		public void run() {
			try {
				// Read the analog value from the input line.
				int value = xbeeDevice.getADCValue(IOLINE_IN);
				System.out.println(IOLINE_IN + ": " + value);
				
			} catch (XBeeException e) {
				e.printStackTrace();
			}
		}
	}
}
