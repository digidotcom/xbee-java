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
package com.digi.xbee.api.receivemicropythondata;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;

/**
 * XBee Java Library Receive MicroPython Data sample application.
 *
 * <p>This example registers a listener to be notified when new data from the
 * MicroPython interface of the XBee device is received.</p>
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

	/**
	 * Application main method.
	 *
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		System.out.println(" +-----------------------------------------------------+");
		System.out.println(" |  XBee Java Library Receive MicroPython Data Sample  |");
		System.out.println(" +-----------------------------------------------------+\n");

		XBeeDevice myDevice = new XBeeDevice(PORT, BAUD_RATE);

		try {
			myDevice.open();

			myDevice.addMicroPythonDataListener(new MyMicroPythonDataReceiveListener());

			System.out.println("\n>> Waiting for data from the MicroPython interface...");

		} catch (XBeeException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
