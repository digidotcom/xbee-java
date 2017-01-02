/**
 * Copyright (c) 2014-2017 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */
package com.digi.xbee.api.receiveexplicitdata;

import com.digi.xbee.api.listeners.IExplicitDataReceiveListener;
import com.digi.xbee.api.models.ExplicitXBeeMessage;
import com.digi.xbee.api.utils.HexUtils;

/**
 * Class to manage the XBee received data in explicit format that was sent by 
 * other modules in the same network.
 * 
 * <p>Acts as an explicit data listener by implementing the 
 * {@link IExplicitDataReceiveListener} interface, and is notified when new 
 * data in explicit format for the module is received.</p>
 * 
 * @see IExplicitDataReceiveListener
 *
 */
public class MyExplicitDataReceiveListener implements IExplicitDataReceiveListener {
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.listeners.IExplicitDataReceiveListener#explicitDataReceived(com.digi.xbee.api.models.ExplicitXBeeMessage)
	 */
	@Override
	public void explicitDataReceived(ExplicitXBeeMessage explicitXBeeMessage) {
		System.out.format("From %s >> %s | %s%n", explicitXBeeMessage.getDevice().get64BitAddress(), 
				HexUtils.prettyHexString(HexUtils.byteArrayToHexString(explicitXBeeMessage.getData())), 
				new String(explicitXBeeMessage.getData()));
		System.out.format(" - Source endpoint: 0x%02X%n", explicitXBeeMessage.getSourceEndpoint());
		System.out.format(" - Destination endpoint: 0x%02X%n", explicitXBeeMessage.getDestinationEndpoint());
		System.out.format(" - Cluster ID: 0x%04X%n", explicitXBeeMessage.getClusterID());
		System.out.format(" - Profile ID: 0x%04X%n%n", explicitXBeeMessage.getProfileID());
	}
}
