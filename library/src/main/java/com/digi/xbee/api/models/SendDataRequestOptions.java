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

import com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket;

/**
 * Enumerates the different options for the {@link SendDataRequestPacket}.
 * 
 * @since 1.2.0
 */
public enum SendDataRequestOptions {

	// Enumeration types.
	OVERWRITE(0, "Overwrite"),
	ARCHIVE(1, "Archive"),
	APPEND(2, "Append"),
	TRANSIENT(3, "Transient data (do not store)");

	// Variables.
	private int id;

	private String name;

	private static HashMap<Integer, SendDataRequestOptions> lookupTable = new HashMap<Integer, SendDataRequestOptions>();

	static {
		for (SendDataRequestOptions option:values())
			lookupTable.put(option.getID(), option);
	}

	/**
	 * Creates a new {@code SendDataRequestOptions} entry with the given ID.
	 *
	 * @param id Option ID.
	 * @param name Option name.
	 */
	SendDataRequestOptions(int id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Retrieves the option ID.
	 *
	 * @return The option ID.
	 */
	public int getID() {
		return id;
	}

	/**
	 * Retrieves the option name.
	 *
	 * @return The option name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves the {@code SendDataRequestOptions} for the given ID.
	 *
	 * @param id ID to retrieve.
	 *
	 * @return The {@code SendDataRequestOptions} associated to the given ID.
	 */
	public static SendDataRequestOptions get(int id) {
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
