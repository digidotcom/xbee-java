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
 * Enumerates the different association indication status for the Thread 
 * protocol.
 * 
 * @since 1.2.1
 */
public enum ThreadAssociationIndicationStatus {

	// Enumeration entries
	ASSOCIATED(0x00, "Device Associated"),
	ALREADY_IN_NWK_BEFORE_RESET(0x01, "Device was part of a network before reset"),
	ATTEMPT_TO_FORM_JOIN_NWK(0x02, "Device is attempting to form/join network"),
	JOINED_WITHOUT_PARENT(0x03, "Device is joined but without a parent"),
	JOINED_ATTACHING(0x04, "Device is joined and currently attaching"),
	COMMISSION_FAILURE_INVALID_PARAM(0xF4, "Commission failure because of invalid parameter"),
	COMMISSION_FAILURE_ALREADY_IN_NWK(0xF5, "Commission failure because node is already part of a network. Issue a NR before attempting to join again"),
	JOIN_FAILURE_INVALID_PARAMS(0xF6, "Join failure because of invalid parameters"),
	JOIN_FAILURE_ALREADY_IN_NWK(0xF7, "Join failure because node is already part of a network. Issue a NR before attempting to join again"),
	FORM_NWK_FAILURE_SCAN_ALREADY_IN_PROGRESS(0xF8, "Form network failure because a network scan was already in progress"),
	SECURITY_FAILURE(0xF9, "Security failure"),
	COMMISSIONING_FAILURE(0xFA, "Commissioning failure"),
	FAIL_TO_FIND_BEACON(0xFB, "Device failed to find beacon with configured parameters"),
	FAIL_TO_FORM_NWK_SCAN_FAILURE(0xFC, "Device failed to form network because of a scan failure"),
	FORM_JOIN_UNKNOWN_REASON_1(0xFD, "Form/Join failure for unknown reason. Retry and if problem persists contact Digi support"),
	FORM_JOIN_UNKNOWN_REASON_2(0xFE, "Form/Join failure for unknown reason. Retry and if problem persists contact Digi support"),
	DISASSOCIATED(0xFF, "Disassociated");
	
	// Variables
	private final int value;
	
	private final String description;
	
	private final static HashMap<Integer, ThreadAssociationIndicationStatus> lookupTable = new HashMap<Integer, ThreadAssociationIndicationStatus>();
	
	static {
		for (ThreadAssociationIndicationStatus associationIndicationStatus:values())
			lookupTable.put(associationIndicationStatus.getValue(), associationIndicationStatus);
	}
	
	/**
	 * Class constructor. Instantiates a new 
	 * {@code ThreadAssociationIndicationStatus} enumeration entry with the 
	 * given parameters.
	 * 
	 * @param value Thread association indication status value.
	 * @param description Thread association indication status description.
	 */
	ThreadAssociationIndicationStatus(int value, String description) {
		this.value = value;
		this.description = description;
	}
	
	/**
	 * Returns the Thread association indication status value.
	 * 
	 * @return The Thread association indication status value.
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Returns the Thread association indication status description.
	 * 
	 * @return The Thread association indication status description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Returns the {@code ThreadAssociationIndicationStatus} associated to 
	 * the given value.
	 * 
	 * @param value Value of the Thread association indication status to 
	 *              retrieve.
	 * 
	 * @return The Thread association indication status of the associated 
	 *         value, {@code null} if it could not be found in the table.
	 */
	public static ThreadAssociationIndicationStatus get(int value) {
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
