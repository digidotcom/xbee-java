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
package com.digi.xbee.api.receivebluetoothfile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.relay.IBluetoothDataReceiveListener;
import com.digi.xbee.api.packet.XBeeChecksum;
import com.digi.xbee.api.utils.ByteUtils;

/**
 * Class to manage the received data from the Bluetooth interface of the XBee
 * device.
 *
 * <p>Acts as a data listener by implementing the
 * {@link IBluetoothDataReceiveListener} interface, and is notified when new
 * data from the Bluetooth interface is received.</p>
 *
 * @see IBluetoothDataReceiveListener
 */
public class MyBluetoothDataReceiveListener implements IBluetoothDataReceiveListener {

	// Constants.
	private static final String SEPARATOR = "@@@";

	private static final String MSG_START = "START" + SEPARATOR;
	private static final String MSG_END = "END";
	private static final String MSG_ACK = "OK";

	// Variables.
	private XBeeDevice device;

	private File receivedFile;
	private FileOutputStream fileOutputStream;

	/**
	 * Class constructor. Instantiates a new
	 * {@code MyBluetoothDataReceiveListener} object for the given XBee device.
	 *
	 * @param device XBee device.
	 */
	public MyBluetoothDataReceiveListener(XBeeDevice device) {
		this.device = device;
	}

	@Override
	public void dataReceived(byte[] data) {
		String dataString = new String(data);

		// Check if the data is 'START' or 'END'.
		if (dataString.startsWith(MSG_START)) {
			// Get the file name.
			String fileName = dataString.split(SEPARATOR)[1];
			try {
				// Create the file and output stream.
				receivedFile = new File(fileName);
				// If the file exists, remove it.
				if (receivedFile.exists())
					receivedFile.delete();
				receivedFile.createNewFile();
				if (fileOutputStream != null)
					fileOutputStream.close();
				fileOutputStream = new FileOutputStream(receivedFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(">> START message received, saving data to file...");
			sendAck();
		} else if (dataString.equals(MSG_END)) {
			// Close the stream.
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException ignore) {}
				fileOutputStream = null;
			}
			System.out.format(">> END message received, file '%s'\n\n", receivedFile.getAbsolutePath());
			sendAck();
		} else if (fileOutputStream != null) {
			byte[] payload = Arrays.copyOfRange(data, 0, data.length - 1);
			int checksum = ByteUtils.byteToInt(data[data.length - 1]);
			// Validate the checksum.
			XBeeChecksum chk = new XBeeChecksum();
			chk.add(payload);
			if (chk.generate() == checksum) {
				// Write block to file.
				try {
					fileOutputStream.write(payload);
				} catch (IOException e) {
					e.printStackTrace();
				}
				sendAck();
			}
		}
	}

	/**
	 * Sends an ACK to the XBee Bluetooth interface.
	 */
	private void sendAck() {
		try {
			device.sendBluetoothData(MSG_ACK.getBytes());
		} catch (XBeeException e) {
			e.printStackTrace();
		}
	}
}
