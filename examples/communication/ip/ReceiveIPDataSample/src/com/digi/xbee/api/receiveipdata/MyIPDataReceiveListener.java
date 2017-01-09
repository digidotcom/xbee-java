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
package com.digi.xbee.api.receiveipdata;

import com.digi.xbee.api.listeners.IIPDataReceiveListener;
import com.digi.xbee.api.models.IPMessage;
import com.digi.xbee.api.utils.HexUtils;

/**
 * Class to manage the IP received data that was sent by other modules.
 *
 * <p>Acts as an IP data listener by implementing the
 * {@link IIPDataReceiveListener} interface, and is notified when new IP data
 * for the module is received.</p>
 *
 * @see IIPDataReceiveListener
 *
 */
public class MyIPDataReceiveListener implements IIPDataReceiveListener {
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.listeners.IIPDataReceiveListener#ipDataReceived(com.digi.xbee.api.models.IPMessage)
	 */
	@Override
	public void ipDataReceived(IPMessage ipMessage) {
		System.out.format("From %s >> %s | %s%n", ipMessage.getIPAddress().getHostAddress(),
				HexUtils.prettyHexString(HexUtils.byteArrayToHexString(ipMessage.getData())),
				ipMessage.getDataString());
	}
}
