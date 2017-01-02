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
