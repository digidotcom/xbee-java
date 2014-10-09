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

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.io.IOSample;

/**
 * This interface defines the required methods that should be implemented to 
 * behave as a IO Sample listener and be notified when IO samples are received 
 * from an XBee device of the network.
 */
public interface IIOSampleReceiveListener {
	
	/**
	 * Called when an IO sample is received through the connection interface.
	 * 
	 * @param ioSample The IO sample.
	 * @param remoteDevice The device that sent the sample.
	 * 
	 * @see IOSample
	 * @see RemoteXBeeDevice
	 */
	public void ioSampleReceived(IOSample ioSample, RemoteXBeeDevice remoteDevice);

}
