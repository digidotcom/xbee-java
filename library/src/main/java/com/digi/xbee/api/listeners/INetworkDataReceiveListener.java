/**
 * Copyright (c) 2016 Digi International Inc.,
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

import com.digi.xbee.api.models.NetworkMessage;

/**
 * This interface defines the required methods that should be implemented to 
 * behave as a network data listener and be notified when new network data is 
 * received.
 */
public interface INetworkDataReceiveListener {

	/**
	 * Called when network data is received.
	 * 
	 * @param networkMessage An {@code NetworkMessage} object containing the 
	 *                       data, the {@code IP32BitAddress} that sent the 
	 *                       data and a flag indicating whether the data was 
	 *                       sent via broadcast or not.
	 * 
	 * @see com.digi.xbee.api.models.IP32BitAddress
	 * @see com.digi.xbee.api.models.NetworkMessage
	 */
	public void networkDataReceived(NetworkMessage networkMessage);
}
