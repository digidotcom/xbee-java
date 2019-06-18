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
package com.digi.xbee.api.receivebluetoothdata;

import com.digi.xbee.api.listeners.relay.IBluetoothDataReceiveListener;
import com.digi.xbee.api.utils.HexUtils;

/**
 * Class to manage the received data from the Bluetooth interface of the XBee
 * device.
 *
 * <p>Acts as a data listener by implementing the
 * {@link IBluetoothDataReceiveListener} interface, and is notified when new
 * data from the Bluetooth interface is received.</p>
 *
 * @see IBluetoothDataReceiveListener
 *
 */
public class MyBluetoothDataReceiveListener implements IBluetoothDataReceiveListener {

	@Override
	public void dataReceived(byte[] data) {
		System.out.format("Data received from Bluetooth >> %s | %s%n",
				HexUtils.prettyHexString(HexUtils.byteArrayToHexString(data)),
				new String(data));
	}
}
