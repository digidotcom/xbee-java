/*
 * Copyright 2019, Digi International Inc.
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
package com.digi.xbee.api.sendbluetoothdata;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;

/**
 * XBee Java Library Send Bluetooth Data sample application.
 *
 * <p>This example sends a message to the Bluetooth Low Energy interface of the
 * XBee device.</p>
 *
 * <p>For a complete description on the example, refer to the 'ReadMe.txt' file
 * included in the root directory.</p>
 */
public class MainApp {

	/* Constants */

	// TODO Replace with the serial port where your module is connected.
	private static final String PORT = "COM1";
	// TODO Replace with the baud rate of your module.
	private static final int BAUD_RATE = 9600;

	private static final String DATA_TO_SEND = "Hello from the serial interface (#%s)";

	/**
	 * Application main method.
	 *
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		System.out.println(" +------------------------------------------------+");
		System.out.println(" |  XBee Java Library Send Bluetooth Data Sample  |");
		System.out.println(" +------------------------------------------------+\n");

		XBeeDevice myDevice = new XBeeDevice(PORT, BAUD_RATE);

		try {
			myDevice.open();

			for (int i = 0; i < 10; i++) {
				String data = String.format(DATA_TO_SEND, i + 1);

				System.out.format("Sending data to Bluetooth interface >> '%s'... ", data);

				myDevice.sendBluetoothData(data.getBytes());

				System.out.println("Success");

				try {
					Thread.sleep(1000);
				} catch (InterruptedException ignore) {}
			}

		} catch (XBeeException e) {
			System.out.println("Error");
			e.printStackTrace();
			System.exit(1);
		} finally {
			myDevice.close();
		}
	}
}
