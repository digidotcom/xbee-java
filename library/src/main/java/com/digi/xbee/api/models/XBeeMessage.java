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
package com.digi.xbee.api.models;

import com.digi.xbee.api.RemoteXBeeDevice;

/**
 * This class represents an XBee message containing the remote XBee device the 
 * message belongs to, the content (data) of the message and a flag indicating 
 * if the message is a broadcast message (was received or is being sent via 
 * broadcast). 
 * 
 * <p>This class is used for sequential packet reading within the XBee Java API.</p>
 */
public class XBeeMessage {

	// Variables.
	private final RemoteXBeeDevice remoteXBeeDevice;
	private final byte[] data;
	private boolean isBroadcast;
	
	/**
	 * Class constructor. Instantiates a new object of type {@code XBeeMessage} with the 
	 * given parameters.
	 * 
	 * @param remoteXBeeDevice The remote XBee device the message belongs to.
	 * @param data Byte array containing the data of the message.
	 * 
	 * @throws NullPointerException if {@code address == null} or
	 *                              if {@code data == null}.
	 */
	public XBeeMessage(RemoteXBeeDevice remoteXBeeDevice, byte[] data) {
		this(remoteXBeeDevice, data, false);
	}
	
	/**
	 * Class constructor. Instantiates a new object of type {@code XBeeMessage} with the 
	 * given parameters.
	 * 
	 * @param remoteXBeeDevice The remote XBee device the message belongs to.
	 * @param data Byte array containing the data of the message.
	 * @param isBroadcast Indicates if the message is being sent or was received via broadcast.
	 * 
	 * @throws NullPointerException if {@code xbeeAddress == null} or
	 *                              if {@code data == null}.
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
	 * Retrieves the remote XBee device this message is associated to.
	 * 
	 * @return The remote XBee device.
	 */
	public RemoteXBeeDevice getDevice() {
		return remoteXBeeDevice;
	}
	
	/**
	 * Retrieves the a byte array containing the data of the message.
	 * 
	 * @return A byte array containing the data of the message.
	 */
	public byte[] getData() {
		return data;
	}
	
	/**
	 * Retrieves the data of the message in string format.
	 * 
	 * @return The data of the message in string format.
	 */
	public String getDataString() {
		return new String(data);
	}
	
	/**
	 * Retrieves whether or not the message is being sent or was received via 
	 * broadcast.
	 * 
	 * @return True if the message is being sent or was received via broadcast, 
	 *         false otherwise.
	 */
	public boolean isBroadcast() {
		return isBroadcast;
	}
}
