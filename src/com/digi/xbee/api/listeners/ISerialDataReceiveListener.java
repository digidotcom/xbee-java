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
package com.digi.xbee.api.listeners;

/**
 * This interface defines the required methods that should be
 * implemented to behave as a serial data listener and be notified
 * when new serial data is received from an XBee device of the network.
 */
public interface ISerialDataReceiveListener {

	/**
	 * Called when serial data is received from a remote node.
	 * 
	 * @param address The address of the remote node that sent the data.
	 * @param data The received data.
	 */
	public void serialDataReceived(String address, byte[] data);
	
	/**
	 * Called when serial data is received from a remote node via broadcast.
	 * 
	 * @param address The address of the remote node that sent the broadcast data.
	 * @param data The received data.
	 */
	public void broadcastSerialDataReceived(String address, byte[] data);
}
