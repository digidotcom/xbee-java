/**
 * Copyright (c) 2016-2017 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
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
