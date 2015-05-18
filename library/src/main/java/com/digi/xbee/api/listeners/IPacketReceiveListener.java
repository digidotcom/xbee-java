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

import com.digi.xbee.api.packet.XBeePacket;

/**
 * This interface defines the required methods that an object should implement
 * to behave as a packet listener and be notified when new packets are received 
 * from a remote XBee device of the network.
 */
public interface IPacketReceiveListener {

	/**
	 * Called when an XBee packet is received through the connection interface.
	 * 
	 * @param receivedPacket The received XBee packet.
	 * 
	 * @see com.digi.xbee.api.packet.XBeePacket
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket
	 */
	public void packetReceived(XBeePacket receivedPacket);
}
