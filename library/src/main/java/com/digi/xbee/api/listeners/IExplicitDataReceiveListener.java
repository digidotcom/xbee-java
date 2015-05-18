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

import com.digi.xbee.api.models.ExplicitXBeeMessage;

/**
 * This interface defines the required methods that should be implemented to 
 * behave as an explicit data listener and be notified when new explicit data 
 * is received from a remote XBee device of the network.
 */
public interface IExplicitDataReceiveListener {

	/**
	 * Called when explicit data is received from a remote node of the network.
	 * 
	 * @param explicitXBeeMessage An {@code ExplicitXBeeMessage} object 
	 *                            containing the data, the 
	 *                            {@code RemoteXBeeDevice} that sent the data, 
	 *                            a flag indicating whether the data was 
	 *                            sent via broadcast or not and the application 
	 *                            layer fields (source endpoint, destination 
	 *                            endpoint, cluster ID and profile ID).
	 * 
	 * @see com.digi.xbee.api.models.ExplicitXBeeMessage
	 * @see com.digi.xbee.api.RemoteXBeeDevice
	 */
	public void explicitDataReceived(ExplicitXBeeMessage explicitXBeeMessage);
}
