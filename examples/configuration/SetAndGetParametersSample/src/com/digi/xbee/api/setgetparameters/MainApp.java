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
package com.digi.xbee.api.setgetparameters;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.utils.ByteUtils;

/**
 * XBee Java Library Set and Get parameters sample application.
 * 
 * <p>This example sets and gets the value of 4 parameters with different 
 * value types. Then it reads them from the device verifying the read values 
 * are the same as the values that were set.</p>
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
	
	private static final String PARAM_NODE_ID = "NI";
	private static final String PARAM_PAN_ID = "ID";
	private static final String PARAM_DEST_ADDRESS_H = "DH";
	private static final String PARAM_DEST_ADDRESS_L = "DL";
	
	private static final String PARAM_VALUE_NODE_ID = "Yoda";
	
	private static final byte[] PARAM_VALUE_PAN_ID = new byte[]{0x12, 0x34};
	
	private static final int PARAM_VALUE_DEST_ADDRESS_H = 0x00;
	private static final int PARAM_VALUE_DEST_ADDRESS_L = 0xFFFF;
	
	/**
	 * Application main method.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		System.out.println(" +---------------------------------------------+");
		System.out.println(" | XBee Java Library Set/Get parameters Sample |");
		System.out.println(" +---------------------------------------------+\n");
		
		XBeeDevice myDevice = new XBeeDevice(PORT, BAUD_RATE);
		
		try {
			myDevice.open();
			
			// Set parameters.
			myDevice.setParameter(PARAM_NODE_ID, PARAM_VALUE_NODE_ID.getBytes());
			myDevice.setParameter(PARAM_PAN_ID, PARAM_VALUE_PAN_ID);
			myDevice.setParameter(PARAM_DEST_ADDRESS_H, ByteUtils.intToByteArray(PARAM_VALUE_DEST_ADDRESS_H));
			myDevice.setParameter(PARAM_DEST_ADDRESS_L, ByteUtils.intToByteArray(PARAM_VALUE_DEST_ADDRESS_L));
			
			// Get parameters
			byte[] paramValueNI = myDevice.getParameter(PARAM_NODE_ID);
			byte[] paramValueID = myDevice.getParameter(PARAM_PAN_ID);
			byte[] paramValueDH = myDevice.getParameter(PARAM_DEST_ADDRESS_H);
			byte[] paramValueDL = myDevice.getParameter(PARAM_DEST_ADDRESS_L);
			
			// Compare the read parameter values with the values that were set.
			if (!new String(paramValueNI).equals(PARAM_VALUE_NODE_ID)) {
				System.out.println("NI parameter was not set correctly.");
				myDevice.close();
				System.exit(1);
			}
			if (ByteUtils.byteArrayToLong(paramValueID) != ByteUtils.byteArrayToLong(PARAM_VALUE_PAN_ID)) {
				System.out.println("ID parameter was not set correctly.");
				myDevice.close();
				System.exit(1);
			}
			if (ByteUtils.byteArrayToInt(paramValueDH) != PARAM_VALUE_DEST_ADDRESS_H) {
				System.out.println("DH parameter was not set correctly.");
				myDevice.close();
				System.exit(1);
			}
			if (ByteUtils.byteArrayToInt(paramValueDL) != PARAM_VALUE_DEST_ADDRESS_L) {
				System.out.println("DL parameter was not set correctly.");
				myDevice.close();
				System.exit(1);
			}
			
			System.out.println("All parameters were set correctly!");
			
			myDevice.close();
			System.exit(0);
			
		} catch (XBeeException e) {
			e.printStackTrace();
			myDevice.close();
			System.exit(1);
		}
	}
}
