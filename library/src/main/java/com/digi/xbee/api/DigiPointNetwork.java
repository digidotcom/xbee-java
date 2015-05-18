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
package com.digi.xbee.api;

/**
 * This class represents a DigiPoint Network.
 *  
 * <p>The network allows the discovery of remote devices in the same network 
 * as the local one and stores them.</p>
 * 
 * @see DigiMeshNetwork
 * @see Raw802Network
 * @see XBeeNetwork
 * @see ZigBeeNetwork
 */
public class DigiPointNetwork extends XBeeNetwork {

	/**
	 * Instantiates a new DigiPoint Network object.
	 * 
	 * @param device Local DigiPoint device to get the network from.
	 * 
	 * @throws NullPointerException if {@code device == null}.
	 * 
	 * @see DigiPointDevice
	 */
	DigiPointNetwork(DigiPointDevice device) {
		super(device);
	}
}
