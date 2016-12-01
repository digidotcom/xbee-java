/**
 * Copyright (c) 2016 Digi International Inc.,
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
 * This class represents a network message containing the IP address the 
 * message belongs to, the source and destination ports, the network protocol,
 * and the content (data) of the message. 
 * 
 * <p>This class is used within the XBee Java Library to read data sent to IP 
 * devices.</p>
 */
public class NetworkMessage {

	// Variables.
	private final IP32BitAddress ipAddress;
	
	private final byte[] data;
	
	private final int sourcePort;
	private final int destPort;
	
	private final NetworkProtocol protocol;
	
	/**
	 * Class constructor. Instantiates a new object of type 
	 * {@code XBeeMessage} with the given parameters.
	 * 
	 * @param ipAddress The IP address the message comes from.
	 * @param sourcePort TCP or UDP source port of the transmission.
	 * @param destPort TCP or UDP destination port of the transmission.
	 * @param protocol Network protocol used in the transmission.
	 * @param data Byte array containing the data of the message.
	 * 
	 * @throws IllegalArgumentException if {@code sourcePort < 0} or
	 *                                  if {@code sourcePort > 65535} or
	 *                                  if {@code destPort < 0} or
	 *                                  if {@code destPort > 65535}.
	 * @throws NullPointerException if {@code ipAddress == null} or
	 *                              if {@code data == null} or
	 *                              if {@code protocol ==  null}.
	 * 
	 * @see com.digi.xbee.api.models.IP32BitAddress
	 * @see com.digi.xbee.api.models.NetworkProtocol
	 */
	public NetworkMessage(IP32BitAddress ipAddress, int sourcePort, int destPort, 
			NetworkProtocol protocol, byte[] data) {
		if (ipAddress == null)
			throw new NullPointerException("IP address cannot be null.");
		if (protocol == null)
			throw new NullPointerException("Protocol cannot be null.");
		if (data == null)
			throw new NullPointerException("Data cannot be null.");
		
		if (sourcePort < 0 || sourcePort > 65535)
			throw new IllegalArgumentException("Source port must be between 0 and 65535.");
		if (destPort < 0 || destPort > 65535)
			throw new IllegalArgumentException("Destination port must be between 0 and 65535.");
		
		this.ipAddress = ipAddress;
		this.sourcePort = sourcePort;
		this.destPort = destPort;
		this.protocol = protocol;
		this.data = data;
	}
	
	/**
	 * Returns the IP address this message is associated to.
	 * 
	 * @return The IP address this message is associated to.
	 * 
	 * @see com.digi.xbee.api.models.IP32BitAddress
	 */
	public IP32BitAddress getIPAddress() {
		return ipAddress;
	}
	
	/**
	 * Returns the source port of the transmission.
	 * 
	 * @return The source port of the transmission.
	 */
	public int getSourcePort() {
		return sourcePort;
	}
	
	/**
	 * Returns the destination port of the transmission.
	 * 
	 * @return The destination port of the transmission.
	 */
	public int getDestPort() {
		return destPort;
	}
	
	/**
	 * Returns the protocol used in the transmission.
	 * 
	 * @return The protocol used in the transmission
	 * 
	 * @see NetworkProtocol
	 */
	public NetworkProtocol getProtocol() {
		return protocol;
	}
	
	/**
	 * Returns the byte array containing the data of the message.
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
}
