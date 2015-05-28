/**
 * Copyright (c) 2014-2015 Digi International Inc.,
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

import com.digi.xbee.api.models.ModemStatusEvent;

/**
 * This interface defines the required methods that an object should implement
 * to behave as a modem status listener and be notified when modem status events 
 * are received from the radio.
 */
public interface IModemStatusReceiveListener {

	/**
	 * Called when a modem status event from the radio is received.
	 * 
	 * @param modemStatusEvent The modem status event that was received.
	 * 
	 * @see com.digi.xbee.api.models.ModemStatusEvent
	 */
	public void modemStatusEventReceived(ModemStatusEvent modemStatusEvent);
}
