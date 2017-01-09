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

import com.digi.xbee.api.utils.HexUtils;

/**
 * Enumerates the different association indication status.
 */
public enum AssociationIndicationStatus {

	// Enumeration entries
	SUCCESSFULLY_JOINED(0x00, "Successfully formed or joined a network."),
	AS_TIMEOUT(0x01, "Active Scan Timeout."),
	AS_NO_PANS_FOUND(0x02, "Active Scan found no PANs."),
	AS_ASSOCIATION_NOT_ALLOED(0x03, "Active Scan found PAN, but the CoordinatorAllowAssociation bit is not set."),
	AS_BEACONS_NOT_SUPPORTED(0x04, "Active Scan found PAN, but Coordinator and End Device are not configured to support beacons."),
	AS_ID_DOESNT_MATCH(0x05, "Active Scan found PAN, but the Coordinator ID parameter does not match the ID parameter of the End Device."),
	AS_CHANNEL_DOESNT_MATCH(0x06, "Active Scan found PAN, but the Coordinator CH parameter does not match the CH parameter of the End Device."),
	ENERGY_SCAN_TIMEOUT(0x07, "Energy Scan Timeout."),
	COORDINATOR_START_REQUEST_FAILED(0x08, "Coordinator start request failed."),
	COORDINATOR_INVALID_PARAMETER(0x09, "Coordinator could not start due to invalid parameter."),
	COORDINATOR_REALIGNMENT(0x0A, "Coordinator Realignment is in progress."),
	AR_NOT_SENT(0x0B, "Association Request not sent."),
	AR_TIMED_OUT(0x0C, "Association Request timed out - no reply was received."),
	AR_INVALID_PARAMETER(0x0D, "Association Request had an Invalid Parameter."),
	AR_CHANNEL_ACCESS_FAILURE(0x0E, "Association Request Channel Access Failure. Request was not transmitted - CCA failure."),
	AR_COORDINATOT_ACK_WASNT_RECEIVED(0x0F, "Remote Coordinator did not send an ACK after Association Request was sent."),
	AR_COORDINATOT_DIDNT_REPLY(0x10, "Remote Coordinator did not reply to the Association Request, but an ACK was received after sending the request."),
	SYNCHRONIZATION_LOST(0x12, "Sync-Loss - Lost synchronization with a Beaconing Coordinator."),
	DISSASOCIATED(0x13, " Disassociated - No longer associated to Coordinator."),
	NO_PANS_FOUND(0x21, "Scan found no PANs."),
	NO_PANS_WITH_ID_FOUND(0x22, "Scan found no valid PANs based on current SC and ID settings."),
	NJ_EXPIRED(0x23, "Valid Coordinator or Routers found, but they are not allowing joining (NJ expired)."),
	NO_JOINABLE_BEACONS_FOUND(0x24, "No joinable beacons were found."),
	UNEXPECTED_STATE(0x25, "Unexpected state, node should not be attempting to join at this time."),
	JOIN_FAILED(0x27, "Node Joining attempt failed (typically due to incompatible security settings)."),
	COORDINATOR_START_FAILED(0x2A, "Coordinator Start attempt failed."),
	CHECKING_FOR_COORDINATOR(0x2B, "Checking for an existing coordinator."),
	NETWORK_LEAVE_FAILED(0x2C, "Attempt to leave the network failed."),
	DEVICE_DIDNT_RESPOND(0xAB, "Attempted to join a device that did not respond."),
	UNSECURED_KEY_RECEIVED(0xAC, "Secure join error - network security key received unsecured."),
	KEY_NOT_RECEIVED(0xAD, "Secure join error - network security key not received."),
	INVALID_SECURITY_KEY(0xAF, "Secure join error - joining device does not have the right preconfigured link key."),
	SCANNING_NETWORK(0xFF, "Scanning for a network/Attempting to associate.");
	
	// Variables
	private final int value;
	
	private final String description;
	
	private final static HashMap<Integer, AssociationIndicationStatus> lookupTable = new HashMap<Integer, AssociationIndicationStatus>();
	
	static {
		for (AssociationIndicationStatus associationIndicationStatus:values())
			lookupTable.put(associationIndicationStatus.getValue(), associationIndicationStatus);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code AssociationIndicationStatus} 
	 * enumeration entry with the given parameters.
	 * 
	 * @param value Association indication status value.
	 * @param description Association indication status description.
	 */
	AssociationIndicationStatus(int value, String description) {
		this.value = value;
		this.description = description;
	}
	
	/**
	 * Returns the association indication status value.
	 * 
	 * @return The association indication status value.
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Returns the association indication status description.
	 * 
	 * @return The association indication status description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Returns the {@code AssociationIndicationStatus} associated to the 
	 * given value.
	 * 
	 * @param value Value of the association indication status to retrieve.
	 * @return The association indication status of the associated value, {@code null} 
	 *         if it could not be found in the table.
	 */
	public static AssociationIndicationStatus get(int value) {
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
