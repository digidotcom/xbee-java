/**
 * Copyright 2017, Digi International Inc.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES 
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR 
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES 
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN 
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF 
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
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
