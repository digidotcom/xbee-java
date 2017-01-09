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
package com.digi.xbee.api.connecttoaccesspoint;

import com.digi.xbee.api.WiFiDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IModemStatusReceiveListener;
import com.digi.xbee.api.models.IPAddressingMode;
import com.digi.xbee.api.models.ModemStatusEvent;

/**
 * XBee Java Library Connect to Access Point sample application.
 *
 * <p>This example configures the Wi-Fi module to connect to a specific access
 * point and reads its addressing settings.</p>
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
	// TODO Fill with the SSID of the access point you want to connect to.
	private static final String SSID = "";
	// TODO Fill with the password of the access point you want to connect to.
	private static final String PASSWORD = "";

	/**
	 * Application main method.
	 *
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		System.out.println(" +----------------------------------------------------+");
		System.out.println(" |  XBee Java Library Connect to Access Point Sample  |");
		System.out.println(" +----------------------------------------------------+\n");

		WiFiDevice myDevice = new WiFiDevice(PORT, BAUD_RATE);

		try {
			myDevice.open();

			// Before connecting to an access point, the module must be disconnected.
			if (myDevice.isConnected())
				myDevice.disconnect();

			myDevice.setIPAddressingMode(IPAddressingMode.DHCP);

			myDevice.addModemStatusListener(new IModemStatusReceiveListener() {
				@Override
				public void modemStatusEventReceived(ModemStatusEvent modemStatusEvent) {
					switch (modemStatusEvent) {
					case STATUS_JOINED_NETWORK:
						System.out.println(">> Connected to the access point");
						break;
					case STATUS_DISASSOCIATED:
						System.out.println(">> Disconnected from the access point");
						break;
					default:
						break;
					}
				}
			});

			if (!myDevice.connect(SSID, PASSWORD)) {
				System.err.format(">> Error: could not connect to '%s'\n", SSID);
				return;
			}

			System.out.format("\n  - IP addressing mode: %s\n", myDevice.getIPAddressingMode());
			System.out.format("  - IP address: %s\n", myDevice.getIPAddress().getHostAddress());
			System.out.format("  - IP address mask: %s\n", myDevice.getIPAddressMask().getHostAddress());
			System.out.format("  - Gateway IP address: %s\n", myDevice.getGatewayIPAddress().getHostAddress());
			System.out.format("  - DNS address: %s\n\n", myDevice.getDNSAddress().getHostAddress());

		} catch (XBeeException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
