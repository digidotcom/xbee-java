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
package com.digi.xbee.api.receiveserialdata;

import com.digi.xbee.api.listeners.ISerialDataReceiveListener;
import com.digi.xbee.api.utils.HexUtils;

/**
 * Class to manage the XBee received data that was sent by other modules in the 
 * same network.
 * 
 * <p>Acts as a data listener by implementing the 
 * {@code ISerialDataReceiveListener} interface, and is notified when new 
 * data for the module is received.</p>
 * 
 * @see ISerialDataReceiveListener
 *
 */
public class MySerialDataReceiveListener implements ISerialDataReceiveListener {
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.listeners.ISerialDataReceiveListener#serialDataReceived(java.lang.String, byte[])
	 */
	@Override
	public void serialDataReceived(String address, byte[] data) {
		System.out.format("From %s >> %s | %s%n", address, 
				HexUtils.prettyHexString(HexUtils.byteArrayToHexString(data)), 
				new String(data));
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.listeners.ISerialDataReceiveListener#broadcastSerialDataReceived(java.lang.String, byte[])
	 */
	@Override
	public void broadcastSerialDataReceived(String address, byte[] data) {
		// This callback is executed when broadcast serial data is received from any node 
		// of the network.
	}
}
