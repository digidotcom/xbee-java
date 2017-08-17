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
 * Enumerates the different HTTP method values.
 * 
 * @since 1.2.1
 */
public enum HTTPMethodEnum {
	
	// Enumeration entries
	EMPTY(0x00, "EMPTY"),
	GET(0x01, "GET"),
	POST(0x02, "POST"),
	PUT(0x03, "PUT"),
	DELETE(0x04, "DELETE");
	
	// Variables
	private int value;
	
	private String name;
	
	private static HashMap<Integer, HTTPMethodEnum> lookupTable = new HashMap<Integer, HTTPMethodEnum>();
	
	static {
		for (HTTPMethodEnum function:values())
			lookupTable.put(function.getValue(), function);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code HTTPMethodEnum} 
	 * enumeration entry with the given parameters.
	 * 
	 * @param value HTTP method value.
	 * @param name HTTP method name.
	 */
	HTTPMethodEnum(int value, String name) {
		this.value = value;
		this.name = name;
	}
	
	/**
	 * Returns the HTTP method value.
	 * 
	 * @return HTTP method value.
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Returns the HTTP method name.
	 * 
	 * @return HTTP method name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the HTTP method associated to the given value.
	 * 
	 * @param value HTTP method value to retrieve.
	 * @return The HTTP method for the given value, {@code null} if not exists.
	 */
	public static HTTPMethodEnum get(int value) {
		return lookupTable.get(value);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString() {
		return HexUtils.byteToHexString((byte)value) + ": " + name;
	}
}
