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
 * Enumerates the different frame errors.
 * 
 * @since 1.2.0
 */
public enum FrameError {

	// Enumeration types.
	INVALID_TYPE(0x02, "Invalid frame type"),
	INVALID_LENGTH(0x03, "Invalid frame length"),
	INVALID_CHECKSUM(0x04, "Erroneous checksum on last frame"),
	PAYLOAD_TOO_BIG(0x05, "Payload of last API frame was too big to fit into a buffer"),
	STRING_ENTRY_TOO_BIG(0x06, "String entry was too big on last API frame sent"),
	WRONG_STATE(0x07, "Wrong state to receive frame"),
	WRONG_REQUEST_ID(0x08, "Device request ID of device response didn't match the number in the request");

	// Variables.
	private int id;

	private String name;

	private static HashMap<Integer, FrameError> lookupTable = new HashMap<Integer, FrameError>();

	static {
		for (FrameError frameError:values())
			lookupTable.put(frameError.getID(), frameError);
	}

	/**
	 * Creates a new Frame Error entry with the given ID.
	 *
	 * @param id Frame Error ID.
	 * @param name Frame Error name.
	 */
	FrameError(int id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Retrieves the Frame Error ID.
	 *
	 * @return Frame Error ID.
	 */
	public int getID() {
		return id;
	}

	/**
	 * Retrieves the Frame Error name.
	 *
	 * @return Frame Error name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves the Frame Error for the given ID.
	 *
	 * @param id ID to retrieve the Frame Error.
	 * @return The Frame Error associated with the given ID.
	 */
	public static FrameError get(int id) {
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
