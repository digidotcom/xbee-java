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
package com.digi.xbee.api.listeners;

import com.digi.xbee.api.models.SMSMessage;

/**
 * This interface defines the required methods that should be implemented to 
 * behave as an SMS listener and be notified when new SMS is received.
 * 
 * @since 1.2.0
 */
public interface ISMSReceiveListener {

	/**
	 * Called when SMS is received.
	 * 
	 * @param smsMessage An {@code SMSMessage} object containing the SMS text 
	 *                   and the phone number that sent the message.
	 * 
	 * @see com.digi.xbee.api.models.SMSMessage
	 */
	public void smsReceived(SMSMessage smsMessage);
}
