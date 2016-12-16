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

import java.util.HashMap;

import com.digi.xbee.api.utils.HexUtils;

/**
 * Enumerates the different association indication status for the Wi-Fi 
 * protocol.
 */
public enum WiFiAssociationIndicationStatus {

	// Enumeration entries
	SUCCESSFULLY_JOINED(0x00, "Successfully joined to access point."),
	INITIALIZING(0x01, "Initialization in progress."),
	INITIALIZED(0x02, "Initialized, but not yet scanning."),
	DISCONNECTING(0x13, "Disconnecting from access point."),
	SSID_NOT_CONFIGURED(0x23, "SSID not configured"),
	INVALID_KEY(0x24, "Encryption key invalid (NULL or invalid length)."),
	JOIN_FAILED(0x27, "SSID found, but join failed."),
	WAITING_FOR_AUTH(0x40, "Waiting for WPA or WPA2 authentication."),
	WAITING_FOR_IP(0x41, "Joined to a network and waiting for IP address."),
	SETTING_UP_SOCKETS(0x42, "Joined to a network and IP configured. Setting up listening sockets."),
	SCANNING_FOR_SSID(0xFF, "Scanning for the configured SSID.");
	
	// Variables
	private final int value;
	
	private final String description;
	
	private final static HashMap<Integer, WiFiAssociationIndicationStatus> lookupTable = new HashMap<Integer, WiFiAssociationIndicationStatus>();
	
	static {
		for (WiFiAssociationIndicationStatus associationIndicationStatus:values())
			lookupTable.put(associationIndicationStatus.getValue(), associationIndicationStatus);
	}
	
	/**
	 * Class constructor. Instantiates a new 
	 * {@code WiFiAssociationIndicationStatus} enumeration entry with the 
	 * given parameters.
	 * 
	 * @param value Wi-Fi association indication status value.
	 * @param description Wi-Fi association indication status description.
	 */
	WiFiAssociationIndicationStatus(int value, String description) {
		this.value = value;
		this.description = description;
	}
	
	/**
	 * Returns the Wi-Fi association indication status value.
	 * 
	 * @return The Wi-Fi association indication status value.
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Returns the Wi-Fi association indication status description.
	 * 
	 * @return The Wi-Fi association indication status description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Returns the {@code WiFiAssociationIndicationStatus} associated to 
	 * the given value.
	 * 
	 * @param value Value of the Wi-Fi association indication status to 
	 *              retrieve.
	 * 
	 * @return The Wi-Fi association indication status of the associated 
	 *         value, {@code null} if it could not be found in the table.
	 */
	public static WiFiAssociationIndicationStatus get(int value) {
		return lookupTable.get(value);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return HexUtils.byteToHexString((byte)value) + ": " + description;
	}
}
