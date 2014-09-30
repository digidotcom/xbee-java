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

/**
 * This class represents an XBee message containing the address of the XBee device 
 * the message belongs to as well as the content (data) of the message. 
 * 
 * <p>This class is used for sequential packet reading within the XBee Java API.</p>
 */
public class XBeeMessage {

	// Variables.
	private final String address;
	private final byte[] data;
	
	/**
	 * Class constructor. Instantiates a new object of type {@code XBeeMessage} with the 
	 * given parameters.
	 * 
	 * @param address The address of the XBee device the message belongs to.
	 * @param data Byte array containing the data of the message.
	 * 
	 * @throws IllegalArgumentException if {@code address.length() == 0}.
	 * @throws NullPointerException if {@code address == null} or
	 *                              if {@code data == null}.
	 */
	public XBeeMessage(String address, byte[] data) {
		if (address == null)
			throw new NullPointerException("Address cannot be null.");
		if (address.length() == 0)
			throw new IllegalArgumentException("Address cannot be empty.");
		if (data == null)
			throw new NullPointerException("Data cannot be null.");
		
		this.address = address;
		this.data = data;
	}
	
	/**
	 * Retrieves the address of the XBee device this message is associated to.
	 * 
	 * @return The address of the XBee device.
	 */
	public String getAddress() {
		return address;
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
}
