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
import com.digi.xbee.api.io.IOSample;

/**
 * This interface defines the required methods that an object should implement
 * to behave as an IO Sample listener and be notified when IO samples are 
 * received from a remote XBee device of the network.
 */
public interface IIOSampleReceiveListener {
	
	/**
	 * Called when an IO sample is received through the connection interface.
	 * 
	 * @param remoteDevice The remote XBee device that sent the sample.
	 * @param ioSample The received IO sample.
	 * 
	 * @see com.digi.xbee.api.RemoteXBeeDevice
	 * @see com.digi.xbee.api.io.IOSample
	 */
	public void ioSampleReceived(RemoteXBeeDevice remoteDevice, IOSample ioSample);
}
