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

import java.net.Inet4Address;

/**
 * This class represents an IP message containing the IP address the 
 * message belongs to, the source and destination ports, the IP protocol,
 * and the content (data) of the message. 
 * 
 * <p>This class is used within the XBee Java Library to read data sent to IP 
 * devices.</p>
 * 
 * @since 1.2.0
 */
public class IPMessage {

	// Variables.
	private final Inet4Address ipAddress;
	
	private final byte[] data;
	
	private final int sourcePort;
	private final int destPort;
	
	private final IPProtocol protocol;
	
	/**
	 * Class constructor. Instantiates a new object of type 
	 * {@code IPMessage} with the given parameters.
	 * 
	 * @param ipAddress The IP address the message comes from.
	 * @param sourcePort TCP or UDP source port of the transmission.
	 * @param destPort TCP or UDP destination port of the transmission.
	 * @param protocol IP protocol used in the transmission.
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
	 * @see com.digi.xbee.api.models.IPProtocol
	 * @see java.net.Inet4Address
	 */
	public IPMessage(Inet4Address ipAddress, int sourcePort, int destPort, 
			IPProtocol protocol, byte[] data) {
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
	 * @see java.net.Inet4Address
	 */
	public Inet4Address getIPAddress() {
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
	 * @see IPProtocol
	 */
	public IPProtocol getProtocol() {
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
