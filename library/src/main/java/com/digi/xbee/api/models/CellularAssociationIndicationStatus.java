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
 * Enumerates the different association indication status for the Cellular 
 * protocol.
 */
public enum CellularAssociationIndicationStatus {

	// Enumeration entries
	SUCCESSFULLY_CONNECTED(0x00, "Connected to the Internet."),
	POWERED_UP(0x20, "Modem powered up and enumerated."),
	IDENTIFIED(0x21, "Modem identified."),
	REGISTERING(0x22, "Modem registering."),
	REGISTERED(0x23, "Modem registered."),
	SETUP_DEVICE(0xA0, "Setup modem device."),
	SETUP_USB(0xC0, "Setup modem USB."),
	POWERUP(0xE0, "Power up modem."),
	REBOOTING(0xFB, "Rebooting modem."),
	SHUTTING_DOWN(0xFC, "Shutting down modem."),
	MANUFACTURING_STATE(0xFD, "Modem in manufacturing state."),
	UNEXPECTED_STATE(0xFE, "Modem in unexpected state."),
	POWERED_DOWN(0xFF, "Modem powered down.");
	
	// Variables
	private final int value;
	
	private final String description;
	
	private final static HashMap<Integer, CellularAssociationIndicationStatus> lookupTable = new HashMap<Integer, CellularAssociationIndicationStatus>();
	
	static {
		for (CellularAssociationIndicationStatus associationIndicationStatus:values())
			lookupTable.put(associationIndicationStatus.getValue(), associationIndicationStatus);
	}
	
	/**
	 * Class constructor. Instantiates a new 
	 * {@code CellularAssociationIndicationStatus} enumeration entry with the 
	 * given parameters.
	 * 
	 * @param value Cellular association indication status value.
	 * @param description Cellular association indication status description.
	 */
	CellularAssociationIndicationStatus(int value, String description) {
		this.value = value;
		this.description = description;
	}
	
	/**
	 * Returns the Cellular association indication status value.
	 * 
	 * @return The Cellular association indication status value.
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Returns the Cellular association indication status description.
	 * 
	 * @return The Cellular association indication status description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Returns the {@code CellularAssociationIndicationStatus} associated to 
	 * the given value.
	 * 
	 * @param value Value of the Cellular association indication status to 
	 *              retrieve.
	 * 
	 * @return The Cellular association indication status of the associated 
	 *         value, {@code null} if it could not be found in the table.
	 */
	public static CellularAssociationIndicationStatus get(int value) {
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
