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
 * Enumerates the different modem status events. This enumeration list is 
 * intended to be used within the 
 * {@link com.digi.xbee.api.packet.common.ModemStatusPacket} packet.
 */
public enum ModemStatusEvent {

	// Enumeration elements
	STATUS_HARDWARE_RESET (0, "Device was reset"),
	STATUS_WATCHDOG_TIMER_RESET (1, "Watchdog timer was reset"),
	STATUS_JOINED_NETWORK (2, "Device joined to network"),
	STATUS_DISASSOCIATED (3, "Device disassociated"),
	STATUS_ERROR_SYNCHRONIZATION_LOST (4, "Configuration error/synchronization lost"),
	STATUS_COORDINATOR_REALIGNMENT (5, "Coordinator realignment"),
	STATUS_COORDINATOR_STARTED (6, "The coordinator started"),
	STATUS_NETWORK_SECURITY_KEY_UPDATED (7, "Network security key was updated"),
	STATUS_NETWORK_WOKE_UP (0x0B, "Network Woke Up"),
	STATUS_NETWORK_WENT_TO_SLEEP (0x0C, "Network Went To Sleep"),
	STATUS_VOLTAGE_SUPPLY_LIMIT_EXCEEDED (0x0D, "Voltage supply limit exceeded"),
	/** @since 1.2.0 */
	STATUS_DEVICE_CLOUD_CONNECTED (0x0E, "Device Cloud connected"),
	/** @since 1.2.0 */
	STATUS_DEVICE_CLOUD_DISCONNECTED (0x0F, "Device Cloud disconnected"),
	STATUS_MODEM_CONFIG_CHANGED_WHILE_JOINING (0x11, " Modem configuration changed while joining"),
	STATUS_ERROR_STACK (0x80, "Stack error"),
	STATUS_ERROR_AP_NOT_CONNECTED (0x82, "Send/join command issued without connecting from AP"),
	STATUS_ERROR_AP_NOT_FOUND (0x83, "Access point not found"),
	STATUS_ERROR_PSK_NOT_CONFIGURED (0x84, "PSK not configured"),
	STATUS_ERROR_SSID_NOT_FOUND (0x87, "SSID not found"),
	STATUS_ERROR_FAILED_JOIN_SECURITY (0x88, "Failed to join with security enabled"),
	STATUS_ERROR_INVALID_CHANNEL (0x8A, "Invalid channel"),
	STATUS_ERROR_FAILED_JOIN_AP (0x8E, "Failed to join access point"),
	STATUS_UNKNOWN (0xFF, "UNKNOWN");
	
	// Variables
	private final int id;
	
	private final String description;
	
	private final static HashMap<Integer, ModemStatusEvent> lookupTable = new HashMap<Integer, ModemStatusEvent>();
	
	static {
		for (ModemStatusEvent at:values())
			lookupTable.put(at.getId(), at);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code ModemStatusEvent} 
	 * enumeration entry  with the given parameters.
	 * 
	 * @param id Modem status ID.
	 * @param description Modem status description.
	 */
	ModemStatusEvent(int id, String description) {
		this.id = id;
		this.description = description;
	}
	
	/**
	 * Returns the modem status ID.
	 * 
	 * @return The modem status ID.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Returns the modem status description.
	 * 
	 * @return Modem status description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Returns the {@code ModemStatusEvent} associated to the given ID.
	 * 
	 * @param id ID of the {@code ModemStatusEvent} to retrieve.
	 * @return The {@code ModemStatusEvent} associated with the given ID.
	 */
	public static ModemStatusEvent get(int id) {
		ModemStatusEvent status = lookupTable.get(id);
		if (status != null)
			return status;
		return STATUS_UNKNOWN;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return String.format("0x%02X: %s", id, description);
	}
}
