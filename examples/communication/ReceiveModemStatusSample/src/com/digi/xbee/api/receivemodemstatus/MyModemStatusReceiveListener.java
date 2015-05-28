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
