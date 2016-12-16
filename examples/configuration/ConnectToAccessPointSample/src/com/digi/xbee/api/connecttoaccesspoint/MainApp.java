/**
 * Copyright (c) 2016 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */
package com.digi.xbee.api.connecttoaccesspoint;

import com.digi.xbee.api.WiFiDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.AccessPoint;
import com.digi.xbee.api.models.IPAddressingMode;

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

			myDevice.disconnect();

			myDevice.setIPAddressingMode(IPAddressingMode.DHCP);

			AccessPoint accessPoint = myDevice.getAccessPoint(SSID);

			if (accessPoint == null) {
				System.err.format(">> Error: could not find any access point with SSID '%s'\n", SSID);
				return;
			}

			if (myDevice.connect(accessPoint, PASSWORD))
				System.out.format(">> Successfully connected to '%s'\n\n", SSID);
			else {
				System.err.format(">> Error: could not connect to '%s'\n", SSID);
				return;
			}

			System.out.format("  - IP addressing mode: %s\n", myDevice.getIPAddressingMode());
			System.out.format("  - IP address: %s\n", myDevice.getIPAddress().getHostAddress());
			System.out.format("  - IP address mask: %s\n", myDevice.getIPAddressMask().getHostAddress());
			System.out.format("  - Gateway IP address: %s\n", myDevice.getGatewayIPAddress().getHostAddress());
			System.out.format("  - DNS address: %s\n\n", myDevice.getDNSAddress().getHostAddress());

		} catch (XBeeException e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			myDevice.close();
		}
	}

}
