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
package com.digi.xbee.api.connection.android;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;

public class AndroidUSBOutputStreamTest {

	// Constants.
	private final static String VARIABLE_WRITE_TIMEOUT = "WRITE_TIMEOUT";

	// Variables.
	private UsbEndpoint sendEndPoint;
	private UsbDeviceConnection usbConnection;

	private AndroidUSBOutputStream os;

	private int timeout;

	@Before
	public void setup() throws Exception {
		sendEndPoint = Mockito.mock(UsbEndpoint.class);
		usbConnection = Mockito.mock(UsbDeviceConnection.class);

		os = PowerMockito.spy(new AndroidUSBOutputStream(sendEndPoint, usbConnection));
		timeout = (Integer) Whitebox.getInternalState(os, VARIABLE_WRITE_TIMEOUT);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.AndroidUSBOutputStream#write(int)}
	 *
	 * <p>Verify that one byte is written successfully.</p>
	 */
	@Test
	public void testWriteOneByte() throws Exception {
		// Prepare the variables.
		int myByte = 0x40;

		// Call the method under test.
		os.write(myByte);

		Thread.sleep(20);

		// Perform the verification.
		Mockito.verify(usbConnection, Mockito.times(1)).bulkTransfer(sendEndPoint, new byte[]{(byte) myByte}, 1, timeout);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.AndroidUSBOutputStream#write(byte[])}
	 *
	 * <p>Verify that a byte array is written successfully.</p>
	 */
	@Test
	public void testWriteByteArray() throws Exception {
		// Prepare the variables.
		byte[] array = new byte[]{0x20, 0x1A};

		// Call the method under test.
		os.write(array);

		Thread.sleep(20);

		// Perform the verification.
		Mockito.verify(usbConnection, Mockito.times(1)).bulkTransfer(sendEndPoint, array, array.length, timeout);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.AndroidUSBOutputStream#write(byte[], int, int)}
	 *
	 * <p>Verify that a byte array is written successfully.</p>
	 */
	@Test
	public void testWriteByteArrayOffset() throws Exception {
		// Prepare the variables.
		byte[] array = new byte[]{0x20, 0x1A};

		// Call the method under test.
		os.write(array, 0, array.length);

		Thread.sleep(20);

		// Perform the verification.
		Mockito.verify(usbConnection, Mockito.times(1)).bulkTransfer(sendEndPoint, array, array.length, timeout);
	}
}
