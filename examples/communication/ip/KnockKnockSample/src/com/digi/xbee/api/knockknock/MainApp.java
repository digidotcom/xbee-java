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
package com.digi.xbee.api.knockknock;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Scanner;

import com.digi.xbee.api.WiFiDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IIPDataReceiveListener;
import com.digi.xbee.api.models.IPMessage;
import com.digi.xbee.api.models.IPProtocol;

/**
 * XBee Java Library Knock Knock sample application.
 *
 * <p>This example starts a simple web server and connects to it by sending a
 * message to start a Knock Knock joke.</p>
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

	private static final int SERVER_PORT = 9750;

	/**
	 * Application main method.
	 *
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		System.out.println(" +----------------------------------------+");
		System.out.println(" |  XBee Java Library Knock Knock Sample  |");
		System.out.println(" +----------------------------------------+\n");

		WebServer.start(SERVER_PORT);

		Scanner scanner = new Scanner(System.in);

		WiFiDevice myDevice = new WiFiDevice(PORT, BAUD_RATE);

		try {
			myDevice.open();

			myDevice.addIPDataListener(new IIPDataReceiveListener() {
				@Override
				public void ipDataReceived(IPMessage ipMessage) {
					System.out.println(ipMessage.getDataString());
				}
			});

			myDevice.sendIPData((Inet4Address) Inet4Address.getLocalHost(),
					SERVER_PORT, IPProtocol.TCP, ("\n").getBytes());

			String line;
			while (!(line = scanner.nextLine()).equalsIgnoreCase("bye.")) {
				myDevice.sendIPData((Inet4Address) Inet4Address.getLocalHost(),
						SERVER_PORT, IPProtocol.TCP, (line + "\n").getBytes());
			}

		} catch (XBeeException | UnknownHostException e) {
			System.out.println("Error sending data to the web server");
			e.printStackTrace();
			myDevice.close();
			System.exit(1);
		} finally {
			scanner.close();
			myDevice.close();
			System.exit(0);
		}
	}
}
