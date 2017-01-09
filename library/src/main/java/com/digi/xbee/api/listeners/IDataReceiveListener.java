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
package com.digi.xbee.api.listeners;

import com.digi.xbee.api.models.XBeeMessage;

/**
 * This interface defines the required methods that should be implemented to 
 * behave as a data listener and be notified when new data is received from a 
 * remote XBee device of the network.
 */
public interface IDataReceiveListener {

	/**
	 * Called when data is received from a remote node of the network.
	 * 
	 * @param xbeeMessage An {@code XBeeMessage} object containing the data,
	 *                    the {@code RemoteXBeeDevice} that sent the data and 
	 *                    a flag indicating whether the data was sent via 
	 *                    broadcast or not.
	 * 
	 * @see com.digi.xbee.api.models.XBeeMessage
	 * @see com.digi.xbee.api.RemoteXBeeDevice
	 */
	public void dataReceived(XBeeMessage xbeeMessage);
}
