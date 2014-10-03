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
import com.digi.xbee.api.models.XBeeMessage;
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
	 * @see com.digi.xbee.api.listeners.ISerialDataReceiveListener#serialDataReceived(com.digi.xbee.api.models.XBeeMessage)
	 */
	@Override
	public void serialDataReceived(XBeeMessage xbeeMessage) {
		System.out.format("From %s >> %s | %s%n", xbeeMessage.getDevice().get64BitAddress(), 
				HexUtils.prettyHexString(HexUtils.byteArrayToHexString(xbeeMessage.getData())), 
				new String(xbeeMessage.getData()));
	}
}
