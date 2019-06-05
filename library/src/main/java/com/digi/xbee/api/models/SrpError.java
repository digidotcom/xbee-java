/*
 * Copyright 2019, Digi International Inc.
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
 * Enumerates the different errors of the SRP authentication.
 *
 * @since 1.3.0
 */
public enum SrpError {

	// Enumeration types.
	UNABLE_OFFER_B(0x80, "Unable to offer B (cryptographic error with content, usually due to A mod N == 0)"),
	INCORRECT_PAYLOAD_LENGTH(0x81, "Incorrect payload length"),
	BAD_PROOF_KEY(0x82, "Bad proof of key"),
	RESOURCE_ALLOCATION_ERROR(0x83, "Resource allocation error"),
	NOT_CORRECT_SEQUENCE(0x84, "Request contained a step not in the correct sequence"),
	UNKNOWN(-1, "Unknown");

	// Variables.
	private int id;

	private String description;

	private static HashMap<Integer, SrpError> lookupTable = new HashMap<>();

	static {
		for (SrpError error : values())
			lookupTable.put(error.getID(), error);
	}

	/**
	 * Class constructor. Instantiates a new {@code SrpError} entry with
	 * the given parameters.
	 *
	 * @param id {@code SrpError} ID code.
	 * @param description {@code SrpError} description.
	 */
	SrpError(int id, String description) {
		this.id = id;
		this.description = description;
	}
	
	/**
	 * Retrieves the {@code SrpError} ID code.
	 * 
	 * @return {@code SrpError} ID code.
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Retrieves the {@code SrpError} description.
	 * 
	 * @return {@code SrpError} description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Retrieves the {@code SrpError} for the given ID code.
	 * 
	 * @param id ID code to retrieve the {@code SrpError}.
	 * 
	 * @return The {@code SrpError} associated with the given ID code,
	 *         {@code UNKNOWN} if there is not any SRP error associated
	 *         to the provided ID.
	 */
	public static SrpError get(int id) {
		if (lookupTable.get(id) != null)
			return lookupTable.get(id);
		return UNKNOWN;
	}
}
