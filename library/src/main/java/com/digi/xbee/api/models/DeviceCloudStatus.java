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
 * Enumerates the different Device Cloud statuses.
 * 
 * @since 1.2.0
 */
public enum DeviceCloudStatus {

	// Enumeration types.
	SUCCESS(0x00, "Success"),
	BAD_REQUEST(0x01, "Bad request"),
	RESPONSE_UNAVAILABLE(0x02, "Response unavailable"),
	DEVICE_CLOUD_ERROR(0x03, "Device Cloud error"),
	CANCELED(0x20, "Device Request canceled by user"),
	TIME_OUT(0x21, "Session timed out"),
	UNKNOWN_ERROR(0x40, "Unknown error");

	// Variables.
	private int id;

	private String name;

	private static HashMap<Integer, DeviceCloudStatus> lookupTable = new HashMap<Integer, DeviceCloudStatus>();

	static {
		for (DeviceCloudStatus status:values())
			lookupTable.put(status.getID(), status);
	}

	/**
	 * Creates a new {@code DeviceCloudStatus} entry with the given ID.
	 *
	 * @param id Status ID.
	 * @param name Status name.
	 */
	DeviceCloudStatus(int id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Retrieves the status ID.
	 *
	 * @return The Status ID.
	 */
	public int getID() {
		return id;
	}

	/**
	 * Retrieves the status name.
	 *
	 * @return The status name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves the {@code DeviceCloudStatus} for the given ID.
	 *
	 * @param id ID to retrieve.
	 *
	 * @return The {@code DeviceCloudStatus} associated to the given ID.
	 */
	public static DeviceCloudStatus get(int id) {
		return lookupTable.get(id);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return name;
	}
}
