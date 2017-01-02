/**
 * Copyright (c) 2016-2017 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */
package com.digi.xbee.api.receiveipdata;

import com.digi.xbee.api.listeners.IIPDataReceiveListener;
import com.digi.xbee.api.models.IPMessage;
import com.digi.xbee.api.utils.HexUtils;

/**
 * Class to manage the IP received data that was sent by other modules.
 *
 * <p>Acts as an IP data listener by implementing the
 * {@link IIPDataReceiveListener} interface, and is notified when new IP data
 * for the module is received.</p>
 *
 * @see IIPDataReceiveListener
 *
 */
public class MyIPDataReceiveListener implements IIPDataReceiveListener {
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.listeners.IIPDataReceiveListener#ipDataReceived(com.digi.xbee.api.models.IPMessage)
	 */
	@Override
	public void ipDataReceived(IPMessage ipMessage) {
		System.out.format("From %s >> %s | %s%n", ipMessage.getIPAddress().getHostAddress(),
				HexUtils.prettyHexString(HexUtils.byteArrayToHexString(ipMessage.getData())),
				ipMessage.getDataString());
	}
}
