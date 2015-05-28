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
package com.digi.xbee.api.discoverdevices;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.listeners.IDiscoveryListener;

/**
 * Class to manage the XBee network discovery events.
 * 
 * <p>Acts as a discovery listener by implementing the 
 * {@code IDiscoveryListener} interface, and is notified when new 
 * devices are found, an error occurs or the discovery process 
 * finishes.</p>
 * 
 * @see IDiscoveryListener
 *
 */
public class MyDiscoveryListener implements IDiscoveryListener {
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.listeners.IDiscoveryListener#deviceDiscovered(com.digi.xbee.api.RemoteXBeeDevice)
	 */
	@Override
	public void deviceDiscovered(RemoteXBeeDevice discoveredDevice) {
		System.out.format(">> Device discovered: %s%n", discoveredDevice.toString());
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.listeners.IDiscoveryListener#discoveryError(java.lang.String)
	 */
	@Override
	public void discoveryError(String error) {
		System.out.println(">> There was an error discovering devices: " + error);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.listeners.IDiscoveryListener#discoveryFinished(java.lang.String)
	 */
	@Override
	public void discoveryFinished(String error) {
		if (error == null)
			System.out.println(">> Discovery process finished successfully.");
		else
			System.out.println(">> Discovery process finished due to the following error: " + error);
		
		System.exit(0);
	}
}
