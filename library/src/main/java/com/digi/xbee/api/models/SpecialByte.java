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
 * Enumerates all the special bytes of the XBee protocol that must be escaped 
 * when working on API 2 mode.
 * 
 * @see OperatingMode
 */
public enum SpecialByte {

	// Enumeration elements
	ESCAPE_BYTE(0x7D),
	HEADER_BYTE (0x7E),
	XON_BYTE(0x11),
	XOFF_BYTE(0x13);
	
	// Variables
	private static final HashMap<Integer, SpecialByte> lookupTable = new HashMap<Integer, SpecialByte>();
	
	static {
		for (SpecialByte sb:values())
			lookupTable.put(sb.getValue(), sb);
	}
	
	private final int value;
	
	/**
	 * Class constructor. Instantiates a new {@code SpecialByte} enumeration 
	 * entry with the given value.
	 * 
	 * @param value Value of the special byte.
	 */
	private SpecialByte(int value) {
		this.value = value;
	}
	
	/**
	 * Returns the special byte value.
	 * 
	 * @return The special byte value.
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Returns the {@code SpecialByte} entry associated with the given value.
	 * 
	 * @param value Value of the {@code SpecialByte} to retrieve.
	 * 
	 * @return {@code SpecialByte} associated to the given value, {@code null} 
	 *         if it does not exist in the list.
	 */
	public static SpecialByte get(int value) {
		return lookupTable.get(value);
	}
	
	/**
	 * Escapes the byte by performing a XOR operation with {@code 0x20} value.
	 * 
	 * @return Escaped byte value.
	 */
	public int escapeByte() {
		return value ^ 0x20;
	}
	
	/**
	 * Checks whether the given byte is special or not.
	 * 
	 * @param byteToCheck Byte to check.
	 * 
	 * @return {@code true} if given byte is special, {@code false} otherwise.
	 */
	public static boolean isSpecialByte(int byteToCheck) {
		return get(byteToCheck) != null;
	}
}
