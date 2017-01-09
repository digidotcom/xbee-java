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

import java.util.HashMap;

/**
 * Enumerates the different transmit status. Transmit status field is part of 
 * the {@code TransmitStatusPacket} and {@code TXStatusPacket} indicating the 
 * status of the transmission.
 * 
 * @see com.digi.xbee.api.packet.common.TransmitStatusPacket
 * @see com.digi.xbee.api.packet.raw.TXStatusPacket
 */
public enum XBeeTransmitStatus {

	// Enumeration types
	SUCCESS (0x00, "Success"),
	NO_ACK (0x01, "No acknowledgement received"),
	CCA_FAILURE (0x02, "CCA failure"),
	PURGED (0x03, "Transmission purged, it was attempted before stack was up"),
	WIFI_PHYSICAL_ERROR (0x04, "Physical error occurred on the interface with the WiFi transceiver"),
	INVALID_DESTINATION (0x15, "Invalid destination endpoint"),
	NO_BUFFERS (0x18, "No buffers"),
	NETWORK_ACK_FAILURE (0x21, "Network ACK Failure"),
	NOT_JOINED_NETWORK (0x22, "Not joined to network"),
	SELF_ADDRESSED (0x23, "Self-addressed"),
	ADDRESS_NOT_FOUND (0x24, "Address not found"),
	ROUTE_NOT_FOUND (0x25, "Route not found"),
	BROADCAST_FAILED (0x26, "Broadcast source failed to hear a neighbor relay the message"),
	INVALID_BINDING_TABLE_INDEX (0x2B, "Invalid binding table index"),
	INVALID_ENDPOINT (0x2C, "Invalid endpoint"),
	BROADCAST_ERROR_APS (0x2D, "Attempted broadcast with APS transmission"),
	BROADCAST_ERROR_APS_EE0 (0x2E, "Attempted broadcast with APS transmission, but EE=0"),
	SOFTWARE_ERROR (0x31, "A software error occurred"),
	RESOURCE_ERROR (0x32, "Resource error lack of free buffers, timers, etc."),
	PAYLOAD_TOO_LARGE (0x74, "Data payload too large"),
	INDIRECT_MESSAGE_UNREQUESTED (0x75, "Indirect message unrequested"),
	SOCKET_CREATION_FAILED (0x76, "Attempt to create a client socket failed"),
	IP_PORT_NOT_EXIST (0x77, "TCP connection to given IP address and port doesn't exist. Source port is non-zero so that a new connection is not attempted"),
	/** @deprecated Use {@link #INVALID_UDP_PORT} instead. */
	UDP_SRC_PORT_NOT_MATCH_LISTENING_PORT (0x78, "Source port on a UDP transmission doesn't match a listening port on the transmitting module."),
	/** @since 1.2.0 */
	INVALID_UDP_PORT(0x78, "Invalid UDP port"),
	/** @since 1.2.0 */
	INVALID_TCP_PORT(0x79, "Invalid TCP port"),
	/** @since 1.2.0 */
	INVALID_HOST(0x7A, "Invalid host"),
	/** @since 1.2.0 */
	INVALID_DATA_MODE(0x7B, "Invalid data mode"),
	/** @since 1.2.0 */
	CONNECTION_REFUSED(0x80, "Connection refused"),
	/** @since 1.2.0 */
	CONNECTION_LOST(0x81, "Connection lost"),
	/** @since 1.2.0 */
	NO_SERVER(0x82, "No server"),
	/** @since 1.2.0 */
	SOCKET_CLOSED(0x83, "Socket closed"),
	/** @since 1.2.0 */
	UNKNOWN_SERVER(0x84, "Unknown server"),
	/** @since 1.2.0 */
	UNKNOWN_ERROR(0x85, "Unknown error"),
	KEY_NOT_AUTHORIZED (0xBB, "Key not authorized"),
	UNKNOWN (255, "Unknown");
	
	// Variables
	private final int id;
	
	private final String description;
	
	private static final HashMap<Integer, XBeeTransmitStatus> lookupTable = new HashMap<Integer, XBeeTransmitStatus>();
	
	static {
		for (XBeeTransmitStatus ts:values())
			lookupTable.put(ts.getId(), ts);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code XBeeTransmitStatus} entry 
	 * with the given parameters.
	 * 
	 * @param id Transmit status identifier.
	 * @param description Transmit status description.
	 */
	private XBeeTransmitStatus(int id, String description) {
		this.id = id;
		this.description = description;
	}
	
	/**
	 * Returns the numeric value of the transmit status identifier.
	 * 
	 * @return Transmit status identifier.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Returns the transmit status description.
	 * 
	 * @return Transmit status description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Returns the {@code XBeeTransmitStatus} associated to the given 
	 * identifier. 
	 * 
	 * Returns {@code UNKNOWN} if the identifier is not in the enumeration.
	 * 
	 * @param id Identifier of the {@code XBeeTransmitStatus} to retrieve.
	 * 
	 * @return The {@code XBeeTransmitStatus} associated to the given 
	 *         identifier.
	 *         {@code UNKNOWN} if the identifier is not in the enumeration.
	 */
	public static XBeeTransmitStatus get(int id) {
		XBeeTransmitStatus status = lookupTable.get(id);
		if (status != null)
			return status;
		return UNKNOWN;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		if (id != SUCCESS.getId())
			return String.format("Error: %s (0x%02X)", description, id);
		else
			return String.format("%s (0x%02X)", description, id);
	}
}
