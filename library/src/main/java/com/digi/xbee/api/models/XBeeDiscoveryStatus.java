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
 * Enumerates all the possible states of the discovery.
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
	 * Class constructor. Instances a new enumeration element of type 
	 * {@code XBeeDiscoveryStatus} with the given parameters.
	 * 
	 * @param id XBee Discovery Status ID.
	 * @param description XBee Discovery Status description.
	 */
	XBeeDiscoveryStatus(int id, String description) {
		this.id = id;
		this.description = description;
	}
	
	/**
	 * Retrieves the XBee Discovery Status ID.
	 * 
	 * @return The XBee Discovery Status ID.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Retrieves the XBee Discovery Status description.
	 * 
	 * @return XBee Discovery Status description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Retrieves the XBee Discovery Status for the given ID.
	 * 
	 * @param id ID to retrieve XBee Discovery Status Status.
	 * 
	 * @return XBee Discovery Status Status associated with the given ID.
	 */
	public static XBeeDiscoveryStatus get(int id) {
		XBeeDiscoveryStatus status = lookupTable.get(id);
		if (status != null)
			return status;
		return XBeeDiscoveryStatus.DISCOVERY_STATUS_UNKNOWN;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return description;
	}
}
