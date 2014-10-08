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
package com.digi.xbee.api.network;

import java.util.Set;

import com.digi.xbee.api.models.XBeeProtocol;

public enum DiscoveryOption {

	/**
	 * Append device type identifier (DD) to the discovery response.
	 * 
	 * <p>Valid for the following protocols:</p>
	 * <ul>
	 *   <li>DigiMesh</li>
	 *   <li>Point-to-multipoint (Digi Point)</li>
	 *   <li>ZigBee</li>s
	 * </ul>
	 */
	APPEND_DD(0x01, "Append device type identifier (DD)"),
	
	/**
	 * Local device sends response frame when discovery is issued.
	 * 
	 * <p>Valid for the following protocols:</p>
	 * <ul>
	 *   <li>DigiMesh</li>
	 *   <li>Point-to-multipoint (Digi Point)</li>
	 *   <li>ZigBee</li>
	 *   <li>802.15.4</li>
	 * </ul>
	 */
	DISCOVER_MYSELF(0x02, "Local device sends response frame"),
	
	/**
	 * Local device sends response frame when discovery is issued.
	 * 
	 * <p>Valid for the following protocols:</p>
	 * <ul>
	 *   <li>DigiMesh</li>
	 *   <li>Point-to-multipoint (Digi Point)</li>
	 * </ul>
	 */
	APPEND_RSSI(0x04, "Append RSSI (of the last hop)");
	
	// Variables.
	private final int value;
	
	private final String description;
	
	private DiscoveryOption(int value, String description) {
		this.value = value;
		this.description = description;
	}
	
	/**
	 * Retrieves the value of the discovery option.
	 * 
	 * @return The discovery option value.
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Retrieves the description of the discovery option.
	 * 
	 * @return The discovery option description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Calculates the total value of a combination of several options for the 
	 * given protocol.
	 * 
	 * @param protocol The protocol to calculate the value of all the given 
	 *                 discovery options.
	 * @param options Collection of options to get the final value.
	 * 
	 * @return the value to be configured in the module depending on the given
	 *         collection of options and the protocol.
	 */
	public static int calculateDiscoveryValue (XBeeProtocol protocol, Set<DiscoveryOption> options) {
		// Calculate value to be configured.
		int value = 0;
		switch (protocol) {
		case ZIGBEE:
		case ZNET:
			for (DiscoveryOption op: options) {
				if (op == DiscoveryOption.APPEND_RSSI)
					continue;
				value = value + op.getValue();
			}
			break;
		case DIGI_MESH:
		case DIGI_POINT:
		case XLR:
			// TODO [XLR_DM] The next version of the XLR will add DigiMesh support.
			// For the moment only point-to-multipoint is supported in this kind of devices.
		case XLR_DM:
			for (DiscoveryOption op: options)
				value = value + op.getValue();
			break;
		case RAW_802_15_4:
		case UNKNOWN:
		default:
			if (options.contains(DiscoveryOption.DISCOVER_MYSELF))
				value = 1; // This is different for 802.15.4.
			break;
		}
		return value;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s (%d)", description, value);
	}
}
