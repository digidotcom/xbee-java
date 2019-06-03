/**
 * Copyright 2017-2019, Digi International Inc.
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
	STATUS_HARDWARE_RESET (0, "Hardware reset"),
	STATUS_WATCHDOG_TIMER_RESET (1, "Watchdog reset"),
	STATUS_JOINED_NETWORK (2, "Device associated"),
	STATUS_DISASSOCIATED (3, "Device disassociated"),
	STATUS_ERROR_SYNCHRONIZATION_LOST (4, "Configuration error/synchronization lost"),
	STATUS_COORDINATOR_REALIGNMENT (5, "Coordinator realignment"),
	STATUS_COORDINATOR_STARTED (6, "The coordinator started"),
	STATUS_NETWORK_SECURITY_KEY_UPDATED (7, "Network security key was updated"),
	/** @since 1.3.0 */
	STATUS_SYNCH_SENT (8, "Reserved, see twiki"),
	/** @since 1.3.0 */
	STATUS_SYNCH_RX (9, "Reserved, see twiki"),
	/** @since 1.3.0 */
	STATUS_SYNCH_MISS (0x0A, "Reserved, see twiki"),
	STATUS_NETWORK_WOKE_UP (0x0B, "Network Woke Up"),
	STATUS_NETWORK_WENT_TO_SLEEP (0x0C, "Network Went To Sleep"),
	STATUS_VOLTAGE_SUPPLY_LIMIT_EXCEEDED (0x0D, "Overvoltage"),
	/** @since 1.2.0 */
	STATUS_DEVICE_CLOUD_CONNECTED (0x0E, "Device Cloud connected"),
	/** @since 1.2.0 */
	STATUS_DEVICE_CLOUD_DISCONNECTED (0x0F, "Device Cloud disconnected"),
	/** @since 1.3.0 */
	STATUS_MODEM_KEY_ESTABLISHMT_DONE (0x10, "Modem key established"),
	STATUS_MODEM_CONFIG_CHANGED_WHILE_JOINING (0x11, "Modem configuration changed while joining"),
	/** @since 1.3.0 */
	STATUS_ACESS_FAULT (0x12, "Access fault"),
	/** @since 1.3.0 */
	STATUS_FATAL_STACK_ERROR (0x13, "Fatal stack error"),
	/** @since 1.3.0 */
	STATUS_MODEM_PLKE_INITIATED (0x14, "PLKE table initiated"),
	/** @since 1.3.0 */
	STATUS_MODEM_PLKE_SUCCESS (0x15, "PLKE table success"),
	/** @since 1.3.0 */
	STATUS_MODEM_PLKE_TABLE_FULL (0x16, "PLKE table is full"),
	/** @since 1.3.0 */
	STATUS_MODEM_PLKE_NOT_AUTHORIZED (0x17, "PLKE Not authorized"),
	/** @since 1.3.0 */
	STATUS_MODEM_PLKE_INVALID_TC_REQ (0x18, "PLKE Invalid Trust Center Request"),
	/** @since 1.3.0 */
	STATUS_MODEM_PLKE_TC_UPDATE_FAIL (0x19, "PLKE Trust Center update fail"),
	/** @since 1.3.0 */
	STATUS_MODEM_PLKE_BAD_EUI64 (0x1A, "PLKE Bad EUI address"),
	/** @since 1.3.0 */
	STATUS_MODEM_PLKE_LK_REJECTED (0x1B, "PLKE Link Key rejected"),
	/** @since 1.3.0 */
	STATUS_MODEM_PLKE_UPDATE (0x1C, "PLKE update occurred"),
	/** @since 1.3.0 */
	STATUS_MODEM_PLKE_CLEAR_LK_TABLE (0x1D, "PLKE Link Key table clear"),
	/** @since 1.3.0 */
	STATUS_MODEM_FREQUENCY_AGILITY (0x1E, "Zigbee Frequency agility has requested channel change"),
	/** @since 1.3.0 */
	STATUS_MODEM_FR_NOASSOC (0x1F, "Zigbee special case execute ATFR when there's no joinable beacon responses after 60 seconds"),
	/** @since 1.3.0 */
	STATUS_MODEM_TOKENS_RECOVERED (0x20, "Zigbee tokens space recovered"),
	/** @since 1.3.0 */
	STATUS_MODEM_TOKENS_UNRECOVERABLE (0x21, "Zigbee tokens space unrecoverable"),
	/** @since 1.3.0 */
	STATUS_MODEM_TOKENS_CORRUPTED (0x22, "Zigbee tokens space corrupted"),
	/** @since 1.3.0 */
	STATUS_MODEM_METAFRAME_ERROR (0x30, "Zigbee Dual Mode metaframe error"),
	/** @since 1.3.0 */
	STATUS_BLE_CONNECT (0x32, "BLE Connect"),
	/** @since 1.3.0 */
	STATUS_BLE_DISCONNECT (0x33, "BLE Disconnect"),
	/** @since 1.3.0 */
	STATUS_BANDMASK_CONFIG_FAILED (0x34, "Bandmask Configuration Failed"),
	STATUS_ERROR_STACK (0x80, "Stack reset"),
	/** @since 1.3.0 */
	STATUS_FIB_BOOTLOADER_RESET (0x81, "FIB Bootloader reset"),
	STATUS_ERROR_AP_NOT_CONNECTED (0x82, "Send/join command issued without connecting from AP"),
	STATUS_ERROR_AP_NOT_FOUND (0x83, "Access point not found"),
	STATUS_ERROR_PSK_NOT_CONFIGURED (0x84, "PSK not configured"),
	STATUS_ERROR_SSID_NOT_FOUND (0x87, "SSID not found"),
	STATUS_ERROR_FAILED_JOIN_SECURITY (0x88, "Failed to join with security enabled"),
	/** @since 1.3.0 */
	STATUS_CORE_LOCKUP_FAILURE (0x89, "Core lockup or crystal failure reset"),
	STATUS_ERROR_INVALID_CHANNEL (0x8A, "Invalid channel"),
	/** @since 1.3.0 */
	STATUS_LOW_VCC_RESET (0x8B, "Low Vcc reset"),
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
