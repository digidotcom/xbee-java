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
