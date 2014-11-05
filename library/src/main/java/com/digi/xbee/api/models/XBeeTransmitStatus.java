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

import java.util.HashMap;

/**
 * Enumerates the different transmit status. Transmit status field is part of 
 * the {@code TransmitStatusPacket} indicating the status of the transmission.
 * 
 * @see com.digi.xbee.api.packet.common.TransmitStatusPacket
 */
public enum XBeeTransmitStatus {

	// Enumeration types
	SUCCESS (0x00, "Success"),
	NO_ACK (0x01, "No acknowledgement received"),
	CCA_FAILURE (0x02, "CCA failure"),
	PURGED (0x03, "Transmission purged, it was attempted before stack was up"),
	WIFI_PHYSICAL_ERROR (0x04, "Physical error occurred on the interface with the WiFi transceiver"),
	INVALID_DESTINATION (0x15, "Invalid destination endpoint"),
	NETWORK_ACK_FAILURE (0x21, "Network ACK Failure"),
	NOT_JOINED_NETWORK (0x22, "Not joined to network"),
	SELF_ADDRESSED (0x23, "Self-addressed"),
	ADDRESS_NOT_FOUND (0x24, "Address not found"),
	ROUTE_NOT_FOUND (0x25, "Route not found"),
	BROADCAST_FAILED (0x26, "Broadcast source failed to hear a neighbor relay the message"),
	INVALID_BINDING_TABLE_INDEX (0x2B, "Invalid binding table index"),
	RESOURCE_ERROR (0x2C, "Resource error lack of free buffers, timers, etc."),
	BROADCAST_ERROR_APS (0x2D, "Attempted broadcast with APS transmission"),
	BROADCAST_ERROR_APS_EE0 (0x2E, "Attempted broadcast with APS transmission, but EE=0"),
	RESOURCE_ERROR_BIS (0x32, "Resource error lack of free buffers, timers, etc."),
	PAYLOAD_TOO_LARGE (0x74, "Data payload too large"),
	SOCKET_CREATION_FAILED (0x76, "Attempt to create a client socket failed"),
	INDIRECT_MESSAGE_UNREUESTED (0x75, "Indirect message unrequested");
	
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
	 * @param id XBee transmit status ID.
	 * @param description XBee transmit status description.
	 */
	private XBeeTransmitStatus(int id, String description) {
		this.id = id;
		this.description = description;
	}
	
	/**
	 * Returns the XBee transmit status ID.
	 * 
	 * @return XBee transmit status ID.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Returns the XBee transmit status description.
	 * 
	 * @return XBee transmit status description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Returns the {@code XBeeTransmitStatus} associated to the given ID.
	 * 
	 * @param id ID of the {@code XBeeTransmitStatus} to retrieve.
	 * 
	 * @return The {@code XBeeTransmitStatus} associated to the given ID.
	 */
	public static XBeeTransmitStatus get(int id) {
		return lookupTable.get(id);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		if (id != 0)
			return "Error: " + description;
		else
			return description;
	}
}
