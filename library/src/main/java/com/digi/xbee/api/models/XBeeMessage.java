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
package com.digi.xbee.api.models;

import com.digi.xbee.api.RemoteXBeeDevice;

/**
 * This class represents an XBee message containing the remote XBee device the 
 * message belongs to, the content (data) of the message and a flag indicating 
 * if the message is a broadcast message (was received or is being sent via 
 * broadcast). 
 * 
 * <p>This class is used within the XBee Java Library to read data sent by 
 * remote devices.</p>
 */
public class XBeeMessage {

	// Variables.
	private final RemoteXBeeDevice remoteXBeeDevice;
	private final byte[] data;
	private boolean isBroadcast;
	
	/**
	 * Class constructor. Instantiates a new object of type 
	 * {@code XBeeMessage} with the given parameters.
	 * 
	 * @param remoteXBeeDevice The remote XBee device the message belongs to.
	 * @param data Byte array containing the data of the message.
	 * 
	 * @throws NullPointerException if {@code remoteXBeeDevice == null} or
	 *                              if {@code data == null}.
	 * 
	 * @see com.digi.xbee.api.RemoteXBeeDevice
	 */
	public XBeeMessage(RemoteXBeeDevice remoteXBeeDevice, byte[] data) {
		this(remoteXBeeDevice, data, false);
	}
	
	/**
	 * Class constructor. Instantiates a new object of type 
	 * {@code XBeeMessage} with the given parameters.
	 * 
	 * @param remoteXBeeDevice The remote XBee device the message belongs to.
	 * @param data Byte array containing the data of the message.
	 * @param isBroadcast Indicates if the message was received via broadcast.
	 * 
	 * @throws NullPointerException if {@code remoteXBeeDevice == null} or
	 *                              if {@code data == null}.
	 * 
	 * @see com.digi.xbee.api.RemoteXBeeDevice
	 */
	public XBeeMessage(RemoteXBeeDevice remoteXBeeDevice, byte[] data, boolean isBroadcast) {
		if (remoteXBeeDevice == null)
			throw new NullPointerException("Remote XBee device cannot be null.");
		if (data == null)
			throw new NullPointerException("Data cannot be null.");
		
		this.remoteXBeeDevice = remoteXBeeDevice;
		this.data = data;
		this.isBroadcast = isBroadcast;
	}
	
	/**
	 * Returns the remote XBee device this message is associated to.
	 * 
	 * @return The remote XBee device this message is associated to.
	 * 
	 * @see com.digi.xbee.api.RemoteXBeeDevice
	 */
	public RemoteXBeeDevice getDevice() {
		return remoteXBeeDevice;
	}
	
	/**
	 * Returns the a byte array containing the data of the message.
	 * 
	 * @return A byte array containing the data of the message.
	 */
	public byte[] getData() {
		return data;
	}
	
	/**
	 * Returns the data of the message in string format.
	 * 
	 * @return The data of the message in string format.
	 */
	public String getDataString() {
		return new String(data);
	}
	
	/**
	 * Returns whether or not the message was received via broadcast.
	 * 
	 * @return {@code true} if the message was received via broadcast, 
	 *         {@code false} otherwise.
	 */
	public boolean isBroadcast() {
		return isBroadcast;
	}
}
