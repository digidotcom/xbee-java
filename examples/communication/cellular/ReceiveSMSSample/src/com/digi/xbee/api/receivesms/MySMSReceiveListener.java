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
package com.digi.xbee.api.receivesms;

import com.digi.xbee.api.listeners.ISMSReceiveListener;
import com.digi.xbee.api.models.SMSMessage;

/**
 * Class to manage the SMS received from a phone or other Cellular device.
 * 
 * <p>Acts as an SMS listener by implementing the {@link ISMSReceiveListener} 
 * interface, and is notified when new SMS for the module is received.</p>
 * 
 * @see ISMSReceiveListener
 */
public class MySMSReceiveListener implements ISMSReceiveListener {
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.listeners.ISMSReceiveListener#smsReceived(com.digi.xbee.api.models.SMSMessage)
	 */
	@Override
	public void smsReceived(SMSMessage smsMessage) {
		System.out.format("Received SMS from %s >> '%s'", smsMessage.getPhoneNumber(), 
				smsMessage.getData());
	}
}
