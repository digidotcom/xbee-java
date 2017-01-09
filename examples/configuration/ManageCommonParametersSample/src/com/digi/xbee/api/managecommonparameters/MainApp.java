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
package com.digi.xbee.api.managecommonparameters;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.PowerLevel;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.utils.HexUtils;

/**
 * XBee Java Library Manage Common parameters sample application.
 * 
 * <p>This example shows how to manage common parameters of an XBee device. As 
 * common parameters are split in cached and non-cached values, the application 
 * refresh the cached values before reading them, then it sets and reads the 
 * non-cached parameters. All the reads and configurations are performed using 
 * the specific getters and setters provided by the XBee device object.</p>
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
	
	private static final byte[] PARAM_VALUE_PAN_ID = new byte[]{0x01, 0x23};
	
	private static final XBee64BitAddress PARAM_DESTINATION_ADDR = XBee64BitAddress.BROADCAST_ADDRESS;
	
	private static final PowerLevel PARAM_POWER_LEVEL = PowerLevel.LEVEL_HIGH;
	
	/**
	 * Application main method.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		System.out.println(" +---------------------------------------------------+");
		System.out.println(" | XBee Java Library Manage Common parameters Sample |");
		System.out.println(" +---------------------------------------------------+\n");
		
		XBeeDevice myDevice = new XBeeDevice(PORT, BAUD_RATE);
		
		try {
			myDevice.open();
			
			// Read cached parameters.
			myDevice.readDeviceInfo();
			
			System.out.println("Cached parameters");
			System.out.println("----------------------");
			System.out.println(" - 64-bit address:   " + myDevice.get64BitAddress());
			System.out.println(" - 16-bit address:   " + myDevice.get16BitAddress());
			System.out.println(" - Node Identifier:  " + myDevice.getNodeID());
			System.out.println(" - Firmware version: " + myDevice.getFirmwareVersion());
			System.out.println(" - Hardware version: " + myDevice.getHardwareVersion());
			System.out.println("");
			
			// Configure and read non-cached parameters.
			myDevice.setPANID(PARAM_VALUE_PAN_ID);
			myDevice.setDestinationAddress(PARAM_DESTINATION_ADDR);
			myDevice.setPowerLevel(PARAM_POWER_LEVEL);
			
			byte[] panID = myDevice.getPANID();
			XBee64BitAddress destinationAddress = myDevice.getDestinationAddress();
			PowerLevel powerLevel = myDevice.getPowerLevel();
			
			System.out.println("Non-Cached parameters");
			System.out.println("----------------------");
			System.out.println(" - PAN ID:           " + HexUtils.byteArrayToHexString(panID));
			System.out.println(" - Destination addr: " + destinationAddress.toString());
			System.out.println(" - Power Level:      " + powerLevel.toString());
			System.out.println("");
			
			myDevice.close();
			System.exit(0);
			
		} catch (XBeeException e) {
			e.printStackTrace();
			myDevice.close();
			System.exit(1);
		}
	}
}
