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

import com.digi.xbee.api.models.IPMessage;

/**
 * This interface defines the required methods that should be implemented to 
 * behave as an IP data listener and be notified when new IP data is 
 * received.
 * 
 * @since 1.2.0
 */
public interface IIPDataReceiveListener {

	/**
	 * Called when IP data is received.
	 * 
	 * @param ipMessage An {@code IPMessage} object containing the data, the 
	 *                  IP address that sent the data, the source and 
	 *                  destination ports and the {@code IPProtocol} of the 
	 *                  transmission.
	 * 
	 * @see com.digi.xbee.api.models.IPMessage
	 */
	public void ipDataReceived(IPMessage ipMessage);
}
