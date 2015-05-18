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

import com.digi.xbee.api.RemoteXBeeDevice;

/**
 * Interface defining the required methods that an object should implement to be 
 * notified about device discovery events. 
 */
public interface IDiscoveryListener {

	/**
	 * Notifies that a remote device was discovered in the network.
	 * 
	 * @param discoveredDevice The discovered remote device.
	 * 
	 * @see com.digi.xbee.api.RemoteXBeeDevice
	 */
	public void deviceDiscovered(RemoteXBeeDevice discoveredDevice);
	
	/**
	 * Notifies that an error occurred during the discovery process.
	 * 
	 * <p>This method is only called when an error occurs but does not cause 
	 * the process to finish.</p>
	 * 
	 * @param error The error message.
	 */
	public void discoveryError(String error);
	
	/**
	 * Notifies that the discovery process has finished.
	 * 
	 * @param error The error message, or {@code null} if the process finished 
	 *              successfully.
	 */
	public void discoveryFinished(String error);
}
