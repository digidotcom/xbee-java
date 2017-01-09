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
 * Enumerates all the possible states of the discovery. Discovery status field 
 * is part of the {@code TransmitStatusPacket} indicating the status of the 
 * discovery when a packet is sent.
 * 
 * @see com.digi.xbee.api.packet.common.TransmitStatusPacket
 */
public enum XBeeDiscoveryStatus {

	// Enumeration elements
	DISCOVERY_STATUS_NO_DISCOVERY_OVERHEAD (0x00, "No discovery overhead"),
	DISCOVERY_STATUS_ADDRESS_DISCOVERY (0x01, "Address discovery"),
	DISCOVERY_STATUS_ROUTE_DISCOVERY (0x02, "Route discovery"),
	DISCOVERY_STATUS_ADDRESS_AND_ROUTE (0x03, "Address and route"),
	DISCOVERY_STATUS_EXTENDED_TIMEOUT_DISCOVERY (0x40, "Extended timeout discovery"),
	DISCOVERY_STATUS_UNKNOWN (0xFF, "Unknown");
	
	// Variables
	private final int id;
	
	private final String description;
	
	private static final HashMap<Integer, XBeeDiscoveryStatus> lookupTable = new HashMap<Integer, XBeeDiscoveryStatus>();
	
	static {
		for (XBeeDiscoveryStatus at:values())
			lookupTable.put(at.getId(), at);
	}
	
	/**
	 * Class constructor. Instantiates a new enumeration element of type 
	 * {@code XBeeDiscoveryStatus} with the given parameters.
	 * 
	 * @param id Discovery status ID.
	 * @param description Discovery status description.
	 */
	private XBeeDiscoveryStatus(int id, String description) {
		this.id = id;
		this.description = description;
	}
	
	/**
	 * Returns the numeric value of the discovery status identifier.
	 * 
	 * @return The discovery status identifier.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Returns the discovery status description.
	 * 
	 * @return Discovery status description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Returns the {@code XBeeDiscoveryStatus} associated to the given 
	 * identifier.
	 * 
	 * Returns {@code DISCOVERY_STATUS_UNKNOWN} if the identifier is not in the 
	 * enumeration.
	 * 
	 * @param id Identifier of the {@code XBeeDiscoveryStatus} to retrieve.
	 * 
	 * @return The {@code XBeeDiscoveryStatus} associated with the given 
	 *         identifier.
	 *         {@code DISCOVERY_STATUS_UNKNOWN} if the identifier is not in the 
	 *         enumeration.
	 */
	public static XBeeDiscoveryStatus get(int id) {
		XBeeDiscoveryStatus status = lookupTable.get(id);
		if (status != null)
			return status;
		return DISCOVERY_STATUS_UNKNOWN;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s (0x%02X)", description, id);
	}
}
