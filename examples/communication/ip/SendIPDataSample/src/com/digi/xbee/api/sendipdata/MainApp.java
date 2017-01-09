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
package com.digi.xbee.api.sendipdata;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import com.digi.xbee.api.WiFiDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.IPProtocol;
import com.digi.xbee.api.utils.HexUtils;

/**
 * XBee Java Library Send IP Data sample application.
 *
 * <p>This example sends IP data to another Wi-Fi device specified by its IP
 * address and port number.</p>
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
	// TODO Replace with the destination IP address.
	private static final String DEST_IP_ADDRESS = "192.168.1.2";
	// TODO Replace with the destination port number (in decimal format).
	private static final int DEST_PORT = 9750;

	private static final IPProtocol PROTOCOL = IPProtocol.TCP;

	private static final String DATA_TO_SEND = "Hello XBee!";

	/**
	 * Application main method.
	 *
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		System.out.println(" +-----------------------------------------+");
		System.out.println(" |  XBee Java Library Send IP Data Sample  |");
		System.out.println(" +-----------------------------------------+\n");

		// For XBee Cellular modules, use the CellularDevice class instead.
		WiFiDevice myDevice = new WiFiDevice(PORT, BAUD_RATE);
		byte[] dataToSend = DATA_TO_SEND.getBytes();

		try {
			myDevice.open();

			if (!myDevice.isConnected()) {
				System.err.println(">> Error: the device is not connected to the network");
				return;
			}

			System.out.format("Sending data to %s:%d >> %s | %s... ", DEST_IP_ADDRESS, DEST_PORT,
					HexUtils.prettyHexString(HexUtils.byteArrayToHexString(dataToSend)),
					new String(dataToSend));

			myDevice.sendIPData((Inet4Address) Inet4Address.getByName(DEST_IP_ADDRESS),
					DEST_PORT, PROTOCOL, dataToSend);

			System.out.println("Success");

		} catch (XBeeException | UnknownHostException e) {
			System.out.println("Error");
			e.printStackTrace();
			System.exit(1);
		} finally {
			myDevice.close();
		}
	}
}
