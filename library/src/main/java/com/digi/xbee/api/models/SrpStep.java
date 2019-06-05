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
 * Enumerates the available steps of the SRP authentication.
 *
 * @since 1.3.0
 */
public enum SrpStep {

	// Enumeration types.
	STEP_1(0x01, "Step 1: client presents A value"),
	STEP_2(0x02, "Step 2: server presents B and salt"),
	STEP_3(0x03, "Step 3: client presents M1 session key validation value"),
	STEP_4(0x04, "Step 4: server presents M2 session key validation value and two 12-byte nonces"),
	UNKNOWN(-1, "Unknown");

	// Variables.
	private int id;

	private String description;

	private static HashMap<Integer, SrpStep> lookupTable = new HashMap<>();

	static {
		for (SrpStep step : values())
			lookupTable.put(step.getID(), step);
	}

	/**
	 * Class constructor. Instantiates a new {@code SrpStep} entry with
	 * the given parameters.
	 *
	 * @param id {@code SrpStep} ID code.
	 * @param description {@code SrpStep} description.
	 */
	SrpStep(int id, String description) {
		this.id = id;
		this.description = description;
	}
	
	/**
	 * Retrieves the {@code SrpStep} ID code.
	 * 
	 * @return {@code SrpStep} ID code.
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Retrieves the {@code SrpStep} description.
	 * 
	 * @return {@code SrpStep} description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Retrieves the {@code SrpStep} for the given ID code.
	 * 
	 * @param id ID code to retrieve the {@code SrpStep}.
	 * 
	 * @return The {@code SrpStep} associated with the given ID code,
	 *         {@code UNKNOWN} if there is not any SRP step associated
	 *         to the provided ID.
	 */
	public static SrpStep get(int id) {
		if (lookupTable.get(id) != null)
			return lookupTable.get(id);
		return UNKNOWN;
	}
}
