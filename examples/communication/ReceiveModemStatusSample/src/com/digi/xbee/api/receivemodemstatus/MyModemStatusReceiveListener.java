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
package com.digi.xbee.api.receivemodemstatus;

import com.digi.xbee.api.listeners.IModemStatusReceiveListener;
import com.digi.xbee.api.models.ModemStatusEvent;

/**
 * Class to manage the Modem Status events.
 * 
 * <p>Acts as a Modem Status events listener by implementing the 
 * {@code IModemStatusReceiveListener} interface, and is notified when new 
 * Modem Status events are received.</p>
 * 
 * @see IModemStatusReceiveListener
 */
public class MyModemStatusReceiveListener implements IModemStatusReceiveListener {
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.listeners.IModemStatusReceiveListener#modemStatusEventReceived(com.digi.xbee.api.models.ModemStatusEvent)
	 */
	@Override
	public void modemStatusEventReceived(ModemStatusEvent modemStatusEvent) {
		System.out.format("Modem Status event received: %s%n", modemStatusEvent.toString());
	}
}
