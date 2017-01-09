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
