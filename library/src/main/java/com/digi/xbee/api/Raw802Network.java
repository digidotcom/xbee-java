/**
 * Copyright (c) 2014 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */
package com.digi.xbee.api;

/**
 * An object that represents a 802.15.4 Network.
 *  
 * <p>The network allows the discovery of remote devices in the same network 
 * as the local one and stores them.</p>
 * 
 * @see XBeeNetwork
 * @see ZigBeeNetwork
 * @see DigiMeshNetwork
 * @see DigiPointNetwork
 */
public class Raw802Network extends XBeeNetwork {

	/**
	 * Instantiates a new 802.15.4 Network object.
	 * 
	 * @param device Local 802.15.4 device to get the network from.
	 * 
	 * @throws NullPointerException If {@code device == null}.
	 * 
	 * @see Raw802Device
	 */
	Raw802Network(Raw802Device device) {
		super(device);
	}
}
