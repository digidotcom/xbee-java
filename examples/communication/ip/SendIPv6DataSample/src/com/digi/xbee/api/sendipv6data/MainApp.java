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
package com.digi.xbee.api.sendipv6data;

import java.net.Inet6Address;
import java.net.UnknownHostException;

import com.digi.xbee.api.ThreadDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.IPProtocol;
import com.digi.xbee.api.utils.HexUtils;

/**
 * XBee Java Library Send IPv6 Data sample application.
 *
 * <p>This example sends IPv6 data to another Thread device specified by its 
 * IPv6 address and port number.</p>
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
	// TODO Replace with the destination IPv6 address (MY, LA or GA command).
	private static final String DEST_IPV6_ADDRESS = "FDB3:0001:0002:0000:0004:0005:0006:0007";
	// TODO Replace with the destination port number (in decimal format).
	private static final int DEST_PORT = 9750;

	private static final IPProtocol PROTOCOL = IPProtocol.UDP;

	private static final String DATA_TO_SEND = "Hello XBee!";

	/**
	 * Application main method.
	 *
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		System.out.println(" +-------------------------------------------+");
		System.out.println(" |  XBee Java Library Send IPv6 Data Sample  |");
		System.out.println(" +-------------------------------------------+\n");

		ThreadDevice myDevice = new ThreadDevice(PORT, BAUD_RATE);
		byte[] dataToSend = DATA_TO_SEND.getBytes();

		try {
			myDevice.open();

			if (!myDevice.isConnected()) {
				System.err.println(">> Error: the device is not connected to the network");
				return;
			}

			System.out.format("Sending data to %s:%d >> %s | %s... ", DEST_IPV6_ADDRESS, DEST_PORT,
					HexUtils.prettyHexString(HexUtils.byteArrayToHexString(dataToSend)),
					new String(dataToSend));

			myDevice.sendIPData((Inet6Address) Inet6Address.getByName(DEST_IPV6_ADDRESS),
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
