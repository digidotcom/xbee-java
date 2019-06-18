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
package com.digi.xbee.api.receivemicropythondata;

import com.digi.xbee.api.listeners.relay.IMicroPythonDataReceiveListener;
import com.digi.xbee.api.utils.HexUtils;

/**
 * Class to manage the received data from the MicroPython interface of the XBee
 * device.
 *
 * <p>Acts as a data listener by implementing the
 * {@link IMicroPythonDataReceiveListener} interface, and is notified when new
 * data from the MicroPython interface is received.</p>
 *
 * @see IMicroPythonDataReceiveListener
 *
 */
public class MyMicroPythonDataReceiveListener implements IMicroPythonDataReceiveListener {

	@Override
	public void dataReceived(byte[] data) {
		System.out.format("Data received from MicroPython >> %s | %s%n",
				HexUtils.prettyHexString(HexUtils.byteArrayToHexString(data)),
				new String(data));
	}
}
