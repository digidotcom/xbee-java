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
 * Enumerates all the possible states of an AT Command after executing it. 
 * {@code ATCommadResponse} objects will contain an entry of this enumerator 
 * indicating the status of the AT Command that was executed.
 * 
 * @see ATCommandResponse
 */
public enum ATCommandStatus {

	// Enumeration elements
	OK (0, "Status OK"),
	ERROR (1, "Status Error"),
	INVALID_COMMAND (2, "Invalid command"), 
	INVALID_PARAMETER (3, "Invalid parameter"),
	TX_FAILURE (4, "TX failure"),
	UNKNOWN (255, "Unknown status");
	
	// Variables
	private int id;
	
	private final String description;
	
	private final static HashMap<Integer, ATCommandStatus> lookupTable = new HashMap<Integer, ATCommandStatus>();
	
	static {
		for (ATCommandStatus at:values())
			lookupTable.put(at.getId(), at);
	}
	
	/**
	 * Class constructor. Instantiates a new enumeration element of type 
	 * {@code ATCommandStatus} with the given parameters.
	 * 
	 * @param id AT Command Status ID.
	 * @param description AT Command Status description.
	 */
	ATCommandStatus(int id, String description) {
		this.id = id;
		this.description = description;
	}
	
	/**
	 * Returns the AT Command Status ID.
	 * 
	 * @return The AT Command Status ID.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Returns the AT Command Status description.
	 * 
	 * @return AT Command Status description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Returns the {@code ATCommandStatus} associated to the given ID.
	 * 
	 * @param id ID to retrieve the corresponding {@code ATCommandStatus}.
	 * 
	 * @return The {@code ATCommandStatus} associated to the given ID.
	 */
	public static ATCommandStatus get(int id) {
		ATCommandStatus status = lookupTable.get(id % 16);
		if (status == null)
			status = UNKNOWN;
		status.id = id;
		return status;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return description;
	}
}
